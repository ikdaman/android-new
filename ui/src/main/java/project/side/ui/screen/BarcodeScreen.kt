package project.side.ui.screen

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.side.domain.model.DomainResult
import project.side.presentation.viewmodel.SearchBookViewModel

private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

@OptIn(ExperimentalCamera2Interop::class)
@Composable
fun BarcodeScreen(
    viewModel: SearchBookViewModel,
    onBack: () -> Unit = {},
    onNavigateToAddBookScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var isPermissionGranted by remember { mutableStateOf(false) }
    val searchResult = viewModel.bookDetail.collectAsStateWithLifecycle().value

    val barcodeScanner = remember { BarcodeScanner() }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> isPermissionGranted = granted }

    LaunchedEffect(Unit) {
        isPermissionGranted = checkPermission(context = context)
    }

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            initCameraProvider(context) { cameraProvider = it }
        } else {
            cameraPermissionLauncher.launch(CAMERA_PERMISSION)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            barcodeScanner.isbnFlow.collect { value ->
                if (value != null) {
                    viewModel.searchBookByIsbn(value)
                }
            }
        }
    }

    LaunchedEffect(searchResult) {
        when (searchResult) {
            is DomainResult.Success -> {
                onNavigateToAddBookScreen()
            }
            else -> {}
        }
    }

    BarcodeScreenUI(
        onBack = onBack,
        isPermissionGranted = isPermissionGranted,
        lifecycleOwner = lifecycleOwner,
        cameraProvider = cameraProvider,
        barcodeScanner = barcodeScanner
    )
}

private fun initCameraProvider(context: Context, onCameraProviderInit: (ProcessCameraProvider) -> Unit) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({ onCameraProviderInit(cameraProviderFuture.get()) }, ContextCompat.getMainExecutor(context))
}

private fun checkPermission(context: Context): Boolean =
    context.checkSelfPermission(CAMERA_PERMISSION) == android.content.pm.PackageManager.PERMISSION_GRANTED

@OptIn(ExperimentalCamera2Interop::class)
@Composable
private fun BarcodeScreenUI(
    onBack: () -> Unit = {},
    isPermissionGranted: Boolean? = null,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    cameraProvider: ProcessCameraProvider? = null,
    barcodeScanner: BarcodeScanner? = null,
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "바코드 스캔하기",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 15.dp),
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
                )
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Back", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        if (isPermissionGranted == null) return@Scaffold

        // For this simplified screen we don't run analysis; only show camera preview box.
        if (isPermissionGranted) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                lifecycleOwner = lifecycleOwner,
                cameraProvider = cameraProvider,
                barcodeScanner = barcodeScanner
            )
        }
    }
}

@OptIn(ExperimentalCamera2Interop::class)
@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraProvider: ProcessCameraProvider? = null,
    lifecycleOwner: LifecycleOwner,
    barcodeScanner: BarcodeScanner? = null
) {
    if (cameraProvider == null) return

    DisposableEffect(cameraProvider, lifecycleOwner) {
        onDispose {
            try {
                cameraProvider.unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    Box(modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                // bind if scanner is provided
                if (barcodeScanner != null) {
                    try {
                        bindCamera(previewView, cameraProvider, lifecycleOwner, barcodeScanner)
                    } catch (e: Exception) {
                        Log.e("BarcodeScreen", "bindCamera failed: ${e.message}")
                    }
                }
                previewView
            },
            update = { view ->
                if (barcodeScanner != null) {
                    try {
                        bindCamera(view, cameraProvider, lifecycleOwner, barcodeScanner)
                    } catch (_: Exception) {
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color(0xFFFFD900))
                .background(Color.Black)
        )
    }
}


@OptIn(ExperimentalCamera2Interop::class)
private fun bindCamera(
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    barcodeScanner: BarcodeScanner
) {
    val preview = Preview.Builder().build().also {
        it.surfaceProvider = previewView.surfaceProvider
    }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    try {
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            barcodeScanner.imageAnalysisBuilder.build()
                .also { analysis ->
                    analysis.setAnalyzer(barcodeScanner.executor) { imageProxy ->
                        barcodeScanner.processImageProxy(imageProxy)
                    }
                }
        )
    } catch (e: Exception) {
        Log.e("BarcodeScreen", "bindToLifecycle failed: ${e.message}", e)
    }
}

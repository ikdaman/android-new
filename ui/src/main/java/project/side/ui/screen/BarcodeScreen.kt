package project.side.ui.screen

import android.Manifest
import android.content.Context
import android.graphics.Rect
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.side.ui.theme.BackgroundDefault
import project.side.ui.R
import project.side.ui.component.TitleBar
import project.side.ui.theme.DungGeunMo
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.side.domain.DataResource
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
            is DataResource.Success -> {
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
            Column(modifier = Modifier.statusBarsPadding()) {
                TitleBar(
                    title = "바코드 스캔",
                    showBackButton = true,
                    onBackButtonClicked = onBack
                )
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

    // Scan region: centered box (10%-90% horizontal, 30%-60% vertical)
    val scanLeft = 100; val scanTop = 300; val scanRight = 900; val scanBottom = 600
    LaunchedEffect(barcodeScanner) {
        barcodeScanner?.scanRegion = Rect(scanLeft, scanTop, scanRight, scanBottom)
    }

    val textMeasurer = rememberTextMeasurer()

    Box(modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
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
                .background(Color.Black)
        )

        // Dark overlay with transparent hole
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxLeft = size.width * scanLeft / 1000f
            val boxTop = size.height * scanTop / 1000f
            val boxRight = size.width * scanRight / 1000f
            val boxBottom = size.height * scanBottom / 1000f
            val boxWidth = boxRight - boxLeft
            val boxHeight = boxBottom - boxTop

            val holePath = Path().apply {
                addRect(androidx.compose.ui.geometry.Rect(boxLeft, boxTop, boxRight, boxBottom))
            }
            clipPath(holePath, clipOp = ClipOp.Difference) {
                drawRect(Color.Black.copy(alpha = 0.8f))
            }

            drawRect(
                color = Color.White,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Guide text + arrow (positioned above the scan box)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(scanTop / 1000f - 0.1f))
            Text(
                text = "책의 바코드 영역을 맞춰주세요.",
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = DungGeunMo,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                modifier = Modifier.height(30.dp)
            )
            Spacer(modifier = Modifier.weight(0.1f + (1000 - scanTop) / 1000f))
        }
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

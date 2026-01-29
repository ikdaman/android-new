package project.side.ui.screen

import android.graphics.Rect
import android.util.Log
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode.TYPE_ISBN
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val TAG = "BarcodeScanner"

@ExperimentalCamera2Interop
@ExperimentalGetImage
class BarcodeScanner {
    private val _isbnFlow = MutableSharedFlow<String?>(1)
    val isbnFlow = _isbnFlow.asSharedFlow()

    private val barcodeScanner = BarcodeScanning.getClient()
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    val imageAnalysisBuilder = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

    fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.valueType == TYPE_ISBN) {
                            barcode.rawValue?.let { value ->
                                Log.d(TAG, "processImageProxy: $value")
                                _isbnFlow.tryEmit(value)
                                break
                            }
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "processImageProxy: $e")
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

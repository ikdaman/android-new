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

    /**
     * Scan region as fractions of the image (0f..1f).
     * Set from the UI layer based on the viewfinder box position.
     */
    @Volatile
    var scanRegion: Rect? = null

    val imageAnalysisBuilder = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

    fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            val imageWidth = imageProxy.width
            val imageHeight = imageProxy.height

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.valueType == TYPE_ISBN) {
                            val box = barcode.boundingBox
                            if (box != null && !isInScanRegion(box, imageWidth, imageHeight)) {
                                continue
                            }
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

    private fun isInScanRegion(box: Rect, imageWidth: Int, imageHeight: Int): Boolean {
        val region = scanRegion ?: return true
        val barcodeCenter = android.graphics.Rect(
            box.left * 1000 / imageWidth,
            box.top * 1000 / imageHeight,
            box.right * 1000 / imageWidth,
            box.bottom * 1000 / imageHeight
        )
        return region.contains(barcodeCenter.centerX(), barcodeCenter.centerY())
    }
}

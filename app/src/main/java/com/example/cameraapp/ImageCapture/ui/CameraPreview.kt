import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.impl.utils.futures.FutureCallback
import androidx.camera.core.impl.utils.futures.Futures
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import com.example.cameraapp.R
import java.util.concurrent.Executors


@Composable
fun CameraPreview(){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val previewView = remember {
        PreviewView(context).apply {
            id = R.id.preview_view
        }
    }
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    AndroidView(factory = {previewView},
        modifier = Modifier.fillMaxSize()
    ){
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider( previewView.surfaceProvider)
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val faceAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy( ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor,FaceAnalyzer())
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            }catch (e: java.lang.Exception){
                println(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

}

private class FaceAnalyzer(): ImageAnalysis.Analyzer {
    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image
        image?.close()
    }

}
//@Composable
//fun startCamera(width: Int, height: Int , preview: Preview){
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current
//    val previewResolution = Size(width, height)
//    preview.
//    Preview.SurfaceProvider { request ->
//        val viewfinderSurfaceRequest = request
//
//    }
//    val surfaceListenableFuture =
//    Futures.addCallback(surfaceListenableFuture, object : FutureCallback {
//        override fun onSuccess(surface: ShortcutInfoCompat.Surface) {
//            /* create a CaptureSession using this surface as usual */
//        }
//        override fun onFailure(t: Throwable) { /* something went wrong */}
//    }, ContextCompat.getMainExecutor(context))
//}
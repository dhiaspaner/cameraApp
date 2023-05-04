import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
fun CameraScreen() {
    val cameraManager: CameraManager =
        LocalContext.current.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraIds: Array<String> = cameraManager.cameraIdList
    var cameraId: String = ""
    for (id in cameraIds) {
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
        //If we want to choose the rear facing camera instead of the front facing one
        if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
            continue


        val previewSize =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        val imageReader =
            ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
        val listener = ImageReader.OnImageAvailableListener {
        }
        val handler = Handler()
        imageReader.setOnImageAvailableListener(listener, handler)
        cameraId = id
    }
}



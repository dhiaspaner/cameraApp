package com.example.cameraapp.ImageCapture.domain

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


//
//class  RecordingManager(
//    private val context: Context,
//    val videoCapture: MutableState<VideoCapture<Recorder>?>,
//    private val outputDirectory: File,
//    private val executor: Executor,
//    private val audioEnabled: Boolean,
//    private val consumer: androidx.core.util.Consumer<VideoRecordEvent>
//    ) {
//    private val videoFile = File(
//        outputDirectory,
//        "myappVideoHhh" + ".mp4"
//    )
//    fun startRecordingVideo(): Recording {
//
//        val outputOptions = FileOutputOptions.Builder(videoFile).build()
//
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.RECORD_AUDIO
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return videoCapture.value!!.output
//                    .prepareRecording(context, outputOptions)
//                    .start(executor, consumer)
//
//            } else {
//                return videoCapture.value!!.output
//                    .prepareRecording(context, outputOptions)
//                    .apply { if (audioEnabled) withAudioEnabled() }
//                    .start(executor, consumer)
//            }
//        }
//
//
//    fun saveVideoToMediaStore(context: Context) {
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
//            put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
//            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//            put(MediaStore.Video.Media.SIZE, videoFile.length())
//        }
//
//        val resolver = context.contentResolver
//        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        resolver.openOutputStream(uri!!)?.use { outputStream ->
//            videoFile.inputStream().use { inputStream ->
//                inputStream.copyTo(outputStream)
//            }
//        }
//
//        videoFile.delete()
//    }
//
//}

@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): androidx.camera.video.VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val qualitySelector = QualitySelector.from(
        Quality.FHD,
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )
    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()
    val videoCapture = androidx.camera.video.VideoCapture.withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        videoCapture
    )

    return videoCapture
}
@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            mainExecutor
        )
    }
}
fun saveVideoToMediaStore(context: Context, file: File) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, file.name)
        put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.SIZE, file.length())
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

    resolver.openOutputStream(uri!!)?.use { outputStream ->
        file.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    file.delete()
}
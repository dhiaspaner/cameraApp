package com.example.cameraapp.HomeScreen.domain//package com.example.cameraapp.HomeScreen.domain
//
//import android.content.Context
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.video.*
//import androidx.camera.view.PreviewView
//import androidx.lifecycle.LifecycleOwner
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//
//
//@RequiresApi(Build.VERSION_CODES.P)
//suspend fun Context.createVideoCaptureUseCase(
//    lifecycleOwner: LifecycleOwner,
//    cameraSelector: CameraSelector,
//    previewView: PreviewView
//): VideoCapture<Recorder> {
//    val preview = Preview.Builder()
//        .build()
//        .apply { setSurfaceProvider(previewView.surfaceProvider) }
//
//    val qualitySelector = QualitySelector.from(
//        Quality.FHD,
//        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
//    )
//    val recorder = Recorder.Builder()
//        .setExecutor(mainExecutor)
//        .setQualitySelector(qualitySelector)
//        .build()
//    val videoCapture = VideoCapture.withOutput(recorder)
//
//    val cameraProvider = getCameraProvider()
//    cameraProvider.unbindAll()
//    cameraProvider.bindToLifecycle(
//        lifecycleOwner,
//        cameraSelector,
//        preview,
//        videoCapture
//    )
//
//    return videoCapture
//}
//
//@RequiresApi(Build.VERSION_CODES.P)
//suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
//    ProcessCameraProvider.getInstance(this).also { future ->
//        future.addListener(
//            {
//                continuation.resume(future.get())
//            },
//            mainExecutor
//        )
//    }
//}
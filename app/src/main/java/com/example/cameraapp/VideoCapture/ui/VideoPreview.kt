package com.example.cameraapp.ImageCapture.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.video.*
import androidx.camera.video.VideoRecordEvent.Finalize.VideoRecordError
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.cameraapp.ImageCapture.domain.createVideoCaptureUseCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import com.example.cameraapp.R
import kotlinx.coroutines.launch



@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoCaptureScreen(
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    )

    var recording: Recording? = remember { null }
    val previewView: PreviewView = remember { PreviewView(context) }
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val recordingStarted: MutableState<Boolean> = remember { mutableStateOf(false) }

    val audioEnabled: MutableState<Boolean> = remember { mutableStateOf(false) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }


    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(previewView) {
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView
        )
    }
    val mediaDir = context.externalCacheDirs.firstOrNull()?.let {
        File(it, context.getString(R.string.app_name)).apply { mkdirs() }
    }


    val videoFile = File(
        mediaDir,
        "my_video.mp4"
    )
    val contentValues = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.name)
        put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.SIZE, videoFile.length())
        put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
    }
    val mediaStoreOutput = MediaStoreOutputOptions.Builder(context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        .setContentValues(contentValues).build()
    val outputOptions = FileOutputOptions.Builder(videoFile).build()

    val recorderConsumer = androidx.core.util.Consumer <VideoRecordEvent>(){event ->
        when(event){
            is VideoRecordEvent.Start ->{

            }
            is VideoRecordEvent.Status ->{
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {


                } else {

                }
            }
            is VideoRecordEvent.Pause ->{}
            is VideoRecordEvent.Resume ->{}
            is VideoRecordEvent.Finalize ->{
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                resolver.openOutputStream(uri!!)?.use { outputStream ->
                    videoFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }

    }


    Column() {
        
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = {
                if (!recordingStarted.value) {
                    recordingStarted.value = true
                    recording = videoCapture.value!!.output
                        .prepareRecording(context, outputOptions)
                        .apply { if (audioEnabled.value) withAudioEnabled() }
                        .start(context.mainExecutor , recorderConsumer)
                } else {
                    recordingStarted.value = false
                    recording?.stop()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(if (recordingStarted.value) R.drawable.stop_record else R.drawable.record),
                contentDescription = "",
                modifier = Modifier.size(64.dp)
            )
        }
        if (!recordingStarted.value) {
            IconButton(
                onClick = {
                    audioEnabled.value = !audioEnabled.value
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 32.dp)
            ) {
                Icon(
                    painter = painterResource(if (audioEnabled.value) R.drawable.mic_on else R.drawable.mic_off),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        if (!recordingStarted.value) {
            IconButton(
                onClick = {
                    cameraSelector.value =
                        if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                    lifecycleOwner.lifecycleScope.launch {
                        videoCapture.value = context.createVideoCaptureUseCase(
                            lifecycleOwner = lifecycleOwner,
                            cameraSelector = cameraSelector.value,
                            previewView = previewView
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.switch_camera),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}


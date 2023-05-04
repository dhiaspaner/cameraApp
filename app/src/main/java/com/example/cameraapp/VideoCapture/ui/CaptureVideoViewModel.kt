//package com.example.cameraapp.VideoCapture.ui
//
//import androidx.camera.core.CameraSelector
//import androidx.camera.video.Recorder
//import androidx.camera.video.Recording
//import androidx.camera.video.VideoCapture
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//
//
//@HiltViewModel
//class CaptureVideoViewModel @Inject constructor(
//    private val createVideoCaptureUseCase: CreateVideoCaptureUseCase,
//    private val getCameraProviderUseCase: GetCameraProviderUseCase,
//    private val startRecordingVideoUseCase: StartRecordingVideoUseCase
//): ViewModel() {
//    var recording: MutableState<Recording?> = mutableStateOf<Recording?>(null)
//    val videoCapture: MutableState<VideoCapture<Recorder>?> =  mutableStateOf(null)
//    val recordingStarted: MutableState<Boolean> = mutableStateOf(false)
//
//    val audioEnabled: MutableState<Boolean> = mutableStateOf(false)
//    val cameraSelector: MutableState<CameraSelector> = mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
//
//}
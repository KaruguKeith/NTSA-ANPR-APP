package com.example.ntsaanpr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    var showWelcomeScreen by remember { mutableStateOf(true) }
    if (showWelcomeScreen) {
        WelcomeScreen { showWelcomeScreen = false }
    } else {
        MainScreen()
    }
}

@Composable
fun WelcomeScreen(onProceed: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "NTSA",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Made by Keith Jambo © 2024",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                onClick = onProceed,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Welcome →")
            }
        }
    }
}

@Composable
fun MainScreen() {
    val plateNumber = remember { mutableStateOf("Plate Number will appear here") }
    val showLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            val photoFile = File(getPathFromUri(context, it))
            showLoading.value = true
            sendImageToApi(photoFile, plateNumber) { showLoading.value = false }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            Text("Extracting...", fontSize = 18.sp)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.75f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.value != null) {
                        // Display the captured/uploaded image
                        AsyncImage(
                            model = imageUri.value,
                            contentDescription = "Captured or Uploaded Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Display live camera preview
                        AndroidView(factory = { context ->
                            val previewView = PreviewView(context)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().apply {
                                    setSurfaceProvider(previewView.surfaceProvider)
                                }
                                try {
                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageCapture
                                    )
                                } catch (e: Exception) {
                                    Log.e("Camera", "Use case binding failed", e)
                                }
                            }, ContextCompat.getMainExecutor(context))
                            previewView
                        }, modifier = Modifier.fillMaxSize())
                    }
                }

                Row {
                    Button(
                        onClick = {
                            val photoFile = File(context.externalCacheDir, "captured_image.jpg")
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val savedUri = Uri.fromFile(photoFile)
                                        imageUri.value = savedUri
                                        sendImageToApi(photoFile, plateNumber) { showLoading.value = false }
                                        showLoading.value = true
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        plateNumber.value = "Capture failed: ${exception.message}"
                                    }
                                }
                            )
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Capture Image")
                    }

                    Button(
                        onClick = { pickImageLauncher.launch("image/*") },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Upload Image")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = plateNumber.value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


fun captureImageAndSendToApi(
    context: Context,
    plateNumber: MutableState<String>,
    imageCapture: ImageCapture,
    onComplete: () -> Unit
) {
    val photoFile = File(context.externalCacheDir, "captured_image.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            sendImageToApi(photoFile, plateNumber, onComplete)
        }

        override fun onError(exception: ImageCaptureException) {
            plateNumber.value = "Capture failed: ${exception.message}"
            onComplete()
        }
    })
}

fun sendImageToApi(
    photoFile: File,
    plateNumber: MutableState<String>,
    onComplete: () -> Unit
) {
    val requestBody = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    val imagePart = MultipartBody.Part.createFormData("image", photoFile.name, requestBody)

    RetrofitClient.apiService.recognizePlate(imagePart).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                val plateText = responseBody?.let {
                    JSONObject(it).getString("plate_text")
                } ?: "No plate detected"
                plateNumber.value = "Done: $plateText"
            } else {
                plateNumber.value = "Error: ${response.message()}"
            }
            onComplete()
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            plateNumber.value = "Failed: ${t.message}"
            onComplete()
        }
    })
}

fun getPathFromUri(context: Context, uri: Uri): String {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return it.getString(columnIndex)
        }
    }
    return ""
}

package com.kicker721.lab26

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var tvColorCode: TextView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var colorBox: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        previewView = findViewById(R.id.previewView)
        tvColorCode = findViewById(R.id.tvColorCode)
        colorBox = findViewById(R.id.colorBox)
        val btnCapture: ImageButton = findViewById(R.id.btnCapture)

        cameraExecutor = Executors.newSingleThreadExecutor()

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startCamera()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        btnCapture.setOnClickListener { capturePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        analyzeCenterColor(image)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzeCenterColor(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val width = image.width
        val height = image.height

        val cx = width / 2
        val cy = height / 2
        val offset = (cy * width + cx) * 4

        val r = buffer.get(offset + 0).toInt() and 0xFF
        val g = buffer.get(offset + 1).toInt() and 0xFF
        val b = buffer.get(offset + 2).toInt() and 0xFF

        val colorHex = String.format("#%02X%02X%02X", r, g, b)

        runOnUiThread {
            tvColorCode.text = colorHex
            colorBox.setBackgroundColor(android.graphics.Color.rgb(r, g, b))
        }


        image.close()
    }

    private fun capturePhoto() {
        val photoFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "photo_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_error, exc.message),
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        getString(R.string.toast_saved, photoFile.name),
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}
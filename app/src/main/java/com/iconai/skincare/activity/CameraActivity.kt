package com.iconai.skincare.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.iconai.skincare.R
import com.iconai.skincare.util.ApiData
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var viewPreview: androidx.camera.view.PreviewView? = null
    private var btnCameraTake: Button? = null

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        init()
    }

    fun init() {
        startCamera()

        viewPreview = findViewById(R.id.view_preview)
        btnCameraTake = findViewById(R.id.btn_camera_take)

        btnCameraTake?.setOnClickListener {
            it.isSelected = true
            takePhoto()
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(cameraExecutor, object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                ApiData.imgFaceBitmap = imageProxyToBitmap(image)
                ApiData.imgFaceFile = bitmapToFile(this@CameraActivity, ApiData.imgFaceBitmap!!, "skinCare.jpg")
                startActivity(Intent(this@CameraActivity, ImageCheckActivity::class.java))
                finish()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val inversionImg = Matrix()
        inversionImg.setScale(-1F, 1F)

        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        var bitmapEmt = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width - 3, bitmap.height - 56, inversionImg, false)

        return Bitmap.createScaledBitmap(bitmapEmt, 800, 1280, true)
    }

    fun bitmapToFile(context: Context?, bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
            file.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapdata: ByteArray = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewPreview?.surfaceProvider)
                    it.targetRotation = Surface.ROTATION_180

                }

            imageCapture = ImageCapture.Builder().apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                setTargetResolution(Size(800, 1280))
            }.build()

            var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }
}
package it.wazabit.dev.extensions.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import it.wazabit.dev.extensions.flip
import it.wazabit.dev.extensions.rotate
import java.nio.ByteBuffer
import java.util.concurrent.Executor

class CameraProxy(private val appCompatActivity: AppCompatActivity, private val surfaceProvider: Preview.SurfaceProvider){


    private var executor: Executor = ContextCompat.getMainExecutor(appCompatActivity)
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(appCompatActivity)
    private lateinit var preview: Preview
    private lateinit var cameraSelector: CameraSelector
    private lateinit var imageCapture:ImageCapture
    private lateinit var cameraProvider: ProcessCameraProvider

    init {
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            imageCapture = buildImageCapture()
            cameraSelector = buildCameraSelector()
            preview = Preview.Builder().build()
            bind()
        },executor)
    }

    private fun buildCameraSelector(facing:Int = CameraSelector.LENS_FACING_BACK) : CameraSelector{
        return CameraSelector.Builder()
            .requireLensFacing(facing)
            .build()
    }

    private fun buildImageCapture() : ImageCapture{
        return ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            Log.d("CameraProxy","Default rotation ${appCompatActivity.windowManager.defaultDisplay.rotation}")
            setTargetRotation(appCompatActivity.windowManager.defaultDisplay.rotation)
        }.build()
    }

    private fun bind(){
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()
        // Bind use cases to camera
        cameraProvider.bindToLifecycle(appCompatActivity, cameraSelector, preview,imageCapture)
        preview.setSurfaceProvider(surfaceProvider)
    }

    fun setFacing(facing:Int){
        cameraProviderFuture.addListener(Runnable {
            cameraProvider = cameraProviderFuture.get()
            imageCapture = buildImageCapture()
            cameraSelector = buildCameraSelector(facing)
            preview = Preview.Builder().build()
            bind()
        },executor)
    }

    fun takePicture(callback : (bitmap:Bitmap) ->Unit){

        imageCapture.takePicture(executor,object : ImageCapture.OnImageCapturedCallback(){
            @SuppressLint("RestrictedApi")
            override fun onCaptureSuccess(image: ImageProxy) {
                Log.d("CameraProxy","rotation ${image.imageInfo.rotationDegrees}")

                val buffer = image.planes[0].buffer
                val bytes = buffer.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size,null).let {
                    when(image.imageInfo.rotationDegrees){
                        90 -> it.rotate(90f)
                        180 -> it.rotate(180f)
                        270 -> it.rotate(270f)
                        else -> it
                    }.run {
                        if (cameraSelector.lensFacing == CameraSelector.LENS_FACING_FRONT){
                            flip(-1.0f)
                        }else{
                            this
                        }
                    }
                }
                callback(bitmap)
                super.onCaptureSuccess(image)
            }
        })
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }
}



fun AppCompatActivity.startCamera(surfaceProvider: Preview.SurfaceProvider) : CameraProxy = CameraProxy(this,surfaceProvider)

fun Fragment.startCamera(surfaceProvider: Preview.SurfaceProvider) = (requireActivity() as AppCompatActivity).startCamera(surfaceProvider)
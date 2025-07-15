package ph.edu.dlsu.ccs.mobicom.remedication

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : ComponentActivity() {

    private lateinit var viewBinding: ActivityCameraBinding
    private lateinit var cameraController: LifecycleCameraController
    private lateinit var tempPhotoFile: File
    private lateinit var confirmedPhotoFile: File
    private lateinit var cameraExecutor: ExecutorService

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeCamera()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initializeCamera()
        } else {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun initializeCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        tempPhotoFile = File(cacheDir, "temp.jpg")
        confirmedPhotoFile = File(cacheDir, "confirmed.jpg")

        if (tempPhotoFile.exists()) tempPhotoFile.delete()

        cameraController = LifecycleCameraController(this).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            bindToLifecycle(this@CameraActivity)
        }

        viewBinding.previewPv.controller = cameraController

        viewBinding.backImgBtn.setOnClickListener {
            if (tempPhotoFile.exists()) {
                tempPhotoFile.delete()
            }
            finish()
        }

        viewBinding.captureBtn.setOnClickListener {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(tempPhotoFile).build()

            cameraController.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                        cameraExecutor.execute {
                            runOnUiThread {
                                Glide.with(viewBinding.capturedIv).load(tempPhotoFile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(viewBinding.capturedIv)
                                viewBinding.capturedIv.visibility = View.VISIBLE
                                viewBinding.confirmBtn.visibility = View.VISIBLE
                                viewBinding.retakeBtn.visibility = View.VISIBLE
                                viewBinding.previewPv.visibility = View.GONE
                                viewBinding.captureBtn.visibility = View.GONE
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {}
                }
            )
        }

        viewBinding.retakeBtn.setOnClickListener {
            viewBinding.previewPv.visibility = View.VISIBLE
            viewBinding.captureBtn.visibility = View.VISIBLE
            viewBinding.capturedIv.visibility = View.GONE
            viewBinding.confirmBtn.visibility = View.GONE
            viewBinding.retakeBtn.visibility = View.GONE
        }

        viewBinding.confirmBtn.setOnClickListener {
            tempPhotoFile.copyTo(confirmedPhotoFile, overwrite = true)
            tempPhotoFile.delete()
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
package it.wazabit.dev.extension.ui.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import it.wazabit.dev.extension.R
import kotlinx.android.synthetic.main.fragment_camera2.*
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors



import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModelProvider
import it.wazabit.dev.extension.Extensions
import it.wazabit.dev.extensions.camera.startCamera
import it.wazabit.dev.extensions.rotate
import it.wazabit.dev.extensions.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

//typealias LumaListener = (luma: Double) -> Unit


class Camera2Fragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(CameraViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Extensions).applicationComponent.cameraComponent().create().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera2, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cameraProxy = startCamera(fragment_camera2_view_finder.createSurfaceProvider())

//        cameraProxy.setFacing(CameraSelector.LENS_FACING_FRONT)

        fragment_camera2_take_picture_action.setSafeOnClickListener {
            cameraProxy.takePicture {
                viewModel.preview.value = it
                requireActivity().onBackPressed()
            }
        }

    }


    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Camera2Fragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
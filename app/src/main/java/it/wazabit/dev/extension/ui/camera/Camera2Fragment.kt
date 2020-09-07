package it.wazabit.dev.extension.ui.camera


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.camera.startCamera
import it.wazabit.dev.extensions.setSafeOnClickListener
import kotlinx.android.synthetic.main.fragment_camera2.*
import timber.log.Timber

//import javax.inject.Inject

//typealias LumaListener = (luma: Double) -> Unit

@AndroidEntryPoint
class Camera2Fragment : Fragment() {
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//    private val viewModel by lazy {
//        ViewModelProvider(requireActivity(), viewModelFactory).get(CameraViewModel::class.java)
//    }

    private val viewModel:CameraViewModel by activityViewModels()


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

        viewModel.sharedValue.observe(viewLifecycleOwner, Observer {
            Timber.d("Shared value: $it")
        })

        viewModel.sharedValue.value = "Value from camera fragment2"

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
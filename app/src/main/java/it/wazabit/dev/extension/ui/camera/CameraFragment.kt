package it.wazabit.dev.extension.ui.camera

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.wazabit.dev.extension.Extensions
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.activity.checkPermission
import kotlinx.android.synthetic.main.fragment_camera.*
import timber.log.Timber
import javax.inject.Inject


class CameraFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory).get(CameraViewModel::class.java)
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        picturePreviewLauncher.launch(null)
    }

    private val picturePreviewLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        viewModel.preview.value = it
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
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_camera_surface_action.setOnClickListener {
            checkPermission("Why not", Manifest.permission.CAMERA){ granted ->
                if (granted) findNavController().navigate(R.id.action_nav_camera_to_camera2Fragment)
            }
        }

        fragment_camera_picture_preview_action.setOnClickListener {
            checkPermission(requestCameraPermissionLauncher,"Why not", Manifest.permission.CAMERA){ granted ->
                if (granted) picturePreviewLauncher.launch(null)
            }
        }

        viewModel.preview.observe(viewLifecycleOwner, Observer {
            fragment_camera_image_view.setImageBitmap(it)
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
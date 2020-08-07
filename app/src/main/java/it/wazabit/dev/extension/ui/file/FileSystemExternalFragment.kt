package it.wazabit.dev.extension.ui.file

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.activity.checkPermission
import it.wazabit.dev.extensions.activity.contracts.WriteDocument
import it.wazabit.dev.extensions.activity.toast
import it.wazabit.dev.extensions.invisible
import it.wazabit.dev.extensions.visible
import kotlinx.android.synthetic.main.fragment_file_system_external.*
import timber.log.Timber
import java.io.File

class FileSystemExternalFragment : Fragment() {

    private var externalFileMime = "*/*"
    private val uri = MutableLiveData<Uri>()
    private val requestExternalStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) readExternalStorageLauncher.launch(arrayOf(externalFileMime))
    }

        private val readExternalStorageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri->
            this.uri.value = uri
        }

    private val requestExternalStorageWritePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            writeAndLaunch()
        }
    }

    private val writeExternalStorageLauncher = registerForActivityResult(WriteDocument()){ result->
        result.output?.let {
            Timber.d("From ${result.input.name} to ${result.output}")
            result.finalize(requireActivity().contentResolver)
            toast("File create successfully")
        }
        result.input.delete()
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
        return inflater.inflate(R.layout.fragment_file_system_external, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_file_extensions_read_external_storage_action.setOnClickListener {
            checkPermission(requestExternalStoragePermissionLauncher,"Why not", Manifest.permission.READ_EXTERNAL_STORAGE){
                if (it) {
                    registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri->
                        this.uri.value = uri
                    }.launch(arrayOf(externalFileMime))
                }
            }
        }

        fragment_file_extension_mime_read_file_mime_toggle_action.check(R.id.fragment_file_extension_mime_read_file_mime_toggle_action_all)

        fragment_file_extension_mime_read_file_mime_toggle_action.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked){
                externalFileMime = when(checkedId){
                    R.id.fragment_file_extension_mime_read_file_mime_toggle_action_all -> "*/*"
                    R.id.fragment_file_extension_mime_read_file_mime_toggle_action_image -> "image/*"
                    R.id.fragment_file_extension_mime_read_file_mime_toggle_action_video -> "video/*"
                    else -> error("Invalid mime")
                }
            }
        }

        fragment_file_extension_mime_read_file_uri_details_action.setOnClickListener {
            uri.value?.let {
                findNavController().navigate(FileSystemFragmentDirections.actionNavFileSystemToFileDetailsFragment(it))
            }

        }

        fragment_file_extensions_external_add_action.setOnClickListener {
            checkPermission(requestExternalStorageWritePermissionLauncher,"Why not",Manifest.permission.WRITE_EXTERNAL_STORAGE){ isGranted ->
                if (isGranted){
                    writeAndLaunch()
                }
            }
        }

        uri.observe(viewLifecycleOwner, Observer {
            showUriDetails(it)
        })

    }

    private fun writeAndLaunch(){
        val file  = File.createTempFile("file_", ".txt", requireContext().cacheDir)
        val fileContents = "Hello world!"
        file.writeText(fileContents)
        file.useLines {s ->
            s.forEach {
                Timber.d(it)
            }
        }

        writeExternalStorageLauncher.launch(file)
    }

    private fun showUriDetails(uri: Uri?){
        if (uri == null) {
            fragment_file_extension_mime_read_file_uri_details_action.invisible()
            fragment_file_extension_mime_read_file_uri.text = getString(R.string.file_s_uri_label)
        }else{
            fragment_file_extension_mime_read_file_uri_details_action.visible()
            fragment_file_extension_mime_read_file_uri.text = uri.toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FileSystemExternalFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
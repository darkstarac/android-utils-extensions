package it.wazabit.dev.extension.ui.file

import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.FileUtils
import it.wazabit.dev.extensions.getFileInfo
import kotlinx.android.synthetic.main.fragment_file_details.*
import timber.log.Timber

class FileDetailsFragment : Fragment() {



    private val args: FileDetailsFragmentArgs by navArgs()


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
        return inflater.inflate(R.layout.fragment_file_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.uri.let {
            fragment_file_details_uri_content.editText?.setText(it.toString())
            fragment_file_details_uri_path.editText?.setText(it.path)
            fragment_file_details_uri_authority.editText?.setText(it.authority)
            fragment_file_details_uri_scheme.editText?.setText(it.scheme)

            when(it.scheme){
                "file" ->{
                    with(it.toFile().getFileInfo()){
                        fragment_file_details_name.editText?.setText(name)
                        fragment_file_details_size.editText?.setText(Formatter.formatFileSize(requireContext(),size))
                        fragment_file_details_mime_type.editText?.setText(mimeType)
                    }
                }
                else -> {
                    with(FileUtils.getFileInfo(requireContext(),it)){
                        fragment_file_details_name.editText?.setText(name)
                        fragment_file_details_size.editText?.setText(Formatter.formatFileSize(requireContext(),size))
                        fragment_file_details_mime_type.editText?.setText(mimeType)
                    }
                }
            }

            Timber.d("Scheme ${it.scheme}")


        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FileDetailsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
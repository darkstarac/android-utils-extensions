package it.wazabit.dev.extension.ui.file

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.wazabit.dev.extension.R

class FileSystemCacheFragment : Fragment() {


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
        return inflater.inflate(R.layout.fragment_file_system_cache, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FileSystemCacheFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
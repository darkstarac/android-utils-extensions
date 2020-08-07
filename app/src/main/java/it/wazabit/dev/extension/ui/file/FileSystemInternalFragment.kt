package it.wazabit.dev.extension.ui.file

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.text.format.Formatter
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.FileUtils
import it.wazabit.dev.extensions.activity.checkPermission
import it.wazabit.dev.extensions.activity.toast
import it.wazabit.dev.extensions.getFileInfo
import it.wazabit.dev.extensions.recyclerview.RecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_file_extensions_internal_storage_files_recyclerview_item.view.*
import kotlinx.android.synthetic.main.fragment_file_system_internal.*
import java.io.File

class FileSystemInternalFragment : Fragment() {

    private lateinit var viewAdapter: InternalStorageFilesAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val internalFilesSelection = MutableLiveData<MutableList<File>>(mutableListOf())
    private var actionMode:ActionMode? = null
    private var externalFileMime = "*/*"

    private val requestReadExternalStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted)  { copyToInternalStorageLauncher.launch(arrayOf(externalFileMime)) }
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
        return inflater.inflate(R.layout.fragment_file_system_internal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        viewAdapter = InternalStorageFilesAdapter(requireContext())

        fragment_file_extension_internal_storage_files_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


        viewAdapter.setOnClickListener { v, file ->
            val found = internalFilesSelection.value?.find { it.name == file.name }
            val card = (v as MaterialCardView)
            internalFilesSelection.value?.size?.let { size ->
                if (size > 0){
                    if (found != null){
                        internalFilesSelection.value?.filter { it.name != found.name }?.toMutableList().let {list->
                            internalFilesSelection.value = list
                            card.isChecked = false
                        }
                    }else{
                        internalFilesSelection.value?.toMutableList()?.let {list->
                            list.add(file)
                            internalFilesSelection.value = list
                            card.isChecked = true
                        }
                    }
                }else{
                    findNavController().navigate(FileSystemFragmentDirections.actionNavFileSystemToFileDetailsFragment(file.toUri()))
                }
            }
        }

        viewAdapter.setOnLongClickListener { v, file ->
            val found = internalFilesSelection.value?.find { it.name == file.name }
            val card = (v as MaterialCardView)
            if (found != null){
                internalFilesSelection.value?.filter { it.name != found.name }?.toMutableList().let {list->
                    internalFilesSelection.value = list
                    card.isChecked = false
                }
            }else{
                internalFilesSelection.value?.toMutableList()?.let {list->
                    list.add(file)
                    internalFilesSelection.value = list
                    card.isChecked = true
                }
            }
        }

        internalFilesSelection.observe(viewLifecycleOwner, Observer {
            if (actionMode == null){
                if (it.size > 0){
                    actionMode = requireActivity().startActionMode(contextualMenuCallback)
                }
            }else{
                if (it.size == 0) actionMode?.finish()
            }
        })


        fragment_file_extensions_internal_add_action.setOnClickListener {
            checkPermission(requestReadExternalStoragePermissionLauncher,"Why not", Manifest.permission.READ_EXTERNAL_STORAGE){
                if (it) { copyToInternalStorageLauncher.launch(arrayOf(externalFileMime)) }
            }
        }

    }

    private val copyToInternalStorageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){
        it?.let { uri ->
            val file = FileUtils.saveToInternalStorage(requireContext(),uri)
            with(viewAdapter){
                items.add(file)
                notifyDataSetChanged()
            }
            toast("Saved into internal storage")
        }
    }

    private val contextualMenuCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.files_extensions_fragment_contextual_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            internalFilesSelection.observe(viewLifecycleOwner, Observer {
                mode?.title = it.size.toString()
            })
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.delete -> {
                    val names = internalFilesSelection.value?.map { it.name } ?: listOf()

                    with(viewAdapter){
                        val indexes = mutableListOf<Int>()
                        items.forEachIndexed { index, file ->
                            if (names.contains(file.name)) {
                                if (file.delete())
                                    indexes.add(index)
                            }
                        }

                        indexes.reverse()

                        indexes.forEach {
                            items.removeAt(it)
                            notifyItemRemoved(it)
                        }

                        internalFilesSelection.value?.clear()
                        actionMode?.finish()
                        actionMode = null

                    }

                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            val names = internalFilesSelection.value?.map { it.name } ?: listOf()
            viewAdapter.items.forEachIndexed { index, file ->
                if (names.contains(file.name)){
                    viewManager.findViewByPosition(index).let {v->
                        (v as MaterialCardView).isChecked = false
                    }
                }
            }
            internalFilesSelection.value?.clear()
            actionMode = null
        }
    }

    private inner class InternalStorageFilesAdapter(val context: Context) : RecyclerViewAdapter<File, InternalStorageFilesAdapter.ViewHolder>(){

        val items = requireContext().fileList().map {  File(requireContext().filesDir, it) }.toMutableList()

        inner class ViewHolder(val mView:View): RecyclerView.ViewHolder(mView){
            val name: TextView = mView.fragment_file_extensions_internal_storage_files_recyclerview_item_name
            val mime: TextView = mView.fragment_file_extensions_internal_storage_files_recyclerview_item_mime
            val size: TextView = mView.fragment_file_extensions_internal_storage_files_recyclerview_item_size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_file_extensions_internal_storage_files_recyclerview_item, parent, false)
            return ViewHolder(view)

        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            val fileInfo = item.getFileInfo()

            with(holder){
                name.text = fileInfo.name
                mime.text = fileInfo.mimeType
                size.text = Formatter.formatFileSize(context,fileInfo.size)
            }

            with(holder.mView as MaterialCardView) {
                tag = item
                setOnClickListener(onClickListener)
                setOnLongClickListener(onLongClickListener)
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FileSystemInternalFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
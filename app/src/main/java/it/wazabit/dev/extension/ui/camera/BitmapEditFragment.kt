package it.wazabit.dev.extension.ui.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.gone
import it.wazabit.dev.extensions.visible
import kotlinx.android.synthetic.main.fragment_bitmap_edit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class BitmapEditFragment : Fragment() {


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
        return inflater.inflate(R.layout.fragment_bitmap_edit, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            val bitmap = withContext(Dispatchers.IO){
                val landscapeBitmapURL = URL("https://www.hwupgrade.it/i/n/androidmalware_1501_720.jpg")
                val portraitBitmapURL = URL("https://upload.wikimedia.org/wikipedia/commons/thumb/d/db/Android_robot_2014.svg/1200px-Android_robot_2014.svg.png")
                BitmapFactory.decodeStream(portraitBitmapURL.openConnection().getInputStream())
            }
            fragment_bitmap_edit_edit_image_view.bitmap = bitmap
        }


        fragment_bitmap_edit_menu_action_cut.setOnClickListener {
            val bitmap = fragment_bitmap_edit_edit_image_view.cut()
            fragment_bitmap_edit_bitmap_preview_wrapper.visible()
            fragment_bitmap_edit_bitmap_preview.setImageBitmap(bitmap)
        }

        fragment_bitmap_edit_bitmap_preview_action_close.setOnClickListener {
            fragment_bitmap_edit_bitmap_preview_wrapper.gone()
        }

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BitmapEditFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
package it.wazabit.dev.extensions.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import it.wazabit.dev.extensions.R
import it.wazabit.dev.extensions.visible
import kotlinx.android.synthetic.main.progress_dialog_fragment.*

class LoadingDialog : DialogFragment() {

    companion object {
        const val MESSAGE = "message"
        const val BACKGROUND_COLOR = "background_color"
        const val PROGRESS_COLOR = "progress_color"
        const val MESSAGE_COLOR = "message_color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_TITLE, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.progress_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // show message if available
        arguments?.getString(MESSAGE)?.let {
            message.visible()
            message.text = it
        }

        arguments?.getInt(BACKGROUND_COLOR)?.let {
            view.setBackgroundColor(it)
        }

        arguments?.getInt(PROGRESS_COLOR)?.let {
            loading_dialog_progress.indeterminateDrawable.setTint(it)
        }

        arguments?.getInt(MESSAGE_COLOR)?.let {
            message.setTextColor(it)
        }



    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            dialog?.window?.statusBarColor = Color.TRANSPARENT
//        }
//
//
//    }
}
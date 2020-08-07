package it.wazabit.dev.extensions.activity.contracts

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import it.wazabit.dev.extensions.copyTo
import java.io.File

class WriteDocument(private val mimeType:String = "*/*") : ActivityResultContract<File, WriteDocument.WriteDocumentResult>() {

    private lateinit var input:File

    override fun createIntent(context: Context, input: File): Intent {
        this.input = input
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, input.name)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): WriteDocumentResult {
        if (intent == null || resultCode != Activity.RESULT_OK) WriteDocumentResult(input,null)
        return WriteDocumentResult(input,intent?.data)
    }


    data class WriteDocumentResult(val input:File,val output:Uri?){
        fun finalize(contentResolver: ContentResolver){
            output?.let {
                input.copyTo(output,contentResolver)
            }
        }
    }

}
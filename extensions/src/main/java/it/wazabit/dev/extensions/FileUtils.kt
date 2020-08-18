package it.wazabit.dev.extensions

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File

data class FileInfo(
    val name:String,
    val size:Long,
    val mimeType:String?
){
    val extension:String
    get() { return  name.substring(name.lastIndexOf(".") + 1)}
}

object FileUtils {


    fun saveToFilesDir(context: Context, uri:Uri): File {
        val fileInfo = context.contentResolver.getFileInfo(uri)

        context.openFileOutput(fileInfo.name, Context.MODE_PRIVATE).use { outPutStream ->
            context.contentResolver.openInputStream(uri).use { inputStream ->
                inputStream?.copyTo(outPutStream)
            }
        }

        return File(context.filesDir,fileInfo.name)
    }

    fun saveToCacheDir(context: Context, uri:Uri): File {
        val fileInfo = context.contentResolver.getFileInfo(uri)

        context.openFileOutput(fileInfo.name, Context.MODE_PRIVATE).use { outPutStream ->
            context.contentResolver.openInputStream(uri).use { inputStream ->
                inputStream?.copyTo(outPutStream)
            }
        }

        return File(context.cacheDir,fileInfo.name)
    }

    fun getFileInfo(context: Context,uri: Uri): FileInfo {
        val contentResolver = context.contentResolver
        return contentResolver.getFileInfo(uri)
    }

    fun getTemporaryFile(context: Context,uri: Uri): File {
        val fileInfo = getFileInfo(context, uri)
        val file = File.createTempFile(fileInfo.name, fileInfo.extension, context.cacheDir)
        context.openFileOutput(fileInfo.name, Context.MODE_PRIVATE).use { outPutStream ->
            file.inputStream().use { inputStream ->
                inputStream.copyTo(outPutStream)
            }
        }

        return file
    }


}

fun ContentResolver.getFileInfo(uri:Uri) : FileInfo{
    if (uri.scheme == "file") error("Invalid scheme file://")
    var fileInfo:FileInfo? = null
    val mimeType= getType(uri) ?: error("Unknown mime type")
    query(uri, null, null, null, null)?.use {
        fileInfo = it.let {cursor->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            FileInfo(
                cursor.getString(nameIndex),
                cursor.getLong(sizeIndex),
                mimeType
            )
        }
    }

    return fileInfo!!
}



fun File.getFileInfo() : FileInfo{
    return FileInfo(
        name,
        length(),
        getMimeType()
    )
}


fun File.copyTo(uri:Uri, contentResolver: ContentResolver) {
    contentResolver.openOutputStream(uri)?.use { outputStream ->
        inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}

fun File.getMimeType(fallback: String = "*/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase()) }
        ?: fallback
}





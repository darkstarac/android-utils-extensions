package it.wazabit.dev.extensions.activity


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * Show a loading [DialogFragment].
 *
 * @param loading true if the dialog fragment should be shown, false otherwise.
 * @param dialog the dialog fragment we want to show during loading.
 */
fun FragmentActivity.loading(loading: Boolean, dialog: DialogFragment?, arguments: Bundle? = null) {
    val tag = "loading_dialog"
    if (loading) {
        val ft = supportFragmentManager.beginTransaction()

        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        dialog?.arguments = arguments

        dialog?.show(ft, tag)
    } else {
        val ft = supportFragmentManager.beginTransaction()

        val prev = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            (prev as? DialogFragment)?.dismiss()
            ft.remove(prev)
        }
    }
}


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Fragment.showKeyboard() {
    view?.let { requireActivity().showKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Activity.hasPermission(permission:String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.checkPermission(launcher: ActivityResultLauncher<String>,message:String, permission:String,continuation : (boolean:Boolean) -> Unit){
    when {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> continuation(true)
        shouldShowRequestPermissionRationale(permission) -> {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    launcher.launch(permission)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    continuation(false)
                    dialog.dismiss()
                }
                .create()
                .show()
        }
        else -> launcher.launch(permission)
    }
}

fun Activity.checkPermission(message:String, permission:String,continuation : (boolean:Boolean) -> Unit){
    when {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> continuation(true)
        shouldShowRequestPermissionRationale(permission) -> {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, which ->
                    continuation(true)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    continuation(false)
                    dialog.dismiss()
                }
                .create()
                .show()
        }
        else -> error("What else ?")
    }
}

fun Fragment.hasPermission(permission:String) = (requireActivity() as Activity).hasPermission(permission)

fun Fragment.checkPermission(launcher: ActivityResultLauncher<String>,message:String, permission:String,continuation : (boolean:Boolean) -> Unit) = (requireActivity() as Activity).checkPermission(launcher, message, permission, continuation)

fun Fragment.checkPermission(message:String, permission:String,continuation : (boolean:Boolean) -> Unit) = (requireActivity() as Activity).checkPermission(message, permission, continuation)

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun Activity.toast(message:CharSequence,duration:Int = Toast.LENGTH_LONG){
    Toast.makeText(this,message,duration).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Fragment.toast(message:CharSequence,duration:Int = Toast.LENGTH_LONG){
    Toast.makeText(activity,message,duration).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}




fun Context.dpToPx(value:Float):Float{
    return resources.displayMetrics.let{metrics ->
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
    }
}


inline fun <reified T : Any> Activity.startActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

inline fun <reified T : Any> Context.startActivity(options: Bundle? = null, noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)
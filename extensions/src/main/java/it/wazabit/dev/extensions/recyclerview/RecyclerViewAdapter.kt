package it.wazabit.dev.extensions.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView



abstract class RecyclerViewAdapter<T:Any,V:RecyclerView.ViewHolder> : RecyclerView.Adapter<V>(){

    protected val onClickListener: View.OnClickListener
    protected val onLongClickListener: View.OnLongClickListener

    private var listener: ((v:View,T) -> Unit)? = null
    private var longListener: ((v:View,T) -> Unit)? = null

    fun setOnClickListener(l: (v:View,T) -> Unit){ this.listener = l }
    fun setOnLongClickListener(l: (v:View,T) -> Unit){ this.longListener = l }

    protected open fun onItemClick(v: View, item: T) {}
    protected open fun onItemLongClick(v: View, item: T) {}

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as T
            onItemClick(v,item)
            listener?.let { it(v,item) }
        }

        onLongClickListener = View.OnLongClickListener {v ->
            val item = v.tag as T
            onItemLongClick(v,item)
            longListener?.let { it(v,item) }
            true
        }
    }
}
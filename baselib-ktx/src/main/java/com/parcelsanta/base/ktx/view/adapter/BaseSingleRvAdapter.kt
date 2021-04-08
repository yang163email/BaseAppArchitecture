package com.parcelsanta.base.ktx.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.parcelsanta.base.ktx.view.ItemClickListener
import com.parcelsanta.base.ktx.view.ItemClickListenerImpl

/**
 * @author : yan
 * @date   : 2019/11/18 10:20
 * @desc   : BaseSingleRvAdapter
 */
abstract class BaseSingleRvAdapter<T>(@LayoutRes val layoutId: Int) :
    RecyclerView.Adapter<BaseSingleRvAdapter.BaseHolder>(),
    ItemClickListener<T> by ItemClickListenerImpl<T>() {

    val datas = arrayListOf<T>()

    open fun setData(data: List<T>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BaseHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val bean = datas[position]
        holder.itemView.apply {
            setOnClickListener { itemClickListener?.invoke(position, bean) }
            setOnLongClickListener { itemLongClickListener?.invoke(bean) ?: false }
        }
    }

    class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
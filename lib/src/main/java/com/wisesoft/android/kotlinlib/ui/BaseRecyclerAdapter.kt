package com.wisesoft.android.kotlinlib.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.wisesoft.android.kotlinlib.utils.getView

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/7/1 15:01.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
abstract class BaseRecyclerAdapter<T>(
    private val mContext: Context,
    private val mBean: List<T>,
    private val bindAction: View.(bean: T) -> Unit?
) : RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>() {
    protected abstract fun getItemView(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mContext.getView(getItemView(), parent))
    }

    override fun getItemCount() = mBean.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.bindAction(mBean[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
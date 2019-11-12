package com.wisesoft.android.kotlinlib.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.wisesoft.android.kotlinlib.utils.getView

/**
 * @Title:  impleRecyclerAdapter
 * @Package com.wisesoft.android.kotlinlib.ui
 * @Description:    封装一个常用的Adapter
 * @date Create on 2018/7/1 15:14.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class SimpleRecyclerAdapter<T>(
    private val mContext: Context,
    private val mViewId: Int,
    private val mBean: List<T>,
    private val bindAction: View.(bean: T) -> Unit
) : RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder>() {

    private val mHeaderView = ArrayList<View>()
    private val mFooterView = ArrayList<View>()

    fun addHeaderView(view: View) {
        if (mHeaderView.add(view))
            notifyDataSetChanged()
    }

    fun removeHeaderView(view: View?) {
        if (mHeaderView.remove(view))
            notifyDataSetChanged()
    }

    fun addFooterView(view: View) {
        if (mFooterView.add(view))
            notifyDataSetChanged()
    }

    fun removeFooterView(view: View?) {
        if (mFooterView.remove(view))
            notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleRecyclerAdapter.ViewHolder {
        return when {
            viewType < mHeaderView.size -> ViewHolder(mHeaderView[viewType])
            viewType >= mHeaderView.size + mBean.size -> ViewHolder(mFooterView[viewType - mBean.size - mHeaderView.size])
            else -> ViewHolder(mContext.getView(mViewId, parent))
        }
    }

    override fun onBindViewHolder(holder: SimpleRecyclerAdapter.ViewHolder, position: Int) {
        if (position in (mHeaderView.size until mHeaderView.size + mBean.size) && mBean.isNotEmpty())
            holder.itemView.bindAction(mBean[position - mHeaderView.size])
    }

    override fun getItemCount() = mBean.size + mHeaderView.size + mFooterView.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}


fun RecyclerView.addHeaderView(viewId: Int, block: View.() -> View) {
    if (adapter is SimpleRecyclerAdapter<*>) {
        (adapter as SimpleRecyclerAdapter<*>).addHeaderView(context.getView(viewId, this).block())
    }
}

fun RecyclerView.addFooterView(viewId: Int, block: View.() -> View) {
    if (adapter is SimpleRecyclerAdapter<*>) {
        (adapter as SimpleRecyclerAdapter<*>).addFooterView(context.getView(viewId, this).block())
    }
}
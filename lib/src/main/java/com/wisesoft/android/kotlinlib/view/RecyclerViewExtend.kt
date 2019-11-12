package com.wisesoft.android.kotlinlib.view

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.wisesoft.android.kotlinlib.utils.getOneDpi

/**
 * @Title:  RecyclerViewExtend
 * @Package com.wisesoft.android.kotlinlib.view
 * @Description: RecyclerView初始化
 * @date Create on 2018/7/1 16:45.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
/**
 * initVertical
 * @description 初始化垂直方向
 * @param needDecoration 是否需要分割线，默认false
 * @param color 分割线色码
 */
fun RecyclerView.initVertical(
    needDecoration: Boolean? = false,
    color: String? = null
): RecyclerView {
    val layoutManager = LinearLayoutManager(this.context)
    layoutManager.orientation = LinearLayoutManager.VERTICAL
    this.layoutManager = layoutManager
    if (needDecoration == true) {
        addItemDecoration(
            CommItemDecoration.createVertical(
                Color.parseColor(color ?: "#E5E5E5"),
                context.getOneDpi().toInt()
            )
        )
    }
    return this
}

/**
 * initVertical
 * @description 初始化水平方向
 * @param needDecoration 是否需要分割线，默认false
 * @param color 分割线色码
 */
fun RecyclerView.initHorizontal(
    needDecoration: Boolean? = false,
    color: String? = null
): RecyclerView {
    val layoutManager = LinearLayoutManager(this.context)
    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
    this.layoutManager = layoutManager
    if (needDecoration == true) {
        addItemDecoration(
            CommItemDecoration.createHorizontal(
                Color.parseColor(color ?: "#E5E5E5"),
                context.getOneDpi().toInt()
            )
        )
    }
    return this
}

/**
 * 初始化 SmartRefreshLayout
 * @param enableLoadmore 是否支持加载更多
 * @param enableRefresh 是否支持下拉刷新
 * @param listener 下拉刷新回调
 * @
 */
fun SmartRefreshLayout.init(
    enableRefresh: Boolean = true,
    enableLoadmore: Boolean = false,
    listener: OnRefreshListener? = null,
    loadmoreListener: OnLoadmoreListener? = null
): SmartRefreshLayout {
    this.isEnableRefresh = enableRefresh
    this.isEnableLoadmore = enableLoadmore
    this.setOnRefreshListener(listener)
    this.setOnLoadmoreListener(loadmoreListener)
    return this
}

/**
 *关闭加载
 * @param mode  0 下拉刷新，1上拉加载更多
 */
fun SmartRefreshLayout.finishLoad(
    mode: Int = 0
): SmartRefreshLayout {
    when (mode) {
        0 -> {
            this.finishRefresh()
        }
        1 -> {
            this.finishLoadmore()
        }
    }
    return this
}
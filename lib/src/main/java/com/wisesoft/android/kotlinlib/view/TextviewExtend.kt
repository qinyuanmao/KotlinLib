package com.wisesoft.android.kotlinlib.view

import android.view.View
import android.widget.TextView

/**
 * @Title:  TextView扩展方法
 * @date Create on 2018/6/29 16:37.
 * @author Create by yangmin
 */

/**
 * 设置气泡显示数字
 */
fun TextView.settextBubble(count: Int = 0): TextView {
    when {
        count == 0 ->
            this.visibility = View.GONE
        count in 1..99 -> {
            this.visibility = View.VISIBLE
            this.text = "$count"
        }
        count > 99 -> {
            this.visibility = View.VISIBLE
            this.text = "$99+"
        }
    }
    return this
}





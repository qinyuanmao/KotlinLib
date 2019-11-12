package com.wisesoft.android.kotlinlib.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * @Title:  ${EditTextExtend}
 * @Package ${cn.qinyuanmao.android.qymlib.utils}
 * @Description:    ${add EditText }
 * @date Create on 2018/6/29 16:37.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun EditText.hideInput() {
    clearFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun EditText.trimText(): String = text.toString().trim()

fun EditText.isNullText(): Boolean = text.toString().trim().isEmpty()




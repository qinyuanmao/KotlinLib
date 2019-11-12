package com.wisesoft.android.kotlinlib.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import com.wisesoft.android.kotlinlib.network.http.NetworkAction
import com.wisesoft.android.kotlinlib.utils.*

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/30 22:46.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun Fragment.toast(message: String?) = activity?.applicationContext?.toast(message)

fun Fragment.longToast(message: String?) = activity?.applicationContext?.longToast(message)

inline fun <reified T : Activity> Fragment.startActivity(vararg args: Pair<String, Any?>?) {
    activity?.run {
        startActivity(IntentManager.createIntent<T>(this, args))
    }
}

inline fun <reified T : Activity> Fragment.getIntent(vararg args: Pair<String, Any?>?): Intent? =
    activity?.let {
        IntentManager.createIntent<T>(it, args)
    }

fun Fragment.sendSMS(number: String, text: String) = activity?.sendSMS(number, text)

fun Fragment.callPhone(tel: String, isCallNow: Boolean = false) = activity?.callPhone(tel, isCallNow)

fun Fragment.browser(url: String, newTask: Boolean = false) = activity?.browser(url, newTask)

fun Fragment.share(text: String, subject: String = "", title: String? = null) = activity?.share(text, subject, title)

fun Fragment.email(email: String, subject: String = "", text: String = "") = activity?.email(email, subject, text)

fun Fragment.ui(action: Context.() -> Unit) = activity?.ui(action)

inline fun <reified T> Fragment.networkUiTask(
    init: NetworkAction<T>.() -> Unit
) = activity?.networkUiTask(init)

fun Fragment.getProgressDialog(
    title: String? = null,
    message: String,
    cancelable: Boolean = true
) = activity?.getProgressDialog(title, message, cancelable)
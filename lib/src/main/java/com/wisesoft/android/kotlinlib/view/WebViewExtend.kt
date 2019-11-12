package com.wisesoft.android.kotlinlib.view

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import java.lang.reflect.InvocationTargetException

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/7/1 16:36.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
@SuppressLint("SetJavaScriptEnabled")
fun WebView.init(): WebView {
    settings.allowFileAccess = true
    settings.javaScriptEnabled = true
    try {
        val clazz = settings.javaClass
        val method = clazz.getMethod(
                "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType)
        method?.invoke(settings, true)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }

    webChromeClient = WebChromeClient()
    val webSettings = settings
    webSettings.javaScriptEnabled = true
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.defaultTextEncodingName = "utf-8"
    webSettings.domStorageEnabled = true
    setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
            goBack()
            return@OnKeyListener true
        }
        false
    })
    return this
}

fun WebView.progressChange(action: (Int) -> Unit) {
    webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            action(newProgress)
        }
    }
}


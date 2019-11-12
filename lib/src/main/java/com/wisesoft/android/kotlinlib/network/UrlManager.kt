package com.wisesoft.android.kotlinlib.network

import net.wisesoft.android.kotlinlib.BuildConfig

/**
 * @Title: UrlManager
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:  根据是否debug设置请求地址
 * @date Create on 2018/6/30 21:47.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
object UrlManager {
    private var DEBUG_URL: String? = null
    private var RELEASE_URL: String? = null
    private var DEBUG_SOCKET_URL: String? = null
    private var RELEASE_SOCKET_URL: String? = null

    fun initApiUrl(debugUrl: String, releaseUrl: String) {
        DEBUG_URL = debugUrl
        RELEASE_URL = releaseUrl
    }

    fun getApiUrl(): String = (if (BuildConfig.DEBUG) DEBUG_URL else RELEASE_URL) ?: ""

    fun initSocket(debugUrl: String, releaseUrl: String) {
        DEBUG_SOCKET_URL = debugUrl
        RELEASE_SOCKET_URL = releaseUrl
    }

    fun getSocketUrl(): String = (if (BuildConfig.DEBUG) DEBUG_SOCKET_URL else RELEASE_SOCKET_URL) ?: ""
}
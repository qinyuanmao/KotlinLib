package com.wisesoft.android.kotlinlib.network.socket

import android.content.Context
import com.wisesoft.android.kotlinlib.network.NetworkFactory
import com.wisesoft.android.kotlinlib.network.UrlManager
import com.wisesoft.android.kotlinlib.network.http.HeaderFactory
import com.wisesoft.android.kotlinlib.utils.Time
import okhttp3.WebSocket

/**
 * @Title:  SocketParam
 * @Package  com.wisesoft.android.kotlinlib.api.socket
 * @Description:  socket请求参数管理
 * @date Create on 2018/11/9 21:01.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class SocketParam {
    lateinit var socket: WebSocket
    var reqEncode = false
    var respEncode = false
    var encodeKey = NetworkFactory.aesPassword
    lateinit var context: Context
    var url = UrlManager.getSocketUrl()
    var header = HashMap<String, String>()
    fun headers(init: HeaderFactory.() -> Unit) {
        val headerMap = HeaderFactory()
        headerMap.init()
        header["Content-Disposition"] = "form-data"
        header.putAll(headerMap.map)
    }
    var heartBlock: (() -> String)? = null
    var heartTime = Time.SECOND * 5
    var key: String = "default"
    var needReconnect = true
    var reconnectTime = Time.SECOND * 5
}

fun createSocketParam(init: SocketParam.() -> Unit): SocketParam {
    val socketParam = SocketParam()
    socketParam.init()
    return socketParam
}
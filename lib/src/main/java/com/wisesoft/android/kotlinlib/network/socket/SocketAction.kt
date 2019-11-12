package com.wisesoft.android.kotlinlib.network.socket

import android.util.Log
import com.wisesoft.android.kotlinlib.network.http.aesDecode
import com.wisesoft.android.kotlinlib.network.http.aesEncode
import com.wisesoft.android.kotlinlib.utils.delayTask
import com.wisesoft.android.kotlinlib.utils.isNetworkAvailable
import com.wisesoft.android.kotlinlib.utils.launch
import com.wisesoft.android.kotlinlib.utils.ui
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import okhttp3.*
import okio.ByteString

/**
 * @Title:  Socket
 * @Package com.wisesoft.android.kotlinlib.network.socket
 * @Description:  Socket连接器
 * @date Create on 2018/11/12 09:04.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

fun connectSocket(init: SocketRunningTime.() -> Unit): Job {
    val runningTime = SocketRunningTime()
    runningTime.init()
    return doConnectSocket(runningTime)
}

private fun doConnectSocket(runningTime: SocketRunningTime): Job {
    val TAG = "WebSocket response"
    val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            runningTime.param.context.ui { runningTime.onOpen?.invoke(response) }
            println("WebSocket连接接成功！")
            Log.i(TAG, response.message())
            runningTime.param.heartBlock?.let {
                delayTask(runningTime.param.heartTime) {
                    webSocket.send(it())
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            runningTime.param.context.ui { runningTime.onMessage?.invoke(
                if (runningTime.param.respEncode)
                    bytes.string(Charsets.UTF_8).aesDecode(runningTime.param.encodeKey) ?: ""
                else
                    bytes.string(Charsets.UTF_8)
            ) }
            println("WebSocket接受到消息！")
            Log.i(TAG, bytes.toString())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            println("WebSocket接受到消息！")
            Log.i(TAG, text)
            runningTime.param.context.ui {
                runningTime.onMessage?.invoke(
                    if (runningTime.param.respEncode)
                        text.aesDecode(runningTime.param.encodeKey) ?: ""
                    else
                        text
                )
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            runningTime.param.context.ui { runningTime.onClosed?.invoke(code, reason) }
            println("WebSocket已关闭！")
            Log.i(TAG, reason)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            runningTime.param.context.ui { runningTime.onClosing?.invoke(code, reason) }
            println("WebSocket正在关闭！")
            Log.i(TAG, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            runningTime.param.context.ui { runningTime.onFailure?.invoke(response) }
            println("WebSocket连接失败！")
            Log.i(TAG, response?.message() ?: (response?.message() ?: "WebSocket连接失败！"))
            if (runningTime.param.needReconnect) {
                launch {
                    val channel = Channel<Boolean>()
                    val job = GlobalScope.async {
                        while (true) {
                            Log.i(TAG, "WebSocket is reconnecting!")
                            isServerCanConnect(runningTime, channel)
                            delay(runningTime.param.reconnectTime)
                        }
                    }
                    repeat(1) {
                        if (channel.receive()) {
                            Log.i(TAG, "WebSocket is reconnected success!")
                            job.cancel()
                            doConnectSocket(runningTime)
                        }
                    }
                }
            }
        }
    }
    return launch {
        val requestBuilder = Request.Builder().url(runningTime.param.url)
        runningTime.param.header.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
        val client = OkHttpClient.Builder().build()
        runningTime.param.socket = client.newWebSocket(requestBuilder.build(), socketListener)
        SocketManager.INSTANCE.addSocket(runningTime.param.key to runningTime.param)
    }
}

private fun isServerCanConnect(runningTime: SocketRunningTime, channel: Channel<Boolean>) {
    if (runningTime.param.context.isNetworkAvailable()) {
        val socketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                launch { channel.send(true) }
                webSocket.cancel()
            }
        }
        launch {
            val requestBuilder = Request.Builder().url(runningTime.param.url)
            runningTime.param.header.forEach {
                requestBuilder.addHeader(it.key, it.value)
            }
            val client = OkHttpClient.Builder().build()
            client.newWebSocket(requestBuilder.build(), socketListener)
        }
    }
}
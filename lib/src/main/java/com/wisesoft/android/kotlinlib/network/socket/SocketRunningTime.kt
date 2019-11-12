package com.wisesoft.android.kotlinlib.network.socket

import okhttp3.Response
import okhttp3.WebSocket

/**
 * @Title:  SocketRunningTime
 * @Package com.wisesoft.android.kotlinlib.network.socket
 * @Description:  Socket运行时返回
 * @date Create on 2018/11/9 22:03.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class SocketRunningTime {
    lateinit var param: SocketParam
    var onOpen: ((response: Response) -> Unit)? = null
    var onMessage: ((text: String) -> Unit)? = null
    var onClosing: ((code: Int, reason: String) -> Unit)? = null
    var onClosed: ((code: Int, reason: String) -> Unit)? = null
    var onFailure: ((response: Response?) -> Unit)? = null
}
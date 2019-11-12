package com.wisesoft.android.kotlinlib.network.socket

import com.wisesoft.android.kotlinlib.network.http.aesEncode

/**
 * @Title:  SocketManager
 * @Package com.wisesoft.android.kotlinlib.network.socket
 * @Description:  针对有多个和单个的WebSocket对象进行管理
 * @date Create on 2018/11/10 14:50.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class SocketManager {
    private val mSocketMap = HashMap<String, SocketParam>()
    companion object {
        val INSTANCE: SocketManager = SocketManager()
    }

    infix fun addSocket(pair: Pair<String, SocketParam>) {
        mSocketMap[pair.first] = pair.second
    }

    fun removeSocket(key: String = "default") {
        mSocketMap[key]?.socket?.close(4099, null)
        mSocketMap.remove(key)
    }

    fun removeAll() {
        for (key in mSocketMap.keys) {
            removeSocket(key)
        }
    }

    fun sendMessage(message: String, key: String = "default") {
        val param = mSocketMap[key]
        param?.run {
            socket.send(if (reqEncode) message.aesEncode(encodeKey) ?: "" else message)
        }
    }

}
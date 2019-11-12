package com.wisesoft.android.kotlinlib.service

/**
 * @Title:  ServiceManager
 * @Package com.wisesoft.android.kotlinlib.service
 * @Description:   创建一个快捷的bindService
 * @date Create on 2018/11/13 15:16.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

class ConnectionAction {
    var disconnected: (() -> Unit)? = null
    lateinit var connected: () -> Unit
}
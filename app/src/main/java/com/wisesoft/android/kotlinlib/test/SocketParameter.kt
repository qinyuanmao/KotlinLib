package com.wisesoft.android.kotlinlib.test

import android.content.Context
import com.wisesoft.android.kotlinlib.network.socket.createSocketParam

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/11/12 14:17.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun createSocketParameter(ctx: Context) = createSocketParam {
    url = "ws://qinyuanmao.cn:8081"
    key = "heart"
    context = ctx
    needReconnect = true
}
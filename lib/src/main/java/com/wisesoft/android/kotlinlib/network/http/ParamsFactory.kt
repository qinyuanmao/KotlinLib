package com.wisesoft.android.kotlinlib.network.http

/**
 * @Title:  HeaderFactory
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:   网络请求参数构造器
 * @date Create on 2018/11/6 18:39.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

class ParamsFactory {
    var map = HashMap<String, Any?>()
    operator fun String.rangeTo(value: Any?) {
        map[this] = value
    }
}
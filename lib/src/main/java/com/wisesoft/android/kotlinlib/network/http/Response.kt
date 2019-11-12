package com.wisesoft.android.kotlinlib.network.http

/**
 * @Title:  Response
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:  网络请求返回构造器
 * @date Create on 2018/11/6 17:15.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
data class Response<T> (
    var data: T? = null,
    var error: String? = null,
    var isSuccess: Boolean = false,
    var statusCode: Int = 0
)
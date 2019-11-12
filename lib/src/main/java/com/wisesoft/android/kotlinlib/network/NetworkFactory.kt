package com.wisesoft.android.kotlinlib.network

import com.wisesoft.android.kotlinlib.utils.toHashMap
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @Title:  NetworkFactory
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:    初始化和提供修改OkHttp参数
 * @date Create on 2018/11/6 08:45.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
object NetworkFactory {
    var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    var header: HashMap<String, String> = HashMap()

    var body: HashMap<String, Any?> = HashMap()

    var contentType = "application/json;charset=utf-8"

    var aesPassword: String? = null

    var paramKey: String? = null
}

class InitNetWork {
    var client: OkHttpClient? = null

    var header: Map<String, String>? = null

    var body: Map<String, Any?>? = null

    var aesPassword: String? = null

    var contentType: String? = null

    var paramKey: String? = null
}

fun initNetworkFactory(init: InitNetWork.() -> Unit) {
    val netWork = InitNetWork()
    netWork.init()
//    netWork.client?.let { NetworkFactory.client = it }
    NetworkFactory.client = netWork.client ?: OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    netWork.header?.let { NetworkFactory.header = it.toHashMap() as HashMap<String, String> }
    netWork.body?.let { NetworkFactory.body = it.toHashMap() }
    netWork.contentType?.let { NetworkFactory.contentType = it }
    netWork.aesPassword?.let { NetworkFactory.aesPassword = it }
    netWork.paramKey?.let { NetworkFactory.paramKey = it }
}
package com.wisesoft.android.kotlinlib.network.http

import android.content.Context
import com.wisesoft.android.kotlinlib.network.NetworkFactory
import com.wisesoft.android.kotlinlib.utils.toJsonStr
import kotlinx.coroutines.channels.Channel

/**
 * @Title:  Require
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:    请求构造器
 * @date Create on 2018/11/5 21:31.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

class Require {
    var reqEncode = false
    var respEncode = false
    var url: String? = null
    var header: HashMap<String, String> =
        NetworkFactory.header
    var method: Method =
        Method.POST
    var body: HashMap<String, Any?> = HashMap()
    fun params(init: ParamsFactory.() -> Unit) {
        val paramMap = ParamsFactory()
        paramMap.init()
        NetworkFactory.paramKey?.let {
            val map = HashMap<String, Any?>()
            map[it] = paramMap.map.toJsonStr()
            paramMap.map.clear()
            paramMap.map = map
        }
        if (reqEncode) {
            paramMap.map.forEach {
                body[it.key] = it.value.toString().aesEncode(NetworkFactory.aesPassword)
            }
        } else {
            body.putAll(paramMap.map)
        }
    }
    fun headers(init: HeaderFactory.() -> Unit) {
        val headerMap = HeaderFactory()
        headerMap.init()
        header.putAll(headerMap.map)
    }
}

class Download {
    var method: Method =
        Method.GET
    val channel = Channel<String?>()
    lateinit var context: Context
    lateinit var url: String
    var savePath: String? = null
    var fileName: String? = null
    var reqEncode = false
    var header: HashMap<String, String> =
        NetworkFactory.header
    fun headers(init: HeaderFactory.() -> Unit) {
        val headerMap = HeaderFactory()
        headerMap.init()
        header["Content-Disposition"] = "form-data"
        header.putAll(headerMap.map)
    }
    var body: HashMap<String, Any?> = HashMap()
    fun params(init: ParamsFactory.() -> Unit) {
        val paramMap = ParamsFactory()
        paramMap.init()
        NetworkFactory.paramKey?.let {
            val map = HashMap<String, Any?>()
            map[it] = paramMap.toJsonStr()
            paramMap.map.clear()
            paramMap.map = map
        }
        if (reqEncode) {
            paramMap.map.forEach {
                body[it.key] = it.value.toString().aesEncode(NetworkFactory.aesPassword)
            }
        } else {
            body.putAll(paramMap.map)
        }
    }
}

class Upload {
    val channel = Channel<String?>()
    lateinit var context: Context
    lateinit var url: String
    lateinit var fileKey: String
    lateinit var filePath: String
    var reqEncode = false
    var respEncode = false
    var header: HashMap<String, String> =
        NetworkFactory.header
    fun headers(init: HeaderFactory.() -> Unit) {
        val headerMap = HeaderFactory()
        headerMap.init()
        header["Content-Disposition"] = "form-data"
        header.putAll(headerMap.map)
    }
    var body: HashMap<String, Any?> = HashMap()
    fun params(init: ParamsFactory.() -> Unit) {
        val paramMap = ParamsFactory()
        paramMap.init()
        NetworkFactory.paramKey?.let {
            val map = HashMap<String, Any?>()
            map[it] = paramMap.toJsonStr()
            paramMap.map.clear()
            paramMap.map = map
        }
        if (reqEncode) {
            paramMap.map.forEach {
                body[it.key] = it.value.toString().aesEncode(NetworkFactory.aesPassword)
            }
        } else {
            body.putAll(paramMap.map)
        }
    }
}

fun createRequire(init: Require.() -> Unit): Require {
    val require = Require()
    require.init()
    return require
}

fun createDownload(init: Download.() -> Unit): Download {
    val download = Download()
    download.init()
    return download
}

fun createUpload(init: Upload.() -> Unit): Upload {
    val upload = Upload()
    upload.init()
    return upload
}


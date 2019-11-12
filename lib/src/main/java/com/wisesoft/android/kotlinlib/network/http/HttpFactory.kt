package com.wisesoft.android.kotlinlib.network.http

import android.os.Environment
import com.wisesoft.android.kotlinlib.utils.async
import com.wisesoft.android.kotlinlib.utils.toJsonStr
import kotlinx.coroutines.Deferred
import okhttp3.*
import java.io.IOException
import android.text.TextUtils
import com.google.gson.Gson
import com.wisesoft.android.kotlinlib.network.NetworkFactory
import com.wisesoft.android.kotlinlib.network.UrlManager
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.random.Random
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * @Title:  RequireFactory
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:    网络请求
 * @date Create on 2018/11/5 21:31.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

/**
 * http json网络请求
 * @param require 请求内容
 * @return Deferred<Response<T>?>? 请求返回的Deferred
 */
inline fun <reified T> doHttp(require: Require): Deferred<Response<out T>?>? {
    var url = require.url
    if (url != null) {
        if (url.startsWith("/", false)) {
            url = UrlManager.getApiUrl() + url
        }
        if (require.method == Method.GET) {
            if (require.body.size != 0) {
                url += "?"
            }
            require.body.forEach {
                url += if (url.endsWith("?", false)) {
                    "${it.key}=${it.value}"
                } else {
                    "&${it.key}=${it.value}"
                }
            }
        }
        return async {
            doNetwork<T>(
                url,
                NetworkFactory.contentType,
                require
            )?.await()
        }
    } else {
        return async {
            Response<T>(null, "请求地址有误！", false, 404)
        }
    }
}

/**
 * http json网络请求
 * @param url 请求地址
 * @param contentType 请求方式
 * @param require 请求体
 * @return Deferred<Response<T>?>? 请求返回的Deferred
 */
inline fun <reified T> doNetwork(
    url: String,
    contentType: String,
    require: Require
): Deferred<Response<out T>?>? {
    val requestBuilder = Request.Builder()
        .url(url)
        .headers(Headers.of(require.header))
    println(contentType)
    if (require.method == Method.POST)
        requestBuilder.post(
            when {
                contentType.startsWith("multipart/form-data", true) -> {
                    val builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)
                    require.body.forEach { (key, value) ->
                        builder.addFormDataPart(key, value.toString())
                    }
                    builder.build()
                }
                contentType.startsWith("application/x-www-form-urlencoded", true)-> {
                    var params = ""
                    require.body.forEach {
                        params += "${it.key}=${it.value}&"
                    }
                    RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;chatset=utf8"), params)
                }
                else ->
                    RequestBody.create(MediaType.parse("application/json;chatset=utf8"), require.body.toJsonStr())
            }
        )
    println(require.body.toJsonStr())
    val request = requestBuilder.build()
    return async {
        try {
            val response = NetworkFactory.client.newCall(request).execute()
            if (response.isSuccessful) {
                Response(
                    if (require.respEncode) {
                        Gson().fromJson<T>(
                            response.body()?.string()?.aesDecode(NetworkFactory.aesPassword) ?: "{}", T::class.java
                        )
                    } else {
                        Gson().fromJson<T>(response.body()?.string() ?: "{}", T::class.java)
                    },
                    null,
                    true,
                    response.code()
                )
            } else {
                Response<T>(
                    null,
                    response.message(),
                    false,
                    response.code()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Response<T>(null, e.message, false, 404)
        }
    }
}

inline fun <reified T> upload(upload: Upload) = async {
    if (upload.url.startsWith("/", false)) {
        upload.url = UrlManager.getApiUrl() + upload.url
    }
    val file = File(upload.filePath)
    return@async if (!file.exists()) {
        upload.channel.close()
        Response<T>(null, "文件不存在！", false, 404)
    } else {
        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(upload.fileKey, file.name, fileBody)
        upload.body.forEach {
            requestBodyBuilder.addFormDataPart(it.key, it.value.toString())
        }
        val request = UploadRequestBody(
            requestBodyBuilder.build(),
            upload.channel
        )
        return@async try {
            val okResponse = NetworkFactory.client.newCall(
                Request.Builder()
                    .url(upload.url)
                    .headers(Headers.of(upload.header))
                    .post(request)
                    .build()
            ).execute()
            Response(
                if (upload.respEncode) {
                    Gson().fromJson<T>(
                        okResponse.body()?.string()?.aesDecode(NetworkFactory.aesPassword) ?: "{}", T::class.java
                    )
                } else {
                    Gson().fromJson<T>(okResponse.body()?.string() ?: "{}", T::class.java)
                },
                okResponse.message(),
                okResponse.isSuccessful,
                okResponse.code()
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Response<T>(null, e.message, false, 404)
        }
    }
}

fun download(download: Download) = async {
    var startIndex = 0L
    if (download.url.startsWith("/", false)) {
        download.url = UrlManager.getApiUrl() + download.url
    }
    var okResponse =
        getPointResponse(download.method, download.body, download.header, download.url, startIndex)
    val file =
        createDownloadFile(download, okResponse, false)
    return@async if (file.exists() && file.length() == okResponse.body()?.contentLength()) {
        download.channel.send("100")
        download.channel.close()
        Response(
            file.absolutePath,
            null,
            true,
            okResponse.code()
        )
    } else {
        val cacheFile = createDownloadFile(download, okResponse, true)
        if (cacheFile.exists()) {
            startIndex = cacheFile.length()
        }
        okResponse = if (okResponse.code() == 206) {
            getPointResponse(
                download.method,
                download.body,
                download.header,
                download.url,
                startIndex,
                false
            )
        } else {
            getPointResponse(
                download.method,
                download.body,
                download.header,
                download.url,
                startIndex
            )
        }
        val responseBody = okResponse.body()
        var total = responseBody?.contentLength()
        if (responseBody == null || total == null || total == 0L) {
            Response<String>(null, "文件错误！", false, 404)
        } else {
            try {
                total += startIndex
                val stream = responseBody.byteStream()
                val fos = FileOutputStream(cacheFile)
                var current: Long = startIndex
                val buf = ByteArray(2048)
                var len = stream.read(buf)
                while (len != -1) {
                    current += len
                    fos.write(buf, 0, len)
                    download.channel.send(((current.toDouble() / total.toDouble()) * 100).toInt().toString())
                    len = stream.read(buf)
                }
                download.channel.close()
                fos.flush()
                cacheFile.renameTo(file.absoluteFile)
                Response(
                    file.absolutePath,
                    null,
                    true,
                    okResponse.code()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Response(null, e.message, false, 404)
            }
        }
    }
}

private fun createDownloadFile(
    download: Download,
    response: okhttp3.Response,
    isCache: Boolean
): File {
    if (download.savePath == null || download.savePath == "") {
        download.savePath =
                "${Environment.getExternalStorageDirectory().absolutePath}/${download.context.packageName}/files/"
    }
    val fileName = if (download.fileName == null || download.fileName == "") {
        val name = getHeaderFileName(response)
        if (name != "") {
            name
        } else {
            Random(5).toString()
        }
    } else {
        download.fileName
    }
    return if (download.savePath!!.endsWith("/")) {
        if (isCache) {
            File("${download.savePath}$fileName.cache")
        } else {
            File("${download.savePath}$fileName")
        }
    } else {
        if (isCache) {
            File("${download.savePath}/$fileName.cache")
        } else {
            File("${download.savePath}/$fileName")
        }
    }
}

private fun getPointResponse(
    method: Method,
    body: HashMap<String, Any?>,
    header: HashMap<String, String>,
    url: String,
    startIndex: Long,
    isTest: Boolean = true
) = if (method == Method.GET) {
    var requireUrl = url
    if (body.size != 0) {
        requireUrl += "?"
    }
    body.forEach {
        requireUrl += if (url.endsWith("?", false)) {
            "${it.key}=${it.value}"
        } else {
            "&${it.key}=${it.value}"
        }
    }
    val requestBuilder =
        Request.Builder()
            .url(url)
            .headers(Headers.of(header))
    if (isTest) {
        requestBuilder.header("Range", "bytes=$startIndex-")
    }
    NetworkFactory.client.newCall(requestBuilder.build()).execute()
} else {
    val requestBuilder =
        Request.Builder().header("Range", "bytes=$startIndex-")
            .url(url)
            .headers(Headers.of(header))
            .post(RequestBody.create(MediaType.parse(NetworkFactory.contentType), body.toJsonStr()))
    if (isTest) {
        requestBuilder.header("Range", "bytes=$startIndex-")
    }
    NetworkFactory.client.newCall(requestBuilder.build()).execute()
}

private fun getHeaderFileName(response: okhttp3.Response): String {
    var dispositionHeader = response.header("Content-Disposition")
    if (!TextUtils.isEmpty(dispositionHeader)) {
        dispositionHeader?.replace("attachment;filename=", "")
        dispositionHeader?.replace("filename*=utf-8", "")
        val strings =
            dispositionHeader?.split("; ".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        if (strings != null && strings.isNotEmpty()) {
            dispositionHeader = strings[1].replace("filename=", "")
            dispositionHeader = dispositionHeader.replace("\"", "")
            return dispositionHeader
        }
        return ""
    }
    return ""
}
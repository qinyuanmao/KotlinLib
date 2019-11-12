package com.wisesoft.android.kotlinlib.utils

import com.blankj.utilcode.util.ToastUtils
import com.wisesoft.android.kotlinlib.network.http.*
import com.wisesoft.android.kotlinlib.ui.BaseActivity
import kotlinx.coroutines.*

/**
 * @Title:  TaskManager
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:    kotlin协程异步任务
 * @date Create on 2018/10/30 12:12.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

@ObsoleteCoroutinesApi
fun launch(contextName: String = "LocalContext", block: suspend CoroutineScope.() -> Unit) =
    GlobalScope.launch(newSingleThreadContext(contextName)) {
        block()
    }

@ObsoleteCoroutinesApi
fun delayTask(
    delayTime: Long,
    contextName: String = "LocalContext",
    delayBlock: suspend CoroutineScope.() -> Unit
) = launch(contextName) {
        while (isActive) {
            delayBlock()
            delay(delayTime)
        }
    }

@ObsoleteCoroutinesApi
fun repeatTask(count: Int, delayTime: Long, delayAction: suspend CoroutineScope.(count: Int) -> Unit) = launch {
        repeat(count) {
            delayAction(count)
        }
    }

@ObsoleteCoroutinesApi
fun <T> async(
    contextName: String = "LocalContext",
    block: suspend CoroutineScope.() -> T
): Deferred<T>? =
    GlobalScope.async(newSingleThreadContext(contextName)) { block() }

@ObsoleteCoroutinesApi
inline fun <reified T> networkTask(
    init: NetworkAction<T>.() -> Unit
): Job {
    val networkAction = NetworkAction<T>()
    networkAction.init()
    return launch {
        val data = doHttp<T>(networkAction.param)?.await()
        data?.let { resp ->
            if (resp.isSuccess) {
                resp.data?.let { networkAction.success(it) }
            } else {
                networkAction.failed(resp.error)
            }
        }
    }
}


@ObsoleteCoroutinesApi
inline fun <reified T> uploadTask(init: UploadAction<T>.() -> Unit): Job {
    val uploadAction = UploadAction<T>()
    uploadAction.init()
    val job = launch {
        val data = upload<T>(uploadAction.param)
        uploadAction.onProcess?.let { onProcess ->
            for (process in uploadAction.param.channel) {
                process?.let {
                    if (it.length > 3) {
                        uploadAction.failed(it)
                    } else {
                        onProcess(it)
                    }
                }
            }
        }
        data?.await()?.let { resp ->
            if (resp.isSuccess) {
                resp.data?.let { uploadAction.success(it) }
            } else {
                uploadAction.failed(resp.error)
            }
        }
    }
    return job
}

@ObsoleteCoroutinesApi
fun downloadTask(
    init: DownloadAction.() -> Unit
): Job {
    val downloadAction = DownloadAction()
    downloadAction.init()
    val job = launch {
        val data = download(downloadAction.param)
        downloadAction.onProcess?.let { onProcess ->
            for (process in downloadAction.param.channel) {
                process?.let { onProcess(it) }
            }
        }
        data?.await()?.let { resp ->
            if (resp.isSuccess) {
                downloadAction.success(resp.data)
            } else {
                downloadAction.failed(resp.error)
            }
        }
    }
    return job
}

class TaskAction<T> {
    lateinit var completed: (T?) -> Unit
    var action: Deferred<T?>? = null
    var failed: ((String?) -> Unit)? = {
        ToastUtils.showShort(it)
    }
}
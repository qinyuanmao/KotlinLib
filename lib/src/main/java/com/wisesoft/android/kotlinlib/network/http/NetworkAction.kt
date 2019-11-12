package com.wisesoft.android.kotlinlib.network.http

import com.blankj.utilcode.util.ToastUtils

/**
 * @Title:  NetworkAction
 * @Package com.wisesoft.android.kotlinlib.api
 * @Description:  网络请求action构造器
 * @date Create on 2018/11/6 18:13.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class NetworkAction<T> {
    lateinit var success: (T) -> Unit
    lateinit var param: Require
    var failed : ((String?) -> Unit) = {
        ToastUtils.showShort(it)
    }
}

class DownloadAction {
    lateinit var param: Download
    var onProcess: ((String) -> Unit)? = null
    lateinit var success: (String?) -> Unit
    var failed: (String?) -> Unit = {
        ToastUtils.showShort(it)
    }
}

class UploadAction<T> {
    lateinit var param: Upload
    var onProcess: ((String) -> Unit)? = null
    lateinit var success: (T) -> Unit
    var failed: (String?) -> Unit = {
        ToastUtils.showShort(it)
    }
}
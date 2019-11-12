package com.wisesoft.android.kotlinlib.update

import android.content.Context
import com.wisesoft.android.kotlinlib.network.http.Method
import com.wisesoft.android.kotlinlib.network.http.createDownload
import com.wisesoft.android.kotlinlib.network.http.createRequire
import com.wisesoft.android.kotlinlib.utils.getLocalVersionCode
import com.wisesoft.android.kotlinlib.utils.getLocalVersionName

/**
 * @Title:  App更新请求参数
 * @Package com.wisesoft.android.kotlinlib.update
 * @Description:   App更新请求参数
 * @date Create on 2018/11/6 16:19.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun checkUpdate(context: Context) = createRequire {
    method = Method.POST
    url = "https://app.wisesoft.net.cn/api/v1/app/checkUpdate"
    params {
        "bundle".."wisesoft.com.chongqingjd"
        "appBuild"..context.getLocalVersionCode()
        "appVersion"..context.getLocalVersionName()
        "system".."ANDROID"
    }
}

fun getFileId(id: String) = createRequire {
    method = Method.POST
    url = "https://app.qinyuanmao.cn/api/v1/file/getFileById"
    params {
        "id"..id
    }
}

fun downloadFile(
    context: Context,
    url: String,
    savePath: String? = null,
    fileName: String? = null
) = createDownload {
    this.url = url
    this.savePath = savePath
    this.fileName = fileName
    this.context = context
}
package com.wisesoft.android.kotlinlib.test

import android.content.Context
import android.os.Environment
import com.wisesoft.android.kotlinlib.network.http.createUpload
import java.io.Serializable

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/11/7 17:22.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
data class UploadFileBean(
    val code: Int,
    val msg: String,
    val result: Result
) : Serializable {
    data class Result(
        val createAt: String,
        val fileId: String,
        val fileName: String,
        val fileUrl: String,
        val md5: String
    ) : Serializable
}

fun uploadFile(context: Context) = createUpload {
    this.context = context
    fileKey = "upload"
    filePath =
            "${Environment.getExternalStorageDirectory().absolutePath}/${context.packageName}/${context.packageName}.apk"
    url = "http://192.168.1.253:8000/file/uploadFile"
    params {
        "md5".."12345"
    }
    headers {
        "token".."43d6a5b6-fa74-4962-a4b4-18881d2a7936"
    }
}
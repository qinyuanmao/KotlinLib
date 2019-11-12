package com.wisesoft.android.kotlinlib.network.update

import java.io.Serializable

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/10/4 20:19.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

data class FileBean(
    val code: Int,
    val msg: String,
    val result: Result
): Serializable {
    data class Result(
        val fileId: String,
        val md5: String,
        val fileUrl: String,
        val fileName: String,
        val createAt: String
    ): Serializable
}

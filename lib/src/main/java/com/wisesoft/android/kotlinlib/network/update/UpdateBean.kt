package com.wisesoft.android.kotlinlib.update

import java.io.Serializable

/**
 * @Title:  UpdateBean
 * @Package com.wisesoft.android.kotlinlib.update
 * @Description:    升级bean
 * @date Create on 2018/10/4 20:16.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

data class UpdateBean(
    val code: Int,
    val msg: String,
    val result: Result
): Serializable {
    data class Result(
        val id: String,
        val appId: String,
        val title: String,
        val version: String,
        val build: Int,
        val isForce: Int,
        val createAt: String,
        val fileId: String,
        val isRelease: Int,
        val detail: String,
        val fileUrl: String
    ): Serializable
}


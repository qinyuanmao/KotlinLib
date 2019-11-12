package com.wisesoft.android.kotlinlib.service

import android.os.Binder

/**
 * @Title: NormalBinder
 * @Package com.wisesoft.android.kotlinlib.service
 * @Description:  创建一个通用的Binder
 * @date Create on 2018/11/13 15:36.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

class NormalBinder(private val mService: NormalService): Binder() {
    fun getService() = mService
}
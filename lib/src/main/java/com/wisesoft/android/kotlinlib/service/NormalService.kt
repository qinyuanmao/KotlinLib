package com.wisesoft.android.kotlinlib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.wisesoft.android.kotlinlib.utils.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @Title: NormalService
 * @Package com.wisesoft.android.kotlinlib.service
 * @Description: 创建一个通用的Service
 * @date Create on 2018/11/13 15:18.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class NormalService: Service() {
    private val mBinder: IBinder = NormalBinder(this)
    override fun onBind(p0: Intent?): IBinder = mBinder
    fun doWork(action: () -> Unit) {
        launch { action() }
    }
}
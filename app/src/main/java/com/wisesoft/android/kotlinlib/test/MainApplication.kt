package com.wisesoft.android.kotlinlib.test

import android.app.Application
import android.os.Environment
import android.support.multidex.MultiDexApplication
import com.wisesoft.android.kotlinlib.KotlinLib
import com.wisesoft.android.kotlinlib.db.DemoModel
import com.wisesoft.android.kotlinlib.db.WisesoftDBHelper

/**
 * @author Create by qinyuanmao
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date Create on 2018/9/27 09:46.
 * @email qinyuanmao.live@gmail.com
 */
class MainApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        KotlinLib.init(this)
        val arrayList = ArrayList<Class<*>>()
        arrayList.add(DemoModel::class.java)
        WisesoftDBHelper.initDBHelper(this, "${Environment.getExternalStorageDirectory().absolutePath}/${this.packageName}/${this.packageName}.db", arrayList)
    }
}

package com.wisesoft.android.kotlinlib.utils

import android.app.Activity
import android.content.Context
import com.wisesoft.android.kotlinlib.ui.BaseActivity
import java.util.LinkedList

/**
 * @author Create by qinyuanmao
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @date Create on 2017/5/11 17:23.
 * @email qinyuanmao.live@gmail.com
 */

object ActivityContainer {

    private val activityStack = LinkedList<Activity>()

    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    fun finishAllActivity() {
        activityStack.forEach{
            it.finish()
        }
        activityStack.clear()
    }

    fun getTopActivity() : Activity {
        return activityStack.last
    }

    inline fun <reified T: Activity> finishAllAndStart(context: Context) {
        finishAllActivity()
        context.startActivity<T>()
    }
}
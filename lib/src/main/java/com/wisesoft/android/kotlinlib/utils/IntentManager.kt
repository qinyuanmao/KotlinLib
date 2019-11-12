@file:Suppress("NOTHING_TO_INLINE")

package com.wisesoft.android.kotlinlib.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/10/3 21:40.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
object IntentManager {
    inline fun <reified T> createIntent(context: Context, args: Array<out Pair<String, Any?>?>? = null): Intent {
        val intent = Intent(context, T::class.java)
        val bundle = Bundle()
        args?.forEach { pair ->
            pair?.first?.let {
                bundle.p(it, pair?.second)
            }
        }
        intent.putExtras(bundle)
        return intent
    }
}
inline fun Intent.clearTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) }
inline fun Intent.clearTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
inline fun Intent.excludeFromRecents(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) }
inline fun Intent.multipleTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK) }
inline fun Intent.newTask(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
inline fun Intent.noAnimation(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) }
inline fun Intent.noHistory(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY) }
inline fun Intent.singleTop(): Intent = apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
inline fun Intent.newDocument(): Intent = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    } else {
        @Suppress("DEPRECATION")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
}
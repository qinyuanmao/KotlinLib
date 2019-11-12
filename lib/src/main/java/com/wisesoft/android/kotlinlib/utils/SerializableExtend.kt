package com.wisesoft.android.kotlinlib.utils

import com.google.gson.Gson
import java.io.Serializable

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/30 20:46.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun Serializable.toMap() : HashMap<String, Any> {

    val map = HashMap<String, Any>()

    val declaredFields = this.javaClass.declaredFields
    for (field in declaredFields) {
        field.isAccessible = true
        map[field.name] = field.get(this)
    }

    return map
}

fun Any.toJsonStr() : String {
    return Gson().toJson(this)
}
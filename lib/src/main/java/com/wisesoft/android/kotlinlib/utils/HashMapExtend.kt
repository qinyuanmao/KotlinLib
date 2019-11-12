package com.wisesoft.android.kotlinlib.utils

import java.io.Serializable
import java.lang.reflect.Modifier

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/30 20:56.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun <T : Serializable> HashMap<String, Any>.toObject(beanClass: Class<T>) : T {
    val obj = beanClass.newInstance()
    val fields = obj.javaClass.declaredFields
    for (field in fields) {
        val mod = field.modifiers
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
            continue
        }
        field.isAccessible = true
        field.set(obj, this[field.name])
    }
    return obj
}

fun HashMap<String, Any>.toStr() : String {
    var str = ""
    forEach{
        str += "${it.key}:${it.value},"
    }
    return str.substring(0, str.length - 1)
}

fun HashMap<String, Any?>.toJsonStr() : String {
    var str = "{"
    forEach{
        str += if (it.value is String) {
            "\"${it.key}\":\"${it.value}\","
        } else {
            "\"${it.key}\":${it.value},"
        }
    }
    return "${str.substring(0, str.length - 1)}}"
}

fun Map<String, Any?>.toHashMap(): HashMap<String, Any?> {
    val map = HashMap<String, Any?>()
    for (key in keys) {
        map[key] = this[key]
    }
    return map
}
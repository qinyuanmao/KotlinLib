package com.wisesoft.android.kotlinlib.utils

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2019/4/13 20:17.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

fun Any.currBasicType() =
    when (this) {
        is Int -> Int::class.javaPrimitiveType
        is Long -> Long::class.javaPrimitiveType
        is Float -> Float::class.javaPrimitiveType
        is Double -> Double::class.javaPrimitiveType
        is Boolean -> Boolean::class.javaPrimitiveType
        is Byte -> Boolean::class.javaPrimitiveType
        is Short -> Short::class.javaPrimitiveType
        is Char -> Char::class.javaPrimitiveType
        else -> this::class.java
    }
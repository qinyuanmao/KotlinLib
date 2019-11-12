package com.wisesoft.android.kotlinlib.db

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/10/17 02:01.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

object start

infix fun String.was(start: start): String = this
infix fun String.with(str: String): Boolean = this.startsWith(str, false)

val str = "Kotlin" was start with "kot"

infix fun String.value(query: Any): String {
    return if (query is String) {
        this.replaceFirst("?", "'$query'")
    } else {
        this.replaceFirst("?", query.toString())
    }
}

infix fun String.values(query: Array<Any>): String {
    query.forEach {
        if (it is String) {
            this.replaceFirst("?", "'$query'")
        } else {
            this.replaceFirst("?", query.toString())
        }
    }
    return this
}
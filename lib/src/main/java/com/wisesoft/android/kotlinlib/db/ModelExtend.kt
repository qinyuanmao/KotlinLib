@file:Suppress("UNCHECKED_CAST")

package com.wisesoft.android.kotlinlib.db

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * @Title:  ModelExtend
 * @Package com.wisesoft.android.kotlinlib.db
 * @Description:    数据库model的增删改
 * @date Create on 2018/9/26 17:15.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun <T : DBModel> T.insert() {
    var sqlStr = "insert into ${getExpress().getTableName()} (?) values(?)"
    for (map in getValueMap()) {
        val key = map.key
        val value = map.value
        value?.let {
            sqlStr = sqlStr.replaceFirst("?", "[$key]")
            if (value is String) {
                sqlStr = sqlStr.replace("?", "'$value'")
                sqlStr = sqlStr.replaceFirst("'$value'", "'$value', ?")
            } else {
                sqlStr = sqlStr.replace("?", "$value")
                sqlStr = sqlStr.replaceFirst("$value", "$value, ?")
            }
            sqlStr = sqlStr.replaceFirst("[$key]", "[$key], ?")
        }
    }
    sqlStr = sqlStr.replace(", ?", "", false)
    WisesoftDBHelper.INSTANCE.doUpdate(sqlStr)
}

fun <T : DBModel> T.update() {
    var sqlStr = "update ${getExpress().getTableName()} set "
    val valueMap = getValueMap()
    for (map in valueMap) {
        map.value?.let {
            sqlStr += if (it is String) {
                "[${map.key}]='$it',"
            } else {
                "[${map.key}]=$it,"
            }
        }
    }
    sqlStr = "${sqlStr.substring(0, sqlStr.length - 1)} where "
    for (map in getExpress().getPKMap()) {
        sqlStr += if (valueMap[map.key] is String) {
            "[${map.key}]='${valueMap[map.key]}',"
        } else {
            "[${map.key}]=${valueMap[map.key]},"
        }
    }
    sqlStr = sqlStr.substring(0, sqlStr.length - 1)
    WisesoftDBHelper.INSTANCE.doUpdate(sqlStr)
}

fun <T : DBModel> T.delete() {
    var sqlStr = "delete from ${getExpress().getTableName()} where "
    val valueMap = getValueMap()
    sqlStr = sqlStr.substring(0, sqlStr.length - 1)
    for (map in getExpress().getPKMap()) {
        sqlStr += if (valueMap[map.key] is String) {
            "[${map.key}]='${valueMap[map.key]}',"
        } else {
            "[${map.key}]=${valueMap[map.key]},"
        }
    }
    sqlStr = sqlStr.substring(0, sqlStr.length - 1)
    WisesoftDBHelper.INSTANCE.doUpdate(sqlStr)
}

private fun <T : DBModel> T.getValueMap(): Map<String, Any?> {
    val map = HashMap<String, Any?>()
    this::class.memberProperties.forEach {
        map[it.name] = it.getUnsafed(this@getValueMap)
    }
    return map
}

fun <T, R> KProperty1<T, R>.getUnsafed(receiver: Any): R {
    return get(receiver as T)
}
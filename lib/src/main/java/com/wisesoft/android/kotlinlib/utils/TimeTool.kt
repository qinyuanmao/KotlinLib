package com.wisesoft.android.kotlinlib.utils

import android.annotation.SuppressLint
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat


/**
 * 时间戳转字符串（yyyy-MM-dd HH:mm:ss）
 */
fun time2Second(time: Long): String {
    return time2String(time)
}

/**
 * 时间戳转字符串（yyyy-MM-dd）
 */
@SuppressLint("SimpleDateFormat")
fun time2Day(time: Long): String {
    return time2String(time, SimpleDateFormat("yyyy-MM-dd"))
}

/**
 * 时间戳转字符串（yyyy-MM-dd）
 */
@SuppressLint("SimpleDateFormat")
fun time2DayCH(time: Long): String {
    return time2String(time, SimpleDateFormat("yyyy年MM月dd日"))
}


/**
 * 时间戳转字符串（yyyy-MM-dd HH-mm）
 */
@SuppressLint("SimpleDateFormat")
fun time2Minute(time: Long): String {
    return time2String(time, SimpleDateFormat("yyyy-MM-dd HH:mm"))
}

/**
 * 时间戳转字符串(自定义format格式)
 */
@SuppressLint("SimpleDateFormat")
fun time2Any(time: Long, format: SimpleDateFormat): String {
    return time2String(time, format)
}


/**
 * 统一调用blank的方法转换（时间戳转字符串）
 */
private fun time2String(time: Long, @SuppressLint("SimpleDateFormat") format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")): String {
    return TimeUtils.millis2String(time, format)
}
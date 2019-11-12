package com.wisesoft.android.kotlinlib.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Patterns
import android.webkit.URLUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayInputStream
import java.io.File
import java.util.regex.Pattern


/**
 * @Title: StringExtend
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:  String扩展函数
 * @date Create on 2018/6/30 09:06.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun String.filterSpecChar(): String {
    val regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return m.replaceAll("").trim()
}

fun String.toInputStream() = ByteArrayInputStream(toByteArray())

fun String.base64ToBitmap(): Bitmap {
    val option = BitmapFactory.Options()
    option.inPreferredConfig = Bitmap.Config.RGB_565
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, option)
}

fun String.createPath(): Boolean {
    return if (this.isEmpty()) {
        false
    } else {
        return try {
            val file = File(this)
            if (!file.exists()) {
                file.mkdir()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

fun String.isUri(): Boolean {
    return try {
        Uri.fromFile(File(this))
        true
    } catch (e: Exception) {
        false
    }
}

fun String.isUrl(): Boolean {
    return Patterns.WEB_URL.matcher(this).matches() || URLUtil.isValidUrl(this)
}

infix fun String.has(str: String): Boolean {
    return this.indexOf(str) != -1
}

inline fun <reified T> String.toJsonObject(): T? {
    return try {
        GsonBuilder().serializeNulls().create().fromJson(this, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * bean转成json 字符串
 *
 * @param object
 * @return
 */
fun gsonString(obj: Any): String {
    return Gson().toJson(obj)
}

///**
// * 转成bean
// *
// * @param gsonString
// * @param cls
// * @return
// */
//fun <T> gsonToBean(gsonString: String, cls: Class<T>): T? {
//    var t: T? = null
//    t = GsonBuilder().serializeNulls().create().fromJson(gsonString, cls)
//    return t
//}

inline fun <reified T> String.toJsonArray(): List<T>? {
    return try {
        GsonBuilder().serializeNulls()
            .create()
            .fromJson<List<T>>(this, object : TypeToken<List<T>>() {}.type)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
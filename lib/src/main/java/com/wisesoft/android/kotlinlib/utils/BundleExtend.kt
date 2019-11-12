package com.wisesoft.android.kotlinlib.utils

import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/9/30 13:57.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun Bundle.p(key: String, value: Any?): Bundle {
    when (value) {
        null -> putSerializable(key, value as Serializable?)
        is Int -> putInt(key, value)
        is String -> putString(key, value)
        is Serializable -> putSerializable(key, value)
        is Double -> putDouble(key, value)
        is Float -> putFloat(key, value)
        is Short -> putShort(key, value)
        is Bundle -> putBundle(key, value)
        is Byte -> putByte(key, value)
        is Boolean -> putBoolean(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Parcelable -> putParcelable(key, value)
        is Size -> putSize(key, value)
        is SizeF -> putSizeF(key, value)
        is Array<*> -> {
            when {
                value.isArrayOf<Parcelable>() -> putParcelableArray(
                    key,
                    value as Array<out Parcelable>
                )
                value.isArrayOf<CharSequence>() -> putCharSequenceArray(
                    key,
                    value as Array<out CharSequence>
                )
                value.isArrayOf<String>() -> putStringArray(key, value as Array<out String>)
                else -> throw Exception("Unsupported bundle component (${value.javaClass})")
            }
        }
        is ArrayList<*> -> {
            if (value.size > 0) {
                when (value.first()) {
                    is Int -> putIntegerArrayList(key, value as ArrayList<Int>)
                    is String -> putStringArrayList(key, value as ArrayList<String>)
                    is Parcelable -> putParcelableArrayList(key, value as ArrayList<out Parcelable>)
                    is CharSequence -> putCharSequenceArrayList(
                        key,
                        value as ArrayList<CharSequence>
                    )
                    else -> throw Exception("Unsupported bundle component (${value.javaClass})")
                }
            } else {
                throw Exception("Unsupported bundle component (${value.javaClass})")
            }
        }
        else -> throw Exception("Unsupported bundle component (${value.javaClass})")
    }
    return this
}

fun bundleOf(vararg pair: Pair<String, Any?>): Bundle {
    val bundle = Bundle()
    pair.forEach {
        val key = it.first
        val value = it.second
        when (value) {
            null -> bundle.putSerializable(key, value as Serializable?)
            is Int -> bundle.putInt(key, value)
            is String -> bundle.putString(key, value)
            is Serializable -> bundle.putSerializable(key, value)
            is Double -> bundle.putDouble(key, value)
            is Float -> bundle.putFloat(key, value)
            is Short -> bundle.putShort(key, value)
            is Bundle -> bundle.putBundle(key, value)
            is Byte -> bundle.putByte(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            is ByteArray -> bundle.putByteArray(key, value)
            is Char -> bundle.putChar(key, value)
            is CharArray -> bundle.putCharArray(key, value)
            is CharSequence -> bundle.putCharSequence(key, value)
            is Parcelable -> bundle.putParcelable(key, value)
            is Size -> bundle.putSize(key, value)
            is SizeF -> bundle.putSizeF(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<Parcelable>() -> bundle.putParcelableArray(
                        key,
                        value as Array<out Parcelable>
                    )
                    value.isArrayOf<CharSequence>() -> bundle.putCharSequenceArray(
                        key,
                        value as Array<out CharSequence>
                    )
                    value.isArrayOf<String>() -> bundle.putStringArray(key, value as Array<out String>)
                    else -> throw Exception("Unsupported bundle component (${value.javaClass})")
                }
            }
            is ArrayList<*> -> {
                if (value.size > 0) {
                    when (value.first()) {
                        is Int -> bundle.putIntegerArrayList(key, value as ArrayList<Int>)
                        is String -> bundle.putStringArrayList(key, value as ArrayList<String>)
                        is Parcelable -> bundle.putParcelableArrayList(key, value as ArrayList<out Parcelable>)
                        is CharSequence -> bundle.putCharSequenceArrayList(
                            key,
                            value as ArrayList<CharSequence>
                        )
                        else -> throw Exception("Unsupported bundle component (${value.javaClass})")
                    }
                } else {
                    throw Exception("Unsupported bundle component (${value.javaClass})")
                }
            }
            else -> throw Exception("Unsupported bundle component (${value.javaClass})")
        }
    }
    return bundle
}
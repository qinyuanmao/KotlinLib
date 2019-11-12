package com.wisesoft.android.kotlinlib.network.http

import android.annotation.SuppressLint
import com.wisesoft.android.kotlinlib.network.NetworkFactory
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

/**
 * @Title: Encode
 * @Package com.wisesoft.android.kotlinlib.network.http
 * @Description:  请求参数加密解密
 * @date Create on 2018/11/12 16:33.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

@SuppressLint("GetInstance")
fun String.aesEncode(password: String? = NetworkFactory.aesPassword): String? {
    try {
        val kGen = KeyGenerator.getInstance("AES")
        kGen.init(128, SecureRandom(password?.toByteArray()))
        val secretKey = kGen.generateKey()
        val enCodeFormat = secretKey.encoded
        val key = SecretKeySpec(enCodeFormat, "AES")
        val cipher = Cipher.getInstance("AES")
        val byteContent = this.toByteArray(Charset.defaultCharset())
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val result = cipher.doFinal(byteContent)
        return String(result)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

@SuppressLint("GetInstance")
fun String.aesDecode(password: String? = NetworkFactory.aesPassword): String? {
    try {
        val kGen = KeyGenerator.getInstance("AES")
        kGen.init(128, SecureRandom(password?.toByteArray()))
        val secretKey = kGen.generateKey()
        val enCodeFormat = secretKey.encoded
        val key = SecretKeySpec(enCodeFormat, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(this.toByteArray(Charsets.UTF_8)))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
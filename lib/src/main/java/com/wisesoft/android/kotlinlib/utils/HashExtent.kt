package com.wisesoft.android.kotlinlib.utils

import com.blankj.utilcode.util.EncryptUtils

/**
 * Description: 哈希，加密相关
 *  on 2019/6/5.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
fun String.md5() = EncryptUtils.encryptMD5ToString(this)

fun String.sha1() = EncryptUtils.encryptSHA1ToString(this)

fun String.sha256() = EncryptUtils.encryptSHA256ToString(this)

fun String.sha512() = EncryptUtils.encryptSHA512ToString(this)

/**
 * 随机数增强的md5算法
 * @param salt 加盐的值
 */
fun String.md5Hmac(salt: String) = EncryptUtils.encryptHmacMD5ToString(this, salt)

fun String.sha1Hmac(salt: String) = EncryptUtils.encryptHmacSHA1ToString(this, salt)

fun String.sha256Hmac(salt: String) = EncryptUtils.encryptHmacSHA256ToString(this, salt)

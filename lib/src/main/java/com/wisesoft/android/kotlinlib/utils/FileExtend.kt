package com.wisesoft.android.kotlinlib.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Base64
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/12/27 16:12.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun String.getFileMD5(): String? {
    return try {
        MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
        println("Exception while encrypting to md5")
        e.printStackTrace()
        null
    }.let {
        it?.update(this@getFileMD5.toByteArray(), 0, this@getFileMD5.length)
        BigInteger(1, it?.digest()).toString(16).apply {
            var md5 = this
            while (md5.length < 32) md5 = "0$md5"
            md5
        }
    }
}


/**
 * encodeBase64File:(将文件转成base64 字符串). <br></br>
 *
 * @param path 文件路径
 * @return
 * @throws Exception
 * @author guhaizhou@126.com
 * @since JDK 1.6
 */
@Throws(Exception::class)
fun encodeBase64File(path: String): String {
    val file = File(path)
    val inputFile = FileInputStream(file)
    val buffer = ByteArray(file.length().toInt())
    inputFile.read(buffer)
    inputFile.close()
    return Base64.encodeToString(buffer, Base64.DEFAULT)
}


/**
 * decoderBase64File:(将base64字符解码保存文件). <br></br>
 *
 * @param base64Code 编码后的字串
 * @param savePath   文件保存路径
 * @throws Exception
 * @author guhaizhou@126.com
 * @since JDK 1.6
 */
@Throws(Exception::class)
fun decoderBase64File(base64Code: String, savePath: String) {
    // byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
    val buffer = Base64.decode(base64Code, Base64.DEFAULT)
    val out = FileOutputStream(savePath)
    out.write(buffer)
    out.close()
}


/**
 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
 *
 * @param imagePath 图像的路径
 * @return 生成base64编码
 */
fun encodeBase64ImageThumbnail(imagePath: String): String {
    return encodeBase64ImageThumbnail(imagePath, 100, 120)
}

/**
 *
 * @param imagePath 图像的路径
 * @param width     指定输出图像的宽度
 * @param height    指定输出图像的高度
 * @return 生成base64编码
 */
fun encodeBase64ImageThumbnail(imagePath: String, width: Int,
                               height: Int): String {
    var bitmap: Bitmap? = null
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    // 获取这个图片的宽和高，注意此处的bitmap为null
    bitmap = BitmapFactory.decodeFile(imagePath, options)
    options.inJustDecodeBounds = false // 设为 false
    // 计算缩放比
    val h = options.outHeight
    val w = options.outWidth
    val beWidth = w / width
    val beHeight = h / height
    var be = 1
    if (beWidth < beHeight) {
        be = beWidth
    } else {
        be = beHeight
    }
    if (be <= 0) {
        be = 1
    }
    options.inSampleSize = be
    // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
    bitmap = BitmapFactory.decodeFile(imagePath, options)
    // 利用、来创建缩略图，这里要指定要缩放哪个Bitmap对象
    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
            ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
    return bitmapToBase64(bitmap)
}

/**
 * bitmap转为base64
 *
 * @param bitmap
 * @return
 */
fun bitmapToBase64(bitmap: Bitmap?): String {
    var result: String = ""
    var baos: ByteArrayOutputStream? = null
    try {
        if (bitmap != null) {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            baos.flush()
            baos.close()
            val bitmapBytes = baos.toByteArray()
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            if (baos != null) {
                baos.flush()
                baos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}

/*
*bitmap转base64
 */
fun base64ToBitmap(base64String: String): Bitmap {
    val bytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

/**
 * 打开系统文件管理器
 */
fun openFileManager(activity: Activity, requestCode: Int) {
    val intent = Intent("android.intent.action.GET_CONTENT")
    intent.type = "*/*"
    intent.addCategory("android.intent.category.OPENABLE")
    activity.startActivityForResult(intent, requestCode)
}

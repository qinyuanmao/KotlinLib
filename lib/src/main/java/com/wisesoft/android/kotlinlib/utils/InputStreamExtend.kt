package com.wisesoft.android.kotlinlib.utils

import android.graphics.BitmapFactory
import java.io.*
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/30 09:11.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
fun InputStream.toStr() =
    BufferedReader(InputStreamReader(this)).lines().collect(Collectors.joining(System.lineSeparator()))

fun InputStream?.toBitmap() = BitmapFactory.decodeStream(this)

fun InputStream.writeFile(filePath: String, downAction: (Int) -> Unit) {
    val file = File(filePath)
    if (file.exists()) {
        file.delete()
    }
    return try {
        val fileOutputStream = FileOutputStream(file)
        val bytes = ByteArray(1024)
        var length = read(bytes)
        while (length != -1) {
            fileOutputStream.write(bytes, 0, length)
            length = read(bytes)
        }
        this.close()
        fileOutputStream.close()
        downAction(KeyString.SAVE_FILE_SUCCESS)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        downAction(KeyString.FILE_NOT_FOUNT)
    } catch (e: IOException) {
        e.printStackTrace()
        downAction(KeyString.FILE_SAVE_IO_EXCEPTION)
    }
}
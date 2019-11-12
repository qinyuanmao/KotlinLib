package com.wisesoft.android.kotlinlib.crash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import com.wisesoft.android.kotlinlib.network.http.createUpload
import com.wisesoft.android.kotlinlib.ui.checkPermission
import com.wisesoft.android.kotlinlib.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Title:  CrashManager
 * @Package com.wisesoft.android.kotlinlib.utils
 * @Description:  收集并记录错误日志
 * @date Create on 2019/1/25 11:10.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

@Suppress("UNREACHABLE_CODE")
class CrashManager(private val mContext: Context) : Exception(), UncaughtExceptionHandler {

    private val mCrashLogPath =
            "${mContext.cacheDir.absolutePath}/crash/"
    // 用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    /**
     * 系统默认的UncaughtException处理类
     */
    private val mDefaultHandler: UncaughtExceptionHandler =
            Thread.getDefaultUncaughtExceptionHandler()

    // 用于格式化日期,作为日志文件名的一部分
    @SuppressLint("SimpleDateFormat")
    private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex)) {
            mDefaultHandler.uncaughtException(thread, ex)
        }
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
        val pathDir = File(mCrashLogPath)
        if (pathDir.exists() && //文件夹是否存在
                pathDir.list() != null &&
                pathDir.list().isNotEmpty() && //文件夹是否有文件
                checkPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) //检查权限
        ) {
            launch {
                val logFiles = pathDir.listFiles().filter { it.name.endsWith(".log") } //取出所有log文件
                repeat(logFiles.size) {
                    uploadTask<NormalBean> {
                        param = uploadFile(mContext, logFiles[it].absolutePath)
                        success = { bean ->
                            if (bean.code == 0) {
                                File(pathDir.list()[it]).delete()
                                Log.e(TAG, bean.toString())
                            } else {
                                Log.e(TAG, bean.msg)
                            }
                        }
                        failed = {
                            Log.e(TAG, it)
                        }
                    }
                }
            }
        }
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private fun handleException(ex: Throwable?): Boolean {
        return try {
            if (ex == null) {
                true
            } else {
                mContext.ui { mContext.toast("对不起，程序出现未知异常，请重新进入") }
                if (checkPermission(mContext, Manifest.permission.READ_PHONE_STATE)) {
                    saveCrashInfo2File(ex)
                    val crashReport = getCrashReport(ex)
                    Log.e("AppException", crashReport)
                    Thread.sleep(3000)
                    ActivityContainer.finishAllActivity()
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(1)
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private fun getCrashReport(ex: Throwable): String = listOf(
            "Android:${mContext.getSystemVersion()}",
            "Version:${android.os.Build.MODEL}",
            "Exception:${ex.message}",
            ex.stackTrace.indices.joinToString("\n")
    ).joinToString { "\n" }

    private fun saveCrashInfo2File(ex: Throwable): String? {
        val sb = StringBuffer()
        for (entry in infos.entries) {
            val key = entry.key
            val value = entry.value
            sb.append("$key=$value\n")
        }
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)
        return try {
            val timestamp = System.currentTimeMillis()
            val time = formatter.format(Date())
            val fileName = "bug-$time-$timestamp.log"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                mCrashLogPath.createPath()
                val file = File(mCrashLogPath, fileName)
                val fos = FileOutputStream(file)
                fos.write(sb.toString().toByteArray())
                fos.close()
            }
            fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing file...", e)
            null
        }
    }

    private fun uploadFile(context: Context, logPath: String) = createUpload {
        this.context = context
        fileKey = "upload"
        filePath = logPath
        url = "https://app.wisesoft.net.cn/api/v1/crash/addCrashReport"
        params {
            "md5"..logPath.getFileMD5()
            "bundle"..context.packageName
            "system".."ANDROID"
            "deviceId"..context.getDeviceId()
            "version"..context.getLocalVersionName()
            "build"..context.getLocalVersionCode()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
        private const val TAG = "AppException"
    }
}
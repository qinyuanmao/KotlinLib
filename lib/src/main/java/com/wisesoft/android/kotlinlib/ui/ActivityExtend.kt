package com.wisesoft.android.kotlinlib.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wisesoft.android.kotlinlib.network.http.*
import com.wisesoft.android.kotlinlib.utils.IntentManager
import com.wisesoft.android.kotlinlib.utils.launch
import com.wisesoft.android.kotlinlib.utils.ui
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi

/**
 * @Title:  ActivityExtend
 * @Package com.wisesoft.android.kotlinlib.ui
 * @Description:    activity扩展函数
 * @date Create on 2018/6/30 22:41.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

/**
 * 设置标题栏和状态栏颜色和标题以及返回按钮
 *
 * @param title
 * @param back
 */
fun Activity.initToolbar(mToolbar: Toolbar, mTitle: TextView, title: String, imgBack: ImageView? = null) {
    if (mTitle != null && mToolbar != null) {
        mTitle.text = title
        mToolbar.title = ""
        if (imgBack != null) {
            imgBack.visibility = View.VISIBLE
            imgBack.setOnClickListener {
                finish()
            }
        }
    }
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    vararg args: Pair<String, Any?>?
) {
    intent = IntentManager.createIntent<T>(this, args)
    startActivityForResult(intent, requestCode)
}

inline fun <reified T> Activity.networkUiTask(
    init: NetworkAction<T>.() -> Unit
): Job {
    val networkAction = NetworkAction<T>()
    networkAction.init()
    val job = launch {
        val data = doHttp<T>(networkAction.param)
        data?.await()?.let { resp ->
            resp.data?.let {
                ui {
                    if (resp.isSuccess) {
                        networkAction.success(it)
                    } else {
                        networkAction.failed(resp.error)
                    }
                }
            }
        }
    }
    if (this is BaseActivity) {
        this.addJob(job)
    }
    return job
}

@ObsoleteCoroutinesApi
fun downloadUiTask(init: DownloadAction.() -> Unit): Job {
    val downloadAction = DownloadAction()
    downloadAction.init()
    val job = launch {
        val data = download(downloadAction.param)
        downloadAction.onProcess?.let { onProcess ->
            for (process in downloadAction.param.channel) {
                downloadAction.param.context.ui { process?.let { onProcess(it) } }
            }
        }
        data?.await()?.let { resp ->
            downloadAction.param.context.ui {
                if (resp.isSuccess) {
                    downloadAction.success(resp.data)
                } else {
                    downloadAction.failed(resp.error)
                }
            }
        }
    }
    if (downloadAction.param.context is BaseActivity) {
        (downloadAction.param.context as BaseActivity).addJob(job)
    }
    return job
}

@ObsoleteCoroutinesApi
inline fun <reified T> uploadUiTask(init: UploadAction<T>.() -> Unit): Job {
    val uploadAction = UploadAction<T>()
    uploadAction.init()
    val job = launch {
        val data = upload<T>(uploadAction.param)
        uploadAction.onProcess?.let { onProcess ->
            for (process in uploadAction.param.channel) {
                process?.let {
                    if (it.length > 3) {
                        uploadAction.param.context.ui { uploadAction.failed(it) }
                    } else {
                        uploadAction.param.context.ui { onProcess(it) }
                    }
                }
            }
        }
        data?.await()?.let { resp ->
            uploadAction.param.context.ui {
                if (resp.isSuccess) {
                    resp.data?.let { uploadAction.success(it) }
                } else {
                    uploadAction.failed(resp.error)
                }
            }
        }
    }
    if (uploadAction.param.context is BaseActivity) {
        (uploadAction.param.context as BaseActivity).addJob(job)
    }
    return job
}

fun Activity.getProgressDialog(
    title: String? = null,
    message: String,
    cancelable: Boolean = true
): ProgressDialog {
    val processDialog = ProgressDialog(this)
    title?.let { processDialog.setTitle(title) }
    processDialog.setMessage(message)
    processDialog.setCancelable(cancelable)
    return processDialog
}

/*申请需要的权限*/
fun askPerssion(context: Activity, permission: String, requsetCode: Int = 100) {
    //还需申请的权限列表
    val permissionList = ArrayList<String>()
    if (Build.VERSION.SDK_INT >= M) {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission)
        }
        if (permissionList.size > 0) {
            context.requestPermissions(permissionList.toTypedArray(), requsetCode)
        }
    }
}

/*批量申请需要的权限*/
fun askPerssion(context: Activity, permissList: ArrayList<String>, requsetCode: Int) {
    //还需申请的权限列表
    val permissionList = ArrayList<String>()
    if (Build.VERSION.SDK_INT >= M) {
        for (permission in permissList) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        if (permissionList.size > 0) {
            context.requestPermissions(permissionList.toTypedArray(), requsetCode)
        }
    }
}

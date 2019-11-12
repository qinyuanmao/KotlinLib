package com.wisesoft.android.kotlinlib.update

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import com.wisesoft.android.kotlinlib.ui.*
import com.wisesoft.android.kotlinlib.utils.canInstallApp
import com.wisesoft.android.kotlinlib.utils.showAlertDialog
import com.wisesoft.android.kotlinlib.utils.toast
import net.wisesoft.android.kotlinlib.R
import java.io.File

/**
 * @Title:  ActivityExtend
 * @Package package com.wisesoft.android.kotlinlib.update
 * @Description:    App更新
 * @date Create on 2018/10/4 23:50.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

fun Activity.normalUpadte(title: String, content: String, isForce: Boolean, url: String) {
    showAlertDialog {
        this.title = title
        message = content
        cancelable = !isForce
        positiveTitle = this@normalUpadte.getString(R.string.update)
        positiveAction = {
            downApp(UpdateBean(
                    1,
                    "",
                    UpdateBean.Result("", "", title, "", 0,
                        if (isForce) 1 else 0, "", "", 1, content, url)
                )
            )
        }
    }
}

fun Activity.checkUpdate() {
    networkUiTask<UpdateBean> {
        param = checkUpdate(this@checkUpdate)
        success = {
            if (it.code != 0) {
                toast(it.msg)
            } else {
                update(it)
            }
        }
    }
}

private fun Activity.update(bean: UpdateBean) {
    bean.result.run {
        showAlertDialog {
            title = "${if (this@run.title.isEmpty()) "发现新版本" else this@run.title}：$version"
            message = "更新内容：$detail"
            cancelable = isForce == 0
            positiveTitle = this@update.getString(R.string.update)
            positiveAction = {
                downApp(bean)
            }
        }
    }
}

private fun Activity.downApp(bean: UpdateBean)  {
    val dialog = getProgressDialog(message = getString(R.string.downloading))
    dialog.show()
    val savePath = "${Environment.getExternalStorageDirectory().absolutePath}/${this.packageName}"
    val saveName = "${this.packageName}.apk"
    downloadUiTask {
        param = downloadFile(this@downApp, bean.result.fileUrl, savePath, saveName)
        onProcess = { dialog.setMessage("正在下载：$it%") }
        success = {
            dialog.dismiss()
            showAlertDialog {
                title = "${bean.result.title}${getString(R.string.downloaded)}"
                message = "更新内容：${bean.result.detail}"
                cancelable = bean.result.isForce == 0
                positiveTitle = this@downApp.getString(R.string.update)
                positiveAction = {
                    if (this@downApp is BasePermissionActivity) {
                        installApp("$savePath/$saveName")
                    } else {
                        try {
                            installApp("$savePath/$saveName")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}

@Throws(Exception::class)
fun Activity.installApp(uri: String) {
    if (canInstallApp())
        startActivity(getInstallIntent(uri))
    else
        throw (Exception("不能在非BasePermissionActivity中请求安装权限！"))
}

private fun Activity.getInstallIntent(uri: String): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    val data = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(File(uri))
    } else {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        FileProvider.getUriForFile(this, "com.wisesoft.android.kotlinlib.provider", File(uri))
    }
    intent.setDataAndType(
        data,
        "application/vnd.android.package-archive"
    )
    return intent
}

private fun BasePermissionActivity.installApp(uri: String) {
    if (canInstallApp()) {
        startActivity(getInstallIntent(uri))
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requirePermission(Manifest.permission.REQUEST_INSTALL_PACKAGES, object : PermissionCallback {
                override fun noGet() {
                    toast(getString(R.string.cannot_install))
                }

                override fun reject() {
                    toast(getString(R.string.cannot_install))
                }

                override fun hadPermission() {
                    startActivity(getInstallIntent(uri))
                }
            })
    }
}
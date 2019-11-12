package com.wisesoft.android.kotlinlib.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.wisesoft.android.kotlinlib.utils.canInstallApp
import com.wisesoft.android.kotlinlib.utils.gotoAlertWindowPermissionActivity
import com.wisesoft.android.kotlinlib.utils.gotoInstallPermissionActivity

/**
 * @Title:  BasePermissionActivity
 * @Package com.wisesoft.android.kotlinlib.ui
 * @Description:    create a super class for permission require
 * @date Create on 2018/10/4 09:52.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
abstract class BasePermissionActivity : BaseActivity() {
    private val PERMISSION_CODE = 888
    private val OVERLAY_PERMISSION_REQ_CODE = 1234
    private val INSTALL_PACKAGES_REQUEST_CODE = 909
    private lateinit var mPermissionList: ArrayList<String>

    protected abstract fun PermissionFactory.init()

    private var mSignalPermissionCallback = HashMap<String, PermissionCallback?>()

    private fun setPermissionList(): ArrayList<String> {
        val permissionFactory = PermissionFactory()
        permissionFactory.init()
        return permissionFactory.array
    }

    abstract fun noGet(permission: String)
    abstract fun reject(permission: String)
    abstract fun hadPermission(permission: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPermissionList = setPermissionList()
        val unGetPermissions = mPermissionList.filter {
            !checkPermission(this, it)
        }
        if (unGetPermissions.isNotEmpty())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requirePermissions(unGetPermissions.toTypedArray())
            }
    }

    fun requirePermission(permission: String, callback: PermissionCallback? = null) {
        mSignalPermissionCallback[permission] = callback
        when (permission) {
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !canInstallApp())
                    gotoInstallPermissionActivity(INSTALL_PACKAGES_REQUEST_CODE)
                else callback?.hadPermission()
            }
            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    gotoAlertWindowPermissionActivity(OVERLAY_PERMISSION_REQ_CODE)
                } else callback?.hadPermission()
            }
            else -> {
                requirePermissions(arrayOf(permission))
            }
        }
    }

    protected fun requirePermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            for (index in 0 until grantResults.size) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    val showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        permissions[index]
                    )
                    if (showRequestPermission) {
                        reject(permissions[index])
                    } else {
                        noGet(permissions[index])
                    }
                } else {
                    hadPermission(permissions[index])
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INSTALL_PACKAGES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mSignalPermissionCallback[Manifest.permission.REQUEST_INSTALL_PACKAGES]?.hadPermission()
            } else {
                mSignalPermissionCallback[Manifest.permission.REQUEST_INSTALL_PACKAGES]?.noGet()
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                mSignalPermissionCallback[Manifest.permission.SYSTEM_ALERT_WINDOW]?.hadPermission()
            } else {
                mSignalPermissionCallback[Manifest.permission.SYSTEM_ALERT_WINDOW]?.noGet()
            }
        }
    }
}

fun checkPermission(context: Context, permission: String) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    else
        true


class PermissionFactory {
    val array: ArrayList<String> = ArrayList()
    operator fun String.unaryPlus() {
        array.add(this)
    }
}
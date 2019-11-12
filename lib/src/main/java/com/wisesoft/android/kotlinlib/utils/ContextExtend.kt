package com.wisesoft.android.kotlinlib.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/6/30 21:06.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */

val Context.TYPE_ACTIVITY: String
    get() = "CONTEXT_TYPE_ACTIVITY"
val Context.TYPE_APPLICATION: String
    get() = "CONTEXT_TYPE_APPLICATION"
val Context.TYPE_SERVICE: String
    get() = "CONTEXT_TYPE_SERVICE"
val Context.TYPE_BROADCAST_RECEIVER: String
    get() = "CONTEXT_TYPE_BROADCAST_RECEIVER"
val Context.TYPE_CONTENT_PROVIDER: String
    get() = "CONTEXT_TYPE_CONTENT_PROVIDER"
val Context.TYPE_OTHER: String
    get() = "CONTEXT_TYPE_OTHER"
private val Context.CHECK_OP_NO_THROW: String
    get() = "checkOpNoThrow"
private val Context.mLocationManager: LocationManager
    get() = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
private val Context.OP_POST_NOTIFICATION: String
    get() = "OP_POST_NOTIFICATION"

/*
 * 判断网络状态
 *
 * @return Boolean
 */
fun Context.isNetworkAvailable(): Boolean {
    val mgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = mgr.allNetworkInfo
    info?.indices?.filter { info[it].state === NetworkInfo.State.CONNECTED }
        ?.forEach { _ -> return true }
    return false
}

@SuppressLint("MissingPermission")
fun Context.getLocation(action: (Location?) -> Unit) {
    val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            ui { action(p0) }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderEnabled(p0: String?) {}
        override fun onProviderDisabled(p0: String?) {}
    }
    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0F, locationListener)
}

/**
 * 跳转到忽略电池优化设置页面
 *
 * @param requestCode 请求的code， 需要自定义onActivityResult
 */
@SuppressLint("BatteryLife")
fun Context.gotoSettingIgnoreBattery(requestCode: Int? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        try {
            val intent = Intent().newTask()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            if (requestCode == null || this !is Activity)
                startActivity(intent)
            else
                startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.gotoInstallPermissionActivity(requestCode: Int? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        toast("请允许App安装权限。")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:$packageName")
        if (requestCode == null || this !is Activity) startActivity(intent)
        else startActivityForResult(intent, requestCode)
    }
}

fun Context.gotoAlertWindowPermissionActivity(requestCode: Int? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + this.packageName)
        )
        if (requestCode == null || this !is Activity) startActivity(intent)
        else startActivityForResult(intent, requestCode)
    }
}

/**
 * 去app白名单列表
 */
fun Context.gotoWhiteListSetting() {
    try {
        val intent = Intent().newTask()
        intent.action = "com.android.settings.action.SETTINGS"
        intent.addCategory("com.android.settings.category")
        intent.`package` = "com.android.settings"
        intent.setClassName(
            "com.android.settings"
            , "com.android.settings.Settings\$PowerUsageSummaryActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/*
 * 判断当前Context类型
 *
 * @return String
 */
fun Context.isWhat(): String {
    return when (this) {
        is Activity -> TYPE_ACTIVITY
        is Application -> TYPE_APPLICATION
        is Service -> TYPE_SERVICE
        is BroadcastReceiver -> TYPE_BROADCAST_RECEIVER
        is ContentProvider -> TYPE_CONTENT_PROVIDER
        else -> TYPE_OTHER
    }
}

fun Context.isGpsOpen(isHighAccuracy: Boolean = false): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return if (isHighAccuracy) gps && network else gps || network
}

/**
 * 获取设备id
 */
@SuppressLint("HardwareIds", "MissingPermission")
fun Context.getDeviceId(): String {
    val telephony = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return if (telephony.deviceId != null) {
        telephony.deviceId
    } else {
        Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
    }
}

/**
 * 获取手机厂商
 *
 * @return  手机厂商
 */
fun Context.getDeviceBrand() = android.os.Build.BRAND

/**
 * 获取手机型号
 */
fun Context.getSystemModel() = android.os.Build.MODEL

/**
 * 获取当前手机系统版本号
 *
 * @return  系统版本号
 */
fun Context.getSystemVersion() = android.os.Build.VERSION.RELEASE

/**
 * 获取应用版本名称
 */
fun Context.getLocalVersionName(): String {
    return AppUtils.getAppVersionName()
}


/**
 * 获取应用版本号
 */
fun Context.getLocalVersionCode(): Int {
    return AppUtils.getAppVersionCode()
}

fun Context.getAssetsArray(fileName: String): JSONArray {
    val sb = StringBuilder()
    val am = assets
    try {
        val br = BufferedReader(
            InputStreamReader(
                am.open(fileName)
            )
        )
        var next = br.readLine()
        while (null != next) {
            sb.append(next)
            next = br.readLine()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        sb.delete(0, sb.length)
    }

    val str = sb.toString().trim { it <= ' ' }
    return JSONArray(str)
}

fun Context.getAssetsObject(fileName: String): JSONObject {
    val sb = StringBuilder()
    val am = assets
    try {
        val br = BufferedReader(
            InputStreamReader(
                am.open(fileName)
            )
        )
        var next = br.readLine()
        while (null != next) {
            sb.append(next)
            next = br.readLine()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        sb.delete(0, sb.length)
    }

    val str = sb.toString().trim { it <= ' ' }
    return JSONObject(str)
}

@SuppressLint("WrongConstant")
inline fun <reified T : Activity> Context.initNotification(
    title: String,
    content: String,
    notifyId: Int,
    icon: Int,
    largeIcon: Bitmap? = null,
    vararg args: Pair<String, Any?>?
) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder = NotificationCompat.Builder(
        this,
        createNotificationChannel("$packageName-$notifyId", "$packageName-$notifyId")
    )
    builder.setContentTitle(title)
        .setContentText(content)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                IntentManager.createIntent<T>(this, args),
                Notification.FLAG_AUTO_CANCEL
            )
        )
        .setWhen(System.currentTimeMillis())
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(false)
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setSmallIcon(icon)
    if (largeIcon != null) builder.setLargeIcon(largeIcon)
    val notify = builder.build()
    notify.flags = NotificationCompat.FLAG_AUTO_CANCEL
    notificationManager.notify(notifyId, notify)
}

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("NewApi")
fun Context.isNotificationEnabled(): Boolean {
    val mAppOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val appInfo = applicationInfo
    val pkg = applicationContext.packageName
    val uid = appInfo.uid
    var appOpsClass: Class<*>? = null
    try {
        appOpsClass = Class.forName(AppOpsManager::class.java.name)
        val checkOpNoThrowMethod = appOpsClass!!.getMethod(
            CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
            String::class.java
        )
        val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)

        val value = opPostNotificationValue.get(Int::class.java) as Int
        return checkOpNoThrowMethod.invoke(
            mAppOps,
            value,
            uid,
            pkg
        ) as Int === AppOpsManager.MODE_ALLOWED

    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }

    return false
}

fun Context.createNotificationChannel(channelId: String, channelName: String): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        channelId
    } else {
        ""
    }
}

@Throws(ContextException::class)
fun Context.hintDialog(title: String? = null, message: String): AlertDialog {
    if (this is Application) {
        throw ContextException("Context can`t an Application context!")
    } else {
        val dialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(true)
            .setNegativeButton("关闭") { dialog, _ -> dialog.dismiss() }

        if (title != null) {
            dialog.setTitle(title)
        }
        val alertDialog = dialog.create()

        if (this !is Activity) {
            alertDialog.window.setType(
                when {
                    Build.VERSION.SDK_INT > 25 -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    Build.VERSION.SDK_INT > 24 -> WindowManager.LayoutParams.TYPE_PHONE
                    else -> WindowManager.LayoutParams.TYPE_TOAST
                }
            )
        }

        alertDialog.show()
        return alertDialog
    }
}

class DialogInfo {
    lateinit var title: String
    lateinit var message: String
    var cancelable: Boolean = true
    var positiveable: Boolean = true
    var positiveTitle: String = "确定"
    var cancelTitle: String = "关闭"
    var positiveAction: (dialog: DialogInterface) -> Unit = {}
    var cancelAction: (dialog: DialogInterface) -> Unit = {}
}

@Throws(ContextException::class)
fun Context.showAlertDialog(init: DialogInfo.() -> Unit) {
    val dialogInfo = DialogInfo()
    dialogInfo.init()
    dialogInfo.run {
        if (this@showAlertDialog is Application) {
            throw ContextException("Context can`t an Application context!")
        } else {
            val dialog = AlertDialog.Builder(this@showAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
            if (cancelable) {
                dialog.setNegativeButton(cancelTitle) { dialog, _ -> cancelAction(dialog); dialog.dismiss() }
            }
            if (positiveable) {
                dialog.setPositiveButton(positiveTitle) { dialog, _ -> positiveAction(dialog); dialog.dismiss() }
            }
            val alertDialog = dialog.create()
            if (this@showAlertDialog !is Activity) {
                alertDialog.window.setType(
                    when {
                        Build.VERSION.SDK_INT > 25 -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        Build.VERSION.SDK_INT > 24 -> WindowManager.LayoutParams.TYPE_PHONE
                        else -> WindowManager.LayoutParams.TYPE_TOAST
                    }
                )
            }
            alertDialog.show()
        }
    }
}

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

@Throws(ContextException::class)
fun Context.getView(viewId: Int, parent: ViewGroup? = null): View {
    if (this is Activity)
        return if (parent == null) {
            LayoutInflater.from(this).inflate(viewId, null)
        } else {
            LayoutInflater.from(this).inflate(viewId, parent, false)
        }
    else
        throw ContextException("Context must be an Activity Context!")
}

inline fun <reified T : Activity> Context.startActivity(vararg args: Pair<String, Any?>?) {
    if (this is Activity) {
        this.startActivity(
            IntentManager.createIntent<T>(this, args)
//            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    } else {
        this.startActivity(IntentManager.createIntent<T>(this, args))
    }
}

inline fun <reified T : Context> Context.getIntent(vararg args: Pair<String, Any?>?) =
    IntentManager.createIntent<T>(this, args)

inline fun <reified T : Service> Context.startService(vararg args: Pair<String, Any?>?) {
    this.startService(IntentManager.createIntent<T>(this, args))
}

inline fun <reified T : Activity> Service.openForeground(
    title: String,
    content: String,
    smallIcon: Int,
    icon: Bitmap?,
    channelName: String = packageName
) {
    val intent = IntentManager.createIntent<T>(this)
    val pi = PendingIntent.getActivity(this, 0, intent, 0)
    val notification =
        NotificationCompat.Builder(this, createNotificationChannel(channelName, channelName))
            .setContentTitle(title)
            .setContentText(content)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(smallIcon)
            .setLargeIcon(icon)
            .setContentIntent(pi)
            .build()
    startForeground(1, notification)
}

fun Context.callPhone(tel: String, isCallNow: Boolean = false) {
    val intent = if (isCallNow) {
        Intent(Intent.ACTION_CALL)
    } else {
        Intent(Intent.ACTION_DIAL)
    }
    val data = Uri.parse("tel:$tel")
    intent.data = data
    startActivity(intent)
}

fun Context.browser(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.newTask()
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.share(text: String, subject: String = "", title: String? = null): Boolean {
    return try {
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, title))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false
}

fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.ui(f: Context.() -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper())
        f(this)
    else
        ContextHelper.handler.post { f(this) }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
}

fun Context.getScreenWidth(): Float {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    val width = dm.widthPixels
    val density = dm.density
    return width / density
}

fun Context.getScreenHeight(): Float {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    val height = dm.heightPixels
    val density = dm.density
    return height / density
}

fun Context.getOneDpi(): Float {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return dm.density
}

fun Context.canInstallApp() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) packageManager.canRequestPackageInstalls() else true
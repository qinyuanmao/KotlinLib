package com.wisesoft.android.kotlinlib.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.blankj.utilcode.util.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.PictureFileUtils
import com.wisesoft.android.kotlinlib.intface.FileResultListener
import me.leolin.shortcutbadger.ShortcutBadger
import top.zibin.luban.CompressionPredicate
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File


/**
 * 常用操作工具类
 * Created by 杨敏 on 2018/9/21.
 */
object CommonUtils {

    /**
     * 设置气泡数textview
     *
     * @param tv
     * @param count
     */
    fun setBubbleText(tv: TextView, count: Int) {
        if (count > 0) {
            tv.visibility = View.VISIBLE
            if (count > 99) {//大于99显示99+
                tv.text = "99+"
            } else
                tv.text = count.toString()
        } else {
            tv.visibility = View.GONE
        }
    }

    //    /**
    //     * 设置应用角标数
    //     *
    //     * @param count
    //     */
    //    public static void setBadgerBubble(Context context, int count) {
    //        if (count > 0) {
    //            if (Rom.isVivo() || Rom.isMiui()) {
    //                apply_vivo(context, count);
    //            } else if (Rom.isMiui()) {
    //                apply_xiaomi(context, count);
    //            } else {
    //                ShortcutBadger.applyCount(Utils.getApp(), count);//for 1.1.4+
    //            }
    //        } else
    //            ShortcutBadger.removeCount(Utils.getApp());
    //    }

    /**
     * 设置应用角标数
     *
     * @param count
     */
    fun setBadgerBubble(count: Int) {
        if (count > 0) {
            ShortcutBadger.applyCount(Utils.getApp(), count)//for 1.1.4+
        } else
            ShortcutBadger.removeCount(Utils.getApp())
    }

    /**
     * 拨手机号
     *
     * @param num
     */
    @SuppressLint("MissingPermission")
    fun callMobile(num: String) {
        if (StringUtils.isEmpty(num) || !RegexUtils.isMobileSimple(num)) {
            ToastUtils.showShort("电话号码为空或者号码不正确")
            return
        }
        try {
            PhoneUtils.call(num)
        } catch (e: Exception) {
            ToastUtils.showShort("拨号失败，请检查是否开启短信发送权限")
        }

    }

    /**
     * 拨号
     *
     * @param num
     */
    fun call(num: String) {
        call(num, true)
    }

    /**
     * 拨号
     *
     * @param num
     */
    @SuppressLint("MissingPermission")
    fun call(num: String, call: Boolean) {
        if (StringUtils.isEmpty(num) || num.contains("**")) {
            ToastUtils.showShort("电话号码为空或号码有误")
            return
        }
        try {
            if (call)
                PhoneUtils.call(num)
            else
                PhoneUtils.dial(num)
        } catch (e: Exception) {
            ToastUtils.showShort("拨号失败，请检查是否开启拨号权限")
        }

    }

    /**
     * 跳转到输入号码界面
     */
    fun callButton(context: Context, num: String) {
        val dialIntent = Intent(Intent.ACTION_CALL_BUTTON)
        try {
            context.startActivity(dialIntent)
        } catch (e: Exception) {
            ToastUtils.showShort("拨号失败，请检查是否开启拨号权限")
        }

    }

    /**
     * 申请检测某个权限
     *
     * @return true 已取得该权限 false 未取得该权限
     */
    fun askForPerssion(context: Activity, permissionStr: String, requsetCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= M) {
            if (context.checkSelfPermission(permissionStr) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(arrayOf(permissionStr), requsetCode)
                return false
            }
        }
        return true
    }

    /**
     * 发短信
     *
     * @param num
     */
    @JvmOverloads
    fun sendSMS(num: String, content: String = "") {
        if (StringUtils.isEmpty(num) || !RegexUtils.isMobileSimple(num)) {
            ToastUtils.showShort("电话号码格式不正确")
            return
        }
        try {
            PhoneUtils.sendSms(num, content)
        } catch (e: Exception) {
            ToastUtils.showShort("操作失败，请检查是否开启短信发送权限")
        }

    }

    fun sendEmail(emailAddress: String) {
        if (StringUtils.isEmpty(emailAddress) || !RegexUtils.isEmail(emailAddress)) {
            ToastUtils.showShort("邮箱地址为空或者格式不正确", Toast.LENGTH_SHORT)
            return
        }
        // 必须明确使用mailto前缀来修饰邮件地址,如果使用
        val uri = Uri.parse("mailto:$emailAddress")
        val email = arrayOf(emailAddress)
        val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
        //emailIntent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "") // 主题
        emailIntent.putExtra(Intent.EXTRA_TEXT, "") // 正文
        Utils.getApp().startActivity(Intent.createChooser(emailIntent, "请选择邮件类应用"))
    }


    /**
     * 包含大小写字母及数字且在6-20位
     * 是否包含
     *
     * @param str
     * @return
     */
    fun isLetterDigit(str: String): Boolean {
        var isDigit = false//定义一个boolean值，用来表示是否包含数字
        var isLetter = false//定义一个boolean值，用来表示是否包含字母
        for (i in 0 until str.length) {
            if (Character.isDigit(str[i])) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true
            } else if (Character.isLetter(str[i])) {  //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true
            }
        }
        val regex = "^[a-zA-Z0-9]{6,20}$"
        return isDigit && isLetter && str.matches(regex.toRegex())
    }


    /**
     * 图片选择器
     *
     * @param activity
     * @param resultCode
     */
    fun pickImage(activity: FragmentActivity, resultCode: Int) {
        pickImage(activity, 1, resultCode)
    }

    /**
     * 图片选择器
     *
     * @param activity
     * @param maxSize
     * @param resultCode
     * @param camera       是否使用拍照功能
     * @param crop 支持裁剪
     * @param circle 圆形图片
     */
    @JvmOverloads
    fun pickImage(
        activity: FragmentActivity,
        maxSize: Int,
        resultCode: Int,
        camera: Boolean = false,
        crop: Boolean = false,
        circle: Boolean = false,
        compress: Boolean = false
//        fileProvider: String = getFileProvider(activity)
    ) {
        if (!askForPerssion(activity, WRITE_EXTERNAL_STORAGE, 999)) {
            ToastUtils.showShort("请先开启文件读写权限，否则功能无法使用")
            return
        }
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//            .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
            .maxSelectNum(maxSize)// 最大图片选择数量 int
            .imageSpanCount(4)// 每行显示个数 int
//            .selectionMode()// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .previewImage(true)// 是否可预览图片 true or false
//            .previewVideo()// 是否可预览视频 true or false
//            .enablePreviewAudio() // 是否可播放音频 true or false
            .isCamera(camera)// 是否显示拍照按钮 true or false
//            .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
//            .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
            .enableCrop(crop)// 是否裁剪 true or false
            .compress(compress)// 是否压缩 true or false
//            .glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//            .withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//            .hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
            .isGif(true)// 是否显示gif图片 true or false
//            .compressSavePath(getPath())//压缩图片保存地址
            .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
            .circleDimmedLayer(circle)// 是否圆形裁剪 true or false
            .showCropFrame(!circle)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
            .showCropGrid(!circle)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
//            .openClickSound()// 是否开启点击声音 true or false
//            .selectionMedia()// 是否传入已选图片 List<LocalMedia> list
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
//            .cropCompressQuality(200)// 裁剪压缩质量 默认90 int
            .minimumCompressSize(200)// 小于100kb的图片不压缩
            .synOrAsy(true)//同步true或异步false 压缩 默认同步
//            .cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int
            .rotateEnabled(false) // 裁剪是否可旋转图片 true or false
            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
//            .videoQuality()// 视频录制质量 0 or 1 int
//            .videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int
//            .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int
//            .recordVideoSecond()//视频秒数录制 默认60s int
            .isDragFrame(false)// 是否可拖动裁剪框(固定)
            .forResult(resultCode)//结果回调onActivityResult code


//            val savePath = "${Environment.getExternalStorageDirectory().absolutePath}/$packageName/images"
//            savePath.createPath()
//            PictureSelector.create(this)
//                    .openGallery(PictureMimeType.ofImage())
//                    .selectionMode(PictureConfig.SINGLE)
//                    .imageFormat(PictureMimeType.PNG)
//                    .isCamera(true)
//                    .setOutputCameraPath(savePath)
//                    .enableCrop(true)
//                    .compress(true)
//                    .compressSavePath(savePath)
//                    .showCropFrame(false)
//                    .showCropGrid(false)
//                    .circleDimmedLayer(true)
//                    .rotateEnabled(false)
//                    .isDragFrame(true)
//                    .cropWH(240, 240)
//                    .withAspectRatio(1, 1)
//                    .freeStyleCropEnabled(true)
//                    .forResult(PICK_IMAGE)

//        Matisse.from(activity)
//                .choose(MimeType.ofImage())
//                .theme(R.style.Matisse_Dracula)
//                .countable(true)
//                .capture(camera)
//                .captureStrategy(CaptureStrategy(true, fileProvider))
//                .maxSelectable(maxSize)
//                .imageEngine(MyGlideEngine())
//                .forResult(resultCode)

    }

    /**
     * 视频录制
     *
     * @param activity
     * @param maxSize
     * @param resultCode
     */
    @JvmOverloads
    fun pickVideo(
        activity: FragmentActivity,
        maxSize: Int,
        resultCode: Int,
        compress: Boolean = false
    ) {
        if (!askForPerssion(activity, WRITE_EXTERNAL_STORAGE, 999)) {
            ToastUtils.showShort("请先开启文件读写权限，否则功能无法使用")
            return
        }
        // 进入相册 以下是例子：用不到的api可以不写
        PictureSelector.create(activity)
            .openGallery(PictureMimeType.ofVideo())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//            .theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
            .maxSelectNum(1)// 最大视频选择数量 int
            .imageSpanCount(4)// 每行显示个数 int
            .previewVideo(true)// 是否可预览视频 true or false
//            .enablePreviewAudio() // 是否可播放音频 true or false
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .compress(compress)// 是否压缩 true or false
            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
            .cropCompressQuality(200)// 裁剪压缩质量 默认90 int
            .synOrAsy(true)//同步true或异步false 压缩 默认同步
            .videoQuality(1)// 视频录制质量 0 or 1 int
            .videoMaxSecond(60)// 显示多少秒以内的视频or音频也可适用 int
            .videoMinSecond(1)// 显示多少秒以内的视频or音频也可适用 int
//            .recordVideoSecond()//视频秒数录制 默认60s int
            .forResult(resultCode)//结果回调onActivityResult code
    }

    /**
     * 清除PictureSelector裁剪和压缩后的缓存
     */
    fun deleteCacheDirFile(context: Context) {
        try {
            PictureFileUtils.deleteCacheDirFile(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 兼容android 7.0 fileProvider的问题（获取fileProvider作者）
     */
    fun getFileProvider(mContext: Context): String {
        return mContext.packageName + ".im.fileprovider"
    }


    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    fun setTopApp(context: Context) {
        if (true) {
            /**获取ActivityManager */
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager

            /**获得当前运行的task(任务) */
            val taskInfoList = activityManager.getRunningTasks(100)
            for (taskInfo in taskInfoList) {
                /**找到本应用的 task，并将它切换到前台 */
                if (taskInfo.topActivity.packageName == context.packageName) {
                    activityManager.moveTaskToFront(taskInfo.id, 0)
                    break
                }
            }
        }
    }

    /**
     * 文件压缩
     * @param path 源文件路径
     * @param size 压缩尺寸控制（200kb以内忽略不压缩）
     * @param callback 压缩回调接口
     * @param targetFolder 目标文件夹
     */
    fun pressImage(
        context: Context,
        path: String,
        size: Int = 200,
        targetFolder: String?,
        callback: FileResultListener
    ) {
        Luban.with(context)
            .load(path)
            .ignoreBy(size)
            .setTargetDir(targetFolder)
            .filter(CompressionPredicate {
                return@CompressionPredicate !(StringUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))
            }).setCompressListener(object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    if (file != null && FileUtils.isFileExists(file)) {
                        callback.onPressSuccess(file)
                    } else {
                        callback.onPressFail("文件不存在")
                    }
                }

                override fun onError(e: Throwable?) {
                    callback.onPressFail("压缩失败")
                }

                override fun onStart() {
                }
            }).launch()
    }

    /**
     * 多个文件压缩（为避免压缩过程阻塞ui线程，使用子线程来完成这个过程）
     * @param path 文件路径
     * @param size 压缩尺寸控制
     * @param callback 压缩回调接口
     */
    fun pressImage(
        context: Context,
        fileList: List<String>,
        size: Int = 200,
        targetPath: String?,
        callback: FileResultListener
    ) {
        val resultList = ArrayList<File>()
        fileList.map {
            pressImage(context, it, size, targetPath, object : FileResultListener {
                override fun onPressSuccess(file: File) {
                    if (FileUtils.isFileExists(file)) {
                        resultList.add(file)
                    }
                }

                override fun onPressListSuccess(fileList: List<File>) {
                }

                override fun onPressFail(errorMsg: String) {
                    resultList.add(File(it))
                }
            })
        }
        callback.onPressListSuccess(resultList)
    }
}
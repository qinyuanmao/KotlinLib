package com.wisesoft.android.kotlinlib.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.wisesoft.android.kotlinlib.intface.DownLoadLinstener
import com.wisesoft.android.kotlinlib.network.http.createDownload
import com.wisesoft.android.kotlinlib.ui.downloadUiTask
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.File
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.Executors

/**
 * 文件处理工具类
 */
@SuppressLint("HandlerLeak")
class FileUtil {

    companion object {
        private var instance: FileUtil? = null
        private var imgCaches: HashMap<String, SoftReference<Bitmap>>? = null

        private val executorThreadPool = Executors
            .newFixedThreadPool(1)

        init {
            instance = FileUtil()
            imgCaches = HashMap()
        }

        fun getInstance(): FileUtil? {
            return if (instance != null) {
                instance
            } else null
        }

        fun getFilePathByUri(context: Activity?, uri: Uri?): String? {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                return getImageAbsolutePath(context, uri)
            } else {//4.4以下下系统调用方法
                return getRealPathFromURI(context, uri)
            }
        }

        @TargetApi(19)
        fun getImageAbsolutePath(context: Activity?, imageUri: Uri?): String? {
            if (context == null || imageUri == null)
                return null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                    context,
                    imageUri
                )
            ) {
                if (isExternalStorageDocument(imageUri)) {
                    val docId = DocumentsContract.getDocumentId(imageUri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return (Environment.getExternalStorageDirectory().toString() + "/"
                                + split[1])
                    }
                } else if (isDownloadsDocument(imageUri)) {
                    val id = DocumentsContract.getDocumentId(imageUri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(imageUri)) {
                    val docId = DocumentsContract.getDocumentId(imageUri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = MediaStore.Images.Media._ID + "=?"
                    val selectionArgs = arrayOf(split[1])
                    return getDataColumn(
                        context, contentUri, selection,
                        selectionArgs
                    )
                }
            } // MediaStore (and general)
            else if ("content".equals(imageUri.scheme!!, ignoreCase = true)) {
                // Return the remote address
                return if (isGooglePhotosUri(imageUri)) imageUri.lastPathSegment else getDataColumn(
                    context,
                    imageUri,
                    null,
                    null
                )
            } else if ("file".equals(imageUri.scheme!!, ignoreCase = true)) {
                return imageUri.path
            }// File
            return null
        }

        fun getRealPathFromURI(mContext: Activity?, contentUri: Uri?): String? {
            var cursor: Cursor? = null
            try {
                var res: String? = null
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = mContext?.contentResolver?.query(contentUri, proj, null, null, null)
                if (null != cursor && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    res = cursor.getString(column_index)
                    cursor.close()
                }
                return res
            } catch (e: Exception) {
                return null
            } finally {
                cursor?.close()
            }
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri
                .authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri
                .authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri
                .authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri
                .authority
        }

        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            try {
                var cursor: Cursor? = null
                val column = MediaStore.Images.Media.DATA
                val projection = arrayOf(column)
                try {
                    cursor = context.contentResolver.query(
                        uri!!, projection,
                        selection, selectionArgs, null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val index = cursor.getColumnIndexOrThrow(column)
                        return cursor.getString(index)
                    }
                } finally {
                    cursor?.close()
                }
            } catch (ie: IllegalArgumentException) {
                return null
            }

            return null
        }

        /**
         * Java文件操作 获取文件扩展名
         *
         * @param filename
         * @return
         */
        fun getFileSuffix(filename: String?): String? {
            if (filename != null && filename.isNotEmpty()) {
                val dot = filename.lastIndexOf('.')
                if (dot > -1 && dot < filename.length - 1) {
                    return filename.substring(dot + 1)
                }
            }
            return filename
        }

        /**
         * 获取文件大小尺寸 单位kb
         */
        fun getFileSize(filePath: String): Int {
            var file = File(filePath)
            if (file.exists())
                return (file.length() / 1024).toInt()
            return 0
        }

        /**
         * 打开系统文件管理器
         *
         * @param activity
         * @param requestCode
         */
        fun openFileManager(activity: Activity, requestCode: Int) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"//设置类型，我这里是任意类型
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            activity.startActivityForResult(intent, requestCode)
        }


        fun openFile(filePath: String): Intent? {
            val file = File(filePath)
            if (!file.exists())
                return null
            /* 取得扩展名 */
            val end = getFileSuffix(file.name)
            /* 依扩展名的类型决定MimeType */
            return if (end == "m4a" || end == "mp3" || end == "mid"
                || end == "xmf" || end == "ogg" || end == "wav"
                || end == "amr"
            ) {
                getAudioFileIntent(filePath)
            } else if (end == "3gp" || end == "mp4" || end == "avi" || end == "rmvb") {
                getVideoFileIntent(filePath)
            } else if (end == "jpg" || end == "gif" || end == "png"
                || end == "jpeg" || end == "bmp"
            ) {
                getImageFileIntent(filePath)
            } else if (end == "apk") {
                getApkFileIntent(filePath)
            } else if (end == "ppt") {
                getPptFileIntent(filePath)
            } else if (end == "xls" || end == "xlsx") {
                getExcelFileIntent(filePath)
            } else if (end == "doc" || end == "docx") {
                getWordFileIntent(filePath)
            } else if (end == "pdf") {
                getPdfFileIntent(filePath)
            } else if (end == "chm") {
                getChmFileIntent(filePath)
            } else if (end == "txt") {
                getTextFileIntent(filePath, false)
            } else {
                getAllIntent(filePath)
            }
        }

        // Android获取一个用于打开APK文件的intent
        fun getAllIntent(param: String): Intent {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "*/*")
            return intent
        }

        // Android获取一个用于打开APK文件的intent
        private fun getApkFileIntent(param: String): Intent {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            return intent
        }

        // Android获取一个用于打开VIDEO文件的intent
        private fun getVideoFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("oneshot", 0)
            intent.putExtra("configchange", 0)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "video/*")
            return intent
        }

        // Android获取一个用于打开AUDIO文件的intent
        private fun getAudioFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("oneshot", 0)
            intent.putExtra("configchange", 0)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "audio/*")
            return intent
        }

        /**
         * 兼容android 7.0文件提供器权限问题
         *
         * @param intent
         * @param path
         * @return
         */
        private fun getPermission(intent: Intent, path: String): Uri {
            var uri = Uri.fromFile(File(path))
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileForAndroid7.getUriForFile(Utils.getApp(), File(path))
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            return uri
        }

        // Android获取一个用于打开Html文件的intent
        fun getHtmlFileIntent(param: String): Intent {
            val uri = Uri.parse(param).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(param).build()
            val intent = Intent("android.intent.action.VIEW")
            intent.setDataAndType(uri, "text/html")
            return intent
        }

        // Android获取一个用于打开图片文件的intent
        private fun getImageFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "image/*")
            return intent
        }

        // Android获取一个用于打开PPT文件的intent
        private fun getPptFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            return intent
        }

        // Android获取一个用于打开Excel文件的intent
        private fun getExcelFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/vnd.ms-excel")
            return intent
        }

        // Android获取一个用于打开Word文件的intent
        private fun getWordFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/msword")
            return intent
        }

        // Android获取一个用于打开CHM文件的intent
        private fun getChmFileIntent(param: String): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/x-chm")
            return intent
        }

        // Android获取一个用于打开文本文件的intent
        private fun getTextFileIntent(param: String, paramBoolean: Boolean): Intent {
            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (paramBoolean) {
                val uri1 = Uri.parse(param)
                intent.setDataAndType(uri1, "text/plain")
            } else {
                val uri2 = getPermission(intent, param)
                intent.setDataAndType(uri2, "text/plain")
            }
            return intent
        }

        // Android获取一个用于打开PDF文件的intent
        private fun getPdfFileIntent(param: String): Intent {

            val intent = Intent("android.intent.action.VIEW")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = getPermission(intent, param)
            intent.setDataAndType(uri, "application/pdf")
            return intent
        }

        /**
         * 下载文件
         *
         * @param url
         */
        @ObsoleteCoroutinesApi
        fun downloadFile(
            context: Context,
            url: String,
            path: String?,
            fileName: String,
            linstener: DownLoadLinstener?
        ) {
            downloadUiTask {
                param = downloadFile(context, url, path, fileName)
                onProcess = {
                    linstener?.onProcess(it.toInt())
                }
                success = {
                    if (!StringUtils.isEmpty(it)) {
                        linstener?.onSucess(it!!)
                    } else {
                        linstener?.onFail("下载失败")
                    }
                }
            }
        }

        fun downloadFile(
            context: Context,
            url: String,
            savePath: String? = null,
            fileName: String? = null
        ) = createDownload {
            this.url = url
            this.savePath = savePath
            this.fileName = fileName
            this.context = context
        }
    }
}

package com.wisesoft.android.kotlinlib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;


/**
 * 兼容android 7.0文件读取权限
 * Created by Lain on 2017/3/8.
 */
public class FileForAndroid7 {

    /**
     * android 7.0 文件管理权限
     *
     * @param file
     */
    public static String getPermission(Context mcontext, File file) {
        Uri uri = FileProvider.getUriForFile(mcontext
                , getFileProvider(mcontext), file);
        mcontext
                .grantUriPermission(mcontext.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return uri.toString();
    }

    /**
     * android 7.0 文件管理权限
     * 获取文件uri
     *
     * @param file
     */
    public static Uri getUriForFile(Context mcontext, File file) {
        Uri uri = FileProvider.getUriForFile(mcontext
                , getFileProvider(mcontext), file);
        mcontext
                .grantUriPermission(mcontext.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return uri;
    }

    private static String getFileProvider(Context mContext) {
        return mContext.getPackageName() + ".provider";
    }
}

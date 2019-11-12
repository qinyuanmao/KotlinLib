package com.wisesoft.android.kotlinlib.intface

import java.io.File

/**
 * 文件压缩回调
 * Created by mm on 2016/6/17.
 */
interface FileResultListener {
    fun onPressSuccess(file: File)

    fun onPressListSuccess(fileList: List<File>)

    fun onPressFail(errorMsg: String)
}

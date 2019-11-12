package com.wisesoft.android.kotlinlib.intface

/**
 * 下载回调接口
 * Created by 杨敏 on 2019/4/4.
 */
interface DownLoadLinstener {
    fun onProcess(progress: Int)
    fun onSucess(path: String)
    fun onFail(msg: String)
}
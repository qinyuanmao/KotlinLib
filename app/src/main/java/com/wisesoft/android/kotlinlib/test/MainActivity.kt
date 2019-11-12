package com.wisesoft.android.kotlinlib.test

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.wisesoft.android.kotlinlib.db.*
import com.wisesoft.android.kotlinlib.network.http.Method
import com.wisesoft.android.kotlinlib.network.http.createRequire
import com.wisesoft.android.kotlinlib.network.initNetworkFactory
import com.wisesoft.android.kotlinlib.network.socket.SocketManager
import com.wisesoft.android.kotlinlib.network.socket.connectSocket
import com.wisesoft.android.kotlinlib.ui.*
import com.wisesoft.android.kotlinlib.update.checkUpdate
import com.wisesoft.android.kotlinlib.utils.*
import com.wisesoft.android.kotlinlib.view.loadUrl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.MediaType

class MainActivity : BasePermissionActivity() {
    override fun PermissionFactory.init() {
        + Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    override fun hadPermission(permission: String) {

    }

    private var mCount = 0
    private var mJob: Job? = null
    private var mIsCountRunning = true

    override fun noGet(permission: String) {
        toast("$permission don't get")
    }

    override fun reject(permission: String) {
        toast("$permission is reject")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sqlListener()
        mImg.loadUrl("https://app.wisesoft.net.cn/api/v1/resource/ipa/com.wisesoft.SXJGT/icon/icon.PNG")
        asyncListener()
        webSocketListener()
        networkListener()
    }

    private fun networkListener() {
        mBtnUpdateApp.setOnClickListener {
            checkUpdate()
        }
        mBtnUploadApp.setOnClickListener {
            uploadFile()
        }
    }

    private fun webSocketListener() {
        mBtnSocketCoc.setOnClickListener {
            connectSocket {
                param = createSocketParameter(this@MainActivity)
            }
        }
        var a = 0
        mBtnSocketService.setOnClickListener {
            this@MainActivity.service {
                connected = {
                    connectSocket {
                        param = createSocketParameter(this@MainActivity)
                        onMessage = {
                            println("第${a++}次心跳")
                        }
                    }
                }
                disconnected = {
                    SocketManager.INSTANCE.removeSocket()
                }
            }
        }

        /*service {
            connected = {
                var count = 1
                delayTask(2000) {
                    println("Service 1:${++count}")
                }
            }
            disconnected = {
                println("Service 1: Finish")
            }
        }*/
        mBtnSocketStop.setOnClickListener {
            SocketManager.INSTANCE.removeSocket()
        }
    }

    private fun uploadFile() {
        val dialog = getProgressDialog(message = "正在上传：0%")
        dialog.show()
        uploadUiTask<UploadFileBean> {
            param = uploadFile(this@MainActivity)
            onProcess = { dialog.setMessage("正在上传：$it%") }
            success = {
                dialog.dismiss()
                mTvNetworkResult.text = it.toJsonStr()
            }
            failed = {
                dialog.dismiss()
                toast(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun sqlListener() {
        mBtnInsert.setOnClickListener {
            val demoModel = DemoModel("123", "234", "18583872978", username = "qinyuanmao")
            demoModel.insert()
        }
        mBtnUpdate.setOnClickListener {
            val demoModel = DemoModel("123", "234", "17358591661", username = "qinyuanmao")
            demoModel.update()
        }
        mBtnSelect.setOnClickListener {
            GlobalScope.launch {
                var data = Select().table("user").where("[id] = ?", "123")
                    .get(DemoModel::class.java).toString()
                ui { mTvSqlResult.text = "第一种：$data" }
                delay(5000)
                data = nGet<DemoModel> {
                    table("user").where("[id] = ?", "123")
                }.toString()
                ui { mTvSqlResult.text = "第二种：$data" }
                delay(5000)
                data = get<DemoModel> {
                    this select "*" from "user" where "id" value "123"
                }.toString()
                ui { mTvSqlResult.text = "第三种：$data" }
            }
        }
        mBtnDelete.setOnClickListener {
            val demoModel = DemoModel("123", "234", "17358591661", username = "qinyuanmao")
            demoModel.delete()
        }
    }

    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun asyncListener() {
        mBtnCountStart.setOnClickListener {
            mBtnCountEnd.callOnClick()
            mBtnCountStart.text = "重新开始"
            mJob = delayTask(1000) {
                ui { mTvCount.text = "${++mCount}" }
            }
        }
        mBtnCountStop.setOnClickListener {
            GlobalScope.launch {
                if (mIsCountRunning) {
                    mJob?.cancel()
                    ui { mBtnCountStop.text = "继续" }
                } else {
                    mBtnCountStart.callOnClick()
                    ui { mBtnCountStop.text = "暂停" }
                }
                mIsCountRunning = !mIsCountRunning
            }
        }
        mBtnCountEnd.setOnClickListener {
            mBtnCountStart.text = "开始"
            GlobalScope.launch {
                mJob?.cancel()
                mCount = 0
                ui { mTvCount.text = "0" }
            }
        }
    }
}
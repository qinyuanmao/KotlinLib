package com.wisesoft.android.kotlinlib.ui

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.BarUtils
import com.wisesoft.android.kotlinlib.service.ConnectionAction
import com.wisesoft.android.kotlinlib.service.NormalBinder
import com.wisesoft.android.kotlinlib.service.NormalService
import com.wisesoft.android.kotlinlib.utils.ActivityContainer
import com.wisesoft.android.kotlinlib.utils.ObserverManager
import com.wisesoft.android.kotlinlib.utils.getIntent
import kotlinx.android.synthetic.main.layout_loading.view.*
import kotlinx.coroutines.Job
import net.wisesoft.android.kotlinlib.R
import solid.ren.skinlibrary.base.SkinBaseActivity

/**
 * @Title:  BaseActivity
 * @Package com.wisesoft.android.kotlinlib.ui
 * @Description:  BaseActivity
 * @date Create on 2018/6/29 15:08.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
abstract class BaseActivity : SkinBaseActivity() {

    private var mIsNeedFull = false
    private lateinit var mContext: Context
    private var mBound: Boolean = false
    private var mConnections: ArrayList<ServiceConnection> = ArrayList()
    private var mJobs: ArrayList<Job> = ArrayList()
    private val mObserverKeySet = ArrayList<String>()
    private var mDialog: MaterialDialog? = null


    fun addJob(job: Job) {
        mJobs.add(job)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        if (mIsNeedFull) {
            window.requestFeature(Window.FEATURE_NO_TITLE)
            window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
//                window.navigationBarColor = Color.TRANSPARENT
            }
        } else
            BarUtils.setStatusBarAlpha(this, 0)
        ActivityContainer.addActivity(this)
    }

    fun setNeedFull(isNeedFull: Boolean) {
        mIsNeedFull = isNeedFull
    }

    override fun onDestroy() {
        ActivityContainer.removeActivity(this)
        if (mBound) {
            mConnections.forEach {
                unbindService(it)
            }
            mBound = false
        }
        mJobs.forEach {
            it.cancel()
        }
        mObserverKeySet.forEach { ObserverManager.INSTANCE.delete(it) }
        super.onDestroy()
    }

    fun service(init: ConnectionAction.() -> Unit) {
        val connection = ConnectionAction()
        connection.init()
        val intent = getIntent<NormalService>()
        mConnections.add(object : ServiceConnection {
            override fun onServiceDisconnected(p0: ComponentName?) {
                mBound = false
                connection.disconnected?.let {
                    it()
                }
            }

            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                val binder = p1 as NormalBinder?
                binder?.getService()?.doWork { connection.connected() }
                mBound = true
            }

        })
        bindService(intent, mConnections[mConnections.size - 1], Context.BIND_AUTO_CREATE)
    }

    fun addObserver(key: String, block: ((Array<out Any?>) -> Unit)? = null) {
        mObserverKeySet.add(key)
        ObserverManager.INSTANCE.add(key, block)
    }

    protected fun setToolbarBtn(txt: String) {
        this.setToolbarBtn(txt, null, null)
    }

    protected fun setToolbarBtn(txt: String, btnRight: TextView?, clickListener: View.OnClickListener?) {
        if (btnRight != null) {
            btnRight.visibility = View.VISIBLE
            btnRight.text = txt
            if (clickListener != null)
                btnRight.setOnClickListener(clickListener)
        }
    }

    protected fun setToolbarImgBtn(imgRes: Int) {
        this.setToolbarImgBtn(imgRes, null, null)
    }

    protected fun setToolbarImgBtn(imgRes: Int, btnRight: ImageView?, clickListener: View.OnClickListener?) {
        if (btnRight != null) {
            btnRight.visibility = View.VISIBLE
            btnRight.setImageResource(imgRes)
            if (clickListener != null)
                btnRight.setOnClickListener(clickListener)
        }
    }

    fun showProgressbarDialog(content: String = "加载中...") {
        showProgressbarDialog(content, R.layout.layout_loading)
    }


    fun showProgressbarDialog(resId: Int) {
        showProgressbarDialog("加载中...", resId)
    }

    private fun showProgressbarDialog(content: String, resId: Int) {
        if (mDialog == null) {
            val view = LayoutInflater.from(mContext).inflate(resId, null)
            view.apply {
                tvContent?.text = content
            }
            mDialog = MaterialDialog.Builder(mContext).customView(view, true).show()
        } else if (!mDialog!!.isShowing) {
            mDialog!!.show()
        }
        mDialog?.setCanceledOnTouchOutside(false)
    }

    fun dismissProgressbarDialog() {
        if (mDialog != null)
            mDialog!!.dismiss()
        mDialog = null
    }
}
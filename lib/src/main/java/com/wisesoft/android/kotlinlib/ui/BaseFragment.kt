package com.wisesoft.android.kotlinlib.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.wisesoft.android.kotlinlib.service.ConnectionAction
import com.wisesoft.android.kotlinlib.service.NormalBinder
import com.wisesoft.android.kotlinlib.service.NormalService
import com.wisesoft.android.kotlinlib.utils.ObserverManager
import com.wisesoft.android.kotlinlib.utils.getIntent
import com.wisesoft.android.kotlinlib.utils.getView
import com.wisesoft.android.kotlinlib.view.EmptyLayout
import kotlinx.android.synthetic.main.layout_loading.view.*
import kotlinx.coroutines.Job
import net.wisesoft.android.kotlinlib.R
import solid.ren.skinlibrary.base.SkinBaseFragment

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2017/12/26 09:40.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
abstract class BaseFragment : SkinBaseFragment() {


    protected lateinit var mParentView: View
    protected lateinit var mContext: Context
    private var mBound: Boolean = false
    private var mConnections: ArrayList<ServiceConnection> = ArrayList()
    private val mObserverKeySet = ArrayList<String>()
    private var mJobs: ArrayList<Job> = ArrayList()

    private var mDialog: MaterialDialog? = null

    fun addJob(job: Job) {
        mJobs.add(job)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = activity as Activity
        mParentView = mContext.getView(getParentViewId(), container)
        return mParentView
    }

    override fun onDestroy() {
        mObserverKeySet.forEach { ObserverManager.INSTANCE.delete(it) }
        mJobs.forEach {
            it.cancel()
        }
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    protected abstract fun getParentViewId(): Int

    protected abstract fun initView()

    fun service(init: ConnectionAction.() -> Unit) {
        val connection = ConnectionAction()
        connection.init()
        val intent = mContext.getIntent<NormalService>()
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
        mContext.bindService(intent, mConnections[mConnections.size - 1], Context.BIND_AUTO_CREATE)
    }

    fun addObserver(key: String, block: ((Array<out Any?>) -> Unit)? = null) {
        mObserverKeySet.add(key)
        ObserverManager.INSTANCE.add(key, block)
    }

    fun setLoadComplete(mRefreshLayout: SmartRefreshLayout) {
        setLoadComplete(mRefreshLayout, 0)
    }

    /**
     * 加载结束
     *
     * @param mRefreshLayout
     * @param mode           0是刷新  1是加载更多
     */
    fun setLoadComplete(mRefreshLayout: SmartRefreshLayout?, mode: Int) {
        try {
            if (mRefreshLayout != null) {
                if (mode == 0)
                    mRefreshLayout.finishRefresh()
                else
                    mRefreshLayout.finishLoadmore()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 设置列表数据空的布局
     */
    fun setEmptyLayout(emptyLayout: EmptyLayout?, listSize: Int) {
        if (emptyLayout != null)
            if (listSize == 0) {
                emptyLayout.showEmpty()
            } else
                emptyLayout.showSuccess()
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

    private fun showProgressbarDialog(content: String, resId: Int = R.layout.layout_loading) {
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
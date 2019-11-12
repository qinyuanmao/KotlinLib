package com.wisesoft.android.kotlinlib.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import net.wisesoft.android.kotlinlib.R


/**
 * 各种状态控制显示
 */
open class EmptyLayout @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(mContext, attrs, defStyleAttr), View.OnClickListener {
    private var mBindView: View? = null//绑定的布局
    private var mEmptyView: View? = null// 空布局
    private var mErrorView: View? = null// 加载错误
    private var mLoadingView: View? = null// 加载布局
    private var mNoNetwork: View? = null//无网络布局

    private var tvLoad: TextView? = null
    private val tvError: TextView? = null
    private var tvEmpty: TextView? = null
    private var tvNoNetwork: TextView? = null

    private var mLisenter: OnRetryLisenter? = null

    init {

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        //居中
        params.gravity = Gravity.CENTER

        val ta = mContext.obtainStyledAttributes(attrs, R.styleable.EmptyLayout, 0, 0)

        //数据为空时的布局
        val emptyLayout = ta.getResourceId(R.styleable.EmptyLayout_elEmptyLayout, R.layout.layout_empty)
        mEmptyView = View.inflate(mContext, emptyLayout, null)
        if (emptyLayout == R.layout.layout_empty)
            tvEmpty = mEmptyView!!.findViewById(R.id.tvEmptyText) as TextView
        addView(mEmptyView, params)

        //加载中的布局
        val loadingLayout = ta.getResourceId(R.styleable.EmptyLayout_elLoadingLayout, R.layout.layout_loading)
        mLoadingView = View.inflate(mContext, loadingLayout, null)
        if (loadingLayout == R.layout.layout_loading)
            tvLoad = mLoadingView!!.findViewById(R.id.tvContent) as TextView
        addView(mLoadingView, params)

        val noNetworkLayout = ta.getResourceId(R.styleable.EmptyLayout_elNoNetwork, R.layout.layout_not_network)
        mNoNetwork = View.inflate(mContext, noNetworkLayout, null)
        if (noNetworkLayout == R.layout.layout_not_network)
            tvNoNetwork = mNoNetwork!!.findViewById(R.id.tvNoNetworkText) as TextView
        addView(mNoNetwork, params)

        //错误时的布局
        val errorLayout = ta.getResourceId(R.styleable.EmptyLayout_elErrorLayout, R.layout.layout_error)
        mErrorView = View.inflate(mContext, errorLayout, null)
        //        if (errorLayout == R.layout.layout_error)
        //            tvError = (TextView) mErrorView.findViewById(R.id.tvErrorTxt);
        addView(mErrorView, params)

        mEmptyView!!.setOnClickListener(this)
        mErrorView!!.setOnClickListener(this)
        mNoNetwork!!.setOnClickListener(this)
        //全部隐藏
        setGone()


    }

    /**
     * TODO: 设置空数据布局
     *
     * @param resId
     */
    fun setEmptyView(resId: Int) {
        setEmptyView(View.inflate(mContext, resId, null))
    }

    /**
     * TODO: 设置空数据布局
     */
    fun setEmptyView(v: View) {
        if (indexOfChild(mEmptyView) != -1) {
            removeView(mEmptyView)
        }
        mEmptyView = v
        addView(mEmptyView)
        setGone()
    }

//    /**
//     * TODO: 设置加载错误布局
//     *
//     * @param layoutId
//     */
//    fun setErrorView(layoutId: Int) {
//        setLoadingView(View.inflate(mContext, layoutId, null))
//    }

    /**
     * TODO: 设置加载错误布局
     *
     * @param errorView
     */
    fun setErrorView(errorView: View) {
        if (indexOfChild(mErrorView) != -1) {
            removeView(mErrorView)
        }
        mErrorView = errorView
        addView(mLoadingView)
        setGone()
    }

//    /**
//     * TODO: 设置无网络布局
//     *
//     * @param layoutId
//     */
//    fun setNoNetworkView(layoutId: Int) {
//        setLoadingView(View.inflate(mContext, layoutId, null))
//    }

    /**
     * TODO: 设置无网络布局
     *
     * @param view
     */
    fun setNoNetworkView(view: View) {
        if (indexOfChild(mNoNetwork) != -1) {
            removeView(mNoNetwork)
        }
        mNoNetwork = view
        addView(mNoNetwork)
        setGone()
    }

    fun bindView(view: View) {
        mBindView = view
    }

    fun showEmpty(empty: String) {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        if (tvEmpty != null)
            tvEmpty!!.text = empty
        mEmptyView!!.visibility = View.VISIBLE
    }

    fun showEmpty() {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        mEmptyView!!.visibility = View.VISIBLE
    }

    fun showError(error: String) {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        if (tvError != null)
            tvError.text = error
        mErrorView!!.visibility = View.VISIBLE
    }

    fun showError() {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        mErrorView!!.visibility = View.VISIBLE
    }

    fun showLoading() {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        mLoadingView!!.visibility = View.VISIBLE
    }

    fun showLoading(loadStr: String) {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        if (tvLoad != null)
            tvLoad!!.text = loadStr
        mLoadingView!!.visibility = View.VISIBLE
    }

    fun showNoNetwork() {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        mNoNetwork!!.visibility = View.VISIBLE
    }

    fun showNoNetwork(noNetworkStr: String) {
        if (mBindView != null)
            mBindView!!.visibility = View.GONE
        setGone()
        if (tvNoNetwork != null)
            tvNoNetwork!!.text = noNetworkStr
        mNoNetwork!!.visibility = View.VISIBLE
    }

    /**
     * 全部隐藏
     */
    private fun setGone() {
        mEmptyView!!.visibility = View.GONE
        mErrorView!!.visibility = View.GONE
        mLoadingView!!.visibility = View.GONE
        mNoNetwork!!.visibility = View.GONE
    }

    fun showSuccess() {
        if (mBindView != null)
            mBindView!!.visibility = View.VISIBLE
        setGone()
    }

    fun setOnRetryLisenter(lisenter: OnRetryLisenter) {
        mLisenter = lisenter
    }

    override fun onClick(view: View) {
        if (mLisenter != null) {
            mLisenter!!.onRetry()
        }
    }

    interface OnRetryLisenter {
        fun onRetry()
    }

}

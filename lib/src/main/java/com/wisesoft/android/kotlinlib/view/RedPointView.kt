package com.wisesoft.android.kotlinlib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.wisesoft.android.kotlinlib.utils.getOneDpi
import net.wisesoft.android.kotlinlib.R

/**
 * @Title:  ${file_name}
 * @Package ${package_name}
 * @Description:    ${todo}
 * @date Create on 2018/12/13 16:06.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class RedPointView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mText = ""
    private var mCount = 0
    private var mRadius = 0F
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBounds = Rect()
    private var mTextSize = 0F

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.RedPointView)
        mCount = array.getInt(R.styleable.RedPointView_count, 0)
        mRadius = array.getDimension(R.styleable.RedPointView_radius, context.getOneDpi() * 10)
        mTextSize = array.getDimension(R.styleable.RedPointView_textSize, 12 * context.getOneDpi())
        mText = if (mCount > 99) "$mCount+" else "$mCount"
        array.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mCount <= 0) {
            visibility = GONE
        } else {
            visibility = VISIBLE
            mPaint.color = Color.RED
            canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mRadius, mPaint)
            mPaint.color = Color.WHITE
            mPaint.textSize = mTextSize
            mPaint.getTextBounds(mText, 0, mText.length, mBounds)
            val textWidth = mBounds.width()
            val textHeight = mBounds.height()
            canvas?.drawText(
                mText,
                (width / 2 - textWidth / 2).toFloat(),
                (height / 2 + textHeight / 2).toFloat(),
                mPaint
            )
        }
    }

    fun setCount(count: Int) {
        mCount = count
        mText = if (mCount > 99) "99+" else "$mCount"
        visibility = if (mCount <= 0) {
            GONE
        } else {
            VISIBLE
        }
        invalidate()
    }
}
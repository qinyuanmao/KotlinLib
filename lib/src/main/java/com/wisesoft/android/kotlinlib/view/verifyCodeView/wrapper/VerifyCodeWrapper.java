package com.wisesoft.android.kotlinlib.view.verifyCodeView.wrapper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by yangmin
 * on 2019/6/25.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
public interface VerifyCodeWrapper {
    /**
     * whether the wrapper will be covered by verify code
     *
     * @return true: wrapper will be covered by verify code
     * false: wrapper and verify code will display together
     * Wrapper for VerifyCodeView, customize your own wrapper appearance by implementing this interface
     * Created by gongw on 2018/10/19.
     */
    boolean isCovered();

    /**
     * here you can draw your wrapper for VerifyCodeView
     *
     * @param canvas    canvas to draw wrapper
     * @param paint     paint to draw wrapper
     * @param rectF     outer boundary of every verify code
     * @param textRectF boundary of verify code text
     */
    void drawWrapper(Canvas canvas, Paint paint, RectF rectF, RectF textRectF);
}

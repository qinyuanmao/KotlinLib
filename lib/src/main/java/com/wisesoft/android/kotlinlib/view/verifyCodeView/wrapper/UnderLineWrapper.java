package com.wisesoft.android.kotlinlib.view.verifyCodeView.wrapper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 下划线样式 VerifyCodeView
 * Created by yangmin
 * on 2019/6/25.
 * email:yangmin_xaht@163.com
 * qq：157715848
 */
public class UnderLineWrapper implements VerifyCodeWrapper {

    @Override
    public boolean isCovered() {
        //the under line and verify code will display together
        return false;
    }

    @Override
    public void drawWrapper(Canvas canvas, Paint paint, RectF rectF, RectF textRectF) {
        //make under line width always twice of text width
        canvas.drawLine(textRectF.left - textRectF.width() / 2, rectF.bottom, textRectF.right + textRectF.width() / 2, rectF.bottom, paint);
    }
}

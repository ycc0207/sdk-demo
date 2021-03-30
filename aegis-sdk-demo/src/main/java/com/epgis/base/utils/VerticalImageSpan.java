package com.epgis.base.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * VerticalImageSpan实现垂直居中显示图片
 */
public class VerticalImageSpan extends ImageSpan {

    private int mFontHeight;
    private int mOffsetY;

    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
    }

    public VerticalImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            mFontHeight = fontMetricsInt.bottom - fontMetricsInt.top;
            final int drawableHalfHeight = (rect.bottom - rect.top) / 2;
            final int fontHalfHeight = (fontMetricsInt.bottom - fontMetricsInt.top) / 2;
            final int fontCenter = fontMetricsInt.top + fontHalfHeight;
            fontMetricsInt.ascent = fontCenter - drawableHalfHeight;
            fontMetricsInt.descent = fontCenter + drawableHalfHeight;
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = fontMetricsInt.descent;

            mOffsetY = fontHalfHeight - drawableHalfHeight;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int height = bottom - top;
        int transY = top + mOffsetY - (mFontHeight - height + 1) / 2;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}

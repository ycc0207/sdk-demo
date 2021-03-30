package com.epgis.search.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SearchEditText extends android.support.v7.widget.AppCompatEditText {

    private Drawable clearDrawable;

    public SearchEditText(Context context) {
        super(context);
        initAttrs();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    private void initAttrs() {
        //clearDrawable = getResources().getDrawable(R.drawable.search_input_clean);

        setCompoundDrawablesWithIntrinsicBounds(null, null,
                null, null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        setClearIconVisible(hasFocus() && text.length() > 0);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setClearIconVisible(focused && length() > 0);
    }

    private void setClearIconVisible(boolean visible) {
//        setCompoundDrawablesWithIntrinsicBounds(null, null,
//                visible ? clearDrawable : null, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Drawable drawable = clearDrawable;
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    setText("");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
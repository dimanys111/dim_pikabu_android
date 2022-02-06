package com.imagezoom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageViewTouch extends ImageViewTouchBase {

    public ImageViewTouch(Context context) {
        super(context);
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewTouch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        performClick();
        return false;
    }
}

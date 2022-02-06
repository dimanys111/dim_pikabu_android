package com.dima.pikabu.src;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup;


public class DrawView extends AppCompatImageView {

    PostItem.Blok blok;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, PostItem.Blok blok_)
    {
        super(context);
        blok=blok_;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        if(d!=null && d.getIntrinsicHeight()>0){
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());

            if(blok.height!=height){
                blok.height=height;
                ViewGroup.LayoutParams p = getLayoutParams();
                p.height=height;
                setLayoutParams(p);
            }
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
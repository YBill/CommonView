package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Bill on 18/5/21.
 * RelativeLayout 宽高动态按比例绘制
 */
public class AutoScaleRelativeLayout extends RelativeLayout {

    private float width_relative_layout_width_aspect_ratio = -1; // 宽相对于屏幕宽的占比
    private float height_relative_width_aspect_ratio = -1; // 高相对于宽的占比

    public AutoScaleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public AutoScaleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoScaleRelativeLayout(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleView);
        width_relative_layout_width_aspect_ratio = a.getFloat(R.styleable.AutoScaleView_width_relative_layout_width_aspect_ratio, -1);
        height_relative_width_aspect_ratio = a.getFloat(R.styleable.AutoScaleView_height_relative_width_aspect_ratio, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        float height;
        float width = getMeasuredWidth();
        if (width_relative_layout_width_aspect_ratio > 0)
            width = width * width_relative_layout_width_aspect_ratio;
        if (height_relative_width_aspect_ratio > 0) {
            height = width * height_relative_width_aspect_ratio;
        } else {
            height = getMeasuredHeight();
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

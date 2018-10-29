package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by Bill on 18/5/21.
 * 图片高按宽比例
 */
public class AutoScaleImageView extends ShapedImageView {

    private float aspect_ratio = 1;

    public AutoScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public AutoScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoScaleImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleImageView);
        aspect_ratio = a.getFloat(a.getIndex(R.styleable.AutoScaleImageView_height_aspect_ratio), 1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int width = getMeasuredWidth();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width * aspect_ratio), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

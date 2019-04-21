package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Bill on 18/7/3.
 * <p>
 * 实现LinearLayout中的View为正方形
 * 使用时布局layout_width必须为match_parent或具体数值
 */
public class HorizontalSquareLayout extends LinearLayout {

    private float square_interval; // view中间间隔，要减去这个值，要不然不是个正方形

    public HorizontalSquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public HorizontalSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HorizontalSquareLayout(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalSquareLayout);
        square_interval = a.getDimension(R.styleable.HorizontalSquareLayout_square_interval, 0);
        a.recycle();

        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        int square_num = getChildCount();

        float height = (getMeasuredWidth() - 2 * square_interval) / 3;

        int width = (int) (square_num * height + square_interval * (square_num - 1));

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
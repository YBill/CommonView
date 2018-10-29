package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;

import java.util.Arrays;

/**
 * Created by Bill on 18/5/21.
 * <p>
 * 可以指定某个角进行圆角
 * 可以指定圆和圆角图片描边
 * 描边只能是圆或为四个圆角描边角度一样
 */
public class ShapedImageView extends android.support.v7.widget.AppCompatImageView {

    @IntDef({ShapedMode.SHAPE_MODE_NONE, ShapedMode.SHAPE_MODE_ROUND_RECT, ShapedMode.SHAPE_MODE_CIRCLE})
    public @interface ShapedMode {
        int SHAPE_MODE_NONE = 0;
        int SHAPE_MODE_ROUND_RECT = 1;
        int SHAPE_MODE_CIRCLE = 2;
    }

    public static final float DEFAULT_RADIUS = 0f;

    private Paint mPaint;
    private Shape mShape;
    @ShapedMode
    private int mShapeMode = 0;
    private final float[] mCornerRadius = new float[8];

    private Paint strokePaint;
    private float mBorderWidth; //描边的宽度
    private int mBorderColor; //描边颜色

    public ShapedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ShapedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ShapedImageView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShapedImageView);
            mShapeMode = a.getInt(R.styleable.ShapedImageView_shape_mode, ShapedMode.SHAPE_MODE_NONE);
            mBorderWidth = a.getDimension(R.styleable.ShapedImageView_img_border_width, 0);
            mBorderColor = a.getColor(R.styleable.ShapedImageView_img_border_color, 0xffffffff);
            switch (mShapeMode) {
                case ShapedMode.SHAPE_MODE_ROUND_RECT:
                    float mRadius = a.getDimension(R.styleable.ShapedImageView_round_radius, -1);
                    if (mRadius != -1) {
                        Arrays.fill(mCornerRadius, mRadius);
                    } else {
                        float leftTop = a.getDimension(R.styleable.ShapedImageView_round_radius_left_top, DEFAULT_RADIUS);
                        float rightTop = a.getDimension(R.styleable.ShapedImageView_round_radius_right_top, DEFAULT_RADIUS);
                        float rightBottom = a.getDimension(R.styleable.ShapedImageView_round_radius_right_bottom, DEFAULT_RADIUS);
                        float leftBottom = a.getDimension(R.styleable.ShapedImageView_round_radius_left_bottom, DEFAULT_RADIUS);
                        mCornerRadius[0] = leftTop;
                        mCornerRadius[1] = leftTop;
                        mCornerRadius[2] = rightTop;
                        mCornerRadius[3] = rightTop;
                        mCornerRadius[4] = rightBottom;
                        mCornerRadius[5] = rightBottom;
                        mCornerRadius[6] = leftBottom;
                        mCornerRadius[7] = leftBottom;
                    }
                    break;
            }
            a.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // 描边
        if (mBorderWidth > 0) {
            strokePaint = new Paint();
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(mBorderColor);
            strokePaint.setAntiAlias(true);
            strokePaint.setStrokeWidth(mBorderWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mShapeMode) {
            case ShapedMode.SHAPE_MODE_NONE:
                super.onDraw(canvas);
                break;
            case ShapedMode.SHAPE_MODE_ROUND_RECT:
            case ShapedMode.SHAPE_MODE_CIRCLE:
                int saveCount = canvas.getSaveCount();
                canvas.save();
                super.onDraw(canvas);
                if (mShape != null) {
                    mShape.draw(canvas, mPaint);
                }
                canvas.restoreToCount(saveCount);
                break;
        }

        drawBorder(canvas, this.getWidth(), this.getHeight());
    }

    /**
     * 描边
     *
     * @param canvas
     * @param width  图片宽
     * @param height 图片高
     */
    private void drawBorder(Canvas canvas, float width, float height) {
        if (mBorderWidth <= 0) {
            return;
        }

        if (mShapeMode == ShapedMode.SHAPE_MODE_CIRCLE) {
            float min = Math.min(getWidth(), getHeight());
            float center = min / 2; // 圆心
            float radius = (min - mBorderWidth) / 2; // 半径
            canvas.drawCircle(center, center, radius, strokePaint);
        } else {
            float indent = mBorderWidth / 2; // 缩进线的一半，要不线会绘制一半
            RectF targetRect = new RectF(0, 0, width, height);
            targetRect.inset(indent, indent);
            canvas.drawRoundRect(targetRect, mCornerRadius[0] - indent, mCornerRadius[0] - indent, strokePaint);
        }

        canvas = null;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if (mShapeMode == ShapedMode.SHAPE_MODE_CIRCLE) {
                int min = Math.min(getWidth(), getHeight());
                float radius = (float) min / 2;
                Arrays.fill(mCornerRadius, radius);
            }
            if (mShapeMode != ShapedMode.SHAPE_MODE_NONE) {
                if (mShape == null) {
                    mShape = new RoundRectShape(mCornerRadius, null, null);
                }
                mShape.resize(getWidth(), getHeight());
            }
        }
    }

}
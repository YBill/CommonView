package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import java.util.Arrays;

/**
 * Created by Bill on 2019/1/26.
 * Describe ：通过BitmapShader画圆形圆角
 * 描边角度跟随圆角走
 * scaleType 仅对fitXY和centerCrop做了处理
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    @ImageType
    private int mShapeMode = 0;

    private static final float DEFAULT_RADIUS = 0f;

    private BitmapShader mBitmapShader; // 当前image的shader
    private Matrix mMatrix; // shader的变换矩阵，当图片大小和imageView大小不一样是放大或缩小shader
    private Paint mBitmapPaint; // 画笔
    private RectF mRoundRect; // ImageView的大小
    private Path mImagePath; // path设置圆角
    private final float[] mCornerRadius = new float[8]; // 图片的圆角

    // 描边
    private Paint mBorderPaint;
    private float mBorderWidth; //描边的宽度
    private int mBorderColor; //描边颜色
    private RectF mBorderRect;
    private Path mBorderPath;
    private float[] mBorderRadius = new float[8];
    private float indent; // 缩进，画描边

    public RoundImageView(Context context) {
        super(context);
        init(null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ShapedImageView);
            mShapeMode = a.getInt(R.styleable.ShapedImageView_shape_mode, ImageType.MODE_NONE);
            mBorderWidth = a.getDimension(R.styleable.ShapedImageView_img_border_width, 0);
            mBorderColor = a.getColor(R.styleable.ShapedImageView_img_border_color, 0xffffffff);
            switch (mShapeMode) {
                case ImageType.MODE_ROUND_RECT:
                    float mRadius = a.getDimension(R.styleable.ShapedImageView_round_radius, -1);
                    if (mRadius != -1) {
                        Arrays.fill(mCornerRadius, mRadius);
                    } else {
                        float leftTop = a.getDimension(R.styleable.ShapedImageView_round_radius_left_top, DEFAULT_RADIUS);
                        float rightTop = a.getDimension(R.styleable.ShapedImageView_round_radius_right_top, DEFAULT_RADIUS);
                        float rightBottom = a.getDimension(R.styleable.ShapedImageView_round_radius_right_bottom, DEFAULT_RADIUS);
                        float leftBottom = a.getDimension(R.styleable.ShapedImageView_round_radius_left_bottom, DEFAULT_RADIUS);
                        mCornerRadius[0] = mCornerRadius[1] = leftTop;
                        mCornerRadius[2] = mCornerRadius[3] = rightTop;
                        mCornerRadius[4] = mCornerRadius[5] = rightBottom;
                        mCornerRadius[6] = mCornerRadius[7] = leftBottom;
                    }
                    break;
            }
            a.recycle();
        }

        if (mShapeMode != ImageType.MODE_NONE) {
            mRoundRect = new RectF();
            mImagePath = new Path();
            mMatrix = new Matrix();
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
        }

        // 描边
        if (mBorderWidth > 0) {
            mBorderPath = new Path();
            mBorderRect = new RectF();
            mBorderPaint = new Paint();
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            indent = mBorderWidth / 2; // 缩进线的一半，要不线会绘制一半
            for (int i = 0; i < mCornerRadius.length; i++) {
                mBorderRadius[i] = mCornerRadius[i] - indent;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mShapeMode == ImageType.MODE_CIRCLE) {
            int mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShapeMode == ImageType.MODE_NONE) {
            super.onDraw(canvas);
        } else {
            if (getDrawable() == null) {
                return;
            }

            setUpShader();

            if (mShapeMode == ImageType.MODE_ROUND_RECT) {
                mRoundRect.set(0, 0, getWidth(), getHeight());
                mImagePath.addRoundRect(mRoundRect, mCornerRadius, Path.Direction.CCW);
                canvas.drawPath(mImagePath, mBitmapPaint);
            } else {
                float mRadius = getWidth() / 2;
                canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
            }
        }

        drawBorder(canvas, this.getWidth(), this.getHeight());
    }

    private void drawBorder(Canvas canvas, float width, float height) {
        if (mBorderWidth <= 0) {
            return;
        }

        if (mShapeMode == ImageType.MODE_CIRCLE) {
            float imgSize = getWidth(); // 圆的宽=高
            float center = imgSize / 2; // 圆心
            float radius = (imgSize - mBorderWidth) / 2; // 半径
            canvas.drawCircle(center, center, radius, mBorderPaint);
        } else {
            mBorderRect.set(0, 0, width, height);
            mBorderRect.inset(indent, indent);
            mBorderPath.addRoundRect(mBorderRect, mBorderRadius, Path.Direction.CCW);
            canvas.drawPath(mBorderPath, mBorderPaint);
        }

        canvas = null;
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bitmap = drawableToBitmap(drawable);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        int viewWidth = getWidth();
        int viewHeight = getHeight();


        float scale = 1.0f;
        float dx = 0;
        float dy = 0;

        if (mShapeMode == ImageType.MODE_CIRCLE) {
            int bSize = Math.min(imgWidth, imgHeight);
            scale = viewWidth * 1.0f / bSize;
        } else if (mShapeMode == ImageType.MODE_ROUND_RECT) {
            scale = Math.max(viewWidth * 1.0f / imgWidth, viewHeight * 1.0f / imgHeight);
        }

        if (ScaleType.FIT_XY == getScaleType()) {
            float scaleX = viewWidth * 1.0f / imgWidth;
            float scaleY = viewHeight * 1.0f / imgHeight;
            mMatrix.setScale(scaleX, scaleY);
        } else {
            // ScaleType.CENTER_CROP
            if (imgWidth * viewHeight > viewWidth * imgHeight) {
                dx = (viewWidth - imgWidth * scale) * 0.5f;
            } else {
                dy = (viewHeight - imgHeight * scale) * 0.5f;
            }
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        }

        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE
                ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

}

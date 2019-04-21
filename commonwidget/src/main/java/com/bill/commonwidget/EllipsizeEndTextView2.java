package com.bill.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Bill on 2019/2/25.
 * Describe ：正文末尾添加全文按钮，可展开，没动画
 */

public class EllipsizeEndTextView2 extends android.support.v7.widget.AppCompatTextView {

    private static final char[] ELLIPSIS_NORMAL = {'\u2026'}; // this is "..."
    private static final String ELLIPSIS_STRING = new String(ELLIPSIS_NORMAL);
    private static final String COLLAPSING_TEXT = "展开";
    private static final String COLLAPSING_COLOR = "#63779C";
    private static final int MAX_LINE = 5;

    private String collapsingText;
    private String collapsingColor;
    private int maxLine;
    private boolean isExpand = false;
    private OnExpandChangeListener mListener;

    private String realText = "";
    private boolean isClickHandled; // 点击展开，整个TextView的点击事件也会响应
    private OnTextClickListener textClickListener;

    public EllipsizeEndTextView2(Context context) {
        this(context, null);
    }

    public EllipsizeEndTextView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizeEndTextView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.EllipsizeEndTextView);
            setExpandedText(typed.getString(R.styleable.EllipsizeEndTextView_collapsingText));
            setExpandedColor(typed.getString(R.styleable.EllipsizeEndTextView_collapsingColor));
            setMaxLine(typed.getInt(R.styleable.EllipsizeEndTextView_maxLine, MAX_LINE));
            typed.recycle();
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickHandled) {
                    isClickHandled = false;
                    return;
                }

                if (textClickListener != null)
                    textClickListener.onClick(v);
            }
        });

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isClickHandled) {
                    isClickHandled = false;
                    return true;
                }
                if (textClickListener != null)
                    textClickListener.onLongClick(v);
                return true;
            }
        });

        this.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setExpandedText(String expandedText) {
        this.collapsingText = TextUtils.isEmpty(expandedText) ? COLLAPSING_TEXT : expandedText;
        this.collapsingText += "##";
    }

    public void setExpandedColor(String expandedColor) {
        this.collapsingColor = TextUtils.isEmpty(expandedColor) ? COLLAPSING_COLOR : expandedColor;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    public void setText(String text, boolean isExpand, OnExpandChangeListener textClickListener) {
        this.realText = text;
        this.isExpand = isExpand;
        this.mListener = textClickListener;

        super.setText(text);
        requestLayout();
    }


    public void setRealText(String text) {
        this.realText = text;
        super.setText(text);
        requestLayout();
    }


    public String getRealText() {
        return realText;
    }

    public void setTextClickListener(OnTextClickListener textClickListener) {
        this.textClickListener = textClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isExpand) {
            return;
        }

        CharSequence text = getText();

        int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        TextPaint tp = getPaint();

        StaticLayout layout = new StaticLayout(text, tp, lineWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int count = layout.getLineCount();
        if (count > maxLine) {
            int start = layout.getLineStart(maxLine - 1);
            final int range[] = {0};
            TextUtils.ellipsize(text.subSequence(start, text.length()), tp, (lineWidth - tp.measureText(collapsingText)),
                    TextUtils.TruncateAt.END, false, new TextUtils.EllipsizeCallback() {
                        @Override
                        public void ellipsized(int start, int end) {
                            range[0] = start;
                        }
                    });
            int pos = start + range[0];

            String lastText = text.subSequence(start, pos).toString();
            if (lastText.contains("\n")) {
                lastText = lastText.replaceAll("\n", " ");
            }

            if (pos > 0) {
                String str = ELLIPSIS_STRING + collapsingText;
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(text.subSequence(0, start));
                builder.append(lastText);
                builder.append(str);
                int startLength = builder.length() - str.length();
                int endLength = builder.length();
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(collapsingColor)),
                        startLength, endLength - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new ImageSpan(getContext(), R.drawable.icon_expand_text),
                        endLength - 2, endLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                builder.setSpan(new ClickableSpan() {

                    @Override
                    public void updateDrawState(TextPaint ds) {
//                        super.updateDrawState(ds);
                    }

                    @Override
                    public void onClick(View view) {
                        isClickHandled = true;
                        isExpand = true;
                        if (mListener != null) {
                            mListener.onExpandStateChanged(isExpand);
                        }
                        setText(realText);
                    }
                }, startLength, endLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                setText(builder);

                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

        } else {
            setText(text);
        }

    }

    public interface OnTextClickListener {
        void onClick(View view);

        void onLongClick(View view);
    }

    public interface OnExpandChangeListener {
        void onExpandStateChanged(boolean isExpand);
    }
}
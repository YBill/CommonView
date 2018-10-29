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
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

/**
 * Created by Bill on 2018/9/21.
 */

public class EllipsizeEndTextView extends android.support.v7.widget.AppCompatTextView {

    private static final char[] ELLIPSIS_NORMAL = {'\u2026'}; // this is "..."
    private static final String ELLIPSIS_STRING = new String(ELLIPSIS_NORMAL);
    private static final String COLLAPSING_TEXT = "全文";
    private static final String COLLAPSING_COLOR = "#457ae6";
    private static final int MAX_LINE = 4;

    private String collapsingText;
    private String collapsingColor;
    private int maxLine;

    public EllipsizeEndTextView(Context context) {
        this(context, null);
    }

    public EllipsizeEndTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizeEndTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    }

    public void setExpandedText(String expandedText) {
        this.collapsingText = TextUtils.isEmpty(expandedText) ? COLLAPSING_TEXT : expandedText;
    }

    public void setExpandedColor(String expandedColor) {
        this.collapsingColor = TextUtils.isEmpty(expandedColor) ? COLLAPSING_COLOR : expandedColor;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

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
                builder.setSpan(new ForegroundColorSpan(Color.parseColor(collapsingColor)),
                        builder.length() - collapsingText.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                setText(builder);

                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

        } else {
            setText(text);
        }

    }

}
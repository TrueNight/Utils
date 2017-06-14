package xyz.truenight.utils.view.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

public class AutoScaledTextView extends AppCompatTextView {

    float textSize;
    private Rect rect = new Rect();

    public AutoScaledTextView(Context context) {
        super(context);
        textSize = getTextSize();
        initMaxLines();
    }

    private void initMaxLines() {
        if (getMaxLines() == Integer.MAX_VALUE) {
            setMaxLines(1);
        }
    }

    public AutoScaledTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textSize = getTextSize();
        initMaxLines();
    }

    public AutoScaledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textSize = getTextSize();
        initMaxLines();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        rect.left = left;
        rect.top = top;
        rect.right = right;
        rect.bottom = bottom;

        setScaledTextSize(rect.width(), rect.height());
    }

    private void setScaledTextSize(int width, int height) {
        float maxTextWidth = width - getPaddingLeft() - getPaddingRight();
        float maxTextHeight = height - getPaddingTop() - getPaddingBottom();
        float textSize = this.textSize;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setTypeface(getTypeface());
        final String string = getText().toString();
        float textSizeByWidth = Integer.MAX_VALUE;
        float measuredWidth = paint.measureText(string);
        if ((measuredWidth / getMaxLines()) > maxTextWidth) {
            textSizeByWidth = textSize * (maxTextWidth / (measuredWidth / getMaxLines()));
        }
        float textSizeByHeight = Integer.MAX_VALUE;
        float measuredHeight = paint.getFontSpacing();
        if (measuredHeight * getMaxLines() > maxTextHeight) {
            textSizeByHeight = textSize * (maxTextHeight / (measuredHeight * getMaxLines()));
        }

        textSize = Math.min(textSize, Math.min(textSizeByHeight, textSizeByWidth));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
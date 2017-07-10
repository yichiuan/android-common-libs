package com.yichiuan.common.bottomphotopicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.yichiuan.common.R;

public class CheckView extends View {

    private static float CIRCLE_STROKE_WIDTH = 8.0f;

    private Drawable checkedDrawable;

    private Paint paint;

    private boolean isChecked = false;

    public CheckView(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        Resources.Theme theme = getContext().getTheme();
        checkedDrawable = VectorDrawableCompat.create(getResources(),
                R.drawable.ic_check_black_24dp, theme);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
    }

    public boolean checked() {
        return isChecked;
    }

    public void setCheck(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int contentWidth = getWidth();
        int contentHeight = getHeight();

        canvas.drawCircle(contentWidth * 0.5f, contentHeight * 0.5f,
                contentWidth * 0.5f - CIRCLE_STROKE_WIDTH * 0.5f, paint);

        if (isChecked) {
            if (checkedDrawable != null) {
                checkedDrawable.setBounds(0, 0, contentWidth, contentHeight);
                checkedDrawable.draw(canvas);
            }
        }
    }
}

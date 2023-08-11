package com.prudhvi.spinwheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class LuckySpinWheelView extends View {

    private Paint paint;
    private RectF rectF;
    private List<LuckyItem> segments;

    private float currentAngle = 0;

    public LuckySpinWheelView(Context context) {
        super(context);
        init();
    }

    public LuckySpinWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        int centerX = width / 2;
        int centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float startAngle = 0;
        float sweepAngle = 360f / segments.size();

        for (int i = 0; i < segments.size(); i++) {
            paint.setColor(segments.get(i).c);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // draw segment text
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            String text = segments.get(i).t;
            float textWidth = paint.measureText(text);
            float textHeight = paint.descent() - paint.ascent();


            float angle = startAngle + sweepAngle / 2;
            float x = (float) (centerX + Math.cos(Math.toRadians(angle)) * radius / 2);
            float y = (float) (centerY + Math.sin(Math.toRadians(angle)) * radius / 2);
            canvas.drawText(text, x - textWidth / 2, y - textHeight / 2, paint);

            startAngle += sweepAngle;
        }
    }

    public String spinWheel(int indexData) {
        return segments.get(indexData).t;
    }

    public void setData(List<LuckyItem> data) {
        this.segments = data;
    }
}

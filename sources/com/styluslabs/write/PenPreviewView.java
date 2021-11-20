package com.styluslabs.write;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.Locale;

public class PenPreviewView extends View {
    public int penColor;
    public boolean penHighlight;
    public float penPressure;
    public float penWidth;

    public PenPreviewView(Context context) {
        this(context, null, 0);
    }

    public PenPreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PenPreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.penWidth = 0.0f;
        this.penColor = -1;
        this.penPressure = 0.0f;
        this.penHighlight = false;
    }

    public void setPenProps(int color, float width, float pressure, boolean highlight) {
        this.penColor = color;
        this.penWidth = width;
        this.penPressure = pressure;
        this.penHighlight = highlight;
    }

    public void setPenProps(String propstring) {
        boolean z = true;
        String[] props = propstring.split(",");
        try {
            this.penColor = Long.decode(props[0]).intValue();
            this.penWidth = Float.parseFloat(props[1]);
            this.penPressure = Float.parseFloat(props[2]);
            if (Integer.parseInt(props[3]) == 0) {
                z = false;
            }
            this.penHighlight = z;
        } catch (Exception e) {
        }
    }

    public String getPenProps() {
        int i = 1;
        Locale locale = Locale.US;
        Object[] objArr = new Object[4];
        objArr[0] = Integer.valueOf(this.penColor);
        objArr[1] = Float.valueOf(this.penWidth);
        objArr[2] = Float.valueOf(this.penPressure);
        if (!this.penHighlight) {
            i = 0;
        }
        objArr[3] = Integer.valueOf(i);
        return String.format(locale, "%d,%f,%f,%d", objArr);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawColor(-1);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(this.penWidth);
        paint.setColor(this.penColor);
        Path path = new Path();
        int w = getWidth();
        int h = getHeight();
        path.moveTo(0.1f * ((float) w), ((float) h) * 0.5f);
        path.cubicTo(0.3f * ((float) w), 0.2f * ((float) h), 0.7f * ((float) w), 0.8f * ((float) h), 0.9f * ((float) w), ((float) h) * 0.5f);
        canvas.drawPath(path, paint);
    }
}

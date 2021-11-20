package com.styluslabs.colorpicker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AlphaPatternDrawable extends Drawable {
    private Bitmap mBitmap;//res-drawable에서 이미지 불러옴
    private final Paint mPaint = new Paint();//안드로이드에서 페인트 그리
    private final Paint mPaintGray = new Paint();
    private final Paint mPaintWhite = new Paint();
    private int mRectangleSize = 10;//사각형 크기
    private int numRectanglesHorizontal;//수평
    private int numRectanglesVertical;//수직

    public AlphaPatternDrawable(int rectangleSize) {
        this.mRectangleSize = rectangleSize;
        this.mPaintWhite.setColor(-1);
        this.mPaintGray.setColor(-3421237);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, (Rect) null, getBounds(), this.mPaint);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawwable.");
    }

    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawwable.");
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        this.numRectanglesHorizontal = (int) Math.ceil((double) (bounds.width() / this.mRectangleSize));
        this.numRectanglesVertical = (int) Math.ceil((double) (height / this.mRectangleSize));
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        if (getBounds().width() > 0 && getBounds().height() > 0) {
            this.mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.mBitmap);
            Rect r = new Rect();
            boolean verticalStartWhite = true;
            for (int i = 0; i <= this.numRectanglesVertical; i++) {
                boolean isWhite = verticalStartWhite;
                for (int j = 0; j <= this.numRectanglesHorizontal; j++) {
                    r.top = this.mRectangleSize * i;
                    r.left = this.mRectangleSize * j;
                    r.bottom = r.top + this.mRectangleSize;
                    r.right = r.left + this.mRectangleSize;
                    canvas.drawRect(r, isWhite ? this.mPaintWhite : this.mPaintGray);
                    isWhite = !isWhite;
                }
                verticalStartWhite = !verticalStartWhite;
            }
        }
    }
}

package com.styluslabs.write;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class UndoToolButton extends ImageView {
    private static final int PRESSED_COLOR = -1090518785;
    private final float M_PI;
    private float downX;
    private float downY;
    private boolean isPopupOpened;
    private UndoRedoListener mListener;
    private final PopupWindow mPopupMenu;
    private final UndoDialView mUndoDialView;
    private float prevAngle;
    private float stepAngle;
    private boolean undoOnRelease;

    public interface UndoRedoListener {
        boolean canRedo();

        boolean canUndo();

        void redo();

        void undo();

        void undoRedoEnd(boolean z);
    }

    public class UndoDialView extends View {
        Paint mDialPaint;
        public float mIndAngle;
        Paint mIndPaint;

        public UndoDialView(UndoToolButton undoToolButton, Context context) {
            this(context, null, 0);
        }

        public UndoDialView(UndoToolButton undoToolButton, Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public UndoDialView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.mIndAngle = 0.0f;
            this.mDialPaint = new Paint(1);
            this.mDialPaint.setColor(-16777216);
            this.mDialPaint.setStyle(Paint.Style.STROKE);
            this.mDialPaint.setStrokeWidth(3.0f);
            this.mIndPaint = new Paint(1);
            this.mIndPaint.setColor(-16776961);
            this.mIndPaint.setStrokeWidth(3.0f);
        }

        public void setIndicatorActive(boolean active) {
            if (active) {
                this.mIndPaint.setColor(-16776961);
            } else {
                this.mIndPaint.setColor(-65536);
            }
        }

        public float getIndicatorAngle(float screenX, float screenY) {
            int[] location = new int[2];
            getLocationOnScreen(location);
            if (location[0] == 0 && location[1] == 0) {
                return 3.1415927f;
            }
            return -((float) Math.atan2((double) ((screenX - ((float) location[0])) - ((float) (getWidth() / 2))), (double) ((screenY - ((float) location[1])) - ((float) (getHeight() / 2)))));
        }

        public void setIndicatorAngle(float angle) {
            this.mIndAngle = angle;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = Math.min(Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec)), 200);
            setMeasuredDimension(size, size);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int a = getWidth() / 2;
            canvas.drawCircle((float) a, (float) a, (float) (a - 10), this.mDialPaint);
            canvas.drawCircle(((float) a) + (((float) (a - 10)) * FloatMath.sin(-this.mIndAngle)), ((float) a) + (((float) (a - 10)) * FloatMath.cos(-this.mIndAngle)), 4.0f, this.mIndPaint);
        }
    }

    public UndoToolButton(Context context) {
        this(context, null, 0);
    }

    public UndoToolButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UndoToolButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isPopupOpened = false;
        this.M_PI = 3.1415927f;
        this.prevAngle = 0.0f;
        this.stepAngle = 0.17453294f;
        this.undoOnRelease = false;
        this.downX = -1.0f;
        this.downY = -1.0f;
        setClickable(true);
        this.mUndoDialView = new UndoDialView(this, context, attrs);
        this.mPopupMenu = new PopupWindow(this.mUndoDialView, -2, -2);
        this.mPopupMenu.setAnimationStyle(0);
    }

    public void setListener(UndoRedoListener cb) {
        this.mListener = cb;
    }

    public void setDialSteps(int steps) {
        this.stepAngle = 6.2831855f / ((float) steps);
    }

    private void processDownEvent() {
        this.prevAngle = this.mUndoDialView.getIndicatorAngle(this.downX, this.downY);
        this.mUndoDialView.setIndicatorAngle(this.prevAngle);
        this.downX = -1.0f;
        this.downY = -1.0f;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        int action = event.getAction();
        if (this.isPopupOpened && this.downX >= 0.0f) {
            processDownEvent();
        }
        switch (action) {
            case 0:
                if (!this.isPopupOpened) {
                    this.mPopupMenu.showAtLocation(this, 0, ((int) event.getRawX()) - 100, ((int) event.getRawY()) + 30);
                    this.isPopupOpened = true;
                }
                this.downX = event.getRawX();
                this.downY = event.getRawY();
                this.mUndoDialView.setIndicatorAngle(3.1415927f);
                this.mUndoDialView.setIndicatorActive(true);
                this.undoOnRelease = true;
                setBackgroundColor(PRESSED_COLOR);
                return true;
            case 1:
                if (this.isPopupOpened) {
                    this.mPopupMenu.dismiss();
                    this.isPopupOpened = false;
                }
                this.mListener.undoRedoEnd(this.undoOnRelease);
                this.undoOnRelease = false;
                setBackgroundColor(0);
                return true;
            case 2:
                if (!this.isPopupOpened) {
                    return true;
                }
                float angle = this.mUndoDialView.getIndicatorAngle(event.getRawX(), event.getRawY());
                float deltaAngle = angle - this.prevAngle;
                if (deltaAngle > 3.1415927f) {
                    deltaAngle -= 6.2831855f;
                } else if (deltaAngle < -3.1415927f) {
                    deltaAngle += 6.2831855f;
                }
                int delta = (int) (deltaAngle / this.stepAngle);
                if (delta == 0) {
                    return true;
                }
                this.mUndoDialView.setIndicatorAngle(angle);
                this.undoOnRelease = false;
                this.prevAngle = ((this.prevAngle + (((float) delta) * this.stepAngle)) % 2.0f) * 3.1415927f;
                while (delta > 0 && this.mListener.canRedo()) {
                    this.mListener.redo();
                    delta--;
                }
                while (delta < 0 && this.mListener.canUndo()) {
                    this.mListener.undo();
                    delta++;
                }
                this.prevAngle = angle;
                UndoDialView undoDialView = this.mUndoDialView;
                if (delta == 0) {
                    z = true;
                }
                undoDialView.setIndicatorActive(z);
                return true;
            default:
                return false;
        }
    }
}

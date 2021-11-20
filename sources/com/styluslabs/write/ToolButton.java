package com.styluslabs.write;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ToolButton extends ImageView implements View.OnClickListener, PopupWindow.OnDismissListener {
    private static final int BUTTON_ACTIVE_COLOR = 1593835775;
    private static final int BUTTON_LOCKED_COLOR = -1090518785;
    private static final int PRESSED_COLOR = -1090518785;
    private static final int SELECTED_COLOR = 1593835775;
    private View activeMenuView;
    private boolean allowSelection;
    private final LayoutInflater inflater;
    private boolean isPopupOpened;
    private final Context mContext;
    private ViewSelectedListener mListener;
    private ViewGroup mMenuLayout;
    private PopupWindow mPopupMenu;
    private boolean pressOpen;
    private View selectedView;

    public interface ViewSelectedListener {
        void viewPressed(ToolButton toolButton, boolean z, boolean z2);

        void viewSelected(ToolButton toolButton, View view);
    }

    public ToolButton(Context context) {
        this(context, null, 0);
    }

    public ToolButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.pressOpen = true;
        this.isPopupOpened = false;
        this.allowSelection = false;
        this.activeMenuView = null;
        this.selectedView = null;
        this.mContext = context;
        setClickable(true);
        this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public void setListener(ViewSelectedListener cb, boolean pressopen) {
        this.mListener = cb;
        this.pressOpen = pressopen;
    }

    public void addMenuItem(int iconres, int titleid, Integer tag) {
        ensureMenuLayout();
        ViewGroup row = (ViewGroup) this.inflater.inflate(R.layout.menu_row, (ViewGroup) null);
        row.setId(-1);
        row.setTag(tag);
        row.setOnClickListener(this);
        ((ImageView) row.getChildAt(0)).setImageResource(iconres);
        ((TextView) row.getChildAt(1)).setText(titleid);
        this.mMenuLayout.addView(row);
    }

    public PenPreviewView addPenPreview(Integer tag) {
        ensureMenuLayout();
        ViewGroup row = (ViewGroup) this.inflater.inflate(R.layout.pen_preview_row, (ViewGroup) null);
        row.setId(-1);
        row.setTag(tag);
        row.setOnClickListener(this);
        ((ImageView) row.getChildAt(0)).setImageResource(R.drawable.ic_menu_draw);
        this.mMenuLayout.addView(row);
        return (PenPreviewView) row.getChildAt(1);
    }

    private void ensureMenuLayout() {
        if (this.mMenuLayout == null) {
            LinearLayout ll = new LinearLayout(this.mContext);
            ll.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            ll.setOrientation(1);
            ll.setBackgroundColor(-16777216);
            this.mMenuLayout = ll;
            this.mPopupMenu = new PopupWindow(this.mMenuLayout, -2, -2);
            this.mPopupMenu.setBackgroundDrawable(new BitmapDrawable());
            this.mPopupMenu.setFocusable(true);
            this.mPopupMenu.setOnDismissListener(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void setActive(int active, int mode) {
        if (this.mMenuLayout != null) {
            this.selectedView = null;
            for (int ii = 0; ii < this.mMenuLayout.getChildCount(); ii++) {
                View v = this.mMenuLayout.getChildAt(ii);
                if (v.getTag() == null || ((Integer) v.getTag()).intValue() != mode) {
                    v.setBackgroundColor(0);
                } else {
                    v.setBackgroundColor(1593835775);
                    this.selectedView = v;
                    if (mode < 100) {
                        setImageDrawable(((ImageView) ((ViewGroup) v).getChildAt(0)).getDrawable());
                    }
                }
            }
        }
        setActive(active);
    }

    /* access modifiers changed from: package-private */
    public void setActive(int active) {
        if (active == 0) {
            setBackgroundColor(0);
        } else if (active == 1) {
            setBackgroundColor(1593835775);
        } else {
            setBackgroundColor(-1090518785);
        }
    }

    /* access modifiers changed from: package-private */
    public void disallowSelection() {
        this.allowSelection = false;
    }

    /* access modifiers changed from: package-private */
    public View getActiveView() {
        return this.activeMenuView != null ? this.activeMenuView : this;
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        return x > ((float) viewX) && x < ((float) (view.getWidth() + viewX)) && y > ((float) viewY) && y < ((float) (view.getHeight() + viewY));
    }

    private void selectAndDismiss() {
        View view;
        if (this.isPopupOpened) {
            this.isPopupOpened = false;
            this.mPopupMenu.dismiss();
        }
        setBackgroundColor(0);
        if (this.activeMenuView != null) {
            this.activeMenuView.setBackgroundColor(0);
        }
        if (this.mListener != null && this.allowSelection) {
            ViewSelectedListener viewSelectedListener = this.mListener;
            if (this.activeMenuView != null) {
                view = this.activeMenuView;
            } else {
                view = this;
            }
            viewSelectedListener.viewSelected(this, view);
        }
        this.activeMenuView = null;
    }

    public void onDismiss() {
        if (this.isPopupOpened) {
            this.isPopupOpened = false;
            selectAndDismiss();
        }
    }

    public void onClick(View v) {
        if (this.activeMenuView == null) {
            this.activeMenuView = v;
        }
        selectAndDismiss();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                if (this.mPopupMenu != null && !this.isPopupOpened && this.pressOpen) {
                    this.mPopupMenu.showAsDropDown(this);
                    this.isPopupOpened = true;
                }
                this.allowSelection = true;
                setBackgroundColor(-1090518785);
                this.mListener.viewPressed(this, true, false);
                return true;
            case 1:
                if (this.mPopupMenu == null || this.isPopupOpened || this.pressOpen || !this.allowSelection) {
                    selectAndDismiss();
                } else {
                    this.mPopupMenu.showAsDropDown(this);
                    this.isPopupOpened = true;
                }
                this.mListener.viewPressed(this, false, !this.allowSelection);
                return true;
            case 2:
                if (!this.isPopupOpened) {
                    return true;
                }
                if (this.activeMenuView != null) {
                    this.activeMenuView.setBackgroundColor(0);
                    this.activeMenuView = null;
                }
                if (this.selectedView != null) {
                    this.selectedView.setBackgroundColor(1593835775);
                }
                for (int ii = 0; ii < this.mMenuLayout.getChildCount(); ii++) {
                    View view = this.mMenuLayout.getChildAt(ii);
                    if (isPointInsideView(event.getRawX(), event.getRawY(), view)) {
                        this.activeMenuView = view;
                        this.activeMenuView.setBackgroundColor(-1090518785);
                        return true;
                    }
                }
                return true;
            default:
                return false;
        }
    }
}

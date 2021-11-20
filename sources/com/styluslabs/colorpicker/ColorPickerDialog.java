package com.styluslabs.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.styluslabs.colorpicker.ColorPickerView;
import com.styluslabs.write.R;

public class ColorPickerDialog extends Dialog implements ColorPickerView.OnColorChangedListener, View.OnClickListener {
    private ColorPickerView mColorPicker;
    private TextView mColorText;
    private OnColorChangedListener mListener;
    private ColorPickerPanelView mNewColor;
    private ColorPickerPanelView mOldColor;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public ColorPickerDialog(Context context, int initialColor) {
        super(context);
        init(initialColor);
    }

    private void init(int color) {
        getWindow().setFormat(1);
        setUp(color);
    }

    private void setUp(int color) {
        View layout = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.dialog_color_picker, (ViewGroup) null);
        setContentView(layout);
        setTitle(R.string.dialog_color_picker);
        this.mColorPicker = (ColorPickerView) layout.findViewById(R.id.color_picker_view);
        this.mOldColor = (ColorPickerPanelView) layout.findViewById(R.id.old_color_panel);
        this.mNewColor = (ColorPickerPanelView) layout.findViewById(R.id.new_color_panel);
        this.mColorText = (TextView) findViewById(R.id.color_picker_color_text);
        ((LinearLayout) this.mOldColor.getParent()).setPadding(Math.round(this.mColorPicker.getDrawingOffset()), 0, Math.round(this.mColorPicker.getDrawingOffset()), 0);
        this.mOldColor.setOnClickListener(this);
        this.mNewColor.setOnClickListener(this);
        this.mColorPicker.setOnColorChangedListener(this);
        this.mOldColor.setColor(color);
        this.mColorPicker.setColor(color, true);
    }

    @Override // com.styluslabs.colorpicker.ColorPickerView.OnColorChangedListener
    public void onColorChanged(int color) {
        this.mNewColor.setColor(color);
        this.mColorText.setText(String.valueOf(getContext().getString(R.string.newcolor)) + String.format(": #%08X", Integer.valueOf(color)));
    }

    public void setAlphaSliderVisible(boolean visible) {
        this.mColorPicker.setAlphaSliderVisible(visible);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }

    public int getColor() {
        return this.mColorPicker.getColor();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.new_color_panel && this.mListener != null) {
            this.mListener.onColorChanged(this.mNewColor.getColor());
        }
        dismiss();
    }
}

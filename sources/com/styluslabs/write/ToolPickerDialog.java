package com.styluslabs.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.styluslabs.colorpicker.ColorPickerDialog;

public class ToolPickerDialog extends AlertDialog implements ColorPickerDialog.OnColorChangedListener, DialogInterface.OnClickListener {
    private static final int COLOR_HIBLUE = 2130706687;
    private static final int COLOR_HIGREEN = 2130771712;
    private static final int COLOR_HIRED = 2147418112;
    private static final int COLOR_HIYELLOW = 2147483392;
    private static final float beta = 0.9f;
    protected int[] mColors;
    private final Context mContext;
    private boolean mHighlight;
    private final OnToolChangedListener mListener;
    private ViewGroup mPenOnlyGroup;
    private EditText mPenSizeText;
    private final float mPressure;
    private Spinner mPressureSpin;
    private final float[] mPressureVals = {0.0f, 0.25f, 0.5f, 1.0f};
    private Spinner mSaveSpin;
    private SeekBar mSeekBar;
    private int mSelectedColor;
    private ImageView mSelectedView;
    private float mSelectedWidth;

    public interface OnToolChangedListener {
        void toolChanged(int i, float f, float f2, boolean z, int i2);
    }

    public ToolPickerDialog(Context context, OnToolChangedListener listener, int color, float width, float pressure, boolean highlight) {
        super(context);
        int[] iArr = new int[30];
        iArr[0] = -16777216;
        iArr[1] = -12632257;
        iArr[2] = -8421505;
        iArr[3] = -4210753;
        iArr[4] = -1;
        iArr[5] = -8454144;
        iArr[6] = -4259840;
        iArr[7] = -65536;
        iArr[8] = -32897;
        iArr[9] = -16449;
        iArr[10] = -16744704;
        iArr[11] = -16728320;
        iArr[12] = -16711936;
        iArr[13] = -8388737;
        iArr[14] = -4194369;
        iArr[15] = -16777089;
        iArr[16] = -16777025;
        iArr[17] = -16776961;
        iArr[18] = -8421377;
        iArr[19] = -4210689;
        iArr[20] = -47872;
        iArr[21] = -29696;
        iArr[22] = -23296;
        iArr[23] = -11776;
        iArr[24] = -256;
        iArr[25] = COLOR_HIYELLOW;
        iArr[26] = COLOR_HIGREEN;
        iArr[27] = COLOR_HIBLUE;
        iArr[28] = COLOR_HIRED;
        this.mColors = iArr;
        this.mContext = context;
        this.mListener = listener;
        this.mSelectedColor = color;
        this.mSelectedWidth = width;
        this.mHighlight = highlight;
        this.mPressure = pressure;
        this.mSelectedView = null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private float seekBarToWidth(float x) {
        float x2 = x / 200.0f;
        float w = ((-0.100000024f * x2) / ((beta * x2) - 1.0f)) * 200.0f;
        if (w < 5.0f) {
            return ((float) Math.round(w * 10.0f)) / 10.0f;
        }
        if (w < 10.0f) {
            return ((float) Math.round(w * 2.0f)) / 2.0f;
        }
        return (float) Math.round(w);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int widthToSeekBar(float w) {
        float w2 = w / 200.0f;
        return Math.round((w2 / (((beta * w2) - beta) + 1.0f)) * 200.0f);
    }

    @Override // com.styluslabs.colorpicker.ColorPickerDialog.OnColorChangedListener
    public void onColorChanged(int color) {
        this.mSelectedColor = color;
    }

    private void allDone() {
        int saveslot = PaintMain.atoi(this.mSaveSpin.getSelectedItem().toString(), 0) - 1;
        int prpos = this.mPressureSpin.getSelectedItemPosition();
        this.mListener.toolChanged(this.mSelectedColor, this.mSelectedWidth, (prpos < 0 || prpos >= this.mPressureVals.length) ? 0.0f : this.mPressureVals[prpos], this.mHighlight, saveslot);
        dismiss();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        setTitle(R.string.pen_selection_title);
        View view = getLayoutInflater().inflate(R.layout.tool_picker, (ViewGroup) null);
        setButton(-1, this.mContext.getText(R.string.confirm), this);
        setButton(-2, this.mContext.getText(R.string.cancel), (DialogInterface.OnClickListener) null);
        this.mPenOnlyGroup = (ViewGroup) view.findViewById(R.id.pen_only_group);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.mContext, 17367048);
        adapter.setDropDownViewResource(17367049);
        adapter.add("None");
        for (int ii = 1; ii <= 6; ii++) {
            adapter.add(new StringBuilder().append(ii).toString());
        }
        this.mSaveSpin = (Spinner) view.findViewById(R.id.save_pen_spinner);
        this.mSaveSpin.setAdapter((SpinnerAdapter) adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this.mContext, 17367048, this.mContext.getResources().getStringArray(R.array.tp_pressurevals));
        adapter2.setDropDownViewResource(17367049);
        this.mPressureSpin = (Spinner) view.findViewById(R.id.pressure_spinner);
        this.mPressureSpin.setAdapter((SpinnerAdapter) adapter2);
        this.mPressureSpin.setSelection(((double) this.mPressure) > 0.75d ? 3 : ((double) this.mPressure) > 0.375d ? 2 : ((double) this.mPressure) > 0.125d ? 1 : 0);
        LinearLayout mColorGrid = (LinearLayout) view.findViewById(R.id.color_grid);
        LinearLayout row = null;
        for (int ii2 = 0; ii2 < this.mColors.length; ii2++) {
            if (ii2 % 5 == 0) {
                row = new LinearLayout(this.mContext);
                row.setOrientation(0);
                LinearLayout.LayoutParams lrow = new LinearLayout.LayoutParams(-1, -1);
                lrow.weight = 1.0f;
                mColorGrid.addView(row, lrow);
            }
            ImageView imageView = new ImageView(this.mContext);
            LinearLayout.LayoutParams limage = new LinearLayout.LayoutParams(-1, -1);
            limage.weight = 1.0f;
            limage.setMargins(2, 2, 2, 2);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setTag(Integer.valueOf(ii2));
            imageView.setOnClickListener(new View.OnClickListener() {
                /* class com.styluslabs.write.ToolPickerDialog.AnonymousClass1 */

                public void onClick(View v) {
                    int color = ToolPickerDialog.this.mColors[((Integer) v.getTag()).intValue()];
                    if (ToolPickerDialog.this.mSelectedView != null) {
                        ToolPickerDialog.this.mSelectedView.setBackgroundDrawable(new ColorDrawable(ToolPickerDialog.this.mSelectedColor));
                    }
                    if (color == 0) {
                        ToolPickerDialog.this.mSelectedView = null;
                        ColorPickerDialog pencolordialog = new ColorPickerDialog(ToolPickerDialog.this.mContext, ToolPickerDialog.this.mSelectedColor);
                        pencolordialog.setOnColorChangedListener(ToolPickerDialog.this);
                        pencolordialog.setAlphaSliderVisible(true);
                        pencolordialog.show();
                        return;
                    }
                    ToolPickerDialog.this.mSelectedView = (ImageView) v;
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{new ColorDrawable(-1), new ColorDrawable(-16777216), new ColorDrawable(color)});
                    ld.setLayerInset(1, 2, 2, 2, 2);
                    ld.setLayerInset(2, 3, 3, 3, 3);
                    ToolPickerDialog.this.mSelectedView.setBackgroundDrawable(ld);
                    ToolPickerDialog.this.mSelectedColor = color;
                    boolean oldhl = ToolPickerDialog.this.mHighlight;
                    ToolPickerDialog.this.mHighlight = Color.alpha(color) < 255;
                    if (ToolPickerDialog.this.mHighlight && !oldhl) {
                        ToolPickerDialog.this.mPenSizeText.setText("40.0");
                    }
                }
            });
            int c = this.mColors[ii2];
            if (c == this.mSelectedColor) {
                this.mSelectedView = imageView;
                LayerDrawable ld = new LayerDrawable(new Drawable[]{new ColorDrawable(-1), new ColorDrawable(-16777216), new ColorDrawable(c)});
                ld.setLayerInset(1, 2, 2, 2, 2);
                ld.setLayerInset(2, 3, 3, 3, 3);
                this.mSelectedView.setBackgroundDrawable(ld);
            } else {
                imageView.setBackgroundDrawable(new ColorDrawable(c));
            }
            if (c == 0) {
                imageView.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.ic_ellipsis));
            } else if (Color.alpha(c) < 255) {
                imageView.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.ic_hilite));
            }
            row.addView(imageView, limage);
        }
        this.mSeekBar = (SeekBar) view.findViewById(R.id.pensize_seekbar);
        this.mPenSizeText = (EditText) view.findViewById(R.id.pensize_edit);
        if (this.mSelectedWidth < 0.0f) {
            this.mPenOnlyGroup.setVisibility(8);
        } else {
            this.mSeekBar.setProgress(widthToSeekBar(this.mSelectedWidth));
            this.mPenSizeText.setText(String.format("%.1f", Float.valueOf(this.mSelectedWidth)));
            this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /* class com.styluslabs.write.ToolPickerDialog.AnonymousClass2 */

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        ToolPickerDialog.this.mSelectedWidth = ToolPickerDialog.this.seekBarToWidth((float) progress);
                        ToolPickerDialog.this.mPenSizeText.setText(String.format("%.1f", Float.valueOf(ToolPickerDialog.this.mSelectedWidth)));
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            this.mPenSizeText.addTextChangedListener(new TextWatcher() {
                /* class com.styluslabs.write.ToolPickerDialog.AnonymousClass3 */

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable e) {
                    float w = PaintMain.atof(ToolPickerDialog.this.mPenSizeText.getText().toString(), -1.0f);
                    if (w >= 0.0f && w <= 200.0f) {
                        ToolPickerDialog.this.mSelectedWidth = w;
                        ToolPickerDialog.this.mSeekBar.setProgress(ToolPickerDialog.this.widthToSeekBar(ToolPickerDialog.this.mSelectedWidth));
                    }
                }
            });
        }
        setView(view);
        super.onCreate(savedInstanceState);
    }

    public void onClick(DialogInterface dialog, int which) {
        allDone();
    }
}

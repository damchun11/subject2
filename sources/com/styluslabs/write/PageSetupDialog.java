package com.styluslabs.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import com.styluslabs.colorpicker.ColorPickerDialog;

public class PageSetupDialog extends AlertDialog implements DialogInterface.OnClickListener, ColorPickerDialog.OnColorChangedListener, AdapterView.OnItemSelectedListener {
    private CheckBox cbApplyAll;
    private CheckBox cbDocDefault;
    private CheckBox cbGlobalDefault;
    private boolean isPageColor;
    private final Context mContext;
    private EditText mHeightEdit;
    private final OnPageSetupListener mListener;
    private EditText mMarginEdit;
    private final SharedPreferences mPrefs;
    private final String mPrompt;
    private Spinner mRuleSpinner;
    private Spinner mSizeSpinner;
    private final int[] mVals;
    private EditText mWidthEdit;
    private EditText mXRulingEdit;
    private EditText mYRulingEdit;
    private int pageColor;
    private final int[][] predefRulings;
    private final int[][] predefSizes = {new int[2], new int[2], new int[2], new int[]{1190, 1540}, new int[]{700, 1120}, new int[]{420, 700}};
    private int ruleColor;

    public interface OnPageSetupListener {
        void pageSetup(String str);
    }

    public PageSetupDialog(Context context, OnPageSetupListener listener, SharedPreferences prefs, String prompt, int[] vals) {
        super(context);
        int[] iArr = new int[4];
        iArr[3] = -16776961;
        int[] iArr2 = new int[4];
        iArr2[1] = 45;
        iArr2[2] = 100;
        iArr2[3] = -16776961;
        int[] iArr3 = new int[4];
        iArr3[1] = 40;
        iArr3[2] = 100;
        iArr3[3] = -16776961;
        int[] iArr4 = new int[4];
        iArr4[1] = 35;
        iArr4[2] = 100;
        iArr4[3] = -16776961;
        int[] iArr5 = new int[4];
        iArr5[0] = 35;
        iArr5[1] = 35;
        iArr5[3] = -8421377;
        int[] iArr6 = new int[4];
        iArr6[0] = 30;
        iArr6[1] = 30;
        iArr6[3] = -8421377;
        int[] iArr7 = new int[4];
        iArr7[0] = 20;
        iArr7[1] = 20;
        iArr7[3] = -8421377;
        this.predefRulings = new int[][]{new int[4], iArr, iArr2, iArr3, iArr4, iArr5, iArr6, iArr7};
        this.mContext = context;
        this.mPrompt = prompt;
        this.mListener = listener;
        this.mVals = vals;
        this.mPrefs = prefs;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        setTitle(this.mPrompt);
        View view = getLayoutInflater().inflate(R.layout.page_setup, (ViewGroup) null);
        setButton(-1, this.mContext.getText(R.string.confirm), this);
        setButton(-2, this.mContext.getText(R.string.cancel), (DialogInterface.OnClickListener) null);
        this.mWidthEdit = (EditText) view.findViewById(R.id.page_width_edit);
        this.mWidthEdit.setText(Integer.toString(this.mVals[0]));
        this.mHeightEdit = (EditText) view.findViewById(R.id.page_height_edit);
        this.mHeightEdit.setText(Integer.toString(this.mVals[1]));
        this.mXRulingEdit = (EditText) view.findViewById(R.id.page_xruling_edit);
        this.mXRulingEdit.setText(Integer.toString(this.mVals[2]));
        this.mYRulingEdit = (EditText) view.findViewById(R.id.page_yruling_edit);
        this.mYRulingEdit.setText(Integer.toString(this.mVals[3]));
        this.mMarginEdit = (EditText) view.findViewById(R.id.page_margin_edit);
        this.mMarginEdit.setText(Integer.toString(this.mVals[4]));
        this.mSizeSpinner = (Spinner) view.findViewById(R.id.page_size_spinner);
        this.mSizeSpinner.setOnItemSelectedListener(this);
        this.mRuleSpinner = (Spinner) view.findViewById(R.id.ruling_spinner);
        this.mRuleSpinner.setOnItemSelectedListener(this);
        this.cbApplyAll = (CheckBox) view.findViewById(R.id.pageApplyAll);
        this.cbDocDefault = (CheckBox) view.findViewById(R.id.pageDocDefault);
        this.cbGlobalDefault = (CheckBox) view.findViewById(R.id.pageGlobalDefault);
        this.pageColor = this.mVals[5];
        this.ruleColor = this.mVals[6];
        this.predefSizes[0][0] = this.mVals[0];
        this.predefSizes[0][1] = this.mVals[1];
        this.predefRulings[0][0] = this.mVals[2];
        this.predefRulings[0][1] = this.mVals[3];
        this.predefRulings[0][2] = this.mVals[4];
        this.predefRulings[0][3] = this.ruleColor;
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
        this.predefSizes[1][0] = this.mPrefs.getInt("screenPageWidth", 768);
        this.predefSizes[1][1] = this.mPrefs.getInt("screenPageHeight", 1024);
        this.predefSizes[2][0] = this.predefSizes[1][1];
        this.predefSizes[2][1] = this.predefSizes[1][0];
        ((Button) view.findViewById(R.id.btnPageColor)).setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.PageSetupDialog.AnonymousClass1 */

            public void onClick(View v) {
                PageSetupDialog.this.isPageColor = true;
                ColorPickerDialog pagecolordialog = new ColorPickerDialog(PageSetupDialog.this.mContext, PageSetupDialog.this.pageColor);
                pagecolordialog.setOnColorChangedListener(PageSetupDialog.this);
                pagecolordialog.show();
            }
        });
        ((Button) view.findViewById(R.id.btnRuleColor)).setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.PageSetupDialog.AnonymousClass2 */

            public void onClick(View v) {
                PageSetupDialog.this.isPageColor = false;
                ColorPickerDialog rulecolordialog = new ColorPickerDialog(PageSetupDialog.this.mContext, PageSetupDialog.this.ruleColor);
                rulecolordialog.setOnColorChangedListener(PageSetupDialog.this);
                rulecolordialog.show();
            }
        });
        setView(view);
        super.onCreate(savedInstanceState);
    }

    @Override // com.styluslabs.colorpicker.ColorPickerDialog.OnColorChangedListener
    public void onColorChanged(int color) {
        if (this.isPageColor) {
            this.pageColor = color;
        } else {
            this.ruleColor = color;
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        int docDefault;
        int globalDefault;
        if (this.mListener != null) {
            int applyAll = this.cbApplyAll.isChecked() ? 1 : 0;
            if (this.cbDocDefault.isChecked()) {
                docDefault = 1;
            } else {
                docDefault = 0;
            }
            if (this.cbGlobalDefault.isChecked()) {
                globalDefault = 1;
            } else {
                globalDefault = 0;
            }
            if (this.cbGlobalDefault.isChecked()) {
                SharedPreferences.Editor spedit = this.mPrefs.edit();
                spedit.putInt("pageWidth", PaintMain.atoi(this.mWidthEdit.getText().toString(), 768));
                spedit.putInt("pageHeight", PaintMain.atoi(this.mHeightEdit.getText().toString(), 1024));
                spedit.putInt("xRuling", PaintMain.atoi(this.mXRulingEdit.getText().toString(), 0));
                spedit.putInt("yRuling", PaintMain.atoi(this.mYRulingEdit.getText().toString(), 40));
                spedit.putInt("marginLeft", PaintMain.atoi(this.mMarginEdit.getText().toString(), 100));
                spedit.putInt("pageColor", this.pageColor);
                spedit.putInt("ruleColor", this.ruleColor);
                spedit.apply();
            }
            this.mListener.pageSetup(String.format("<pagesetup width='%s' height='%s' xruling='%s' yruling='%s' marginLeft='%s' color='%d' ruleColor='%d' applyToAll='%d' docDefault='%d' globalDefault='%d'/>", this.mWidthEdit.getText().toString(), this.mHeightEdit.getText().toString(), this.mXRulingEdit.getText().toString(), this.mYRulingEdit.getText().toString(), this.mMarginEdit.getText().toString(), Integer.valueOf(this.pageColor), Integer.valueOf(this.ruleColor), Integer.valueOf(applyAll), Integer.valueOf(docDefault), Integer.valueOf(globalDefault)));
            dialog.dismiss();
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
        if (arg0 == this.mSizeSpinner && pos < this.predefSizes.length) {
            this.mWidthEdit.setText(String.valueOf(this.predefSizes[pos][0]));
            this.mHeightEdit.setText(String.valueOf(this.predefSizes[pos][1]));
        } else if (arg0 == this.mRuleSpinner && pos < this.predefRulings.length) {
            this.mXRulingEdit.setText(String.valueOf(this.predefRulings[pos][0]));
            this.mYRulingEdit.setText(String.valueOf(this.predefRulings[pos][1]));
            this.mMarginEdit.setText(String.valueOf(this.predefRulings[pos][2]));
            this.ruleColor = this.predefRulings[pos][3];
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}

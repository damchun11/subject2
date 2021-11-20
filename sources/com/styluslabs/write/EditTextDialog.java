package com.styluslabs.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private final Context mContext;
    private EditText mEditText;
    private final OnTitleSetListener mListener;
    private final String mPrompt;
    private final String mTitle;

    public interface OnTitleSetListener {
        void titleSet(String str);
    }

    public EditTextDialog(Context context, OnTitleSetListener listener, String prompt, String title) {
        super(context);
        this.mContext = context;
        this.mPrompt = prompt;
        this.mTitle = title;
        this.mListener = listener;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        setTitle(this.mPrompt);
        View view = getLayoutInflater().inflate(R.layout.title_setter, (ViewGroup) null);
        setButton(-1, this.mContext.getText(R.string.confirm), this);
        setButton(-2, this.mContext.getText(R.string.cancel), (DialogInterface.OnClickListener) null);
        this.mEditText = (EditText) view.findViewById(R.id.edittitle);
        this.mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /* class com.styluslabs.write.EditTextDialog.AnonymousClass1 */

            public boolean onEditorAction(TextView tv, int actionId, KeyEvent event) {
                if (actionId != 6 || (event != null && event.getAction() != 0)) {
                    return false;
                }
                EditTextDialog.this.doTitleSet();
                return true;
            }
        });
        this.mEditText.setText(this.mTitle);
        setView(view);
        super.onCreate(savedInstanceState);
    }

    public void onClick(DialogInterface dialog, int which) {
        doTitleSet();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doTitleSet() {
        if (this.mListener != null) {
            this.mListener.titleSet(this.mEditText.getText().toString());
            dismiss();
        }
    }
}

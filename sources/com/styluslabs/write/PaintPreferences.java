package com.styluslabs.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import java.util.List;

public class PaintPreferences extends PreferenceActivity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // android.preference.PreferenceActivity
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.preferences, target);
    }

    public static class DocPrefs extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.doc_prefs);
        }
    }

    public static class InputPrefs extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.input_prefs);
        }
    }

    public static class DrawingPrefs extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.drawing_prefs);
        }
    }

    public static class AdvancedPrefs extends PreferenceFragment {
        private Context mContext;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.advanced_prefs);
            this.mContext = getActivity();
        }

        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getKey().equals("DeleteDB")) {
                new AlertDialog.Builder(this.mContext).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.db_clear_title).setMessage(R.string.db_clear_prompt).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    /* class com.styluslabs.write.PaintPreferences.AdvancedPrefs.AnonymousClass1 */

                    public void onClick(DialogInterface dialog, int which) {
                        NotesDbAdapter mDbHelper = new NotesDbAdapter(AdvancedPrefs.this.mContext);
                        mDbHelper.open();
                        mDbHelper.reset();
                        mDbHelper.close();
                        Toast.makeText(AdvancedPrefs.this.mContext, (int) R.string.db_cleared_msg, 0).show();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    /* class com.styluslabs.write.PaintPreferences.AdvancedPrefs.AnonymousClass2 */

                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            } else if (preference.getKey().equals("ResetPrefs")) {
                new AlertDialog.Builder(this.mContext).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.resetprefs_title).setMessage(R.string.resetprefs_prompt).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    /* class com.styluslabs.write.PaintPreferences.AdvancedPrefs.AnonymousClass3 */

                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.getDefaultSharedPreferences(AdvancedPrefs.this.mContext).edit().clear().commit();
                        Toast.makeText(AdvancedPrefs.this.mContext, (int) R.string.resetprefs_msg, 0).show();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    /* class com.styluslabs.write.PaintPreferences.AdvancedPrefs.AnonymousClass4 */

                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                return true;
            } else if (preference.getKey().equals("ImportExisting")) {
                Intent intent = new Intent(this.mContext, ImportActivity.class);
                intent.putExtra(ImportActivity.IMPORT_PATH, PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("SaveImgPath", ""));
                startActivity(intent);
                return true;
            } else if (!preference.getKey().equals("ShowChangelog")) {
                return super.onPreferenceTreeClick(preferenceScreen, preference);
            } else {
                PaintMain.showChangelog(this.mContext);
                return true;
            }
        }
    }
}

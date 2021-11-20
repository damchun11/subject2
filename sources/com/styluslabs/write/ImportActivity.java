package com.styluslabs.write;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class ImportActivity extends Activity {
    private static final int ACTIVITY_IMPORTDOC = 1;
    public static final String IMPORT_PATH = "importPath";
    NotesDbAdapter mDbHelper;
    ArrayList<File> mFiles;
    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mDbHelper = new NotesDbAdapter(this);
        this.mDbHelper.open();
        File folder = new File(getIntent().getStringExtra(IMPORT_PATH));
        if (!folder.isDirectory()) {
            folder = new File(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + getString(R.string.defaultdir));
        }
        File[] files = folder.listFiles(new FilenameFilter() {
            /* class com.styluslabs.write.ImportActivity.AnonymousClass1 */

            public boolean accept(File parentdir, String name) {
                return name.toLowerCase().endsWith(".html");
            }
        });
        if (files == null) {
            Toast.makeText(getApplicationContext(), (int) R.string.noimport_msg, 0).show();
            finish();
            return;
        }
        this.mFiles = new ArrayList<>(Arrays.asList(files));
        this.progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.importing_msg));
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
        onActivityResult(1, -1, null);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x003b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResult(int r9, int r10, android.content.Intent r11) {
        /*
        // Method dump skipped, instructions count: 137
        */
        throw new UnsupportedOperationException("Method not decompiled: com.styluslabs.write.ImportActivity.onActivityResult(int, int, android.content.Intent):void");
    }
}

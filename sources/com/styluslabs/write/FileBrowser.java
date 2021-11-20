package com.styluslabs.write;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.styluslabs.write.EditTextDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileBrowser extends ListActivity implements EditTextDialog.OnTitleSetListener {
    public static final String MODE = "MODE";
    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;
    public static final String RESULT_PATH = "RESULT_PATH";
    public static final String START_NAME = "START_NAME";
    public static final String START_PATH = "START_PATH";
    private Button cancelCreateButton;
    private Button cancelSelButton;
    private Button createButton;
    private String currentPath = "/";
    private LinearLayout layoutCreate;
    private LinearLayout layoutSelect;
    private FileBrowserAdapter mAdapter;
    private ArrayList<String> mDirs;
    private EditText mFileName;
    private ArrayList<String> mFiles;
    private int mMode = 1;
    private TextView myPath;
    private final String root = "/";
    private Button selectButton;
    private File selectedFile;
    private int selectedPos = -1;
    private String startPath;

    /* access modifiers changed from: private */
    public class FileBrowserAdapter extends BaseAdapter {
        private final ColorDrawable hilite = new ColorDrawable(-8438016);
        private final Bitmap mFileIcon;
        private final Bitmap mFolderIcon;
        private final LayoutInflater mInflater;

        class ViewHolder {
            ImageView icon;
            TextView text;

            ViewHolder() {
            }
        }

        public FileBrowserAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            this.mFolderIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_folder);
            this.mFileIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_file);
        }

        public int getCount() {
            return FileBrowser.this.mDirs.size() + FileBrowser.this.mFiles.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = this.mInflater.inflate(R.layout.file_browser_row, (ViewGroup) null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.fdrowtext);
                holder.icon = (ImageView) convertView.findViewById(R.id.fdrowimage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < FileBrowser.this.mDirs.size()) {
                holder.text.setText((CharSequence) FileBrowser.this.mDirs.get(position));
                holder.icon.setImageBitmap(this.mFolderIcon);
            } else {
                holder.text.setText((CharSequence) FileBrowser.this.mFiles.get(position - FileBrowser.this.mDirs.size()));
                holder.icon.setImageBitmap(this.mFileIcon);
            }
            if (position == FileBrowser.this.selectedPos) {
                convertView.setBackgroundDrawable(this.hilite);
            } else {
                convertView.setBackgroundDrawable(null);
            }
            return convertView;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0, getIntent());
        this.mDirs = new ArrayList<>();
        this.mFiles = new ArrayList<>();
        this.mAdapter = new FileBrowserAdapter(this);
        setContentView(R.layout.file_browser_main);
        this.myPath = (TextView) findViewById(R.id.path);
        this.mFileName = (EditText) findViewById(R.id.fdEditTextFile);
        this.layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);
        this.layoutCreate = (LinearLayout) findViewById(R.id.fdLinearLayoutCreate);
        this.selectButton = (Button) findViewById(R.id.fdButtonSelect);
        this.selectButton.setEnabled(false);
        this.selectButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FileBrowser.AnonymousClass1 */

            public void onClick(View v) {
                if (FileBrowser.this.selectedFile != null) {
                    FileBrowser.this.getIntent().putExtra(FileBrowser.RESULT_PATH, FileBrowser.this.selectedFile.getPath());
                    FileBrowser.this.setResult(-1, FileBrowser.this.getIntent());
                    FileBrowser.this.finish();
                }
            }
        });
        this.cancelSelButton = (Button) findViewById(R.id.fdButtonCancelSel);
        this.cancelSelButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FileBrowser.AnonymousClass2 */

            public void onClick(View v) {
                FileBrowser.this.finish();
            }
        });
        this.cancelCreateButton = (Button) findViewById(R.id.fdButtonCancelCreate);
        this.cancelCreateButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FileBrowser.AnonymousClass3 */

            public void onClick(View v) {
                FileBrowser.this.finish();
            }
        });
        this.createButton = (Button) findViewById(R.id.fdButtonCreate);
        this.createButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FileBrowser.AnonymousClass4 */

            public void onClick(View v) {
                if (FileBrowser.this.mFileName.getText().length() > 0) {
                    FileBrowser.this.getIntent().putExtra(FileBrowser.RESULT_PATH, String.valueOf(FileBrowser.this.currentPath) + "/" + ((Object) FileBrowser.this.mFileName.getText()));
                    FileBrowser.this.setResult(-1, FileBrowser.this.getIntent());
                    FileBrowser.this.finish();
                }
            }
        });
        this.mMode = getIntent().getIntExtra("MODE", 1);
        if (this.mMode == 1) {
            this.layoutCreate.setVisibility(8);
            setTitle(R.string.menu_openfile);
        } else if (this.mMode == 2) {
            this.layoutSelect.setVisibility(8);
            setTitle(R.string.menu_savefile);
            String startName = getIntent().getStringExtra(START_NAME);
            if (startName != null) {
                ((TextView) findViewById(R.id.fdEditTextFile)).setText(startName);
            }
        }
        this.startPath = getIntent().getStringExtra(START_PATH);
        if (this.startPath == null) {
            this.startPath = "/";
        }
        getDir(this.startPath);
        this.mAdapter = new FileBrowserAdapter(this);
        setListAdapter(this.mAdapter);
    }

    @Override // com.styluslabs.write.EditTextDialog.OnTitleSetListener
    public void titleSet(String title) {
    }

    private void getDir(String dirPath) {
        File f = new File(dirPath);
        while (f != null && !f.isDirectory()) {
            f = f.getParentFile();
        }
        if (f == null) {
            f = new File("/");
        }
        getDir(f);
    }

    private void getDir(File f) {
        String seldir = null;
        File[] filelist = f.listFiles();
        this.mDirs.clear();
        this.mFiles.clear();
        if (!f.getPath().equals("/")) {
            this.mDirs.add("../");
        }
        if (filelist != null) {
            for (File file : filelist) {
                if (file.isDirectory()) {
                    this.mDirs.add(String.valueOf(file.getName()) + "/");
                    if (file.getPath().equals(this.currentPath)) {
                        seldir = String.valueOf(file.getName()) + "/";
                    }
                } else {
                    this.mFiles.add(file.getName());
                }
            }
        }
        Collections.sort(this.mDirs, String.CASE_INSENSITIVE_ORDER);
        Collections.sort(this.mFiles, String.CASE_INSENSITIVE_ORDER);
        this.currentPath = f.getPath();
        this.myPath.setText(((Object) getText(R.string.location)) + ": " + this.currentPath);
        this.mAdapter.notifyDataSetChanged();
        if (seldir != null) {
            getListView().setSelection(this.mDirs.indexOf(seldir));
        }
    }

    /* access modifiers changed from: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        File file;
        if (position < this.mDirs.size()) {
            file = new File(this.currentPath, this.mDirs.get(position));
        } else {
            file = new File(this.currentPath, this.mFiles.get(position - this.mDirs.size()));
        }
        if (file.isDirectory()) {
            unselect();
            if (file.getName().equals("..")) {
                file = new File(this.currentPath).getParentFile();
            }
            if (file.canRead()) {
                getDir(file);
            } else {
                alertMessage("[" + file.getName() + "] " + ((Object) getText(R.string.cant_read_folder)));
            }
        } else if (this.mMode == 1) {
            this.selectedFile = file;
            this.selectedPos = position;
            this.selectButton.setEnabled(true);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        unselect();
        if (this.currentPath.equals(this.startPath) || this.currentPath.equals("/")) {
            finish();
        } else {
            getDir(new File(this.currentPath).getParentFile());
        }
        return true;
    }

    private void unselect() {
        this.selectedPos = -1;
        this.selectButton.setEnabled(false);
    }

    private void alertMessage(String msg) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.error).setMessage(msg).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.FileBrowser.AnonymousClass5 */

            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}

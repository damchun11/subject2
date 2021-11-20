package com.styluslabs.write;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.styluslabs.write.EditTextDialog;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PaintMain extends Activity implements AdapterView.OnItemClickListener {
    private static final int ACTIVITY_ADDFILE = 5;
    private static final int ACTIVITY_EDITPAINT = 2;
    private static final int ACTIVITY_NEWPAINT = 0;
    private static final int ACTIVITY_OPENFILE = 4;
    private static final int ACTIVITY_SAVEZIP = 6;
    private static final int ADDFILE_ID = 4;
    public static final String BASE_PACKAGE = "com.styluslabs.write";
    private static final int DELETEFOLDER_ID = 201;
    private static final int DELETENOTE_ID = 101;
    private static final int HELP_ID = 7;
    public static final String MODE = "MODE";
    public static final int MODE_NORMAL = 1;
    public static final int MODE_SEL = 2;
    private static final int MOVENOTE_ID = 103;
    private static final int NEWFOLDER_ID = 1;
    private static final int NEWNOTE_ID = 2;
    private static final int OPENFILE_ID = 3;
    private static final int PREFS_ID = 8;
    private static final int RENAMEFOLDER_ID = 202;
    private static final int RENAMENOTE_ID = 102;
    private static final int SAVEZIP_ID = 5;
    public static final String SEL_NOTE_ID = "SEL_NOTE_ID";
    private static final String TAG_DELIM = ":::";
    public static final String UNLOCK_PACKAGE = "com.styluslabs.write.plus";
    private static final int VIEWSTATE_BOTH = 0;
    private static final int VIEWSTATE_DOCS = 2;
    private static final int VIEWSTATE_TAGS = 1;
    private Uri imageURI = null;
    private NotesDbAdapter mDbHelper;
    private String mExtStorageDir = "/sdcard";
    private int mMode;
    private int mNoteListPos = 0;
    private NoteListAdapter mNotesAdapter = null;
    private View mNotesContainer;
    private TextView mNotesHeaderText;
    private GridView mNotesList;
    SharedPreferences.Editor mPrefEditor;
    SharedPreferences mPrefs;
    private String mSelectedTag = "";
    private String mSortColumn;
    private ListView mTagList;
    private List<String> mTags;
    int mThumbWidth = 375;
    private int mViewState;

    public class NoteListAdapter extends CursorAdapter {
        private final LayoutInflater mInflater;

        private class NoteViewHolder {
            ImageView icon;
            TextView text;

            private NoteViewHolder() {
            }

            /* synthetic */ NoteViewHolder(NoteListAdapter noteListAdapter, NoteViewHolder noteViewHolder) {
                this();
            }
        }

        public NoteListAdapter(Context context, Cursor c) {
            super(context, c);
            this.mInflater = LayoutInflater.from(context);
        }

        public void bindView(View view, Context context, Cursor cursor) {
            NoteViewHolder holder = (NoteViewHolder) view.getTag();
            String title = cursor.getString(cursor.getColumnIndex(NotesDbAdapter.KEY_TITLE));
            if (title == null || title.length() == 0) {
                title = "";
            }
            byte[] thumbraw = cursor.getBlob(cursor.getColumnIndex(NotesDbAdapter.KEY_THUMBNAIL));
            if (thumbraw != null && thumbraw.length > 0) {
                holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(thumbraw, 0, thumbraw.length));
                holder.icon.setAdjustViewBounds(true);
                holder.icon.setMaxHeight((PaintMain.this.mThumbWidth * 5) / PaintMain.OPENFILE_ID);
            }
            holder.text.setText(title);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            NoteViewHolder holder = new NoteViewHolder(this, null);
            View view = this.mInflater.inflate(R.layout.grid_item, (ViewGroup) null);
            holder.text = (TextView) view.findViewById(R.id.griditemtext);
            holder.icon = (ImageView) view.findViewById(R.id.griditemicon);
            view.setLayoutParams(new AbsListView.LayoutParams(PaintMain.this.mThumbWidth, -2));
            view.setTag(holder);
            return view;
        }
    }

    public static int atoi(String a, int defaultval) {
        try {
            return Integer.parseInt(a);
        } catch (NumberFormatException e) {
            return defaultval;
        }
    }

    public static float atof(String s, float defaultval) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            try {
                return NumberFormat.getInstance().parse(s).floatValue();
            } catch (ParseException e2) {
                return defaultval;
            }
        }
    }

    public void onCreate(Bundle savedstate) {
        super.onCreate(savedstate);
        setContentView(R.layout.notes_list);
        setTitle("Stylus Labs Write");
        this.mDbHelper = new NotesDbAdapter(this);
        this.mDbHelper.open();
        this.mExtStorageDir = Environment.getExternalStorageDirectory().getPath();
        Intent intent = getIntent();
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.mPrefEditor = this.mPrefs.edit();
        if (this.mPrefs.contains("NoteTags")) {
            this.mTags = new ArrayList(Arrays.asList(this.mPrefs.getString("NoteTags", "").split(TAG_DELIM)));
            this.mTags.removeAll(Arrays.asList("", null));
        } else {
            loadTagsFromDB();
        }
        this.mThumbWidth = atoi(this.mPrefs.getString("ThumbnailSize", getString(R.string.pref_thumbnail_size_default)), this.mThumbWidth);
        this.mSelectedTag = this.mPrefs.getString("SelectedTag", "");
        this.mNoteListPos = this.mPrefs.getInt("NoteListPos", 0);
        this.mNotesList = (GridView) findViewById(R.id.notesgrid);
        registerForContextMenu(this.mNotesList);
        this.mMode = intent.getIntExtra("MODE", 1);
        this.mNotesList.setOnItemClickListener(this);
        this.mNotesContainer = findViewById(R.id.notescontainer);
        this.mNotesHeaderText = (TextView) findViewById(R.id.notesheadertext);
        this.mTagList = (ListView) findViewById(R.id.taglist);
        registerForContextMenu(this.mTagList);
        this.mTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass1 */

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                String tag = position > 0 ? (String) PaintMain.this.mTags.get(position - 1) : "";
                if (PaintMain.this.mViewState != 0) {
                    PaintMain.this.mNotesContainer.setVisibility(0);
                    PaintMain.this.mTagList.setVisibility(PaintMain.PREFS_ID);
                    PaintMain.this.mViewState = 2;
                }
                if (tag != PaintMain.this.mSelectedTag) {
                    PaintMain.this.mSelectedTag = tag;
                    PaintMain.this.mNoteListPos = 0;
                    PaintMain.this.fillNotes();
                }
            }
        });
        fillTags();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (((float) metrics.widthPixels) / metrics.density < 580.0f) {
            this.mViewState = 1;
            this.mNotesContainer.setVisibility(PREFS_ID);
        } else {
            this.mViewState = 0;
            findViewById(R.id.notesheader).setVisibility(PREFS_ID);
            this.mTagList.setChoiceMode(1);
            this.mTagList.setItemChecked(this.mTags.indexOf(this.mSelectedTag) + 1, true);
        }
        if (this.mPrefs.contains("tempPath")) {
            File tempfolder = new File(this.mPrefs.getString("tempPath", ""));
            if (tempfolder.isDirectory()) {
                for (File child : tempfolder.listFiles()) {
                    child.delete();
                }
            }
        }
        String action = intent.getAction();
        if ("android.intent.action.SEND".equals(action)) {
            if (intent.getType() != null && intent.getType().startsWith("image/")) {
                this.imageURI = (Uri) intent.getParcelableExtra("android.intent.extra.STREAM");
                Toast.makeText(this, (int) R.string.image_dest_msg, 0).show();
            }
        } else if (("android.intent.action.VIEW".equals(action) || "android.intent.action.EDIT".equals(action)) && "text/html".equals(intent.getType()) && intent.getData() != null) {
            Intent i = new Intent(this, FingerPaint.class);
            i.putExtra("Filename", intent.getData().getPath());
            i.putExtra(NotesDbAdapter.KEY_ROWID, -1L);
            startActivity(i);
            finish();
        }
    }

    public static boolean isFullVersion(Context context) {
        return context.getPackageManager().checkSignatures(BASE_PACKAGE, UNLOCK_PACKAGE) == 0;
    }

    private void loadTagsFromDB() {
        this.mTags = new ArrayList(Arrays.asList(this.mPrefs.getString("NoteTags", "").split(TAG_DELIM)));
        Cursor tagcursor = this.mDbHelper.fetchTags();
        while (tagcursor.moveToNext()) {
            String tag = tagcursor.getString(tagcursor.getColumnIndex(NotesDbAdapter.KEY_TAGS));
            if (!this.mTags.contains(tag)) {
                this.mTags.add(tag);
            }
        }
        tagcursor.close();
        this.mTags.removeAll(Arrays.asList("", null));
        if (this.mTags.contains(this.mSelectedTag)) {
            this.mSelectedTag = "";
        }
        saveTags();
    }

    private void fillTags() {
        List<String> tags = new ArrayList<>(this.mTags);
        tags.add(0, getString(R.string.unfiled));
        this.mTagList.setAdapter((ListAdapter) new ArrayAdapter<>(this, (int) R.layout.list_item, (int) R.id.listitemtext, tags));
        if (this.mViewState == 0) {
            this.mTagList.setItemChecked(this.mTags.indexOf(this.mSelectedTag) + 1, true);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void saveTags() {
        Collections.sort(this.mTags, String.CASE_INSENSITIVE_ORDER);
        this.mPrefEditor.putString("NoteTags", TextUtils.join(TAG_DELIM, this.mTags.toArray()));
        this.mPrefEditor.apply();
        this.mNoteListPos = 0;
        if (this.mTagList != null) {
            fillTags();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fillNotes() {
        Cursor cursor = this.mDbHelper.fetchNotes(this.mSelectedTag, this.mSortColumn);
        if (this.mNotesAdapter != null) {
            this.mNotesAdapter.changeCursor(cursor);
        } else {
            this.mNotesAdapter = new NoteListAdapter(this, cursor);
            this.mNotesList.setAdapter((ListAdapter) this.mNotesAdapter);
        }
        this.mNotesList.setColumnWidth(this.mThumbWidth);
        this.mNotesHeaderText.setText(this.mSelectedTag.isEmpty() ? getString(R.string.unfiled) : this.mSelectedTag);
    }

    private void showTutorialDoc() {
        try {
            File file = new File(String.valueOf(Environment.getExternalStorageDirectory().getPath()) + getString(R.string.tempdir), "Tutorial");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(file);
            InputStream in = getResources().openRawResource(R.raw.writetut);
            byte[] buf = new byte[65536];
            while (true) {
                int len = in.read(buf);
                if (len <= 0) {
                    in.close();
                    out.close();
                    Intent intent = new Intent(this, FingerPaint.class);
                    intent.putExtra("Filename", file.getPath());
                    intent.putExtra("TitleString", getString(R.string.tutorial));
                    intent.putExtra(NotesDbAdapter.KEY_ROWID, -1L);
                    startActivityForResult(intent, 2);
                    return;
                }
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
        }
    }

    private void zipAllDocs(String outfilename) {
        File savefolder = new File(this.mPrefs.getString("SaveImgPath", ""));
        if (savefolder.isDirectory()) {
            byte[] buf = new byte[65536];
            try {
                FileOutputStream outfile = new FileOutputStream(outfilename);
                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(outfile));
                try {
                    File[] listFiles = savefolder.listFiles();
                    for (File child : listFiles) {
                        if (!child.isDirectory()) {
                            zos.putNextEntry(new ZipEntry(child.getName()));
                            FileInputStream in = new FileInputStream(child);
                            while (true) {
                                int len = in.read(buf);
                                if (len <= 0) {
                                    break;
                                }
                                zos.write(buf, 0, len);
                            }
                            in.close();
                            zos.closeEntry();
                        }
                    }
                } finally {
                    zos.close();
                    outfile.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private int getVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void showChangelog(Context context) {
        Scanner sc = new Scanner(context.getResources().openRawResource(R.raw.changelog)).useDelimiter("\\A");
        String changelog = sc.hasNext() ? sc.next() : "";
        WebView wv = new WebView(context);
        wv.setBackgroundColor(0);
        wv.loadDataWithBaseURL(null, changelog, "text/html", "UTF-8", null);
        new AlertDialog.Builder(context, 2).setTitle(R.string.changelog_title).setView(wv).setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass2 */

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.imageURI = null;
        this.mNoteListPos = this.mNotesList.getFirstVisiblePosition();
        this.mPrefEditor.putString("SelectedTag", this.mSelectedTag);
        this.mPrefEditor.putInt("NoteListPos", this.mNoteListPos);
        this.mPrefEditor.apply();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        int thumbwidth = atoi(this.mPrefs.getString("ThumbnailSize", getString(R.string.pref_thumbnail_size_default)), this.mThumbWidth);
        if (thumbwidth != this.mThumbWidth) {
            this.mThumbWidth = thumbwidth;
            this.mNotesAdapter = null;
        }
        int sortorder = atoi(this.mPrefs.getString("DocSortOrder", getString(R.string.pref_docsort_default)), 0);
        if (sortorder == 0) {
            this.mSortColumn = "createtime DESC";
        } else if (sortorder == 1) {
            this.mSortColumn = "modifytime DESC";
        } else {
            this.mSortColumn = "title ASC";
        }
        fillNotes();
        if (this.mNoteListPos > 0) {
            this.mNotesList.setSelection(this.mNoteListPos);
        }
        if (this.mPrefs.getBoolean("FirstRun", true)) {
            this.mPrefEditor.putBoolean("FirstRun", false);
            this.mPrefEditor.putInt("currVersionCode", getVersionCode());
            this.mPrefEditor.apply();
            String defpath = String.valueOf(this.mExtStorageDir) + getString(R.string.defaultdir);
            if (new File(defpath).isDirectory()) {
                Intent intent = new Intent(this, ImportActivity.class);
                intent.putExtra(ImportActivity.IMPORT_PATH, defpath);
                startActivity(intent);
                return;
            }
            createPaint();
            fillNotes();
        } else if (getVersionCode() > this.mPrefs.getInt("currVersionCode", 0)) {
            this.mPrefEditor.putInt("currVersionCode", getVersionCode());
            this.mPrefEditor.apply();
            showChangelog(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mDbHelper.close();
        this.mDbHelper = null;
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (this.mMode != 2) {
            menu.add(0, 1, 0, R.string.menu_newfolder).setIcon(R.drawable.ic_menu_add_folder).setShowAsAction(ACTIVITY_SAVEZIP);
            menu.add(0, 2, 0, R.string.menu_insertnote).setIcon(R.drawable.ic_menu_add_doc).setShowAsAction(ACTIVITY_SAVEZIP);
        }
        if (this.mMode != 2) {
            menu.add(0, OPENFILE_ID, 0, R.string.menu_openfile).setIcon(R.drawable.ic_menu_folder);
            menu.add(0, 4, 0, R.string.menu_addfile).setIcon(R.drawable.ic_menu_folder);
            menu.add(0, 5, 0, R.string.menu_exportdocs).setIcon(R.drawable.ic_menu_folder);
            menu.add(0, HELP_ID, 0, R.string.menu_help).setIcon(R.drawable.ic_menu_help);
            menu.add(0, PREFS_ID, 0, R.string.menu_prefs).setIcon(R.drawable.ic_menu_preferences);
        }
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                String newfolder = getString(R.string.menu_newfolder);
                int count = 1;
                while (this.mTags.contains(newfolder)) {
                    newfolder = String.valueOf(getString(R.string.menu_newfolder)) + " (" + Integer.toString(count) + ")";
                    count++;
                }
                this.mSelectedTag = newfolder;
                this.mTags.add(newfolder);
                saveTags();
                fillNotes();
                new EditTextDialog(this, new RenameFolderListener(newfolder), getString(R.string.edit_tag), newfolder).show();
                return true;
            case 2:
                createPaint();
                return true;
            case OPENFILE_ID /*{ENCODED_INT: 3}*/:
            case 4:
                Intent intent = new Intent(this, FileBrowser.class);
                intent.putExtra(FileBrowser.START_PATH, this.mPrefs.getString("OpenFilePath", this.mExtStorageDir));
                startActivityForResult(intent, id == OPENFILE_ID ? 4 : 5);
                return true;
            case 5:
                Intent intent2 = new Intent(this, FileBrowser.class);
                intent2.putExtra(FileBrowser.START_PATH, this.mPrefs.getString("ZipFilePath", this.mExtStorageDir));
                intent2.putExtra(FileBrowser.START_NAME, "WriteBackup.zip");
                intent2.putExtra("MODE", 2);
                startActivityForResult(intent2, ACTIVITY_SAVEZIP);
                return true;
            case ACTIVITY_SAVEZIP /*{ENCODED_INT: 6}*/:
            default:
                return super.onMenuItemSelected(featureId, item);
            case HELP_ID /*{ENCODED_INT: 7}*/:
                showTutorialDoc();
                return true;
            case PREFS_ID /*{ENCODED_INT: 8}*/:
                startActivity(new Intent(this, PaintPreferences.class));
                return true;
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == this.mNotesList) {
            menu.add(0, DELETENOTE_ID, 0, R.string.menu_deletedoc);
            menu.add(0, RENAMENOTE_ID, 0, R.string.menu_rename);
            menu.add(0, MOVENOTE_ID, 0, R.string.menu_movenote);
        } else if (v == this.mTagList && ((AdapterView.AdapterContextMenuInfo) menuInfo).position > 0) {
            menu.add(0, DELETEFOLDER_ID, 0, R.string.menu_deletefolder);
            menu.add(0, RENAMEFOLDER_ID, 0, R.string.menu_rename);
        }
    }

    private class RenameNoteListener implements EditTextDialog.OnTitleSetListener {
        private final long mId;

        public RenameNoteListener(long id) {
            this.mId = id;
        }

        @Override // com.styluslabs.write.EditTextDialog.OnTitleSetListener
        public void titleSet(String title) {
            PaintMain.this.mDbHelper.updateNote(this.mId, NotesDbAdapter.KEY_TITLE, title);
            PaintMain.this.fillNotes();
        }
    }

    private class RenameFolderListener implements EditTextDialog.OnTitleSetListener {
        private final String mOldtag;

        public RenameFolderListener(String oldtag) {
            this.mOldtag = oldtag;
        }

        @Override // com.styluslabs.write.EditTextDialog.OnTitleSetListener
        public void titleSet(String newtag) {
            if (newtag != null && newtag.length() > 0) {
                PaintMain.this.mDbHelper.renameTag(this.mOldtag, newtag);
                PaintMain.this.mTags.remove(this.mOldtag);
                PaintMain.this.mTags.remove(newtag);
                PaintMain.this.mTags.add(newtag);
                PaintMain.this.mSelectedTag = newtag;
                PaintMain.this.saveTags();
                PaintMain.this.fillNotes();
            }
        }
    }

    private void moveNotePrompt(final long _rowid) {
        new AlertDialog.Builder(this).setTitle(R.string.msg_movenote).setAdapter(this.mTagList.getAdapter(), new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass3 */

            public void onClick(DialogInterface dialog, int which) {
                PaintMain.this.mDbHelper.updateNote(_rowid, NotesDbAdapter.KEY_TAGS, which > 0 ? (String) PaintMain.this.mTags.get(which - 1) : "");
                PaintMain.this.fillNotes();
            }
        }).show();
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case DELETENOTE_ID /*{ENCODED_INT: 101}*/:
                Cursor note = this.mDbHelper.fetchNote(info.id);
                String body = note.getString(note.getColumnIndex(NotesDbAdapter.KEY_BODY));
                note.close();
                if (body == null || body.length() <= 1 || body.charAt(0) != 172) {
                    warnDeleteFile(null, info.id);
                    return true;
                }
                warnDeleteFile(body.substring(1), info.id);
                return true;
            case RENAMENOTE_ID /*{ENCODED_INT: 102}*/:
                Cursor note2 = this.mDbHelper.fetchNote(info.id);
                String mTitleString = note2.getString(note2.getColumnIndex(NotesDbAdapter.KEY_TITLE));
                note2.close();
                new EditTextDialog(this, new RenameNoteListener(info.id), getString(R.string.edit_title), mTitleString).show();
                return true;
            case MOVENOTE_ID /*{ENCODED_INT: 103}*/:
                moveNotePrompt(info.id);
                return true;
            case DELETEFOLDER_ID /*{ENCODED_INT: 201}*/:
                if (info.position <= 0) {
                    return true;
                }
                String tag = this.mTags.get(info.position - 1);
                this.mDbHelper.deleteTag(tag);
                this.mTags.remove(tag);
                saveTags();
                if (this.mSelectedTag == tag) {
                    this.mSelectedTag = "";
                }
                fillNotes();
                return true;
            case RENAMEFOLDER_ID /*{ENCODED_INT: 202}*/:
                if (info.position <= 0) {
                    return true;
                }
                String oldtag = this.mTags.get(info.position - 1);
                new EditTextDialog(this, new RenameFolderListener(oldtag), getString(R.string.edit_tag), oldtag).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createPaint() {
        Intent i = new Intent(this, FingerPaint.class);
        i.putExtra(NotesDbAdapter.KEY_TAGS, this.mSelectedTag);
        if (this.imageURI != null) {
            i.putExtra("imageURI", this.imageURI);
        }
        startActivityForResult(i, 0);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
        if (this.mMode == 2) {
            getIntent().putExtra(SEL_NOTE_ID, (int) id);
            setResult(-1, getIntent());
            finish();
            return;
        }
        Cursor note = this.mDbHelper.fetchNote(id);
        Intent i = new Intent(this, FingerPaint.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        if (this.imageURI != null) {
            i.putExtra("imageURI", this.imageURI);
        }
        i.putExtra(NotesDbAdapter.KEY_TAGS, note.getString(note.getColumnIndex(NotesDbAdapter.KEY_TAGS)));
        startActivityForResult(i, 2);
        note.close();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == -1) {
            if (requestCode == 0) {
                this.mNoteListPos = 0;
            } else if (requestCode != 2) {
                if (requestCode == 5 || requestCode == 4) {
                    String filePath = intent.getStringExtra(FileBrowser.RESULT_PATH);
                    this.mPrefEditor.putString("OpenFilePath", filePath);
                    this.mPrefEditor.apply();
                    Intent i = new Intent(this, FingerPaint.class);
                    i.putExtra("Filename", filePath);
                    if (requestCode == 4) {
                        i.putExtra(NotesDbAdapter.KEY_ROWID, -1L);
                    } else if (requestCode == 5) {
                        i.putExtra(NotesDbAdapter.KEY_TAGS, this.mSelectedTag);
                        Cursor note = this.mDbHelper.fetchNoteWithBody("¬" + filePath);
                        if (note != null && note.getCount() > 0) {
                            this.mDbHelper.deleteNote(note.getLong(note.getColumnIndex(NotesDbAdapter.KEY_ROWID)));
                        }
                    }
                    startActivityForResult(i, 2);
                } else if (requestCode == ACTIVITY_SAVEZIP) {
                    String filePath2 = intent.getStringExtra(FileBrowser.RESULT_PATH);
                    this.mPrefEditor.putString("ZipFilePath", filePath2);
                    this.mPrefEditor.apply();
                    zipAllDocs(filePath2);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 4:
                if (this.mViewState == 2) {
                    this.mTagList.setVisibility(0);
                    this.mNotesContainer.setVisibility(PREFS_ID);
                    this.mViewState = 1;
                    return true;
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void warnDeleteFile(final String _filename, final long _rowid) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.confirm_delete_title).setMessage(R.string.confirm_delete_msg).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass4 */

            public void onClick(DialogInterface dialog, int which) {
                PaintMain.this.mDbHelper.deleteNote(_rowid);
                if (_filename != null) {
                    FingerPaint.jniDeleteFile(_filename);
                }
                PaintMain.this.fillNotes();
            }
        }).setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass5 */

            public void onClick(DialogInterface dialog, int which) {
                PaintMain.this.mDbHelper.deleteNote(_rowid);
                PaintMain.this.fillNotes();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.PaintMain.AnonymousClass6 */

            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
}

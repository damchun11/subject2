package com.styluslabs.write;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.styluslabs.write.EditTextDialog;
import com.styluslabs.write.PageSetupDialog;
import com.styluslabs.write.ToolButton;
import com.styluslabs.write.ToolPickerDialog;
import com.styluslabs.write.UndoToolButton;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class FingerPaint extends Activity implements ToolPickerDialog.OnToolChangedListener, EditTextDialog.OnTitleSetListener, PageSetupDialog.OnPageSetupListener, ToolButton.ViewSelectedListener {
    private static final int ID_CANCEL = 109;
    private static final int ID_CAPTUREIMG = 1021;
    private static final int ID_COPYSEL = 106;
    private static final int ID_CUTSEL = 107;
    private static final int ID_DELPAGE = 120;
    private static final int ID_DELSEL = 105;
    private static final int ID_DIRTYSCREEN = 511;
    private static final int ID_DISCARD = 1005;
    private static final int ID_DUPSEL = 130;
    private static final int ID_EMULATEPENBTN = 3000;
    private static final int ID_EXPANDDOWN = 111;
    private static final int ID_EXPANDRIGHT = 112;
    private static final int ID_EXPORTDOC = 1009;
    private static final int ID_EXPORTPDF = 1011;
    private static final int ID_INVSEL = 104;
    private static final int ID_LINKBOOKMARK = 131;
    private static final int ID_LINKSEL = 1012;
    private static final int ID_NEWDOC = 121;
    private static final int ID_NEXTPAGE = 117;
    private static final int ID_PAGEAFTER = 118;
    private static final int ID_PAGEBEFORE = 119;
    private static final int ID_PAGESETUP = 1002;
    private static final int ID_PASTE = 108;
    private static final int ID_PREFERENCES = 1003;
    private static final int ID_PREVPAGE = 116;
    private static final int ID_REDO = 101;
    private static final int ID_RENAME = 1010;
    private static final int ID_RESETZOOM = 115;
    private static final int ID_SAVE = 1001;
    private static final int ID_SAVEDPEN = 2000;
    private static final int ID_SELALL = 102;
    private static final int ID_SELECTIMG = 1020;
    private static final int ID_SELSIMILAR = 103;
    private static final int ID_SENDDOC = 1008;
    private static final int ID_SENDPAGE = 1004;
    private static final int ID_SETPEN = 1000;
    private static final int ID_TOGGLEBOOKMARKS = 510;
    private static final int ID_TOOLMENU = 1007;
    private static final int ID_UNDO = 100;
    private static final int ID_WEBVIEW = 1006;
    private static final int ID_ZOOMALL = 123;
    private static final int ID_ZOOMIN = 113;
    private static final int ID_ZOOMOUT = 114;
    private static final int ID_ZOOMWIDTH = 124;
    public static final int MODE_BOOKMARK = 28;
    public static final int MODE_ERASE = 13;
    public static final int MODE_ERASEFREE = 16;
    public static final int MODE_ERASERULED = 15;
    public static final int MODE_ERASESTROKE = 14;
    public static final int MODE_INSSPACE = 24;
    public static final int MODE_INSSPACEHORZ = 26;
    public static final int MODE_INSSPACERULED = 27;
    public static final int MODE_INSSPACEVERT = 25;
    public static final int MODE_LAST = 29;
    public static final int MODE_MOVESEL = 21;
    public static final int MODE_MOVESELFREE = 22;
    public static final int MODE_MOVESELRULED = 23;
    public static final int MODE_NONE = 10;
    public static final int MODE_PAN = 11;
    public static final int MODE_SELECT = 17;
    public static final int MODE_SELECTLASSO = 20;
    public static final int MODE_SELECTRECT = 18;
    public static final int MODE_SELECTRULED = 19;
    public static final int MODE_STROKE = 12;
    private static final int THUMB_HEIGHT = 625;
    private static final int THUMB_WIDTH = 375;
    private static final int TOOL_LOCKED = -1090518785;
    private static final int TOOL_NOUSE = 0;
    private static final int TOOL_ONEUSE = 1593835775;
    private static boolean staticLoad = true;
    private boolean apiLevel14 = false;
    View browserView = null;
    private ToolButton clipboardButton;
    private final String[] defaultPens = {"0xFF000000", "0xFFFF0000", "0xFF00FF00", "0xFF0000FF", "0xFFFFFF00", "0xFF444444"};
    private int dipHeight;
    private int dipWidth;
    private ToolButton drawButton;
    private ToolButton eraseButton;
    private ToolButton insSpaceButton;
    private PenPreviewView lastPen;
    private int mActiveMode = -1;
    private boolean mAppendPage = false;
    private boolean mBookmarks = false;
    private boolean mCancel = false;
    private int mCurrPenNum = -1;
    private NotesDbAdapter mDbHelper;
    EditText mEditURL = null;
    private String mFilename;
    private Handler mHandler = null;
    private Menu mMenu = null;
    private boolean mModeLocked = false;
    private String mNoteTag;
    private SharedPreferences mPrefs;
    private ToolButton mPressedToolBtn = null;
    private Long mRowId;
    private Runnable mTimerTask = null;
    private String mTitleString;
    private MyView myview = null;
    private ToolButton panButton;
    private PenPreviewView[] penViews;
    private SharedPreferences.Editor prefEditor;
    private ToolButton selectButton;
    LinearLayout splitLayout = null;
    private UndoToolButton undoButton;
    private int volDownAction = TOOL_NOUSE;
    private int volDownState = 1;
    private int volUpAction = TOOL_NOUSE;
    private int volUpState = 1;
    WebView webView = null;

    /* access modifiers changed from: private */
    public interface ImageInputStreamFactory {
        InputStream getStream() throws FileNotFoundException;
    }

    /* access modifiers changed from: private */
    public static native boolean jniCancelLastPan();

    private static native boolean jniCmpFilename(String str);

    /* access modifiers changed from: private */
    public static native void jniCreateHyperRef(String str);

    public static native boolean jniDeleteFile(String str);

    public static native void jniExit();

    private static native String jniGetClickedLink();

    private static native String jniGetConfigValue(String str);

    private static native String jniGetHyperRef();

    private static native int[] jniGetPageProps();

    /* access modifiers changed from: private */
    public static native int[] jniGetUIState();

    public static native void jniInit();

    /* access modifiers changed from: private */
    public static native void jniInputStart(int i, int i2, int i3);

    private static native void jniInsertImage(Bitmap bitmap);

    /* access modifiers changed from: private */
    public static native void jniItemClick(int i);

    private static native boolean jniLoadDocConfig(String str);

    private static native boolean jniLoadGlobalConfig(String str);

    private static native int jniOpenFile(String str);

    private static native void jniPaint(Surface surface);

    private static native void jniPaintThumb(Bitmap bitmap);

    private static native boolean jniSaveFile();

    private static native boolean jniSaveHTML(String str);

    private static native boolean jniSavePDF(String str);

    private static native boolean jniSavePNG(String str);

    private static native boolean jniSetDocConfigValue(String str, String str2);

    private static native boolean jniSetFilename(String str);

    /* access modifiers changed from: private */
    public static native boolean jniSetGlobalConfigValue(String str, String str2);

    private static native boolean jniSetPageProps(String str);

    private static native void jniSetPen(int i, float f, float f2, boolean z);

    /* access modifiers changed from: private */
    public static native void jniSingleInput(float f, float f2, float f3, int i);

    /* access modifiers changed from: private */
    public static native boolean jniTimerEvent();

    /* access modifiers changed from: private */
    public static native void jniTwoInput(float f, float f2, float f3, float f4, int i);

    static {
        System.loadLibrary("scribble");
        jniInit();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(-1);
        this.mDbHelper = new NotesDbAdapter(this);
        this.mDbHelper.open();
        this.mRowId = null;
        this.mFilename = null;
        this.mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefEditor = this.mPrefs.edit();
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState != null || extras == null) {
            this.mRowId = (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
            if (!(this.mRowId == null || this.mRowId.longValue() > 0 || extras == null)) {
                this.mFilename = extras.getString("Filename");
            }
        } else {
            if (extras.containsKey(NotesDbAdapter.KEY_ROWID)) {
                this.mRowId = Long.valueOf(extras.getLong(NotesDbAdapter.KEY_ROWID));
            }
            if (extras.containsKey(NotesDbAdapter.KEY_TAGS)) {
                this.mNoteTag = extras.getString(NotesDbAdapter.KEY_TAGS);
            }
            this.mFilename = extras.getString("Filename");
            if (extras.containsKey("TitleString")) {
                this.mTitleString = extras.getString("TitleString");
            }
        }
        if (extras == null || !extras.getBoolean("importOnly", false)) {
            initHelper();
            if (extras != null && extras.containsKey("imageURI")) {
                doRepaint();
                final Uri streamuri = (Uri) getIntent().getParcelableExtra("imageURI");
                doInsertImage(new ImageInputStreamFactory() {
                    /* class com.styluslabs.write.FingerPaint.AnonymousClass1 */

                    @Override // com.styluslabs.write.FingerPaint.ImageInputStreamFactory
                    public InputStream getStream() throws FileNotFoundException {
                        return FingerPaint.this.getContentResolver().openInputStream(streamuri);
                    }
                });
                getIntent().removeExtra("imageURI");
            }
            if (savedInstanceState != null && savedInstanceState.getBoolean("showWebView", false)) {
                showWebView(true);
                this.webView.restoreState(savedInstanceState);
            }
            this.mHandler = new Handler();
            this.mTimerTask = new Runnable() {
                /* class com.styluslabs.write.FingerPaint.AnonymousClass2 */

                public void run() {
                    FingerPaint.this.mHandler.postDelayed(this, 100);
                    if (FingerPaint.jniTimerEvent()) {
                        FingerPaint.this.doRepaint();
                    }
                }
            };
            return;
        }
        if (staticLoad) {
            initHelper();
        }
        populateFields();
        saveState();
        finish();
    }

    private void initHelper() {
        this.apiLevel14 = Build.VERSION.SDK_INT >= 14 ? true : TOOL_NOUSE;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.dipWidth = (int) (((float) metrics.widthPixels) / metrics.density);
        this.dipHeight = (int) (((float) metrics.heightPixels) / metrics.density);
        if (this.mPrefs.getFloat("preScale", -1.0f) != metrics.density) {
            this.prefEditor.putFloat("preScale", metrics.density);
            this.prefEditor.apply();
        }
        setupPageSizes();
        if (this.mPrefs.getBoolean("KeepScreenOn", false)) {
            getWindow().addFlags(128);
        }
        this.myview = new MyView(this);
        setContentView(this.myview);
        prefsUpdated();
        populateFields();
        this.lastPen = new PenPreviewView(this);
        this.lastPen.setPenProps(this.mPrefs.getString("lastPen", "0xFF000000,1,0,0"));
        if (staticLoad) {
            jniSetPen(this.lastPen.penColor, this.lastPen.penWidth, this.lastPen.penPressure, this.lastPen.penHighlight);
            staticLoad = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.mHandler.removeCallbacks(this.mTimerTask);
        this.prefEditor.putString("lastPen", this.lastPen.getPenProps());
        this.prefEditor.apply();
        saveState();
        if (this.webView != null) {
            this.webView.onPause();
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        updateUI();
        if (this.webView != null) {
            this.webView.onResume();
        }
        if (!jniCmpFilename(this.mFilename)) {
            this.mCancel = true;
            finish();
        }
        this.mHandler.postDelayed(this.mTimerTask, 100);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mTimerTask);
        }
        this.mDbHelper.close();
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, this.mRowId);
        outState.putBoolean("restarting", true);
        if (this.webView != null && this.mMenu != null && this.mMenu.findItem(ID_TOOLMENU).isChecked()) {
            this.webView.saveState(outState);
            outState.putBoolean("showWebView", true);
        }
    }

    /* access modifiers changed from: protected */
    public void setupPageSizes() {
        if (!this.mPrefs.contains("screenPageWidth")) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int rawwidth = metrics.widthPixels;
            int rawheight = metrics.heightPixels;
            try {
                Point rawsize = new Point(rawwidth, rawheight);
                Display.class.getMethod("getRealSize", new Class[TOOL_NOUSE]).invoke(display, new Object[TOOL_NOUSE]);
                rawwidth = rawsize.x;
                rawheight = rawsize.y;
            } catch (Exception e) {
                try {
                    Method mGetRawH = Display.class.getMethod("getRawHeight", new Class[TOOL_NOUSE]);
                    rawwidth = ((Integer) Display.class.getMethod("getRawWidth", new Class[TOOL_NOUSE]).invoke(display, new Object[TOOL_NOUSE])).intValue();
                    rawheight = ((Integer) mGetRawH.invoke(display, new Object[TOOL_NOUSE])).intValue();
                } catch (Exception e2) {
                }
            }
            int pagewidth = ((int) (((float) Math.min(rawwidth, rawheight)) / metrics.density)) - 24;
            int pageheight = ((int) (((float) Math.max(rawwidth, rawheight)) / metrics.density)) - 24;
            this.prefEditor.putInt("screenPageWidth", pagewidth);
            this.prefEditor.putInt("screenPageHeight", pageheight);
            if (!this.mPrefs.contains("pageWidth")) {
                this.prefEditor.putInt("pageWidth", pagewidth);
                this.prefEditor.putInt("pageHeight", pageheight);
                this.prefEditor.putInt("marginLeft", Math.min((int) ID_UNDO, pagewidth / 7));
            }
            this.prefEditor.apply();
        }
    }

    /* access modifiers changed from: protected */
    public String serializePrefs() {
        String prefsXML = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n";
        for (Map.Entry<String, ?> entry : this.mPrefs.getAll().entrySet()) {
            prefsXML = String.valueOf(prefsXML) + String.format("<auto name='%s' value='%s'/>\n", entry.getKey(), entry.getValue().toString());
        }
        return String.valueOf(prefsXML) + "</map>\n";
    }

    /* access modifiers changed from: protected */
    public void prefsUpdated() {
        switch (PaintMain.atoi(this.mPrefs.getString("VolBtnMode", "1"), 1)) {
            case TOOL_NOUSE /*{ENCODED_INT: 0}*/:
                this.volUpAction = TOOL_NOUSE;
                this.volDownAction = TOOL_NOUSE;
                break;
            case 1:
                this.volUpAction = ID_REDO;
                this.volDownAction = ID_UNDO;
                break;
            case 2:
                this.volUpAction = ID_ZOOMIN;
                this.volDownAction = ID_ZOOMOUT;
                break;
            case 3:
                this.volUpAction = ID_PREVPAGE;
                this.volDownAction = ID_NEXTPAGE;
                break;
            case 4:
                this.volUpAction = ID_NEXTPAGE;
                this.volDownAction = ID_PREVPAGE;
                break;
            case 5:
                this.volUpAction = ID_EMULATEPENBTN;
                this.volDownAction = ID_EMULATEPENBTN;
                break;
        }
        this.myview.inputConfig();
        jniLoadGlobalConfig(serializePrefs());
    }

    private void populateFields() {
        String filename = this.mFilename;
        if (this.mRowId != null && this.mRowId.longValue() > 0) {
            Cursor note = this.mDbHelper.fetchNote(this.mRowId.longValue());
            String body = null;
            if (note.getCount() > 0) {
                this.mTitleString = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                body = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
            }
            if (body == null || body.length() <= 0 || body.charAt(TOOL_NOUSE) != 172) {
                this.mCancel = true;
                fatalMessage(getString(R.string.db_error_msg));
            } else {
                filename = body.substring(1);
            }
            note.close();
        }
        if (filename != null) {
            int res = jniOpenFile(filename);
            if (res == -1) {
                this.mCancel = true;
                fatalMessage(String.valueOf(getString(R.string.openerror_msg)) + filename);
            } else if (!(res == 0 || res == -3)) {
                if (filename.endsWith(".svg")) {
                    alertMessage(getString(R.string.open_svg_msg));
                } else {
                    alertMessage(String.valueOf(getString(R.string.openerror_msg)) + filename + "\n" + getString(R.string.missing_pages_msg));
                }
            }
            this.mFilename = filename;
            if (this.mTitleString == null || this.mTitleString.isEmpty()) {
                String doctitle = jniGetConfigValue("docTitle");
                if (doctitle == null || doctitle.isEmpty()) {
                    this.mTitleString = new File(filename).getName();
                } else {
                    this.mTitleString = doctitle;
                }
            }
            if (this.mNoteTag == null) {
                this.mNoteTag = jniGetConfigValue("docTags");
            }
        } else {
            this.mFilename = new File(new File(getSavePath()), String.valueOf(String.valueOf(System.currentTimeMillis())) + ".html").getPath();
            if (this.mTitleString == null || this.mTitleString.isEmpty()) {
                this.mTitleString = new SimpleDateFormat("MMM dd HH:mm").format(new Date());
            }
            jniItemClick(ID_NEWDOC);
            if (!jniSetFilename(this.mFilename)) {
                fatalMessage(String.valueOf(getString(R.string.fnerror_msg)) + this.mFilename);
            }
        }
        setTitle(this.mTitleString);
    }

    /* access modifiers changed from: package-private */
    public void saveState() {
        if (this.mCancel) {
            this.mCancel = false;
            return;
        }
        if (this.mTitleString != null) {
            jniSetDocConfigValue("docTitle", this.mTitleString);
        }
        if (this.mNoteTag != null) {
            jniSetDocConfigValue("docTags", this.mNoteTag);
        }
        if (this.webView != null) {
            jniSetDocConfigValue("lastURL", this.webView.getUrl());
        }
        if (!jniSaveFile()) {
            alertMessage(String.valueOf(getString(R.string.saveerror_msg)) + this.mFilename);
        }
        if (this.mRowId == null) {
            if (this.mFilename != null) {
                this.mRowId = Long.valueOf(this.mDbHelper.createNote(0L, this.mTitleString, "¬" + this.mFilename, this.mNoteTag, null, new File(this.mFilename).lastModified()));
            } else {
                this.mRowId = Long.valueOf(this.mDbHelper.createNote(0L, this.mTitleString, null, this.mNoteTag, null, 0));
            }
        }
        if (this.mRowId.longValue() > 0) {
            updateDBRow();
        }
    }

    private void updateDBRow() {
        try {
            Bitmap thumb = Bitmap.createBitmap(THUMB_WIDTH, THUMB_HEIGHT, Bitmap.Config.ARGB_8888);
            jniPaintThumb(thumb);
            ByteArrayOutputStream thumbout = new ByteArrayOutputStream(234375);
            thumb.compress(Bitmap.CompressFormat.PNG, ID_UNDO, thumbout);
            thumbout.flush();
            thumbout.close();
            this.mDbHelper.updateNote(this.mRowId.longValue(), this.mTitleString, null, this.mNoteTag, thumbout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            fatalMessage(getString(R.string.dbuperr_msg));
        }
    }

    private String getSavePath() {
        String saveImgPath = this.mPrefs.getString("SaveImgPath", "");
        if (!saveImgPath.isEmpty() && new File(saveImgPath).isDirectory()) {
            return saveImgPath;
        }
        String saveImgPath2 = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + getString(R.string.defaultdir);
        this.prefEditor.putString("SaveImgPath", saveImgPath2);
        this.prefEditor.apply();
        new File(saveImgPath2).mkdirs();
        return saveImgPath2;
    }

    private String getTempPath() {
        String tempPath;
        if (this.mPrefs.contains("tempPath")) {
            tempPath = this.mPrefs.getString("tempPath", "");
        } else {
            tempPath = String.valueOf(Environment.getExternalStorageDirectory().getPath()) + getString(R.string.tempdir);
            this.prefEditor.putString("tempPath", tempPath);
            this.prefEditor.apply();
        }
        new File(tempPath).mkdirs();
        return tempPath;
    }

    @Override // com.styluslabs.write.EditTextDialog.OnTitleSetListener
    public void titleSet(String title) {
        this.mTitleString = title;
        setTitle(this.mTitleString);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doCancel() {
        this.mCancel = true;
        jniItemClick(ID_NEWDOC);
        finish();
    }

    private void alertMessage(String msg) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.error).setMessage(msg).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass3 */

            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    private void fatalMessage(String msg) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.error).setCancelable(false).setMessage(msg).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass4 */

            public void onClick(DialogInterface dialog, int which) {
                FingerPaint.this.finish();
            }
        }).show();
    }

    private void confirmDiscardChanges() {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.confirm_discard_title).setMessage(R.string.confirm_discard_msg).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass5 */

            public void onClick(DialogInterface dialog, int which) {
                FingerPaint.this.doCancel();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass6 */

            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

    /* access modifiers changed from: protected */
    public boolean sendBitmap() {
        String str;
        int[] uiState = jniGetUIState();
        int pageNum = uiState[5];
        int totalPages = uiState[6];
        StringBuilder sb = new StringBuilder(String.valueOf(getTitle().toString().replaceAll("\\W+", "_")));
        if (totalPages > 1) {
            str = String.format("_p%02d.png", Integer.valueOf(pageNum));
        } else {
            str = ".png";
        }
        String tempfilepath = String.valueOf(getTempPath()) + sb.append(str).toString();
        if (!jniSavePNG(tempfilepath)) {
            return false;
        }
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setFlags(268435456);
        emailIntent.putExtra("android.intent.extra.SUBJECT", this.mTitleString);
        emailIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + tempfilepath));
        emailIntent.setType("image/png");
        startActivity(Intent.createChooser(emailIntent, getString(R.string.menu_sendpage)));
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean sendDocument() {
        String tempfilepath = String.valueOf(getTempPath()) + getTitle().toString().replaceAll("\\W+", "_") + ".html";
        if (!jniSaveHTML(tempfilepath)) {
            return false;
        }
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setFlags(268435456);
        emailIntent.putExtra("android.intent.extra.SUBJECT", this.mTitleString);
        emailIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + tempfilepath));
        emailIntent.setType("text/html");
        startActivity(Intent.createChooser(emailIntent, getString(R.string.menu_senddoc)));
        return true;
    }

    public static void cleanUp() {
    }

    /* access modifiers changed from: private */
    public class JetWebViewClient extends WebViewClient {
        private JetWebViewClient() {
        }

        /* synthetic */ JetWebViewClient(FingerPaint fingerPaint, JetWebViewClient jetWebViewClient) {
            this();
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            FingerPaint.this.mEditURL.setText(url.replaceFirst("^http://", ""));
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.endsWith(".pdf") && !url.endsWith(".ppt") && !url.endsWith(".pptx")) {
                return false;
            }
            view.loadUrl("http://docs.google.com/viewer?url=" + URLEncoder.encode(url) + "&embedded=false");
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void showWebView(boolean show) {
        int browseridx = 1;
        if (show) {
            LinearLayout.LayoutParams geom = new LinearLayout.LayoutParams(-1, -1, 1.0f);
            if (this.splitLayout == null) {
                createWebView();
                this.splitLayout = new LinearLayout(this);
                setContentView(this.splitLayout);
                this.splitLayout.addView(this.myview, TOOL_NOUSE, geom);
            }
            Display display = getWindowManager().getDefaultDisplay();
            if (display.getWidth() > display.getHeight()) {
                this.splitLayout.setOrientation(TOOL_NOUSE);
                if (!this.mPrefs.getBoolean("leftHanded", false)) {
                    browseridx = TOOL_NOUSE;
                }
                this.splitLayout.addView(this.browserView, browseridx, geom);
            } else {
                this.splitLayout.setOrientation(1);
                this.splitLayout.addView(this.browserView, TOOL_NOUSE, geom);
            }
            this.browserView.setVisibility(TOOL_NOUSE);
        } else if (this.browserView != null) {
            this.browserView.setVisibility(8);
            this.splitLayout.removeView(this.browserView);
        }
    }

    /* access modifiers changed from: protected */
    public void createWebView() {
        this.browserView = getLayoutInflater().inflate(R.layout.web_browser, (ViewGroup) null);
        this.mEditURL = (EditText) this.browserView.findViewById(R.id.editURL);
        Button goButton = (Button) this.browserView.findViewById(R.id.btnGo);
        ImageButton backButton = (ImageButton) this.browserView.findViewById(R.id.btnBack);
        ImageButton fwdButton = (ImageButton) this.browserView.findViewById(R.id.btnFwd);
        this.webView = (WebView) this.browserView.findViewById(R.id.webView);
        this.webView.setWebViewClient(new JetWebViewClient(this, null));
        this.webView.setWebChromeClient(new WebChromeClient());
        WebSettings cfg = this.webView.getSettings();
        cfg.setJavaScriptEnabled(true);
        cfg.setPluginState(WebSettings.PluginState.ON);
        cfg.setBuiltInZoomControls(true);
        if (this.mPrefs.getBoolean("WideWebViewPort", true)) {
            cfg.setUseWideViewPort(true);
            cfg.setLoadWithOverviewMode(true);
        }
        if (this.mPrefs.getBoolean("DesktopUserAgent", false)) {
            this.webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/534.13");
        }
        goButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass7 */

            public void onClick(View v) {
                String url = FingerPaint.this.mEditURL.getText().toString().trim();
                WebView webView = FingerPaint.this.webView;
                if (!url.matches("^\\w+://.*")) {
                    url = "http://" + url;
                }
                webView.loadUrl(url);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass8 */

            public void onClick(View v) {
                if (FingerPaint.this.webView.canGoBack()) {
                    FingerPaint.this.webView.goBack();
                }
            }
        });
        fwdButton.setOnClickListener(new View.OnClickListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass9 */

            public void onClick(View v) {
                if (FingerPaint.this.webView.canGoForward()) {
                    FingerPaint.this.webView.goForward();
                }
            }
        });
        this.mEditURL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass10 */

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 2) {
                    return false;
                }
                FingerPaint.this.webView.loadUrl("http://" + FingerPaint.this.mEditURL.getText().toString());
                return false;
            }
        });
        String lastURL = jniGetConfigValue("lastURL");
        WebView webView2 = this.webView;
        if (lastURL == null || lastURL.isEmpty()) {
            lastURL = "http://www.google.com";
        }
        webView2.loadUrl(lastURL);
    }

    public class MyView extends SurfaceView implements SurfaceHolder.Callback {
        private static final int DETECTED_PEN = 11;
        private static final int FORCED_PEN = -1;
        private static final int ICS_PEN = 2;
        private static final int INPUTEVENT_CANCEL = 2;
        private static final int INPUTEVENT_MOVE = 0;
        private static final int INPUTEVENT_PRESS = 1;
        private static final int INPUTEVENT_RELEASE = -1;
        private static final int INPUTSOURCE_PEN = 2;
        private static final int INPUTSOURCE_TOUCH = 3;
        private static final int MODEMOD_EDGETOP = 16;
        private static final int MODEMOD_ERASE = 1;
        private static final int MODEMOD_NONE = 0;
        private static final int MODEMOD_PENBTN = 2;
        private static final int REDETECT_PEN = 10;
        private static final int SAMSUNG_PEN = 3;
        private static final int TEGRA_PEN = 4;
        private static final int THINKPAD_PEN = 1;
        boolean ignoreTouch = false;
        private final Context mContext;
        private int mPenType = FingerPaint.TOOL_NOUSE;
        private Toast mToast;
        private long mTouchHoldoff = 0;
        private boolean penBtnPressed = false;
        boolean penEvent = false;
        long prevHoverExitTime = 0;
        long prevTouchExitTime = 0;
        private int sPenBtnMode = FingerPaint.TOOL_NOUSE;

        public MyView(Context c) {
            super(c);
            this.mContext = c;
            getHolder().addCallback(this);
            this.mPenType = FingerPaint.this.mPrefs.getInt("PenType", FingerPaint.TOOL_NOUSE);
            if (!FingerPaint.this.mPrefs.contains("PenType") || (this.mPenType >= 10 && this.mPenType < 11)) {
                detectPenType();
            }
        }

        public void inputConfig() {
            if (FingerPaint.this.mPrefs.getBoolean("ForcePen", false)) {
                this.mPenType = -1;
            }
            FingerPaint.jniSetGlobalConfigValue("penAvailable", this.mPenType == 0 ? "0" : "1");
            this.mTouchHoldoff = (long) PaintMain.atoi(FingerPaint.this.mPrefs.getString("TouchHoldoff", "200"), 200);
            if (this.mPenType == 3) {
                this.sPenBtnMode = PaintMain.atoi(FingerPaint.this.mPrefs.getString("penButtonMode", "17"), 17);
            }
        }

        private void detectPenType() {
            if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen.pen")) {
                this.mPenType = 2;
            } else if (this.mPenType >= 10 && this.mPenType < 11) {
                this.mPenType = 11;
            }
            String model = Build.MODEL;
            if (model.equalsIgnoreCase("ThinkPad Tablet")) {
                this.mPenType = 1;
            } else if (model.startsWith("GT-N") || model.startsWith("SM-P") || model.startsWith("SM-N") || (this.mPenType > 0 && (Build.BRAND.equalsIgnoreCase("Samsung") || Build.MANUFACTURER.equalsIgnoreCase("Samsung")))) {
                this.mPenType = 3;
            } else if (model.startsWith("TegraNote") || this.mContext.getPackageManager().hasSystemFeature("com.nvidia.nvsi.feature.DirectStylus") || this.mContext.getPackageManager().hasSystemFeature("com.nvidia.nvsi.product.TegraNOTE7")) {
                this.mPenType = TEGRA_PEN;
            }
            FingerPaint.this.prefEditor.putInt("PenType", this.mPenType);
            if (this.mPenType == 0 && !FingerPaint.this.mPrefs.contains("panFromEdge")) {
                FingerPaint.this.prefEditor.putBoolean("panFromEdge", true);
            }
            FingerPaint.this.prefEditor.apply();
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            FingerPaint.jniItemClick(FingerPaint.ID_DIRTYSCREEN);
            FingerPaint.this.doRepaint();
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
            return false;
        }

        private void inputEvent(MotionEvent event, int eventtype) {
            int i = 1;
            if (event.getPointerCount() > 1) {
                FingerPaint.jniTwoInput(event.getX(FingerPaint.TOOL_NOUSE), event.getY(FingerPaint.TOOL_NOUSE), event.getX(1), event.getY(1), eventtype);
                return;
            }
            if (this.mPenType == TEGRA_PEN) {
                i = 3;
            }
            float ps = (float) i;
            if (eventtype == 0) {
                int n = event.getHistorySize();
                for (int ii = FingerPaint.TOOL_NOUSE; ii < n; ii++) {
                    FingerPaint.jniSingleInput(event.getHistoricalX(ii), event.getHistoricalY(ii), event.getHistoricalPressure(ii) * ps, eventtype);
                }
            }
            FingerPaint.jniSingleInput(event.getX(), event.getY(), event.getPressure() * ps, eventtype);
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0033  */
        /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
        @android.annotation.TargetApi(com.styluslabs.write.FingerPaint.MODE_ERASESTROKE)
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onGenericMotionEvent(android.view.MotionEvent r9) {
            /*
            // Method dump skipped, instructions count: 112
            */
            throw new UnsupportedOperationException("Method not decompiled: com.styluslabs.write.FingerPaint.MyView.onGenericMotionEvent(android.view.MotionEvent):boolean");
        }

        @TargetApi(FingerPaint.MODE_ERASESTROKE)
        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            if (this.mToast != null) {
                this.mToast.cancel();
                this.mToast = null;
            }
            if (!this.ignoreTouch || event.getActionMasked() == 0) {
                switch (event.getActionMasked()) {
                    case FingerPaint.TOOL_NOUSE /*{ENCODED_INT: 0}*/:
                        int modemod = FingerPaint.TOOL_NOUSE;
                        this.penEvent = false;
                        if (FingerPaint.this.apiLevel14) {
                            int tooltype = event.getToolType(FingerPaint.TOOL_NOUSE);
                            if (tooltype == TEGRA_PEN) {
                                this.penEvent = true;
                                modemod = 1;
                            } else {
                                this.penEvent = tooltype == 2 ? true : FingerPaint.TOOL_NOUSE;
                                if ((event.getButtonState() & 2) == 2) {
                                    modemod = 2;
                                }
                            }
                        }
                        if (!this.penEvent && this.mPenType != 0) {
                            switch (this.mPenType) {
                                case 1:
                                    this.penEvent = event.getTouchMajor() == 0.0f ? true : FingerPaint.TOOL_NOUSE;
                                    break;
                                case 2:
                                case 3:
                                    this.penEvent = (event.getSource() & 16386) == 16386 ? true : FingerPaint.TOOL_NOUSE;
                                    break;
                            }
                        } else if (this.penEvent && this.mPenType == 0) {
                            this.mPenType = 11;
                            detectPenType();
                            FingerPaint.this.prefsUpdated();
                        }
                        if (!this.penEvent && event.getEventTime() - this.prevHoverExitTime < this.mTouchHoldoff) {
                            z = true;
                        }
                        this.ignoreTouch = z;
                        if (!this.ignoreTouch) {
                            if ((FingerPaint.this.volUpAction == FingerPaint.ID_EMULATEPENBTN && FingerPaint.this.volUpState == 0) || (FingerPaint.this.volDownAction == FingerPaint.ID_EMULATEPENBTN && FingerPaint.this.volDownState == 0)) {
                                modemod = 2;
                            }
                            int pressedmode = FingerPaint.TOOL_NOUSE;
                            if (FingerPaint.this.mPressedToolBtn != null) {
                                View v = FingerPaint.this.mPressedToolBtn.getActiveView();
                                FingerPaint.this.mPressedToolBtn.disallowSelection();
                                if (!(v == null || v.getTag() == null)) {
                                    pressedmode = ((Integer) v.getTag()).intValue();
                                }
                            }
                            FingerPaint.jniInputStart(this.penEvent ? 2 : 3, modemod, pressedmode);
                            inputEvent(event, 1);
                            break;
                        }
                        break;
                    case 1:
                        if (!this.penEvent) {
                            this.prevTouchExitTime = event.getEventTime();
                        }
                        inputEvent(event, -1);
                        FingerPaint.this.updateUI();
                        break;
                    case 2:
                        inputEvent(event, FingerPaint.TOOL_NOUSE);
                        break;
                    case 3:
                        FingerPaint.jniItemClick(FingerPaint.ID_CANCEL);
                        break;
                    case 5:
                        inputEvent(event, 1);
                        break;
                    case 6:
                        inputEvent(event, -1);
                        break;
                }
                FingerPaint.this.doRepaint();
            }
            return true;
        }
    }

    @Override // com.styluslabs.write.ToolPickerDialog.OnToolChangedListener
    public void toolChanged(int color, float width, float pressure, boolean highlight, int saveslot) {
        jniSetPen(color, width, pressure, highlight);
        this.lastPen.setPenProps(color, width, pressure, highlight);
        if (saveslot >= 0 && saveslot < this.penViews.length && this.penViews[saveslot] != null) {
            this.penViews[saveslot].setPenProps(color, width, pressure, highlight);
            this.prefEditor.putString("savedPen" + saveslot, this.penViews[saveslot].getPenProps());
            this.prefEditor.apply();
        }
        this.mCurrPenNum = saveslot;
    }

    /* access modifiers changed from: protected */
    public void doToolSel() {
        int[] uiState = jniGetUIState();
        float width = -1.0f;
        if (uiState[12] != 28) {
            width = ((float) uiState[8]) / 100.0f;
            doJNIAction(12);
        }
        if (uiState[2] != 0) {
            new ToolPickerDialog(this, this, uiState[9], width, 0.0f, false).show();
        } else {
            new ToolPickerDialog(this, this, this.lastPen.penColor, this.lastPen.penWidth, this.lastPen.penPressure, this.lastPen.penHighlight).show();
        }
    }

    @Override // com.styluslabs.write.PageSetupDialog.OnPageSetupListener
    public void pageSetup(String s) {
        boolean clipped = jniSetPageProps(s);
        jniLoadGlobalConfig(serializePrefs());
        doRepaint();
        updateUI();
        if (clipped) {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.pagesize_title).setMessage(R.string.pagesize_msg).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                /* class com.styluslabs.write.FingerPaint.AnonymousClass11 */

                public void onClick(DialogInterface dialog, int which) {
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                /* class com.styluslabs.write.FingerPaint.AnonymousClass12 */

                public void onClick(DialogInterface dialog, int which) {
                    FingerPaint.jniItemClick(FingerPaint.ID_UNDO);
                    FingerPaint.this.doRepaint();
                }
            }).show();
        }
    }

    /* access modifiers changed from: protected */
    public void doPageSetup() {
        new PageSetupDialog(this, this, this.mPrefs, getString(R.string.menu_pagesetup), jniGetPageProps()).show();
    }

    /* access modifiers changed from: protected */
    public void doRepaint() {
        Surface s = this.myview.getHolder().getSurface();
        if (s.isValid()) {
            jniPaint(s);
        }
    }

    private MenuItem addMenuItem(Menu menu, int id, int titleid, int icon) {
        return addMenuItem(menu, id, titleid, icon, TOOL_NOUSE, TOOL_NOUSE);
    }

    private MenuItem addMenuItem(Menu menu, int id, int titleid, int icon, int action, int group) {
        MenuItem item = menu.add(group, id + 1, TOOL_NOUSE, titleid);
        item.setShowAsAction(action);
        if (icon != 0) {
            item.setIcon(icon);
        }
        return item;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        int showaction = 1;
        createActionBar();
        if (this.dipWidth <= 600) {
            showaction = TOOL_NOUSE;
        }
        addMenuItem(menu, ID_TOGGLEBOOKMARKS, R.string.menu_bookmarks, R.drawable.ic_menu_bookmark, showaction, TOOL_NOUSE).setCheckable(true);
        addMenuItem(menu, ID_PREVPAGE, R.string.menu_prevpage, R.drawable.ic_menu_up, showaction, TOOL_NOUSE);
        addMenuItem(menu, ID_NEXTPAGE, R.string.menu_nextpage, R.drawable.ic_menu_down, showaction, TOOL_NOUSE);
        SubMenu selmenu = menu.addSubMenu(String.valueOf(getString(R.string.menu_selection)) + "...");
        if (this.dipWidth <= 480) {
            addMenuItem(selmenu, ID_CUTSEL, R.string.menu_cut, R.drawable.ic_menu_cut);
            addMenuItem(selmenu, ID_COPYSEL, R.string.menu_copy, R.drawable.ic_menu_copy);
            addMenuItem(selmenu, ID_PASTE, R.string.menu_paste, R.drawable.ic_menu_paste);
            addMenuItem(selmenu, ID_DUPSEL, R.string.menu_duplicate, R.drawable.ic_menu_copy);
            addMenuItem(selmenu, ID_DELSEL, R.string.menu_deletesel, R.drawable.ic_menu_discard);
            addMenuItem(selmenu, ID_SELALL, R.string.menu_selectall, R.drawable.ic_menu_select);
        }
        addMenuItem(selmenu, ID_LINKSEL, R.string.menu_linktoweb, R.drawable.ic_menu_globe);
        addMenuItem(selmenu, ID_LINKBOOKMARK, R.string.menu_linktobookmark, R.drawable.ic_menu_bookmark);
        addMenuItem(selmenu, ID_SELSIMILAR, R.string.menu_selsimilar, R.drawable.ic_menu_select);
        addMenuItem(selmenu, ID_INVSEL, R.string.menu_invsel, R.drawable.ic_menu_select);
        SubMenu docmenu = menu.addSubMenu(String.valueOf(getString(R.string.menu_document)) + "...");
        addMenuItem(docmenu, ID_SENDDOC, R.string.menu_senddoc, TOOL_NOUSE);
        addMenuItem(docmenu, ID_SENDPAGE, R.string.menu_sendpage, TOOL_NOUSE);
        addMenuItem(docmenu, ID_EXPORTDOC, R.string.menu_exportdoc, TOOL_NOUSE);
        addMenuItem(docmenu, ID_EXPORTPDF, R.string.menu_exportpdf, TOOL_NOUSE);
        addMenuItem(docmenu, ID_RENAME, R.string.menu_rename, TOOL_NOUSE);
        addMenuItem(docmenu, ID_DISCARD, R.string.menu_discard, TOOL_NOUSE);
        addMenuItem(menu, ID_PAGESETUP, R.string.menu_pagesetup, TOOL_NOUSE);
        SubMenu pagemenu = menu.addSubMenu(String.valueOf(getString(R.string.menu_page)) + "...");
        addMenuItem(pagemenu, ID_PAGEAFTER, R.string.menu_newpageafter, TOOL_NOUSE);
        addMenuItem(pagemenu, ID_PAGEBEFORE, R.string.menu_newpagebefore, TOOL_NOUSE);
        addMenuItem(pagemenu, ID_DELPAGE, R.string.menu_deletepage, TOOL_NOUSE);
        addMenuItem(pagemenu, ID_EXPANDDOWN, R.string.menu_expanddown, TOOL_NOUSE);
        addMenuItem(pagemenu, ID_EXPANDRIGHT, R.string.menu_expandright, TOOL_NOUSE);
        SubMenu zoommenu = menu.addSubMenu(String.valueOf(getString(R.string.menu_zoom)) + "...");
        addMenuItem(zoommenu, ID_ZOOMIN, R.string.menu_zoomin, TOOL_NOUSE);
        addMenuItem(zoommenu, ID_ZOOMOUT, R.string.menu_zoomout, TOOL_NOUSE);
        addMenuItem(zoommenu, ID_ZOOMALL, R.string.menu_zoompage, TOOL_NOUSE);
        addMenuItem(zoommenu, ID_ZOOMWIDTH, R.string.menu_zoomwidth, TOOL_NOUSE);
        addMenuItem(zoommenu, ID_RESETZOOM, R.string.menu_resetzoom, TOOL_NOUSE);
        SubMenu insmenu = menu.addSubMenu(String.valueOf(getString(R.string.menu_insert)) + "...");
        addMenuItem(insmenu, ID_CAPTUREIMG, R.string.menu_captureimg, R.drawable.ic_menu_camera);
        addMenuItem(insmenu, ID_SELECTIMG, R.string.menu_selectimg, R.drawable.ic_menu_add_pic);
        addMenuItem(menu, ID_WEBVIEW, R.string.menu_webview, TOOL_NOUSE).setCheckable(true);
        addMenuItem(menu, ID_PREFERENCES, R.string.menu_prefs, TOOL_NOUSE);
        this.mModeLocked = false;
        updateUI();
        return true;
    }

    private void enableMenuItem(int id, boolean enabled) {
        MenuItem item = this.mMenu.findItem(id + 1);
        if (item != null) {
            item.setEnabled(enabled);
        }
    }

    /* access modifiers changed from: private */
    public class UndoToolButtonListener implements UndoToolButton.UndoRedoListener {
        private UndoToolButtonListener() {
        }

        /* synthetic */ UndoToolButtonListener(FingerPaint fingerPaint, UndoToolButtonListener undoToolButtonListener) {
            this();
        }

        @Override // com.styluslabs.write.UndoToolButton.UndoRedoListener
        public void undo() {
            FingerPaint.jniItemClick(FingerPaint.ID_UNDO);
            FingerPaint.this.doRepaint();
        }

        @Override // com.styluslabs.write.UndoToolButton.UndoRedoListener
        public void redo() {
            FingerPaint.jniItemClick(FingerPaint.ID_REDO);
            FingerPaint.this.doRepaint();
        }

        @Override // com.styluslabs.write.UndoToolButton.UndoRedoListener
        public boolean canUndo() {
            if (FingerPaint.jniGetUIState()[FingerPaint.TOOL_NOUSE] != 0) {
                return true;
            }
            return false;
        }

        @Override // com.styluslabs.write.UndoToolButton.UndoRedoListener
        public boolean canRedo() {
            if (FingerPaint.jniGetUIState()[1] != 0) {
                return true;
            }
            return false;
        }

        @Override // com.styluslabs.write.UndoToolButton.UndoRedoListener
        public void undoRedoEnd(boolean doUndo) {
            if (doUndo) {
                FingerPaint.jniItemClick(FingerPaint.ID_UNDO);
            }
            FingerPaint.this.doRepaint();
            FingerPaint.this.updateUI();
        }
    }

    @Override // com.styluslabs.write.ToolButton.ViewSelectedListener
    public void viewPressed(ToolButton toolbtn, boolean down, boolean forceupdate) {
        if (down) {
            this.mPressedToolBtn = toolbtn;
        } else {
            this.mPressedToolBtn = null;
        }
        if (forceupdate) {
            this.mModeLocked = false;
            updateUI();
        }
    }

    @Override // com.styluslabs.write.ToolButton.ViewSelectedListener
    public void viewSelected(ToolButton toolbtn, View v) {
        if (v != null && v.getTag() != null) {
            int tag = ((Integer) v.getTag()).intValue();
            if (tag < ID_SAVEDPEN || tag >= this.penViews.length + ID_SAVEDPEN) {
                switch (tag) {
                    case ID_SETPEN /*{ENCODED_INT: 1000}*/:
                        doToolSel();
                        if (toolbtn != null) {
                            toolbtn.setActive(2, -1);
                            return;
                        }
                        return;
                    case MODE_STROKE /*{ENCODED_INT: 12}*/:
                        if (toolbtn != null) {
                            toolbtn.setActive(2, this.mCurrPenNum >= 0 ? this.mCurrPenNum + ID_SAVEDPEN : -1);
                            break;
                        }
                        break;
                }
                this.mModeLocked = false;
                doJNIAction(tag);
                return;
            }
            int pennum = tag - 2000;
            doJNIAction(12);
            jniSetPen(this.penViews[pennum].penColor, this.penViews[pennum].penWidth, this.penViews[pennum].penPressure, this.penViews[pennum].penHighlight);
            this.lastPen.setPenProps(this.penViews[pennum].penColor, this.penViews[pennum].penWidth, this.penViews[pennum].penPressure, this.penViews[pennum].penHighlight);
            this.mCurrPenNum = pennum;
            this.mModeLocked = false;
            if (toolbtn != null) {
                toolbtn.setActive(2, pennum + ID_SAVEDPEN);
            }
        }
    }

    @TargetApi(MODE_ERASESTROKE)
    private void createActionBar() {
        ActionBar actionBar = getActionBar();
        LinearLayout actionBarView = (LinearLayout) getLayoutInflater().inflate(R.layout.action_bar, (ViewGroup) null);
        boolean pressOpen = this.mPrefs.getBoolean("PressOpenMenus", true);
        this.panButton = (ToolButton) actionBarView.findViewById(R.id.action_pan);
        this.panButton.setTag(11);
        this.panButton.setListener(this, false);
        if (!this.mPrefs.getBoolean("ShowPanTool", false)) {
            actionBarView.removeView(this.panButton);
        }
        this.clipboardButton = (ToolButton) actionBarView.findViewById(R.id.action_clipboard);
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_cut, R.string.menu_cut, Integer.valueOf((int) ID_CUTSEL));
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_copy, R.string.menu_copy, Integer.valueOf((int) ID_COPYSEL));
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_paste, R.string.menu_paste, Integer.valueOf((int) ID_PASTE));
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_copy, R.string.menu_duplicate, Integer.valueOf((int) ID_DUPSEL));
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_discard, R.string.menu_deletesel, Integer.valueOf((int) ID_DELSEL));
        this.clipboardButton.addMenuItem(R.drawable.ic_menu_select, R.string.menu_selectall, Integer.valueOf((int) ID_SELALL));
        this.clipboardButton.setListener(this, pressOpen);
        if (this.dipWidth <= 480) {
            actionBarView.removeView(this.clipboardButton);
        }
        this.drawButton = (ToolButton) actionBarView.findViewById(R.id.action_draw);
        this.penViews = new PenPreviewView[this.defaultPens.length];
        int numpens = this.defaultPens.length;
        if (this.dipHeight < 360) {
            numpens = 3;
        } else if (this.dipHeight <= 480) {
            numpens = 4;
        }
        for (int ii = TOOL_NOUSE; ii < numpens; ii++) {
            this.penViews[ii] = this.drawButton.addPenPreview(Integer.valueOf(ii + ID_SAVEDPEN));
            String penwidth = this.mPrefs.getInt("PenType", TOOL_NOUSE) == 4 ? ",1.6,1,0" : ",1,0,0";
            if (this.mPrefs.contains("penColor" + ii)) {
                if (!this.mPrefs.contains("savedPen" + ii)) {
                    this.penViews[ii].setPenProps(this.mPrefs.getInt("penColor" + ii, -16777216), this.mPrefs.getFloat("penWidth" + ii, 1.0f), 0.0f, false);
                    this.prefEditor.putString("savedPen" + ii, this.penViews[ii].getPenProps());
                }
                this.prefEditor.remove("penColor" + ii);
                this.prefEditor.remove("penWidth" + ii);
                this.prefEditor.apply();
            }
            this.penViews[ii].setPenProps(this.mPrefs.getString("savedPen" + ii, String.valueOf(this.defaultPens[ii]) + penwidth));
        }
        if (!this.mPrefs.contains("lastPen")) {
            this.lastPen.setPenProps(this.penViews[TOOL_NOUSE].getPenProps());
            jniSetPen(this.lastPen.penColor, this.lastPen.penWidth, this.lastPen.penPressure, this.lastPen.penHighlight);
        }
        this.drawButton.addMenuItem(R.drawable.ic_menu_set_pen, R.string.menu_custompen, Integer.valueOf((int) ID_SETPEN));
        this.drawButton.addMenuItem(R.drawable.ic_menu_add_bookmark, R.string.menu_addbookmark, 28);
        this.drawButton.setTag(12);
        this.drawButton.setListener(this, pressOpen);
        this.eraseButton = (ToolButton) actionBarView.findViewById(R.id.action_erase);
        this.eraseButton.addMenuItem(R.drawable.ic_menu_erase, R.string.menu_strokeeraser, 14);
        this.eraseButton.addMenuItem(R.drawable.ic_menu_erase_ruled, R.string.menu_rulederaser, 15);
        this.eraseButton.addMenuItem(R.drawable.ic_menu_erase_free, R.string.menu_freeeraser, 16);
        this.eraseButton.setTag(13);
        this.eraseButton.setListener(this, pressOpen);
        jniItemClick(13);
        this.eraseButton.setActive(TOOL_NOUSE, jniGetUIState()[12]);
        this.selectButton = (ToolButton) actionBarView.findViewById(R.id.action_select);
        this.selectButton.addMenuItem(R.drawable.ic_menu_select, R.string.menu_rectsel, 18);
        this.selectButton.addMenuItem(R.drawable.ic_menu_select_ruled, R.string.menu_ruledsel, 19);
        this.selectButton.addMenuItem(R.drawable.ic_menu_lasso_select, R.string.menu_lassosel, 20);
        this.selectButton.setTag(17);
        this.selectButton.setListener(this, pressOpen);
        jniItemClick(17);
        this.selectButton.setActive(TOOL_NOUSE, jniGetUIState()[12]);
        this.insSpaceButton = (ToolButton) actionBarView.findViewById(R.id.action_insert_space);
        this.insSpaceButton.addMenuItem(R.drawable.ic_menu_insert_space, R.string.menu_insspace, 25);
        this.insSpaceButton.addMenuItem(R.drawable.ic_menu_insert_space_ruled, R.string.menu_ruledinsspace, 27);
        this.insSpaceButton.setTag(24);
        this.insSpaceButton.setListener(this, pressOpen);
        jniItemClick(24);
        this.insSpaceButton.setActive(TOOL_NOUSE, jniGetUIState()[12]);
        jniItemClick(12);
        this.undoButton = (UndoToolButton) actionBarView.findViewById(R.id.action_undo);
        this.undoButton.setListener(new UndoToolButtonListener(this, null));
        this.undoButton.setDialSteps(PaintMain.atoi(this.mPrefs.getString("undoCircleSteps", "36"), 36));
        actionBar.setCustomView(actionBarView, new ActionBar.LayoutParams(-2, -1, 5));
        if (this.dipWidth <= 480) {
            actionBar.setDisplayOptions(16);
        } else {
            actionBar.setDisplayOptions(26);
        }
        if (this.apiLevel14) {
            actionBar.setHomeButtonEnabled(true);
        }
        try {
            ((ViewGroup) findViewById(16908290).getParent()).setMotionEventSplittingEnabled(true);
        } catch (Exception e) {
            Log.d("Exception", "failed to set motion event splitting enabled!");
        }
    }

    /* access modifiers changed from: protected */
    public void updateUI() {
        if (this.mMenu != null) {
            int[] uiState = jniGetUIState();
            boolean activeSel = uiState[2] != 0;
            boolean clipboard = uiState[3] != 0;
            int pageNum = uiState[5];
            int totalPages = uiState[6];
            this.mBookmarks = uiState[11] != 0;
            int mode = uiState[12];
            int nextmode = uiState[13];
            boolean linkclicked = uiState[14] != 0;
            enableMenuItem(ID_CUTSEL, activeSel);
            enableMenuItem(ID_COPYSEL, activeSel);
            enableMenuItem(ID_DELSEL, activeSel);
            enableMenuItem(ID_DUPSEL, activeSel);
            enableMenuItem(ID_SELSIMILAR, activeSel);
            enableMenuItem(ID_INVSEL, activeSel);
            enableMenuItem(ID_LINKBOOKMARK, activeSel);
            enableMenuItem(ID_LINKSEL, activeSel);
            enableMenuItem(ID_PASTE, clipboard);
            enableMenuItem(ID_PREVPAGE, pageNum > 1);
            MenuItem bookmarkItem = this.mMenu.findItem(ID_DIRTYSCREEN);
            if (this.mBookmarks != bookmarkItem.isChecked()) {
                bookmarkItem.setChecked(this.mBookmarks);
            }
            if (this.mAppendPage != (pageNum == totalPages)) {
                MenuItem nextPageItem = this.mMenu.findItem(ID_PAGEAFTER);
                this.mAppendPage = pageNum == totalPages;
                nextPageItem.setIcon(this.mAppendPage ? R.drawable.ic_menu_append_page : R.drawable.ic_menu_down);
            }
            MenuItem webViewItem = this.mMenu.findItem(ID_TOOLMENU);
            boolean webviewvis = this.browserView != null && this.browserView.getVisibility() == 0;
            if (webviewvis != webViewItem.isChecked()) {
                webViewItem.setChecked(webviewvis);
            }
            if (this.mActiveMode != mode || !this.mModeLocked) {
                ToolButton oldbtn = modeToButtonIdx(this.mActiveMode);
                if (oldbtn != null) {
                    oldbtn.setActive(TOOL_NOUSE);
                }
                ToolButton newbtn = modeToButtonIdx(mode);
                if (newbtn != null) {
                    if (mode == 12) {
                        this.drawButton.setImageResource(R.drawable.ic_menu_draw);
                        newbtn.setActive(2, this.mCurrPenNum >= 0 ? this.mCurrPenNum + ID_SAVEDPEN : -1);
                    } else {
                        newbtn.setActive(mode == nextmode ? 2 : 1, mode);
                    }
                }
                this.mActiveMode = mode;
                this.mModeLocked = mode == nextmode;
            }
            if (linkclicked) {
                if (this.browserView == null || this.browserView.getVisibility() != 0) {
                    showWebView(true);
                    MenuItem item = this.mMenu.findItem(ID_TOOLMENU);
                    if (item != null) {
                        item.setChecked(true);
                    }
                }
                String url = jniGetClickedLink();
                WebView webView2 = this.webView;
                if (!url.matches("^\\w+://.*")) {
                    url = "http://" + url;
                }
                webView2.loadUrl(url);
            }
        }
    }

    private ToolButton modeToButtonIdx(int mode) {
        switch (mode) {
            case MODE_PAN /*{ENCODED_INT: 11}*/:
                return this.panButton;
            case MODE_STROKE /*{ENCODED_INT: 12}*/:
                return this.drawButton;
            case MODE_ERASE /*{ENCODED_INT: 13}*/:
            case MODE_SELECT /*{ENCODED_INT: 17}*/:
            case MODE_MOVESEL /*{ENCODED_INT: 21}*/:
            case MODE_MOVESELFREE /*{ENCODED_INT: 22}*/:
            case MODE_MOVESELRULED /*{ENCODED_INT: 23}*/:
            case MODE_INSSPACE /*{ENCODED_INT: 24}*/:
            default:
                return null;
            case MODE_ERASESTROKE /*{ENCODED_INT: 14}*/:
            case MODE_ERASERULED /*{ENCODED_INT: 15}*/:
            case MODE_ERASEFREE /*{ENCODED_INT: 16}*/:
                return this.eraseButton;
            case MODE_SELECTRECT /*{ENCODED_INT: 18}*/:
            case MODE_SELECTRULED /*{ENCODED_INT: 19}*/:
            case MODE_SELECTLASSO /*{ENCODED_INT: 20}*/:
                return this.selectButton;
            case MODE_INSSPACEVERT /*{ENCODED_INT: 25}*/:
            case MODE_INSSPACEHORZ /*{ENCODED_INT: 26}*/:
            case MODE_INSSPACERULED /*{ENCODED_INT: 27}*/:
                return this.insSpaceButton;
            case MODE_BOOKMARK /*{ENCODED_INT: 28}*/:
                return this.drawButton;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z;
        int itemid = item.getItemId() - 1;
        if (itemid >= 0) {
            switch (itemid) {
                case ID_SETPEN /*{ENCODED_INT: 1000}*/:
                    doToolSel();
                    break;
                case ID_SAVE /*{ENCODED_INT: 1001}*/:
                    saveState();
                    break;
                case ID_PAGESETUP /*{ENCODED_INT: 1002}*/:
                    doPageSetup();
                    break;
                case ID_PREFERENCES /*{ENCODED_INT: 1003}*/:
                    startActivityForResult(new Intent(this, PaintPreferences.class), ID_PREFERENCES);
                    break;
                case ID_SENDPAGE /*{ENCODED_INT: 1004}*/:
                    sendBitmap();
                    break;
                case ID_DISCARD /*{ENCODED_INT: 1005}*/:
                    confirmDiscardChanges();
                    break;
                case ID_WEBVIEW /*{ENCODED_INT: 1006}*/:
                    if (item.isChecked()) {
                        z = false;
                    } else {
                        z = true;
                    }
                    item.setChecked(z);
                    showWebView(item.isChecked());
                    break;
                case ID_SENDDOC /*{ENCODED_INT: 1008}*/:
                    sendDocument();
                    break;
                case ID_EXPORTDOC /*{ENCODED_INT: 1009}*/:
                case ID_EXPORTPDF /*{ENCODED_INT: 1011}*/:
                    Intent intent = new Intent(this, FileBrowser.class);
                    intent.putExtra(FileBrowser.START_PATH, this.mPrefs.getString("ExportFilePath", "/sdcard"));
                    intent.putExtra("MODE", 2);
                    intent.putExtra(FileBrowser.START_NAME, String.valueOf(getTitle().toString().replaceAll("\\W+", "_")) + (itemid == ID_EXPORTDOC ? ".html" : ".pdf"));
                    startActivityForResult(intent, itemid);
                    break;
                case ID_RENAME /*{ENCODED_INT: 1010}*/:
                    new EditTextDialog(this, this, getString(R.string.edit_title), this.mTitleString).show();
                    break;
                case ID_LINKSEL /*{ENCODED_INT: 1012}*/:
                    linkSelection();
                    break;
                case ID_SELECTIMG /*{ENCODED_INT: 1020}*/:
                    Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                    intent2.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent2, getString(R.string.select_image)), ID_SELECTIMG);
                    break;
                case ID_CAPTUREIMG /*{ENCODED_INT: 1021}*/:
                    Intent intent3 = new Intent();
                    intent3.setAction("android.media.action.IMAGE_CAPTURE");
                    intent3.putExtra("output", Uri.fromFile(new File(getTempPath(), "_camera.jpg")));
                    startActivityForResult(intent3, ID_CAPTUREIMG);
                    break;
                case 16908331:
                    finish();
                    break;
                default:
                    doJNIAction(itemid);
                    break;
            }
        }
        return true;
    }

    private void linkSelection() {
        String href = jniGetHyperRef();
        if (href.length() < 1 && this.webView != null) {
            href = this.webView.getUrl();
        }
        new EditTextDialog(this, new EditTextDialog.OnTitleSetListener() {
            /* class com.styluslabs.write.FingerPaint.AnonymousClass13 */

            @Override // com.styluslabs.write.EditTextDialog.OnTitleSetListener
            public void titleSet(String title) {
                FingerPaint.jniCreateHyperRef(title);
            }
        }, getString(R.string.set_link_target), href).show();
    }

    private void doJNIAction(int id) {
        jniItemClick(id);
        doRepaint();
        updateUI();
    }

    private void doInsertImage(ImageInputStreamFactory streamfactory) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 1;
        Bitmap img = null;
        do {
            try {
                img = BitmapFactory.decodeStream(streamfactory.getStream(), null, opt);
            } catch (OutOfMemoryError e) {
            } catch (Exception e2) {
            }
            opt.inSampleSize *= 2;
            if (img != null) {
                break;
            }
        } while (opt.inSampleSize <= 16);
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            Toast.makeText(this, (int) R.string.noimage_msg, (int) TOOL_NOUSE).show();
        } else {
            jniInsertImage(img);
        }
        doRepaint();
        updateUI();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == ID_CAPTUREIMG && resultCode == -1) {
            final File streamfile = new File(getTempPath(), "_camera.jpg");
            doInsertImage(new ImageInputStreamFactory() {
                /* class com.styluslabs.write.FingerPaint.AnonymousClass14 */

                @Override // com.styluslabs.write.FingerPaint.ImageInputStreamFactory
                public InputStream getStream() throws FileNotFoundException {
                    return new FileInputStream(streamfile);
                }
            });
        } else if (requestCode == ID_SELECTIMG && resultCode == -1) {
            final Uri streamuri = intent.getData();
            doInsertImage(new ImageInputStreamFactory() {
                /* class com.styluslabs.write.FingerPaint.AnonymousClass15 */

                @Override // com.styluslabs.write.FingerPaint.ImageInputStreamFactory
                public InputStream getStream() throws FileNotFoundException {
                    return FingerPaint.this.getContentResolver().openInputStream(streamuri);
                }
            });
        } else if ((requestCode == ID_EXPORTDOC || requestCode == ID_EXPORTPDF) && resultCode == -1) {
            String filePath = intent.getStringExtra(FileBrowser.RESULT_PATH);
            this.prefEditor.putString("ExportFilePath", filePath);
            this.prefEditor.apply();
            if (requestCode == ID_EXPORTDOC) {
                jniSaveHTML(filePath);
            } else if (requestCode == ID_EXPORTPDF) {
                jniSavePDF(filePath);
            }
        } else if (requestCode == ID_PREFERENCES) {
            if (!this.mPrefs.contains("PenType")) {
                finish();
            }
            prefsUpdated();
            this.mModeLocked = false;
            invalidateOptionsMenu();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 4:
                if (this.mBookmarks) {
                    if (event.getAction() != 0) {
                        return true;
                    }
                    doJNIAction(ID_TOGGLEBOOKMARKS);
                    return true;
                }
                break;
            case MODE_INSSPACE /*{ENCODED_INT: 24}*/:
                if (this.volUpAction != 0) {
                    if (this.volUpAction == ID_EMULATEPENBTN) {
                        this.volUpState = event.getAction();
                        return true;
                    } else if (event.getAction() != 0) {
                        return true;
                    } else {
                        doJNIAction(this.volUpAction);
                        return true;
                    }
                }
                break;
            case MODE_INSSPACEVERT /*{ENCODED_INT: 25}*/:
                if (this.volDownAction != 0) {
                    if (this.volDownAction == ID_EMULATEPENBTN) {
                        this.volDownState = event.getAction();
                        return true;
                    } else if (event.getAction() != 0) {
                        return true;
                    } else {
                        doJNIAction(this.volDownAction);
                        return true;
                    }
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }
}

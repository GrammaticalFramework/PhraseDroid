package org.grammaticalframework.android.phrasedroid;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;

public class PhrasedroidApp extends Application {
    // Logging
    private static final boolean DBG = true;
    private static final String TAG = "PhraseDroid";

    // Preference Keys
    public static final String PREFS_NAME = "PhrasedroidPrefs";
    public static final String TLANG_PREF_KEY = "targetLanguageCode";
    public static final String SLANG_PREF_KEY = "sourceLanguageCode";

    protected PGFThread mPGFThread;

    public void onCreate() {
        super.onCreate();
	if (DBG) Log.i(TAG, "My own App class");
        // // Setup languages
        // SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // // FIXME : do Source language
        // Locale l = Locale.getDefault();
        // Language source = Language.fromCode(l.getLanguage());
        // if (source == null)
        //     source = Language.ENGLISH;
        // // Target language
        // String tLangCode = settings.getString(TLANG_PREF_KEY, null);
        // Language target = Language.fromCode(tLangCode);
        // if (target == null || 
        //     !Arrays.asList(source.getAvailableTargetLanguages()).contains(target))
        //     target = source.getDefaultTargetLanguage();
        // this.setLanguages(source, target);
    }

    // public void setLanguages(Language sLang, Language tLang) {
    //     this.sLang = sLang;
    //     this.tLang = tLang;
    //     // Setup the thread for the pgf
    //     // FIXME : localize the dialog...
    // 	// FIXME: How to displa a progress dialog ????
    //     mPGFThread = new PGFThread(this, sLang, tLang);
    //     mPGFThread.start();
    // }
}

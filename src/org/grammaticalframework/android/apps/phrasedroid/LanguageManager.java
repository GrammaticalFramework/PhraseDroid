package org.grammaticalframework.android.apps.phrasedroid;

import java.util.Locale;
import java.util.Comparator;
import java.util.Arrays;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.util.Log;



/**
 * This class is resposible for managing the application languages,
 * it will do the following things:
 * - it sets and gets the user language from the preferences
 * - it chooses a default user language based on the phone local if non is set in the preferences
 * - it retrieves the list of available languages from the xml array
 * - it sort this list according to the language names in the current local and  take care of removing the user language from the list of possible target languages.
 */
class LanguageManager {

    private static final boolean DBG = false;
    private static final String TAG = "PhraseDroid";

    private static final String PREFS_NAME = "PhraseDroid_preferences";
    private static final String DEFAULT_LANGUAGE = "en";



    Context mContext;

    public LanguageManager(Context mContext) {
	this.mContext = mContext;
	// TODO: cache the array of available languages in a hashtable...
    }
    
    public Language getUserLanguage() {
	SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
	String c = settings.getString("userLanguage", null);
	if (c != null)
	    try {
		return this.getLangForCode(c);
	    } catch (UnknownLanguageException e) {
		if (DBG) Log.w(TAG, "User prefered language isn't available anymore");
	    }
	return this.getDefaultUserLang(); // TODO: save it in preferences to get it faster next time
    }

    public void setUserLanguage(String code) {
	// We need an Editor object to make preference changes.
	// All objects are from android.context.Context
	SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
	SharedPreferences.Editor editor = settings.edit();
	editor.putString("userLanguage", code);
	// Commit the edits!
	editor.commit();
    }

    public void setUserLanguage(Language lang) {
	this.setUserLanguage(lang.getCode());
    }

    public Language getLangForCode(String code) throws UnknownLanguageException {
	TypedArray languageArray = mContext.getResources().obtainTypedArray(R.array.languages);
	TypedArray languageIcons = mContext.getResources().obtainTypedArray(R.array.language_icons);
	
	for (int i = 0 ; i < languageArray.length() ; i++) {
	    String[] languageInfo = languageArray.getString(i).split("\\|");
	    if (languageInfo[0].equals(code)) {
		Locale locale = new Locale(languageInfo[0]);
		String concrete = languageInfo[1];
		int icon = languageIcons.getResourceId(i,0);
		return new Language(locale, concrete, icon);
	    }
	}
	throw new UnknownLanguageException();
    }

    /** This try to guess a default langauge for the user.
     * It tries langauge in this order
     * - a language corresponding to the default locale
     * - english
     * - an arbitrary language from the available languages
     * If all of them fails (ie there is not a single language in the pgf)
     * it throws a runtime exception
     */
    public Language getDefaultUserLang() {
	Locale defaultLocale = Locale.getDefault();
	String code = defaultLocale.getLanguage();
	try {
	    return this.getLangForCode(code); }
	catch (UnknownLanguageException e) {
	    if (DBG) Log.i(TAG, "Cannot find language corresponding to current locale: " + code);
	}
	try {
	    return this.getLangForCode(DEFAULT_LANGUAGE); }
	catch (UnknownLanguageException e) {
	    if (DBG) Log.i(TAG, "Cannot find language corresponding to default: " + DEFAULT_LANGUAGE);
	}
	Language[] ls = this.getAvailableLanguages();
	if (ls.length > 0)
	    return ls[0];
	if (DBG) Log.e(TAG, "Cannot find any language in the resource array !");
	throw new RuntimeException("I cannot find a default langauge !");
    }
    
    /** Provide a list of possible target languages,
     * ommiting the user language
     * and sorted byt language name in the current locale
     */
    public Language[] getTargetLanguages() {
	Language user = getUserLanguage();
	Language[] all = getAvailableLanguages();
	Language[] target = new Language[all.length - 1];
	int k = 0;
	for (int i = 0 ; i < all.length ; i++) {
	    if (!all[i].equals(user)) {
		if (DBG) Log.d(TAG, "Adding target language " + all[i]);
		target[k] = all[i];
		k++;
	    }
	}
	// getAvailableLanguages is already sorted
	//Arrays.sort(target, new LocaleLanguageComparator());
	return target;
    }

    public Language[] getAvailableLanguages() {
	TypedArray languageArray = mContext.getResources().obtainTypedArray(R.array.languages);
	TypedArray languageIcons = mContext.getResources().obtainTypedArray(R.array.language_icons);
	Language[] languages = new Language[languageArray.length()];
	for (int i = 0 ; i < languages.length ; i++) {
	    String[] languageInfo = languageArray.getString(i).split("\\|");
	    Locale locale = new Locale(languageInfo[0]);
	    String concrete = languageInfo[1];
	    int icon = languageIcons.getResourceId(i,0);
	    languages[i] = new Language(locale, concrete, icon);
	}
	Arrays.sort(languages, new LocaleLanguageComparator());
	return languages;
    }

    public class Language{
	public Locale locale;
	public String concrete;
	public int icon;
	
	public Language(Locale locale, String concrete, int icon) {
	    this.locale = locale;
	    this.concrete = concrete;
	    this.icon = icon;
	}

	public String getCode() { return this.locale.getLanguage(); }
	public String getName() { return this.locale.getDisplayLanguage(); }
	
	public boolean equals(Object other) {
	    return (other instanceof Language
		    && this.locale.equals(((Language)other).locale)
		    && this.concrete.equals(((Language)other).concrete));
	}

	public int hashCode() {
	    return this.concrete.hashCode() + this.locale.hashCode();
	}

	public String toString() {
	    return this.locale.getDisplayLanguage()
		+ " (" + this.locale.getLanguage()
		+ "|" + this.concrete + ")";
	}
    }

    
    class LocaleLanguageComparator implements Comparator<Language> {
	// Comparator interface requires defining compare method.
	public int compare(Language a, Language b) {
	    //... Sort according to the locale name
	    return a.locale.getDisplayLanguage().compareTo(b.locale.getDisplayLanguage());
	}
    }

    public class UnknownLanguageException extends Exception {}

}

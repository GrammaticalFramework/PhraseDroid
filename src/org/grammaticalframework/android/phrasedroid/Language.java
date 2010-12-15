package org.grammaticalframework.android.phrasedroid;

import java.util.Locale;

public enum Language {
    BULGARIAN ("PhrasebookBul", new Locale("bg")),
    CATALAN   ("PhrasebookCat", new Locale("ca")),
    DANISH    ("PhrasebookDan", new Locale("da")),
    DUTCH     ("PhrasebookDut", new Locale("nl")),
    ENGLISH   ("PhrasebookEng", Locale.ENGLISH),
    FRENCH    ("PhrasebookFre", Locale.FRENCH),
    GERMAN    ("PhrasebookGer", Locale.GERMAN),
    ITALIAN   ("PhrasebookIta", Locale.ITALIAN),
    NORWEGIAN ("PhrasebookNor", new Locale("no")),
    POLISH    ("PhrasebookPol", new Locale("pl")),
    ROMANIAN  ("PhrasebookRon", new Locale("ro")),
    SPANISH   ("PhrasebookSpa", new Locale("es")),
    SWEDISH   ("PhrasebookSwe", new Locale("sv"));


    String concrete;
    Locale locale;
    Language(String concrete, Locale locale) {
        this.concrete = concrete;
        this.locale = locale;
    }

    public String getName() {
        return this.locale.getDisplayLanguage();
    }

    public Language[] getAvailableTargetLanguages() {
        Language[] ls = this.values();
        Language [] tls = new Language[4];
        int i = 0;
        for (Language l : ls)
            if (l != this) {
                tls[i] = l;
                i++;
            }
        return tls;
    }
    
    public Language getDefaultTargetLanguage() {
        switch (this) {
        case ENGLISH: return FRENCH;
        default: return ENGLISH;
        }
    }
    
    static public Language fromCode(String code) {
        if(code == null)
            return null;
        else if (code.equals("de"))
            return GERMAN;
        else if (code.equals("es"))
            return SPANISH;
        else if (code.equals("fr"))
            return FRENCH;
        else if (code.equals("it"))
            return ITALIAN;
        else if (code.equals("en"))
            return ENGLISH;
        else
            return null;
    }


    public String toString() {
	return this.getName();
    }

}


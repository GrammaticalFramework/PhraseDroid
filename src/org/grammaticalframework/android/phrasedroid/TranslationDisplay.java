package org.grammaticalframework.android.phrasedroid;
/**
 * This class is responsible for the display of the translations.
 * It manages the little display, under the magnets and takes care of displaying all translation
 * when clicked.
 */

class TranslationDisplay {
    PhrasedroidActivity activity;
    boolean is_TTS_available = false;
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ API ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public TranslationDisplay(PhrasedroidActivity activity) {}

    public void setTranslation(Translation t) {}

    public void setTTSAvailable(boolean b) {
	this.is_TTS_available = b;
    }
} 
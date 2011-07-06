package org.grammaticalframework.android.apps.phrasedroid;

import java.util.List;
/** 
 * This class is used to pass ParseResult between thread.
 * A parse result is constitued of a list of possible translations
 * and a list of possible next tokens.
 */
class ParseResult {
    public final List<String> translations;
    public final List<String> disambiguation;
    public final String[] predictions;
    
    public ParseResult(List<String> translations,
		       List<String> disambiguation,
		       String[] predictions){
	this.translations = translations;
	this.predictions = predictions;
	this.disambiguation = disambiguation;
    }
}

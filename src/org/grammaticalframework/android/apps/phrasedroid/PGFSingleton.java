package org.grammaticalframework.android.apps.phrasedroid;

import org.grammaticalframework.PGF;

public class PGFSingleton {

    public ParseResult result;
    public PGF pgf;

    private static PGFSingleton instance = null;
    protected PGFSingleton() {
	// Exists only to defeat instantiation.
    }

    public static PGFSingleton getInstance() {
	if(instance == null) {
	    instance = new PGFSingleton();
	}
	return instance;
    }
}

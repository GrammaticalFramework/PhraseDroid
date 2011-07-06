package org.grammaticalframework.android.apps.phrasedroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.grammaticalframework.Linearizer;
import org.grammaticalframework.PGF;
import org.grammaticalframework.PGFBuilder;
import org.grammaticalframework.Parser;
import org.grammaticalframework.Trees.Absyn.Tree;
import org.grammaticalframework.UnknownLanguageException;
import org.grammaticalframework.android.ui.layout.PredicateLayout;
import org.grammaticalframework.android.ui.view.*;
import org.grammaticalframework.parser.ParseState;

/**
 * 
 */
public class PhrasebookActivity extends Activity
    implements View.OnClickListener
{

    // Logging
    private static final boolean DBG = false;
    private static final String TAG = "PhraseDroid";

    // CONSTANTS
    private static final String DISAMB = "DisambPhrasebookEng";

    // Internal objects
    private LanguageManager mLanguages;
    private LanguageManager.Language sLang;
    private LanguageManager.Language tLang;

    private PGF mPGF;
    private ParseState mParseState;
    private Parser mParser;           // The Parser object to use TODO: we only use it to get the initial parse state. Since this one is expensive to calculate, it could be interesting to cache it and then, we wouldn't need to keep the parser object arround...
    private Linearizer mLinearizer;   // The Linearizer object to use
    private Linearizer mDisambLinearizer;   // The Linearizer object to use

    // UI Objects
    private View translationPanel;
    private ProgressBar mProgressBar;
    // UI Listeners
    MagnetOnClickListener mMagnetOnClickListener;

    // Tasks
    AsyncTask currentTask;


    /* ********************************************************************* */ 
    /*                             Initialisation                            */
    /* ********************************************************************* */ 

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_phrasebook);
        
	if (!setupLanguages()) {
	    Resources res = getResources();
	    String msg = res.getString(R.string.cannot_find_language);
	    toHomeActivity(msg);
	}
	TextView t = (TextView)findViewById(R.id.source_language);
	t.setText(sLang.getName());
	t = (TextView)findViewById(R.id.target_language);
	t.setText(tLang.getName());

	// Get the progress bar
	mProgressBar = (ProgressBar)findViewById(R.id.progress);
	// Load the pgf data in memory
	new LoadPGFTask().execute(sLang.concrete, tLang.concrete, DISAMB);

	// Create listeners
	mMagnetOnClickListener = new MagnetOnClickListener();
	// 
	ComposeMagnet compose =
		(ComposeMagnet)findViewById(R.id.magnets);
	compose.setClearOnClickListener(new ClearOnClickListener());
	compose.setDeleteOnClickListener(new DeleteOnClickListener());
    }

    /** Set up phrasebook languages.
     * return true if succesfull, false if there is a problem.
     */
    private boolean setupLanguages()
    {
	// GET the parameter (code of target language)
	String tLangCode = null;
	Bundle extras = getIntent().getExtras();
	if(extras !=null)
	    tLangCode = extras.getString("org.grammaticalframework.android.apps.phrasedroid,TargetLang");
	// If we are not given a parameter for target language, 
	if (tLangCode == null) {
	    Log.e(TAG, "No language given");
	    return false;
	}
	mLanguages = new LanguageManager(this);
	sLang = mLanguages.getUserLanguage();
	try {
	    tLang = mLanguages.getLangForCode(tLangCode);
	} catch (LanguageManager.UnknownLanguageException e) {
	    Log.e(TAG, "Unknown language");
	    return false;
	}
	return true;
    }

    /**
     * Send the use to the home activity
     */
    public void toHomeActivity() {
	    Intent intent = new Intent(this, HomeActivity.class);
	    startActivity(intent);
    }

    /**
     * Send the use to the home activity, for the given reason.
     * The reason should be a text message and will be displayed to
     * the user in a toast.
     */
    public void toHomeActivity(String reason) {
	Toast.makeText(this, reason, 
		       Toast.LENGTH_SHORT).show();
	this.toHomeActivity();
    }

    /* ********************************************************************* */ 
    /*                               UI Actions                              */
    /* ********************************************************************* */ 

    class MagnetOnClickListener implements View.OnClickListener {
	public void onClick(View v) {
	    if (v instanceof TextView) {
		/* Clicking on a magnet should trigger:
		 * - its addition to the input box
		 * - launch of the corresponding task: ScanParseTask
		 * - removing the current list of magnet (handled by the task)
		 * - displaying a progress message (handled by the task)
		 */
		String token = ((TextView)v).getText().toString();
		new ScanParseTask().execute(token);
		ComposeMagnet compose =
		    (ComposeMagnet)findViewById(R.id.magnets);
		compose.addMagnet(token);
	    }
	}
    }

    class ClearOnClickListener implements View.OnClickListener {
	public void onClick(View v) {
	    if (currentTask != null) return;
	    /* Clicking the clear button should trigger:
	     * - clearing the input box
	     * - launch of the corresponding task: ClearParseTask
	     * - removing the current list of magnet (handled by the task)
	     * - displaying a progress message (handled by the task)
	     */
	    new ClearParseTask().execute();
	    ComposeMagnet compose =
		(ComposeMagnet)findViewById(R.id.magnets);
	    compose.clearMagnets();
	}
    }

    class DeleteOnClickListener implements View.OnClickListener {
	public void onClick(View v) {
	    if (currentTask != null) return;
	    /* Clicking the delete button should trigger:
	     * - removing one magnet from the input box
	     * - Getting the list of remaining magnets
	     * - launch of the corresponding task: FullParseTask
	     * - removing the current list of magnet (handled by the task)
	     * - displaying a progress message (handled by the task)
	     */
	    ComposeMagnet compose =
		(ComposeMagnet)findViewById(R.id.magnets);
	    compose.deleteLastMagnet();
	    String[] tokens = compose.getTokens();
	    new FullParseTask().execute(tokens);
	}
    }

    // needed by View.onClickListener
    public void onClick(View v) {
	if (v == findViewById(R.id.view_all_button)) {
	    Intent intent = new Intent(this, ResultActivity.class);
	    intent.putExtra("org.grammaticalframework.android.apps.phrasedroid.Locale", tLang.getCode());
	    ComposeMagnet compose =
		(ComposeMagnet)findViewById(R.id.magnets);
	    String[] tokens = compose.getTokens();
	    StringBuffer buffer = new StringBuffer();
	    for (String t : tokens) {
		buffer.append(t);
		buffer.append(" ");
	    }
	    String feedbackUrl = String.format("http://www.molto-project.eu/node/1031?grammar=Phrasebook&from=%s&to=%s&input=%s",
					       sLang.getName(), tLang.getName(), buffer.toString());
	    intent.putExtra("org.grammaticalframework.android.apps.phrasedroid.FeedbackURL", feedbackUrl);
	    startActivity(intent);
	}
    }

    public void switchLanguages(View v) {
	ComposeMagnet compose =
	    (ComposeMagnet)findViewById(R.id.magnets);
	compose.clearMagnets();
	PredicateLayout magnetLayout =
	    (PredicateLayout)findViewById(R.id.words_magnets);
	magnetLayout.removeAllViews();
	LanguageManager.Language l = tLang;
	tLang = sLang;
	sLang = l;
	TextView t = (TextView)findViewById(R.id.source_language);
	t.setText(sLang.getName());
	t = (TextView)findViewById(R.id.target_language);
	t.setText(tLang.getName());
	reconfigureLanguages();
    }

    /* ********************************************************************* */ 
    /*                          Application logic                            */
    /* ********************************************************************* */ 


    /**
     * This class is used to load the PGF file asychronously.
     * It display a blocking progress dialog while doing so.
     */
    private class LoadPGFTask extends AsyncTask<String, Void, PGF> {

	private ProgressDialog progress;

	protected void onPreExecute() {
	    // Display loading popup
	    Resources res = getResources();
	    String title = res.getString(R.string.app_name);
	    String text = res.getString(R.string.grammar_loading);
	    this.progress =
		ProgressDialog.show(PhrasebookActivity.this,title,text,true);
	}
	
	protected PGF doInBackground(String... concretes) {
            if (DBG) Log.i(TAG, "Loading PGF");
	    int pgf_res = R.raw.phrasebook;
            InputStream is = getResources().openRawResource(pgf_res);
            try {
		final long begin_time = System.currentTimeMillis();
		PGF pgf = PGFBuilder.fromInputStream(is, concretes);
		final long end_time = System.currentTimeMillis();
		if (DBG) Log.d(TAG, "Grammar loaded in " 
			       + (end_time - begin_time) + "ms");
		return pgf;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UnknownLanguageException e) {
                throw new RuntimeException(e);
            }
	}

	protected void onPostExecute(PGF result) {
	    PGFSingleton.getInstance().pgf = result;
	    mPGF = result;
	    reconfigureLanguages();
	    if (this.progress != null)
		this.progress.dismiss(); // Remove loading popup
	}
    }

    /** This is called both after the pgf is loaded and 
     * when switching languages.
     * it creates the parser and linearizer.
     */
    private void reconfigureLanguages(){
	try {
	    this.mParser = new Parser(mPGF, sLang.concrete);
	    this.mLinearizer = new Linearizer(mPGF, tLang.concrete);
	    this.mDisambLinearizer = new Linearizer(mPGF, DISAMB);
	} catch (UnknownLanguageException e) {
	    throw new RuntimeException(e);
	} catch (Exception e) {
	    throw new RuntimeException("Cannot create the linearizer : "
				       + e);
	}
	new ClearParseTask().execute();
    }


    /**
     * This class is used to parse a list of token. To avoid blocking
     * the UI thread, this is done asychronously.
     * Once finished, it update the magnets and the translations.
     */
    private abstract class AbsParseTask
	extends AsyncTask<String, Void, ParseResult> 
    {
	protected void onPreExecute() {
	    if (currentTask == null) {
		currentTask = this;
		// Display loading wheel
		mProgressBar.setVisibility(View.VISIBLE);
		/* - Remove current magnets
		 * - remove translation panel
		 * - TODO display a progress status instead.
		 */
		PredicateLayout magnetLayout =
		    (PredicateLayout)findViewById(R.id.words_magnets);
		magnetLayout.removeAllViews();
		if (translationPanel != null)
		    translationPanel.setVisibility(View.GONE);
	    } else {
		if (DBG) Log.w(TAG, "There is already a task running !");
		this.cancel(false);
	    }
	}
	
	protected abstract void parseAction(String... tokens);
	
	protected ParseResult doInBackground(String... tokens) {
	    if (DBG) Log.i(TAG, "Parsnig task started...");
	    this.parseAction(tokens);

	    
            Tree[] trees = (Tree[])mParseState.getTrees();
            ArrayList<String> translations = new ArrayList<String>();
            ArrayList<String> disambiguation = new ArrayList<String>();
	    for (Tree t : trees) {
                try {
                    String s = mLinearizer.linearizeString(t);
                    String d = mDisambLinearizer.linearizeString(t);
                    translations.add(s);
                    disambiguation.add(d);
                } catch (java.lang.Exception e) {
                    if (DBG) Log.w(TAG, "No translation (Error during linearization) ");
                }
	    }
	    return new ParseResult(translations, disambiguation, mParseState.predict());
	}

	protected void onPostExecute(ParseResult result) {
	    setMagnets(result.predictions);
	    setTranslations(result);
	    PGFSingleton.getInstance().result = result;
	    mProgressBar.setVisibility(View.INVISIBLE);
	    if (currentTask == this)
		currentTask = null;
	}
    }

    /**
     * This class is used to parse a list of token. To avoid blocking
     * the UI thread, this is done asychronously.
     * Once finished, it update the magnets and the translations.
     */
    private class FullParseTask extends AbsParseTask {
	protected void parseAction(String... tokens) {
            mParseState = mParser.parse(tokens);
	}
    }

    /**
     * This class is used to scan a new token.
     * Once finished, it update the magnets and the translations.
     */
    private class ScanParseTask extends AbsParseTask {
	protected void parseAction(String... tokens) {
	    for (String t : tokens)
		mParseState.scan(t);
	}
    }

    /**
     * This class is used to clear the internal parse state.
     * Once finished, it update the magnets and the translations.
     */
    private class ClearParseTask extends AbsParseTask {
	protected void parseAction(String... tokens) {
            mParseState = mParser.parse();
	}
    }

    public void setMagnets(final String[] magnets) {
        Arrays.sort(magnets);
	//wordsMagnets.addAll(Arrays.asList(magnets));
	// wordsMagnets.add("a");
	//this.magnets_ready = true;
	PredicateLayout magnetLayout =
	    (PredicateLayout)findViewById(R.id.words_magnets);
	for (String word : magnets) {
	    TextView t =
		(TextView)getLayoutInflater().inflate(R.layout.magnet, null);
	    if (DBG) Log.i(TAG, "Adding magnet "+ word);
	    t.setText(word);
	    t.setClickable(true);
	    t.setOnClickListener(mMagnetOnClickListener);
	    magnetLayout.addView(t);
	}
    }

    public void setTranslations(ParseResult result) {
	if (translationPanel == null) {
	    translationPanel = ((ViewStub)findViewById(R.id.stub_translation)).inflate();
	    translationPanel.setVisibility(View.GONE);
	    Button view_all = (Button)findViewById(R.id.view_all_button);
	    view_all.setOnClickListener(this);
	}
	if (!result.translations.isEmpty()) {
	    TextView t = (TextView)findViewById(R.id.translation_text);
	    t.setText(result.translations.get(0));
	    TextView d = (TextView)findViewById(R.id.translation_disamb);
	    if (result.translations.size() > 1)
		d.setText(result.disambiguation.get(0));
	    else
		d.setText("");
	    translationPanel.setVisibility(View.VISIBLE);
	}
    }
}

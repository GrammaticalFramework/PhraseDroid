package org.grammaticalframework.android.apps.phrasedroid;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends ListActivity
    implements TextToSpeech.OnInitListener
{

    // Logging
    private static final boolean DBG = false;
    private static final String TAG = "PhraseDroid";

    // TTS Intent code
    static final int MY_TTS_CHECK_CODE = 2347453;
    

    // Text to speech
    private boolean tts_available = false;
    private TextToSpeech mTextToSpeech;
    private Locale mLocale;

    // Others
    private String feedbackURL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);


	// We'll define a custom screen layout here
	setContentView(R.layout.activity_result);

	ParseResult result = PGFSingleton.getInstance().result;
	setListAdapter(new ParseResultAdapter(this, result));
	ListView lv = getListView();
	lv.setTextFilterEnabled(true);
	Bundle extras = getIntent().getExtras();
	String locale_code = null;
	if(extras !=null) {
	    locale_code = extras.getString("org.grammaticalframework.android.apps.phrasedroid.Locale");
	    feedbackURL = extras.getString("org.grammaticalframework.android.apps.phrasedroid.FeedbackURL");
	}
	// If we are not given a parameter for target language, 
	if (locale_code == null) {
	    if (DBG) Log.w(TAG, "No locale given, using Engish");
	    locale_code = "en";
	}
	mLocale = new Locale(locale_code);

	startTTSInit();
	
	// lv.setOnItemClickListener(new OnItemClickListener() {
	// 	public void onItemClick(AdapterView<?> parent, View view,
	// 				int position, long id) {
	// 	    LanguageManager.Language choice = languages[position];
	// 	    mLanguages.setUserLanguage(choice);
	// 	    String text = String.format(res.getString(R.string.user_language_changed_to), choice.locale.getDisplayLanguage());
	// 	    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	// 	    finish();
	// 	}
	//     });
    }

    class ParseResultAdapter extends BaseAdapter {
	private final Context mContext;
	private List<String> translations;
	private List<String> disambiguation;

	public ParseResultAdapter(Context c) { 
	    mContext = c;
	}

	public ParseResultAdapter(Context c, ParseResult r) { 
	    mContext = c;
	    this.newParseResult(r);
	}

	public void newParseResult(ParseResult r) {
	    this.translations = r.translations;
	    this.disambiguation = r.disambiguation;
	    this.notifyDataSetChanged();
	}
	public int getCount() {
	    if (translations == null)
		return 0;
	    return translations.size();
	}
	public Object getItem(int position) { return position; }
	public long getItemId(int position) { return position; }
	public View getView(int position, View view, ViewGroup parent) {
	    if (view == null)
		view = getLayoutInflater().inflate(R.layout.translation_listitem,
							  null);
	    TextView text = (TextView)view.findViewById(R.id.translation_text);
	    text.setText(translations.get(position));
	    TextView disamb = (TextView)view.findViewById(R.id.translation_disamb);
	    disamb.setText(disambiguation.get(position));
	    Button speech = (Button)view.findViewById(R.id.speak_button);
	    speech.setOnClickListener(new SpeechOnClickListener(translations.get(position)));
	    return view;
	}
    }

    private class SpeechOnClickListener implements View.OnClickListener {
	private final String text;
 	public SpeechOnClickListener(String text) {
	    this.text = text;
	}
	public void onClick(View v) {
	    if (tts_available)
		mTextToSpeech.speak(this.text, TextToSpeech.QUEUE_ADD, null);
	    else {
		Resources res = getResources();
		String text = res.getString(R.string.speech_not_available);
		Toast.makeText(ResultActivity.this, text,
			       Toast.LENGTH_SHORT).show();
	    }
	}
    }


    // ********************************  TTS  *********************************
    
    // Text-To-Speech initialization is done in three (asychronous) steps 
    // coresponding to the three methods below :
    // First : we check if the TTS data is present on the system
    public void startTTSInit() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_TTS_CHECK_CODE);
    }

    // Second: if the data is present, we initialise the TTS engine
    // (otherwise we ask to install it)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_TTS_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTextToSpeech = new TextToSpeech(this, this);
            } else {
                // missing data, install it
		//                Intent installIntent = new Intent();
                //installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                //startActivity(installIntent);
		Log.w(TAG, "No voice data");
            }
        }
    }
    
    // Finally: once the TTS engine is initialized, we set-up the language.
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
	    if (this.mTextToSpeech.isLanguageAvailable(mLocale) >= 0) {
		mTextToSpeech.setLanguage(mLocale);
		this.tts_available = true;
	    } else
		this.tts_available = false;
        }
    }

    public void feedback(View v) {
	if (feedbackURL != null) {
	    Intent i = new Intent(Intent.ACTION_VIEW);
	    i.setData(Uri.parse(feedbackURL));
	    startActivity(i);
	}
    }
}


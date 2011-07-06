package org.grammaticalframework.android.apps.phrasedroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.AdapterView;
import android.view.View;
import android.content.res.Resources;

public class SetUserLanguageActivity extends ListActivity
{
    
    //private LanguageManager mLanguages;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	final LanguageManager mLanguages = new LanguageManager(this);
	final LanguageManager.Language [] languages = mLanguages.getAvailableLanguages();
	final Resources res = getResources();
	String [] languageNames = new String[languages.length];
	for (int i = 0 ; i <  languages.length ; i++)
	    languageNames[i]= languages[i].locale.getDisplayLanguage();
	
	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_language_item, languageNames));
	
	ListView lv = getListView();
	lv.setTextFilterEnabled(true);
	
	lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
		    LanguageManager.Language choice = languages[position];
		    mLanguages.setUserLanguage(choice);
		    String text = String.format(res.getString(R.string.user_language_changed_to), choice.locale.getDisplayLanguage());
		    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
		    finish();
		}
	    });
    }
}
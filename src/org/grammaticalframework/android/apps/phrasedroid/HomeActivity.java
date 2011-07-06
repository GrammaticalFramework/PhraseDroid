package org.grammaticalframework.android.apps.phrasedroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.content.res.Resources;
import android.content.Intent;

/**
 * Front-door {@link Activity} that displays the list of language
 * the user can choose from.
 */
public class HomeActivity extends Activity implements OnItemClickListener
{
    
    private LanguageManager mLanguages;
    private GridView mGridView;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	mLanguages = new LanguageManager(this);
        setContentView(R.layout.activity_home);

	mGridView = (GridView) findViewById(R.id.language_grid);
	mGridView.setAdapter(new LanguageAdapter(this, mLanguages));

	mGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
 	// Display the current user language
	LanguageManager.Language userLang = mLanguages.getUserLanguage();
	Resources res = getResources();
	String text = String.format(res.getString(R.string.user_language_is), userLang.locale.getDisplayLanguage());
	TextView userLangView = (TextView) findViewById(R.id.user_language_view);
	userLangView.setText(text);
	mGridView.setAdapter(new LanguageAdapter(this, mLanguages));

    }


    /** An item on the flag grid is clicked -> we start the phrasebook activity
     */
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	LanguageManager.Language language = (LanguageManager.Language)this.mGridView.getAdapter().getItem(position);
	Intent intent = new Intent(this, PhrasebookActivity.class);
	intent.putExtra("org.grammaticalframework.android.apps.phrasedroid,TargetLang", language.getCode());
	startActivity(intent);
    }

    public void showInformation(View button) {
	Toast.makeText(HomeActivity.this, "Showing informations...", 
				   Toast.LENGTH_SHORT).show();
    }

    public void changeUserLanguage(View button) {
	Intent intent = new Intent(this, SetUserLanguageActivity.class);
	startActivity(intent);
    }

}

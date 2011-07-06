package org.grammaticalframework.android.apps.phrasedroid;

import java.util.Locale;

import android.widget.TextView;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public class LanguageAdapter extends BaseAdapter {

    private Context mContext;
    private LanguageManager.Language[] mLanguages;

    public LanguageAdapter(Context c, LanguageManager l) {
        mContext = c;
	mLanguages = l.getTargetLanguages();
    }

    public int getCount() {
        return mLanguages.length;
    }

    public Object getItem(int position) {
        return mLanguages[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
	    // if it's not recycled, initialize some attributes
	    LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    textView = (TextView)li.inflate(R.layout.home_btn,parent, false);
            //textView = new TextView(mContext, null, R.style.HomeButton);
	    //            textView.setLayoutParams(new GridView.LayoutParams(-1, -2));
	    //            textView.setPadding(0,0,0,0);
            textView.setGravity(1);
        } else {
            textView = (TextView) convertView;
        }
	Locale locale = mLanguages[position].locale;
	String name = locale.getDisplayLanguage();
	int icon = mLanguages[position].icon;

	textView.setCompoundDrawablesWithIntrinsicBounds(0,icon,0,0);
        textView.setText(name);
        return textView;
    }
}
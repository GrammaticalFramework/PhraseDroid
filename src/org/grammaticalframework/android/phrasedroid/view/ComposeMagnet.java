package org.grammaticalframework.android.phrasedroid.view;

import org.grammaticalframework.android.phrasedroid.R;
import se.fnord.android.layout.PredicateLayout;
import android.graphics.*;
import android.content.Context;
import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.view.View;
import java.util.Vector;
import java.util.ArrayList;
import android.widget.*;
import android.util.Log;


public class ComposeMagnet extends LinearLayout {

    // Logging
    private static final boolean DBG = true;
    private static final String TAG = "PhraseDroid";

    // State
    private MagnetWatcher watcher;
    private PredicateLayout mPredLayout;
    private int             mHighlightColor = 0xCC475925;
    private boolean focused = false;
    private String[] predictions = new String[]{};
    private PopupWindow predictionPopup;

    //private final Context mContext;
    private final Vector<String> mMagnets = new Vector<String>();
    //private final OnClickListener mOnClickListener;
    

    public ComposeMagnet(Context context, AttributeSet attrs) {
        super(context, attrs);
	this.setUp(context);
    }

    public ComposeMagnet(Context context) {
        super(context);
	this.setUp(context);
    }

    // Actions
    public void setMagnets(String[] magnets) {
	this.clearMagnets();
	for (String magnet: magnets) {
	    this.mMagnets.add(magnet);
	    TextView t = new Magnet(this.getContext());
	    t.setText(magnet);
	    //t.setClickable(true);
	    //t.setOnTouchListener(this);
	    this.mPredLayout.addView(t, new PredicateLayout.LayoutParams(3, 3));
	}
	
	final ImageView deleteButton = new ImageView(this.getContext());
	deleteButton.setImageResource(R.drawable.ic_input_delete);
	deleteButton.setPadding(0,0,0,0);
	this.mPredLayout.addView(deleteButton);
    }

    public void setMagnets(String[] magnets, String [] predictions) {
	this.setMagnets(magnets);
	this.predictions = predictions;

    }

    public void clearMagnets() {
        mPredLayout.removeAllViews();
	mMagnets.clear();
    }

    public void removeLastMagnet() {}

    public abstract class MagnetWatcher {
	public abstract void onClearMagnets(ComposeMagnet c);
	public abstract void onAddMagnets(ComposeMagnet c, String[] added);
	public abstract void onRemoveMagnets(ComposeMagnet c, String[] removed);
    }

    public void setChangeListener(MagnetWatcher l) {
	this.watcher = l;
    }

    private void setUp(Context context) {
	this.setOrientation(LinearLayout.HORIZONTAL);
	this.setBackgroundColor(Color.WHITE);
	//this.setLayoutParams(containerParams);

	LinearLayout.LayoutParams predLayoutParams
	    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					    LinearLayout.LayoutParams.WRAP_CONTENT,
					    1.0F);
	mPredLayout = new PredicateLayout(context);
	mPredLayout.setLayoutParams(predLayoutParams);
	this.addView(mPredLayout);

	LinearLayout.LayoutParams controlsParams
	    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					    LinearLayout.LayoutParams.WRAP_CONTENT,
					    0.0F);
	final Button clearButton = new Button(context);
	clearButton.setBackgroundResource(R.drawable.ic_clear);
	clearButton.setLayoutParams(controlsParams);
	clearButton.setPadding(0,0,0,0);
	this.addView(clearButton);

	ArrayList<View> focus = new ArrayList();
	focus.add(this);
	setFocusable(true);
	setFocusableInTouchMode(true);
    }

    /*
     * Display 
     * 
     */
    private void showDropDown() {
	if (this.predictionPopup == null) {
	    this.predictionPopup = new PopupWindow(this.getContext());
	}
	PredicateLayout layout = new PredicateLayout(this.getContext());
	PredicateLayout.LayoutParams params = new PredicateLayout.LayoutParams(2, 1);
	for (String magnet: predictions) {
	    TextView t = new Magnet(this.getContext());
	    t.setText(magnet);
	    t.setPadding(10,10,10,10);
	    layout.addView(t, params);
	}
	layout.setPadding(10, 10, 10, 20);
	int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
	int heightSpec = MeasureSpec.UNSPECIFIED;
	layout.measure(widthSpec, heightSpec);
	Log.i(TAG, "content width is " + layout.getWidth());
	this.predictionPopup.setContentView(layout);
	this.predictionPopup.setWidth(layout.getMeasuredWidth());
	this.predictionPopup.setHeight(layout.getMeasuredHeight());
	this.predictionPopup.showAsDropDown(this);
    }
    
    private void hideDropDown() {
	if (this.predictionPopup != null) {
	    this.predictionPopup.dismiss();
	}
    }

    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
	super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	this.focused = gainFocus;
	if (gainFocus) {
	    Log.i(TAG, "I will now display the popup");
	    this.showDropDown();
	} else {
	    this.hideDropDown();
	}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	if (this.focused) {
	    Rect rect = new Rect();
	    Paint paint = new Paint();
	    paint.setStyle(Paint.Style.STROKE);
	    //paint.setColor(mHighlightColor);
	    paint.setColor(Color.GREEN);
	    paint.setStrokeWidth(2);
	    //paint.setShadowLayer(5, 3, 3, Color.BLACK);
	    getLocalVisibleRect(rect);
	    rect.top  += 1;
	    rect.left += 1;
	    rect.bottom -= 1;
	    rect.right  -= 1;
	    canvas.drawRect(rect, paint);
	}
    }
}

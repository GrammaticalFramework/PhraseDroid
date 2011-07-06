package org.grammaticalframework.android.ui.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Vector;
import org.grammaticalframework.android.apps.phrasedroid.R;
import org.grammaticalframework.android.ui.layout.PredicateLayout;

public class ComposeMagnet extends LinearLayout
    implements View.OnClickListener
{

    // Logging
    private static final boolean DBG = false;
    private static final String TAG = "PhraseDroid";

    // State
    private MagnetWatcher watcher;
    private PredicateLayout mPredLayout;
    private int             mHighlightColor = 0xCC475925;
    private boolean focused = false;
    private LayoutInflater inflater;

    // private final Context mContext;
    private final Vector<String> mMagnets = new Vector<String>();
    // private final OnClickListener mOnClickListener;
    
    // UI
    private Button clearButton;
    private View.OnClickListener deleteOnClickListener;
    

    public ComposeMagnet(Context context, AttributeSet attrs) {
        super(context, attrs);
	this.setUp(context);
    }

    public ComposeMagnet(Context context) {
        super(context);
	this.setUp(context);
    }

    private void setUp(Context context) {
	this.setOrientation(LinearLayout.HORIZONTAL);

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
	controlsParams.gravity = Gravity.CENTER;
	clearButton = new Button(context);
	clearButton.setBackgroundResource(R.drawable.ic_clear);
	clearButton.setLayoutParams(controlsParams);
	clearButton.setPadding(0,0,0,0);
	this.addView(clearButton);

	ArrayList<View> focus = new ArrayList();
	focus.add(this);
	setFocusable(true);
	setFocusableInTouchMode(true);
	inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /* ********************************************************************* */ 
    /*                               API Actions                             */
    /* ********************************************************************* */ 
    /** Add a magnet to the compose widget, after the existing ones.
     */
    public void addMagnet(String magnet) {
	this.addMagnet(magnet, true);
    }

    public void addMagnet(String magnet, boolean last) {
	// for (String magnet: magnets) {
	this.mMagnets.add(magnet);
	TextView t = (TextView)inflater.inflate(R.layout.magnet, null);
	t.setText(magnet);
	if (last) {
	    /* Remove delete icon from last magnet */
	    int nb = this.mPredLayout.getChildCount();
	    if (nb > 0) {
		TextView previousMagnet = (TextView)this.mPredLayout.getChildAt(nb-1);
		makeLast(previousMagnet, false);
	    }
	    makeLast(t, true);
	}
	this.mPredLayout.addView(t);
    }

    // make the magnet last (red cross, clickable, fire the deleteonclicklistener)
    private void makeLast(TextView t, boolean last) {
	if (last) {
	    int icon = R.drawable.ic_input_delete;
	    t.setCompoundDrawablesWithIntrinsicBounds(0,0,icon,0);
	    t.setClickable(true);
	    t.setOnClickListener(this);
	} else {
	    t.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
	    t.setClickable(false);
	}
    }

    public void setMagnets(String... magnets) {
	this.clearMagnets();
	for (int i = 0; i < magnets.length ; i++)
	    this.addMagnet(magnets[i], i == magnets.length - 1);
    }

    public void clearMagnets() {
        mPredLayout.removeAllViews();
	mMagnets.clear();
    }

    public void deleteLastMagnet() {
	int nb = mMagnets.size();
	if (nb > 0) {
	    this.mMagnets.remove(nb -1);
	    this.mPredLayout.removeViewAt(nb-1);
	    if (nb > 1) {
		TextView previous = (TextView)this.mPredLayout.getChildAt(nb-2);
		makeLast(previous, true);
	    }
	}

    }

    public void setChangeListener(MagnetWatcher l) {
	this.watcher = l;
    }

    public void setClearOnClickListener(View.OnClickListener l) {
	this.clearButton.setOnClickListener(l);
    }

    public void setDeleteOnClickListener(View.OnClickListener l) {
	this.deleteOnClickListener = l;
    }

    public void onClick(View v) {
	if (this.deleteOnClickListener != null)
	    this.deleteOnClickListener.onClick(v);
    }

    public String[] getTokens() {
	return this.mMagnets.toArray(new String[]{});
    }

    /*
     * Display 
     * 
     */
    // private void showDropDown() {
    // 	if (this.predictionPopup == null) {
    // 	    this.predictionPopup = new PopupWindow(this.getContext());
    // 	}
    // 	PredicateLayout layout = new PredicateLayout(this.getContext());
    // 	PredicateLayout.LayoutParams params = new PredicateLayout.LayoutParams(2, 1);
    // 	for (String magnet: predictions) {
    // 	    TextView t = new Magnet(this.getContext());
    // 	    t.setText(magnet);
    // 	    t.setPadding(10,10,10,10);
    // 	    layout.addView(t, params);
    // 	}
    // 	layout.setPadding(10, 10, 10, 20);
    // 	int widthSpec = MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST);
    // 	int heightSpec = MeasureSpec.UNSPECIFIED;
    // 	layout.measure(widthSpec, heightSpec);
    // 	Log.i(TAG, "content width is " + layout.getWidth());
    // 	this.predictionPopup.setContentView(layout);
    // 	this.predictionPopup.setWidth(layout.getMeasuredWidth());
    // 	this.predictionPopup.setHeight(layout.getMeasuredHeight());
    // 	this.predictionPopup.showAsDropDown(this);
    // }
    
    // private void hideDropDown() {
    // 	if (this.predictionPopup != null) {
    // 	    this.predictionPopup.dismiss();
    // 	}
    // }

    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
	super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	this.focused = gainFocus;
	if (gainFocus) {
	    //Log.i(TAG, "I will now display the popup");
	    //this.showDropDown();
	} else {
	    //this.hideDropDown();
	}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
	if (this.focused) {
	    Rect rect = new Rect();
	    Paint paint = new Paint();
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setColor(R.color.focus);
	    paint.setStrokeWidth(2);
	    getLocalVisibleRect(rect);
	    rect.top  += 1;
	    rect.left += 1;
	    rect.bottom -= 1;
	    rect.right  -= 1;
	    canvas.drawRect(rect, paint);
	}
    }

    public abstract class MagnetWatcher {
	public abstract void onClearMagnets(ComposeMagnet c);
	public abstract void onAddMagnets(ComposeMagnet c, String[] added);
	public abstract void onRemoveMagnets(ComposeMagnet c, String[] removed);
    }

}

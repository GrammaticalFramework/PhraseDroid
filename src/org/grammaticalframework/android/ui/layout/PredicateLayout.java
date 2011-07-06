package org.grammaticalframework.android.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import org.grammaticalframework.android.apps.phrasedroid.R;

/**
 * ViewGroup that arranges child views in a similar way to text,
 * with them laid out one line at a time and "wrapping" to the next line 
 * as needed.
 * 
 * Code licensed under CC-by-SA
 *  
 * @author Henrik Gustafsson
 * @see http://stackoverflow.com/questions/549451/line-breaking-widget-layout-for-android
 * @license http://creativecommons.org/licenses/by-sa/2.5/
 *
 */
public class PredicateLayout extends ViewGroup {

    private int line_height;

    public PredicateLayout(Context context) {
        super(context);
    }

    public PredicateLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;
	int lines = 1;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(
                        MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));

                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
		    lines += 1;
                }

                xpos += childw + lp.horizontal_spacing;
            }
        }
        this.line_height = line_height;

	final int total_height = lines * line_height + getPaddingTop() + getPaddingBottom();

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED){
            height = total_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            if (total_height < height){
                height = total_height;
            }
        }
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
            }
        }
    }




    
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PredicateLayout.LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
	static private final int HSPACE =
	    R.styleable.layout_PredicateLayout_layout_hspace;	
	static private final int VSPACE =
	    R.styleable.layout_PredicateLayout_layout_vspace;
	static private final int DEFAULT_HSPACE = 1;
	static private final int DEFAULT_VSPACE = 1;
        public final int horizontal_spacing;
        public final int vertical_spacing;

        public LayoutParams() {
	    super(0, 0);
	    this.horizontal_spacing = DEFAULT_VSPACE;
	    this.vertical_spacing = DEFAULT_VSPACE;
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
	    super(p);
	    this.horizontal_spacing = DEFAULT_VSPACE;
	    this.vertical_spacing = DEFAULT_VSPACE;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
	    super(c, attrs);
	    this.horizontal_spacing = attrs.getAttributeIntValue(HSPACE, DEFAULT_HSPACE);
	    this.vertical_spacing = attrs.getAttributeIntValue(VSPACE, DEFAULT_VSPACE);
        }


        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            this(0, 0, horizontal_spacing, vertical_spacing);
        }

        /**
         * @param width 
         * @param height
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public LayoutParams(int width, int height, int horizontal_spacing, int vertical_spacing) {
            super(width, height);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }
}

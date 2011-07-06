package org.grammaticalframework.android.phrasedroid.view;

import android.graphics.*;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;

public class Magnet extends TextView {
    int borderColor = Color.BLACK;
    int borderWidth = 2;

    public Magnet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
	this.applyStyle();
    }

    public Magnet(Context context, AttributeSet attrs) {
        super(context, attrs);
	this.applyStyle();
    }

    public Magnet(Context context) {
        super(context);
	this.applyStyle();
    }

    public void setBorderColor(int color) {
	this.borderColor = color;
    }

    private void applyStyle() {
	this.setTextColor(Color.BLACK);
        this.setBackgroundColor(Color.WHITE);
        this.setSingleLine(true);
        this.setPadding(10, 2, 10, 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	int width = getMeasuredWidth() + 2;
     	int height = getMeasuredHeight() + 2;
	setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(this.borderColor);
        paint.setStrokeWidth(0);
	//paint.setShadowLayer(5, 3, 3, Color.BLACK);
        getLocalVisibleRect(rect);
	rect.bottom -= 1;
	rect.right  -= 1;
        canvas.drawRect(rect, paint);
    }
}
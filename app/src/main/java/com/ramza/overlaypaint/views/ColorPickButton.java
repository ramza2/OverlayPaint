package com.ramza.overlaypaint.views;

import com.ramza.overlaypaint.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorPickButton extends View {
	
	private int color = Color.BLACK;
	private Paint drawPaint;
	private float radius;
	
	private float centerX;
	private float centerY;
	
	private final float CIRCLE_PADDING;

	public ColorPickButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		CIRCLE_PADDING = context.getResources().getDimension(R.dimen.circle_padding);
		init();
	}

	public ColorPickButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		CIRCLE_PADDING = context.getResources().getDimension(R.dimen.circle_padding);
		init();
	}

	public ColorPickButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		CIRCLE_PADDING = context.getResources().getDimension(R.dimen.circle_padding);
		init();
	}

	private void init(){
		setFocusable(true);
		setClickable(true);
		
		drawPaint = new Paint();
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		drawPaint.setColor(color);
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		int width = right - left;
		int height = bottom - top;
		int shortSize = width < height ? width : height;
		
		float circleSize = ((float)shortSize - (CIRCLE_PADDING*2));
		radius = circleSize / 2.0f;
		
		centerX = (float)width / 2.0f;
		centerY = (float)height / 2.0f;
		
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawCircle(centerX, centerY, radius, drawPaint);
	}
}

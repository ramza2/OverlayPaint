package com.ramza.overlaypaint.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends View {
	
	public interface OnColorChangedListener {
	    void colorChanged(int color);
	}
	
    private Paint paint;
    private Paint centerPaint;
    private final int[] colors;
    private OnColorChangedListener listener;
    
    private int CENTER_X = 100;
    private int CENTER_Y = 100;
    private int OUTTER_RADIUS = 100;
    private int CENTER_RADIUS = 32;
    private int OUTER_STROKE_WIDTH = 32;
    private int INNER_STROKE_WIDTH = 5;

    private boolean trackingCenter;
    private boolean highlightCenter;
    
    public ColorPicker(Context c) {
        super(c);
        colors = new int[] {
        	0xFFFFFFFF, 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF000000 , 0xFF00FFFF, 0xFF00FF00,
            0xFFFFFF00, 0xFFFF0000, 0xFFFFFFFF
        };
        Shader s = new SweepGradient(0, 0, colors, null);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(OUTER_STROKE_WIDTH);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.RED);
        centerPaint.setStrokeWidth(INNER_STROKE_WIDTH);
    }
    
    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener){
    	this.listener = onColorChangedListener;
    }
    
    public void setInitColor(int color){
    	centerPaint.setColor(color);
    	invalidate();
    }
    
    public int getColor(){
    	return centerPaint.getColor();
    }

    @Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
    	int width = right - left;
    	int height = bottom - top;
    	
    	int minSize = width > height ? height : width;
    	
    	CENTER_X = width / 2;
    	CENTER_Y = height / 2;
    	
    	OUTTER_RADIUS = minSize / 2;
    	CENTER_RADIUS = minSize * 32 / 200;
    	OUTER_STROKE_WIDTH = CENTER_RADIUS;
    	INNER_STROKE_WIDTH = minSize * 5 / 200;
    	
    	paint.setStrokeWidth(OUTER_STROKE_WIDTH);
    	centerPaint.setStrokeWidth(INNER_STROKE_WIDTH);
    	
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
    protected void onDraw(Canvas canvas) {
        float r = OUTTER_RADIUS - paint.getStrokeWidth()*0.5f;

        canvas.translate(CENTER_X, CENTER_Y);

        canvas.drawOval(new RectF(-r, -r, r, r), paint);
        canvas.drawCircle(0, 0, CENTER_RADIUS, centerPaint);

        if (trackingCenter) {
            int c = centerPaint.getColor();
            centerPaint.setStyle(Paint.Style.STROKE);

            if (highlightCenter) {
                centerPaint.setAlpha(0xFF);
            } else {
                centerPaint.setAlpha(0x80);
            }
            canvas.drawCircle(0, 0,
                              CENTER_RADIUS + centerPaint.getStrokeWidth(),
                              centerPaint);

            centerPaint.setStyle(Paint.Style.FILL);
            centerPaint.setColor(c);
        }
    }

    private int floatToByte(float x) {
        int n = java.lang.Math.round(x);
        return n;
    }
    private int pinToByte(int n) {
        if (n < 0) {
            n = 0;
        } else if (n > 255) {
            n = 255;
        }
        return n;
    }

    private int ave(int s, int d, float p) {
        return s + java.lang.Math.round(p * (d - s));
    }

    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = colors[i];
        int c1 = colors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    @SuppressWarnings("unused")
	private int rotateColor(int color, float rad) {
        float deg = rad * 180 / 3.1415927f;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        ColorMatrix cm = new ColorMatrix();
        ColorMatrix tmp = new ColorMatrix();

        cm.setRGB2YUV();
        tmp.setRotate(0, deg);
        cm.postConcat(tmp);
        tmp.setYUV2RGB();
        cm.postConcat(tmp);

        final float[] a = cm.getArray();

        int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
        int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
        int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);

        return Color.argb(Color.alpha(color), pinToByte(ir),
                          pinToByte(ig), pinToByte(ib));
    }

    private static final float PI = 3.1415926f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - CENTER_X;
        float y = event.getY() - CENTER_Y;
        boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                trackingCenter = inCenter;
                if (inCenter) {
                    highlightCenter = true;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if (trackingCenter) {
                    if (highlightCenter != inCenter) {
                        highlightCenter = inCenter;
                        invalidate();
                    }
                } else {
                    float angle = (float)java.lang.Math.atan2(y, x);
                    // need to turn angle [-PI ... PI] into unit [0....1]
                    float unit = angle/(2*PI);
                    if (unit < 0) {
                        unit += 1;
                    }
                    centerPaint.setColor(interpColor(colors, unit));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (trackingCenter) {
                    if (inCenter && listener != null) {
                        listener.colorChanged(centerPaint.getColor());
                    }
                    trackingCenter = false;    // so we draw w/o halo
                    invalidate();
                }
                break;
        }
        return true;
    }
}

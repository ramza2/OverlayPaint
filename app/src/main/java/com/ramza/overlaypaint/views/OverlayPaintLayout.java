package com.ramza.overlaypaint.views;

import org.json.JSONException;

import com.ramza.overlaypaint.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class OverlayPaintLayout extends RelativeLayout{
	private OverlayPaintView overlayPaintView;
	
	public OverlayPaintLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setBackgroundColor(Color.TRANSPARENT);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.overlay_view, null);
		addView(v);
		
		overlayPaintView = (OverlayPaintView) v.findViewById(R.id.paintView);
	}
	
	public void setCurrentOrietation(int orientation){
		overlayPaintView.setCurrentOrietation(orientation);
	}
	
	public String getPathData() throws JSONException{
		return overlayPaintView.getPathData();
	}
	
	public void setPathData(String pathJsonData){
		overlayPaintView.setPathData(pathJsonData);
	}
	
	public void setPaintType(int type){
		overlayPaintView.setType(type);
	}
	
	public void setPenWidth(float penWidth){
		overlayPaintView.setPenWidth(penWidth);
	}

	public void setPaintColor(int color){
		overlayPaintView.setColor(color);
	}
	
	public void clearPaint(){
		overlayPaintView.clear();
	}
	
}

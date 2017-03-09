package com.ramza.overlaypaint.datas;

import java.util.ArrayList;

import android.graphics.PointF;

public class PaintPathData {
	public static final int DRAW = 0;
	public static final int ERASE = 1;
	
	private int type;
	private float penWidth;
	private int color;
	private int orientation;
	private ArrayList<PointF> pointList;
	
	public PaintPathData() {
		// TODO Auto-generated constructor stub
		pointList = new ArrayList<PointF>();
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getPenWidth() {
		return penWidth;
	}

	public void setPenWidth(float penWidth) {
		this.penWidth = penWidth;
	}

	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

	public int getOrientation() {
		return orientation;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	public PointF getPoint(int index){
		return pointList.get(index);
	}
	
	public void addPoint(PointF point){
		pointList.add(point);
	}
	
	public int getPointSize(){
		return pointList.size();
	}
}

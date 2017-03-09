package com.ramza.overlaypaint.views;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ramza.overlaypaint.consts.OverlayPaintDatas;
import com.ramza.overlaypaint.datas.PaintPathData;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OverlayPaintView extends View {
	private ArrayList<PaintPathData> pathList;
	
	private Paint drawPaint;
	
	private int currentColor = Color.BLACK;
	
	public final float DEFAULT_STROKE_WIDTH = 1.0f;
	
	private PaintPathData drawingPathData;
	
	private int currentOrientation;
	
	private int currentType = PaintPathData.DRAW;
	
	private float currentPenWidth = DEFAULT_STROKE_WIDTH;

	public OverlayPaintView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public OverlayPaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public OverlayPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init(){
		setBackgroundColor(Color.TRANSPARENT);
		pathList = new ArrayList<PaintPathData>();
		drawPaint = new Paint();
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		drawPaint.setStyle(Style.STROKE);
		drawPaint.setStrokeJoin(Join.ROUND);
		drawPaint.setStrokeCap(Cap.ROUND);
	}
	
	public void clear(){
		pathList.clear();
		invalidate();
	}	

	public void setType(int type){
		this.currentType = type;
	}
	
	public void setPenWidth(float penWidth){
		this.currentPenWidth = penWidth;
	}
	
	public void setColor(int color) {
		this.currentColor = color;
	}

	public void setCurrentOrietation(int orientation) {
		this.currentOrientation = orientation;
		invalidate();
	}

	public void setPathData(String pathJsonData){
		if(pathJsonData == null) return;
		
		try {
			JSONObject jsonObject = new JSONObject(pathJsonData);
			JSONArray paths = jsonObject.getJSONArray(OverlayPaintDatas.PATHS);
			int pathsCount = paths.length();
			for (int i = 0; i < pathsCount; i++) {
				JSONObject path = paths.getJSONObject(i);
				int type = path.getInt(OverlayPaintDatas.TYPE);
				int color = path.getInt(OverlayPaintDatas.COLOR);
				float penWidth = (float) path.getDouble(OverlayPaintDatas.PEN_WIDTH);
				int orientation = path.getInt(OverlayPaintDatas.ORIENTATION);
				PaintPathData paintPathData = new PaintPathData();
				paintPathData.setType(type);
				paintPathData.setColor(color);
				paintPathData.setPenWidth(penWidth);
				paintPathData.setOrientation(orientation);
				JSONArray points = path.getJSONArray(OverlayPaintDatas.POINTS);
				int pointCount = points.length();
				for (int j = 0; j < pointCount; j++) {
					JSONObject pointObj = points.getJSONObject(j);
					float x = (float) pointObj.getDouble(OverlayPaintDatas.X);
					float y = (float) pointObj.getDouble(OverlayPaintDatas.Y);
					PointF pointF = new PointF(x, y);
					paintPathData.addPoint(pointF);
				}
				pathList.add(paintPathData);
			}
			invalidate();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPathData() throws JSONException{
		if(pathList != null){
			int pathCount = pathList.size();
			JSONObject pathsObj = new JSONObject();
			JSONArray pathsArray = new JSONArray();
			for (int i = 0; i < pathCount; i++) {
				PaintPathData paintPathData = pathList.get(i);
				JSONObject path = new JSONObject();
				path.put(OverlayPaintDatas.TYPE, paintPathData.getType());
				path.put(OverlayPaintDatas.COLOR, paintPathData.getColor());
				path.put(OverlayPaintDatas.PEN_WIDTH, paintPathData.getPenWidth());
				path.put(OverlayPaintDatas.ORIENTATION, paintPathData.getOrientation());
				int pointCount = paintPathData.getPointSize();
				JSONArray pointArray = new JSONArray();
				for (int j = 0; j < pointCount; j++) {
					PointF pointF = paintPathData.getPoint(j);
					JSONObject pointObj = new JSONObject();
					pointObj.put(OverlayPaintDatas.X, pointF.x);
					pointObj.put(OverlayPaintDatas.Y, pointF.y);
					pointArray.put(pointObj);	
				}
				path.put(OverlayPaintDatas.POINTS, pointArray);
				pathsArray.put(path);
			}
			pathsObj.put(OverlayPaintDatas.PATHS, pathsArray);
			
			return pathsObj.toString();
		}
		
		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawingPathData = new PaintPathData();
			drawingPathData.setType(this.currentType);
			drawingPathData.setPenWidth(this.currentPenWidth);
			drawingPathData.setColor(this.currentColor);
			drawingPathData.setOrientation(currentOrientation);
			pathList.add(drawingPathData);
			break;
		case MotionEvent.ACTION_MOVE:
			if(drawingPathData != null){
				float x = event.getX();
				float y = event.getY();
				PointF pointF = new PointF(x, y);
				drawingPathData.addPoint(pointF);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			drawingPathData = null;
		default:
			break;
		}
		invalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int pathCount = pathList.size();
		Path path = new Path();
		for (int i = 0; i <pathCount; i++) {
			PaintPathData paintPathData = pathList.get(i);
			int pointCount = paintPathData.getPointSize();
			int type = paintPathData.getType();
			int paintColor = paintPathData.getColor();
			float penWdith = paintPathData.getPenWidth();
			int paintOrientation = paintPathData.getOrientation();
			path.reset();
			for (int j = 0; j <pointCount; j++) {
				PointF pointF = paintPathData.getPoint(j);
				float x = 0;
				float y = 0;
				
				if(paintOrientation == currentOrientation){
					x = pointF.x;
					y = pointF.y;
				}else if(paintOrientation == Configuration.ORIENTATION_PORTRAIT){
					x = pointF.y;
					y = getHeight() - pointF.x;
				}else if(paintOrientation == Configuration.ORIENTATION_LANDSCAPE){
					x = getWidth() - pointF.y;
					y = pointF.x;
				}
				
				if(j == 0){
					path.moveTo(x, y);
				}else{
					path.lineTo(x, y);
				}
			}
			if(type == PaintPathData.DRAW ){
				drawPaint.setXfermode(null);
				drawPaint.setColor(paintColor);
				drawPaint.setStrokeWidth(penWdith);
			}else if(type == PaintPathData.ERASE){
				drawPaint.setXfermode(new PorterDuffXfermode(
	                    PorterDuff.Mode.CLEAR));
				drawPaint.setStrokeWidth(penWdith);
			}
			
			canvas.drawPath(path, drawPaint);
		}
	}
}

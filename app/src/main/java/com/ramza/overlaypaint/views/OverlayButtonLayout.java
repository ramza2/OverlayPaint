package com.ramza.overlaypaint.views;

import com.ramza.overlaypaint.R;
import com.ramza.overlaypaint.consts.OverlayPaintDatas;
import com.ramza.overlaypaint.datas.PaintPathData;
import com.ramza.overlaypaint.service.OverlayPaintService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class OverlayButtonLayout extends RelativeLayout implements OnClickListener{
	
	private LinearLayout viewTypeLayout;
	private LinearLayout editTypeLayout;
	
	private ImageButton eraseBtn;
	private ColorPickButton colorPickButton;
	private Spinner penWidthSpinner;
	
	private boolean touchDown = false;
	
	private float startX = -1;
	private float startY = -1;
	private float lX;
	private float lY;
	
	private WindowManager windowManager;
	
	private int type = PaintPathData.DRAW;

	public OverlayButtonLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		setBackgroundColor(Color.parseColor("#44ffffff"));
		
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflater.inflate(R.layout.overlay_buttons, null);
		addView(v);
		
		viewTypeLayout = (LinearLayout) v.findViewById(R.id.viewTypeLayout);
		editTypeLayout = (LinearLayout)v.findViewById(R.id.editTypeLayout);
		
		ImageButton editBtn = (ImageButton) v.findViewById(R.id.editBtn);
		editBtn.setOnClickListener(this);
		ImageButton hideBtn = (ImageButton)v.findViewById(R.id.hideBtn);
		hideBtn.setOnClickListener(this);
		ImageButton exitBtn = (ImageButton)v.findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(this);
		ImageButton saveBtn = (ImageButton)v.findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(this);
		eraseBtn = (ImageButton)v.findViewById(R.id.eraseBtn);
		eraseBtn.setOnClickListener(this);
		ImageButton clearBtn = (ImageButton)v.findViewById(R.id.clearBtn);
		clearBtn.setOnClickListener(this);
		ImageButton cancelBtn = (ImageButton)v.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(this);
		penWidthSpinner = (Spinner) v.findViewById(R.id.penWidthSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.pen_sizes, R.layout.spinner_text);
		adapter.setDropDownViewResource(R.layout.spinner_item_text);
		penWidthSpinner.setAdapter(adapter);
		penWidthSpinner.getBackground().setAlpha(68);
		penWidthSpinner.setOnItemSelectedListener((OverlayPaintService)context);
		
		colorPickButton = (ColorPickButton)v.findViewById(R.id.colorPickButton);
		colorPickButton.setOnClickListener(this);
	}
	
	public void setPaintType(int type){
		this.type = type;
		switch (type) {
		case PaintPathData.DRAW:
			eraseBtn.setSelected(false);
			break;
		case PaintPathData.ERASE:
			eraseBtn.setSelected(true);
			break;
		default:
			break;
		}
	}
	
	public void setLayoutType(int type){
		switch (type) {
		case OverlayPaintDatas.VIEW_TYPE:
			viewTypeLayout.setVisibility(View.VISIBLE);
			editTypeLayout.setVisibility(View.GONE);
			break;
		case OverlayPaintDatas.EDIT_TYPE:
			viewTypeLayout.setVisibility(View.GONE);
			editTypeLayout.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getRawX();
	        startY = event.getRawY();
	        WindowManager.LayoutParams params = (WindowManager.LayoutParams)getLayoutParams();
	        lX = params.x;
	        lY = params.y;
	        touchDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if(touchDown){
	            //터치된 뷰의 위치 변경
				float  x = lX + (startX - event.getRawX());
				float  y = lY + (event.getRawY() - startY);
				params = (WindowManager.LayoutParams)getLayoutParams();
				params.x = (int) x;
				params.y = (int) y;
				windowManager.updateViewLayout(this, params);
			}
			break;
		case MotionEvent.ACTION_UP:
			touchDown = false;
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.editBtn:
			Intent intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.EDIT_TYPE);
			getContext().startService(intent);
			break;
		case R.id.hideBtn:
			intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.HIDE_TYPE);
			getContext().startService(intent);
			break;
		case R.id.exitBtn:
			intent = new Intent(getContext(), OverlayPaintService.class);
			getContext().stopService(intent);
			break;
		case R.id.saveBtn:
			intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.VIEW_TYPE);
			intent.putExtra(OverlayPaintDatas.INTENT_SAVE, true);
			getContext().startService(intent);
			break;
		case R.id.eraseBtn:
			if(type == PaintPathData.DRAW){
				intent = new Intent(getContext(), OverlayPaintService.class);
				intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.ERASE_TYPE);
				getContext().startService(intent);
			}else if(type == PaintPathData.ERASE){
				intent = new Intent(getContext(), OverlayPaintService.class);
				intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.DRAW_TYPE);
				getContext().startService(intent);
			}
			
			break;
		case R.id.clearBtn:
			intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.CLEAR_TYPE);
			getContext().startService(intent);
			break;
		case R.id.cancelBtn:
			intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.VIEW_TYPE);
			intent.putExtra(OverlayPaintDatas.INTENT_SAVE, false);
			getContext().startService(intent);
			break;
		case R.id.colorPickButton:
			colorPickButton.setVisibility(View.GONE);
			intent = new Intent(getContext(), OverlayPaintService.class);
			intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.PICK_COLOR_TYPE);
			getContext().startService(intent);
			break;
		default:
			break;
		}
	}
	
	public void setPickedColor(int color){
		if(colorPickButton != null) {
			colorPickButton.setVisibility(View.VISIBLE);
			colorPickButton.setColor(color);
		}
	}
	
	public int getPickedColor(){
		if(colorPickButton == null) return Color.BLACK;
		return colorPickButton.getColor();
	}
	
	public void setSelectedPenWidth(int position){
		penWidthSpinner.setSelection(position);
	}
}

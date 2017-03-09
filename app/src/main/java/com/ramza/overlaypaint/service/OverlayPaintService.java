package com.ramza.overlaypaint.service;

import org.json.JSONException;

import com.ramza.overlaypaint.R;
import com.ramza.overlaypaint.consts.OverlayPaintDatas;
import com.ramza.overlaypaint.datas.IDataManager;
import com.ramza.overlaypaint.datas.PaintData;
import com.ramza.overlaypaint.datas.PaintPathData;
import com.ramza.overlaypaint.datas.file.PaintFileDataManager;
import com.ramza.overlaypaint.views.ColorPicker;
import com.ramza.overlaypaint.views.OverlayButtonLayout;
import com.ramza.overlaypaint.views.OverlayPaintLayout;
import com.ramza.overlaypaint.views.ColorPicker.OnColorChangedListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class OverlayPaintService extends Service implements OnColorChangedListener, OnItemSelectedListener{
	private OverlayPaintLayout overlayPaintLayout;
	private OverlayButtonLayout overlayButtonLayout;
	private ColorPicker colorPicker;

	private final String FILE_NAME = "overLayPaint.paint";
	private String pathJsonData = null;

	private static final int NOTIFICATION_ID = 1;

	private int type = PaintPathData.DRAW;
	private int color = Color.BLACK;
	private int selectedPenWidthPos = 0;
	private float selectedPenWidth;
	private int buttonLayoutPosX = 0;
	private int buttonLayoutPosY = 0;

	private IDataManager<PaintData> dataManager;
	
	private int currentOrientation = Configuration.ORIENTATION_PORTRAIT;
	
	private WindowManager windowManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dataManager = new PaintFileDataManager();
		PaintData paintData = dataManager.loadData(FILE_NAME);
		if (paintData != null)
			pathJsonData = paintData.jsonData;
		
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		showNotification();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if(newConfig != null){
			currentOrientation = newConfig.orientation;
			overlayPaintLayout.setCurrentOrietation(currentOrientation);
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			int modeType = intent.getIntExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.VIEW_TYPE);
			boolean needSave = intent.getBooleanExtra(OverlayPaintDatas.INTENT_SAVE, false);
			color = OverlayPaintDatas.getColor(getApplicationContext());
			selectedPenWidthPos = OverlayPaintDatas.getPenPos(getApplicationContext());

			switch (modeType) {
			case OverlayPaintDatas.VIEW_TYPE:
				if (overlayPaintLayout != null) {
					if (needSave) {
						try {
							pathJsonData = overlayPaintLayout.getPathData();
							PaintData paintData = new PaintData();
							paintData.id = FILE_NAME;
							paintData.jsonData = pathJsonData;
							dataManager.saveData(paintData);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					windowManager.removeViewImmediate(overlayPaintLayout);
				}
				if (overlayButtonLayout != null) {
					WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayButtonLayout
							.getLayoutParams();
					buttonLayoutPosX = params.x;
					buttonLayoutPosY = params.y;
					windowManager.removeViewImmediate(overlayButtonLayout);
				}

				overlayPaintLayout = new OverlayPaintLayout(this);
				overlayPaintLayout.setCurrentOrietation(currentOrientation);
				WindowManager.LayoutParams params = getLayoutParams(true, false);
				params.gravity = Gravity.LEFT | Gravity.TOP;

				windowManager.addView(overlayPaintLayout, params);

				if (pathJsonData != null)
					overlayPaintLayout.setPathData(pathJsonData);

				overlayButtonLayout = new OverlayButtonLayout(this);
				params = getLayoutParams(false, true);
				params.gravity = Gravity.RIGHT | Gravity.TOP;
				params.x = buttonLayoutPosX;
				params.y = buttonLayoutPosY;
				windowManager.addView(overlayButtonLayout, params);
				
				overlayButtonLayout.setPaintType(type);
				overlayButtonLayout.setLayoutType(modeType);
				overlayButtonLayout.setPickedColor(color);
				overlayButtonLayout.setSelectedPenWidth(selectedPenWidthPos);
				overlayPaintLayout.setPaintType(type);
				overlayPaintLayout.setPaintColor(color);
				overlayPaintLayout.setPenWidth(selectedPenWidth);
				break;
			case OverlayPaintDatas.EDIT_TYPE:
				if (overlayPaintLayout != null) {
					try {
						pathJsonData = overlayPaintLayout.getPathData();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					windowManager.removeViewImmediate(overlayPaintLayout);
				}
				if (overlayButtonLayout != null) {
					params = (WindowManager.LayoutParams) overlayButtonLayout.getLayoutParams();
					buttonLayoutPosX = params.x;
					buttonLayoutPosY = params.y;
					windowManager.removeViewImmediate(overlayButtonLayout);
				}

				overlayPaintLayout = new OverlayPaintLayout(this);
				overlayPaintLayout.setCurrentOrietation(currentOrientation);
				params = getLayoutParams(true, true);
				params.gravity = Gravity.LEFT | Gravity.TOP;
				windowManager.addView(overlayPaintLayout, params);

				if (pathJsonData != null)
					overlayPaintLayout.setPathData(pathJsonData);

				overlayButtonLayout = new OverlayButtonLayout(this);
				params = getLayoutParams(false, true);
				params.gravity = Gravity.RIGHT | Gravity.TOP;
				params.x = buttonLayoutPosX;
				params.y = buttonLayoutPosY;
				
				windowManager.addView(overlayButtonLayout, params);
				
				overlayButtonLayout.setPaintType(type);
				overlayButtonLayout.setLayoutType(modeType);
				overlayButtonLayout.setPickedColor(color);
				overlayButtonLayout.setSelectedPenWidth(selectedPenWidthPos);
				overlayPaintLayout.setPaintType(type);
				overlayPaintLayout.setPaintColor(color);
				overlayPaintLayout.setPenWidth(selectedPenWidth);
				break;
			case OverlayPaintDatas.SHOW_TYPE:
				if (overlayButtonLayout != null)
					overlayButtonLayout.setVisibility(View.VISIBLE);
				break;
			case OverlayPaintDatas.HIDE_TYPE:
				if (overlayButtonLayout != null)
					overlayButtonLayout.setVisibility(View.GONE);
				break;
			case OverlayPaintDatas.CLEAR_TYPE:
				if (overlayPaintLayout != null)
					overlayPaintLayout.clearPaint();
				break;
			case OverlayPaintDatas.PICK_COLOR_TYPE:
				colorPicker = new ColorPicker(this);
				colorPicker.setInitColor(color);
				colorPicker.setOnColorChangedListener(this);
				windowManager.addView(colorPicker, getLayoutParams(true, true));
				break;
			case OverlayPaintDatas.DRAW_TYPE:
				this.type = PaintPathData.DRAW;
				overlayButtonLayout.setPaintType(type);
				overlayPaintLayout.setPaintType(type);
				break;
			case OverlayPaintDatas.ERASE_TYPE:
				this.type = PaintPathData.ERASE;
				overlayButtonLayout.setPaintType(type);
				overlayPaintLayout.setPaintType(type);
				break;
			default:
				break;
			}
		}
		return START_STICKY;
	}

	@Override
	public void colorChanged(int color) {
		// TODO Auto-generated method stub
		this.color = color;
		OverlayPaintDatas.setColor(getApplicationContext(), color);
		overlayButtonLayout.setPickedColor(color);
		overlayPaintLayout.setPaintColor(color);
		windowManager.removeViewImmediate(colorPicker);
	}

	private void showNotification() {
		Intent intent = new Intent(this, OverlayPaintService.class);
		intent.putExtra(OverlayPaintDatas.INTENT_MODE, OverlayPaintDatas.SHOW_TYPE);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.icon).setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.show_overlay_paint_menu)).setAutoCancel(true).setOngoing(true)
				.setContentIntent(pendingIntent).build();
		
		startForeground(NOTIFICATION_ID, notification);
	}

	public WindowManager.LayoutParams getLayoutParams(boolean fullWidth, boolean touchable) {
		WindowManager.LayoutParams params = null;
		if (touchable) {
			params = new WindowManager.LayoutParams(fullWidth ? WindowManager.LayoutParams.MATCH_PARENT
					: WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);
		} else {
			params = new WindowManager.LayoutParams(fullWidth ? WindowManager.LayoutParams.MATCH_PARENT
					: WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);
		}
		return params;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	    OverlayPaintDatas.setPenPos(getApplicationContext(), position);
	    
		String penWidthText = (String) ((TextView)view).getText();
		float penWidth = Float.valueOf(penWidthText.substring(0, penWidthText.indexOf("px")));
		overlayPaintLayout.setPenWidth(penWidth);
		selectedPenWidthPos = position;
		selectedPenWidth = penWidth;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
		if (overlayPaintLayout != null) // 서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
		{
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(overlayPaintLayout);
			overlayPaintLayout = null;
		}

		if (overlayButtonLayout != null) {
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(overlayButtonLayout);
			overlayButtonLayout = null;
		}

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}

}

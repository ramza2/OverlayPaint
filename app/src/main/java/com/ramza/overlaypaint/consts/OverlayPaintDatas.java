package com.ramza.overlaypaint.consts;

import com.ramza.overlaypaint.utils.PreferenceUtils;

import android.content.Context;
import android.graphics.Color;

public class OverlayPaintDatas {
	public static final String PATHS = "paths";
	public static final String TYPE = "type";
	public static final String COLOR = "color";
	public static final String PEN_WIDTH = "penWidth";
	public static final String ORIENTATION = "orientation";
	public static final String POINTS = "points";
	public static final String X = "X";
	public static final String Y = "Y";
	
	public static final String INTENT_MODE = "MODE_TYPE";
	public static final int VIEW_TYPE = 0;
	public static final int EDIT_TYPE = 1;
	public static final int SHOW_TYPE = 2;
	public static final int HIDE_TYPE = 3;
	public static final int DRAW_TYPE = 4;
	public static final int ERASE_TYPE = 5;
	public static final int CLEAR_TYPE = 6;
	public static final int PICK_COLOR_TYPE = 7;
	public static int modeType = VIEW_TYPE;
	
	public static final String INTENT_SAVE = "SAVE_TYPE";
	
	public static final String PREF_KEY_COLOR = "COLOR";
	public static final String PREF_KEY_PEN_POS = "PEN_POS";
	public static int getColor(Context context){
	    return PreferenceUtils.getPreferenceInt(context, PREF_KEY_COLOR, Color.BLACK);
	}
	public static void setColor(Context context, int color){
	    PreferenceUtils.setPreference(context, PREF_KEY_COLOR, color);
	}
	public static int getPenPos(Context context){
	    return PreferenceUtils.getPreferenceInt(context, PREF_KEY_PEN_POS, 0);
	}
	public static void setPenPos(Context context, int penPos){
	    PreferenceUtils.setPreference(context, PREF_KEY_PEN_POS, penPos);
	}
}

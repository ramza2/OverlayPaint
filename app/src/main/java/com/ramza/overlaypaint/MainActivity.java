package com.ramza.overlaypaint;

import com.ramza.overlaypaint.service.OverlayPaintService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startService(new Intent(this, OverlayPaintService.class));
		finish();
	}
}

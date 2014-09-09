package com.atleusdigital.android.criminalintent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Window;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleFragmentActivity {
	
	private final String TAG = "CrimeCameraActivity";
	private OrientationEventListener mOrientationEventListener;

	@Override
	protected Fragment createFragment() {
		return new CrimeCameraFragment();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Hide the window title. We have to do it in the parent activty since this cannot be done in the fragment
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Hide the status bar and other OS-level chrome
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
	}
	
	
}
package com.atleusdigital.android.criminalintent;

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
	
	private int mOrientation = 0;
	
	private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
	private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
	private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
	private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

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
		
		if (mOrientationEventListener == null) {            
	        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {

	            @Override
	            public void onOrientationChanged(int orientation) {
	            	// determine our orientation based on sensor response
	                int lastOrientation = mOrientation;

	                if (orientation >= 315 || orientation < 45) {
	                    if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {                          
	                        mOrientation = ORIENTATION_PORTRAIT_NORMAL;
	                    }
	                }
	                else if (orientation < 315 && orientation >= 225) {
	                    if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
	                        mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
	                    }                       
	                }
	                else if (orientation < 225 && orientation >= 135) {
	                    if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
	                        mOrientation = ORIENTATION_PORTRAIT_INVERTED;
	                    }                       
	                }
	                else { // orientation <135 && orientation > 45
	                    if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
	                        mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
	                    }                       
	                }   

	                Log.i(TAG, "Orientation change: " + orientation + " val=" + mOrientation);
	            }
	        };
		}
		
	    if (mOrientationEventListener.canDetectOrientation()) {
	        mOrientationEventListener.enable();
	    }
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();	
		mOrientationEventListener.enable();
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		mOrientationEventListener.disable();
	}

	public int getOrientation() {
		return mOrientation;
	}
	
	
}
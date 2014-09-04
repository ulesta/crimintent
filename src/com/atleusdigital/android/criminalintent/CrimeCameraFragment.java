package com.atleusdigital.android.criminalintent;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {

		private static final String TAG = "CrimeCameraFragment";
		
		private Camera mCamera;
		private SurfaceView mSurfaceView;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);
			
			Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
			takePictureButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getActivity().finish();
				}
			});
			
			mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
			
			return v;
		}
		
		
		
		
	
}


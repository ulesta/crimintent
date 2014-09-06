package com.atleusdigital.android.criminalintent;

import java.util.List;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/* Notes:
 * 
 * - Unlike other View objects, SurfaceView does not draw anything into itself
 * - A Surface's client is whatever object wants to draw into its buffer
 * - In our case, the client is the camera
 * 
 * - SurfaceHolder.Callback is an interface which allwos us to listen for the 
 * 		in the lifecycle of Surface 
 */

public class CrimeCameraFragment extends Fragment {

		private static final String TAG = "CrimeCameraFragment";
		
		private Camera mCamera;
		private SurfaceView mSurfaceView;
		private View mProgressContainer;
		
		@Override
		@SuppressWarnings("deprecation")
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
			
			mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
			mProgressContainer.setVisibility(View.INVISIBLE);
			
			mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
			
			// SurfaceHolder is your connection to Surface, which represents a buffer of raw pixel data
			/* Surface LifeCycle:
			 * - Created when SurfaceView appears on the screen
			 * - Destroyed when SurfaceView is no longer visible
			 * 
			 * Note: ensure nothing is drawn to the Surface when it does not exist
			 */
			final SurfaceHolder holder = mSurfaceView.getHolder();
			// setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated but are required for Camera preview to work on pre-3.0 devices
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			
			holder.addCallback(new SurfaceHolder.Callback() {	
				/* This method is called when the view hierarchy the the SurfaceVeiw belongs to is put on screen.
				 * This is where you connect the Surface with its client.
				 * (non-Javadoc)
				 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
				 */
				@Override
				public void surfaceCreated(SurfaceHolder arg0) {
					// Tell the camera to use this surface as its preview area
					try {
						if (mCamera != null) {
							mCamera.setPreviewDisplay(holder);
						}
					} catch (Exception e) {
						Log.e(TAG, "Error setting up preview display", e);
					}
				}
				
				/* Called when the surface is being displayed for the first time.
				 * This method informs the Surface of pixel format and dimensions.
				 * Drawing area dimensions are given in this method.
				 * (non-Javadoc)
				 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
				 */
				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
					if (mCamera == null) return;
					
					// The surface has changed size; update the camera preview size
					Camera.Parameters parameters = mCamera.getParameters();
					Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
					parameters.setPreviewSize(s.width, s.height);
					mCamera.setParameters(parameters);
					
					try {
						mCamera.startPreview();
					} catch (Exception e) {
						Log.e(TAG, "Could not start preview", e);
						mCamera.release();
						mCamera = null;
					}
				}
				
				/* This is where we detach Surface's client, i.e. camera
				 * (non-Javadoc)
				 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
				 */
				@Override
				public void surfaceDestroyed(SurfaceHolder arg0) {
					// we can no longer display on this surface, stop preview.
					if (mCamera != null) {
						mCamera.stopPreview();
					}
				}
			});
			
			return v;
		}

		@TargetApi(9)
		@Override
		public void onResume() {
			super.onResume();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				mCamera = Camera.open(0);
			} else {
				mCamera = Camera.open();
			}
		}

		@Override
		public void onPause() {
			super.onPause();
			// must ensure that the camera instance exists, if null, cam may not be avaialale or another app may be using it
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
			
		}
		
		/** A simple algorithm to get the largest size available. For a more
		 * robust version, see CameraPreview.java in the ApiDemos
		 * sample app from Android. */
		private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
			Size bestSize = sizes.get(0);
			int largestArea = bestSize.width * bestSize.height;
			for ( Size s: sizes ) {
				int area = s.width * s.height;
				if ( area > largestArea ) {
					bestSize = s;
					largestArea = area;
				}
			}
			return bestSize;
		}

}


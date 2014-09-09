package com.atleusdigital.android.criminalintent;

import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
		
		public static final String EXTRA_PHOTO_FILENAME = "com.atleusdigital.android.criminalintent.photo_filename";
		public static final String EXTRA_PHOTO_ORIENTATION = "com.atleusdigital.android.criminalintent.photo_orientation";
		
		private Camera mCamera;
		private SurfaceView mSurfaceView;
		private View mProgressContainer;
		
		private Camera.ShutterCallback mShutterCallback;
		
		private Camera.PictureCallback mJpegCallback;

		private OrientationEventListener mOrientationEventListener;
		
		private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
		private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
		private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
		private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

		private int mOrientation = 0;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			/* I feel like it'd be cleaner if we instaniated the Camera 
			 * Callbacks in here
			 */
			
			if (mOrientationEventListener == null) {            
		        mOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_UI) {

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
		    if (mOrientationEventListener.canDetectOrientation()) {
		        mOrientationEventListener.enable();
		    }
		 }
			
			
			mShutterCallback = new Camera.ShutterCallback() {
				
				@Override
				public void onShutter() {
					// Display the progress indicator
					mProgressContainer.setVisibility(View.VISIBLE);
					Log.i(TAG, "Orientation: " + getActivity().getResources().getConfiguration().orientation);
				}
			};
			
			mJpegCallback = new PictureCallback() {
				
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					// Create a filename
					String filename = UUID.randomUUID().toString() + ".jpg";
					// Save the jpeg data to disk
					FileOutputStream os = null;
					boolean success = true;
					try {
						os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
						os.write(data);
					} catch (Exception e) {
						Log.e(TAG, "Error writing to file " + filename, e);
						success = false;
					} finally {
						try {
							if (os != null) {
								os.close();
							}
						} catch (Exception e) {
							Log.e(TAG, "Error closing file " + filename, e);
							success = false;
						}
					}
					
					if (success) {
						//Log.i(TAG, "JPEG saved at " + filename);
						// Set the photo filename on the result intent
						Intent i = new Intent();
						i.putExtra(EXTRA_PHOTO_FILENAME, filename);
						i.putExtra(EXTRA_PHOTO_ORIENTATION, mOrientation);
						// allows data to propagate back to originating activity
						getActivity().setResult(Activity.RESULT_OK, i);
					} else {
						getActivity().setResult(Activity.RESULT_CANCELED);
					}
					// finish off activity
					getActivity().finish();
				}
			};

		}
		
		
		

		@Override
		@SuppressWarnings("deprecation")
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);
			
			
			
			ImageButton takePictureButton = (ImageButton) v.findViewById(R.id.crime_camera_takePictureButton);
			takePictureButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mCamera != null) {
						mCamera.takePicture(mShutterCallback, null, mJpegCallback);
					}
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
					// Set picture output size
					s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
					parameters.setPictureSize(s.width, s.height);
					// Set parameters
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


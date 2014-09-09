package com.atleusdigital.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {
	
	private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
	private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
	private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
	private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;
	
	/**
	 * Get a BitmapDrawawble from a local file that is scaled down
	 * to fit the current Window size.
	 */
	
	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity a, String path, int orientation) {
		Display display = a.getWindowManager().getDefaultDisplay();
		float destWidth = display.getWidth();
		float destHeight = display.getHeight();
		
		// Read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;
		
		int inSampleSize = 1;
		// if image is too big for the activity window, get max(height, width)
		if ( srcHeight > destHeight || srcWidth > destWidth) {
			if (srcWidth > srcHeight) {
				inSampleSize = Math.round(srcHeight / destHeight);
			} else {
				inSampleSize = Math.round(srcWidth / destWidth);
			}
		}
		
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		/* create a bitmap based on the optimal sampling size */
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		
		/* rotate image based on orientation it was taken */
		Matrix matrix = new Matrix();
	    switch (orientation) {
		case ORIENTATION_LANDSCAPE_NORMAL:
			matrix.setRotate(0);
			break;
		case ORIENTATION_LANDSCAPE_INVERTED:
			matrix.setRotate(180);
			break;
		case ORIENTATION_PORTRAIT_NORMAL:
			matrix.setRotate(90);	// (?) not so important bug: why 90 if actual rotation angle is 270? Maybe it's backward? o_o
			break;
		case ORIENTATION_PORTRAIT_INVERTED:
			matrix.setRotate(270);	// would make sense if backwards bc this is 270deg and it's fine!
			break;
		default:
			break;
		}
	    
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		
		return new BitmapDrawable(a.getResources(), newBitmap);
	}
	
	public static void cleanImageView(ImageView imageView) {
		if (!(imageView.getDrawable() instanceof BitmapDrawable)) {
			return;
		}
		
		// Clean up the view's image for the sake of memory
		BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
		b.getBitmap().recycle();
		imageView.setImageDrawable(null);
	}
	
}

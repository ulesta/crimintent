package com.atleusdigital.android.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	
	public static final String EXTRA_IMAGE_PATH = "com.atleusdigital.android.criminalintent.image_path";
	public static final String EXTRA_IMAGE_ORIENTATION = "com.atleusdigital.android.criminalintent.image_orientation";
	
	private ImageView mImageView;
	
	public static ImageFragment newInstance(String imagePath, int orientation) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		args.putInt(EXTRA_IMAGE_ORIENTATION, orientation);
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		int orientation = getArguments().getInt(EXTRA_IMAGE_ORIENTATION);
		
		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, orientation);
		
		mImageView.setImageDrawable(image);
		mImageView.setBackgroundColor(0xff080808);
		
		return mImageView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		PictureUtils.cleanImageView(mImageView);
	}

	
	
}

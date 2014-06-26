package com.atleusdigital.android.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class CrimePagerActivity extends FragmentActivity {
	
		private final static String TAG = "CrimePagerActivity";

		private ViewPager mViewPager;
		private ArrayList<Crime> mCrimes;

		@Override
		protected void onCreate(Bundle savedInstaceState) {		
			super.onCreate(savedInstaceState);
			mViewPager = new ViewPager(this);
			mViewPager.setId(R.id.viewPager);
			setContentView(mViewPager);
			
			mCrimes = CrimeLab.get(this).getCrimes();
			
			FragmentManager fm = getSupportFragmentManager();
			mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
				
				@Override
				public int getCount() {
					return mCrimes.size();
				}
				
				@Override
				public Fragment getItem(int pos) {
					Crime crime = mCrimes.get(pos);
					return CrimeFragment.newInstance(crime.getmId());
				}
			});
			
			// set the action bar's title to match current crime
			mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int pos) {
					Crime crime = mCrimes.get(pos);
					if (crime.getTitle() != null ) {
						setTitle(crime.getTitle());
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// intentionally blank
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// intentionally blank
				}
			});
			
			UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.KEY_UUID);
			Log.i(TAG, "crimeId = " + crimeId.toString());
			
			// loop through until the crime is found
			for (int i = 0; i < mCrimes.size(); i++ ) {
				if (mCrimes.get(i).getmId().equals(crimeId)) {
					mViewPager.setCurrentItem(i);
					Log.i(TAG, "index = " + i);
					break;
				}
				Log.i(TAG, "for loop = " + i);
			}
			
		}
	
}

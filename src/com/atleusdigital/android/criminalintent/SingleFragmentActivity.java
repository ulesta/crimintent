package com.atleusdigital.android.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class SingleFragmentActivity extends FragmentActivity {
	
	protected abstract Fragment createFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if ( fragment == null ) {
			fragment = createFragment();
			/* for fragments to get arguments, fragments must be 
				created before adding to fragmentmanager */
			fm.beginTransaction()
				.add(R.id.fragmentContainer, fragment)
				.commit();
		} 
	}
	
}

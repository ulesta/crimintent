package com.atleusdigital.android.criminalintent;

import java.util.Calendar;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class CrimeActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return CrimeFragment
		.newInstance((UUID) getIntent().getSerializableExtra(CrimeListFragment.KEY_UUID));
	}
	
	

}

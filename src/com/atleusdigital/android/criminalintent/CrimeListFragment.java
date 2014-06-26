package com.atleusdigital.android.criminalintent;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	
	private static final int REQUEST_CRIME = 1;
	private static final String TAG = "CrimeListFragment";
	public static final String KEY_UUID = "crime_key";
	
	private ArrayList<Crime> mCrimes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get crime from adapter
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		//Intent i = new Intent(getActivity(), CrimeActivity.class);
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		Log.i(TAG, "mId = " + c.getmId());
		i.putExtra(KEY_UUID, c.getmId());
		startActivityForResult(i, REQUEST_CRIME);
		
		Log.i(TAG, c.getTitle() + " was clicked!");
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Handle results from activity started
		if ( requestCode == REQUEST_CRIME) {
			// handle result here
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Crime changed successfully!");
			} else {
				Log.i(TAG, "No changes made!!");
			}
		}
	}



	private class CrimeAdapter extends ArrayAdapter<Crime> {
		
		public CrimeAdapter(ArrayList<Crime> crimes) {
			// pass 0 for layout since we are not using a predefined layout
			super(getActivity(), 0, crimes);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if we weren't given one then inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_crime, null);
			}
			
			// Configure view for this crime
			Crime c = getItem(position);
			
			TextView titleView = (TextView)convertView
					.findViewById(R.id.crime_list_item_titleTextView);
			titleView.setText(c.getTitle());
			
			TextView dateView = (TextView)convertView
					.findViewById(R.id.crime_list_item_dateTextView);
			dateView.setText(c.getDate().toString());
			
			CheckBox solvedCheckBox = (CheckBox)convertView
					.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());
			
			
			return convertView;
		}
		
		
		
	}
	
	

}

package com.atleusdigital.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	
	private static final int REQUEST_CRIME = 1;
	private static final String TAG = "CrimeListFragment";
	public static final String KEY_UUID = "crime_key";
	
	private ArrayList<Crime> mCrimes;
	private boolean mSubtitleVisible;
	
	private Button mEmptyButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// must be called so that the fragment gets onCreateOptionMenu callback
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.crimes_title);
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		
		// to retain subtitle's visibility
		setRetainInstance(true);
		mSubtitleVisible = false;
		
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);
	}
	
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/* remember that view gets recreated after a config change,
		 * in this case we make sure we set the subtitle to show if it was already showing
		 */
		//View v = super.onCreateView(inflater, container, savedInstanceState);
		
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		
		View v = inflater.inflate(R.layout.fragment_crimelist, null);
		
		// wire up functionality to empty button
		mEmptyButton = (Button) v.findViewById(R.id.fragment_crimelist_empty_button);
		mEmptyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getmId());
				startActivityForResult(i, 0);
			}
		});
		
		return v;
	}



	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get crime from adapter
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		//Intent i = new Intent(getActivity(), CrimeActivity.class);
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		Log.i(TAG, "mId = " + c.getmId());
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getmId());
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
	
	



	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// must be called to inflate our menu in res/menu
		inflater.inflate(R.menu.fragment_crime_list, menu);
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}
	
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.get(getActivity()).addCrime(crime);
			Intent i = new Intent(getActivity(), CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getmId());
			startActivityForResult(i, 0);
			return true;
		case R.id.menu_item_show_subtitle:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
				// to retain subtitle's visibility
				mSubtitleVisible = true;
				item.setTitle(R.string.hide_subtitle);
			}
			else {
				getActivity().getActionBar().setSubtitle(null);
				mSubtitleVisible = false;
				item.setTitle(R.string.show_subtitle);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

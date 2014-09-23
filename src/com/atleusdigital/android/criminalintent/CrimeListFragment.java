package com.atleusdigital.android.criminalintent;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
	
	private Callbacks mCallbacks;
	/**
	 * Required interface for hosting activities.
	 */
	public interface Callbacks {
		void onCrimeSelected(Crime crime);
	}
	
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks) activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
	
	public void updateUI() {
		((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
	}

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
				((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
				mCallbacks.onCrimeSelected(crime);
			}
		});
		
		/* We must first register the view to trigger a context menu.
		 * The android.R.id.list res ID is used to retrieve the ListView managed by ListFragment
		 * with onCreateView(..).  */
		
		ListView listView = (ListView) v.findViewById(android.R.id.list);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			// Use floating context menus on Froyo and Gingerbread
			registerForContextMenu(listView);
		} else {
			// Use contextual action bar on Honeycomb and higher
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		}
		
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				/* called after onCreateActionMode(..) and whenever contextual action bar
				 * needs to be refreshed
				 */
				// required but not used in this implementation
				return false;
			}
			
			public void onDestroyActionMode(ActionMode arg0) {
				/* called when the ActionMode is about to be destroyed */
				// required but not used
			}
			
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				/* This is where you inflate the context menue resource */
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.crime_list_item_context, menu);
				return true;
			}
			
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				/* called when the user selects an action, where response to contextual actions is done */
				switch (item.getItemId()) {
				case R.id.menu_item_delete_crime:
					CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
					CrimeLab crimeLab = CrimeLab.get(getActivity());
					for ( int i = adapter.getCount() - 1 ; i >= 0; i-- ) {
						if (getListView().isItemChecked(i)) {
							crimeLab.deleteCrime(adapter.getItem(i));
						}	
					}
					mode.finish();
					adapter.notifyDataSetChanged();
					return true;
				default:
					return false;
				}

			}
			
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// Required but not used in this implementation
				
			}
		});
		
		return v;
	}



	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get crime from adapter
		Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);
		mCallbacks.onCrimeSelected(c);
		
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
			// makes sure to reload the list 
			((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
			mCallbacks.onCrimeSelected(crime);
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
	
	/* For floating context menu */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		/* Unlike onCreateOptionsMenue(...), this menu callback does not pass in an
		 * instance of MenuInflater.
		 * MenuInflater.inflate(...) allows you to pass in the resource ID of our context menu
		 * to populate the menu instance with the items given in our file.
		 * Note: currently we only have one context menu, were to we have more, we can determine
		 * which to inflate by checking the ID of the View
		 */
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		/* because ListView is a subclass of AdapterView we must obtain AdapterContextMenuInfo */
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		Crime crime = adapter.getItem(position);
		
		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(crime);
			// needed to refresh list
			adapter.notifyDataSetChanged();
			return true;

		default:
			return super.onContextItemSelected(item);
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

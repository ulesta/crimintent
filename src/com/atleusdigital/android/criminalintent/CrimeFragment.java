package com.atleusdigital.android.criminalintent;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {
	
	private final static String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "extraCrimeID";
	public static final String KEY_UUID = "crime_key";
	private static final String DIALOG_DATE = "date";
	
	private static final String DIALOG_IMAGE = "image";
	
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_PHOTO = 1;
	private static final int REQUEST_CONTACT = 2;
	
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Button mSuspectButton;
	private Button mSearchButton;
	
	private Callbacks mCallbacks;
	
	private PackageManager pm;
	
	/**
	 * Required interface for hosting activities.
	 */
	public interface Callbacks {
		void onCrimeUpdated(Crime crime);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks)activity;
	}



	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UUID crimeID = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
		setHasOptionsMenu(true);
	}
	
	
	
	@Override
	public void onPause() {
		// When the fragment loses focus, the CrimeLab is saved to a JSON file
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}



	public static CrimeFragment newInstance(UUID id) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, id);
		Log.i(TAG, "id = " + id.toString());
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	// fragments cannot return results, only activities can
	public void returnResult(){
		getActivity().setResult(Activity.RESULT_OK, null);
	}
	
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.menu_item_delete_crime:
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			CrimeLab.get(getActivity()).deleteCrime(mCrime);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, container, false);
		
		// check if Android version supports up button
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		
		// add text change listener
		mTitleField.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mCrime.setTitle(s.toString());
				mCallbacks.onCrimeUpdated(mCrime);
				getActivity().setTitle(mCrime.getTitle());
				// [bug]: when orientation is changed -- nullpointer
				returnResult();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// blank
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// blank;
			}
		});
		
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		Calendar cal = Calendar.getInstance();
		Log.i("Crime.java", "Friday is: " + cal.get(Calendar.DAY_OF_WEEK));
		updateDate();
		
		mDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Set the crime's isSolved property
				mCrime.setSolved(isChecked);
				mCallbacks.onCrimeUpdated(mCrime);
			}
			
		});
		
		mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
			
		});
		
		// If camera is not available, disable camera functionality
		pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mPhotoButton.setEnabled(false);
		}
		
		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		
		// [Challenge ch20 - 3]: implement context menu on hold of imageview
		// Reference: http://www.technotalkative.com/contextual-action-bar-cab-android/
		mPhotoView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				Log.i(TAG, "onLongClick event!");
				
				getActivity().startActionMode(new Callback() {
					
					@Override
					public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
						return false;
					}
					
					@Override
					public void onDestroyActionMode(ActionMode mode) {
						
					}
					
					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						/* This is where we inflate context menu resource */
						MenuInflater inflater = mode.getMenuInflater();
						inflater.inflate(R.menu.crime_fragment_context, menu);
						// you have to return true to see the action context menu
						return true;
					}
					
					@Override
					public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
						switch (item.getItemId()) {
						case R.id.menu_item_delete_photo:
							PictureUtils.cleanImageView(mPhotoView);
							File in;
							try {
								in = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename());
								in.delete();
								Log.i(TAG, "Deleted file!");
								mCrime.setPhoto(null);
							} catch (Exception e) {
								Log.i(TAG, "File not found or does not exist!");
							}
							// Close context menu immediately after
							mode.finish();
							break;

						default:
							break;
						}
						
						return false;
					}
				});
				
				// return true to indicate no further click processing is to be done
				// that is, the event ends with a long click
				return true;
			}
		});
		
		mPhotoView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if (p == null) {
					return;
				}
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				
				/* show is a convenience that displays the dialog and adds it to the FragmentManager */
				ImageFragment.newInstance(path, p.getOrientation())
								.show(fm, DIALOG_IMAGE);
			}
		});
		
		
		mSuspectButton = (Button)v.findViewById(R.id.crime_choose);
		mSuspectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, 
						ContactsContract.Contacts.CONTENT_URI);
				// hi check to make sure we have app to handle implicit intent
				List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
				
				if ( activities.size() > 0 ) {
					startActivityForResult(i, REQUEST_CONTACT);
				} else {
					return;
				}
				
			}
		});
		
		if (mCrime.getSuspect() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
		}
		
		Button reportButton = (Button)v.findViewById(R.id.crime_send);
		reportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// cool
				//Intent i = new Intent(Intent.ACTION_VOICE_COMMAND);
				Intent i = new Intent(Intent.ACTION_SEND);
				Log.i(TAG, i.toString());
				
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});
		
		mSearchButton = (Button)v.findViewById(R.id.crime_search);
		mSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
				String suspect = mCrime.getSuspect();
				if (suspect == null) {
					Builder err = new AlertDialog.Builder(getActivity());
					err.setMessage("No suspect specified!");
					err.setPositiveButton("Okay", new Dialog.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
					err.create();
					err.show();
				} else {
					i.putExtra(SearchManager.QUERY, suspect);
					startActivity(i);
				}
			}
		});
		
		return v;
	}
	
	public void updateDate() {
		Calendar cal = Calendar.getInstance();
		Date thisDate = mCrime.getDate();
		
		cal.setTime(thisDate);
		
		mDateButton.setText(mCrime.getDate().toString());
	}
	
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_DATE) {
			Date date = (Date)data.getSerializableExtra(
					DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			mCallbacks.onCrimeUpdated(mCrime);
			updateDate();
		} else if (requestCode == REQUEST_PHOTO) {
			// Create a new Photo object and attach it to the crime
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			int orientation = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ORIENTATION, 1);
			if (filename != null) {
				//Log.i(TAG, "filename: " + filename);
				
				// [Challenge Ch20 - 2 ]: Delete previous photo if there exists one
				if ( mCrime.getPhoto() != null ) {
					File in;
					boolean deleteResult = false;
					try {
						in = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename());
						deleteResult = in.delete();
					} catch (Exception e) {
						// TODO: handle exception
					}
					Log.i(TAG, "File deleted? " + deleteResult);
					mCrime.setPhoto(null);
				}
				
				
				Photo p = new Photo(filename, orientation);
				mCrime.setPhoto(p);
				//Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo");
				mCallbacks.onCrimeUpdated(mCrime);
				showPhoto();
			}
		} else if (requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			
			// Specify which fields we want our query to return values for
			String[] queryFields = new String[] {
					ContactsContract.Contacts.DISPLAY_NAME
			};
			// Perform query - the contactUri is like a "where" clause here
			Cursor c = getActivity().getContentResolver()
					.query(contactUri, queryFields, null, null, null);
			
			// Double-check results
			if (c.getCount() == 0) {
				c.close();
				return;
			}
			
			// Pull out first column of the first and only row of data (suspect's name)
			c.moveToFirst();
			String suspect = c.getString(0);
			mCrime.setSuspect(suspect);
			Log.i(TAG, "Suspect is: " + suspect);
			mSuspectButton.setText(suspect);
			mCallbacks.onCrimeUpdated(mCrime);
			c.close();
			
		}
	}	
	
	/** method which sets the ImageView thumbnail as the image in our Crime */
	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path, p.getOrientation());
		}
		mPhotoView.setImageDrawable(b);
	}

	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	private String getCrimeReport(){
		String solvedString = null;
		if(mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		};
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		
		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}
		
		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
		
		return report;
	}

}

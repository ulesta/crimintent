package com.atleusdigital.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;

public class DatePickerFragment extends DialogFragment {

	public static final String EXTRA_DATE = 
			"com.atleusdigital.android.criminalintent.date";
	private Date mDate;
	private Calendar calendar;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Grab bundle arguments
		mDate = (Date)getArguments().getSerializable(EXTRA_DATE);
		
		// Create a Calendar to get the year, month and day
		calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
		
		// set up the view to employ date passed in from args
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
		datePicker.init(year, month, day, new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int month,
					int day) {
				// Translate year, month, day into a Date obj using a calendar
				calendar.set(year, month, day);
				mDate = calendar.getTime();
				
				// update argument to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_DATE, mDate);
				
			}
		});
		
		// set up time picker
		TimePicker timePicker = (TimePicker) v.findViewById(R.id.dialog_date_timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// Translate year, month, day into a Date obj using a calendar
				calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				calendar.set(Calendar.MINUTE, minute);
				mDate = calendar.getTime();
				// update argument to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_DATE, mDate);
				/* Code for dismissing dialog */
				//getDialog().dismiss();
			}
		});
		
		
		return new AlertDialog.Builder(getActivity()).setTitle(R.string.date_picker_title)
				.setView(v)
				.setPositiveButton( android.R.string.ok, 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sendResult(Activity.RESULT_OK);
							}
						})
				.create();
		
		
	}

	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}
	
	
}

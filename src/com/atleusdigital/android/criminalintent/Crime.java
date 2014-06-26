package com.atleusdigital.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;
import android.util.Log;

public class Crime {
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	
	private final String daysOfWeek[] = {
			
	};
	
	public Crime() {
		// Generate unique identifier
		mId = UUID.randomUUID();
		mDate = new Date();
	
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public UUID getmId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}

	@Override
	public String toString() {
		return mTitle;
	}

	
	
}

package com.atleusdigital.android.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Crime {
	// JSON constants
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOVLED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";
	
	// random test for git
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;
	
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
	
	public String getSuspect() {
		return mSuspect;
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
	
	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}

	@Override
	public String toString() {
		return mTitle;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOVLED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		if (mPhoto != null) {
			json.put(JSON_PHOTO, mPhoto.toJSON());
		}
		if (mSuspect != null) {
			json.put(JSON_SUSPECT, mSuspect);
		}
		return json;
	}
	
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		mTitle = json.getString(JSON_TITLE);
		mSolved = json.getBoolean(JSON_SOVLED);
		mDate = new Date(json.getLong(JSON_DATE));
		if (json.has(JSON_PHOTO)) {
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		}
		if (json.has(JSON_SUSPECT)) {
			mSuspect = json.getString(JSON_SUSPECT);
		}
	}
	
	public Photo getPhoto() {
		return mPhoto;
	}
	
	public void setPhoto(Photo p) {
		mPhoto = p;
	}
	
}

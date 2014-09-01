package com.atleusdigital.android.criminalintent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CriminalIntentJSONSerializer {
	
	private static final String TAG = "CriminalIntentJSONSerializer";
	private static final String WORKING_DIR = "/CriminalIntent";

	private Context mContext;
	private String mFilename;
	
	public CriminalIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	public void saveCrimes(ArrayList<Crime> crimes) throws JSONException {
		// Build an array in JSON
		JSONArray array = new JSONArray();
		for (Crime c : crimes) {
			array.put(c.toJSON());
		}
		// Write file to disk
		Writer writer = null;
		File file = null;
		File path = null;
		try {
			/* For Ch.17 Challenge:
			 * Writing to external (non-sandboxed) directory
			 * requires a method called getExternalFilesDir() method from Context class
			 */
			
			// getExternalFilesDir() will place the file in sdCard0/Android/packagename
			//File path = mContext.getExternalFilesDir(null);
			
			// on the other hand, we can make a new directory in the root of the external storage directory
			// creates new directory
			path = new File(Environment.getExternalStorageDirectory().toString() + WORKING_DIR);
			// File.mkdirs() is the magic solution! :)
			if (!path.exists() && !path.mkdirs()) {
				Log.e("Debug", "ERROR --> FAILED TO CREATE DIRECTORY: "
						+ WORKING_DIR);
				return;
			}
			
			//Log.i("JSONSerializer", "" + Environment.getExternalStorageDirectory());
			//Log.i("JSONSerializer", "toString(): " + Environment.getExternalStorageDirectory().toString());
			
			file = new File(path, mFilename);
			OutputStream out = new FileOutputStream(file);
			
			
			/* openFileOutput is for internal (sandboxed) storage */
			//OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
			Log.d(TAG+" [write]", ""+array.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (writer != null) {
				try {
					writer.close();
					
					// Tell the media scanner about the new file so that it is
			        // immediately available to the user.
					// This makes the file viewable in the files directory
			        MediaScannerConnection.scanFile(mContext,
			                new String[] { file.toString() }, null,
			                new MediaScannerConnection.OnScanCompletedListener() {
			            public void onScanCompleted(String path, Uri uri) {
			                Log.i("ExternalStorage", "Scanned " + path + ":");
			                Log.i("ExternalStorage", "-> uri=" + uri);
			            }
			        });

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
		ArrayList<Crime> crimes = new ArrayList<Crime>();
		BufferedReader reader = null;
		try {
			// Open and read the file into a StringBuilder
			
			//File path = mContext.getExternalFilesDir(null);
			File path = new File(Environment.getExternalStorageDirectory().toString() + WORKING_DIR);
			File file = new File(path, mFilename);
			InputStream in = new FileInputStream(file);
			
			/* openFileInput() is for opening internal (sandboxed) files */
			//InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null ) {
				// line breaks are omitted and irrelevant
				jsonString.append(line);
			}
			Log.d(TAG, ""+jsonString.toString());
			// Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			// Build the array of crimes from JSON Objects
			for (int i = 0; i < array.length(); i++) {
				crimes.add(new Crime(array.getJSONObject(i)));
			}
		} catch (Exception e) {
			// ignore; happens when starting fresh
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return crimes;
	}
	
}

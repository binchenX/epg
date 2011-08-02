package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
//import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;




import android.content.Intent;
import android.widget.*;
import android.util.Log;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

//import android.app.Activity;
import android.os.Bundle;

import android.view.*; //import android.widget.*;
//import android.R.layout.*;
//import android.content.Intent;
import android.view.View.OnClickListener;


/**
 * 
 * 
 * Pick a time  and display the events in that time inteval
 * @author root
 *
 */

public class TimePicker extends Activity {

	private static final String TAG = "TimePicker";

	private TextView mStarTimeTextView;
	private TextView mEndTimeTextView;
	
	private int mStartYear = 1990;
	private int mStartMonth = 5; //start from 0
	private int mStartDay = 1;

	private long start_time_utc = getUTCTime(mStartYear, mStartMonth, mStartDay);
	
	private int mEndYear = 2011;
	private int mEndMonth = 5; //start from 0
	private int mEndDay = 2;
	
	private long end_time_utc = getUTCTime(mEndYear,mEndMonth,mEndDay);
	

	static final int DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;
	
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mStartYear = year;
			mStartMonth = monthOfYear;
			mStartDay = dayOfMonth;
			start_time_utc = getUTCTime(mStartYear, mStartMonth, mStartDay);
			updateTimeViewDisplay();
			
		}
	};

	private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mEndYear = year;
			mEndMonth = monthOfYear;
			mEndDay = dayOfMonth;
			end_time_utc = getUTCTime(mEndYear,mEndMonth,mEndDay);
			updateTimeViewDisplay();
			
		}
	};
	
	
	private long getUTCTime(int y, int m ,int d)
	{
		
		GregorianCalendar calendar = new GregorianCalendar(y,m,d);
		// set time zone
		TimeZone zone = TimeZone.getTimeZone("UTC");
		calendar.setTimeZone(zone);
		return calendar.getTime().getTime()/1000;
		
		
	}

	

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mStartYear,
					mStartMonth, mStartDay);
		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mEndDateSetListener, mEndYear,
					mEndMonth, mEndDay);
		}
		return null;
	}

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_picker);

		Log.d(TAG, "onCreate");
		
		mStarTimeTextView = (TextView) findViewById(R.id.startDateDisplay);
		mEndTimeTextView = (TextView) findViewById(R.id.endDateDisplay);
		
		
		
	
		mStarTimeTextView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});


		mEndTimeTextView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(END_DATE_DIALOG_ID);
			}
		});

		// get the current date and init the start_time and end_time
		
//		final Calendar c = Calendar.getInstance();
//		mStartYear = c.get(Calendar.YEAR);
//		mStartMonth = c.get(Calendar.MONTH);
//		mStartDay = c.get(Calendar.DAY_OF_MONTH);
//
//		mEndYear = c.get(Calendar.YEAR);
//		mEndMonth = c.get(Calendar.MONTH);
//		mEndDay = c.get(Calendar.DAY_OF_MONTH) + 1;

		
		// display the current date, which will be used as the search constraint
		updateTimeViewDisplay();
		
		// back button
		Button backButton = (Button) findViewById(R.id.pick_bt_back);

		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
			}
		});
		
		
		// OK button
		Button okButton = (Button) findViewById(R.id.pick_bt_ok);

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//Intent intent = new Intent();
				
				Intent myIntent = new Intent("com.trident.tv.si.intent.action.LIST");
				
				myIntent.putExtra("start_time", String.valueOf(start_time_utc));
				myIntent.putExtra("end_time", String.valueOf(end_time_utc));
				
                startActivityForResult(myIntent, 1);                 
			}
		});
		
		
		

	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
	//	Log.d(TAG, "onResume is called. LastSelectedPosition is " + lastSelectedPosition);
		
	//	lv.setSelection(lastSelectedPosition);
	//	lv.requestFocusFromTouch();
		
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		Log.d(TAG, "onStart");
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		Log.d(TAG, "onPause");
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		Log.d(TAG, "onStop");
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		
		Log.d(TAG, "onRestart");
	}
	
	

	// updates the date in the TextView
	private void updateTimeViewDisplay() {
		mStarTimeTextView.setText(new StringBuilder()
		        // Month is 0 based so add 1
				.append(mStartMonth + 1).append("-").append(mStartDay).append(
						"-").append(mStartYear).append(" "));

		mEndTimeTextView.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mEndMonth + 1).append("-").append(mEndDay).append("-")
				.append(mEndYear).append(" "));
	}
	
}
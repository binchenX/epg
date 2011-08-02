package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.TimeZone;

//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ContentTypeColumns;
//import com.trident.android.tv.si.provider.epg.EventsContract.Events;
//import com.trident.android.tv.si.provider.epg.EventsContract;


import android.content.Intent;
//import android.net.Uri;
//import android.database.*;
import android.widget.*;
import android.util.Log;
import android.app.Activity;
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.app.ListActivity;
//import android.app.SearchManager;

//import android.app.Activity;
import android.os.Bundle;
//import android.widget.AdapterView.*;
import android.view.*; //import android.widget.*;
//import android.R.layout.*;
//import android.content.Intent;
import android.view.View.OnClickListener;

/**
 * 
 * This Activity is just a demonstration of how to use EPGProvider to write a
 * EPG Program
 * 
 * @author Pierr
 * 
 *         Feature list:
 * 
 *         1. Display the Basic Event list 2. When press will show detail
 *         information 3. Be able to filter/search against the event name ,event
 *         short descriptor, event extended descriptors 4. Be able to search by
 *         event type 5. Be able to filter by event start/end time
 * 
 *         Others:
 * 
 *         1. db_populator will keep populating the database locating at
 *         /data/system/epg-1.db, which is the database the contentProvider open
 *         and read.
 * 
 * 
 */

public class BrowseByActivity extends Activity {

	private static final String TAG = "BrowseByActivity";
	private static int REQUEST_BROWS_BY_CATEGORY = 1;
	private static int REQUEST_BROWS_ALL = 1;
		
	
	

	
	
	
	


	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browser_by);

		Log.d(TAG, "onCreate");
		
		// movie button
		Button movieButton = (Button) findViewById(R.id.bt_movie);
		if(movieButton == null)
		{
			Toast.makeText(this, "Error:no movie button", 1).show();
			return;
		}

		movieButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "movie button has been pressed...........");

				
				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");

				myIntent.putExtra("TYPE", "movie");
				startActivityForResult(myIntent, REQUEST_BROWS_BY_CATEGORY);  
				//finish();
			}
		});
		
		// news button
		Button newsButton = (Button) findViewById(R.id.bt_news);

		newsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "news button has been pressed...........");


				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");
				myIntent.putExtra("TYPE", "news");
				startActivityForResult(myIntent, REQUEST_BROWS_BY_CATEGORY);  
				//finish();
			}
		});
		
		// sports button
		Button sportsButton = (Button) findViewById(R.id.bt_sports);

		sportsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "sports button has been pressed...........");

				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");

				myIntent.putExtra("TYPE", "sports");
				startActivityForResult(myIntent, REQUEST_BROWS_BY_CATEGORY);  
				//finish();
			}
		});
		
		// all button
		Button allButton = (Button) findViewById(R.id.bt_all);

		allButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "sports button has been pressed...........");

				Intent myIntent = new Intent("com.trident.tv.si.intent.action.LIST");
				startActivityForResult(myIntent, REQUEST_BROWS_ALL);  
				//finish();
			}
		});
		
		// byChannel button
		Button byServiceIDButton = (Button) findViewById(R.id.bt_byServiceId);

		byServiceIDButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "byChannel button has been pressed...........");

//				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");
//
//				myIntent.putExtra("TYPE", "sports");
//				startActivity(myIntent);
				//finish();
			}
		});
		
	
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		//Log.d(TAG, "onResume is called. LastSelectedPosition is " + lastSelectedPosition);
		
		//lv.setSelection(lastSelectedPosition);
		//lv.requestFocusFromTouch();
		
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
	


	
}
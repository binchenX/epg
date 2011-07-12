package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ContentTypeColumns;
import com.trident.android.tv.si.provider.epg.EventsContract.Events;

import android.content.Intent;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;

//import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView.*;
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

public class SearchResultActivity extends ListActivity {

	private static final String TAG = "SearchResultActivity";

	private ListView lv;
	private int lastSelectedPosition = 0;
	
	static final int DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;
	
	
	private final int REQUEST_DETAIL = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);

		Log.d(TAG, "onCreate");
				
		lv = getListView(); // == findViewById(android.R.id.list)

		lv.setTextFilterEnabled(true);
				
		//show detail information when item was clicked..

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				lastSelectedPosition = position;
				
				Intent myIntent = new Intent("com.trident.tv.si.intent.action.PICK");
				
				TextView idView = (TextView) view.findViewById(R.id._id);
				
				Log.d(TAG, "get detail of event " + idView.getText());

				myIntent.putExtra("EVENTID", idView.getText());

				startActivityForResult(myIntent, REQUEST_DETAIL);
				

			}
		});
		
		Button button = (Button)findViewById(R.id.backbutton2);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//no need - finish() will make you go back to previous Activity that start the SearchResult
				//instead,following code will always back you to the main page
				//startActivity(new Intent("com.trident.tv.si.intent.action.LIST"));
				finish();
			}
		});

	   //Handle the query ------->>>>>
		
		Log.d(TAG,"Checking query type---------------------------");
		
		//String[] query = getQueryType();

		ListAdapter adapter = null;
		
		String keyWords = getIntent().getStringExtra(SearchManager.QUERY);
		
		//Log.d(TAG,"Checking query type---------------------------" + query[0]);
	
		//adapter = searchByKeywords(keyWords);
		
		Cursor c = managedQuery(EPGProvider.CONTENT_URI_EVENTS_SEARCH,
				new String[] { Events.SERVICE_ID, Events.NAME }, // selections
				null, // always be NULL
				new String[] { keyWords }, // the keywords
				null);

		int results = c.getCount();
		
		TextView textView =  (TextView)findViewById(R.id.search_result);
		
		textView.setText("Find " + results + " programs containing \"" + keyWords + "\"");
		
		adapter = getAdaptor(c);
	
		
		if(adapter == null)
		{
			Log.d(TAG, "Opooos. Find nothing....");
			return; 
		}
		
		setListAdapter(adapter);



	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume is called. LastSelectedPosition is " + lastSelectedPosition);
		
		lv.setSelection(lastSelectedPosition);
		lv.requestFocusFromTouch();
		
		
		
	}
	
	

	private SimpleCursorAdapter getAdaptor(Cursor c) {

		// The Cursor should include all the entries specified in "from"
		// TODO: add check??

		SimpleCursorAdapter adapter = null;

		
		
		if (c.getColumnIndex(ContentTypeColumns.LEVEL1) != -1) {

			//TODO:we can use setTag/getTag to associate a tag, the _id of the event in our case, to
			//the view ,and in the listener we can get the tag.
			adapter = new EventCursorAdaptor(this, 
					R.layout.list_item, 
					c,
					new String[] { Events.ID, Events.SERVICE_ID, Events.NAME,
							Events.LEVEL1, Events.START_TIME }, 
					new int[] { R.id._id,   //the hinder column used to search other information
							R.id.serviceID, R.id.eventName, R.id.eventType,
							R.id.startTime });

		} else {
			// When using FTS, the returned Cursor won't contain level column
			//ID is must to be able get the detail information
			adapter = new EventCursorAdaptor(this, R.layout.list_item, c,
					new String[] { Events.ID, Events.SERVICE_ID, Events.NAME }, 
					new int[] { R.id._id, R.id.serviceID, R.id.eventName });

		}

		return adapter;

	}

	
	

	
}
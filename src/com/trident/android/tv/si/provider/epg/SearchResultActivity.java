package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ContentTypeColumns;

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

	private TextView mStarTimeTextView;
	private TextView mEndTimeTextView;

	private int mStartYear = 2011;
	private int mStartMonth = 5; //start from 0
	private int mStartDay = 1;

	private long start_time_utc = getUTCTime(mStartYear, mStartMonth, mStartDay);
	
	private int mEndYear = 2011;
	private int mEndMonth = 5; //start from 0
	private int mEndDay = 2;
	
	private long end_time_utc = getUTCTime(mEndYear,mEndMonth,mEndDay);
	

	static final int DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;

		
	private long getUTCTime(int y, int m ,int d)
	{
		
		GregorianCalendar calendar = new GregorianCalendar(y,m,d);
		// set time zone
		TimeZone zone = TimeZone.getTimeZone("UTC");
		calendar.setTimeZone(zone);
		return calendar.getTime().getTime()/1000;
		
		
	}

	

	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);

		Log.d(TAG, "onCreate");
				
		ListView lv = getListView(); // == findViewById(android.R.id.list)

		lv.setTextFilterEnabled(true);
				
		//show detail information when item was clicked..

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent myIntent = new Intent("com.trident.tv.si.intent.action.PICK");
				
				TextView idView = (TextView) view.findViewById(R.id._id);

				myIntent.putExtra("EVENT_NAME", idView.getText());

				startActivity(myIntent);
				finish();

			}
		});
		
		Button button = (Button)findViewById(R.id.backbutton2);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent("com.trident.tv.si.intent.action.LIST"));
				
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
	
	
	/**
	 * 
	 * case 1. Enter the main page ,displaying all the events
	 * case 2. User press "Search"
	 * case 3. User press "Movie", "Sports"
	 * case 4. User change the star_time and/or end_time
	 * @return
	 */
	
	private String[] getQueryType()
	{
		Intent intent = getIntent();

		String query = null;

		
		//case 1: 
		if (intent == null ||              //MAIN LAUNCH
			intent.getAction() == null ||   //IMPLICT start 
			intent.getAction().equals("com.trident.tv.si.intent.action.LIST") //EXPLICT start
		    ) {
			
			Log.d(TAG, "started without search query ,will display all the events..");

			return new String[] {"1", ""};
		
		}else if (Intent.ACTION_SEARCH.equals(intent.getAction())){

			   // case 2 :intent from Search bar
				query = intent.getStringExtra(SearchManager.QUERY);
				Log.d(TAG, "Will search .." + query);
				
				return new String[] {"2", query};

	    } else if(getIntent().getAction().equals("com.trident.tv.si.intent.action.CATEGORY")) {
				//case 3:intent from movie/news button
				Bundle bd = getIntent().getExtras();
				
				String type;
				if (bd != null) {

					type = bd.getString("TYPE");
					Log.d(TAG, "try to get events belong to type " + type);
					return new String[] {"3", type};

				}

		}
		
	
		//default to case 1:
		return new String[] {"1", ""};

		
	}
	
	
	ListAdapter searchByKeywords(String keyWords) {
		
		Cursor c = null;
		
		c = managedQuery(EPGProvider.CONTENT_URI_EVENTS_SEARCH,
				new String[] { Events.SERVICE_ID, Events.NAME }, // selections
				null, // always be NULL
				new String[] { keyWords }, // the keywords
				null);

	
	// Used to map notes entries from the database to views
	// show only the event name

	return getAdaptor(c);
		
		
	
	}

	ListAdapter normalSearch(String selection, String[] selectionArgs, String orderBy) {

		Cursor c = null;

		
		//default sort order
		if(orderBy == null)
		{
			
			orderBy = Events.SERVICE_ID + " ASC " + " , " +  Events.START_TIME + " ASC ";
		}

		c = managedQuery(EPGProvider.CONTENT_URI_EVENTS, new String[] {
					Events.SERVICE_ID, Events.NAME, Events.LEVEL1,
					Events.START_TIME }, // projections
					selection,
					selectionArgs, 
					orderBy);
		
		if(c == null)
		{
			return null;
		}


		// Used to map notes entries from the database to views
		// show only the event name

		return getAdaptor(c);
	}

	SimpleCursorAdapter getAdaptor(Cursor c) {

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

	ListAdapter searchByEventType(String type) {
		Cursor c = null;
		// TODO: use FTS instead of LIKE
		Uri uri;
		if(type.equals("movie")) 
		{
			 uri = EPGProvider.CONTENT_URI_EVENTS_MOVIE;
		}else if(type.equals("news")){
			 uri = EPGProvider.CONTENT_URI_EVENTS_NEWS;
		}else if (type.equals("sports")){
			uri = EPGProvider.CONTENT_URI_EVENTS_SPORTS;
		}else{
			return null;
		}
			
		c = managedQuery(uri, null, null,
				null, null);

		return getAdaptor(c);

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

	void populate_the_database() {
		// add 2 EPG events after app starte
		// ---add an event
		// ContentValues values = new ContentValues();
		// values.put(EPGProvider.NAME, "hello");
		// values.put(EPGProvider.SHORT_DESCRIPTION, "hello android");
		// Uri uri = getContentResolver().insert(
		// EPGProvider.CONTENT_URI, values);
		//	               
		// //---add another event---
		// values.clear();
		// values.put(EPGProvider.NAME, "hello2");
		// values.put(EPGProvider.SHORT_DESCRIPTION, "hello android2");
		// uri = getContentResolver().insert(
		// EPGProvider.CONTENT_URI, values);
	}
}
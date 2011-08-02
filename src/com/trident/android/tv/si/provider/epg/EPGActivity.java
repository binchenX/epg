package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
//import java.util.Calendar;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

//import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ContentTypeColumns;
import com.trident.android.tv.si.provider.epg.EventsContract.Events;
//import com.trident.android.tv.si.provider.epg.EventsContract;


import android.content.Intent;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
//import android.app.SearchManager;

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

public class EPGActivity extends ListActivity {

	private static final String TAG = "EPGProviderActivity";
	
	//the listview for the events..
	private ListView lv;
	private int lastSelectedPosition = 0;

	private TextView mStarTimeTextView;
	private TextView mEndTimeTextView;

	private final int  REQEUST_DETAIL = 1;
	
	private int mStartYear = 1960;
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
			filterTheEventByTime();
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
			filterTheEventByTime();
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

	// use current startTime and endTime to filter the result
	void filterTheEventByTime() {
				
		String selection = " start_time > ? AND start_time < ? ";
		Log.d(TAG, "search events between " + start_time_utc + "," + end_time_utc);
		
		String[] selectionArgs = {String.valueOf(start_time_utc), String.valueOf(end_time_utc)};
		ListAdapter adapter = normalSearch( selection, selectionArgs, null);
		setListAdapter(adapter);
		
		
		

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
		setContentView(R.layout.main);

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

		
		lv = getListView(); // == findViewById(android.R.id.list)

		lv.setTextFilterEnabled(true);
				
		//show detail information when item was clicked..

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
				Intent myIntent = new Intent("com.trident.tv.si.intent.action.PICK");
				
				TextView idView = (TextView) view.findViewById(R.id._id);

				//TODO: the name should be com.trident.tv.EVENTID
				myIntent.putExtra("EVENTID", idView.getText());

				lastSelectedPosition = position;

				startActivityForResult(myIntent, REQEUST_DETAIL);
				//should not finish ,we are waiting EventDetailActivity to finish
				//finish();

			}
		});

		// search button
		Button searchButton = (Button) findViewById(R.id.searchbutton);

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// start an search
				Log.d(TAG,"start search...............................");
				onSearchRequested();
				// finish() should not be called

			}
		});
		
		
		//
		// browser by button
		Button browseBy = (Button) findViewById(R.id.bt_browseBy);

		browseBy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(TAG, "browseBy button has been pressed...........");
				Intent myIntent = new Intent(EPGActivity.this, BrowseByActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
//		
		
		
//
//		// movie button
//		Button movieButton = (Button) findViewById(R.id.bt_movie);
//
//		movieButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				Log.d(TAG, "movie button has been pressed...........");
//
//				
//				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");
//
//				myIntent.putExtra("TYPE", "movie");
//				startActivity(myIntent);
//				finish();
//			}
//		});
//		
//		// news button
//		Button newsButton = (Button) findViewById(R.id.bt_news);
//
//		newsButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				Log.d(TAG, "news button has been pressed...........");
//
//
//				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");
//				myIntent.putExtra("TYPE", "news");
//				startActivity(myIntent);
//				finish();
//			}
//		});
//		
//		// movie button
//		Button sportsButton = (Button) findViewById(R.id.bt_sports);
//
//		sportsButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				Log.d(TAG, "sports button has been pressed...........");
//
//
//				Intent myIntent = new Intent("com.trident.tv.si.intent.action.CATEGORY");
//
//				myIntent.putExtra("TYPE", "sports");
//				startActivity(myIntent);
//				finish();
//			}
//		});
		
		//Handle the query ------->>>>>	
		String[] query = getQueryType();

		ListAdapter adapter = null;
		
		Log.d(TAG,"Checking query type---------------------------" + query[0]);
	
	
		switch(Integer.valueOf(query[0]))
		{
			
			//case 2:adapter = searchByKeywords(query[1]);break;
			case 3:adapter = searchByEventType(query[1]);break;
			case 4:  //fall through
			case 1: 
			{
				 //
						
				////select by service_id
				
				if (!query[1].equals(""))
				{
					
					String serviceId = query[1];
					String selection = " service_id = ? AND start_time > ? AND start_time < ? ";
					Log.d(TAG, "search events for service " + serviceId + " between " + start_time_utc + "," + end_time_utc);
					
					String[] selectionArgs = {serviceId, String.valueOf(start_time_utc), String.valueOf(end_time_utc)};
					
					adapter = normalSearch( selection, selectionArgs,null);
					
				//all service	
				}else {
					String selection = " start_time > ? AND start_time < ? ";
					Log.d(TAG, "search events in all service between " + start_time_utc + "," + end_time_utc);
					
					String[]  selectionArgs = {String.valueOf(start_time_utc), String.valueOf(end_time_utc)};
					
					adapter = normalSearch( selection, selectionArgs,null);
					
					
				}
				
				
				break;
				}
			default:
			{
				Log.d(TAG, "unexpect query type,  return ...");
				return;
			}
				
		}
		
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

		//String query = null;

		
		//case 1: 
		if (intent == null ||              //MAIN LAUNCH
			intent.getAction() == null ||   //IMPLICT start 
			intent.getAction().equals("com.trident.tv.si.intent.action.LIST") //EXPLICT start
		    ) {
						
			Bundle bd = getIntent().getExtras();
			
			String serviceID;
			if (bd != null) {

				serviceID = bd.getString("service_id");
				Log.d(TAG, "try to get events belong to service " + serviceID);
				return new String[] {"1", serviceID};

			} else {
				Log.d(TAG, "try to display  all the events..");
				return new String[] {"1", ""};
				
			}

			


		
		}
//		else if (Intent.ACTION_SEARCH.equals(intent.getAction())){
//
//			   // case 2 :intent from Search bar
//				query = intent.getStringExtra(SearchManager.QUERY);
//				Log.d(TAG, "Will search .." + query);
//				
//				return new String[] {"2", query};
//
//	    }
		else if(getIntent().getAction().equals("com.trident.tv.si.intent.action.CATEGORY")) {
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
	
	


	private ListAdapter normalSearch(String selection, String[] selectionArgs, String orderBy) {

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

	private SimpleCursorAdapter getAdaptor(Cursor c) {

		// The Cursor should include all the entries specified in "from"
		// TODO: add check??

		SimpleCursorAdapter adapter = null;

		
		//TODO:if we were to change level1 to concrete_level , what we will use to getColumnIndex
		if (c.getColumnIndex(Events.LEVEL1) != -1) {

			//TODO:we can use setTag/getTag to associate a tag, the _id of the event in our case, to
			//the view ,and in the listener we can get the tag.
			adapter = new EventCursorAdaptor(this, 
					R.layout.list_item, 
					c,
					new String[] { Events._ID, Events.SERVICE_ID, Events.NAME,
							Events.LEVEL1, Events.START_TIME }, 
					new int[] { R.id._id,   //the hinder column used to search other information
							R.id.serviceID, R.id.eventName, R.id.eventType,
							R.id.startTime });

		} else {
			// in case of searching by keywords and by event type, the returned Cursor won't contain level column
			// should always associate R.id._id with Events.ID otherwise , get Detail will Fail
			adapter = new EventCursorAdaptor(this, R.layout.list_item, c,
					new String[] { Events._ID, Events.SERVICE_ID, Events.NAME },
					new int[] { R.id._id,
							R.id.serviceID, R.id.eventName }
				);

		}

		return adapter;

	}

	private ListAdapter searchByEventType(String type) {
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
			
		c = managedQuery(uri, 
				new String[] {
				Events._ID, Events.SERVICE_ID, Events.NAME,
				Events.START_TIME }, // projections 
				null,
				null, null);
		
		//no result
		if(c == null)
		{
			return null;
		}

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
	
}
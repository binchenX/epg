

package com.trident.android.tv.si.provider.epg;

//import android.R.*;

import java.util.*; //For Date

//import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;
//import android.app.ListActivity;


import android.app.Activity;
import android.os.Bundle;
//import android.widget.AdapterView.*;
import android.view.*;
import android.view.View.OnClickListener;
//import android.widget.*;
//import android.R.layout.*;
//import android.content.Intent;

/**
 * 
 * Show the details of certain event and allow to go back to the main EPG 
 * @author Pierr
 *
 */

public class EventDetail extends Activity {
	
	private Context context;
	private static final String TAG = "EventDetail";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//This is a MUST...otherise, findViewByID will return NULL
		setContentView(R.layout.event_detail_layout);
		
		Log.d(TAG, "onCreate");
		
		Button button = (Button)findViewById(R.id.backbutton);
				
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null) {
			String event_name = bundle.getString("EVENT_NAME");
			//use the event_name to query
			
		   Uri allEvents = Uri.parse(
	           "content://com.trident.android.tv.si.provider.EPG/events");
		   String selection = "tblEvent_basic.event_name = ?";
		   String [] selectionArgs = new String[] {event_name};
		   //selectionArgs[0] = event_name;
	       Cursor c = managedQuery(allEvents , null, selection, selectionArgs, null);
	       
	       //should return only one record
	       String short_description;
	       long startTime = 0;
	       int duration = 0;
	       
	       if(c.getCount() < 1)
	       {
	    	   short_description = "No details information avaliable";
	    	   
	       }else{
	    	   c.moveToFirst();
	    	   short_description = c.getString(c.getColumnIndexOrThrow(EPGProvider.SHORT_DESCRIPTION));
	    	   startTime = c.getInt(c.getColumnIndexOrThrow(EPGProvider.START_TIME));
	    	   duration  = c.getInt(c.getColumnIndexOrThrow(EPGProvider.DURATION));
	    	   
	    	   
	    	   //Let's try to get the extended information in another table
	    	  
	    	   
	    	   
	    	   //show the details

	    	   
	    	   ListView detailListView = (ListView)findViewById(R.id.event_detail_list);
		       String []info = new String[] {
		    		   (new Date(startTime)).toLocaleString(),   //star_time
		    		   Integer.toString(duration/60) + "minutes", //duration
		    		   short_description};
	    	   detailListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,info));
	    	   
	    	   
	       }
	       
	     ;
	       
	       
		} else {
			
		}
		
		context = this;
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//create a intent for the EPGProviderActivity
				startActivity(new Intent(context,  EPGProviderActivity.class));
				finish();
			}
		});
		
//		setContentView(detailViewLayout);
		
	//	relativeLayout.addView(button);
	//	LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
	//			LayoutParams.FILL_PARENT);
	//	setContentView(relativeLayout, params);
	}
	
}
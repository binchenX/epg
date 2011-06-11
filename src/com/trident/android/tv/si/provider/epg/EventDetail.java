

package com.trident.android.tv.si.provider.epg;

//import android.R.*;

import java.util.*; //For Date

import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.Clause;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ExtendedColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ExtendedFTSColumns;

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
		   String selection = Clause.QUERY_BASIC_INFO_BY_EVENT_NAME;
		   String [] selectionArgs = new String[] {event_name};
	       Cursor c = managedQuery(EPGProvider.CONTENT_URI_EVENTS , 
	    		   null, 
	    		   selection, 
	    		   selectionArgs, 
	    		   null);
	       
	       //should return only one record
	       String short_description;
	       long startTime = 0;
	       int duration = 0;
	       
	       if(c.getCount() < 1)
	       {
	    	   short_description = "No details information avaliable";
	    	   
	       }else{
	    	   c.moveToFirst();
	    	   short_description = c.getString(c.getColumnIndexOrThrow(BasicColumns.SHORT_DESCRIPTION));
	    	   startTime = c.getInt(c.getColumnIndexOrThrow(BasicColumns.START_TIME));
	    	   duration  = c.getInt(c.getColumnIndexOrThrow(BasicColumns.DURATION));
	    	   
	    	   
	    	  String eguid = c.getString(c.getColumnIndexOrThrow(BasicColumns._ID));
	    	
	    	  Log.d(TAG, "get extended information.....................");
			  	    	  
		      Cursor c2 = managedQuery(Uri.parse(EPGProvider.CONTENT_URI_QUERY_EXTENED + "/" + eguid),
		    		   null, 
		    		   null, //"eguid = ?" ,          //selection 
		    		   null, //new String[] {eguid},  //selection args
		    		   null);
		       
		       
	    	   
	    	   String extended_description = "No extended description";
	    	   
	    	   //c2.moveToFirst();
	    	   
	    	   if(c2 != null){
		       
	    	   extended_description = c2.getString(c2.getColumnIndexOrThrow(ExtendedFTSColumns.ITEM_DES)) 
	    	                        + " : " 
	    	   	    	            + c2.getString(c2.getColumnIndexOrThrow(ExtendedFTSColumns.ITEM_CONTENT));
	    	   }
	    	   //show the details
		       
	    	   ListView detailListView = (ListView)findViewById(R.id.event_detail_list);
		       String []info = new String[] {
		    		   (new Date(startTime * 1000)).toLocaleString(),   //Java need milliseconds, so x 1000
		    		   Integer.toString(duration/60) + "minutes", //duration
		    		   short_description,
		    		   extended_description};
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
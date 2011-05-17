package com.trident.android.tv.si.provider.epg;

//import android.R.*;

//import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;
import android.app.ListActivity;


//import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView.*;
import android.view.*;
//import android.widget.*;
//import android.R.layout.*;
//import android.content.Intent;

/**
 * 
 * This Activity is just a demonstration of how to use EPGProvider to write a EPG Program
 * @author Pierr
 *
 */

public class EPGProviderActivity extends ListActivity {
	
	private static final String TAG = "EPGProviderActivity";
	
	private static final boolean  use_preexsit_database = true;
	
    /** Called when the activity is first created. */

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.main);
       
        Log.d(TAG, "onCreate");
    
       if (!use_preexsit_database) {
               populate_the_database();
        
       }
        
  
       Uri allEvents = Uri.parse(
           "content://com.trident.android.tv.si.provider.EPG/events");
       Cursor c = managedQuery(allEvents , null, null, null, null);
        
       
//       if (c.moveToFirst()) {
//           do{
////              Toast.makeText(this, 
////           	  c.getString(c.getColumnIndexOrThrow(
////           	                 EPGProvider.NAME)) +
////                 c.getString(c.getColumnIndexOrThrow(
////                    EPGProvider.SHORT_DESCRIPTION)), 
////                 Toast.LENGTH_LONG).show();       
//        	   
//        	   
//           } while (c.moveToNext());
//        }
//       
             
       // Used to map notes entries from the database to views
       // show only the event name
       SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c,
               new String[] { "event_name" }, new int[] { android.R.id.text1 });
       
       setListAdapter(adapter);

       
       ListView lv = getListView();
       lv.setTextFilterEnabled(true);

       lv.setOnItemClickListener(new OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view,
             int position, long id) {
           // When clicked, show a toast with the TextView text
           // TODO: when clicked , show the detail information of the events ,
        	 
           //start another activity......
          // Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
          //     Toast.LENGTH_SHORT).show();
           
           Intent myIntent = new Intent(EPGProviderActivity.this, EventDetail.class);
           
           //The detailActivity will use this to query the detail information.
           
           myIntent.putExtra("EVENT_NAME", ((TextView) view).getText());
           startActivity(myIntent);
           finish();
           
         }
       });
        
    }
	
	
	
	void populate_the_database()
	{
		  //add 2 EPG events after app starte
	       //---add an event
//	        ContentValues values = new ContentValues();
//	        values.put(EPGProvider.NAME, "hello");
//	        values.put(EPGProvider.SHORT_DESCRIPTION, "hello android");        
//	        Uri uri = getContentResolver().insert(
//	           EPGProvider.CONTENT_URI, values);
//	               
//	        //---add another event---
//	        values.clear();
//	        values.put(EPGProvider.NAME, "hello2");
//	        values.put(EPGProvider.SHORT_DESCRIPTION, "hello android2");        
//	        uri = getContentResolver().insert(
//	           EPGProvider.CONTENT_URI, values);
	}
}
package com.trident.android.tv.si.provider.epg;

import android.R.*;
import android.content.ContentValues;
import android.net.Uri;
import android.database.*;
import android.widget.*;
import android.util.Log;

import android.app.Activity;
import android.os.Bundle;

public class EPGProviderActivity extends Activity {
	
	private static final String TAG = "EPGProviderActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //add 2 EPG events
       //---add an event
        ContentValues values = new ContentValues();
        values.put(EPGProvider.NAME, "hello");
        values.put(EPGProvider.SHORT_DESCRIPTION, "hello android");        
        Uri uri = getContentResolver().insert(
           EPGProvider.CONTENT_URI, values);
               
        //---add another event---
        values.clear();
        values.put(EPGProvider.NAME, "hello2");
        values.put(EPGProvider.SHORT_DESCRIPTION, "hello android2");        
        uri = getContentResolver().insert(
           EPGProvider.CONTENT_URI, values);
        
        
    //    LOG.D(TAG, "after inserting xxxxxxxxx..................");
        
        //display those two events 
        
        //TODO: error handling ... 
//       Uri allEvents = Uri.parse(
//        "content://com.trident.android.tv.si.provider.EPG/events");
//    Cursor c = managedQuery(allEvents , null, null, null, "_id ASC");
     
    Toast.makeText(this,"Hello Android", 2000);
//     
//     if (c.moveToFirst()) {
//        do{
//           Toast.makeText(this, 
//              c.getString(c.getColumnIndex(
//              EPGProvider._ID)) + ", " +                     
//              c.getString(c.getColumnIndex(
//                 EPGProvider.NAME)) + ", " +                     
//              c.getString(c.getColumnIndex(
//                 EPGProvider.SHORT_DESCRIPTION)), 
//              Toast.LENGTH_LONG).show();               
//        } while (c.moveToNext());
//     }


        
    }
}
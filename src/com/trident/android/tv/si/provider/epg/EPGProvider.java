package com.trident.android.tv.si.provider.epg;

import android.content.ContentProvider;
//import android.content.ContentUris;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
//import android.provider.ContactsContract.Contacts;
//import android.text.TextUtils;
import android.util.Log;
//import java.io.*;

import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.Clause;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ExtendedColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.Table;


/**
 * ref : 
 * http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 * http://www.devx.com/wireless/Article/41133/0/page/2
 * @author Pierr Chen
 *
 */
public class EPGProvider extends ContentProvider 
{
	
	public static final String PROVIDER_NAME = "com.trident.android.tv.si.provider.EPG";
	public static final Uri CONTENT_URI_EVENTS = Uri.parse("content://" + PROVIDER_NAME + "/events");
	public static final Uri CONTENT_URI_QUERY_EXTENED = Uri.parse("content://" + PROVIDER_NAME + "/extended/eguid");
	public static final Uri CONTENT_URI_EVENTS_MOVIE = Uri.parse("content://" + PROVIDER_NAME + "/movie");
	public static final Uri CONTENT_URI_EVENTS_NEWS = Uri.parse("content://" + PROVIDER_NAME + "/news");
	public static final String TAG = "EPGProvider";
	
	

	
	
	//---for database use---
	//the database have alredy been created by native code
    //we just need to open so as to get an handle of it
	 private SQLiteDatabase epgDB;
	

	
	   
	   
	 
	   
	   //we don't create database. we use the one created by native code. 
	   
//	   private static final String DATABASE_CREATE =
//	         "CREATE TABLE IF NOT EXISTS " + TABLE_BASIC + 
//	         " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
//	         " sguid  INT,tsid  INT,onid  INT,service_id  INT," +
//	         " event_id  INT, start_time  INT,duration  INT,running_status  INT,free_ca_mode  INT," +
//	         " event_name  VARCHAR(256)," +
//	         " text VARCHAR(256)," +
//	         " end_time  INT);";
	   
	   private static final int EVENTS = 1;
	   private static final int EVENT_ID = 2;
	   private static final int EXTENDED_QUERY_ID = 3;
	   private static final int EXTENDED_QUERY_EGUID = 4;
	   private static final int MOVIE = 5;
	   private static final int NEWS = 6;
	         		
	   
	   private static final UriMatcher uriMatcher;
	      static{
	         uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	         uriMatcher.addURI(PROVIDER_NAME, "events", EVENTS);
	         uriMatcher.addURI(PROVIDER_NAME, "events/#", EVENT_ID);   
	         
	         //uriMatcher.addURI(PROVIDER_NAME, "extended", EVENTS);
	         //extended/eguid/3 , query the extended information whose eguid = 3
	         //extended/id/3 , query the extended information whose _id = 3
	         uriMatcher.addURI(PROVIDER_NAME, "extended/id/#", EXTENDED_QUERY_ID);
	         uriMatcher.addURI(PROVIDER_NAME, "extended/eguid/#", EXTENDED_QUERY_EGUID);  
	         
	         uriMatcher.addURI(PROVIDER_NAME, "movie", MOVIE);   
	         uriMatcher.addURI(PROVIDER_NAME, "news", NEWS);   
	      }

	   
	 
	 
   @Override
   public int delete(Uri arg0, String arg1, String[] arg2) {
      return 0;
   }

   @Override
   public String getType(Uri uri) {
	   
	   switch (uriMatcher.match(uri)){
       //---get all books---
       case EVENTS:
          return "vnd.android.cursor.dir/vnd.trident.tv.si.events ";
       //---get a particular book---
       case EVENT_ID:                
          return "vnd.android.cursor.item/vnd.trident.tv.si.events ";
       default:
          throw new IllegalArgumentException("Unsupported URI: " + uri);        
    }   

     
   }

   @Override
   public Uri insert(Uri uri, ContentValues values) {
	   
	  Log.d(TAG, "INSERT EVENTS  TO DATABASE xxxxx.................");
	  
//	  //---add a new book---
//      long rowID = epgDB.insert(
//         TABLE_BASIC, "", values);
//           
//      //---if added successfully---
//      if (rowID>0)
//      {
//         Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
//         getContext().getContentResolver().notifyChange(_uri, null);    
//         return _uri;                
//      }        
//      throw new SQLException("Failed to insert row into " + uri);
	  
	  throw new SQLException("Failed to insert row into " + uri);

   }

   @Override
   public boolean onCreate() {
	   
	      Log.d(TAG, "EPGProvider::onCreate");
    	  Context context = getContext();
    	  EPGDatabaseHelp dbHelper = new EPGDatabaseHelp(context);
	      epgDB = dbHelper.getReadableDatabase();
	      return (epgDB == null)? false:true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection,
         String[] selectionArgs, String sortOrder) {
	   
	   Log.d(TAG, "QUERY the database DATABASE " + uri);
	   
	   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	   Cursor c = null;
	   
	   
	   //1.construct the sqlbuild
	   switch(uriMatcher.match(uri)) {
	   
	   case EVENTS:
	     
	      qb.setTables(Table.BASIC);	       
	       
	      if (sortOrder==null || sortOrder=="")
	         sortOrder = BasicColumns._ID;
	      break;
	    
	      
	   case EXTENDED_QUERY_EGUID:

			qb.setTables(Table.EXTENDED);
			if (sortOrder == null || sortOrder == "") {
				sortOrder = BasicColumns._ID;
			}
			long event_guid = ContentUris.parseId(uri);
			// setTablesAndProjectionMapForContacts(qb, uri, projection);
			
			//the final selectionArgs is:
			//even_guid , query()'s selectionArgs parameters
			selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(event_guid));
			
			//the final where statement is:
			//WHERE (<append chunk 1><append chunk2>) AND (<query() selection parameter>)
			qb.appendWhere(ExtendedColumns.EVENG_GUID + " = ? ");

            break;
            
	   case MOVIE:
		   
		   qb.setTables(Table.BASIC);
		   //suppose 1 is MOVIE
		   selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(1));
		   qb.appendWhere(Clause.SEARCH_BY_TYPE);
		   
		   break;
		   
	   case NEWS:
		   
		   qb.setTables(Table.BASIC);
		   //suppose 2 is NEWS
		   selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(2));
		   qb.appendWhere(Clause.SEARCH_BY_TYPE);
		   
		   break;
	   
	   default:
	   
		   throw new IllegalArgumentException("unknown Content Uri");
		  // break;
	   }
	   
	   
	   //2. execute the query and return the cursor
	    c = qb.query(
		         epgDB, 
		         projection, 
		         selection, 
		         selectionArgs, 
		         null, 
		         null, 
		         sortOrder);
		   
		      //---register to watch a content URI for changes---
	    c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	   
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection,
         String[] selectionArgs) {
      return 0;
   }
   
   
   /**
    * Inserts an argument at the beginning of the selection arg list.
    */
   private String[] insertSelectionArg(String[] selectionArgs, String arg) {
       if (selectionArgs == null) {
           return new String[] {arg};
       } else {
           int newLength = selectionArgs.length + 1;
           String[] newSelectionArgs = new String[newLength];
           newSelectionArgs[0] = arg;
           System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, selectionArgs.length);
           return newSelectionArgs;
       }
   }

   private String[] appendProjectionArg(String[] projection, String arg) {
       if (projection == null) {
           return null;
       }
       final int length = projection.length;
       String[] newProjection = new String[length + 1];
       System.arraycopy(projection, 0, newProjection, 0, length);
       newProjection[length] = arg;
       return newProjection;
   }
}


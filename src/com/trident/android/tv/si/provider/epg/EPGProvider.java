package com.trident.android.tv.si.provider.epg;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class EPGProvider extends ContentProvider 
{
	public static final String PROVIDER_NAME = "com.trident.android.tv.si.provider.EPG";
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/events");
	
	
	
	public static final String _ID = "_rowid";
	public static final String SECTION_GUID = "sguid";
	public static final String TSID = "tsid";
	public static final String ONID = "onid";
	public static final String SERVICE_ID = "service_id";
    public static final String START_TIME = "start_time";
    public static final String DURATION = "duration";
    public static final String RUNNING_STATUS = "running_status";
    public static final String CA_MODE = "free_ca_mode";
	public static final String NAME = "event_name";
	public static final String SHORT_DESCRIPTION = "text";
	
	
	
	  //---for database use---
	   private SQLiteDatabase epgDB;
	   private static final String DATABASE_NAME =  "epg.db";
	   private static final String DATABASE_TABLE = "tblEvent_basic";
	   private static final int DATABASE_VERSION = 1;
	   private static final String DATABASE_CREATE =
	         "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + 
	         " (_id integer primary key autoincrement, "+
	         " sguid  INT,tsid  INT,onid  INT,service_id  INT," +
	         " event_id  INT, start_time  INT,duration  INT,running_status  INT,free_ca_mode  INT," +
	         " event_name  VARCHAR(256)," +
	         " text VARCHAR(256)," +
	         " end_time  INT);";
	         		
	   
	   private static class EPGDatabaseOpenHelper extends SQLiteOpenHelper 
	   {
		   EPGDatabaseOpenHelper(Context context) {
	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	      }

	      @Override
	      public void onCreate(SQLiteDatabase db) 
	      {
	         db.execSQL(DATABASE_CREATE);
	      }

	      @Override
	      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	      int newVersion) {
	         Log.w("Content provider database", 
	              "Upgrading database from version " + 
	              oldVersion + " to " + newVersion + 
	              ", which will destroy all old data");
	         db.execSQL("DROP TABLE IF EXISTS titles");
	         onCreate(db);
	      }
	   }   

	
   @Override
   public int delete(Uri arg0, String arg1, String[] arg2) {
      return 0;
   }

   @Override
   public String getType(Uri uri) {
      return null;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values) {
      return null;
   }

   @Override
   public boolean onCreate() {
      return false;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection,
         String[] selectionArgs, String sortOrder) {
      return null;
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection,
         String[] selectionArgs) {
      return 0;
   }
}


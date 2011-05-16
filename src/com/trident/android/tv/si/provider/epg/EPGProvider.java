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
import java.io.*;


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
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/events");
	
	public static final String TAG = "EPGProvider";
	
	public static final String _ID = "_id";
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
	  //the database have alredy been created by native code
      //we just need to open so as to get an handle of it
	   private SQLiteDatabase epgDB;
	   private static final String DATABASE_NAME =  "epg_1.db";
	   private static final String DATABASE_TABLE = "tblEvent_basic";
	   private static final int DATABASE_VERSION = 1;
	   private static final String DATABASE_CREATE =
	         "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + 
	         " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
	         " sguid  INT,tsid  INT,onid  INT,service_id  INT," +
	         " event_id  INT, start_time  INT,duration  INT,running_status  INT,free_ca_mode  INT," +
	         " event_name  VARCHAR(256)," +
	         " text VARCHAR(256)," +
	         " end_time  INT);";
	   
	   public static final int EVENTS = 1;
	   public static final int EVENT_ID = 2;
	         		
	   
	   private static final UriMatcher uriMatcher;
	      static{
	         uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	         uriMatcher.addURI(PROVIDER_NAME, "events", EVENTS);
	         uriMatcher.addURI(PROVIDER_NAME, "events/#", EVENT_ID);      
	      }

	   
	   private static class EPGDatabaseOpenHelper extends SQLiteOpenHelper 
	   {
		   
		   private final Context myContext;
		   private static final String PACKAGE_NAME = "com.trident.android.tv.si.provider.epg";
		   private static final String DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/";
			
		   
		   EPGDatabaseOpenHelper(Context context) {
			   
	       super(context, DATABASE_NAME, null, DATABASE_VERSION);
	       Log.d(TAG, "EPGDatabaseOpenhelper constructor");
	       
	       this.myContext = context;
	      }

		   
		   
	      @Override
	      public void onCreate(SQLiteDatabase db) 
	      {
	    	  
	    	  //create a db at /data/data/your_package/database
	    	 Log.d(TAG, "EPGDatabaseOpenHelper::onCreate");
	    	 
	    	 //a database will be created at 
	    	 ///data/data/com.trident.android.tv.si.provider.epg/databases
	    	 //db.execSQL(DATABASE_CREATE);

	    	 //copy database from assert to data/data/package_name/
	    	 //did not work..... 
	    	 //copyDatabaseFromAssetToDataDir();
	         
	      }

	      boolean copyDatabaseFromAssetToDataDir()
	      {
	    	  
	     	   InputStream myInput; 
	    	  //Open the empty db as the output stream
		      	OutputStream myOutput;
		     // Path to the just created empty db
		      	String outFileName = DATABASE_PATH + DATABASE_NAME;
	    	
		      	Log.d(TAG, "copy database from apk to /data/data/package/database");
		      	
	    	  try {
	    		//Open your local db ,which stored in the assert direct of the apk file , as the input stream
	    	  myInput = myContext.getAssets().open(DATABASE_NAME);
	    	  myOutput = new FileOutputStream(outFileName);
	    	  
	    	 //transfer bytes from the inputfile to the outputfile
		      	byte[] buffer = new byte[1024];
		      	int length;
		      	while ((length = myInput.read(buffer))>0){
		      		myOutput.write(buffer, 0, length);
		      	}
	    	 
	 	      	//Close the streams
			   	myOutput.flush();
			    myOutput.close();
			    myInput.close();
	    	  
	    	  }catch(IOException x)  {
	    		  x.printStackTrace();
	    		  Log.d(TAG,x.toString());
	    		  
	    		  return false;
	    	  }
	  
	      	
	    	  return true;
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
	  
	  //---add a new book---
      long rowID = epgDB.insert(
         DATABASE_TABLE, "", values);
           
      //---if added successfully---
      if (rowID>0)
      {
         Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
         getContext().getContentResolver().notifyChange(_uri, null);    
         return _uri;                
      }        
      throw new SQLException("Failed to insert row into " + uri);

   }

   @Override
   public boolean onCreate() {
	   
	      Log.d(TAG, "EPGProvider::onCreate");
    	  Context context = getContext();
    	  EPGDatabaseOpenHelper dbHelper = new EPGDatabaseOpenHelper(context);
	      epgDB = dbHelper.getWritableDatabase();
	      return (epgDB == null)? false:true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection,
         String[] selectionArgs, String sortOrder) {
	   
	   Log.d(TAG, "QUERY the database DATABASE..................");

	      SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	      sqlBuilder.setTables(DATABASE_TABLE);
	       
	      //---if getting a particular event
//	      if (uriMatcher.match(uri) == EVENT_ID) {
//	       
//	         sqlBuilder.appendWhere(
//	            _ID + " = " + uri.getPathSegments().get(1));     
//	      }
	       
	      if (sortOrder==null || sortOrder=="")
	         sortOrder = _ID;
	   
	      Cursor c = sqlBuilder.query(
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
}


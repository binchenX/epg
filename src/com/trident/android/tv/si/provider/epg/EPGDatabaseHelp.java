package com.trident.android.tv.si.provider.epg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

  /*package*/ class EPGDatabaseHelp extends SQLiteOpenHelper 
	   {
	  
	       private static final String TAG = "EPGDatabaseHelp"; 		   
		   private final Context myContext;
		   private static final String PACKAGE_NAME = "com.trident.android.tv.si.provider.epg";
		   //path for application mode
		   //private static final String DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/";
		   
		   //From the native code's perspective it is something like /mnt/nfsroot/data/system, 
		   //The native code should be able to write to this directory,
		   //the ContentProviderEPG.apk should be able to read it..
		   private static final String DATABASE_PATH = "/data/system/";
		   
		   private String dbName;	
		   private static final String DATABASE_NAME =  "epg_1.db";
		   private static final int DATABASE_VERSION = 2;
		   
		   
		   public interface  Table{
			   
			   public static final String BASIC = "tblEvent_basic";
			   public static final String EXTENDED = "tblEvent_extended";
			   public static final String EXTENDED_FTS = "tblEvent_extDes";
			   public static final String GROUP = "tblEvent_group";
			   
		   }
		   
		   public interface BasicColumns{
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
				
		   }
		   
		   public interface ExtendedColumns{
			   
			   public static final String _ID = "_id";
			   public static final String SECTION_GUID = "sguid";
			   public static final String EVENG_GUID = "eguid";
			   public static final String ITEM_DES = "item_description";
			   public static final String ITEM_CONTENT = "item";
			   public static final String SERVICE_ID = "service_id";
			   public static final String EVENT_ID = "event_id";
			   public static final String DESCRIPTOR_NUMBER = "descriptor_number";
			   public static final String LAST_DESCRIPTOER_NUMER = "last_descriptor_number";
			   
			   
		   }
		   
           public interface ExtendedFTSColumns{
			   
			   public static final String _ID = "_id";
			   //public static final String SECTION_GUID = "sguid";
			   public static final String EVENG_GUID = "_id";
			   public static final String ITEM_DES = "item_description";
			   public static final String ITEM_CONTENT = "item";
			   //public static final String SERVICE_ID = "service_id";
			   //public static final String EVENT_ID = "event_id";
			   //public static final String DESCRIPTOR_NUMBER = "descriptor_number";
			   //public static final String LAST_DESCRIPTOER_NUMER = "last_descriptor_number";
			   
			   
		   }
		   
		   public interface Clause{
			   public static final String QUERY_BASIC_INFO_BY_EVENT_NAME = BasicColumns.NAME + " = ?";
			   public static final String SEARCH_BY_TYPE = "_id IN (select eguid FROM tblEvent_content WHERE level1=? )";
		   }
		   
		   EPGDatabaseHelp(Context context) {
			   
	       super(context, DATABASE_NAME, null, DATABASE_VERSION);
	       Log.d(TAG, "EPGDatabaseOpenhelper constructor");
	       this.dbName = DATABASE_NAME;
	       
	       this.myContext = context;
	      }

		   
		   
		   
		   
		   
		   @Override
		   public synchronized SQLiteDatabase getReadableDatabase ()
		   {
			 try{  
			 return   SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
			
			 }catch(SQLiteException ex)
			 {
				 Log.d(TAG, "unable to open the database..");
				 ex.printStackTrace();
				 return null;
			 }
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
		      	String outFileName = DATABASE_PATH + this.dbName;
	    	
		      	Log.d(TAG, "copy database from apk to /data/data/package/database");
		      	
	    	  try {
	    		//Open your local db ,which stored in the assert direct of the apk file , as the input stream
	    	  myInput = myContext.getAssets().open(this.dbName);
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

	
	      
	    
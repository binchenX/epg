package com.trident.android.tv.si.provider.epg;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.trident.android.tv.si.provider.epg.EventsContract.Events;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * A help class for EPGProvider.
 * 
 * Designed with only package access. 
 * 
 * 
 */



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
		   //Currently, plfApp are using this directory to save data, so it make sense to put it here.
		   
		   
		   //on simulator 
		   private static final String DATABASE_PATH = "/data/system/";
		   private static final String DATABASE_NAME =  "epg.db";
		   //for SX5
		   //private static final String DATABASE_PATH = "/tmp/";
		   //private static final String DATABASE_NAME =  "eit.sqlite";
		   
		   private String dbName;	
	
		   private static final int DATABASE_VERSION = 2;
		   
		   
		   public interface  Table{
			   
			   public static final String BASIC = "tblEvent_basic";
			   public static final String EXTENDED = "tblEvent_extended";
			   public static final String EXTENDED_FTS = "tblEvent_extdes";
			   public static final String GROUP = "tblEvent_group";
			   public static final String TYPE = "tblEvent_content";
			   public static final String RATINGS = "tblEvent_rating";
			   public static final String BASIC_JOIN_SHORT_DES = "tblEvent_basic a INNER JOIN tblEvent_shortdes b ON a.rowid = b.eguid";
			   public static final String BASIC_JOIN_TYPE = Table.BASIC + " LEFT OUTER JOIN " + Table.TYPE + 
			                                                " ON tblEvent_basic._id = tblEvent_content.eguid ";
			   
		   }
		   
		   public interface BasicColumns{
			   
			    public static final String CONCRETE_ID          =  Table.BASIC + "." + EventsContract.Events._ID;
				public static final String CONCRETE_SECTION_GUID = Table.BASIC + "." + EventsContract.Events.SECTION_GUID;//"sguid";
				public static final String CONCRETE_TSID         = Table.BASIC + "." + EventsContract.Events.TSID;
				public static final String CONCRETE_ONID         = Table.BASIC + "." + EventsContract.Events.ONID;
				public static final String CONCRETE_SERVICE_ID   = Table.BASIC + "." + EventsContract.Events.SERVICE_ID;
			    public static final String CONCRETE_START_TIME   = Table.BASIC + "." + EventsContract.Events.START_TIME;
			    public static final String CONCRETE_DURATION     = Table.BASIC + "." + EventsContract.Events.DURATION;
			    public static final String CONCRETE_RUNNING_STATUS = Table.BASIC + "." + EventsContract.Events.RUNNING_STATUS;
			    public static final String CONCRETE_CA_MODE      = Table.BASIC + "." + EventsContract.Events.CA_MODE;
				public static final String CONCRETE_NAME         = Table.BASIC + "." + EventsContract.Events.NAME;
				public static final String CONCRETE_SHORT_DESCRIPTION = Table.BASIC + "." + EventsContract.Events.SHORT_DESCRIPTION;
				
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
			   public static final String EVENG_GUID = "eguid";
			   public static final String ITEM_DES = "item_description";
			   public static final String ITEM_CONTENT = "item";
			   //public static final String SERVICE_ID = "service_id";
			   //public static final String EVENT_ID = "event_id";
			   //public static final String DESCRIPTOR_NUMBER = "descriptor_number";
			   //public static final String LAST_DESCRIPTOER_NUMER = "last_descriptor_number";
			   
			   
			   
		   }
           
           public interface RatingColumns{
        	   public static final String CONCRETE_ID =  Table.RATINGS + "." + Events._ID;
        	   public static final String EVENT_ID = Table.RATINGS + "." + "eguid";
        	   public static final String CONCRETE_COUNTRY_CODE = Table.RATINGS + "." + EventsContract.RatingColumns.COUNTRY_CODE;
        	   public static final String CONCRETE_RATING = Table.RATINGS + "." + EventsContract.RatingColumns.RATING;
           }
           
        public interface ShortDesFTSColumns{
			   
			   public static final String _ID = "_id";
			   public static final String EVENG_GUID = "eguid";
			   public static final String NAME = "event_name";
			   public static final String SHORT_DESCRIPTION = "short_des";
			   
		   }
        
        public interface ContentTypeColumns{
        	public static final String _ID = "_id";
        	public static final String CONCRETE_LEVEL1 = Table.TYPE + EventsContract.Events.LEVEL1;
        	public static final String CONCRETE_LEVEL2 = Table.TYPE + EventsContract.Events.LEVEL1;
        	
        }
 
		   
		   public interface Clause{
			   public static final String QUERY_BASIC_INFO_BY_EVENT_NAME = BasicColumns.CONCRETE_NAME + " = ?";
			   public static final String QUERY_BASIC_INFO_BY_ID =  BasicColumns.CONCRETE_ID + " = ?";
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
				 
				 Log.d(TAG,"Trying to open database " + DATABASE_PATH+DATABASE_NAME);
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

	
	      
	    
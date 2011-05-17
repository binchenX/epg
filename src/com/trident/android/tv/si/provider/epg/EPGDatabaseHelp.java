package com.trident.android.tv.si.provider.epg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

  /*package*/ class EPGDatabaseHelp extends SQLiteOpenHelper 
	   {
	  
	       private static final String TAG = "EPGDatabaseHelp"; 		   
		   private final Context myContext;
		   private static final String PACKAGE_NAME = "com.trident.android.tv.si.provider.epg";
		   private static final String DATABASE_PATH = "/data/data/" + PACKAGE_NAME + "/databases/";
		   private String dbName;	
		   private static final int DATABASE_VERSION = 2;
		   
		   
		   public interface  Table{
			   
			   public static final String BASIC = "tblEvent_basic";
			   public static final String EXTENDED = "tblEvent_extended";
			   public static final String GROUP = "tblEvent_group";
			   
			   
			   
		   }
		   EPGDatabaseHelp(Context context ,String dbName) {
			   
	       super(context, dbName, null, DATABASE_VERSION);
	       Log.d(TAG, "EPGDatabaseOpenhelper constructor");
	       this.dbName = dbName;
	       
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

	
	      
	    
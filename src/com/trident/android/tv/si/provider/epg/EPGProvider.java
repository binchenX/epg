package com.trident.android.tv.si.provider.epg;

import java.util.HashMap;

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
import android.provider.BaseColumns;
//import android.provider.SyncStateContract.Columns;
//import android.provider.ContactsContract.Contacts;
//import android.text.TextUtils;
import android.util.Log;
//import java.io.*;

import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.BasicColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.Clause;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ContentTypeColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ExtendedFTSColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.RatingColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.ShortDesFTSColumns;
import com.trident.android.tv.si.provider.epg.EPGDatabaseHelp.Table;
import com.trident.android.tv.si.provider.epg.EventsContract.Events;


/**
 * EPGProvider is the provider of EPG data within the TV system. 
 * 
 * @author Pierr Chen
 *
 */
public class EPGProvider extends ContentProvider 
{
	
	public static final String PROVIDER_NAME = "com.trident.android.tv.si.provider.EPG";
	
	/**
	 *
	 * Normal query. The projections, selections and selectionArgs are limited to :
	 * Following Values.
	 * 
	 * <p>
	 * 
     * <ul>
	 * <li>Events.SECTION_GUID
	 * <li> Events.TSID 
	 * <li>	Events.ONID 
	 * <li> Events.SERVICE_ID
	 * <li> Events.START_TIME 
	 * <li> Events.DURATION 
	 * <li> Events.RUNNING_STATUS 
	 * <li> Events.CA_MODE 
	 * <li> Events.NAME 
	 * <li>	Events.SHORT_DESCRIPTION
	 * <li> Events.level1 *
	 * <li> Events.level2
	 * </ul>
	 *  <p>
	 *  @see Events
	 * 
	 *
	 */
	public static final Uri CONTENT_URI_EVENTS = Uri.parse("content://" + PROVIDER_NAME + "/events");
	
	/**
	 * 
	 * 
	 * Query the events by keywords.
	 * <p>
	 * 
	 * 
	 * 1. We will search following columns in respectively table,
	 * Events.NAME , Events.SHORT_DESCRITPION and Events.item
	 * 
	 * <p>
	 * 2. It is important to note that CONTENT_URI_EVENTS_SEARCH the projects are limited to :
	 * <p>
	 * <ul>
	 * <li>Events.SECTION_GUID
	 * <li> Events.TSID 
	 * <li>	Events.ONID 
	 * <li> Events.SERVICE_ID
	 * <li> Events.START_TIME 
	 * <li> Events.DURATION 
	 * <li> Events.RUNNING_STATUS 
	 * <li> Events.CA_MODE 
	 * <li> Events.NAME 
	 * <li>	Events.SHORT_DESCRIPTION
	 * </ul>
	 * To get the projections Events.item, Events.item_descriptions the user should issue another query 
	 * using CONTENT_URI_QUERY_EXTENED
	 * 
	 */
	public static final Uri CONTENT_URI_EVENTS_SEARCH = Uri.parse("content://" + PROVIDER_NAME + "/events/search");
	
	/**
	 * Query extended events by eguid. Will return following attributes associated with this event specified by
	 * the eguid.
	 * <p>
	 * Events.item, Events.item_descriptions
	 * 
	 * <p>
	 * user case : get the basic event, use the id of that events to get the extended descriptors.
	 * 
	 * 
	 */
	public static final Uri CONTENT_URI_QUERY_EXTENED = Uri.parse("content://" + PROVIDER_NAME + "/extended/eguid");
	
	
	/**
	 * Query extended events by Rating. Will return following attributes associated with this event specified by
	 * the eguid.
	 * <p>
	 * Events.country_code, Events.rating
	 * 
	 * <p>
	 * user case : get the basic event, use the id of that events to get the extended descriptors.
	 * 
	 * 
	 */
	public static final Uri CONTENT_URI_QUERY_RATING = Uri.parse("content://" + PROVIDER_NAME + "/ratings/eguid");
	
	
	
	/**
	 * 
	 * Query all the movie events. The projections can be used are some as that when using CONTENT_URI_EVENTS.
	 * 
	 */
	public static final Uri CONTENT_URI_EVENTS_MOVIE = Uri.parse("content://" + PROVIDER_NAME + "/movie");
	
	/**
	 * 
	 * Query all the news events. 
	 */
	public static final Uri CONTENT_URI_EVENTS_NEWS = Uri.parse("content://" + PROVIDER_NAME + "/news");
	
	/**
	 * 
	 * Query all the sports events.  
	 */
	public static final Uri CONTENT_URI_EVENTS_SPORTS = Uri.parse("content://" + PROVIDER_NAME + "/sports");
	
	/**
	 * 
	 * Query all the music events.
	 */
	public static final Uri CONTENT_URI_EVENTS_MUSIC = Uri.parse("content://" + PROVIDER_NAME + "/music");
	
	/**
	 * Query all the educations events. 
	 * 
	 */
	public static final Uri CONTENT_URI_EVENTS_EDUCATION = Uri.parse("content://" + PROVIDER_NAME + "/education");
	
	
	
	
	
	/**
	 * 
	 * private THINGS.....................
	 * 
	 */
	
	private static final String TAG = "EPGProvider";
	
	private static final HashMap<String,String> columnMap = buildColumnMap();
	private static final HashMap<String,String> searchColumnMap =  buildSearchColumnMap();

	private static final HashMap<String,String> tblExtFTScolumnMap = buildExtFTSColumnMap();
	
	

	
	
	//---for database use---
	//the database have alredy been created by native code
    //we just need to open so as to get an handle of it
	 private SQLiteDatabase epgDB;
	

	
	   
	   
	 
	   private static final int QUERY_EVENTS_ALL = 1;
	   private static final int QUERY_EVENT_ID = 2;
	   private static final int QUERY_EXTENDED_QUERY_ID = 3;
	   private static final int QUERY_EXTENDED_QUERY_EGUID = 4;
	   private static final int QUERY_EVENTS_SEARCH = 7;
	   private static final int QUERY_RATINGS_QUERY_EGUID = 8;
	   
	   //for Query by  genre
	   private static final int QUERY_MOVIE = 0x1001;
	   private static final int QUERY_NEWS  = 0x1002;
	   private static final int QUERY_SPORTS  = 0x1003;
	   private static final int QUERY_MUSIC  = 0x1004;
	   private static final int QUERY_EDUCATION  = 0x1005;
	   
	   //TODO: native code should convert standard specific type definition to a unified code
	   //Currently, let's just use what was for DVB
	   //see EventsContract.Gerne
	   
	   
	   
	   //For EventType
	   //Different standards are using different classification system. That means we should use different value for
	   //when searching in the database. However, for user, that will always use string to search ,e.g: CONTENT_URI_EVENTS_MOVIE 
	   //ARIB : STD-B10 Annex H : Genre designation in content descriptor
	   //DVB  : DVB SI Spec section 6.2.9 Content Descriptor
	   

	   private static final HashMap<Integer,String> dvbEventTypeMap = buildDvbEventTypeMap();
	   
	   private static HashMap<Integer,String> buildDvbEventTypeMap() {
	          HashMap<Integer,String> map = new HashMap<Integer,String>();
	          //the value should be in decimal , that means "0x01" is not correct
	          map.put(new Integer(QUERY_MOVIE),     "1");
	          map.put(new Integer(QUERY_NEWS),      "2");
	          map.put(new Integer(QUERY_SPORTS),    "4");
	          map.put(new Integer(QUERY_MUSIC),     "6");
	          map.put(new Integer(QUERY_EDUCATION), "0");
	          
	          
	          
	          return map;
	          
	   }

// event type mapping for ARIB	   
//	   private static HashMap<Integer,String> buildARIBEventTypeMap() {
//	          HashMap<Integer,String> map = new HashMap<Integer,String>();
//	          
//	          map.put(new Integer(QUERY_MOVIE),     "6");
//	          map.put(new Integer(QUERY_NEWS),      "0");
//	          map.put(new Integer(QUERY_SPORTS),    "1");
//	          map.put(new Integer(QUERY_MUSIC),     "4");
//	          map.put(new Integer(QUERY_EDUCATION), "A");
//	          return map;
//	          
//	   }
	   
	   
	   
	   String getLevelValueAccordingToStandards(int type, String standards)
	   {
		   
		   //default to DVB at this moment
		   
		   return dvbEventTypeMap.get(new Integer(type));
		   
	   }
	         		
	   
	   private static final UriMatcher uriMatcher;
	      static{
	         uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	         uriMatcher.addURI(PROVIDER_NAME, "events", QUERY_EVENTS_ALL);
	         uriMatcher.addURI(PROVIDER_NAME, "events/search", QUERY_EVENTS_SEARCH);
	         uriMatcher.addURI(PROVIDER_NAME, "events/#", QUERY_EVENT_ID);   
	         
	         //uriMatcher.addURI(PROVIDER_NAME, "extended", EVENTS);
	         //extended/eguid/3 , query the extended information whose eguid = 3
	         //extended/id/3 , query the extended information whose _id = 3
	         uriMatcher.addURI(PROVIDER_NAME, "extended/id/#", QUERY_EXTENDED_QUERY_ID);
	         uriMatcher.addURI(PROVIDER_NAME, "extended/eguid/#", QUERY_EXTENDED_QUERY_EGUID);  
	         
	         uriMatcher.addURI(PROVIDER_NAME, "ratings/eguid/#", QUERY_RATINGS_QUERY_EGUID);  
	         
	         uriMatcher.addURI(PROVIDER_NAME, "movie",  QUERY_MOVIE);   
	         uriMatcher.addURI(PROVIDER_NAME, "news",   QUERY_NEWS);  
	         uriMatcher.addURI(PROVIDER_NAME, "sports", QUERY_SPORTS);  
	         uriMatcher.addURI(PROVIDER_NAME, "music",  QUERY_MUSIC);  
	         uriMatcher.addURI(PROVIDER_NAME, "education", QUERY_EDUCATION);  
	      }

	     private static HashMap<String,String> buildColumnMap() {
	          HashMap<String,String> map = new HashMap<String,String>();
	          
	          
	     /*     map.put(BasicColumns._ID,                Table.BASIC + "." + BasicColumns._ID);
	          map.put(BasicColumns.NAME,               Table.BASIC + "." + BasicColumns.NAME);
	          map.put(BasicColumns.SHORT_DESCRIPTION,  Table.BASIC + "." + BasicColumns.SHORT_DESCRIPTION);
	     */     
	
	     /*     map.put(ExtendedFTSColumns._ID,         "rowid as _id");
	          map.put(ExtendedFTSColumns.EVENG_GUID,   ExtendedFTSColumns.EVENG_GUID +  " AS eguid" );
	          map.put(ExtendedFTSColumns.ITEM_CONTENT, ExtendedFTSColumns.ITEM_CONTENT);
	          map.put(ExtendedFTSColumns.ITEM_DES,     ExtendedFTSColumns.ITEM_DES);*/
	          
	          map.put(ShortDesFTSColumns._ID,         "rowid as _id");
	          map.put(ShortDesFTSColumns.EVENG_GUID,            ShortDesFTSColumns.EVENG_GUID );
	          map.put(ShortDesFTSColumns.NAME,                  ShortDesFTSColumns.NAME);
	          map.put(ShortDesFTSColumns.SHORT_DESCRIPTION,     ShortDesFTSColumns.SHORT_DESCRIPTION);
		       		         
	    
	          return map;
	      }
	     
	     /*
	      * 
	      * Solve the ambiguity column name , "event_name" , "short_description" as both tblEvent_basic 
	      * and tblEvent_shortDes have those column and Ambiguity raise when we join those two table.
	      * */
	     
	     
	     
	     private static HashMap<String,String> buildSearchColumnMap() {
	          HashMap<String,String> map = new HashMap<String,String>();
	          
	          map.put(BaseColumns._ID,                    BaseColumns._ID);
	          map.put(Events.NAME,                  BasicColumns.CONCRETE_NAME);
	          map.put(Events.SHORT_DESCRIPTION,     BasicColumns.CONCRETE_SHORT_DESCRIPTION);
	          
	          return map;
	      }
	     
	     private static HashMap<String,String>  buildExtFTSColumnMap(){
	          HashMap<String,String> map = new HashMap<String,String>();
	          
	          
	          map.put(BaseColumns._ID  ,       "rowid as " + BaseColumns._ID);
	          map.put(ExtendedFTSColumns.EVENG_GUID , ExtendedFTSColumns.EVENG_GUID);
	          map.put(ExtendedFTSColumns.ITEM_DES, ExtendedFTSColumns.ITEM_DES);
	          map.put(ExtendedFTSColumns.ITEM_CONTENT ,           ExtendedFTSColumns.ITEM_CONTENT);
	          
	          return map;
	      }

	 
	 
   @Override
   public int delete(Uri arg0, String arg1, String[] arg2) {
      return 0;
   }

   @Override
   public String getType(Uri uri) {
	   
	   switch (uriMatcher.match(uri)){
       //---get all books---
       case QUERY_EVENTS_ALL:
          return "vnd.android.cursor.dir/vnd.trident.tv.si.events ";
       //---get a particular book---
       case QUERY_EVENT_ID:                
          return "vnd.android.cursor.item/vnd.trident.tv.si.events ";
       default:
          throw new IllegalArgumentException("Unsupported URI: " + uri);        
    }   

     
   }
   
   /**
    * 
    * Application shall never use this function as it is the native code 
    * that is responsible for updating the database. When called,  this function will throw an exception.
    * 
    */

   @Override
   public Uri insert(Uri uri, ContentValues values) {
	   
	  Log.d(TAG, "ERROR: shall never try to write to the database");
	    
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

   
   /**
    * Test if any of the columns appear in the given projection.
    */
   private boolean isInProjection(String[] projection, String... columns) {
       if (projection == null) {
           return true;
       }

       // Optimized for a single-column test
       if (columns.length == 1) {
           String column = columns[0];
           for (String test : projection) {
               if (column.equals(test)) {
                   return true;
               }
           }
       } else {
           for (String test : projection) {
               for (String column : columns) {
                   if (column.equals(test)) {
                       return true;
                   }
               }
           }
       }
       return false;
   }
   
   /**
    * 
    * THE ENTRY point for query. For details see following URL.
    * 
    * 
    */
   @Override
   public Cursor query(Uri uri, String[] projection, String selection,
         String[] selectionArgs, String sortOrder) {
	   
	   Log.d(TAG, "QUERY the database DATABASE " + uri);
	   
	   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	   Cursor c = null;
	   
	   int query_type = uriMatcher.match(uri);
	   //1.construct the sqlbuild
	   switch(query_type) {
	   
	   case QUERY_EVENTS_ALL:
		   
		  //if it the project contains columns on ContentTypeColumn, need JOIN
		  //otherwise,simply setTable to Table.BASIC
		   
		  if(isInProjection(projection, 
				  Events.LEVEL1,
				  Events.LEVEL2))
		  {
			  qb.setTables(Table.BASIC_JOIN_TYPE);
			 
		  } else {
			  qb.setTables(Table.BASIC); 
		  }
		  
	     	      
	      //should always select _id, otherwise, cursor won't working complaining : column '_id' does not exist
	      //_id will become ambiguous when join with tblEvent_content
		  projection = prePendProjectionArg(projection, BasicColumns.CONCRETE_ID);
	       
	      if (sortOrder==null || sortOrder=="")
	         sortOrder = BasicColumns.CONCRETE_ID;
	      break;
	    
	      
	   case QUERY_EXTENDED_QUERY_EGUID:
	   {
			qb.setTables(Table.EXTENDED_FTS);
			if (sortOrder == null || sortOrder == "") {
				sortOrder =  "rowid"; //ExtendedFTSColumns._ID;
			}
			
			long event_guid = ContentUris.parseId(uri);
			//setTablesAndProjectionMapForContacts(qb, uri, projection);
			
			Log.d(TAG, "query extended descriotors for eguid " + event_guid);
			
			//the final selectionArgs is:
			//even_guid , query()'s selectionArgs parameters
			//selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(event_guid));
			
			//selectionArgs = new String[] {"196"};
			
			//I can not get the ? to work with selectionArgs, so use this workaround
			//TODO:find the root cause
			qb.appendWhere(ExtendedFTSColumns.EVENG_GUID + " =  " + String.valueOf(event_guid));
			
			qb.setProjectionMap(tblExtFTScolumnMap);

			
			//just for test
			//String sql = "SELECT rowid as _id, item, item_description FROM tblEvent_extDes WHERE eguid = " + 
			//			  String.valueOf(event_guid) + " ORDER by rowid";
			
			//String sql = "SELECT rowid as _id, item, item_description FROM tblEvent_extDes " +
			//		     " WHERE eguid = ? ORDER by rowid";
			
//			c = epgDB.rawQuery(sql,  new String[] {"196"});
//			
//			 if (c == null) {
//		            return null;
//		        } else if (!c.moveToFirst()) {
//		            c.close();
//		            return null;
//		        }
//			 
//			return c;
			break;
	   }
            
			
	   case QUERY_RATINGS_QUERY_EGUID:
	   {
		    qb.setTables(Table.RATINGS);
		   
			if (sortOrder == null || sortOrder == "") {
				sortOrder =  "rowid"; //ExtendedFTSColumns._ID;
			}
			
			long event_guid = ContentUris.parseId(uri);
			
			Log.d(TAG, "query ratings descriotors for eguid " + event_guid);
			
			selectionArgs = insertSelectionArg(selectionArgs, String.valueOf(event_guid));
						
			qb.appendWhere(Clause.QUERY_RATING_BY_EVENT_ID); 

			break;
	   }
	   case QUERY_EVENTS_SEARCH:
		 
		   if (selectionArgs == null) {
               throw new IllegalArgumentException(
                   "selectionArgs must be provided for the Uri: " + uri);
             }
		   
		   //add the wildcard * so that when searching "play" , "playboy" will be considered as a match
		   String keyWords = selectionArgs[0]+"*";
		    
		   Log.d(TAG, "fts search:" + keyWords);
		   
//		   //set table
//		   qb.setTables("tblEvent_basic JOIN tblEvent_shortDes  ON tblEvent_basic.rowid = tblEvent_shortDes._id ");
//		   qb.appendWhere("tblEvent_shortDes.event_name MATCH ? " );
//		  
//		   //rebuild the selectionArgs 
//		   selectionArgs = new String[] {keyWords + "*"};
//		   
//		   //override the projection
//		   projection = new String[] {  "_id",     //BaseColumns._ID. We need this for Adaptor to work
//				   						"event_name" 
//				                     };
//		   
//		   
//		   qb.setProjectionMap(searchColumnMap);
		   
		   //To Union queries, we have to use RawQuery.
           //TODO: the projection should be set by the use. Otherwise, program will crash when user try to
		   //get the column that has not been selected here.
		   
		   //The projections are limited to be the rows in Table.Basic
		   
		   //Check the comments of CONTENT_URI_EVENTS_SEARCH.
		   
		   
		   
		   String search_projections = join(projection, " , ");
		   
//		   String sql_search_event_name =  " SELECT " +  search_projections + 
//                                           " FROM  " + Table.BASIC_JOIN_SHORT_DES  +
//                                           " WHERE b.event_name MATCH ? " ;
//                                           //UNION ALL
//		   String sql_search_short_des =   " SELECT " +  search_projections + 
//                                           " FROM " + Table.BASIC_JOIN_SHORT_DES  +
//                                           " WHERE b.short_des MATCH ? ";
		   
		   String sql_search_event_name_and_short_des =  " SELECT " +  search_projections + 
		   							" FROM " + Table.BASIC_JOIN_SHORT_DES  +
		   							" WHERE tblEvent_shortdes MATCH ? ";
		   
		   
		   //UNION ALL
		   String sql_search_ext_des =  " SELECT " +  search_projections + 
		   " FROM tblEvent_basic a " +  
		   " WHERE a._id IN ( " + 
		   " Select a._id " + 
		   " FROM " + 
		   " tblEvent_basic a JOIN tblEvent_extdes c ON a._id = c.eguid " + 
		   " WHERE c.item MATCH ?)";
		   
		   
		   String sql_search_all = qb.buildUnionQuery(new String[] {
				   //sql_search_event_name, 
				   //sql_search_short_des,
				   sql_search_event_name_and_short_des,
				   sql_search_ext_des}, 
				                                      sortOrder, 
				                                      null);

		   Log.d(TAG, sql_search_all);
	   
		   return epgDB.rawQuery(sql_search_all, new String[] {keyWords ,keyWords });
		   
		   //break;
            
	   case QUERY_MOVIE:   //fall through
	   case QUERY_NEWS:    //fall through
	   case QUERY_SPORTS:  //fall through
	   case QUERY_MUSIC:   //fall through
	   case QUERY_EDUCATION:  
		   
		   qb.setTables(Table.BASIC);
		   String level = getLevelValueAccordingToStandards(query_type, "DVB");
		   Log.d(TAG, "trying to search type with value " + level);
		   selectionArgs = insertSelectionArg(selectionArgs, level);
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
		   
	    if (c == null) {
            return null;
        } else if (!c.moveToFirst()) {
            c.close();
            return null;
        }
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
   
   
   private String[] prePendProjectionArg(String[] projection, String arg) {
       if (projection == null) {
           return null;
       }
       final int length = projection.length;
       String[] newProjection = new String[length + 1];
       newProjection[0] = arg;
       System.arraycopy(projection, 0, newProjection, 1, length);
       
       return newProjection;
   }
   
   
   
   /**
    * 
    * It is equivalent to 
    * s.unshift("_id").map{|s| "a."+s}.join(sep)
    * 
    * a is the alias used for Table.BASIC in following sql statement
    * 
    * @param s
    * @param sep
    * @return
    */
   
   private String join(String[] s, String sep)
   {
     int k=s.length;
     if (k==0) {
       return null;
     }
     StringBuilder out=new StringBuilder();
     out.append("a._id , ");
     out.append("a."+s[0]);
     for (int x=1;x<k;++x){
       out.append(sep).append("a."+s[x]);
     }
     return out.toString();
   }

}


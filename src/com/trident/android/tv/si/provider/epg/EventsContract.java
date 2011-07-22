package com.trident.android.tv.si.provider.epg;

import android.provider.BaseColumns;



/**
 * 
 * This interface define all the fields that can be used in the Query's projection
 * 
 * @author Pierr
 *
 */


public final class EventsContract{
	
	
	/**
	 * 
	 * 
	 * The Contracts between the database and the User. Client code will use the members in Events as the 
	 * query projections.
	 *
	 */
	 
	
	public static class Events implements BaseColumns, BasicColumns , ContentColumns {
				
		//public static final String ITEM_DES = "item_description";
		//public static final String ITEM_CONTENT = "item";
		   		
	}
	
	protected interface BasicColumns{
		
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
	
	protected interface ContentColumns{
		public static final String LEVEL1 = "level1";
       	public static final String LEVEL2 = "level2";
	}
	
	
	public interface Gerne{
		public int EDUCATION = 0;
		public int MOVIE = 1;
		public int NEWS = 2;
		public int SPORTS = 4;
		public int MUSIC = 6;
	
	}
	
	
	
}
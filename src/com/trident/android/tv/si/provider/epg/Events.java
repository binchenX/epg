package com.trident.android.tv.si.provider.epg;



/**
 * 
 * This interface define all the fields that can be used in the Query's projection
 * 
 * @author Pierr
 *
 */


public interface Events{
	
	 
	/**
	 * 
	 * the unique id for each event ,used only for getting other attributes in other tables.
	 * 
	 */
	    public static final String ID = "_id";
	    
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
		
		public static final String ITEM_DES = "item_description";
		public static final String ITEM_CONTENT = "item";
		   
		public static final String LEVEL1 = "level1";
       	public static final String LEVEL2 = "level2";
		
	
	
	
}
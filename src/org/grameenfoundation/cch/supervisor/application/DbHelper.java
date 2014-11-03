package org.grameenfoundation.cch.supervisor.application;


import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.model.CCHTrackerLog;
import org.grameenfoundation.cch.supervisor.R;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;


public class DbHelper extends SQLiteOpenHelper {

	static final String TAG = DbHelper.class.getSimpleName();
	static final String DB_NAME = "cchsupervisor.db";
	static final int DB_VERSION = 15;

	private SQLiteDatabase db;
	private SharedPreferences prefs;
	private Context ctx;
	private SQLiteDatabase read;
	
	// CCH: Tracker table
	private static final String CCH_TRACKER_TABLE = "CCHTrackerLog";
	private static final String CCH_TRACKER_ID = BaseColumns._ID;
	private static final String CCH_TRACKER_USERID = "userid"; // reference to current user id
	private static final String CCH_TRACKER_MODULE = "module"; 
	private static final String CCH_TRACKER_START_DATETIME = "starttime";
	private static final String CCH_TRACKER_END_DATETIME = "endtime";
	private static final String CCH_TRACKER_DATA = "data";
	private static final String CCH_TRACKER_SUBMITTED = "submitted";
	private static final String CCH_TRACKER_INPROGRESS = "inprogress";
	
	// CCH: Login/User Table 
    private static final String CCH_USER_TABLE = "login";
	private static final String CCH_USER_ID = BaseColumns._ID;
	private static final String CCH_STAFF_ID = "staff_id";
	private static final String CCH_USER_PASSWORD = "password";
	private static final String CCH_USER_APIKEY = "apikey";
	private static final String CCH_USER_FIRSTNAME = "first_name";
	private static final String CCH_USER_LASTNAME = "last_name";
	private static final String CCH_USER_SUPERVISOR_INFO = "supervisor_info";
		
	// CCH: Events Table
	public static final String EVENTS_SET_TABLE="events_set";
	public static final String COL_EVENT_SET_NAME="event_name";
	public static final String COL_EVENT_PERIOD="event_period";
	public static final String COL_EVENT_NUMBER="event_number";
	public static final String COL_EVENT_MONTH="month";
	public static final String COL_SYNC_STATUS ="sync_status";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
		

	public DbHelper(Context ctx) { //
		super(ctx, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
		read=this.getReadableDatabase();
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createUserTable(db);
		createCCHTrackerTable(db);
		createEventsSetTable(db);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	
	// Login/User
	public void createUserTable(SQLiteDatabase db){
		String l_sql = "create table if not exists " + CCH_USER_TABLE + " (" + 
				CCH_USER_ID + " integer primary key autoincrement, " + 
				CCH_STAFF_ID + " text, " + 
				CCH_USER_PASSWORD + " text, " + 
				CCH_USER_APIKEY + " text default '', " + 
				CCH_USER_FIRSTNAME + " text default '', " + 
				CCH_USER_SUPERVISOR_INFO + " text default '', " + 
				CCH_USER_LASTNAME + " text default '')";
		db.execSQL(l_sql);
	}
	
	public void addUser(User u) 
	{	
		if (checkUserExists(u)==null)
		{
			SQLiteDatabase db = this.getWritableDatabase(); 
        	ContentValues values = new ContentValues();
        	values.put(CCH_STAFF_ID, u.getUsername()); // StaffId
        	values.put(CCH_USER_PASSWORD, u.getPassword()); // Password  
            values.put(CCH_USER_APIKEY, u.getApi_key());
            values.put(CCH_USER_FIRSTNAME, u.getFirstname());
            values.put(CCH_USER_LASTNAME, u.getLastname());
            values.put(CCH_USER_SUPERVISOR_INFO, u.getSupervisorInfo());
        	db.insert(CCH_USER_TABLE, null, values);
        	db.close(); // Closing database connection
		}
    }
	
	public void updateUser(User u) 
	{
        SQLiteDatabase db = this.getWritableDatabase(); 
        ContentValues values = new ContentValues();
        values.put(CCH_STAFF_ID, u.getUsername()); // StaffId
        values.put(CCH_USER_PASSWORD, u.getPassword()); // Password 
        values.put(CCH_USER_APIKEY, u.getApi_key());
        values.put(CCH_USER_FIRSTNAME, u.getFirstname());
        values.put(CCH_USER_LASTNAME, u.getLastname());
        values.put(CCH_USER_SUPERVISOR_INFO, u.getSupervisorInfo());
           
        // Inserting Row        
        db.update(CCH_USER_TABLE, values, CCH_STAFF_ID + "= '"+u.getUsername()+"'", null);
        db.close(); // Closing database connection        
    }
	
	public void updateUser(String staff_id, String apikey, String firstname, String lastname) 
	{
        SQLiteDatabase db = this.getWritableDatabase(); 
        ContentValues values = new ContentValues();
        values.put(CCH_USER_APIKEY, apikey);
        values.put(CCH_USER_FIRSTNAME, firstname);
        values.put(CCH_USER_LASTNAME, lastname);
    
        // Inserting Row        
        db.update(CCH_USER_TABLE, values, CCH_STAFF_ID + "=" + staff_id, null);
        db.close(); // Closing database connection        
    }
	
    // check if User exists
	public User checkUserExists(User u) 
	{			
			SQLiteDatabase db = this.getReadableDatabase();		 
		    Cursor cursor = db.query(CCH_USER_TABLE, new String[] { CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD, CCH_USER_APIKEY, CCH_USER_FIRSTNAME, CCH_USER_LASTNAME, CCH_USER_SUPERVISOR_INFO }, CCH_STAFF_ID + "=?",
		            new String[] { String.valueOf(u.getUsername()) }, null, null, null, null); 	        
		   if (cursor == null || cursor.getCount()==0)
			    return null;	    	
		        cursor.moveToFirst();
		    
		   u.setApi_key(cursor.getString(3));
		   u.setFirstname(cursor.getString(4));
		   u.setLastname(cursor.getString(5));
		   u.setSupervisorInfo(cursor.getString(6));
		   
		   if (!(u.getPassword().equals(cursor.getString(2)))) {
			   u.setPasswordRight(false);	   
		   } 
		   
		   return u;
   }
	
	public User getUser(String uid) 
	{			
			SQLiteDatabase db = this.getReadableDatabase();		 
		    Cursor cursor = db.query(CCH_USER_TABLE, new String[] { CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD, CCH_USER_APIKEY, CCH_USER_FIRSTNAME, CCH_USER_LASTNAME, CCH_USER_SUPERVISOR_INFO }, CCH_STAFF_ID + "=?",
		            new String[] { uid }, null, null, null, null); 	        
		   if (cursor == null || cursor.getCount()==0)
			    return null;	    	
		        cursor.moveToFirst();
		    
		   User u = new User();
		   u.setApi_key(cursor.getString(3));
		   u.setFirstname(cursor.getString(4));
		   u.setLastname(cursor.getString(5));
		   u.setSupervisorInfo(cursor.getString(6));
		   u.setPassword(cursor.getString(2));
		   u.setUsername(cursor.getString(1));
		  
		   return u;
   }
	
	
   // Tracker	
   public void createCCHTrackerTable(SQLiteDatabase db){
		String l_sql = "create table if not exists " + CCH_TRACKER_TABLE + " (" + 
				CCH_TRACKER_ID + " integer primary key autoincrement, " + 
				CCH_TRACKER_USERID + " text, " + 
				CCH_TRACKER_MODULE + " text, " + 
				CCH_TRACKER_START_DATETIME + " string , " + 
				CCH_TRACKER_END_DATETIME + " string , " + 
				CCH_TRACKER_DATA + " text, " + 
				CCH_TRACKER_SUBMITTED + " integer default 0, " + 
				CCH_TRACKER_INPROGRESS + " integer default 0)";
		db.execSQL(l_sql);
	}
   

	public void insertCCHLog(String module, String data, String starttime, String endtime){
		
		SQLiteDatabase db = this.getWritableDatabase(); 
		String userid = prefs.getString(ctx.getString(R.string.prefs_username), "noid"); 
		ContentValues values = new ContentValues();
		values.put(CCH_TRACKER_USERID, userid);
		values.put(CCH_TRACKER_MODULE, module);
		values.put(CCH_TRACKER_DATA, data);
		values.put(CCH_TRACKER_START_DATETIME, starttime);
		values.put(CCH_TRACKER_END_DATETIME, endtime);
		//Log.v("insertCCHLOG", values.toString());
		db.insertOrThrow(CCH_TRACKER_TABLE, null, values);
		db.close();
	}
	
	public Payload getCCHUnsentLog(){
		String s = CCH_TRACKER_SUBMITTED + "=? ";
		String[] args = new String[] { "0" };
		Cursor c = db.query(CCH_TRACKER_TABLE, null, s, args, null, null, null);
		c.moveToFirst();

		ArrayList<Object> sl = new ArrayList<Object>();
		while (c.isAfterLast() == false) {
			CCHTrackerLog so = new CCHTrackerLog();
			so.setId(c.getLong(c.getColumnIndex(CCH_TRACKER_ID)));
			
			String content ="" ;
			
			try {
				JSONObject json = new JSONObject();
				json.put("user_id", c.getString(c.getColumnIndex(CCH_TRACKER_USERID)));
				json.put("data", c.getString(c.getColumnIndex(CCH_TRACKER_DATA)));
				json.put("module", c.getString(c.getColumnIndex(CCH_TRACKER_MODULE)));
				json.put("start_time", c.getString(c.getColumnIndex(CCH_TRACKER_START_DATETIME)));
				json.put("end_time", c.getString(c.getColumnIndex(CCH_TRACKER_END_DATETIME)));
				content = json.toString();
			} catch (JSONException e) {
			    e.printStackTrace();
			}
			
			so.setContent(content);
			sl.add(so);
			c.moveToNext();
		}
		
		Payload p = new Payload(sl);
		c.close();
		
		return p;
	}
	
	public int markCCHLogSubmitted(long rowId){
		ContentValues values = new ContentValues();
		values.put(CCH_TRACKER_SUBMITTED, 1);
		return db.update(CCH_TRACKER_TABLE, values, CCH_TRACKER_ID + "=" + rowId, null);
	}

    // Events
	public void createEventsSetTable(SQLiteDatabase db)
	{
	//	table create statement for events set table 
		final String EVENT_SET_CREATE_TABLE =
			    "CREATE TABLE " + EVENTS_SET_TABLE + " (" +
			    		BaseColumns._ID + " INTEGER PRIMARY KEY," +
			    		COL_EVENT_SET_NAME + TEXT_TYPE + COMMA_SEP +
			    		COL_EVENT_PERIOD + TEXT_TYPE + COMMA_SEP +
			    		COL_SYNC_STATUS + TEXT_TYPE + COMMA_SEP +
			    		COL_EVENT_MONTH + TEXT_TYPE + COMMA_SEP +
			    		COL_EVENT_NUMBER+TEXT_TYPE+
			    " )";
		db.execSQL(EVENT_SET_CREATE_TABLE);
	}
		
	
    public boolean insertEventSet(String event_name, String event_period, String event_number, String month,String sync_status){
		ContentValues values = new ContentValues();
		values.put(COL_EVENT_SET_NAME,event_name);
		values.put(COL_EVENT_PERIOD,event_period);
		values.put(COL_SYNC_STATUS,sync_status);
		values.put(COL_EVENT_NUMBER,event_number);
		values.put(COL_EVENT_MONTH,month);
		
		 db.insert(EVENTS_SET_TABLE, null, values);
		return true;
	}
    	
	public boolean updateEventTarget(String sync_status, long id)
	{
		    
	        String updateQuery = "Update "+EVENTS_SET_TABLE+" set "+
	        					COL_SYNC_STATUS+" = '"+ sync_status +"'"+
	        					" where "+BaseColumns._ID+" = "+id;
	        db.execSQL(updateQuery);
		return true;
		
	}
	
    public ArrayList<String> getAllEventName(String month){
	      ArrayList<String> list=new ArrayList<String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_MONTH
             +" = '"+month+"'";
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.add(c.getString(c.getColumnIndex(COL_EVENT_SET_NAME)));
		c.moveToNext();						
	}
	c.close();
	return list;
}

public ArrayList<String> getAllEventNumber(String month){
	ArrayList<String> list=new ArrayList<String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_NUMBER
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_MONTH
             +" = '"+month+"'";
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.add(c.getString(c.getColumnIndex(COL_EVENT_NUMBER)));
		c.moveToNext();						
	}
	c.close();
	return list;
}


public ArrayList<String> getAllEventID(String month){
	ArrayList<String> list=new ArrayList<String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_MONTH
             +" = '"+month+"'";
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
	list.add(c.getString(c.getColumnIndex(BaseColumns._ID)));
		c.moveToNext();						
	}
	c.close();
	return list;
}


public HashMap<String,String> getAllDailyEvents(String month){
	HashMap<String,String> list=new HashMap<String,String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +","+COL_EVENT_NUMBER
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_PERIOD
             + " = '"+"Daily"+"'"
             +" and "+COL_EVENT_MONTH
             +" = '"+month+"'"
             +" and "+COL_SYNC_STATUS
             + " = '"+"new_record"+"'";	
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.put("event_id",c.getString(c.getColumnIndex(BaseColumns._ID)));
		list.put("event_name", c.getString(c.getColumnIndex(COL_EVENT_SET_NAME)));
		list.put("event_number",  c.getString(c.getColumnIndex(COL_EVENT_NUMBER)));
		c.moveToNext();	
		System.out.println(list);
	}
	c.close();
	return list;
}
public HashMap<String,String> getAllMonthlyEvents(String month){
	HashMap<String,String> list=new HashMap<String,String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +","+COL_EVENT_NUMBER
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_PERIOD
             + " = '"+"Monthly"+"'"
             +" and "+COL_EVENT_MONTH
             +" = '"+month+"'"
             +" and "+COL_SYNC_STATUS
             + " = '"+"new_record"+"'";	
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.put("event_id",c.getString(c.getColumnIndex(BaseColumns._ID)));
		list.put("event_name", c.getString(c.getColumnIndex(COL_EVENT_SET_NAME)));
		list.put("event_number",  c.getString(c.getColumnIndex(COL_EVENT_NUMBER)));
		c.moveToNext();	
		System.out.println(list);
	}
	c.close();
	return list;
}

public HashMap<String,String> getAllWeeklyEvents(String month){
	HashMap<String,String> list=new HashMap<String,String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +","+COL_EVENT_NUMBER
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_PERIOD
             + " = '"+"Weekly"+"'"
             +" and "+COL_EVENT_MONTH
             +" = '"+month+"'"
             +" and "+COL_SYNC_STATUS
             + " = '"+"new_record"+"'";	
	 
	System.out.println("Weekly events "+strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.put("event_id",c.getString(c.getColumnIndex(BaseColumns._ID)));
		list.put("event_name", c.getString(c.getColumnIndex(COL_EVENT_SET_NAME)));
		list.put("event_number",  c.getString(c.getColumnIndex(COL_EVENT_NUMBER)));
		c.moveToNext();	
		System.out.println("Weekly events"+list);
	}
	c.close();
	return list;
}
public HashMap<String,String> getAllYearlyEvents(String month){
	HashMap<String,String> list=new HashMap<String,String>();
	 String strQuery="select "+BaseColumns._ID
             +","+COL_EVENT_SET_NAME
             +","+COL_EVENT_NUMBER
             +" from "+EVENTS_SET_TABLE
             +" where "+COL_EVENT_PERIOD
             + " = '"+"Yearly"+"'"
             +" and "+COL_EVENT_MONTH
             +" = '"+month+"'"
             +" and "+COL_SYNC_STATUS
             + " = '"+"new_record"+"'";	
	 
	System.out.println(strQuery);
	Cursor c=read.rawQuery(strQuery, null);
	c.moveToFirst();
	while (c.isAfterLast()==false){
		list.put("event_id",c.getString(c.getColumnIndex(BaseColumns._ID)));
		list.put("event_name", c.getString(c.getColumnIndex(COL_EVENT_SET_NAME)));
		list.put("event_number",  c.getString(c.getColumnIndex(COL_EVENT_NUMBER)));
		c.moveToNext();						
	}
	c.close();
	return list;
}
	
	public boolean editEventCategory(String event_category,String event_number,String event_period, long id)
	{
	    
        String updateQuery = "Update "+EVENTS_SET_TABLE+" set "+
        					COL_EVENT_SET_NAME+" = '"+ event_category +"'"+
        					","+COL_EVENT_NUMBER+" = '"+event_number+"'"+
        					","+COL_EVENT_PERIOD+" = '"+event_period+"'"+
        					" where "+BaseColumns._ID+" = "+id;
        db.execSQL(updateQuery);
        return true;		
	}

	public boolean deleteEventCategory(long id)
	{
		String deleteQuery="Delete from "+EVENTS_SET_TABLE+" where "+
			 			BaseColumns._ID+" = "+ id;
		System.out.println(deleteQuery);
        	db.execSQL(deleteQuery);
        	return true;
	}
	
	
	public void onLogout(){
		
		// Store current users prefs.
		String userid = prefs.getString(ctx.getString(R.string.prefs_username),"" );
		
		if (! userid.equals(""))
		{
			String firstname = "";
			String lastname ="" ;
			
			if (! prefs.getString(ctx.getString(R.string.prefs_display_name),"").isEmpty()) {
				String displayname = prefs.getString(ctx.getString(R.string.prefs_display_name),"");
				String[] name = displayname.split(" ");
				if (name.length > 0) {
					firstname = name[0];
					lastname = name[1];
				} else {
					firstname = displayname;
				}
			}
				
			updateUser(userid, prefs.getString(ctx.getString(R.string.prefs_api_key),""), firstname, lastname);
		}
		
		
		// Reset preferences
		Editor editor = prefs.edit();
    	editor.putString(ctx.getString(R.string.prefs_username), "");
    	editor.putString(ctx.getString(R.string.prefs_api_key), "");
    	editor.putString(ctx.getString(R.string.prefs_display_name),"");
    	editor.commit();
	}
	
}

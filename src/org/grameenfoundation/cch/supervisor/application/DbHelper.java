package org.grameenfoundation.cch.supervisor.application;

import org.grameenfoundation.cch.supervisor.model.*;
import org.grameenfoundation.cch.supervisor.model.WebAppInterface.MyGroupItem;
import org.grameenfoundation.cch.supervisor.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	// CCH: Tracker table
	private static final String CCH_TRACKER_TABLE = "CCHTrackerLog";
	private static final String CCH_TRACKER_ID = BaseColumns._ID;
	private static final String CCH_TRACKER_USERID = "userid"; // reference to
																// current user
																// id
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
	private static final String CCH_USERNAME = "username";
	private static final String CCH_USER_FIRSTNAME = "first_name";
	private static final String CCH_USER_ROLE = "role";
	private static final String CCH_USER_LASTNAME = "last_name";
	private static final String CCH_USER_GENDER = "gender";
	private static final String CCH_USER_SUPERVISOR_INFO = "supervisor_info";

	private static final String CCH_FACILITY_TABLE = "cch_facility";
	private static final String CCH_ID = BaseColumns._ID;
	private static final String CCH_FACILITY_NAME = "name";
	private static final String CCH_NAME = "name";
	private static final String CCH_FACILITY_TYPE = "facility_type";
	private static final String CCH_DISTRICT = "district";
	private static final String CCH_SUB_DISTRICT = "sub_district";
	private static final String CCH_REGION = "region";

	private static final String CCH_NURSE_TABLE = "cch_nurse";
	private static final String CCH_FACILITY_ID = "facility_id";
	private static final String CCH_FACILITY = "fac";
	private static final String CCH_REGION_ID = "reg_id";
	private static final String CCH_DISTRICT_ID = "dist_id";

	private static final String CCH_TITLE = "title";
	private static final String CCH_IS_CHN = "is_chn";
	private static final String CCH_USER_GROUP = "user_group";
	private static final String CCH_PHONE_NUMBER = "phone_number";
	private static final String CCH_DEVICE_ID = "device_id";
	private static final String CCH_MODIFIED_BY = "modified_at";
	private static final String CCH_CREATED_AT = "created_at";

	private static final String CCH_MY_FACILITY = "my_facility";

	private static final String CCH_TARGET_TABLE = "cch_target";
	private static final String CCH_CATEGORY = "target_category";
	private static final String CCH_TARGET_TYPE = "target_type";
	private static final String CCH_TARGET_ID = "target_id";
	private static final String CCH_TARGET = "target_no";

	private static final String CCH_ACHIEVED = "achieved";
	private static final String CCH_JUSTIFICATION = "justification";

	private static final String CCH_COURSE_TABLE = "cch_course";
	private static final String CCH_NURSE_ID = "nurse_id";
	private static final String CCH_COMPLETED = "completed";
	private static final String CCH_ATTEMPTS = "name";
	private static final String CCH_SCORE = "score";
	private static final String CCH_TIME_TAKEN = "time_taken";
	private static final String CCH_LAST_ACCESSED = "last_accessed";
	private static final String CCH_PERCENTAGE_COMPLETED = "percentage_complete";

	private static final String CCH_TOPICS_TABLE = "topic";
	private static final String CCH_COURSE_ID = "course_id";
	private static final String CCH_ACTIVITIES = "activity";

	private static final String CCH_UPDATED_AT = "updated_at";
	private static final String CCH_STATUS = "status";
	private static final String CCH_EVENT_TABLE = "event";

	/**
	 * "title": "CWC Outreach at dorngwam", "location": "dorngwam", "type":
	 * "CWC Outreach", "start": "1428570041000", "end": "1428580841000",
	 * "eventid": "0"
	 * 
	 * @param ctx
	 */

	private static final String CCH_CALENDAR_TABLE = "cch_event";
	private static final String CCH_LOCATION = "cch_course";
	private static final String CCH_TYPE = "event_type";
	private static final String CCH_START = "start";
	private static final String CCH_END = "end";
	private static final String CCH_EVENTID = "event_id";

	// ICTC Stuff
	private static final String ICTC_FARMER = "ictc_farmer";
	private static final String FIRST_NAME = "fname";
	private static final String OTHER_NAMES = "lname";
	private static final String COMMUNITY = "community";
	private static final String DISTRICT = "district";
	private static final String REGION = "region";
	private static final String GENDER = "gender";
	private static final String EDUCATION = "edu";
	private static final String NICKNAME = "nickname";
	private static final String VILLAGE = "village";
	private static final String NO_OF_CHILD = "noc";
	private static final String AGE = "age";
	private static final String MARITAL_STATUS = "ms";
	private static final String NO_OF_DEPENDANT = "nod";
	private static final String FARMER_ID = "farmer_id";
	private static final String CLUSTER = "cluster";

	/**
	 * obj.put("lname",farmer.getLastName()); obj.put("age",farmer.getAge());
	 * obj.put("community",farmer.getCommunity());
	 * obj.put("district",farmer.getDistrict());
	 * obj.put("edu",farmer.getEducation());
	 * obj.put("gender",farmer.getGender());
	 * obj.put("nickname",farmer.getNickname());
	 * obj.put("village",farmer.getVillage());
	 * obj.put("region",farmer.getRegion());
	 * obj.put("noc",farmer.getNumberOfChildren());
	 * obj.put("ms",farmer.getMaritalStatus());
	 * obj.put("nod",farmer.getNumberOfDependants());
	 * 
	 * 
	 * obj.put("id",farmer.getFarmID()); obj.put("cluster",farmer.getCluster());
	 * 
	 * @param ctx
	 */
	public DbHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createUserTable(db);
		createCCHTrackerTable(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// Login/User
	public void createUserTable(SQLiteDatabase db) {

		String l_sql = "create table if not exists " + CCH_USER_TABLE + " ("
				+ CCH_USER_ID + " integer primary key autoincrement, "
				+ CCH_STAFF_ID + " text, " + CCH_USER_PASSWORD + " text, "
				+ CCH_USER_APIKEY + " text default '', " + CCH_USER_FIRSTNAME
				+ " text default '', " + CCH_USER_SUPERVISOR_INFO
				+ " blob default '', " + CCH_USER_ROLE + " text default '', "
				+ CCH_USER_LASTNAME + " text default '')";

		db.execSQL(l_sql);

//		l_sql = "create table if not exists " + ICTC_FARMER + " (" + CCH_ID
//				+ " integer primary key autoincrement, " + FIRST_NAME
//				+ " text, " + OTHER_NAMES + " text default '', " + COMMUNITY
//				+ " text default '', " + DISTRICT + " text default '', "
//				+ REGION + " text, " + GENDER + " text default '', "
//				+ EDUCATION + " text default '', " + NICKNAME
//				+ " text default '', " + VILLAGE + " text default '', "
//				+ NO_OF_CHILD + " text default '', " + NO_OF_DEPENDANT
//				+ " text default '', " + CLUSTER + " text, " + CCH_DISTRICT
//				+ " text, " + GENDER + " text default '', " + EDUCATION
//				+ " text default '', " + NICKNAME + " text default ''" + ")";
//		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_FACILITY_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, "
				+ CCH_FACILITY_NAME + " text, " + CCH_DISTRICT + " text, "
				+ CCH_SUB_DISTRICT + " text default '', " + CCH_REGION
				+ " text default '', " + CCH_FACILITY_TYPE
				+ " text default '')";
		db.execSQL(l_sql);
		l_sql = "create table if not exists " + CCH_FACILITY_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, "
				+ CCH_FACILITY_NAME + " text, " + CCH_DISTRICT + " text, "
				+ CCH_SUB_DISTRICT + " text default '', " + CCH_REGION
				+ " text default '', " + CCH_FACILITY_TYPE
				+ " text default '')";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_REGION + " (" + CCH_ID
				+ " integer primary key autoincrement, " + CCH_NAME
				+ " text not null unique " + ")";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_DISTRICT + " (" + CCH_ID
				+ " integer primary key autoincrement, " + CCH_NAME
				+ " text not null UNIQUE" + " )";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_SUB_DISTRICT + " ("
				+ CCH_ID + " integer primary key autoincrement, " + CCH_NAME
				+ " text not null unique )" + "";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_NURSE_TABLE + " (" + CCH_ID
				+ " integer primary key autoincrement, " + CCH_USER_FIRSTNAME
				+ " text, " + CCH_USERNAME + " text, " + CCH_USER_LASTNAME
				+ " text, " + CCH_USER_GENDER + " text default '', "
				+ CCH_USER_ROLE + " text default '', " + CCH_USER_GROUP
				+ " text default '', " + CCH_PHONE_NUMBER + " text default '',"
				+ CCH_TITLE + " text default ''," + CCH_IS_CHN
				+ " text default '' , " + CCH_DEVICE_ID + " text default '',"
				+ CCH_MODIFIED_BY + " text default ''," + CCH_CREATED_AT
				+ " text default ''," + CCH_UPDATED_AT + " text default '',"
				+ CCH_STATUS + " text default ''," + CCH_FACILITY_ID
				+ " text default ''," + CCH_MY_FACILITY + " text default '')";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_COURSE_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, " + CCH_TITLE
				+ " text, " + CCH_ATTEMPTS + " int default  0, " + CCH_SCORE
				+ " int default 0, " + CCH_TIME_TAKEN + " text default '', "
				+ CCH_NURSE_ID + " text default '', " + CCH_LAST_ACCESSED
				+ " text default ''," + CCH_FACILITY_ID + " int default  0, "
				+ CCH_DISTRICT_ID + " int default  0, " + CCH_FACILITY
				+ " text default  '', " + CCH_DISTRICT + " text default  '', "
				+ CCH_REGION_ID + " text default  ''" + ", "
				+ CCH_PERCENTAGE_COMPLETED + " int default '')";

		db.execSQL(l_sql);
		l_sql = "create table if not exists " + CCH_TOPICS_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, " + CCH_NAME
				+ " text, " + CCH_LAST_ACCESSED + " datetime  , "
				+ CCH_TIME_TAKEN + " int default 0, "
				+ CCH_PERCENTAGE_COMPLETED + " text default '')";

		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_TARGET_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, "
				+ CCH_CATEGORY + " text, " + CCH_TARGET_TYPE + " text, "
				+ CCH_ACHIEVED + " INT default 0 , " + CCH_TARGET
				+ " INT default 0, " + CCH_JUSTIFICATION + " text default '' ,"
				+ CCH_START + " text , " + CCH_END + " text ," + CCH_NURSE_ID
				+ " text ," + CCH_COMPLETED + " int ," + CCH_FACILITY_ID
				+ " text default ''," + CCH_TARGET_ID + " int default  0 )";
		db.execSQL(l_sql);

		l_sql = "create table if not exists " + CCH_CALENDAR_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, " + CCH_EVENTID
				+ " INT DEFAULT  0 , " + CCH_TITLE + " text DEFAULT  '' , "
				+ CCH_TYPE + " text, " + CCH_FACILITY_ID + " text, "
				+ CCH_NURSE_ID + " text, " + CCH_ACHIEVED + " INT default 0 , "
				+ CCH_START + " text , " + CCH_END + " text )";
		db.execSQL(l_sql);
	}

	public void addUser(User u) {
		if (checkUserExists(u) == null) {
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

	public void updateUser(User u) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CCH_STAFF_ID, u.getUsername()); // StaffId
		values.put(CCH_USER_PASSWORD, u.getPassword()); // Password
		values.put(CCH_USER_APIKEY, u.getApi_key());
		values.put(CCH_USER_FIRSTNAME, u.getFirstname());
		values.put(CCH_USER_LASTNAME, u.getLastname());
		values.put(CCH_USER_SUPERVISOR_INFO, u.getSupervisorInfo());

		// Inserting Row
		db.update(CCH_USER_TABLE, values,
				CCH_STAFF_ID + "= '" + u.getUsername() + "'", null);
		db.close(); // Closing database connection
	}

	// check if User exists
	public User checkUserExists(User u) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CCH_USER_TABLE,
				new String[] { CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD,
						CCH_USER_APIKEY, CCH_USER_FIRSTNAME, CCH_USER_LASTNAME,
						CCH_USER_SUPERVISOR_INFO }, CCH_STAFF_ID + "=?",
				new String[] { String.valueOf(u.getUsername()) }, null, null,
				null, null);
		if (cursor == null || cursor.getCount() == 0)
			return null;
		try {
			cursor.moveToFirst();

			u.setApi_key(cursor.getString(3));
			u.setFirstname(cursor.getString(4));
			u.setLastname(cursor.getString(5));
			u.setSupervisorInfo(cursor.getString(6));

			if (!(u.getPassword().equals(cursor.getString(2)))) {
				u.setPasswordRight(false);
			}

			return u;
		} catch (Exception e) {
			System.out.println("Exception e : " + e.getLocalizedMessage());
			return null;
		}

	}

	public User getUser(String uid) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(CCH_USER_TABLE,
				new String[] { CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD,
						CCH_USER_APIKEY, CCH_USER_FIRSTNAME, CCH_USER_LASTNAME,
						CCH_USER_SUPERVISOR_INFO }, CCH_STAFF_ID + "=?",
				new String[] { uid }, null, null, null, null);
		if (cursor == null || cursor.getCount() == 0)
			return null;
		try {
			cursor.moveToFirst();

			User u = new User();
			u.setApi_key(cursor.getString(3));
			u.setFirstname(cursor.getString(4));
			u.setLastname(cursor.getString(5));
			u.setSupervisorInfo(cursor.getString(6));
			u.setPassword(cursor.getString(2));
			u.setUsername(cursor.getString(1));

			return u;
		} catch (Exception e) {
			return null;
		}

	}

	// Tracker
	public void createCCHTrackerTable(SQLiteDatabase db) {
		String l_sql = "create table if not exists " + CCH_TRACKER_TABLE + " ("
				+ CCH_TRACKER_ID + " integer primary key autoincrement, "
				+ CCH_TRACKER_USERID + " text, " + CCH_TRACKER_MODULE
				+ " text, " + CCH_TRACKER_START_DATETIME + " string , "
				+ CCH_TRACKER_END_DATETIME + " string , " + CCH_TRACKER_DATA
				+ " text, " + CCH_TRACKER_SUBMITTED + " integer default 0, "
				+ CCH_TRACKER_INPROGRESS + " integer default 0)";
		db.execSQL(l_sql);
	}

	public void insertCCHLog(String module, String data, String starttime,
			String endtime) {

		SQLiteDatabase db = this.getWritableDatabase();
		String userid = prefs.getString(ctx.getString(R.string.prefs_username),
				"noid");
		ContentValues values = new ContentValues();
		values.put(CCH_TRACKER_USERID, userid);
		values.put(CCH_TRACKER_MODULE, module);
		values.put(CCH_TRACKER_DATA, data);
		values.put(CCH_TRACKER_START_DATETIME, starttime);
		values.put(CCH_TRACKER_END_DATETIME, endtime);
		// Log.v("insertCCHLOG", values.toString());
		db.insertOrThrow(CCH_TRACKER_TABLE, null, values);
		db.close();
	}

	public Payload getCCHUnsentLog() {
		String s = CCH_TRACKER_SUBMITTED + "=? ";
		String[] args = new String[] { "0" };
		Cursor c = db.query(CCH_TRACKER_TABLE, null, s, args, null, null, null);
		c.moveToFirst();

		ArrayList<Object> sl = new ArrayList<Object>();
		while (c.isAfterLast() == false) {
			CCHTrackerLog so = new CCHTrackerLog();
			so.setId(c.getLong(c.getColumnIndex(CCH_TRACKER_ID)));

			String content = "";

			try {
				JSONObject json = new JSONObject();
				json.put("user_id",
						c.getString(c.getColumnIndex(CCH_TRACKER_USERID)));
				json.put("data",
						c.getString(c.getColumnIndex(CCH_TRACKER_DATA)));
				json.put("module",
						c.getString(c.getColumnIndex(CCH_TRACKER_MODULE)));
				json.put("start_time", c.getString(c
						.getColumnIndex(CCH_TRACKER_START_DATETIME)));
				json.put("end_time",
						c.getString(c.getColumnIndex(CCH_TRACKER_END_DATETIME)));
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

	public int markCCHLogSubmitted(long rowId) {
		ContentValues values = new ContentValues();
		values.put(CCH_TRACKER_SUBMITTED, 1);
		return db.update(CCH_TRACKER_TABLE, values, CCH_TRACKER_ID + "="
				+ rowId, null);
	}

	// Events

	public void onLogout() {
		// Reset preferences
		Editor editor = prefs.edit();
		editor.putString(ctx.getString(R.string.prefs_username), "");
		editor.putString(ctx.getString(R.string.prefs_api_key), "");
		editor.putString(ctx.getString(R.string.prefs_display_name), "");

		editor.commit();
		resetData();
	}

	public void facilityAdd(int id, String name, String region,
			String facilityType, String district, String subDistrict) {

		ContentValues values = new ContentValues();
		values.put(CCH_ID, id); // StaffId
		values.put(CCH_FACILITY_NAME, name); // Password
		values.put(CCH_REGION, region);
		values.put(CCH_FACILITY_TYPE, facilityType);
		values.put(CCH_DISTRICT, district);
		values.put(CCH_SUB_DISTRICT, subDistrict);
		db.insert(CCH_FACILITY_TABLE, null, values);
		// db.close(); // Closing database connection

	}

	public void regionAdd(String name) {
		locationAdd(CCH_REGION, name);
	}

	public void districtAdd(int id, String name) {
		locationAdd(CCH_DISTRICT, id, name);
	}

	public void subDistrictAdd(int id, String name) {
		locationAdd(CCH_SUB_DISTRICT, id, name);
	}

	public void locationAdd(String locTable, int id, String name) {
		ContentValues values = new ContentValues();
		values.put(CCH_ID, id);
		values.put(CCH_NAME, name);
		db.insert(locTable, null, values);
		// db.close(); // Closing database connection
	}

	public void locationAdd(String locTable, String name) {
		ContentValues values = new ContentValues();

		values.put(CCH_NAME, name);
		db.insert(locTable, null, values);
		// db.close(); // Closing database connection
	}

	/**
	 * @param facilityId
	 * @param id
	 * @param username
	 * @param lastname
	 * @param othernames
	 * @param gender
	 * @param phone_number
	 * @param group
	 * @param role
	 * @param title
	 * @param isChn
	 * @param device
	 * @param status
	 * @param myfac
	 */

	public void nurseAdd(String facilityId, Long id, String username,
			String lastname, String othernames, String gender,
			String phone_number, String group, String role, String title,
			String isChn, String device, String status, String myfac) {
		ContentValues values = new ContentValues();
		values.put(CCH_ID, id);
		values.put(CCH_USERNAME, username);
		values.put(CCH_USER_LASTNAME, lastname);
		values.put(CCH_USER_FIRSTNAME, othernames);
		values.put(CCH_USER_GENDER, gender);
		values.put(CCH_USER_GROUP, group);
		values.put(CCH_PHONE_NUMBER, phone_number);
		values.put(CCH_USER_ROLE, role);
		values.put(CCH_TITLE, title);
		values.put(CCH_IS_CHN, isChn);
		values.put(CCH_STATUS, status);
		values.put(CCH_DEVICE_ID, device);
		values.put(CCH_FACILITY_ID, facilityId);
		values.put(CCH_MY_FACILITY, myfac);
		db.insert(CCH_NURSE_TABLE, null, values);
		// db.close(); // Closing database connection
	}

	public void targetAdd(String facilityId, String nurseId, String targetId,
			String category, int target, int achievved, String justification,
			String start, String end, String type) {
		ContentValues values = new ContentValues();

		int completed = 0;
		if (achievved >= target) {
			completed = 1;
		}
		values.put(CCH_NURSE_ID, nurseId);
		values.put(CCH_TARGET_ID, targetId);
		values.put(CCH_CATEGORY, category);
		values.put(CCH_TARGET, target);
		values.put(CCH_ACHIEVED, achievved);
		values.put(CCH_COMPLETED, completed);
		values.put(CCH_FACILITY_ID, facilityId);
		values.put(CCH_TARGET_TYPE, type);
		values.put(CCH_JUSTIFICATION, justification);
		values.put(CCH_START, getDateOnly(start));
		values.put(CCH_END, getDateOnly(end));
		db.insert(CCH_TARGET_TABLE, null, values);
		// db.close(); // Closing database connection
	}

	/**
	 * "title": "Health Talk at opd", "location": "opd", "type": "Health Talk",
	 * "start": "1430940616000", "end": "1430942416000", "eventid": "13"
	 * 
	 * @param nurseId
	 * @param eventId
	 * @param category
	 * @param target
	 * @param achievved
	 * @param justification
	 * @param start
	 * @param end
	 * @param name
	 */

	public void eventAdd(String nurseId, String facId, String eventId,
			String title, String type, long start, long end) {
		ContentValues values = new ContentValues();
		values.put(CCH_NURSE_ID, nurseId);
		values.put(CCH_EVENTID, eventId);
		values.put(CCH_TITLE, title);
		values.put(CCH_TYPE, type);
		values.put(CCH_FACILITY_ID, facId);
		values.put(CCH_START, longToDate(start));
		values.put(CCH_END, longToDate(end));
		db.insert(CCH_CALENDAR_TABLE, null, values);
		// db.close(); // Closing database connection
	}

	/**
	 * 
	 * @param nurseId
	 * @param facId
	 * @param eventId
	 * @param title
	 * @param type
	 * @param start
	 * @param end
	 */

	public void courseAdd(String title, Long nurseId, long facId,
			String attempts, String scores, String timeTaken,
			String lastAccess, long percentage) {

	}

	public String getDateOnly(String date) {

		SimpleDateFormat fromDate = new SimpleDateFormat("dd-MM-yyyy");

		SimpleDateFormat todateDate = new SimpleDateFormat("yyyy-MM-dd");

		try {
			return todateDate.format(fromDate.parse(date));
		} catch (Exception e) {
		}
		return "";
	}

	public String longToDate(long date) {
		try {
			SimpleDateFormat todateDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return todateDate.format(new Date(date));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";

	}

	public void courseAdd(String title, Long nurseId, long facId,
			String facName, int did, String distName, String region,
			String attempts, String scores, String timeTaken,
			String last_accessed, String status) {

		int attempt = 0;
		try {
			attempt = Integer.parseInt(attempts);
		} catch (Exception e) {

		}
		int score = 0;
		try {
			score = Integer.parseInt(scores);
		} catch (Exception e) {
		}

		ContentValues values = new ContentValues();
		values.put(CCH_NURSE_ID, nurseId);
		values.put(CCH_FACILITY_ID, facId);
		values.put(CCH_DISTRICT_ID, did);
		values.put(CCH_FACILITY, facName);
		values.put(CCH_DISTRICT, distName);
		values.put(CCH_REGION_ID, region);
		values.put(CCH_TITLE, title);
		values.put(CCH_ATTEMPTS, attempt);
		values.put(CCH_SCORE, score);
		values.put(CCH_TIME_TAKEN, timeTaken);
		values.put(CCH_LAST_ACCESSED, last_accessed);
		values.put(CCH_PERCENTAGE_COMPLETED, status);
		db.insert(CCH_COURSE_TABLE, null, values);
		// db.close(); // Closing database connection

	}

	public boolean resetData() {

		String[] tables = new String[] { CCH_FACILITY_TABLE,
				CCH_CALENDAR_TABLE, CCH_COURSE_TABLE, CCH_DISTRICT };
		for (String string : tables) {
			deleteTableContent(string);
		}
		return false;
	}

	public void deleteTableContent(String tableName) {
		try {
			this.getWritableDatabase().execSQL("delete from " + tableName);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Cursor findById(String table, String idFiled, String idValue,
			String xtraSearch) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			String str = " select * from " + table + "  where " + idFiled
					+ " = ?  " + xtraSearch;
			// System.out.println("Select Query : " + str);
			Cursor localCursor = db.query(table, null, idFiled + "=?",
					new String[] { idValue }, null, null, xtraSearch);
			System.out.println("Cursor : " + idFiled);
			// return localCursor;
		} catch (Exception localException) {
			System.out.println("Exception  findById : "
					+ localException.getLocalizedMessage());
		}
		return null;
	}

	public List<District> getDistrictData() {
		List<District> districts = new ArrayList<District>();

		Cursor districtCursor = findAll(CCH_DISTRICT, "order by " + CCH_NAME
				+ " asc ");
		while (districtCursor.moveToNext()) {
			District district = new District(
					districtCursor
							.getInt(districtCursor.getColumnIndex(CCH_ID)),
					districtCursor.getString(districtCursor
							.getColumnIndex(CCH_NAME)));
			Cursor cursor = findById(CCH_FACILITY_TABLE, CCH_DISTRICT,
					district.getName(), CCH_FACILITY_NAME + " asc ");

			List<Facility> facilities = new ArrayList<Facility>();
			while (cursor.moveToNext()) {
				Facility facility = new Facility(cursor.getInt(cursor
						.getColumnIndex(CCH_ID)), cursor.getString(cursor
						.getColumnIndex(CCH_FACILITY_NAME)),
						cursor.getString(cursor
								.getColumnIndex(CCH_FACILITY_TYPE)),
						cursor.getString(cursor.getColumnIndex(CCH_REGION)),
						cursor.getString(cursor
								.getColumnIndex(CCH_SUB_DISTRICT)));

				Cursor eventCursor = findById(CCH_CALENDAR_TABLE,
						CCH_FACILITY_ID, String.valueOf(facility.getId()),
						CCH_START + " asc ");
				List<Event> events = new ArrayList<Event>();
				while (eventCursor.moveToNext()) {
					Event event = new Event(eventCursor.getInt(eventCursor
							.getColumnIndex(CCH_ID)),
							eventCursor.getInt(eventCursor
									.getColumnIndex(CCH_EVENTID)),
							eventCursor.getString(eventCursor
									.getColumnIndex(CCH_NURSE_ID)),
							eventCursor.getString(eventCursor
									.getColumnIndex(CCH_TITLE)),
							eventCursor.getString(eventCursor
									.getColumnIndex(CCH_TYPE)),
							eventCursor.getString(eventCursor
									.getColumnIndex(CCH_START)),
							eventCursor.getString(eventCursor
									.getColumnIndex(CCH_END)));
					events.add(event);

				}
				Cursor courseCursor = findById(CCH_COURSE_TABLE,
						CCH_FACILITY_ID, String.valueOf(facility.getId()),
						CCH_TITLE + " asc ");

				List<Course> courses = new ArrayList<Course>();

				while (courseCursor.moveToNext()) {

					Course course = new Course(courseCursor.getInt(courseCursor
							.getColumnIndex(CCH_ID)),
							courseCursor.getString(courseCursor
									.getColumnIndex(CCH_TITLE)),
							courseCursor.getInt(courseCursor
									.getColumnIndex(CCH_NURSE_ID)),
							courseCursor.getString(courseCursor
									.getColumnIndex(CCH_LAST_ACCESSED)),
							courseCursor.getDouble(courseCursor
									.getColumnIndex(CCH_SCORE)),
							courseCursor.getInt(courseCursor
									.getColumnIndex(CCH_PERCENTAGE_COMPLETED)),
							courseCursor.getInt(courseCursor
									.getColumnIndex(CCH_PERCENTAGE_COMPLETED)));
					courses.add(course);

				}
				Cursor targetCursor = findById(CCH_TARGET_TABLE,
						CCH_FACILITY_ID, String.valueOf(facility.getId()),
						CCH_START + " asc ");
				List<Target> targets = new ArrayList<Target>();
				while (targetCursor.moveToNext()) {

					Target target = new Target(targetCursor.getInt(targetCursor
							.getColumnIndex(CCH_ID)),
							targetCursor.getInt(targetCursor
									.getColumnIndex(CCH_TARGET_ID)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_NURSE_ID)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_TARGET_TYPE)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_CATEGORY)),
							targetCursor.getInt(targetCursor
									.getColumnIndex(CCH_ACHIEVED)),
							targetCursor.getInt(targetCursor
									.getColumnIndex(CCH_TARGET)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_JUSTIFICATION)),
							targetCursor.getInt(targetCursor
									.getColumnIndex(CCH_COMPLETED)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_START)),
							targetCursor.getString(targetCursor
									.getColumnIndex(CCH_END)));
					targets.add(target);
				}
			}
		}

		return districts;

	}

	public Nurse getNurseDetails(int nurseId) {
		Nurse nurse = null;
		Cursor cursor = findById(CCH_NURSE_TABLE, CCH_ID,
				String.valueOf(nurseId), "");
		while (cursor.moveToNext()) {
			nurse = new Nurse(
					cursor.getInt(cursor.getColumnIndex(CCH_ID)),
					cursor.getString(cursor.getColumnIndex(CCH_USERNAME)),
					cursor.getString(cursor.getColumnIndex(CCH_USER_LASTNAME)),
					cursor.getString(cursor.getColumnIndex(CCH_USER_FIRSTNAME)),
					cursor.getString(cursor.getColumnIndex(CCH_USER_GENDER)),
					cursor.getString(cursor.getColumnIndex(CCH_USER_ROLE)),
					cursor.getString(cursor.getColumnIndex(CCH_USER_GROUP)),
					cursor.getString(cursor.getColumnIndex(CCH_STATUS)), cursor
							.getString(cursor.getColumnIndex(CCH_MY_FACILITY)));

			return getNurseDetails(nurse);
		}
		return nurse;
	}

	public Nurse getNurseDetails(Nurse nurse) {

		int nurseId = nurse.getNurseId();
		Cursor eventCursor = findById(CCH_CALENDAR_TABLE, CCH_NURSE_ID,
				String.valueOf(nurseId), CCH_START + " asc ");
		List<Event> events = new ArrayList<Event>();
		while (eventCursor.moveToNext()) {
			Event event = new Event(
					eventCursor.getInt(eventCursor.getColumnIndex(CCH_ID)),
					eventCursor.getInt(eventCursor.getColumnIndex(CCH_EVENTID)),
					eventCursor.getString(eventCursor
							.getColumnIndex(CCH_NURSE_ID)),
					eventCursor.getString(eventCursor.getColumnIndex(CCH_TITLE)),
					eventCursor.getString(eventCursor.getColumnIndex(CCH_TYPE)),
					eventCursor.getString(eventCursor.getColumnIndex(CCH_START)),
					eventCursor.getString(eventCursor.getColumnIndex(CCH_END)));
			events.add(event);

		}
		Cursor courseCursor = findById(CCH_COURSE_TABLE, CCH_NURSE_ID,
				String.valueOf(nurseId), CCH_TITLE + " asc ");

		List<Course> courses = new ArrayList<Course>();

		while (courseCursor.moveToNext()) {

			Course course = new Course(courseCursor.getInt(courseCursor
					.getColumnIndex(CCH_ID)),
					courseCursor.getString(courseCursor
							.getColumnIndex(CCH_TITLE)),
					courseCursor.getInt(courseCursor
							.getColumnIndex(CCH_NURSE_ID)),
					courseCursor.getString(courseCursor
							.getColumnIndex(CCH_LAST_ACCESSED)),
					courseCursor.getDouble(courseCursor
							.getColumnIndex(CCH_SCORE)),
					courseCursor.getInt(courseCursor
							.getColumnIndex(CCH_PERCENTAGE_COMPLETED)),
					courseCursor.getInt(courseCursor
							.getColumnIndex(CCH_PERCENTAGE_COMPLETED)));
			courses.add(course);

		}
		Cursor targetCursor = findById(CCH_TARGET_TABLE, CCH_NURSE_ID,
				String.valueOf(nurseId), CCH_START + " asc ");
		List<Target> targets = new ArrayList<Target>();
		while (targetCursor.moveToNext()) {

			Target target = new Target(
					targetCursor.getInt(targetCursor.getColumnIndex(CCH_ID)),
					targetCursor.getInt(targetCursor
							.getColumnIndex(CCH_TARGET_ID)),
					targetCursor.getString(targetCursor
							.getColumnIndex(CCH_NURSE_ID)),
					targetCursor.getString(targetCursor
							.getColumnIndex(CCH_TARGET_TYPE)),
					targetCursor.getString(targetCursor
							.getColumnIndex(CCH_CATEGORY)),
					targetCursor.getInt(targetCursor
							.getColumnIndex(CCH_ACHIEVED)),
					targetCursor.getInt(targetCursor.getColumnIndex(CCH_TARGET)),
					targetCursor.getString(targetCursor
							.getColumnIndex(CCH_JUSTIFICATION)),
					targetCursor.getInt(targetCursor
							.getColumnIndex(CCH_COMPLETED)),
					targetCursor.getString(targetCursor
							.getColumnIndex(CCH_START)),
					targetCursor.getString(targetCursor.getColumnIndex(CCH_END)));
			targets.add(target);
		}

		nurse.setCourses(courses);
		nurse.setEvents(events);
		nurse.setTargets(targets);

		return nurse;
	}

	public Cursor findAll(String table, String orderBy) {
		Cursor localCursor = this.getWritableDatabase().rawQuery(
				" select * from " + table + " " + orderBy, null);
		return localCursor;
	}

	public List<CourseSummary> courseSummary() {

		String q = "SELECT count(*) as cnt, facility_id, (select name from cch_facility where _id=facility_id) as name FROM cch_course c  group   by facility_id ";
		Cursor localCursor = this.getWritableDatabase().rawQuery(q, null);
		List<CourseSummary> summaries = new ArrayList<CourseSummary>();
		while (localCursor.moveToNext()) {
			CourseSummary summary = new CourseSummary(
					localCursor.getInt(localCursor.getColumnIndex("cnt")),
					completedCourse(localCursor.getInt(localCursor
							.getColumnIndex("facility_id"))),
					localCursor.getString(localCursor.getColumnIndex("name")));
			summaries.add(summary);
		}
		return summaries;
	}

	public int completedCourse(int facility) {
		String q = "select count(*) as cnt from cch_course ch where ch.percentage_complete>=100 and ch.facility_id="
				+ facility;
		Cursor localCursor = this.getWritableDatabase().rawQuery(q, null);
		while (localCursor.moveToNext()) {
			return localCursor.getInt(localCursor.getColumnIndex("cnt"));
		}
		return 0;
	}

	public int courseGraph(String course, String fieldToGroupBy,
			String activity, String fieldToSumOrCount, String fieldName,
			String fieldVal) {
		String query = " select " + fieldToGroupBy + " , " + activity + "("
				+ fieldToSumOrCount + ") as cnt  from " + CCH_COURSE_TABLE;
		query += " where " + fieldName + " = '" + fieldVal + "' ";
		query += " and " + CCH_TITLE + " = '" + course + "' ";
		// System.out.println("Query : " + query);
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		while (localCursor.moveToNext()) {
			return localCursor.getInt(localCursor.getColumnIndex("cnt"));
		}
		return 0;

	}

	public double courseGraphMean(String course, String fieldToGroupBy,
			String activity, String fieldToSumOrCount, String fieldName,
			String fieldVal) {
		String query = " select " + fieldToGroupBy + " , " + "(sum("
				+ fieldToSumOrCount + ")/count(" + fieldToSumOrCount
				+ "))  as cnt  from " + CCH_COURSE_TABLE;
		query += " where " + fieldName + " = '" + fieldVal + "' ";
		query += " and " + CCH_TITLE + " = '" + course + "' ";
		// System.out.println("Query : " + query);
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		while (localCursor.moveToNext()) {
			return localCursor.getDouble(localCursor.getColumnIndex("cnt"));
		}
		return 0.0;

	}

	public List<String> getCourseItem() {
		String query = " select " + CCH_TITLE + " from " + CCH_COURSE_TABLE
				+ " group by " + CCH_TITLE;
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		List<String> response = new ArrayList<String>();
		while (localCursor.moveToNext()) {
			response.add(localCursor.getString(localCursor
					.getColumnIndex(CCH_TITLE)));
		}
		return response;
	}
	
	public Farmer saveFarmer(String firstName, String lastName, String nickname, String community, String village, String district,
			String region, String age, String gender, String maritalStatus, String numberOfChildren, String numberOfDependants,
			String education, String cluster, String farmID){
		
		ContentValues values = new ContentValues();
		values.put(FIRST_NAME, firstName);
		values.put(OTHER_NAMES, lastName);
		values.put(NICKNAME, nickname);
		values.put(COMMUNITY, community);
		values.put(VILLAGE, village);
		values.put(DISTRICT, district);
		values.put(REGION, region);
		values.put(AGE, age);
		values.put(GENDER, gender);
		values.put(MARITAL_STATUS, maritalStatus);
		values.put(NO_OF_CHILD, numberOfChildren);
		values.put(NO_OF_DEPENDANT, numberOfDependants);
		values.put(MARITAL_STATUS, maritalStatus);
		values.put(CLUSTER, cluster);
		values.put(EDUCATION, education);
		values.put(FARMER_ID, farmID);
		
		db.insert(ICTC_FARMER, null, values);
		return new Farmer(firstName, lastName, nickname, community, village, district, region, age, gender, maritalStatus, numberOfChildren, numberOfDependants, education, cluster, farmID);
	}
	


	public List<Farmer> getFarmers() {
		String query = " select  * from " + ICTC_FARMER + "  ";
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		List<Farmer> response = new ArrayList<Farmer>();
		while (localCursor.moveToNext()) {
			response.add(new Farmer(
					localCursor.getString(localCursor.getColumnIndex(FIRST_NAME)),
					localCursor.getString(localCursor.getColumnIndex(OTHER_NAMES)),
					localCursor.getString(localCursor.getColumnIndex(NICKNAME)),
					localCursor.getString(localCursor.getColumnIndex(COMMUNITY)),
					localCursor.getString(localCursor.getColumnIndex(VILLAGE)),
					localCursor.getString(localCursor.getColumnIndex(DISTRICT)),
					localCursor.getString(localCursor.getColumnIndex(REGION)),
					localCursor.getString(localCursor.getColumnIndex(AGE)),
					localCursor.getString(localCursor.getColumnIndex(GENDER)),
					localCursor.getString(localCursor.getColumnIndex(MARITAL_STATUS)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_CHILD)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_DEPENDANT)),
					localCursor.getString(localCursor.getColumnIndex(EDUCATION)),
					localCursor.getString(localCursor.getColumnIndex(CLUSTER)),
					localCursor.getString(localCursor.getColumnIndex(CCH_ID))));
		}
		return response;
	}
	
	public List<Farmer> getSearchedFarmers(String searchBy,String searchValue) {
		String query = " select  * from " + ICTC_FARMER + "  where  "+searchBy+"= '"+searchValue+"' ";
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		List<Farmer> response = new ArrayList<Farmer>();
		while (localCursor.moveToNext()) {
			response.add(new Farmer(
					localCursor.getString(localCursor.getColumnIndex(FIRST_NAME)),
					localCursor.getString(localCursor.getColumnIndex(OTHER_NAMES)),
					localCursor.getString(localCursor.getColumnIndex(NICKNAME)),
					localCursor.getString(localCursor.getColumnIndex(COMMUNITY)),
					localCursor.getString(localCursor.getColumnIndex(VILLAGE)),
					localCursor.getString(localCursor.getColumnIndex(DISTRICT)),
					localCursor.getString(localCursor.getColumnIndex(REGION)),
					localCursor.getString(localCursor.getColumnIndex(AGE)),
					localCursor.getString(localCursor.getColumnIndex(GENDER)),
					localCursor.getString(localCursor.getColumnIndex(MARITAL_STATUS)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_CHILD)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_DEPENDANT)),
					localCursor.getString(localCursor.getColumnIndex(EDUCATION)),
					localCursor.getString(localCursor.getColumnIndex(CLUSTER)),
					localCursor.getString(localCursor.getColumnIndex(CCH_ID))));
		}
		return response;
	}

	public Farmer findFarmers(String id) {
		String query = " select  * from " + ICTC_FARMER + "  where  "+FARMER_ID+" = '"+id+"'";
		Cursor localCursor = this.getWritableDatabase().rawQuery(query, null);
		Farmer response = null ;
		while (localCursor.moveToNext()) {
			response = (new Farmer(
					localCursor.getString(localCursor.getColumnIndex(FIRST_NAME)),
					localCursor.getString(localCursor.getColumnIndex(OTHER_NAMES)),
					localCursor.getString(localCursor.getColumnIndex(NICKNAME)),
					localCursor.getString(localCursor.getColumnIndex(COMMUNITY)),
					localCursor.getString(localCursor.getColumnIndex(VILLAGE)),
					localCursor.getString(localCursor.getColumnIndex(DISTRICT)),
					localCursor.getString(localCursor.getColumnIndex(REGION)),
					localCursor.getString(localCursor.getColumnIndex(AGE)),
					localCursor.getString(localCursor.getColumnIndex(GENDER)),
					localCursor.getString(localCursor.getColumnIndex(MARITAL_STATUS)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_CHILD)),
					localCursor.getString(localCursor.getColumnIndex(NO_OF_DEPENDANT)),
					localCursor.getString(localCursor.getColumnIndex(EDUCATION)),
					localCursor.getString(localCursor.getColumnIndex(CLUSTER)),
					localCursor.getString(localCursor.getColumnIndex(CCH_ID))));
		}
		return response;
	}
	
	public String getGraphCourseDetail(String title, List<MyGroupItem> grpItem,
			String fieldToGroupBy, String activity, String fieldToSumOrCount,
			String where) {
		String keyValue = "";

		String courseStr = thContent(title);
		int cnt = 0;
		List<String> courses = getCourseItem();
		for (String string : courses) {
			cnt++;
			String splitted = splitDevice(string);
			courseStr += thContent(splitted);
			keyValue += "<div class='span3 colspan3'><strong>" + splitted
					+ "</strong>  :  " + string + "</div>";
			// if(cnt%2 ==0 )keyValue+="<br />";
		}
		courseStr = tableContent("tr", courseStr);
		for (MyGroupItem grp : grpItem) {
			String nm = tdContent(grp.name);
			for (String string : courses) {
				nm += tdContent(String.valueOf(courseGraph(string,
						fieldToGroupBy, activity, fieldToSumOrCount,
						grp.fieldName, grp.id)));
			}
			nm = tableContent("tr", nm);
			courseStr += nm;
		}

		// System.out.println("Couse Str" +courseStr);

		String ret = "<h3>Courses taken</h3>" + keyValue;

		ret += "<table class='datatable table-bordered table-striped dataTable' style='width:100%'>"
				+ courseStr + "</table>";
		return ret;
	}

	public String getGraphCourseDetailMean(String title,
			List<MyGroupItem> grpItem, String fieldToGroupBy, String activity,
			String fieldToSumOrCount, String where) {
		String keyValue = "";
		String courseStr = thContent(title);
		int cnt = 0;
		List<String> courses = getCourseItem();
		for (String string : courses) {
			String splitted = splitDevice(string);
			courseStr += thContent(splitted);

		}
		courseStr = tableContent("tr", courseStr);

		DecimalFormat df = new DecimalFormat("#.00");
		// courseStr= tableContent("tr", courseStr);
		for (MyGroupItem grp : grpItem) {
			String nm = tdContent(grp.name);
			for (String string : courses) {
				nm += tdContent(String.valueOf(df.format(courseGraphMean(
						string, fieldToGroupBy, activity, fieldToSumOrCount,
						grp.fieldName, grp.id) + 0)));
			}
			nm = tableContent("tr", nm);
			courseStr += nm;
		}

		// System.out.println("Couse Str" +courseStr);

		String ret = "<table class='datatable table-bordered table-striped  dataTable' style='width:100%'>"
				+ courseStr + "</table>";
		return ret;
	}

	public String tableContent(String tag, String content) {
		return "<" + tag + ">" + content + "</" + tag + ">";
	}

	public String tdContent(String content) {
		return tableContent("td", content);
	}

	public String thContent(String content) {
		return tableContent("th", content);
	}

	public String splitDevice(String val) {
		String[] str = val.split(" ");
		String shortCode = "";
		for (String string : str) {
			shortCode += string.charAt(0);
		}

		return shortCode;

	}

}

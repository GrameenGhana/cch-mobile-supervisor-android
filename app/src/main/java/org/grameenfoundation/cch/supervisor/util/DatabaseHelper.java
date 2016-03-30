package org.grameenfoundation.cch.supervisor.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.Course;
import org.grameenfoundation.cch.supervisor.model.District;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.model.Facility;
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.Region;
import org.grameenfoundation.cch.supervisor.model.Target;
import org.grameenfoundation.cch.supervisor.model.TrackerLog;
import org.grameenfoundation.cch.supervisor.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

	static final String TAG = DatabaseHelper.class.getSimpleName();
	static final String DB_NAME = "cch_supervisor.db";
	static final int DB_VERSION = 15;

    static final long INITIAL_TIME =  1325376000; // January 1, 2012
    static final long REFRESH_DATA_TIME =  1800;  // 30 minutes
    static final long REFRESH_DATA_TIME_ONE_DAY =  86400;  // 1 day

	private SQLiteDatabase db;
	private SharedPreferences prefs;
	private Context ctx;

    // Common fields
    public static final String CCH_ID = BaseColumns._ID;
    public static final String CCH_TITLE = "title";

	// Tracker table
	public static final String CCH_TRACKER_TABLE = "CCHTrackerLog";
	public static final String CCH_TRACKER_ID = BaseColumns._ID;
	public static final String CCH_TRACKER_USER_ID = "user_id";
	public static final String CCH_TRACKER_MODULE = "module";
	public static final String CCH_TRACKER_START_DATETIME = "start_time";
	public static final String CCH_TRACKER_END_DATETIME = "end_time";
	public static final String CCH_TRACKER_DATA = "data";
	public static final String CCH_TRACKER_SUBMITTED = "submitted";
	public static final String CCH_TRACKER_IN_PROGRESS = "in_progress";

	// Login/User Table
	public static final String CCH_USER_TABLE = "login";
	public static final String CCH_USER_ID = BaseColumns._ID;
	public static final String CCH_STAFF_ID = "staff_id";
	public static final String CCH_USER_PASSWORD = "password";
	public static final String CCH_USER_API_KEY = "api_key";
	public static final String CCH_USER_FIRST_NAME = "first_name";
	public static final String CCH_USER_ROLE = "role";
	public static final String CCH_USER_LAST_NAME = "last_name";

    // Update Tracking table
    public static final String CCH_UPDATE_TABLE = "data_update";
    public static final String CCH_UPDATE_LOC_LAST_UPDATE = "date_loc";
    public static final String CCH_UPDATE_NURSE_LAST_UPDATE = "date_nurse";
    public static final String CCH_UPDATE_COURSE_LAST_UPDATE = "date_course";
    public static final String CCH_UPDATE_EVENT_LAST_UPDATE = "date_event";
    public static final String CCH_UPDATE_TARGET_LAST_UPDATE = "date_target";

    // Courses table
    public static final String CCH_COURSE_TABLE = "cch_courses";
    public static final String CCH_COURSE_ID = "course_id";
    public static final String CCH_QUIZ_STATUS = "quiz_taken";
    public static final String CCH_ATTEMPTS = "attempts";
    public static final String CCH_SCORE = "score";
    public static final String CCH_TIME_TAKEN = "time_taken";
    public static final String CCH_LAST_ACCESSED = "last_accessed";
    public static final String CCH_PERCENTAGE_COMPLETED = "percentage_complete";
    public static final String CCH_KSA_STATUS = "ksa_status";

    // Events table
    public static final String CCH_EVENTS_TABLE = "cch_events";
    public static final String CCH_EVENT_ID = "id";
    public static final String CCH_EVENT_TITLE = "title";
    public static final String CCH_EVENT_LOCATION = "location";
    public static final String CCH_EVENT_TYPE = "type";
    public static final String CCH_EVENT_START = "start";
    public static final String CCH_EVENT_END = "end";
    public static final String CCH_EVENT_JUSTIFICATION = "justification";
    public static final String CCH_EVENT_COMMENTS = "comments";
    public static final String CCH_EVENT_STATUS = "status";

    // Targets table
    public static final String CCH_TARGET_TABLE = "cch_target";
    public static final String CCH_TARGET_ID = "tid";
    public static final String CCH_TARGET = "target";
    public static final String CCH_TARGET_TYPE = "type";
    public static final String CCH_TARGET_CATEGORY = "category";
    public static final String CCH_TARGET_ACHIEVED = "achieved";
    public static final String CCH_TARGET_STATUS = "status";
    public static final String CCH_TARGET_JUSTIFICATION = "justification";
    public static final String CCH_TARGET_DUE_DATE = "due_date";
    public static final String CCH_TARGET_START_DATE = "start_date";

    // Nurses table
    public static final String CCH_NURSE_TABLE = "cch_nurse";
    public static final String CCH_NURSE_ID = "nurse_id";
    public static final String CCH_NURSE_NAME = "name";
    public static final String CCH_NURSE_ROLE = "role";
    public static final String CCH_NURSE_IS_CHN = "is_chn";
    public static final String CCH_NURSE_GROUP = "user_group";
    public static final String CCH_NURSE_STATUS = "status";
    public static final String CCH_NURSE_GENDER = "gender";
    public static final String CCH_NURSE_PHONE_NUMBER = "phone_number";
    public static final String CCH_NURSE_FACILITY = "primary_facility";

    // Facility Table
    public static final String CCH_FACILITY_TABLE = "cch_facility";
    public static final String CCH_FACILITY_ID = "facility_id";
    public static final String CCH_FACILITY_NAME = "facility";
    public static final String CCH_FACILITY_TYPE = "facility_type";

    // District Table
    public static final String CCH_DISTRICT_TABLE = "cch_district";
    public static final String CCH_DISTRICT_ID = "dist_id";
    public static final String CCH_DISTRICT_NAME = "district";

    // Region Table
    public static final String CCH_REGION_TABLE = "cch_region";
    public static final String CCH_REGION_ID = "reg_id";
    public static final String CCH_REGION_NAME = "reg_name";


	public DatabaseHelper(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void onLogout() {
        // Reset data
        updateLastUpdateTime(CCH_UPDATE_LOC_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        updateLastUpdateTime(CCH_UPDATE_NURSE_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        updateLastUpdateTime(CCH_UPDATE_COURSE_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        updateLastUpdateTime(CCH_UPDATE_EVENT_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        updateLastUpdateTime(CCH_UPDATE_TARGET_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        resetData("");

        // Reset preferences
        Editor editor = prefs.edit();
        editor.putString(ctx.getString(R.string.prefs_username), "");
        editor.putString(ctx.getString(R.string.prefs_api_key), "");
        editor.putString(ctx.getString(R.string.prefs_display_name), "");
        editor.apply();
    }

    public void createTables(SQLiteDatabase db) {
        // Tracker table
        String l_sql = "create table if not exists " + CCH_TRACKER_TABLE + " ("
                + CCH_TRACKER_ID + " integer primary key autoincrement, "
                + CCH_TRACKER_USER_ID + " text, " + CCH_TRACKER_MODULE
                + " text, " + CCH_TRACKER_START_DATETIME + " string , "
                + CCH_TRACKER_END_DATETIME + " string , " + CCH_TRACKER_DATA
                + " text, " + CCH_TRACKER_SUBMITTED + " integer default 0, "
                + CCH_TRACKER_IN_PROGRESS + " integer default 0)";
        db.execSQL(l_sql);

        // Update tracking table
        l_sql = "create table if not exists " + CCH_UPDATE_TABLE + " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_UPDATE_LOC_LAST_UPDATE + " string default '00000', "
                + CCH_UPDATE_NURSE_LAST_UPDATE + " string default '00000', "
                + CCH_UPDATE_COURSE_LAST_UPDATE + " string default '00000', "
                + CCH_UPDATE_EVENT_LAST_UPDATE + " string default '00000', "
                + CCH_UPDATE_TARGET_LAST_UPDATE + " string default '00000')";
        db.execSQL(l_sql);

        // User table
		l_sql = "create table if not exists " + CCH_USER_TABLE + " ("
				+ CCH_USER_ID + " integer primary key autoincrement, "
				+ CCH_STAFF_ID + " text, "
                + CCH_USER_PASSWORD + " text, "
				+ CCH_USER_API_KEY + " text default '', "
                + CCH_USER_FIRST_NAME + " text default '', "
                + CCH_USER_LAST_NAME + " text default '', "
                + CCH_USER_ROLE + " text default '') ";
		db.execSQL(l_sql);

        // Course table
        l_sql = "create table if not exists " + CCH_COURSE_TABLE + " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_NURSE_ID + " text, "
                + CCH_COURSE_ID + " int, "
                + CCH_TITLE + " text, "
                + CCH_KSA_STATUS + " text default '',"
                + CCH_QUIZ_STATUS + " int default 0,"
                + CCH_SCORE  + " int default 0, "
                + CCH_PERCENTAGE_COMPLETED + " int default '', "
                + CCH_ATTEMPTS + " int default  0, "
                + CCH_TIME_TAKEN + " text default '', "
                + CCH_LAST_ACCESSED + " text default '', "
                + CCH_FACILITY_ID + " int default  0, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_FACILITY_NAME + " text default  '', "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
        db.execSQL(l_sql);

        // Events table
        l_sql = "create table if not exists " + CCH_EVENTS_TABLE + " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_NURSE_ID + " text, "
                + CCH_NURSE_NAME + " text, "
                + CCH_EVENT_ID + " int default  0, "
                + CCH_EVENT_TITLE + " text default  '' , "
                + CCH_EVENT_LOCATION + " text default  '' , "
                + CCH_EVENT_TYPE + " text default '', "
                + CCH_EVENT_START + " text default '', "
                + CCH_EVENT_END + " text default '', "
                + CCH_EVENT_JUSTIFICATION + " text default '', "
                + CCH_EVENT_COMMENTS + " text default '', "
                + CCH_EVENT_STATUS + " text default '', "
                + CCH_FACILITY_ID + " int default  0, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_FACILITY_NAME + " text default  '', "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
        db.execSQL(l_sql);

        // Target table
        l_sql = "create table if not exists " + CCH_TARGET_TABLE + " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_NURSE_ID + " text, "
                + CCH_TARGET_ID + " int default  0, "
                + CCH_TARGET + " int default 0, "
                + CCH_TARGET_TYPE + " text, "
                + CCH_TARGET_CATEGORY + " text, " 
                + CCH_TARGET_ACHIEVED + " INT default 0 , "
                + CCH_TARGET_STATUS + " text default 0 , "
                + CCH_TARGET_JUSTIFICATION + " text default '' ,"
                + CCH_TARGET_START_DATE + " text , " 
                + CCH_TARGET_DUE_DATE + " text ," 
                + CCH_FACILITY_ID + " int default  0, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_FACILITY_NAME + " text default  '', "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
        db.execSQL(l_sql);

        // Nurse table
        l_sql = "create table if not exists " + CCH_NURSE_TABLE + " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_NURSE_ID + " text, "
                + CCH_NURSE_NAME + " text, "
                + CCH_TITLE + " text, "
                + CCH_NURSE_ROLE + " text, "
                + CCH_NURSE_IS_CHN + " int default 0, "
                + CCH_NURSE_GROUP + " text default '', "
                + CCH_NURSE_STATUS + " text default '', "
                + CCH_NURSE_GENDER + " text default '', "
                + CCH_NURSE_PHONE_NUMBER + " text default '',"
                + CCH_NURSE_FACILITY + " text default '',"
                + CCH_FACILITY_ID + " int default  0, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_FACILITY_NAME + " text default  '', "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
        db.execSQL(l_sql);

        // Facility table
		l_sql = "create table if not exists " + CCH_FACILITY_TABLE + " ("
				+ CCH_ID + " integer primary key autoincrement, "
                + CCH_FACILITY_ID + " int default  0, "
                + CCH_FACILITY_NAME + " text, "
                + CCH_FACILITY_TYPE + " text, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
				
		db.execSQL(l_sql);

        // District table
        l_sql = "create table if not exists " + CCH_DISTRICT_TABLE + " (" 
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_DISTRICT_ID + " int default  0, "
                + CCH_DISTRICT_NAME + " text default  '', "
                + CCH_REGION_NAME + " text default  '')";
        db.execSQL(l_sql);

        // Region table
        l_sql = "create table if not exists " + CCH_REGION_TABLE+ " ("
                + CCH_ID + " integer primary key autoincrement, "
                + CCH_REGION_ID + " int default  0, "
                + CCH_REGION_NAME + " text not null unique " + ")";
        db.execSQL(l_sql);

        // Insert default info into update table
        ContentValues values = new ContentValues();
        values.put(CCH_ID, 1);
        values.put(CCH_UPDATE_LOC_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        values.put(CCH_UPDATE_NURSE_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        values.put(CCH_UPDATE_COURSE_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        values.put(CCH_UPDATE_EVENT_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        values.put(CCH_UPDATE_TARGET_LAST_UPDATE, String.valueOf(INITIAL_TIME));
        db.insertOrThrow(CCH_UPDATE_TABLE, null, values);
	}

    public boolean resetData(String table) {

        String[] tables;
        if (table.equalsIgnoreCase("")) {
            tables =new String[]{CCH_REGION_TABLE, CCH_DISTRICT_TABLE, CCH_FACILITY_TABLE,
                    CCH_NURSE_TABLE, CCH_EVENTS_TABLE, CCH_TARGET_TABLE, CCH_COURSE_TABLE};
        } else {
            tables = new String[] { table };
        }

        for (String string : tables) {
            deleteTableContent(string);
        }

        return true;
    }


    // Update table methods
    private void updateLastUpdateTime(String field, String time) {
        ContentValues values = new ContentValues();
        values.put(field, time);
        db.update(CCH_UPDATE_TABLE, values, CCH_ID + "= 1", null);
    }

    private String getLastUpdateTime(String field) {
        String lastUpdate = "0";
        Cursor cursor = findById(CCH_UPDATE_TABLE, CCH_ID, "1", "");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                lastUpdate = cursor.getString(cursor.getColumnIndex(field));
            }
            cursor.close();
        }

        return lastUpdate;
    }

    private boolean isUpdateNeeded(String field, long timeLimit) {
        long lastUpdate = Long.parseLong(getLastUpdateTime(field));
        return ( ((System.currentTimeMillis()/1000) - lastUpdate)  > timeLimit);
    }

    public String getLastLocationUpdateTime() { return getLastUpdateTime(CCH_UPDATE_LOC_LAST_UPDATE); }
    public String getLastNurseUpdateTime() { return getLastUpdateTime(CCH_UPDATE_NURSE_LAST_UPDATE); }
    public String getLastCourseUpdateTime() { return getLastUpdateTime(CCH_UPDATE_COURSE_LAST_UPDATE); }
    public String getLastEventUpdateTime() { return getLastUpdateTime(CCH_UPDATE_EVENT_LAST_UPDATE); }
    public String getLastTargetUpdateTime() { return getLastUpdateTime(CCH_UPDATE_TARGET_LAST_UPDATE); }

    public void updateLocationUpdateTime() {
        updateLastUpdateTime(CCH_UPDATE_LOC_LAST_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
    }
    public void updateNurseUpdateTime() {
        updateLastUpdateTime(CCH_UPDATE_NURSE_LAST_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
    }
    public void updateCourseUpdateTime() {
        updateLastUpdateTime(CCH_UPDATE_COURSE_LAST_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
    }
    public void updateEventUpdateTime() {
        updateLastUpdateTime(CCH_UPDATE_EVENT_LAST_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
    }
    public void updateTargetUpdateTime() {
        updateLastUpdateTime(CCH_UPDATE_TARGET_LAST_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
    }

    public boolean isLocationUpdateNeeded() { return isUpdateNeeded(CCH_UPDATE_LOC_LAST_UPDATE, REFRESH_DATA_TIME_ONE_DAY); }
    public boolean isNurseUpdateNeeded() { return isUpdateNeeded(CCH_UPDATE_NURSE_LAST_UPDATE, REFRESH_DATA_TIME); }
    public boolean isCourseUpdateNeeded() { return isUpdateNeeded(CCH_UPDATE_COURSE_LAST_UPDATE, REFRESH_DATA_TIME); }
    public boolean isEventUpdateNeeded() { return isUpdateNeeded(CCH_UPDATE_EVENT_LAST_UPDATE, REFRESH_DATA_TIME_ONE_DAY); }
    public boolean isTargetUpdateNeeded() { return isUpdateNeeded(CCH_UPDATE_TARGET_LAST_UPDATE, REFRESH_DATA_TIME_ONE_DAY); }

    public boolean needDataUpdate() {
        return (isLocationUpdateNeeded() || isNurseUpdateNeeded() || isCourseUpdateNeeded() || isEventUpdateNeeded() || isTargetUpdateNeeded());
    }


    // Logging methods
    public void insertLog(String data, String startTime, String endTime) {
         insertLog("Supervisor", data, startTime, endTime);
    }
    public void insertLog(String module, String data, String startTime, String endTime) {
        String userId = prefs.getString(ctx.getString(R.string.prefs_username), "noId");
        ContentValues values = new ContentValues();
        values.put(CCH_TRACKER_USER_ID, userId);
        values.put(CCH_TRACKER_MODULE, module);
        values.put(CCH_TRACKER_DATA, data);
        values.put(CCH_TRACKER_START_DATETIME, startTime);
        values.put(CCH_TRACKER_END_DATETIME, endTime);
        db.insertOrThrow(CCH_TRACKER_TABLE, null, values);
    }

    public Payload getUnsentLogs() {
        String s = CCH_TRACKER_SUBMITTED + "=? ";
        String[] args = new String[] { "0" };
        Cursor c = db.query(CCH_TRACKER_TABLE, null, s, args, null, null, null);
        c.moveToFirst();

        ArrayList<Object> sl = new ArrayList<>();
        while (!c.isAfterLast()) {
            TrackerLog so = new TrackerLog();
            so.setId(c.getLong(c.getColumnIndex(CCH_TRACKER_ID)));

            String content = "";

            try {
                JSONObject json = new JSONObject();
                json.put("user_id", c.getString(c.getColumnIndex(CCH_TRACKER_USER_ID)));
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

    public int markLogsSubmitted(long rowId) {
        ContentValues values = new ContentValues();
        values.put(CCH_TRACKER_SUBMITTED, 1);
        return db.update(CCH_TRACKER_TABLE, values, CCH_TRACKER_ID + "=" + rowId, null);
    }


    // User methods
	public void addUser(User u) {
		if (checkUserExists(u) == null) {
			ContentValues values = new ContentValues();
			values.put(CCH_STAFF_ID, u.getUsername());
			values.put(CCH_USER_PASSWORD, u.getPassword());
			values.put(CCH_USER_API_KEY, u.getApi_key());
			values.put(CCH_USER_FIRST_NAME, u.getFirstName());
			values.put(CCH_USER_LAST_NAME, u.getLastName());
			db.insert(CCH_USER_TABLE, null, values);
		}
	}

    public User getUser(String uid) {
        Cursor cursor = db.query(CCH_USER_TABLE,
                new String[]{CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD,
                        CCH_USER_API_KEY, CCH_USER_FIRST_NAME, CCH_USER_LAST_NAME
                }, CCH_STAFF_ID + "=?",
                new String[]{uid}, null, null, null, null);
        if (cursor == null || cursor.getCount() == 0)
            return null;
        try {
            cursor.moveToFirst();

            User u = new User();
            u.setApi_key(cursor.getString(3));
            u.setFirstName(cursor.getString(4));
            u.setLastName(cursor.getString(5));
            u.setPassword(cursor.getString(2));
            u.setUsername(cursor.getString(1));

            return u;
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
        }
    }

	public void updateUser(User u) {
		ContentValues values = new ContentValues();
        values.put(CCH_STAFF_ID, u.getUsername());
		values.put(CCH_USER_PASSWORD, u.getPassword());
		values.put(CCH_USER_API_KEY, u.getApi_key());
		values.put(CCH_USER_FIRST_NAME, u.getFirstName());
		values.put(CCH_USER_LAST_NAME, u.getLastName());
        values.put(CCH_USER_ROLE, u.getRole());
		db.update(CCH_USER_TABLE, values, CCH_STAFF_ID + "= '" + u.getUsername() + "'", null);
	}

	public User checkUserExists(User u) {
		Cursor cursor = db.query(CCH_USER_TABLE,
				new String[] { CCH_USER_ID, CCH_STAFF_ID, CCH_USER_PASSWORD,
                        CCH_USER_API_KEY, CCH_USER_FIRST_NAME, CCH_USER_LAST_NAME}, CCH_STAFF_ID + "=?",
				new String[] { String.valueOf(u.getUsername()) }, null, null,
				null, null);
		if (cursor == null || cursor.getCount() == 0)
			return null;
		try {
			cursor.moveToFirst();

			u.setApi_key(cursor.getString(3));
			u.setFirstName(cursor.getString(4));
			u.setLastName(cursor.getString(5));

            if (!(u.getPassword().equals(cursor.getString(2)))) {
				u.setPasswordRight(false);
			}

			return u;
		} catch (Exception e) {
			Log.d(TAG, "Exception e : " + e.getLocalizedMessage());
			return null;
		} finally {
            cursor.close();
        }
	}

    
    // Nurse methods
    public void addNurse(String id, String name, String title, String role, int is_chn, String group,
                         String status, String gender, String phone_number, String facility,
                         String region, String distName, String facName, int did, long facId) {

        ContentValues values = new ContentValues();
        values.put(CCH_NURSE_ID, id);
        values.put(CCH_NURSE_NAME, name);
        values.put(CCH_TITLE, title);
        values.put(CCH_NURSE_ROLE, role);
        values.put(CCH_NURSE_IS_CHN, is_chn);
        values.put(CCH_NURSE_GROUP, group);
        values.put(CCH_NURSE_STATUS, status);
        values.put(CCH_NURSE_GENDER, gender);
        values.put(CCH_NURSE_PHONE_NUMBER, phone_number);
        values.put(CCH_NURSE_FACILITY, facility);
        values.put(CCH_FACILITY_ID, facId);
        values.put(CCH_DISTRICT_ID, did);
        values.put(CCH_FACILITY_NAME, facName);
        values.put(CCH_DISTRICT_NAME, distName);
        values.put(CCH_REGION_NAME, region);

        db.insert(CCH_NURSE_TABLE, null, values);
    }


    public void updateNurse(Nurse n) {
        ContentValues values = new ContentValues();
        values.put(CCH_NURSE_ID, n.id);
        values.put(CCH_NURSE_NAME, n.name);
        values.put(CCH_TITLE, n.title);
        values.put(CCH_NURSE_ROLE, n.role);
        values.put(CCH_NURSE_IS_CHN, n.ischn);
        values.put(CCH_NURSE_GROUP, n.group);
        values.put(CCH_NURSE_STATUS, n.status);
        values.put(CCH_NURSE_GENDER, n.gender);
        values.put(CCH_NURSE_PHONE_NUMBER, n.phoneNumber);
        values.put(CCH_NURSE_FACILITY, n.primaryFacility);
        values.put(CCH_FACILITY_ID, n.facilityId);
        values.put(CCH_DISTRICT_ID, n.districtId);
        values.put(CCH_FACILITY_NAME, n.facility);
        values.put(CCH_DISTRICT_NAME, n.district);
        values.put(CCH_REGION_NAME, n.region);
        db.update(CCH_NURSE_TABLE, values, CCH_ID + "= '" + n.id + "'", null);
    }

    public void removeNurse(int id) {
        deleteTableRowByField(CCH_NURSE_TABLE, CCH_NURSE_ID, String.valueOf(id));
    }

    public Nurse getNurse(String nurseId) {
        Nurse nurse = null;

        Cursor nurseCursor = findById(CCH_NURSE_TABLE, CCH_NURSE_ID, nurseId, "");

        if (nurseCursor != null) {
            while (nurseCursor.moveToNext()) {
                nurse = buildNurse(nurseCursor);
            }
            nurseCursor.close();
        }

        return nurse;
    }

    public ArrayList<Nurse> getNurseByField(String field, String value) {
        Cursor nurseCursor = findById(CCH_NURSE_TABLE, field, value, "ORDER BY " + CCH_NURSE_NAME);

        ArrayList<Nurse> nurses = new ArrayList<>();

        if (nurseCursor != null) {
            while (nurseCursor.moveToNext()) {
                nurses.add(buildNurse(nurseCursor));
            }
            nurseCursor.close();
        }

        return nurses;
    }


    // Event methods
    public void addEvent(String nurseId, String nurse, Long eventId, String title, String location, String type,
                         Long start, Long end, String justification, String comments, String status,
                         String region, String distName, String facName, int did, long facId) {

        ContentValues values = new ContentValues();
        values.put(CCH_NURSE_ID, nurseId);
        values.put(CCH_NURSE_NAME, nurse);
        values.put(CCH_EVENT_ID, eventId);
        values.put(CCH_EVENT_TITLE, title);
        values.put(CCH_EVENT_TYPE, type);
        values.put(CCH_EVENT_LOCATION, location);
        values.put(CCH_EVENT_START, start * 1000);
        values.put(CCH_EVENT_END, end * 1000);
        values.put(CCH_EVENT_JUSTIFICATION, justification);
        values.put(CCH_EVENT_COMMENTS, comments);
        values.put(CCH_EVENT_STATUS, status);
        values.put(CCH_FACILITY_ID, facId);
        values.put(CCH_DISTRICT_ID, did);
        values.put(CCH_FACILITY_NAME, facName);
        values.put(CCH_DISTRICT_NAME, distName);
        values.put(CCH_REGION_NAME, region);
        db.insert(CCH_EVENTS_TABLE, null, values);
    }

    public ArrayList<Event> getEventsByField(String field, String id) {
        Cursor eventCursor = findById(CCH_EVENTS_TABLE, field, id, " ORDER BY " + CCH_EVENT_START);

        ArrayList<Event> events = new ArrayList<>();

        if (eventCursor != null) {
            while (eventCursor.moveToNext()) {
                events.add(buildEvent(eventCursor));
            }
            eventCursor.close();
        }

        return events;
    }


    // Course methods
    public void addCourse(String nurseId, Long courseId, String title, String ksa, int qStatus,
                          Long pc, int score, String timeTaken, String last_accessed, int attempts,
                          String region, String distName, String facName, int did, long facId) {

        ContentValues values = new ContentValues();
        values.put(CCH_NURSE_ID, nurseId);
        values.put(CCH_COURSE_ID, courseId);
        values.put(CCH_TITLE, title);
        values.put(CCH_KSA_STATUS, ksa);
        values.put(CCH_QUIZ_STATUS, qStatus);
        values.put(CCH_PERCENTAGE_COMPLETED, pc);
        values.put(CCH_SCORE, score);
        values.put(CCH_TIME_TAKEN, timeTaken);
        values.put(CCH_LAST_ACCESSED, last_accessed);
        values.put(CCH_ATTEMPTS, attempts);
        values.put(CCH_FACILITY_ID, facId);
        values.put(CCH_DISTRICT_ID, did);
        values.put(CCH_FACILITY_NAME, facName);
        values.put(CCH_DISTRICT_NAME, distName);
        values.put(CCH_REGION_NAME, region);
        db.insert(CCH_COURSE_TABLE, null, values);
    }

    public ArrayList<Course> getCourses(String nurseId) {
       return getCoursesByField(CCH_NURSE_ID, nurseId);
    }

    public ArrayList<Course> getCoursesByField(String field, String id) {
        ArrayList<Course> courses = new ArrayList<>();

        Cursor courseCursor = findById(CCH_COURSE_TABLE, field, id, " ORDER BY " + CCH_TITLE + " asc ");

        if (courseCursor != null) {
            while (courseCursor.moveToNext()) {
                courses.add(buildCourse(courseCursor));
            }
            courseCursor.close();
        }

        return courses;
    }

    public int updateCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(CCH_KSA_STATUS, course.ksa);
        String where = CCH_NURSE_ID + "=\"" + course.nurseId + "\" AND " + CCH_COURSE_ID + "=" + course.courseId;
        return db.update(CCH_COURSE_TABLE, values, where, null);
    }

    public int countCourseByStatus(String nurseId, String status) {
        String where = CCH_NURSE_ID + "=\""+ nurseId + "\" AND LOWER(" +CCH_KSA_STATUS + ") =\"" + status + "\"";
        String q = "SELECT count(*) as cnt FROM "+CCH_COURSE_TABLE + " WHERE " +where;
        Cursor localCursor = this.getWritableDatabase().rawQuery(q, null);

        int value = 0;
        if (localCursor.getCount()  > 0) {
            localCursor.moveToNext();
            value = localCursor.getInt(localCursor.getColumnIndex("cnt"));
        }
        localCursor.close();
        return value;
    }


    // Target methods
    public void addTarget(String nurseId, int targetId, int target,
                          String type, String category, int achieved, String justification,
                          String start_date, String due_date, String status,
                          String region, String distName, String facName, int did, long facId) {

        ContentValues values = new ContentValues();

        values.put(CCH_NURSE_ID, nurseId);
        values.put(CCH_TARGET_ID, targetId);
        values.put(CCH_TARGET, target);
        values.put(CCH_TARGET_TYPE, type);
        values.put(CCH_TARGET_CATEGORY, category);
        values.put(CCH_TARGET_ACHIEVED, achieved);
        values.put(CCH_TARGET_JUSTIFICATION, justification);
        values.put(CCH_TARGET_START_DATE, start_date);
        values.put(CCH_TARGET_DUE_DATE, due_date);
        values.put(CCH_TARGET_STATUS, status);
        values.put(CCH_FACILITY_ID, facId);
        values.put(CCH_DISTRICT_ID, did);
        values.put(CCH_FACILITY_NAME, facName);
        values.put(CCH_DISTRICT_NAME, distName);
        values.put(CCH_REGION_NAME, region);
        db.insert(CCH_TARGET_TABLE, null, values);
    }

    public ArrayList<Target> getTargetsByField(String field, String id)  {
        ArrayList<Target> targets = new ArrayList<>();

        Cursor targetCursor = findById(CCH_TARGET_TABLE, field, id, "ORDER BY "+CCH_TARGET_START_DATE + " asc ");

        if (targetCursor != null) {
            while (targetCursor.moveToNext()) {
                targets.add(buildTarget(targetCursor));
            }
            targetCursor.close();
        }

        return targets;
    }


    // Facility methods
    public void addFacility(long id, String name, String type, String region, String dist, int did){
        ContentValues values = new ContentValues();
        values.put(CCH_FACILITY_ID, id); 
        values.put(CCH_FACILITY_NAME, name);
        values.put(CCH_FACILITY_TYPE, type);
        values.put(CCH_DISTRICT_ID, did);
        values.put(CCH_DISTRICT_NAME, dist);
        values.put(CCH_REGION_NAME, region);
        db.insert(CCH_FACILITY_TABLE, null, values);
    }

    public void updateFacility(Facility f) {
        ContentValues values = new ContentValues();
        values.put(CCH_FACILITY_ID, f.id);
        values.put(CCH_FACILITY_NAME, f.name);
        values.put(CCH_FACILITY_TYPE, f.facilityType);
        values.put(CCH_DISTRICT_ID, f.districtId);
        values.put(CCH_DISTRICT_NAME, f.district);
        values.put(CCH_REGION_NAME, f.region);
        db.update(CCH_FACILITY_TABLE, values, CCH_ID + "= '" + f.id + "'", null);
    }

    public void removeFacility(int id) {
        deleteTableRowByField(CCH_FACILITY_TABLE, CCH_FACILITY_ID, String.valueOf(id));
    }

    public Facility getFacility(int fid) {
        Facility facility = null;
        Cursor facilityCursor = findById(CCH_FACILITY_TABLE, CCH_FACILITY_ID, String.valueOf(fid), "");

        if (facilityCursor != null) {
            while (facilityCursor.moveToNext()) {
                   facility = buildFacility(facilityCursor);
            }
            facilityCursor.close();
        }

        return facility;
    }

    public ArrayList<Facility> getFacilities() {

        Cursor facilityCursor = this.findAll(CCH_FACILITY_TABLE, CCH_FACILITY_NAME);
        ArrayList<Facility> facilities = new ArrayList<>();

        if (facilityCursor != null) {
            while (facilityCursor.moveToNext()) {
                facilities.add(buildFacility(facilityCursor));
            }
            facilityCursor.close();
        }

        return facilities;
    }

    public ArrayList<Facility> getFacilitiesByField(String field, String id)  {
        ArrayList<Facility> facilities = new ArrayList<>();

        Cursor facilityCursor = findById(CCH_FACILITY_TABLE, field, id, "ORDER BY " + CCH_FACILITY_NAME);

        if (facilityCursor != null) {
            while (facilityCursor.moveToNext()) {
                facilities.add(buildFacility(facilityCursor));
            }
            facilityCursor.close();
        }

        return facilities;
    }


    // District methods
    public void addDistrict(int id, String name, String region) {
        ContentValues values = new ContentValues();
        values.put(CCH_DISTRICT_ID, id);
        values.put(CCH_DISTRICT_NAME, name);
        values.put(CCH_REGION_NAME, region);
        db.insert(CCH_DISTRICT_TABLE, null, values);
    }

    public void updateDistrict(District d) {
        ContentValues values = new ContentValues();
        values.put(CCH_DISTRICT_ID, d.districtId);
        values.put(CCH_DISTRICT_NAME, d.name);
        values.put(CCH_REGION_NAME, d.region);
        db.update(CCH_DISTRICT_TABLE, values, CCH_ID + "= '" + d.id + "'", null);
    }

    public void removeDistrict(int id) {
        deleteTableRowByField(CCH_DISTRICT_TABLE, CCH_DISTRICT_ID, String.valueOf(id));
    }

    public District getDistrict(int id) {
        District district = null;
        Cursor districtCursor = findById(CCH_DISTRICT_TABLE, CCH_DISTRICT_ID, String.valueOf(id), "");

        if (districtCursor != null) {
            while (districtCursor.moveToNext()) {
                district = buildDistrict(districtCursor);
            }
            districtCursor.close();
        }

        return district;
    }

    public ArrayList<District> getDistricts() {

        Cursor districtCursor = this.findAll(CCH_DISTRICT_TABLE, CCH_DISTRICT_NAME);
        ArrayList<District> districts = new ArrayList<>();

        if (districtCursor != null) {
            while (districtCursor.moveToNext()) {
                districts.add(buildDistrict(districtCursor));
            }
            districtCursor.close();
        }

        return districts;
    }


    // Region methods
    public void addRegion(int id, String name) {
        ContentValues values = new ContentValues();
        values.put(CCH_REGION_ID, id);
        values.put(CCH_REGION_NAME, name);
        db.insert(CCH_REGION_TABLE, null, values);
    }

    public void updateRegion(Region r) {
        ContentValues values = new ContentValues();
        values.put(CCH_REGION_ID, r.regionId);
        values.put(CCH_REGION_NAME, r.name);
        db.update(CCH_REGION_TABLE, values, CCH_ID + "= '" + r.id + "'", null);
    }

    public void removeRegion(int id) {
        deleteTableRowByField(CCH_REGION_TABLE, CCH_REGION_ID, String.valueOf(id));
    }

    public Region getRegion(String name) {
        Region region = null;
        Cursor regionCursor = findById(CCH_REGION_TABLE, CCH_REGION_NAME, name, "");

        if (regionCursor != null) {
            while (regionCursor.moveToNext()) {
                region = buildRegion(regionCursor);
            }
            regionCursor.close();
        }

        return region;
    }


    // Helper  methods
    private Region buildRegion(Cursor rCursor) {
        Region r = new Region();
        String supConstraint = "AND " + CCH_NURSE_ROLE + " NOT LIKE '%nurse%'";
        String nConstraint = "AND " + CCH_NURSE_ROLE + " LIKE '%nurse%'";

        r.id = rCursor.getInt(rCursor.getColumnIndex(CCH_ID));
        r.regionId = rCursor.getInt(rCursor.getColumnIndex(CCH_REGION_ID));
        r.name = rCursor.getString(rCursor.getColumnIndex(CCH_REGION_NAME));
        r.cntDistricts = getCount(CCH_DISTRICT_TABLE, CCH_REGION_NAME, r.name, "");
        r.cntFacilities = getCount(CCH_FACILITY_TABLE, CCH_REGION_NAME, r.name,"");
        r.cntNurses = getCount(CCH_NURSE_TABLE, CCH_REGION_NAME, r.name, nConstraint);
        r.cntSupervisors = getCount(CCH_NURSE_TABLE, CCH_REGION_NAME, r.name, supConstraint);
        r.cntEvents = getCount(CCH_EVENTS_TABLE, CCH_REGION_NAME, r.name,"");
        r.cntTargets = getCount(CCH_TARGET_TABLE, CCH_REGION_NAME, r.name,"");
        return r;
    }

    private District buildDistrict(Cursor districtCursor){
        String supConstraint = "AND " + CCH_NURSE_ROLE + " NOT LIKE '%nurse%'";
        String nurseConstraint = "AND " + CCH_NURSE_ROLE + " LIKE '%nurse%'";

        District district = new District();
        district.id = districtCursor.getInt(districtCursor.getColumnIndex(CCH_ID));
        district.districtId = districtCursor.getInt(districtCursor.getColumnIndex(CCH_DISTRICT_ID));
        district.name = districtCursor.getString(districtCursor.getColumnIndex(CCH_DISTRICT_NAME));
        district.region = districtCursor.getString(districtCursor.getColumnIndex(CCH_REGION_NAME));
        Region region = getRegion(district.region);
        district.regionId = region.id;

        district.cntFacilities = getCount(CCH_FACILITY_TABLE, CCH_DISTRICT_NAME, district.name, "");
        district.cntNurses = getCount(CCH_NURSE_TABLE, CCH_DISTRICT_NAME, district.name, nurseConstraint);
        district.cntSupervisors = getCount(CCH_NURSE_TABLE, CCH_DISTRICT_NAME, district.name, supConstraint);
        district.cntEvents = getCount(CCH_EVENTS_TABLE, CCH_DISTRICT_NAME, district.name, "");
        district.cntTargets = getCount(CCH_TARGET_TABLE, CCH_DISTRICT_NAME, district.name, "");

        district.facilities = getFacilitiesByField(CCH_DISTRICT_ID, String.valueOf(district.districtId));


        return district;
    }

    private Facility buildFacility(Cursor facilityCursor){
        String supConstraint = "AND " + CCH_NURSE_ROLE + " NOT LIKE '%nurse%'";
        String nurseConstraint = "AND " + CCH_NURSE_ROLE + " LIKE '%nurse%'";

        Facility facility = new Facility();
        facility.id = facilityCursor.getInt(facilityCursor.getColumnIndex(CCH_ID));
        facility.facilityId = facilityCursor.getInt(facilityCursor.getColumnIndex(CCH_FACILITY_ID));
        facility.name = facilityCursor.getString(facilityCursor.getColumnIndex(CCH_FACILITY_NAME));
        facility.facilityType = facilityCursor.getString(facilityCursor.getColumnIndex(CCH_FACILITY_TYPE));
        facility.region = facilityCursor.getString(facilityCursor.getColumnIndex(CCH_REGION_NAME));
        facility.district = facilityCursor.getString(facilityCursor.getColumnIndex(CCH_DISTRICT_NAME));
        facility.districtId = facilityCursor.getInt(facilityCursor.getColumnIndex(CCH_DISTRICT_ID));

        facility.cntNurses = getCount(CCH_NURSE_TABLE, CCH_FACILITY_NAME, facility.name, nurseConstraint);
        facility.cntSupervisors = getCount(CCH_NURSE_TABLE, CCH_FACILITY_NAME, facility.name, supConstraint);
        facility.cntEvents = getCount(CCH_EVENTS_TABLE, CCH_FACILITY_NAME, facility.name, "");
        facility.cntTargets = getCount(CCH_TARGET_TABLE, CCH_FACILITY_NAME, facility.name, "");

        return facility;
    }

    private Nurse buildNurse(Cursor nurseCursor) {
        Nurse nurse = new Nurse();
        nurse.id = nurseCursor.getInt(nurseCursor.getColumnIndex(CCH_ID));
        nurse.nurseId = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_ID));
        nurse.name = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_NAME));
        nurse.title = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_TITLE));
        nurse.role = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_ROLE));
        nurse.ischn = nurseCursor.getInt(nurseCursor.getColumnIndex(CCH_NURSE_IS_CHN));
        nurse.group = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_GROUP));
        nurse.status = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_STATUS));
        nurse.gender = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_GENDER));
        nurse.phoneNumber = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_PHONE_NUMBER));
        nurse.primaryFacility = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_NURSE_FACILITY));
        nurse.region = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_REGION_NAME));
        nurse.district = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_DISTRICT_NAME));
        nurse.facility = nurseCursor.getString(nurseCursor.getColumnIndex(CCH_FACILITY_NAME));
        nurse.districtId = nurseCursor.getInt(nurseCursor.getColumnIndex(CCH_DISTRICT_ID));
        nurse.facilityId = nurseCursor.getInt(nurseCursor.getColumnIndex(CCH_FACILITY_ID));

        nurse.title = nurse.title.equals("") ? nurse.role : nurse.title;

        nurse.courses = getCoursesByField(CCH_NURSE_ID, nurse.nurseId);
        nurse.targets = getTargetsByField(CCH_NURSE_ID, nurse.nurseId);
        nurse.events = getEventsByField(CCH_NURSE_ID, nurse.nurseId);

        nurse.coursesPassed = countCourseByStatus(nurse.nurseId, "passed");
        nurse.coursesEligible = countCourseByStatus(nurse.nurseId, "eligible");
        nurse.coursesInProgress = countCourseByStatus(nurse.nurseId, "in progress");

        return nurse;
    }

    private Event buildEvent(Cursor eventCursor) {
        Event event = new Event();
        event.eventId = eventCursor.getInt(eventCursor.getColumnIndex(CCH_EVENT_ID));
        event.nurseId = eventCursor.getString(eventCursor.getColumnIndex(CCH_NURSE_ID));
        event.nurse = eventCursor.getString(eventCursor.getColumnIndex(CCH_NURSE_NAME));
        event.title = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_TITLE));
        event.location = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_LOCATION));
        event.type = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_TYPE));
        event.justification = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_JUSTIFICATION));
        event.comments = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_COMMENTS));
        event.status = eventCursor.getString(eventCursor.getColumnIndex(CCH_EVENT_STATUS));
        event.start = eventCursor.getLong(eventCursor.getColumnIndex(CCH_EVENT_START));
        event.end = eventCursor.getLong(eventCursor.getColumnIndex(CCH_EVENT_END));
        event.region = eventCursor.getString(eventCursor.getColumnIndex(CCH_REGION_NAME));
        event.district = eventCursor.getString(eventCursor.getColumnIndex(CCH_DISTRICT_NAME));
        event.facility = eventCursor.getString(eventCursor.getColumnIndex(CCH_FACILITY_NAME));
        event.districtId = eventCursor.getInt(eventCursor.getColumnIndex(CCH_DISTRICT_ID));
        event.facilityId = eventCursor.getInt(eventCursor.getColumnIndex(CCH_FACILITY_ID));

        return event;
    }

    private Target buildTarget(Cursor targetCursor){
        Target target = new Target();
        target.id = targetCursor.getInt(targetCursor.getColumnIndex(CCH_ID));
        target.nurseId = targetCursor.getString(targetCursor.getColumnIndex(CCH_NURSE_ID));
        target.targetId = targetCursor.getInt(targetCursor.getColumnIndex(CCH_TARGET_ID));
        target.target = targetCursor.getInt(targetCursor.getColumnIndex(CCH_TARGET));
        target.type = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_TYPE));
        target.category = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_CATEGORY));
        target.justification = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_JUSTIFICATION));
        target.achieved = targetCursor.getInt(targetCursor.getColumnIndex(CCH_TARGET_ACHIEVED));
        target.status = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_STATUS));
        target.startDate = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_START_DATE));
        target.dueDate = targetCursor.getString(targetCursor.getColumnIndex(CCH_TARGET_DUE_DATE));
        target.region = targetCursor.getString(targetCursor.getColumnIndex(CCH_REGION_NAME));
        target.district = targetCursor.getString(targetCursor.getColumnIndex(CCH_DISTRICT_NAME));
        target.facility = targetCursor.getString(targetCursor.getColumnIndex(CCH_FACILITY_NAME));
        target.districtId = targetCursor.getInt(targetCursor.getColumnIndex(CCH_DISTRICT_ID));
        target.facilityId = targetCursor.getInt(targetCursor.getColumnIndex(CCH_FACILITY_ID));
        return target;
    }

    private Course buildCourse(Cursor localCursor){
        Course course = new Course();
        course.id = localCursor.getLong(localCursor.getColumnIndex(CCH_ID));
        course.courseId = localCursor.getLong(localCursor.getColumnIndex(CCH_COURSE_ID));
        course.nurseId = localCursor.getString(localCursor.getColumnIndex(CCH_NURSE_ID));
        course.title = localCursor.getString(localCursor.getColumnIndex(CCH_TITLE));
        course.attempts = localCursor.getInt(localCursor.getColumnIndex(CCH_ATTEMPTS));
        course.score = localCursor.getInt(localCursor.getColumnIndex(CCH_SCORE));
        course.last_accessed = localCursor.getString(localCursor.getColumnIndex(CCH_LAST_ACCESSED));
        course.percentageCompletion = localCursor.getInt(localCursor.getColumnIndex(CCH_PERCENTAGE_COMPLETED));
        course.ksa = localCursor.getString(localCursor.getColumnIndex(CCH_KSA_STATUS));
        course.quiz_status = localCursor.getInt(localCursor.getColumnIndex(CCH_QUIZ_STATUS));
        course.timeTaken = localCursor.getString(localCursor.getColumnIndex(CCH_TIME_TAKEN));
        course.region = localCursor.getString(localCursor.getColumnIndex(CCH_REGION_NAME));
        course.district = localCursor.getString(localCursor.getColumnIndex(CCH_DISTRICT_NAME));
        course.facility = localCursor.getString(localCursor.getColumnIndex(CCH_FACILITY_NAME));
        course.districtId = localCursor.getInt(localCursor.getColumnIndex(CCH_DISTRICT_ID));
        course.facilityId = localCursor.getInt(localCursor.getColumnIndex(CCH_FACILITY_ID));
        return course;
    }



    // DB helper
    private int getCount(String table, String field, String idValue, String extraSearch) {
        Cursor c = findById(table, field, idValue, extraSearch);
        return (c==null) ? 0 : c.getCount();
    }

    private Cursor findAll(String table, String orderBy) {
        try {
            return  this.getWritableDatabase().rawQuery("SELECT * FROM " + table + " ORDER BY " + orderBy, null);
        } catch (Exception e) {
            Log.d(TAG, "Exception  findAll : " + e.getLocalizedMessage());
        }
        return null;
    }

    private Cursor findById(String table, String field, String idValue, String extraSearch) {
        try {
            String str = " SELECT * FROM " + table + " WHERE " + field + " =  \"" + idValue + "\" " + extraSearch;
            return this.getWritableDatabase().rawQuery(str,null);
        } catch (Exception e) {
            Log.d(TAG, "Exception  findById : " + e.getLocalizedMessage());
        }

        return null;
    }

    private boolean valueExistsInTable(String table, String field, String value) {
        boolean result;

        try {
            Cursor localCursor = findById(table, field, value, "");
            result = (localCursor!=null) && (localCursor.getCount() > 0);
        } catch(NullPointerException e) {
            result = false;
        }
        return result;
    }

    private void deleteTableRowByField(String table, String field, String value) {
        String where = field + "=?";
        String[] whereArgs = new String[] { value };
        db.delete(table, where, whereArgs);
    }

    private void deleteTableContent(String tableName) {
        try {
            this.getWritableDatabase().execSQL("DELETE FROM " + tableName);
        } catch (Exception e) {
            Log.d(TAG, "Exception  deleteTableContent : " + e.getLocalizedMessage());
        }
    }
}

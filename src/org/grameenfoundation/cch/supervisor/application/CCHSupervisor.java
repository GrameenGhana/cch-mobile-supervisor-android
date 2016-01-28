package org.grameenfoundation.cch.supervisor.application;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.tasks.UpdateCCHLogTask;
import org.grameenfoundation.cch.supervisor.tasks.UpdateSupervisorInfoTask;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CCHSupervisor extends Application {

    public static final String TAG = CCHSupervisor.class.getSimpleName();

    public static final String OPPIAMOBILE_API = "api/v1/";
    public static final String LOGIN_PATH = OPPIAMOBILE_API + "user/";
    public static final String CCH_TRACKER_SUBMIT_PATH = "api/v1/tracker";
    public static final String CCH_SUPERVISOR_API = "api/v1/incharge";

    public static final String BUGSENSE_API_KEY = "f3a6ec3a";
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String USER_AGENT = "CCHSupervisor Android: ";
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm:ss");

    public UpdateCCHLogTask omUpdateCCHLogTask = null; 
    public UpdateSupervisorInfoTask omUpdateSupervisorInfoTask = null;

    public static boolean isLoggedIn(Activity act) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act.getBaseContext());
        String username = prefs.getString(act.getString(R.string.prefs_username), "");
        String apiKey = prefs.getString(act.getString(R.string.prefs_api_key), "");
        if (username.trim().equals("") || apiKey.trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }
}

package org.grameenfoundation.cch.supervisor;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.task.UpdateDataTask;
import org.grameenfoundation.cch.supervisor.task.UploadLogsTask;
import org.grameenfoundation.cch.supervisor.util.DatabaseHelper;

public class Supervisor extends Application {

    public static final String TAG = Supervisor.class.getSimpleName();

    public static Context mAppContext;
    public static DatabaseHelper Db;

    public UploadLogsTask omUploadLogsTask = null;
    public UpdateDataTask omUpdateLocationTask = null;
    public UpdateDataTask omUpdateNurseTask = null;
    public UpdateDataTask omUpdateEventTask = null;
    public UpdateDataTask omUpdateCourseTask = null;
    public UpdateDataTask omUpdateTargetTask = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        Db = new DatabaseHelper(mAppContext);
    }

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public static User getUser() {
        User u = null;
        String userId = Supervisor.getPreferences().getString(mAppContext.getString(R.string.prefs_username), "noId");
        if (!userId.equalsIgnoreCase("noId")) {
            u = Supervisor.Db.getUser(userId);
        }
        return u;
    }
}


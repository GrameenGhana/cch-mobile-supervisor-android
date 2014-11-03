package org.grameenfoundation.cch.supervisor.service;


import java.util.ArrayList;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.listener.SubmitListener;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.tasks.UpdateCCHLogTask;
import org.grameenfoundation.cch.supervisor.tasks.UpdateSupervisorInfoTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

public class TrackerService extends Service implements SubmitListener {

	public static final String TAG = TrackerService.class.getSimpleName();
	
	private final IBinder mBinder = new MyBinder();
	private SharedPreferences prefs;
	private DbHelper dbh;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbh = new DbHelper(this);
		BugSenseHandler.initAndStartSession(this, CCHSupervisor.BUGSENSE_API_KEY);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Starting Tracker Service");
		
		boolean backgroundData = true;
		Bundle b = intent.getExtras();
		if (b != null) {
			backgroundData = b.getBoolean("backgroundData");
		}

		if (isOnline() && backgroundData) 
		{							
			CCHSupervisor app = (CCHSupervisor) this.getApplication();
			
			// check for updated nurse info and upload logs: should only do this once a day or so....
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			long lastRun = prefs.getLong("lastCourseUpdateCheck", 0);
			long now = System.currentTimeMillis()/1000;
				
			if((lastRun + (3600*12)) < now) {
							
				Editor editor = prefs.edit();
				editor.putLong("lastCourseUpdateCheck", now);
				editor.commit();
			
				/* Check to see if the CCH log needs any updating */
				if(app.omUpdateCCHLogTask == null){
					Log.v(TAG, "Updating CCH logs");
					Payload mqp = dbh.getCCHUnsentLog();
					app.omUpdateCCHLogTask = new UpdateCCHLogTask(this);
					app.omUpdateCCHLogTask.execute(mqp);
				}
				
				/* Update supervisor information */
				if(app.omUpdateSupervisorInfoTask == null) {
					ArrayList<Object> users = new ArrayList<Object>();
					String userid = prefs.getString(getApplicationContext().getString(R.string.prefs_username), "noid"); 
					if (userid != "noid")
					{
						User u = dbh.getUser(userid);
						users.add(u);
						Payload p = new Payload(users);
						app.omUpdateSupervisorInfoTask = new UpdateSupervisorInfoTask(this);
						app.omUpdateSupervisorInfoTask.setUpdateSupervisorInfoListener(this);
						app.omUpdateSupervisorInfoTask.execute(p);
					}
				}
			}		
		}
		
		return Service.START_NOT_STICKY;
	}
	
	public void submitComplete(Payload response) 
	{			
		if(response.isResult()){
			User u = (User) response.getData().get(0);		
	    	dbh.updateUser(u);
		} 		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		public TrackerService getService() {
			return TrackerService.this;
		}
	}

	private boolean isOnline() {
		getApplicationContext();
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}	
}

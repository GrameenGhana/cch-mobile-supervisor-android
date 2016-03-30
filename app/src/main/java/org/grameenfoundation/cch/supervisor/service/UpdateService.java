package org.grameenfoundation.cch.supervisor.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.task.SynchronizationManager;
import org.grameenfoundation.cch.supervisor.util.ConnectionUtils;

public class UpdateService extends Service  {

	public static final String TAG = UpdateService.class.getSimpleName();

	private final IBinder mBinder = new MyBinder();

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (ConnectionUtils.isNetworkConnected(getApplicationContext()) && Supervisor.Db.needDataUpdate()) {
            try {
                    SynchronizationManager.getServiceInstance().uploadLogs();

                    if (Supervisor.Db.isLocationUpdateNeeded()) {
                        SynchronizationManager.getServiceInstance().updateLocations();
                    }

                    if (Supervisor.Db.isNurseUpdateNeeded())    {
                        SynchronizationManager.getServiceInstance().updateNurses();
                    }

                    if (Supervisor.Db.isCourseUpdateNeeded()) {
                        SynchronizationManager.getServiceInstance().updateCourses();
                    }

                    if (Supervisor.Db.isEventUpdateNeeded()) {
                        SynchronizationManager.getServiceInstance().updateEvents();
                    }

                    if (Supervisor.Db.isTargetUpdateNeeded())   {
                        SynchronizationManager.getServiceInstance().updateTargets();
                    }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
		}
		
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		public UpdateService getService() {
			return UpdateService.this;
		}
	}
}

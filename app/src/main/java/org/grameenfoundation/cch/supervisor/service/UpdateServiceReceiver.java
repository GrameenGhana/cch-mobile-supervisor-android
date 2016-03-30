package org.grameenfoundation.cch.supervisor.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class UpdateServiceReceiver extends BroadcastReceiver {

	public static final String TAG = UpdateServiceReceiver.class.getSimpleName();

	// Restart service every 1 hour
	private static final long REPEAT_TIME = 60000;

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent i = new Intent(context, UpdateService.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        // Start 30 seconds after boot completed
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 30);

		// every 1 hour: InexactRepeating allows Android to optimize the energy consumption
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);
	}
}

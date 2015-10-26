package org.grameenfoundation.cch.supervisor.service;

import org.grameenfoundation.cch.supervisor.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class TrackerStartServiceReceiver extends BroadcastReceiver {

	public final static String TAG = TrackerStartServiceReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean backgroundData = prefs.getBoolean(ctx.getString(R.string.prefs_background_data_connect), true);
		Intent service = new Intent(ctx, TrackerService.class);
		
		Bundle tb = new Bundle();
		tb.putBoolean("backgroundData", backgroundData);
		service.putExtras(tb);
		
		ctx.startService(service);
	}
}
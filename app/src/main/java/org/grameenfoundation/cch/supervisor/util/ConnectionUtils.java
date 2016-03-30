package org.grameenfoundation.cch.supervisor.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import org.grameenfoundation.cch.supervisor.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConnectionUtils {

	public final static String TAG = ConnectionUtils.class.getSimpleName();

	public static boolean isLoggedIn(Activity act) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act.getBaseContext());
		String username = prefs.getString(act.getString(R.string.prefs_username), "");
		String apiKey = prefs.getString(act.getString(R.string.prefs_api_key), "");
		return (!(username.trim().equals("") || apiKey.trim().equals("")));
	}

	public static boolean isOnWifi(Context ctx) {
		ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		return (!(netInfo == null || netInfo.getType() != ConnectivityManager.TYPE_WIFI));
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm
				.getActiveNetworkInfo().isConnected());
	}

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
        inputStream.close();
        return result;
    }


}

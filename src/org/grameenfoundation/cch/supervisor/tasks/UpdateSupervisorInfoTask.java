package org.grameenfoundation.cch.supervisor.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.listener.SubmitListener;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class UpdateSupervisorInfoTask extends AsyncTask<Payload, Object, Payload> {

	public static final String TAG = UpdateSupervisorInfoTask.class.getSimpleName();

	private Context ctx;
	private SharedPreferences prefs;
	private SubmitListener mStateListener;
	
	public UpdateSupervisorInfoTask(Context c) {
		this.ctx = c;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	@Override
	protected Payload doInBackground(Payload... params) {

		Payload payload = params[0];
		User u = (User) payload.getData().get(0);
		HttpClient client = new DefaultHttpClient();

		String url = prefs.getString("prefServer", ctx.getString(R.string.prefServerYabr3)) + CCHSupervisor.CCH_SUPERVISOR_API +'/' + u.getUsername();
		
		HttpGet httpGet = new HttpGet(url);

		try {
			// make request
			HttpResponse response = client.execute(httpGet);

			// read response
			InputStream content = response.getEntity().getContent();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(content), 4096);
			String responseStr = "";
			String s = "";

			while ((s = buffer.readLine()) != null) {
				responseStr += s;
			}
			
			// check status code
			switch (response.getStatusLine().getStatusCode()){
				case 400: // unauthorised
					payload.setResult(false);
					payload.setResultResponse("Error");
					break;
				case 200: // got data
					u.setSupervisorInfo(responseStr);
					payload.setResult(true);
					payload.setResultResponse("Complete");
					break;
				default:
					Log.d(TAG,responseStr);
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.error_connection));
			}
			

		} catch (UnsupportedEncodingException e) {
			payload.setResult(false);
			payload.setResultResponse(ctx.getString(R.string.error_connection));
		} catch (ClientProtocolException e) {
			payload.setResult(false);
			payload.setResultResponse(ctx.getString(R.string.error_connection));
		} catch (IOException e) {
			payload.setResult(false);
			payload.setResultResponse(ctx.getString(R.string.error_connection));
		} finally {

		}
		return payload;
	}

	@Override
	protected void onPostExecute(Payload response) {
		synchronized (this) {
			// reset submit task back to null after completion - so next call can run properly
			CCHSupervisor app = (CCHSupervisor) ctx.getApplicationContext();
			app.omUpdateSupervisorInfoTask = null;
			
            if (mStateListener != null) {
               mStateListener.submitComplete(response);
            }
        }
	}
	
	public void setUpdateSupervisorInfoListener(SubmitListener srl) {
        synchronized (this) {
            mStateListener = srl;
        }
    }
}

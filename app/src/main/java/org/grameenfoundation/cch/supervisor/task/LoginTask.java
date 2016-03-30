package org.grameenfoundation.cch.supervisor.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.util.ConnectionUtils;
import org.grameenfoundation.cch.supervisor.util.Constants;
import org.grameenfoundation.cch.supervisor.util.MetaDataUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<Payload, Object, Payload> {

	public static final String TAG = LoginTask.class.getSimpleName();

	private Context ctx;
	private SharedPreferences prefs;
	private SubmitListener mStateListener;
	
	public LoginTask(Context c) {
		this.ctx = c;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public void setListener(SubmitListener srl) {
		synchronized (this) {
			mStateListener = srl;
		}
	}

	@Override
	protected Payload doInBackground(Payload... params) {

		Payload payload = params[0];
		User u = (User) payload.getData().get(0);

        InputStream input = null;
        HttpURLConnection urlConnection = null;

		try {
            URL url = new URL(Constants.LOGIN_PATH);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("USER_AGENT", Constants.USER_AGENT);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

			// add post params
            JSONObject json = new JSONObject();
            DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
            json.put("username", u.getUsername());
            json.put("password", u.getPassword());
            dStream.writeBytes(json.toString());
            dStream.flush();
            dStream.close();

			// Log.d(TAG, "Response code: " + urlConnection.getResponseCode());

			// check status code
			switch (urlConnection.getResponseCode()){
				case 400: // unauthorised
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.error_login));
					break;
				case 201: // logged in
                    input = new BufferedInputStream(urlConnection.getInputStream());
                    String response = ConnectionUtils.convertInputStreamToString(input);
					JSONObject jsonResp = new JSONObject(response);
					//Log.d(TAG, "Response: " + response);
					u.setApi_key(jsonResp.getString("api_key"));
					u.setFirstName(jsonResp.getString("first_name"));
					u.setLastName(jsonResp.getString("last_name"));
					try {
						JSONObject metadata = jsonResp.getJSONObject("metadata");
				        MetaDataUtils mu = new MetaDataUtils(ctx);
				        mu.saveMetaData(metadata, prefs);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					payload.setResult(true);
                    payload.getResponseData().add(u);
					payload.setResultResponse(ctx.getString(R.string.login_complete));
					break;
				default:
					payload.setResult(false);
					payload.setResultResponse(ctx.getString(R.string.error_connection));
			}

        } catch (IOException e) {
            payload.setResult(false);
            payload.setResultResponse(ctx.getString(R.string.error_connection));
		} catch (JSONException e) {
			e.printStackTrace();
			payload.setResult(false);
			payload.setResultResponse(ctx.getString(R.string.error_processing_response));
		}

		return payload;
	}

	@Override
	protected void onPostExecute(Payload response) {
		synchronized (this) {
            if (mStateListener != null) {
               mStateListener.submitComplete(response);
            }
        }
	}
}

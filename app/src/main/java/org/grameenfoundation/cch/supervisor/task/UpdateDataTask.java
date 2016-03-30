package org.grameenfoundation.cch.supervisor.task;

import android.os.AsyncTask;
import android.util.Log;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.util.ConnectionUtils;
import org.grameenfoundation.cch.supervisor.util.Constants;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateDataTask extends AsyncTask<Payload, Object, Payload> {

	public final static String TAG = UpdateDataTask.class.getSimpleName();

    private String resource;
    private String lastUpdateTime;
    private SubmitListener requestListener = null;

	public UpdateDataTask(String location, String time) {
        resource = location;
        lastUpdateTime = time;
    }

    public void setListener(SubmitListener listener) {
        synchronized (this) {
            requestListener = listener;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

	@Override
	protected Payload doInBackground(Payload... params) {

		Payload payload = params[0];

        User u = Supervisor.getUser();

        if (u != null) {
            @SuppressWarnings("unchecked")
            InputStream input = null;
            HttpURLConnection urlConnection = null;
            String href = Constants.CCH_SUPERVISOR_API + '/' + u.getUsername() + '/' + resource + '/' + lastUpdateTime;

            try {
                URL url = new URL(href);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("USER_AGENT", Constants.USER_AGENT);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //Log.d(TAG, "Response code from "+resource+": " + urlConnection.getResponseCode());
                switch (urlConnection.getResponseCode()) {
                    case 400: // unauthorised
                        payload.setResult(false);
                        payload.setResultResponse("Unauthorized request");
                        break;
                    case 200: // got data
                        input = new BufferedInputStream(urlConnection.getInputStream());
                        String response = ConnectionUtils.convertInputStreamToString(input);
                        payload.setResult(true);
                        payload.addResponseData(u);
                        payload.addResponseData(response);
                        payload.setResultResponse("Complete");
                        break;
                    case 500:
                        Log.d(TAG, urlConnection.getResponseMessage());
                        payload.setResult(false);
                        payload.setResultResponse(Supervisor.mAppContext.getString(R.string.error_connection));
                    default:
                        payload.setResult(false);
                        payload.setResultResponse(Supervisor.mAppContext.getString(R.string.error_connection));
                }

            } catch (IOException e) {
                payload.setResult(false);
                payload.setResultResponse(Supervisor.mAppContext.getString(R.string.error_connection));
            }
        } else {
            payload.setResult(false);
            payload.setResultResponse("User not found. Please login first.");
        }

		return payload;
	}

	@Override
    protected void onPostExecute(Payload p) {
        synchronized (this) {
            if (requestListener != null) {
                requestListener.submitComplete(p);
            }
        }
    }
}

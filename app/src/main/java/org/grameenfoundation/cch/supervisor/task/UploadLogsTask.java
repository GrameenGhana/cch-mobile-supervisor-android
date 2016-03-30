package org.grameenfoundation.cch.supervisor.task;

import android.os.AsyncTask;
import android.util.Base64;

import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.TrackerLog;
import org.grameenfoundation.cch.supervisor.util.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


public class UploadLogsTask extends AsyncTask<Payload, Object, Payload> {

	private final static String TAG = UploadLogsTask.class.getSimpleName();

	private SubmitListener requestListener = null;

    public UploadLogsTask() {}

    public void setListener(SubmitListener listener) {
        synchronized (this) {
            requestListener = listener;
        }
    }

    private String getAuthHeader(){
        byte[] b = (Constants.CCH_API_USER + ":" + Constants.CCH_API_KEY).getBytes();
        return "Basic " + Base64.encode(b, Base64.NO_WRAP);
    }

    private static Collection<Collection<TrackerLog>> split(Collection<Object> bigCollection, int maxBatchSize) {
        Collection<Collection<TrackerLog>> result = new ArrayList<>();

        ArrayList<TrackerLog> currentBatch = null;
        for (Object obj : bigCollection) {
            TrackerLog tl = (TrackerLog) obj;
            if (currentBatch == null) {
                currentBatch = new ArrayList<>();
            } else if (currentBatch.size() >= maxBatchSize) {
                result.add(currentBatch);
                currentBatch = new ArrayList<>();
            }

            currentBatch.add(tl);
        }

        if (currentBatch != null) {
            result.add(currentBatch);
        }

        return result;
    }

    private String createDataString(Collection<TrackerLog> collection){
        String s = "data={\"logs\":[";
        int counter = 0;
        for(TrackerLog tl: collection){
            counter++;
            s += tl.getContent();
            if(counter != collection.size()){ s += ","; }
        }
        s += "]}";
        return s;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

	@Override
	protected Payload doInBackground(Payload... params) {

		Payload payload = params[0];
		
        @SuppressWarnings("unchecked")
        Collection<Collection<TrackerLog>> result = (Collection<Collection<TrackerLog>>) split((Collection<Object>) payload.getData(), 10);

        try {
		    URL url = new URL(Constants.CCH_TRACKER_SUBMIT_PATH);
            HttpURLConnection urlConnection =  (HttpURLConnection) url.openConnection();
		    urlConnection.setRequestMethod("POST");
		    urlConnection.setRequestProperty("USER_AGENT", Constants.USER_AGENT);
		    urlConnection.setRequestProperty("Content-Type", "application/json");
		    urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Authorization", getAuthHeader());
		    urlConnection.setDoOutput(true);

            for (Collection<TrackerLog> trackerBatch : result) {
                String dataToSend = createDataString(trackerBatch);
                DataOutputStream dStream = new DataOutputStream(urlConnection.getOutputStream());
                dStream.writeBytes(dataToSend);
                dStream.flush();
                dStream.close();

                // check status code
                switch (urlConnection.getResponseCode()) {
                    case 200: // submitted
                        for (TrackerLog tl : trackerBatch) { Supervisor.Db.markLogsSubmitted(tl.getId()); }
                        payload.setResult(true);
                        break;

                    case 400: // submitted but invalid request - returned 400 Bad Request - so record as submitted so doesn't keep trying
                        payload.setResult(false);
                        break;

                    default:
                        payload.setResult(false);
                }
            }

		} catch (IOException e) {
			payload.setResult(false);
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

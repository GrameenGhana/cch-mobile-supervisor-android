package org.grameenfoundation.cch.supervisor.tasks;

import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.model.CCHTrackerLog;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.util.CCHHTTPConnectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class UpdateCCHLogTask extends AsyncTask<Payload, Object, Payload> {

	public final static String TAG = UpdateCCHLogTask.class.getSimpleName();

	private Context ctx;

	public UpdateCCHLogTask(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	protected Payload doInBackground(Payload... params) {

		Payload payload = params[0];
		
		@SuppressWarnings("unchecked")
		Collection<Collection<CCHTrackerLog>> result = (Collection<Collection<CCHTrackerLog>>) split((Collection<Object>) payload.getData(), 10);
		
		CCHHTTPConnectionUtils client = new CCHHTTPConnectionUtils(ctx);
		
		String url = client.getFullURL(CCHSupervisor.CCH_TRACKER_SUBMIT_PATH);
		
		//HttpPatch httpPatch = new HttpPatch(url);
		HttpPost httpPatch = new HttpPost(url);
		
		for (Collection<CCHTrackerLog> trackerBatch : result) {
			String dataToSend = createDataString(trackerBatch);
			
			try {
				List<NameValuePair> nameValuePairs = client.postData(dataToSend);	
		        httpPatch.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpPatch.addHeader("Authorization", client.getAuthHeader());

                //Log.v(TAG,url);
				//Log.v(TAG,dataToSend);
				
                // make request
				HttpResponse response = client.execute(httpPatch);	
				
				//Log.d(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
				
				InputStream content = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content), 4096);
				String s = "";

				while ((s += buffer.readLine()) != null) {}
				
				Log.d(TAG,s);
				
				switch (response.getStatusLine().getStatusCode()){
					case 200: // submitted
						DbHelper dbh = new DbHelper(ctx);
						for(CCHTrackerLog tl: trackerBatch){ 
							dbh.markCCHLogSubmitted(tl.getId());
						}
						dbh.close();
						payload.setResult(true);				    	
						break;
						
					case 400: // submitted but invalid request - returned 400 Bad Request - so record as submitted so doesn't keep trying
						DbHelper dbh2 = new DbHelper(ctx);
						for(CCHTrackerLog tl: trackerBatch){
							dbh2.markCCHLogSubmitted(tl.getId());
						};
						dbh2.close();
						payload.setResult(true);
						break;
					default:
						payload.setResult(false);
				}

			} catch (UnsupportedEncodingException e) {
				payload.setResult(false);
			} catch (ClientProtocolException e) {
				payload.setResult(false);
			} catch (IOException e) {
				payload.setResult(false);
			}
		}
		
		return payload;
	}

	protected void onProgressUpdate(String... obj) {
		// do nothing
	}
	
	@Override
    protected void onPostExecute(Payload p) {
		// reset submit task back to null after completion - so next call can run properly
		CCHSupervisor app = (CCHSupervisor) ctx.getApplicationContext();
		app.omUpdateCCHLogTask = null;
    }
	
	private static Collection<Collection<CCHTrackerLog>> split(Collection<Object> bigCollection, int maxBatchSize) {
		Collection<Collection<CCHTrackerLog>> result = new ArrayList<Collection<CCHTrackerLog>>();

		ArrayList<CCHTrackerLog> currentBatch = null;
		for (Object obj : bigCollection) {
			CCHTrackerLog tl = (CCHTrackerLog) obj;
			if (currentBatch == null) {
				currentBatch = new ArrayList<CCHTrackerLog>();
			} else if (currentBatch.size() >= maxBatchSize) {
				result.add(currentBatch);
				currentBatch = new ArrayList<CCHTrackerLog>();
			}

			currentBatch.add(tl);
		}

		if (currentBatch != null) {
			result.add(currentBatch);
		}

		return result;
	}
	
	private String createDataString(Collection<CCHTrackerLog> collection){
		String s = "{\"logs\":[";
		int counter = 0;
		for(CCHTrackerLog tl: collection){
			counter++;
			s += tl.getContent();
			if(counter != collection.size()){ s += ","; }
		}
		s += "]}";
		return s;
	}

}

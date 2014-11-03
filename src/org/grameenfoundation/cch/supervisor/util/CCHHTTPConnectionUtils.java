package org.grameenfoundation.cch.supervisor.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;

public class CCHHTTPConnectionUtils extends DefaultHttpClient {
	
	private HttpParams httpParameters;
	
	private static final String CCH_SERVER = "http://188.226.189.149/cch/yabr3/";
	private static final String CCH_API_USER= "tracker";
	private static final String CCH_API_KEY = "dog";
	
	public CCHHTTPConnectionUtils(Context ctx){
		this.httpParameters = new BasicHttpParams();
		
		HttpConnectionParams.setConnectionTimeout(
				httpParameters,
				Integer.parseInt(ctx.getString(R.string.prefServerTimeoutConnectionDefault)));
				
		HttpConnectionParams.setSoTimeout(
				httpParameters,
				Integer.parseInt(ctx.getString(R.string.prefServerTimeoutResponseDefault)));
		
		// add user agent 
		String v = "0";
		try {
			v = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
		super.setParams(httpParameters);
		super.getParams().setParameter(CoreProtocolPNames.USER_AGENT, CCHSupervisor.USER_AGENT + v);
	}
	
	public String getAuthHeader(){
		byte[] b = (CCH_API_USER+":"+CCH_API_KEY).getBytes();
		return "Basic " + Base64.encode(b, Base64.NO_WRAP);
	}
	
	public String getFullURL(String apiPath){
		return CCH_SERVER + apiPath;
	}
	
	public List<NameValuePair> postData(String data)
	{
		List<NameValuePair> pairs = new LinkedList<NameValuePair>();
		pairs.add(new BasicNameValuePair("data", data));
		return pairs;
	}

	public String createUrlWithCredentials(String baseUrl){
		List<NameValuePair> pairs = new LinkedList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", CCH_API_USER));
		pairs.add(new BasicNameValuePair("api_key", CCH_API_KEY));
		pairs.add(new BasicNameValuePair("format", "json"));
		String paramString = URLEncodedUtils.format(pairs, "utf-8");
		if(!baseUrl.endsWith("?"))
			baseUrl += "?";
		baseUrl += paramString;
		return baseUrl;
	}
}

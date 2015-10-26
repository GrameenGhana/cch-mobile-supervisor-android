package org.grameenfoundation.cch.supervisor.activity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.listener.SubmitListener;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.model.WebAppInterface;
import org.grameenfoundation.cch.supervisor.service.TrackerService;
import org.grameenfoundation.cch.supervisor.tasks.UpdateSupervisorInfoTask;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class MainActivity extends Activity implements SubmitListener   {
		
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private SharedPreferences prefs;
	
	private static final String HOME_URL = "file:///android_asset/www/cch/index.html";
	private static final String EVENT_BLANK_URL = "file:///android_asset/www/cch/modules/eventplanner/blank.html";
	private static final String EVENT_HOME_URL = "file:///android_asset/www/cch/modules/eventplanner/index.html";

	private DbHelper Db;
	private WebView myWebView;
	
	private long pageOpenTime;
    private String oldPageUrl;
    WebAppInterface webInverter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get user information
		Db = new DbHelper(getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
			
		Intent service = new Intent(this, TrackerService.class);
		Bundle tb = new Bundle();
		tb.putBoolean("backgroundData", true);
		service.putExtras(tb);
		this.startService(service);
		
		myWebView = (WebView) findViewById(R.id.webView1);	    	 
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setDomStorageEnabled(true);
	
		myWebView.addJavascriptInterface(new WebAppInterface(this,MainActivity.this), "Android");
		myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		this.setUpWebData();
	}
	
	public void setUpWebData()
	{		
		String userid = prefs.getString(getApplicationContext().getString(R.string.prefs_username), "noid"); 
		
		if (userid.equals("noid")) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();			
		} else {
			
			CCHSupervisor app = (CCHSupervisor) this.getApplication();
						
			User u = Db.getUser(userid);
	    	
			if (null == u) {
	    		startActivity(new Intent(this, LoginActivity.class));
				finish();
	    	} 
//			try{
	    
	    		if(!u.hasSupervisorInfo())
		    	{			
		    		if (app.omUpdateSupervisorInfoTask == null)
		    		{
		    			System.out.println("Pg 3");
		    			ArrayList<Object> users = new ArrayList<Object>();
		    			users.add(u);
		    			Payload p = new Payload(users);

		    			System.out.println("Pg 4");
		    			app.omUpdateSupervisorInfoTask = new UpdateSupervisorInfoTask(this);
		    			app.omUpdateSupervisorInfoTask.setUpdateSupervisorInfoListener(this);
		    			app.omUpdateSupervisorInfoTask.execute(p);

		    			System.out.println("Pg 5");
		    			return;
		    		}
		    	
			}
//			}catch(Exception e ){}
			
			this.setUpWebView();
			
		}
	}
	

	public void submitComplete(Payload response) 
	{			
		if(response.isResult()){
			User u = (User) response.getData().get(0);		
	    	Db.updateUser(u);
		} 
		
		CCHSupervisor app = (CCHSupervisor) this.getApplication();
		app.omUpdateSupervisorInfoTask = null;
		Toast.makeText(this, "Data loaded", Toast.LENGTH_LONG).show();
		System.out.println("FOund : Updating read Facility");
		System.out.println("Submit complete Main activity");
		this.setUpWebView();
	}
		
	public void setUpWebView()
	{      				
		myWebView.setWebViewClient(new WebViewClient(){
				
			     @Override
			     public void onReceivedError(WebView view, int errorCod, String description, String failingUrl) {
		            Toast.makeText(view.getContext(), description , Toast.LENGTH_LONG).show();
		         }
			    
			     @Override
		         public void onPageFinished(WebView view, String url) {
			 			 saveToLog(pageOpenTime, oldPageUrl);
			 			 oldPageUrl = url;
			 			 pageOpenTime = System.currentTimeMillis();	
			 			 
			 			 Pattern viewPattern = Pattern.compile("id=(\\d+)");
						 Matcher viewMatcher = viewPattern.matcher(url);
						 
						 if (viewMatcher.find()) {	
							 String fid = viewMatcher.group().replace("id=", "");
								System.out.println("javascript:loadData ('"+fid+"')");

							 view.loadUrl("javascript:loadData('"+fid+"')");
						 }
		         }
			    
				 @Override
			     public boolean shouldOverrideUrlLoading(WebView view, String url) {
																    
					    Pattern viewEventPattern = Pattern.compile("viewcal\\/(\\d+)");
					    Matcher viewEventMatcher = viewEventPattern.matcher(url);
					    
					    //android.util.Log.e("SupervisorMainActivity", url);
					    			    				    					    
						if (viewEventMatcher.find()) {
							    
								long calendarEventID = Long.parseLong(viewEventMatcher.group().replace("viewcal/", ""));
							   
								Intent intent = new Intent(Intent.ACTION_VIEW);

						    	intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(calendarEventID)));
						    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						    			| Intent.FLAG_ACTIVITY_SINGLE_TOP
						    			| Intent.FLAG_ACTIVITY_CLEAR_TOP
						    			| Intent.FLAG_ACTIVITY_NO_HISTORY
						    			| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						    	getApplicationContext().startActivity(intent);
						
						}  else if (url.equals("file:///android_asset/www/cch/modules/eventplanner/viewcal")) {	
							
							Intent intent =  new Intent(Intent.ACTION_VIEW);
						    intent.setData(Uri.parse("content://com.android.calendar/time"));
						    startActivity(intent);	
						    
						} else {
							
							view.loadUrl(url);
						}
											
						return true;
				}
				 
		});
		
	    oldPageUrl = "";
	    pageOpenTime = System.currentTimeMillis();
	    
	    String url = HOME_URL;
	    try 
	    {
			if (!(getIntent().getStringExtra("LOAD_URL")).isEmpty()) {	url = getIntent().getStringExtra("LOAD_URL"); }				
		} catch (NullPointerException e) {}


Log.i(TAG,"Home Urling : "+url);
		myWebView.loadUrl(url);
	}
	
	public void saveToLog(Long starttime, String url) 
	{
			if (! url.isEmpty())
			{
				String module = "Supervisor";
				Long endtime = System.currentTimeMillis();				
				Db.insertCCHLog(module, url, starttime.toString(), endtime.toString());	
			}	
	}
			
	@Override
	public void onStart() {
		super.onStart();		
	}

	@Override
	public void onResume(){
		super.onResume();
		
		String url = myWebView.getUrl();
		
		if (url != null)
		{
			if (url.equals(EVENT_BLANK_URL)) {
	 			myWebView.clearHistory();
				myWebView.loadUrl(EVENT_HOME_URL);
	 		 }
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		Db.close();
		super.onDestroy();
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (myWebView.getUrl()).equals(HOME_URL)) {
			finish();
			
		} else if ((keyCode == KeyEvent.KEYCODE_BACK) && (myWebView.getUrl()).equals(EVENT_HOME_URL)) {
			myWebView.clearHistory();
	    	myWebView.loadUrl(HOME_URL);	
      
		} else if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
	        myWebView.goBack();
    
	    } else if((keyCode == KeyEvent.KEYCODE_BACK) && !myWebView.canGoBack()) {
	    	myWebView.clearHistory();
	    	myWebView.loadUrl(HOME_URL);	        
	    } 
		
	    return true; 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_logout:
				logout();
				return true;
		}
		return true;
	}

	private void logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.logout);
		builder.setMessage(R.string.logout_confirm);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				DbHelper db = new DbHelper(MainActivity.this);
				db.onLogout();
				
				// restart the app
				MainActivity.this.startActivity(new Intent(MainActivity.this, StartupActivity.class));
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return; // do nothing
			}
		});
		builder.show();
	}
}

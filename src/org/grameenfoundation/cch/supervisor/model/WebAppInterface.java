package org.grameenfoundation.cch.supervisor.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
	
	public static final String TAG = WebAppInterface.class.getSimpleName();
   
    Context mContext;
    private DbHelper dbh;
    
    private ArrayList<MyEvent> calEvents = new ArrayList<MyEvent>();
    private ArrayList<MyDistrict> Districts = new ArrayList<MyDistrict>();

    private int pastEventsNum = 0;
    private int todaysEventsNum = 0;
    private int tomorrowsEventsNum = 0;
    private int futureEventsNum = 0;
    private String previousLocations = "";

    private int facNum = 0;
    private int nurseNum = 0;


    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c) {
        mContext = c;
        dbh = new DbHelper(c);
        //readCalendarEvent(c);
        readFacilityNurseInfo();
    }
      
    @JavascriptInterface
    public String getUsername() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return prefs.getString(mContext.getString(R.string.prefs_display_name),
    						   mContext.getString(R.string.prefs_username));
    }
    
	@JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }
	
	private String getUid() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return prefs.getString(mContext.getString(R.string.prefs_username),"0");
	}
    
    /******************************* Facility methods ***********/
  
    @JavascriptInterface
    public String getNumFacilities() {	
    	return String.valueOf(facNum) ;
    }
    
	
	@JavascriptInterface
    public String getFacilityName(String id) 
	{
		readFacilityNurseInfo();
		
		String name = "Facility: unknown name";
		
		// Find facility
    	for(MyDistrict district: Districts) {	
 		    for(MyFacility f: district.getFacilities()) {
 				if (f.facId == Long.parseLong(id)) {  return f.name; }
 		    }
 		}
    	
    	return name;
    }
	  	
	@SuppressLint("DefaultLocale")
 	@JavascriptInterface
     public String getFacilityList()
     {	
		readFacilityNurseInfo();
		
     	String facHtml = "";
     	
     	if (facNum==0) { 
     		facHtml += emptyFacilityListItemAsHTML();
     	} else {
     		for(MyDistrict district: Districts) {
     		    facHtml += "<div class=\"list-group\">" 
                        +  "    <a href=\"\" class=\"group-title\">"+district.name+"</a>"
                        +  "    <div class=\"group-content\">";
     		  
     		    for(MyFacility fac: district.getFacilities()) {
     		    		facHtml += facilityListItemAsHTML(fac);
     		    }
     		    facHtml += "</div></div>";
     		}
     	}
     	 	
     	return facHtml;
     }
	
 	 @JavascriptInterface
	 public String getFacilityEventsList(String period, String id)
	 {
			readFacilityNurseInfo();

	    	String evHtml = "";
	    	
	    	// Find facility
	    	for(MyDistrict district: Districts) {	
	 		    for(MyFacility f: district.getFacilities()) {
	 		    	if (f.facId == Long.parseLong(id)) { 
	 		    		for(MyEvent ev: f.getEvents()) {
	 		    			if (period.toLowerCase().equals("future") && ev.isFuture()) {
	 		    				evHtml += eventListItemAsHTML(ev, false, true); 
	 		    			} else if (period.toLowerCase().equals("tomorrow") && ev.isTomorrow()) {
	 		    				evHtml += eventListItemAsHTML(ev, false, true); 
	 		    			} else if (period.toLowerCase().equals("today") && ev.isToday()) {
	 		    				evHtml += eventListItemAsHTML(ev, false, true); 
	 		    			} else if (period.toLowerCase().equals("past") && ev.isPast()) {
	 		    				evHtml += eventListItemAsHTML(ev, false, true); 
	 		    			} else {}	 		    			
	 		    		} 
	 		    	}
	 		    }
	 		}
	    	
	    	if (evHtml=="") {
	    		evHtml = emptyEventListItemAsHTML();
	    	}
	    	
	    	return evHtml;
	}
 
    private String facilityListItemAsHTML(MyFacility fac)
    {
    	String subtitle = (fac.getNurses().size()==0) ? "No nurses" : fac.getNurses().size() + " nurses";
    	subtitle += (fac.getEvents().size()==0) ? ";  No events this month" : ";  " + fac.getEvents().size() + " events";
   	
    	return  "<a class=\"list\" href=\"#\">" 
             +  "  <div class=\"list-content gotoevent\" data-url=\"view.html?id="+fac.facId+"\"> " 
             +  "   <span class=\"list-title\"><span class=\"place-right\">"+fac.facType+"</span>"+fac.name+"</span>" 
             +  "   <span class=\"list-subtitle\">"+subtitle+"</span>" 
             +  "   <span class=\"list-remark\"></span>" 
             +  "  </div>"
    	     +  "</a>";    
    }

    private String emptyFacilityListItemAsHTML()
    {
    	return  "<div class=\"list-group\">" 
             +  "    <a href=\"\" class=\"group-title\"></a>"
             +  "    <div class=\"group-content\">"
    		 +	"           <a class=\"list\" href=\"#\">" 
             +  "  				<div class=\"list-content\"> " 
             +  "   				<span class=\"list-title\"><span class=\"place-right\"></span>No facilities found.</span>" 
             +  "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No facilities associated with this supervisor.</span>" 
             +  "   				<span class=\"list-remark\"></span>" 
             +  "  				</div>"
    	     +  "			</a>"
             +  "    </div>"
    	     +  "</div>";    
    }
    
    /******************************* Nurse methods ***********/
    @JavascriptInterface
    public String getNumNurses() {	
    	return String.valueOf(nurseNum) ;
    }
    
    @JavascriptInterface
	public String getNurseName(String id) 
	{
			readFacilityNurseInfo();
						
			String name = "Nurse: unknown name";
			
			// Find nurses
	    	for(MyDistrict district: Districts) {	
	 		    for(MyFacility f: district.getFacilities()) {
	 		  	    for(MyNurse n: f.getNurses())
	 		    	{
	 		    		if (String.valueOf(n.id).equals(id)) {  return n.name; }
	 		    	}
	 		    	
	 		    }
	 		}
	    	
	    	return name;
	}
		
    @SuppressLint("DefaultLocale")
 	@JavascriptInterface
     public String getNurseList()
     {	
		readFacilityNurseInfo();
		
     	String nurseHtml = "";
     	
     	if (nurseNum==0) { 
     		nurseHtml = emptyNurseListItemAsHTML();
     	} else {
     		for(MyDistrict district: Districts) {
     		    nurseHtml += "<div class=\"list-group\">" 
                        +  "    <a href=\"\" class=\"group-title\">"+district.name+"</a>"
                        +  "    <div class=\"group-content\">";
     		  
     		    ArrayList<MyNurse> nurses = new ArrayList<MyNurse>();
     		    for(MyFacility fac: district.getFacilities()) {
     		    	for(MyNurse n: fac.getNurses()) {	nurses.add(n); }
     		    }
     		    
     		    Collections.sort(nurses, new NurseNameComparator());
     		    for(MyNurse n: nurses) { nurseHtml += nurseListItemAsHTML(n); }

     		    nurseHtml += "</div></div>";
     		}
     	}
     	 	
     	return nurseHtml;
     }
	
	@SuppressLint("DefaultLocale")
 	@JavascriptInterface
     public String getNurseTargets(String id)
     {	
		readFacilityNurseInfo();
		
     	String nurseHtml = "";
     	
		// Find nurses
    	for(MyDistrict district: Districts) {	
 		    for(MyFacility f: district.getFacilities()) {
 		  	    for(MyNurse n: f.getNurses()) {
 		    		if (String.valueOf(n.id).equals(id)) {  
 		    			if (n.targets.size() > 0) {
 		    				for(MyTarget t: n.targets) {
 		    					nurseHtml += nurseTargetListItemAsHTML(t);
 		    				} 		    				
 		    			} else {
 		    				nurseHtml += emptyNurseTargetListItemAsHTML();
 		    			} 		    			
 		    		}
 		    	}
 		    }
 		}
    	
    	if (nurseHtml == "") { nurseHtml = emptyNurseTargetListItemAsHTML(); }
     	
     	return nurseHtml;
     }
	
	@SuppressLint("DefaultLocale")
 	@JavascriptInterface
     public String getNurseCourses(String id)
     {	
		readFacilityNurseInfo();
		
     	String nurseHtml = "";
     	
		// Find nurses
    	for(MyDistrict district: Districts) {	
 		    for(MyFacility f: district.getFacilities()) {
 		  	    for(MyNurse n: f.getNurses()) {
 		    		if (String.valueOf(n.id).equals(id)) {  
 		    			if (n.courses.size() > 0) {
 		    				    Collections.sort(n.courses, new CourseNameComparator());
		    					for(MyCourse c: n.courses) {
		    						nurseHtml += nurseCourseListItemAsHTML(c);
 		    					}

 		    			} else {
 		    					nurseHtml += emptyNurseCourseListItemAsHTML();
 		    			}
 		    			
 		    		}
 		    	}
 		    }
 		}
     	
     	return nurseHtml;
     }
	
	 
	@JavascriptInterface
	public String getNurseEventsList(String period, String id)
	{
			readFacilityNurseInfo();

	    	String evHtml = "";
	    	
	    	// Find facility
	    	for(MyDistrict district: Districts) {	
	 		    for(MyFacility f: district.getFacilities()) {
	 		    	for(MyNurse n: f.getNurses()) {
	 		    		if (String.valueOf(n.id).equals(id)) { 
	 		    			for(MyEvent ev: n.events) {
	 		    				if (period.toLowerCase().equals("future") && ev.isFuture()) {
	 		    					evHtml += eventListItemAsHTML(ev, false, true); 
	 		    				} else if (period.toLowerCase().equals("tomorrow") && ev.isTomorrow()) {
	 		    					evHtml += eventListItemAsHTML(ev, false, true); 
	 		    				} else if (period.toLowerCase().equals("today") && ev.isToday()) {
	 		    					evHtml += eventListItemAsHTML(ev, false, true); 
	 		    				} else if (period.toLowerCase().equals("past") && ev.isPast()) {
		 		    				evHtml += eventListItemAsHTML(ev, false, true); 
	 		    				} else {}	 		    			
	 		    			} 
	 		    		}
	 		    	}
	 		    }
	 		}
	    	
	    	if (evHtml=="") {
	    		evHtml = emptyEventListItemAsHTML();
	    	}
	    	
	    	return evHtml;
	} 
	 
    
    @JavascriptInterface
    public String getFacilityNurses(String id)
	{
			readFacilityNurseInfo();
	   
  			String nurseHtml = "";
	     	
	     	for(MyDistrict district: Districts) {
	     		    for(MyFacility fac: district.getFacilities()) {
	     		    	if (String.valueOf(fac.facId).equals(id)) {
	     		    		for(MyNurse nu: fac.getNurses()) {
	     		    			nurseHtml += nurseListItemAsHTML(nu);
	     		    		}
	     		    	}
	     		    }
	     	}
	     
	     	if (nurseHtml=="") {
	     		nurseHtml = emptyNurseListItemAsHTML();
	     	} 
	     	 	
	     	return nurseHtml;
	}
    
    private String nurseListItemAsHTML(MyNurse nu)
    {
    	String subtitle = (nu.courses.size()==0) ? "No courses" : nu.courses.size() + " courses";
    	subtitle += (nu.events.size()==0) ? ";  No events this month" : ";  " + nu.events.size() + " events";
    	subtitle += (nu.targets.size()==0) ? ";  No targets" : ";  " + nu.targets.size() + " targets";
    	String location = nu.district + ", " + nu.facility;
   	
    	return  "<a class=\"list\" href=\"#\">" 
             +  "  <div class=\"list-content gotoevent\" data-url=\"/android_asset/www/cch/modules/nurses/view.html?id="+nu.id+"\"> " 
             +  "   <span class=\"list-title\"><span class=\"place-right\"></span>"+nu.name+"</span>" 
             +  "   <span class=\"list-subtitle\">"+nu.title+" - "+location+"</span>" 
             +  "   <span class=\"list-remark\">"+subtitle+"</span>" 
             +  "  </div>"
    	     +  "</a>";    
    }
    
    private String nurseTargetListItemAsHTML(MyTarget t)
    {   	
    	String flag = (t.progress.equals("Completed")) ? "icon-flag-2 fg-green smaller" : "icon-flag-2 fg-red smaller";
    	String style = (t.progress.equals("Completed")) ? "" : "color: red;";
    	String remark = (t.progress.equals("Completed")) ? t.description : "Justification: "+t.justification;

    	return  "<a class=\"list\" id=\"target_"+t.id+"\" href=\"#\">" 
             +  "  <div class=\"list-content\">" 
             +  "   <span class=\"list-title\"><span class=\"place-right "+flag+"\"></span>"+t.target+"</span>" 
             +  "   <span class=\"list-subtitle\" style=\""+style+"\"><span class=\"place-right\"></span>"+t.progress+"</span>" 
             +  "   <span class=\"list-remark\">"+remark+"</span>" 
             +  "  </div>"
    	     +  "</a>";    
    }
    
    private String nurseCourseListItemAsHTML(MyCourse c)
    {   	  	
    	String flag = (c.status.equals("100")) ? "icon-flag-2 fg-green smaller" : "icon-flag-2 fg-red smaller";
    	String score = (c.score.equals("Not taken")) ? c.score : c.score+"%";
    	
    	return  "<a id=\"course-"+c.id+"\" class=\"list\" href=\"#\">" 
             +  "  <div class=\"list-content\"> " 
             +  "   <span class=\"list-title\"><span class=\"place-right "+flag+"\"></span>"+c.title+"</span>" 
             +  "   <span class=\"list-subtitle\"><span class=\"place-right\"></span>Final exam score: "+score+"; Attempts: "+c.attempts+"</span>" 
             +  "   <span class=\"list-remark\">"+c.status+"% complete; Last seen: "+c.last_accessed+"</span>" 
             +  "  </div>"
    	     +  "</a>";    
    }
    
    private String emptyNurseListItemAsHTML()
    {
    	return  "<div class=\"list-group\">" 
             +  "    <a href=\"\" class=\"group-title\">Nurses</a>"
             +  "    <div class=\"group-content\">"
       		 +  "           <a class=\"list\" href=\"#\">" 
             +  "  				<div class=\"list-content\"> " 
             +  "   				<span class=\"list-title\"><span class=\"place-right\"></span>No nurses found.</span>" 
             +  "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No nurses associated with this supervisor.</span>" 
             +  "   				<span class=\"list-remark\"></span>" 
             +  "  				</div>"
    	     +  "			</a>"
             +  "     </div>"
    	     +  "</div>";   
    }
    
    private String emptyNurseTargetListItemAsHTML()
    {
    	return  "           <a class=\"list\" href=\"#\">" 
             +  "  				<div class=\"list-content\"> " 
             +  "   				<span class=\"list-title\"><span class=\"place-right\"></span>No targets found.</span>" 
             +  "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No targets associated with this nurse.</span>" 
             +  "   				<span class=\"list-remark\"></span>" 
             +  "  				</div>"
    	     +  "			</a>";  
    }
    
    private String emptyNurseCourseListItemAsHTML()
    {
    	return 	"           <a class=\"list\" href=\"#\">" 
             +  "  				<div class=\"list-content\"> " 
             +  "   				<span class=\"list-title\"><span class=\"place-right\"></span>No courses found.</span>" 
             +  "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No courses associated with this nurse.</span>" 
             +  "   				<span class=\"list-remark\"></span>" 
             +  "  				</div>"
    	     +  "			</a>";  
    }
    
       
    /******************************* Event planner methods *****/

    @JavascriptInterface
    public void refreshEvents() {
    	readCalendarEvent(mContext);
    }
    
    @SuppressLint("NewApi") 
    @JavascriptInterface
    public void addEvent(String evt, String location, String desc)
    {		
    	Calendar cal = Calendar.getInstance();
		
		Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,  cal.getTimeInMillis()+60*60*1000)
		        .putExtra(Events.TITLE, evt)
		        .putExtra(Events.DESCRIPTION, desc)
		        .putExtra(Events.EVENT_LOCATION, location)
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
		 		.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false);
		       
		mContext.startActivity(intent);
    }
    
    @JavascriptInterface
    public String getNumEventsToday() {	
    	return String.valueOf(todaysEventsNum) ;
    }
    
    @JavascriptInterface
    public String getTodaysEventsSnippet() {
       String evHtml = ""; 
       int evNum = 0;
       
       if (todaysEventsNum==0) {
    	   evHtml = this.eventSnippetItemAsHTML("No planned events today.", evNum); 			  
       } else {
    	   for(MyEvent ev: calEvents){
        	   if (ev.isToday())
        	   {
        		   evHtml += this.eventSnippetItemAsHTML(ev.eventType, evNum);
        		   evNum++;
        	   }
    	   }
       }
              
       return evHtml;
   	}
    
    @SuppressLint("DefaultLocale")
	@JavascriptInterface
    public String getEventsList(String period)
    {
    	String evHtml = "";
    	
    	if (period.toLowerCase().equals("future")) {
    		  if (futureEventsNum==0) { 
    			   evHtml = emptyEventListItemAsHTML();
    		   } else {
    			   for(MyEvent ev: calEvents) {
    		     	   if (ev.isFuture()) {   evHtml += eventListItemAsHTML(ev, false, true); }
    			   }
    		   }
    	} else if (period.toLowerCase().equals("tomorrow")) {
	  		  if (tomorrowsEventsNum==0) { 
				   evHtml = emptyEventListItemAsHTML();
			   } else {
				   for(MyEvent ev: calEvents) {
			     	   if (ev.isTomorrow()) {   evHtml += eventListItemAsHTML(ev, false, false); }
				   }
			   }	
    	} else if (period.toLowerCase().equals("past")) {
	  		  if (pastEventsNum==0) { 
				   evHtml = emptyEventListItemAsHTML();
			   } else {
				   for(MyEvent ev: calEvents) {
			     	   if (ev.isPast()) {   evHtml += eventListItemAsHTML(ev, false, false); }
				   }
			   }	  
    	} else {
    		  if (todaysEventsNum==0) { 
   			   		evHtml = emptyEventListItemAsHTML();
   		   	  } else {
   			        for(MyEvent ev: calEvents){
   		     	        if (ev.isToday()) { evHtml += eventListItemAsHTML(ev, true, false); }
   			        }
   		      }
    	}
    	
    	return evHtml;
    }
    
    @JavascriptInterface
    public String getPreviousLocations() { 
    	return "{\"myLocations\": ["+previousLocations+"]}";
    }
    
        
    private String eventSnippetItemAsHTML(String event, int evNum)
    {
		if (event.length() >= 26) {   event = event.substring(0,29).concat("..."); }
    	return "<div class=\"tile-content\"><div class=\"padding10\">"+
  		       "		<p id=\"calevent"+evNum+"\" class=\"secondary-text fg-white no-margin\">"+event+"</p>"+
               "</div></div>";
    }
    
    private String eventListItemAsHTML(MyEvent ev, Boolean inclFlag, Boolean showDay)
    {
    	String dformat = (showDay)? "MMM dd" : "hh:mm a";
    	String d = ev.getDate(dformat);
    	String subtitle = (ev.location.equals("")) ? "No location specified" : ev.eventType + " at " + ev.location;
    	
    	String flag = "";
    	if (inclFlag) {
    		Calendar c = Calendar.getInstance();
    		flag = (ev.startDate <= c.getTimeInMillis()) ? "icon-flag-2 fg-red smaller" : "";
    	}
    	
    	return  "<a class=\"list\" href=\"#\">" 
             +  "  <div class=\"list-content gotoevent\" data-url=\"viewcal/"+ev.eventId+"\"> " 
             +  "   <span class=\"list-title\"><span class=\"place-right "+flag+"\"></span>"+ev.eventType+"</span>" 
             +  "   <span class=\"list-subtitle\"><span class=\"place-right\">"+d+"</span>"+subtitle+"</span>" 
             +  "   <span class=\"list-remark\">"+ev.description+"</span>" 
             +  "  </div>"
    	     +  "</a>";    
    }

    private String emptyEventListItemAsHTML()
    {
    	return  "<a class=\"list\" href=\"#\">" 
             +  "  <div class=\"list-content\"> " 
             +  "   <span class=\"list-title\"><span class=\"place-right\"></span>No events planned.</span>" 
             +  "   <span class=\"list-subtitle\"><span class=\"place-right\"></span>No events found on your calendar</span>" 
             +  "   <span class=\"list-remark\"></span>" 
             +  "  </div>"
    	     +  "</a>";    
    }

    private void addToPreviousLocations(String s)
    {
    	try {
    	   if ((! s.isEmpty()) && s.length() < 20) 
    	   {
    		   s = s.replace(",","");
    		   s = s.replace("'", "");
    		   s = s.toLowerCase(Locale.UK).trim();
    		   if (! previousLocations.contains(s)) {
        		   if (!  this.previousLocations.equals("")) { this.previousLocations += ","; }
    			   this.previousLocations += "\""+ s + "\"";
    		   }
    	   }
    	} catch (NullPointerException e) {}
    }
    
    
    /** Utility classes */
    private int hasDistrict(String name)
    {
    	for(MyDistrict d: Districts)
    	{
    		if (d.name.equals(name)) return Districts.indexOf(d);
    	}
    	
    	return -1;
    }
    
    @SuppressWarnings("unchecked")
	private void readFacilityNurseInfo() 
    {      
    	   facNum = 0;
    	   nurseNum = 0;
    	   
    	   User u = dbh.getUser(getUid());
    	   
    	   if (u!= null)
    	   {
    		   Districts.clear();
    		   
    		   String data = u.getSupervisorInfo();
   
    		   try {
    				JSONObject obj = new JSONObject(data);
    				JSONObject supervisor = obj.getJSONObject("data").getJSONObject("supervisor");
    				JSONArray facilities = supervisor.getJSONArray("facilities");
    				    				
				    Long cid = 1L;
				    Long eid = 1L;
				    
    				for(int i=0; i < facilities.length(); i++) 
    				{
    			        String dname = facilities.getJSONObject(i).getString("district");

    					// Add facility info
    					String fid = facilities.getJSONObject(i).getString("id");
    					String fname = facilities.getJSONObject(i).getString("name");
    					MyFacility facility = new MyFacility();
    			        facility.name = fname;
    			        facility.facId = Long.parseLong(fid);
    			        facility.facType = (fname.contains("CHPS")) ? "CHPS" : "HC";
    					
    			        // Get facility event info
    			        JSONArray nurses = facilities.getJSONObject(i).getJSONArray("nurses");
    			        for(int j=0; j < nurses.length(); j++)
    			        {
    			        	Long nid = Long.parseLong(nurses.getJSONObject(j).getString("id"));
    			        	String nname = nurses.getJSONObject(j).getString("first_name") + " " + nurses.getJSONObject(j).getString("last_name");
    			        	String ntitle = nurses.getJSONObject(j).getString("title");

    				        ArrayList<MyTarget> ts = new ArrayList<MyTarget>();
    				        ArrayList<MyCourse> cs = new ArrayList<MyCourse>();
    				        ArrayList<MyEvent> es = new ArrayList<MyEvent>();

    				        // Get courses	
    				        JSONObject courses = nurses.getJSONObject(j).optJSONObject("courses");
    			        	if (courses != null)
    			        	{
	    				        Iterator<String> keys = courses.keys();
	    			        	while(keys.hasNext())
	    			        	{
	    			        		 String ctitle = (String) keys.next();
	    			        		 JSONObject cinfo = courses.getJSONObject(ctitle);
	    			        		 
	    			        		 MyCourse c = new MyCourse();
		    				         c.id = cid; 
		    				         c.title = ctitle;
		    				         c.score = cinfo.getString("score"); 
		    				         c.attempts =  cinfo.getString("attempts");
		    				         //c.time_taken = cinfo.getString("time_taken");
		    				         c.last_accessed = cinfo.getString("last_accessed");
		    				         c.status = cinfo.getString("percentcomplete");
		    				         
	    			        		 /*Iterator<String> tkeys = cinfo.getJSONObject("topics").keys();
		    				         while(tkeys.hasNext())
	    			        		 {
	    			        			 MyTopic topic = new MyTopic();
	    			        			 topic.title =  (String) tkeys.next();
	    			        			 topic.last_accessed = cinfo.getJSONObject("topics").getJSONObject(topic.title).getString("last_accessed");
	    			        			 topic.time_taken = cinfo.getJSONObject("topics").getJSONObject(topic.title).getString("time_taken");
	    			        			 topic.status = cinfo.getJSONObject("topics").getJSONObject(topic.title).getString("percentcomplete");
	    			        			 c.topics.add(topic);
	    			        		 }    
	    			        		 */ 
		    				         cs.add(c);		        		 
		    				         cid = cid + 1;
	    			        	}
    			        	}
    			        	
    				        // Get targets
    				        MyTarget t = new MyTarget();
    				        t.id = 1L;
    				        t.progress = "Completed";
    				        t.target = "Gain 5 pounds";
    				        t.description = "Personal growth target";
    				        t.justification = "None needed.";
    				        ts.add(t);
    				           
    				        MyTarget t1 = new MyTarget();
    				        t1.id = 2L;
    				        t1.progress = "Missed";
    				        t1.justification = "Ebola killed most of the people.";
    				        t1.target = "Register 2000 clients";
    				        t1.description = "ANC Coverage target";
    				        ts.add(t1);
    			        	
    			        	// Get events	
    				        JSONObject calendar = nurses.getJSONObject(j).optJSONObject("calendar");
    			        	if(calendar != null)
    			        	{
	    				        Iterator<String> calkeys = calendar.keys();
	    			        	while(calkeys.hasNext())
	    			        	{
	    			        		 String caltitle = (String) calkeys.next();
	    			        		 JSONObject event = calendar.getJSONObject(caltitle);
	    			        		 
	    			        		 String etitle = event.getString("title");
	    			        		 String location = event.getString("location");
	    			        	     String type = event.getString("type");
	    			        		 Long estart = Long.parseLong(event.getString("start"));
	    			        		 Long eend = Long.parseLong(event.getString("end"));
	    			        			 
	    			        		 MyEvent ev = new MyEvent();
	    			        		 ev.eventId = eid;
	    			        		 ev.eventType = type;
	    			        		 ev.location = location;
	    			        		 ev.description = ""; 
	    			        		 ev.startDate = estart;
	    			        		 ev.endDate = eend;
	    			        		 eid = eid + 1;
	    			        		 es.add(ev);
	    			        		     
	    			        		 facility.addEvent(eid, type, location, etitle, estart, eend);    			        		 
	    			        	}
    			        	}
    			        	
    			        	nurseNum++;
    			        	facility.addNurse(nid, nname, ntitle, dname, fname, es, cs, ts);
    			        }
    			        
    			        facNum++;
    			     
    			        int exists = hasDistrict(dname);
    			        
    					if (exists >= 0) {
    						Districts.get(exists).addFacility(facility);    						
    					} else {
    						MyDistrict district = new MyDistrict(); 
    						district.name = dname;
        			        district.addFacility(facility);
        					Districts.add(district);
    					}
    					
    				}
    			} catch(JSONException e) {
    				Log.e("SupervisorMainActivity", e.getMessage());
    			}
    		       		   
    	   } else {
    		   Districts.clear(); 
    	   }
    }

    private void readCalendarEvent(Context context) 
    {
           Cursor cursor = context.getContentResolver()
                   .query(
                           Uri.parse("content://com.android.calendar/events"),
                           new String[] { "calendar_id", "title", "description",
                                   "dtstart", "dtend", "eventLocation", "_id as max_id" }, null, null, "dtstart");
           cursor.moveToFirst();
           
           // fetching calendars name
           String CNames[] = new String[cursor.getCount()];

           calEvents.clear();
           todaysEventsNum = 0;
           tomorrowsEventsNum = 0;
           futureEventsNum = 0;
           pastEventsNum = 0;
           previousLocations = "";

           Calendar c = Calendar.getInstance();
           for (int i = 0; i < CNames.length; i++) {
                CNames[i] = cursor.getString(1);

        	    long start = Long.parseLong(cursor.getString(3));
        	    long end = c.getTimeInMillis();
                
        	    try {
        		   end = Long.parseLong(cursor.getString(4));
        	    } catch(NumberFormatException e) {}
        	   
        	   MyEvent payload = new MyEvent();
        	   payload.eventId = cursor.getLong(cursor.getColumnIndex("max_id"));
        	   payload.eventType = cursor.getString(1);
        	   payload.description = cursor.getString(2);
        	   payload.startDate = start;
        	   payload.endDate = end;
        	   payload.location = cursor.getString(5);
    	       addToPreviousLocations(cursor.getString(5));
        	   calEvents.add(payload);
       	   
        	   if (payload.isToday())              { todaysEventsNum++;    } 
        	   else if (payload.isTomorrow())      { tomorrowsEventsNum++; } 
        	   else if (payload.isFuture())        { futureEventsNum++;    }
        	   else if (payload.isPast())          { pastEventsNum++; }
        	   
               cursor.moveToNext();
           }  
           
           cursor.close();
    }

    
    /*** Private classes **/
	private class MyDistrict
	{
		public String name;
	    private ArrayList<MyFacility> facilities = new ArrayList<MyFacility>();
		public void addFacility(MyFacility fac) { this.facilities.add(fac); }
		public ArrayList<MyFacility> getFacilities() 
		{
			Collections.sort(this.facilities, new FacilityNameComparator());
			return this.facilities; 
		}
	}

    private class MyFacility
	{
    		public long facId;
    		public String name;
	    	public String facType;
	    	private ArrayList<MyEvent> events = new ArrayList<MyEvent>();
	    	private ArrayList<MyNurse> nurses = new ArrayList<MyNurse>();
	    	
	    	public void addNurse(long id, String name, String title, String district, String facility, ArrayList<MyEvent> ev, ArrayList<MyCourse> c, ArrayList<MyTarget> t)
	    	{
	    		MyNurse nu = new MyNurse();
	    		nu.id = id;
	    		nu.name = name;
	    		nu.title = title;
	    		nu.district = district;
	    		nu.facility = facility;
	    		nu.events = ev;
	    		nu.courses = c;
	    		nu.targets = t;
	    		this.nurses.add(nu);
	    	}
	    	
	    	public ArrayList<MyNurse> getNurses() { 
				Collections.sort(this.nurses, new NurseNameComparator());
	    		return this.nurses; 
	    	}
	    	
	    	public void addEvent(long id, String type, String location, String desc, Long start, Long end)
	    	{
	    		if (!eventExists(type, location, desc, start, end))
	    		{
		    		MyEvent ev = new MyEvent();
		    		ev.eventId = id;
		    		ev.eventType = type;
		    		ev.location = location;
		    		ev.description = desc;
		    		ev.startDate = start;
		    		ev.endDate = end;
		    		this.events.add(ev);
	    		}
	    	}
	    	
	    	public ArrayList<MyEvent> getEvents() { return this.events; }
	
	    	private boolean eventExists(String type, String location, String desc, Long start, Long end)
	    	{	    		
	    		for(MyEvent e: this.events)
	    		{
	    			if (type.equals(e.eventType) && location.equals(e.location) && desc.equals(e.description) && start==e.startDate && end == e.endDate)
	    			{
	    				return true;
	    			}
	    		}
	    		
	    		return false;
	    	}
	}
    
    private class MyNurse
    {
    	public String name;
    	public long id;
    	public String title;
    	public String district;
    	public String facility;
    	public ArrayList<MyEvent> events = new ArrayList<MyEvent>();
    	public ArrayList<MyCourse> courses = new ArrayList<MyCourse>();
    	public ArrayList<MyTarget> targets = new ArrayList<MyTarget>();   	
    }
 	
    private class MyCourse
    {
    	public long id;
    	public String title;
    	public String status;
    	public String score;
    	public String attempts;
    	//public ArrayList<MyTopic> topics = new ArrayList<MyTopic>();
    	//public String time_taken;
    	public String last_accessed;
    }
    
    /*private class MyTopic 
    {
    	public String title;
    	public String last_accessed;
    	public String time_taken;
    	public String status;
    }*/
    
    private class MyTarget
    {
    	public long id;
    	public String target;
    	public String progress;
    	public String description;
    	public String justification;
    }
    
    	
	@SuppressLint("SimpleDateFormat") 
	private class MyEvent
	{
	    	public long eventId;
	    	public String eventType;
	    	public String location;
	    	public String description;
	    	public Long startDate;
	    	public Long endDate;
	    	
	    	public boolean isToday()
	    	{
	    			long milliSeconds = this.startDate;
	    	    	String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date(System.currentTimeMillis()));
	    	        return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
	    	       				.toString().equals(today)) ? true : false;
	    	}
	    	
	    	public boolean isTomorrow()
	    	{
	    			long milliSeconds = this.startDate;
	    	    	Calendar c = Calendar.getInstance();
	    	    	c.add(Calendar.DATE, 1);
	    	    	String tomorrow = new SimpleDateFormat("MM/dd/yyyy").format(new Date(c.getTimeInMillis()));
	    	        return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
	    	       				.toString().equals(tomorrow)) ? true : false;
	    	}
	    	    
	    	public boolean isFuture()
	    	{
	    			long milliSeconds = this.startDate;
	    	    	Calendar c = Calendar.getInstance();
	    	    	c.add(Calendar.DATE, 2);
	    	        return (milliSeconds >= c.getTimeInMillis()) ? true : false;
	    	}
	    	
	    	public boolean isPast()
	    	{
	    		long milliSeconds = this.startDate;
    	    	Calendar c = Calendar.getInstance();
    	    	c.add(Calendar.DATE, 2);
    	        return (milliSeconds < c.getTimeInMillis()) ? true : false;
	    	}
	    	
	    	/*public boolean isThisMonth() { return isThisMonth(false); }
	    	public boolean isThisMonth(boolean completed)
	    	{
	    		boolean resp = false;
	    		
	    		long milliSeconds = this.startDate;
		    	Calendar c = Calendar.getInstance();
		    	String today = new SimpleDateFormat("MM/yyyy").format(new Date(c.getTimeInMillis()));
		        
		    	// is it this month?
		    	resp =  (DateFormat.format("MM/yyyy", new Date(milliSeconds))
		       				.toString().equals(today)) ? true : false;
	 
		    	if (resp && completed)
		    	{
		    		resp =  (milliSeconds < c.getTimeInMillis()) ? true : false;
		    	}
		    	
	    		return resp;
	    	}*/
	              
	        public String getDate(String format) {
				   long milliSeconds = this.startDate;
	               SimpleDateFormat formatter = new SimpleDateFormat(format);
	               Calendar calendar = Calendar.getInstance();
	               calendar.setTimeInMillis(milliSeconds);
	               return formatter.format(calendar.getTime());
	        }
	        
	 }
	
	 /*** comparators **/  
    private class NurseNameComparator implements Comparator<MyNurse>
    {
    	public int compare(MyNurse one, MyNurse two) {
    		return one.name.compareToIgnoreCase(two.name);
    	}
    }
    
    private class FacilityNameComparator implements Comparator<MyFacility>
    {
    	public int compare(MyFacility one, MyFacility two) {
    		return one.name.compareToIgnoreCase(two.name);
    	}
    }
    
    private class CourseNameComparator implements Comparator<MyCourse>
    {
    	public int compare(MyCourse one, MyCourse two) {
    		return one.title.compareToIgnoreCase(two.title);
    	}
    }
}


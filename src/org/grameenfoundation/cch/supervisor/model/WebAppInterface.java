package org.grameenfoundation.cch.supervisor.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.activity.MainActivity;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.listener.SubmitListener;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.tasks.UpdateSupervisorInfoTask;
import org.joda.time.LocalDate;
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

public class WebAppInterface implements SubmitListener {

	public static final String TAG = WebAppInterface.class.getSimpleName();

	Context mContext;
	private DbHelper dbh;
	MainActivity mainActivity;

	private ArrayList<MyEvent> calEvents = new ArrayList<MyEvent>();
	private ArrayList<MyDistrict> Districts = new ArrayList<MyDistrict>();
	private ArrayList<MyRegion> regions = new ArrayList<MyRegion>();
	private ArrayList<MyFacility> facilities = new ArrayList<MyFacility>();

	private int pastEventsNum = 0;
	private int todaysEventsNum = 0;
	private int tomorrowsEventsNum = 0;
	private int futureEventsNum = 0;
	private String previousLocations = "";

	public String role = "-";
	private int facNum = 0;
	private int distNum = 0;
	private int regNum = 0;

	private int nurseNum = 0;

	String username;

	/**
	 * Instantiate the interface and set the context
	 */

	public WebAppInterface(Context c) {
		mContext = c;
		dbh = new DbHelper(c);
		// readCalendarEvent(c);
		readFacilityNurseInfo();
	}

	public WebAppInterface(Context c, MainActivity mainActivity) {
		mContext = c;
		dbh = new DbHelper(c);
		this.mainActivity = mainActivity;
		// readCalendarEvent(c);MY
		readFacilityNurseInfo();
	}

	@JavascriptInterface
	public String getUsername() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return prefs.getString(mContext.getString(R.string.prefs_display_name),
				mContext.getString(R.string.prefs_username));
	}

	@JavascriptInterface
	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
	}

	private String getUid() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return prefs
				.getString(mContext.getString(R.string.prefs_username), "0");
	}

	/**
	 * ***************************** Facility methods **********
	 */
	@JavascriptInterface
	public String getNumFacilities() {
		return String.valueOf(facNum);
	}

	@JavascriptInterface
	public String getNumDistrict() {
		return String.valueOf(distNum);
	}

	@JavascriptInterface
	public String getNumRegion() {
		return String.valueOf(regNum);
	}

	@JavascriptInterface
	public String getFacilityName(String id) {
		// readFacilityNurseInfo();
		processNurseFacility();
		String name = "Facility: unknown name";

		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				if (f.facId == Long.parseLong(id)) {
					return f.name;
				}
			}
		}

		return name;
	}

	@JavascriptInterface
	public void refreshUserInformation() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);

		String userid = prefs.getString(
				mContext.getString(R.string.prefs_username), "0");
		System.out.println("Refreshing for  " + userid);
		User u = dbh.getUser(userid);
		if (null == u) {
			return;
		}
		ArrayList<Object> users = new ArrayList<Object>();
		users.add(u);

		Payload p = new Payload(users);
		showToast("Refreshing for the most recent Data Please wait...");
		System.out.println("Getting most data");
		Log.i(TAG, "Getting most data");
		UpdateSupervisorInfoTask omUpdateSupervisorInfoTask = new UpdateSupervisorInfoTask(
				mContext);
		System.out.println("Get III");
		omUpdateSupervisorInfoTask
				.setUpdateSupervisorInfoListener(WebAppInterface.this);
		omUpdateSupervisorInfoTask.execute(p);
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilitiesInDistrict(String id) {
		// readFacilityNurseInfo();

		int did = Integer.parseInt(id);
		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (did == district.regId) {
					facHtml += "<div class=\"list-group\">"
							+ "    <a href=\"\" class=\"group-title\">"
							+ district.name + "</a>"
							+ "    <div class=\"group-content\">";

					// facHtml += districtListItemAsHTML(district);

					for (MyFacility fac : district.getFacilities()) {

						facHtml += facilityListItemAsHTML(fac);
					}
					// facHtml += "</div></div>";
				}
			}
		}

		return facHtml;
	}

	@JavascriptInterface
	public String getRole() {
		return role;
	}

	@JavascriptInterface
	public String getRoleDetail() {
		if (role.equalsIgnoreCase("Not Set")) {
			processNurseFacility();
		}
		System.out.println("RolerCoaster : " + role);
		if (role.toLowerCase().contains("district")) {
			return "district";
		} else if (role.toLowerCase().contains("nation")) {
			return "nation";
		} else if (role.toLowerCase().contains("region")) {
			return "region";
		} else if (role.equalsIgnoreCase("Not Set")) {
			return "not-set";
		}
		return "district";

	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilityList() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				facHtml += "<div class=\"list-group\">"
						+ "    <a href=\"\" class=\"group-title\">"
						+ district.name + "</a>"
						+ "    <div class=\"group-content\">";

				// facHtml += districtListItemAsHTML(district);

				for (MyFacility fac : district.facilities) {
					facHtml += facilityListItemAsHTML(fac);
				}
				// facHtml += "</div></div>";
			}
		}

		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getDistrictName(String id) {
		processNurseFacility();
		String facHtml = "";

		int did = Integer.parseInt(id);
		for (MyDistrict district : Districts) {
			if (did == district.regId) {
				return district.name;
			}
		}

		return facHtml;

	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getRegionName(String id) {
		processNurseFacility();
		String facHtml = "";

		int did = Integer.parseInt(id);
		for (MyRegion district : regions) {
			if (did == district.id) {
				return district.name;
			}
		}

		return facHtml;

	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getRegionList() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyRegionListItemAsHTML();
		} else {
			for (MyRegion district : regions) {
				facHtml += regionListHTML(district);
			}
		}

		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getRegionGraph() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyRegionListItemAsHTML();
		} else {
			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyRegion district : regions) {
				grp.add(new MyGroupItem(district.name, district.name, "reg_id"));
			}

			facHtml += dbh.getGraphCourseDetail("Region", grp, "reg_id",
					"count", "reg_id", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getRegionGraphFinalQuiz() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyRegionListItemAsHTML();
		} else {
			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyRegion district : regions) {
				grp.add(new MyGroupItem(district.name, district.name, "reg_id"));
			}

			facHtml += dbh.getGraphCourseDetailMean("Region", grp, "score",
					"count", "score", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getRegionGraphCompleted() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyRegionListItemAsHTML();
		} else {
			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyRegion district : regions) {
				grp.add(new MyGroupItem(district.name, district.name, "reg_id"));
			}

			facHtml += dbh.getGraphCourseDetailMean("Region", grp,
					"percentage_complete", "count", "percentage_complete", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilityInDistrictList(String id) {
		// readFacilityNurseInfo();

		int did = Integer.parseInt(id);
		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (did == district.regId) {
					facHtml += "<div class=\"list-group\">"
							+ "    <a href=\"\" class=\"group-title\">"
							+ district.name + "</a>"
							+ "    <div class=\"group-content\">";

					// facHtml += districtListItemAsHTML(district);

					for (MyFacility fac : district.getFacilities()) {
						facHtml += facilityListItemAsHTML(fac);
					}
					// facHtml += "</div></div>";
				}
			}
		}

		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilityInDistrictListGraph(String id) {
		// readFacilityNurseInfo();

		int did = Integer.parseInt(id);
		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (did == district.regId) {
							List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			

					for (MyFacility fac : district.getFacilities()) {
						//facHtml += facilityListItemAsHTML(fac);
						grp.add(new MyGroupItem(String.valueOf(fac.facId),
								fac.name, "facility_id"));
					}
					facHtml += dbh.getGraphCourseDetail("Facility", grp, "facility_id",
							"count", "facility_id", "");

				}
			}
		}

		return facHtml;
	}
	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilityInDistrictListGraphCompleted(String id) {
		// readFacilityNurseInfo();

		int did = Integer.parseInt(id);
		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (did == district.regId) {
							List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			

					for (MyFacility fac : district.getFacilities()) {
						//facHtml += facilityListItemAsHTML(fac);
						grp.add(new MyGroupItem(String.valueOf(fac.facId),
								fac.name, "facility_id"));
					}
					
					facHtml += dbh
							.getGraphCourseDetailMean("Facility", grp,
									"percentage_complete", "count",
									"percentage_complete", "");

				}
			}
		}

		return facHtml;
	}
	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getFacilityInDistrictListGraphFinalScore(String id) {
		// readFacilityNurseInfo();

		int did = Integer.parseInt(id);
		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyFacilityListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (did == district.regId) {
							List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			

					for (MyFacility fac : district.getFacilities()) {
//						facHtml += facilityListItemAsHTML(fac);
						grp.add(new MyGroupItem(String.valueOf(fac.facId),
								fac.name, "facility_id"));
					}
					facHtml += dbh
							.getGraphCourseDetailMean("District", grp,
									"score", "count",
									"score", "");

				}
			}
		}

		return facHtml;
	}

	@JavascriptInterface
	public String districtListInRegion(String id) {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";
		int k = 0;
		System.out.println("Inside Now  getDistrictListInRegion " + id);
		int did = Integer.parseInt(id);
		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				if (district.region.id == did) {
					facHtml += districtListItemAsHTML(district,
							"../district/view.html");
					k++;
				}
			}
		}
		System.out.println("K___- : " + k);
		return facHtml;
	}
	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String districtListInRegionGraph(String id) {
		// readFacilityNurseInfo();
System.out.println("Inside Now  getDistrictListInRegion Start " + id);
		processNurseFacility();
		String facHtml = "";
		int k = 0;
		
		int did = Integer.parseInt(id);
		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {

			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyDistrict district : Districts) {
				if (district.region.id == did) {
					grp.add(new MyGroupItem(String.valueOf(district.regId),
							district.name, "dist_id"));
				}
			}

			facHtml += dbh.getGraphCourseDetail("District", grp, "dist_id",
					"count", "dist_id", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;

	}

	@JavascriptInterface
	public String districtListInRegionGraphCompleted(String id) {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";
		int k = 0;
		System.out.println("Inside Now  districtListInRegionGraphCompleted " + id);
		int did = Integer.parseInt(id);
		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {

			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyDistrict district : Districts) {
				if (district.region.id == did) {

				grp.add(new MyGroupItem(String.valueOf(district.regId),
						district.name, "dist_id"));
				}			}

			facHtml += dbh
					.getGraphCourseDetailMean("District", grp,
							"percentage_complete", "count",
							"percentage_complete", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;

	}
	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String districtListInRegionGraphFinalScore(String id) {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";
		int k = 0;
		System.out.println("Inside Now  getDistrictListInRegion " + id);
		int did = Integer.parseInt(id);
		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {

			List<MyGroupItem> grp = new ArrayList<WebAppInterface.MyGroupItem>();
			for (MyDistrict district : Districts) {
				if (district.region.id == did) {
				grp.add(new MyGroupItem(String.valueOf(district.regId),
						district.name, "dist_id"));
				}}

			facHtml += dbh.getGraphCourseDetailMean("District", grp, "score",
					"count", "score", "");
		}

		System.out.println("final Return : " + facHtml);
		return facHtml;

	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getDistrictListItem() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				facHtml += districtListItemAsHTML(district);
			}
		}

		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getDistrictCntItem() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (distNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				facHtml += districtListItemAsHTML(district);
			}
		}

		return facHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getDistrictList() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String facHtml = "";

		if (facNum == 0) {
			facHtml += emptyDistrictListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				facHtml += "<div class=\"list-group\">"
						+ "    <a href=\"\" class=\"group-title\">"
						+ district.name + "</a>"
						+ "    <div class=\"group-content\">";

				for (MyFacility fac : district.getFacilities()) {
					facHtml += facilityListItemAsHTML(fac);
				}
				facHtml += "</div></div>";
			}
		}

		return facHtml;
	}

	@JavascriptInterface
	public String getFacilityEventsList(String period, String id) {
		// readFacilityNurseInfo();

		processNurseFacility();
		String evHtml = "";

		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				if (f.facId == Long.parseLong(id)) {
					for (MyEvent ev : f.events) {
						if (period.toLowerCase().equals("future")
								&& ev.isFuture()) {
							evHtml += eventListItemAsHTML(ev, false, true,true);
						} else if (period.toLowerCase().equals("tomorrow")
								&& ev.isTomorrow()) {
							evHtml += eventListItemAsHTML(ev, false, true,true);
						} else if (period.toLowerCase().equals("today")
								&& ev.isToday()) {
							evHtml += eventListItemAsHTML(ev, false, true,true);
						} else if (period.toLowerCase().equals("pastlastmonth")
								&& ev.isPastLastMonth()) {
							evHtml += eventListItemAsHTML(ev, true, true,true);
						}else if (period.toLowerCase().equals("pastthismonth")
								&& ev.isPastThisMonth()) {
							evHtml += eventListItemAsHTML(ev, true, true,true);
						}else if (period.toLowerCase().equals("yesterday")
								&& ev.isYesterday()) {
							evHtml += eventListItemAsHTML(ev, true, true,true);
						} else {
						}
					}
				}
			}
		}

		if (evHtml == "") {
			evHtml = emptyEventListItemAsHTML();
		}

		return evHtml;
	}

	private String facilityListItemAsHTML(MyFacility fac) {
		String subtitle = (fac.nurses.size() == 0) ? "No nurses" : fac.nurses
				.size() + " nurses";

		subtitle += (fac.supervisors.size() == 0) ? "; No Supervisor" : "; "
				+ fac.supervisors.size() + " Supervisors";
		subtitle += (fac.events.size() == 0) ? ";  No events" : ";  "
				+ fac.events.size() + " events";
		subtitle += (fac.targets.size() == 0) ? ";  No Targets" : ";  "
				+ fac.targets.size() + " targets";

		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content gotoevent\" data-url=\"../facilities/view.html?id="
				+ fac.facId + "\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right\">"
				+ fac.facType + "</span>" + fac.name + "</span>"
				+ "   <span class=\"list-subtitle\">" + subtitle + "</span>"
				+ "   <span class=\"list-remark\"></span>" + "  </div>"
				+ "</a>";
	}

	private String districtListItemAsHTML(MyDistrict fac) {

		return districtListItemAsHTML(fac, "view.html");
	}

	private String districtListItemAsHTML(MyDistrict fac, String url) {
		String subtitle = (fac.facilities.size() == 0) ? "No Facilities"
				: fac.facilities.size() + " Facilities";
		subtitle += (fac.nursesCnt == 0) ? ";  No Nurses " : ";  "
				+ fac.nursesCnt + " Nurses ";
		subtitle += (fac.supervisorCnt == 0) ? ";  No Supervisors" : ";  "
				+ fac.supervisorCnt + " Supervisors ;";
		subtitle += "<br />";
		subtitle += (fac.targetNo == 0) ? " No Target" : " " + fac.targetNo
				+ " targets";
		subtitle += (fac.eventCnt == 0) ? ";  No Events" : ";  " + fac.eventCnt
				+ " Events";

		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content gotoevent\" data-url=\"" + url
				+ "?id=" + fac.regId + "\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right\">"
				+ "</span>" + fac.name + "</span>"
				+ "   <span class=\"list-subtitle\">" + subtitle + "</span>"
				+ "   <span class=\"list-remark\"></span>" + "  </div>"
				+ "</a>";
	}

	private String regionListHTML(MyRegion fac) {
		String subtitle = (fac.districts.size() == 0) ? "No Districts"
				: fac.districts.size() + " Districts";
		subtitle += (fac.facilityNo == 0) ? "; No Facilities" : "; "
				+ fac.facilityNo + " Facilities ";
		subtitle += (fac.nursesCnt == 0) ? "; No Nurses ; " : ";  "
				+ fac.nursesCnt + " Nurses  ";
		subtitle += "<br />";
		subtitle += (fac.supervisorCnt == 0) ? ";  No Supervisors; " : "   "
				+ fac.supervisorCnt + " Supervisors; ";
		subtitle += (fac.targetNo == 0) ? "; No Target" : "   " + fac.targetNo
				+ " targets";
		subtitle += (fac.eventCnt == 0) ? ";  No Events" : " ; " + fac.eventCnt
				+ " Events";

		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content gotoevent\" data-url=\"view.html?id="
				+ fac.id + "\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right\">"
				+ "</span>" + fac.name + "</span>" + "<div>"
				+ "   <span class=\"list-subtitle\">" + subtitle + "</span>"
				+ "   <span class=\"list-remark\"></span>" + " </div> </div>"
				+ "</a>";
	}

	private String emptyFacilityListItemAsHTML() {
		return "<div class=\"list-group\">"
				+ "    <a href=\"\" class=\"group-title\"></a>"
				+ "    <div class=\"group-content\">"
				+ "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No facilities found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No facilities associated with this supervisor.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>" + "    </div>" + "</div>";
	}

	private String emptyDistrictListItemAsHTML() {
		return "<div class=\"list-group\">"
				+ "    <a href=\"\" class=\"group-title\"></a>"
				+ "    <div class=\"group-content\">"
				+ "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No District found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No District associated with this supervisor.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>" + "    </div>" + "</div>";
	}

	private String emptyRegionListItemAsHTML() {
		return "<div class=\"list-group\">"
				+ "    <a href=\"\" class=\"group-title\"></a>"
				+ "    <div class=\"group-content\">"
				+ "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No Region found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No Region associated with this supervisor.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>" + "    </div>" + "</div>";
	}

	/**
	 * ***************************** Nurse methods **********
	 */
	@JavascriptInterface
	public String getNumNurses() {
		return String.valueOf(nurseNum);
	}

	@JavascriptInterface
	public String getNurseName(String id) {
		readFacilityNurseInfo();

		String name = "Nurse: unknown name";

		// Find nurses
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyNurse> nurses = f.nurses;
				nurses.addAll(f.supervisors);
				for (MyNurse n : nurses) {
					if (String.valueOf(n.id).equals(id)) {
						return n.name;
					}
				}

			}
		}

		return name;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getNurseList() {
		// readFacilityNurseInfo();

		processNurseFacility();
		String nurseHtml = "";

		if (nurseNum == 0) {
			nurseHtml = emptyNurseListItemAsHTML();
		} else {
			for (MyDistrict district : Districts) {
				nurseHtml += "<div class=\"list-group\">"
						+ "    <a href=\"\" class=\"group-title\">"
						+ district.name + "</a>"
						+ "    <div class=\"group-content\">";

				ArrayList<MyNurse> nurses = new ArrayList<MyNurse>();
				for (MyFacility fac : district.getFacilities()) {
					for (MyNurse n : fac.nurses) {
						nurses.add(n);
					}
				}

				Collections.sort(nurses, new NurseNameComparator());
				for (MyNurse n : nurses) {
					nurseHtml += nurseListItemAsHTML(n);
				}

				nurseHtml += "</div></div>";
			}
		}

		return nurseHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getNurseTargets(String id) {
		// readFacilityNurseInfo();

		String nurseHtml = "";

		// Find nurses
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyNurse> nurses = f.nurses;
				nurses.addAll(f.supervisors);
				for (MyNurse n : nurses) {

					if (String.valueOf(n.id).equals(id)) {

						if (n.targets.size() > 0) {
							System.out.println("Target Size : "
									+ n.targets.size());

							for (MyTarget t : n.targets) {
								nurseHtml += nurseTargetListItemAsHTML(t);
							}
						} else {
							nurseHtml += emptyNurseTargetListItemAsHTML();
						}
					}
				}
			}
		}

		if (nurseHtml == "") {
			nurseHtml = emptyNurseTargetListItemAsHTML();
		}

		System.out.println("Nurse TargetList : " + nurseHtml);
		return nurseHtml;
	}

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getNurseCourses(String id) {
		// readFacilityNurseInfo();

		String nurseHtml = "";

		// Find nurses
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				List<MyNurse> nurses = f.nurses;
				nurses.addAll(f.supervisors);
				for (MyNurse n : nurses) {
					if (String.valueOf(n.id).equals(id)) {
						if (n.courses.size() > 0) {
							Collections.sort(n.courses,
									new CourseNameComparator());

							System.out.println("NurseId  " + id
									+ " Courses Count : " + n.courses.size());
							for (MyCourse c : n.courses) {
								nurseHtml += nurseCourseListItemAsHTML(c);
							}
						} else {
							nurseHtml = emptyNurseCourseListItemAsHTML();
						}

						return nurseHtml;
					}
				}
			}
		}

		// System.out.println("Nurse List : "+nurse);
		return nurseHtml;
	}

	@JavascriptInterface
	public String getNurseTargetList(String type, String id) {
		// readFacilityNurseInfo();

		String evHtml = "";
		String[] categories = { "event", "coverage", "learning" };
		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				List<MyNurse> nurses = f.nurses;
				nurses.addAll(f.supervisors);
				for (MyNurse n : nurses) {
					if (String.valueOf(n.id).equals(id)) {
						for (MyTarget ev : n.targets) {
							if (type.equalsIgnoreCase("other")) {
								boolean isOther = true;
								for (String category : categories) {
									if (category.toLowerCase().equals(
											ev.category))
										isOther = false;
								}
								if (isOther)
									evHtml += nurseTargetListItemAsHTML(ev);
							} else if (type.toLowerCase().equals(ev.category)) {
								evHtml += nurseTargetListItemAsHTML(ev);
							}
						}
					}
				}
			}
		}

		if (evHtml == "") {
			evHtml = emptyNurseTargetListItemAsHTML();
		}

		return evHtml;
	}

	@JavascriptInterface
	public String getNurseEventsList(String period, String id) {
		// readFacilityNurseInfo();

		String evHtml = "";

		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				List<MyNurse> nurses = f.nurses;
				nurses.addAll(f.supervisors);
				for (MyNurse n : nurses) {
					if (String.valueOf(n.id).equals(id)) {
						List<MyEvent> evs = n.events;
						for (MyEvent ev : evs) {
							if (period.toLowerCase().equals("future")
									&& ev.isFuture()) {
								evHtml += eventListItemAsHTML(ev, false, true,false);
							} else if (period.toLowerCase().equals("tomorrow")
									&& ev.isTomorrow()) {
								evHtml += eventListItemAsHTML(ev, false, true,false);
							} else if (period.toLowerCase().equals("today")
									&& ev.isToday()) {
								evHtml += eventListItemAsHTML(ev, false, true,false);
							} else if (period.toLowerCase().equals("pastlastmonth")
									&& ev.isPastLastMonth()) {
								evHtml += eventListItemAsHTML(ev, true, true,false);
							} else if (period.toLowerCase().equals("pastthismonth")
									&& ev.isPastThisMonth()) {
								evHtml += eventListItemAsHTML(ev, true, true,false);
							}else if (period.toLowerCase().equals("yesterday")
									&& ev.isYesterday()) {
								evHtml += eventListItemAsHTML(ev, true, true,false);
							}else {
							}
						}
					}
				}
			}
		}

		if (evHtml == "") {
			evHtml = emptyEventListItemAsHTML();
		}

		return evHtml;
	}

	@JavascriptInterface
	public String getFacilityNurses(String id) {
		// readFacilityNurseInfo();

		String nurseHtml = "";

		for (MyDistrict district : Districts) {
			for (MyFacility fac : district.facilities) {
				if (String.valueOf(fac.facId).equals(id)) {
					for (MyNurse nu : fac.nurses) {
						nurseHtml += nurseListItemAsHTML(nu);
					}
				}
			}
		}

		if (nurseHtml == "") {
			nurseHtml = emptyNurseListItemAsHTML();
		}

		return nurseHtml;
	}

	@JavascriptInterface
	public String getFacilitySupervisors(String id) {
		// readFacilityNurseInfo();

		String nurseHtml = "";

		for (MyDistrict district : Districts) {
			for (MyFacility fac : district.getFacilities()) {
				if (String.valueOf(fac.facId).equals(id)) {
					for (MyNurse nu : fac.supervisors) {
						nurseHtml += nurseListItemAsHTML(nu);
					}
				}
			}
		}

		if (nurseHtml == "") {
			nurseHtml = emptySupervisorListItemAsHTML();
		}

		return nurseHtml;
	}

	private String nurseListItemAsHTML(MyNurse nu) {
		String subtitle = (nu.courses.size() == 0) ? "No courses" : nu.courses
				.size() + " courses";
		subtitle += (nu.events.size() == 0) ? ";  No events this month" : ";  "
				+ nu.events.size() + " events";
		subtitle += (nu.targets.size() == 0) ? ";  No targets" : ";  "
				+ nu.targets.size() + " targets";
		String location = nu.district + ", " + nu.facility;

		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content gotoevent\" data-url=\"/android_asset/www/cch/modules/nurses/view.html?id="
				+ nu.id
				+ "\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right\"></span>"
				+ nu.name + "</span>" + "   <span class=\"list-subtitle\">"
				+ nu.title + " - " + location + "</span>"
				+ "   <span class=\"list-remark\">" + subtitle + "</span>"
				+ "  </div>" + "</a>";
	}

	private String nurseTargetListItemAsHTML(MyTarget t) {
		int tar = (t.target == 0) ? 1 : t.target;
		double percentageCompleted = Math
				.ceil(((t.achieved * 1.0) / tar) * 100);
		if (percentageCompleted >= 100)
			percentageCompleted = 100.00;
		String flag = (t.achieved > 0 && t.achieved == t.target) ? "icon-flag-2 fg-green smaller"
				: "icon-flag-2 fg-red smaller";
		String style = (percentageCompleted >= 80) ? "green "
				: (percentageCompleted >= 50) ? "yellow " : "red ";

		String remark = (t.achieved >= 0 && t.achieved == t.target) ? t.justification
				: "Justification: " + t.justification;
		String type = (t.type.isEmpty()) ? "No Type " : t.type;

		return "<a class=\"list\" id=\"target_"
				+ t.id
				+ "\" href=\"#\">"
				+ "  <div class=\"list-content\">"
				+ "   <span class=\"list-title\"><span class=\"place-right icon-flag-2 fg-"
				+ style
				+ " smaller "
				+ "\"></span>"
				+ t.target
				+ " "
				+ type
				+ "</span>"
				+ "   <span class=\"list-subtitle\" style=\"color:"
				+ style
				+ "\"><span class=\"place-right\"></span>"
				+ Math.ceil(((t.achieved * 1.0) / tar) * 100)
				+ "% <em> ~ ("
				+ t.achieved
				+ ") Completed</em></span>"
				+ "<div class='bg-grey' style='width:100%:height:5px;clear:both;background-color:#f0f0f0;margin-bottom:3px;'>"
				+ "<div class='' style='background-color:" + style + ";width:"
				+ percentageCompleted
				+ "%;height:5px;'>&nbsp;</div></div>"
				+ "   <span class=\"list-remark\">"
				+ remark
				+ "</span>"
				// + "<span class=\"place-right\" >" + t.startDate + " -"+
				// t.dueDate + "</span>"
				+ "<span class=\"list-remark\"><span class=\"place-right\">"
				+ t.category + "</span> " + t.getStartDate() + " -"
				+ t.getDueDate() + "</span>" + "  </div>" + "</a>";
	}

	private String nurseCourseListItemAsHTML(MyCourse c) {
		String percentageCompletion = c.status;
		percentageCompletion = (percentageCompletion.equalsIgnoreCase("")) ? "0"
				: percentageCompletion;
		int perCompletion = Integer.parseInt(percentageCompletion);
		int finalScore = 0;
		String flag = (finalScore >= 80) ? "green "
				: (finalScore >= 50) ? "yellow " : "red ";
		String scoreColor = (perCompletion >= 80) ? "green "
				: (perCompletion >= 50) ? "yellow " : "red ";
		String score = (c.score.equals("Not taken")) ? c.score : c.score + "%";
		if ((c.score.equals("Not taken"))) {
			finalScore = 0;
			flag = "red ";
		} else {
			try {
				finalScore = Integer.parseInt(score.replace("%", ""));
			} catch (Exception e) {
				System.out.println("Score Error :(" + score + ")"
						+ e.getLocalizedMessage());
			}

			flag = (finalScore >= 80) ? "green "
					: (finalScore >= 50) ? "yellow " : "wine ";
		}
		return "<a id=\"course-"
				+ c.id
				+ "\" class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right icon-flag-2 fg-"
				+ flag
				+ " smaller "
				+ "\"></span>"
				+ c.title
				+ "</span>"
				+ "   <span class=\"list-subtitle\"><span class=\"place-right\"></span>Final exam score: "
				+ score + "; Attempts: " + c.attempts + "</span>"
				+ "   <span class=\"list-remark\">" + c.status
				+ " % complete;</span>"
				+ "<div class='' style='background-color:" + scoreColor
				+ ";width:" + perCompletion + "%;height:5px;'>&nbsp;</div>"
				+ "<span class=\"list-remark\"> Last seen: " + c.last_accessed
				+ "</span>" + "  </div>" + "</a>";
	}

	private String emptyNurseListItemAsHTML() {
		return ""
				+ "    "
				+ "    <div class=\"group-content\">"
				+ "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No nurses found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No nurses associated with this supervisor.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "	" + "     " + "";
	}

	private String emptySupervisorListItemAsHTML() {
		return "<div class=\"list-group\">"
				+ "    <a href=\"\" class=\"group-title\">Supervisor</a>"
				+ "    <div class=\"group-content\">"
				+ "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No Supervisor found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No supervisor.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>" + "     </div>" + "</div>";
	}

	private String emptyNurseTargetListItemAsHTML() {
		return "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No targets found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No targets associated with this nurse.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>";
	}

	private String emptyNurseCourseListItemAsHTML() {
		return "           <a class=\"list\" href=\"#\">"
				+ "  				<div class=\"list-content\"> "
				+ "   				<span class=\"list-title\"><span class=\"place-right\"></span>No courses found.</span>"
				+ "   				<span class=\"list-subtitle\"><span class=\"place-right\"></span>No courses associated with this nurse.</span>"
				+ "   				<span class=\"list-remark\"></span>" + "  				</div>"
				+ "			</a>";
	}

	/**
	 * ***************************** Event planner methods ****
	 */
	@JavascriptInterface
	public void refreshEvents() {
		readCalendarEvent(mContext);
	}

	@SuppressLint("NewApi")
	@JavascriptInterface
	public void addEvent(String evt, String location, String desc) {
		Calendar cal = Calendar.getInstance();

		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setData(Events.CONTENT_URI)
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						cal.getTimeInMillis())
				.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
						cal.getTimeInMillis() + 60 * 60 * 1000)
				.putExtra(Events.TITLE, evt).putExtra(Events.DESCRIPTION, desc)
				.putExtra(Events.EVENT_LOCATION, location)
				.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
				.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

		mContext.startActivity(intent);
	}

	@JavascriptInterface
	public String getNumEventsToday() {
		return String.valueOf(todaysEventsNum);
	}

	@JavascriptInterface
	public String getTodaysEventsSnippet() {
		String evHtml = "";
		int evNum = 0;

		if (todaysEventsNum == 0) {
			evHtml = this.eventSnippetItemAsHTML("No planned events today.",
					evNum);
		} else {
			for (MyEvent ev : calEvents) {
				if (ev.isToday()) {
					evHtml += this.eventSnippetItemAsHTML(ev.eventType, evNum);
					evNum++;
				}
			}
		}

		return evHtml;
	}
	
	@JavascriptInterface
	public String getEventName(String id) {
		
		String name = "unknown name";
		System.out.println("Got id:"+id);
		
		// Find event
				for (MyDistrict district : Districts) {
					for (MyFacility f : district.getFacilities()) {
						List<MyEvent> events = f.events;
						for (MyEvent ev : events) {
							if (String.valueOf(ev.eventId).equals(id)) {
								return ev.eventType;
							}
						}

					}
				}

		
		return name;
	}
	
	@JavascriptInterface
	public String getEventLocation(String id) {
		
		String name = "unknown location";

		// Find event
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyEvent> events = f.events;
				for (MyEvent ev : events) {
				if (ev.eventId == Long.parseLong(id)) {
					return ev.location;
				}
			
				}

			}
		}


return name;
}
	
	@JavascriptInterface
	public String getEventDate(String id) {
		
		String name = "unknown date";


		// Find event
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyEvent> events = f.events;
				for (MyEvent ev : events) {
				if (ev.eventId == Long.parseLong(id)) {
					return ev.getDate("EEE, d MMM yyyy HH:mm:ss");
				}
			
		}

	}
}


return name;
}
	
	@JavascriptInterface
	public String getEventDescription(String id) {
		
		String name = "unknown description";


		// Find event
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyEvent> events = f.events;
				for (MyEvent ev : events) {
				if (ev.eventId == Long.parseLong(id)) {
					return ev.description;
				}
			
		}

	}
}


return name;
}
	
	@JavascriptInterface
	public String getEventJustification(String id) {
		
		String name = "no justification";


		// Find event
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyEvent> events = f.events;
				for (MyEvent ev : events) {
				if (ev.eventId == Long.parseLong(id)) {
					if(null != ev.justification){
					    return ev.justification;
					}else{
						return name;
					}
				}
			
		}

	}
}


return name;
}
	
	@JavascriptInterface
	public String getEventComments(String id) {
		
		String name = "no comments";


		// Find event
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.getFacilities()) {
				List<MyEvent> events = f.events;
				for (MyEvent ev : events) {
				if (ev.eventId == Long.parseLong(id)) {
					if(null!= ev.comments){
					    return ev.comments;
					}else{
						return name;
					}
				}
			
		}

	}
}


return name;
}
	
	
	@JavascriptInterface
	public String getEventNurseName(String id) {
		System.out.println("Got id ->"+ id );
		
		String response = "not available";
         
		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				List<MyNurse> nurses = f.nurses;
				for (MyNurse n : nurses) {
						List<MyEvent> evs = n.events;
						for (MyEvent ev : evs) {
							
							if (ev.eventId == Long.parseLong(id)) {
								System.out.println("Got event of nurse ->" +n.name);
								return n.name;
								
							}
						}
					
				}
			}
		}

		

		return response;
	}
	
	@JavascriptInterface
	public String getEventNurseFacility(String id) {
		System.out.println("Got id ->"+ id );
		
		String response = "not available";
         
		// Find facility
		for (MyDistrict district : Districts) {
			for (MyFacility f : district.facilities) {
				List<MyNurse> nurses = f.nurses;
				for (MyNurse n : nurses) {
						List<MyEvent> evs = n.events;
						for (MyEvent ev : evs) {
							
							if (ev.eventId == Long.parseLong(id)) {
								System.out.println("Got event of nurse ->" +n.facility);
								return n.facility;
								
							}
						}
					
				}
			}
		}

		

		return response;
	}
	
	

	@SuppressLint("DefaultLocale")
	@JavascriptInterface
	public String getEventsList(String period) {
		String evHtml = "";

		if (period.toLowerCase().equals("future")) {
			if (futureEventsNum == 0) {
				evHtml = emptyEventListItemAsHTML();
			} else {
				for (MyEvent ev : calEvents) {
					if (ev.isFuture()) {
						evHtml += eventListItemAsHTML(ev, false, true,true);
					}
				}
			}
		} else if (period.toLowerCase().equals("tomorrow")) {
			if (tomorrowsEventsNum == 0) {
				evHtml = emptyEventListItemAsHTML();
			} else {
				for (MyEvent ev : calEvents) {
					if (ev.isTomorrow()) {
						evHtml += eventListItemAsHTML(ev, false, false,true);
					}
				}
			}
		} else if (period.toLowerCase().equals("past")) {
			if (pastEventsNum == 0) {
				evHtml = emptyEventListItemAsHTML();
			} else {
				for (MyEvent ev : calEvents) {
					if (ev.isPast()) {
						evHtml += eventListItemAsHTML(ev, false, false,true);
					}
				}
			}
		} else {
			if (todaysEventsNum == 0) {
				evHtml = emptyEventListItemAsHTML();
			} else {
				for (MyEvent ev : calEvents) {
					if (ev.isToday()) {
						evHtml += eventListItemAsHTML(ev, true, false,true);
					}
				}
			}
		}

		return evHtml;
	}

	@JavascriptInterface
	public String getPreviousLocations() {
		return "{\"myLocations\": [" + previousLocations + "]}";
	}

	private String eventSnippetItemAsHTML(String event, int evNum) {
		if (event.length() >= 26) {
			event = event.substring(0, 29).concat("...");
		}
		return "<div class=\"tile-content\"><div class=\"padding10\">"
				+ "		<p id=\"calevent" + evNum
				+ "\" class=\"secondary-text fg-white no-margin\">" + event
				+ "</p>" + "</div></div>";
	}

	private String eventListItemAsHTML(MyEvent ev, Boolean inclFlag,
			Boolean showDay,Boolean facilityEvent) {
		String dformat = (showDay) ? "MMM dd" : "hh:mm a";
		String d = ev.getDate(dformat);
		String subtitle = (ev.location.equals("")) ? "No location specified"
				: ev.eventType + " at " + ev.location;

		String flag = "";
		if (inclFlag) {
			Calendar c = Calendar.getInstance();
			if(ev.startDate <= c.getTimeInMillis()){
			
			if(ev.status.equals("complete")){
				
				flag =  "icon-checkmark fg-green ";
				
			}else if(ev.status.equals("incomplete")){
				
				flag =  "icon-cancel fg-red ";
				
			}else{
				flag =  "icon-help fg-yellow ";
			}
			
			}
			
			
			
		}
		subtitle = (ev.description == "") ? ev.description : subtitle;
		
		if(facilityEvent){
		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content gotoevent\" data-url=\"../eventplanner/view.html?id="
				+ ev.eventId + "\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right "
				+ flag + "\"></span>" + ev.eventType + "</span>"
				+ "   <span class=\"list-remark\">" + ev.eventType + " at "
				+ ev.location + " " + " <span class=\"place-right\">" + d
				+ "		</span>" + "   </span>" 
				+ "   <span class=\"list-remark\" style=\"color:#d80073; \">" + getEventNurseName(Long.toString(ev.eventId)) +  "</span>" + "</div></a>";
		}else{
			return "<a class=\"list\" href=\"#\">"
					+ "  <div class=\"list-content gotoevent\" data-url=\"../eventplanner/view.html?id="
					+ ev.eventId + "\"> "
					+ "   <span class=\"list-title\"><span class=\"place-right "
					+ flag + "\"></span>" + ev.eventType + "</span>"
					+ "   <span class=\"list-remark\">" + ev.eventType + " at "
					+ ev.location + " " + " <span class=\"place-right\">" + d
					+ "		</span>" + "   </span> </div></a>" ;
		}
	}

	private String emptyEventListItemAsHTML() {
		return "<a class=\"list\" href=\"#\">"
				+ "  <div class=\"list-content\"> "
				+ "   <span class=\"list-title\"><span class=\"place-right\"></span>No events planned.</span>"
				+ "   <span class=\"list-subtitle\"><span class=\"place-right\"></span>No events found on your calendar</span>"
				+ "   <span class=\"list-remark\"></span>" + "  </div>"
				+ "</a>";
	}

	private void addToPreviousLocations(String s) {
		try {
			if ((!s.isEmpty()) && s.length() < 20) {
				s = s.replace(",", "");
				s = s.replace("'", "");
				s = s.toLowerCase(Locale.UK).trim();
				if (!previousLocations.contains(s)) {
					if (!this.previousLocations.equals("")) {
						this.previousLocations += ",";
					}
					this.previousLocations += "\"" + s + "\"";
				}
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Utility classes
	 */
	private int hasDistrict(String name) {
		for (MyDistrict d : Districts) {
			if (d.name.equals(name)) {
				return Districts.indexOf(d);
			}
		}

		return -1;
	}

	private int hasRegion(String name) {
		for (MyRegion d : regions) {
			if (d.name.equals(name)) {
				return regions.indexOf(d);
			}
		}

		return -1;
	}

	private int hasDistrictInRegion(int did, int region) {

		for (MyDistrict district : regions.get(region).districts) {
			if (district.regId == did) {
				return regions.get(region).districts.indexOf(did);
			}
		}

		return -1;
	}

	public void processNurseFacility() {
		if (Districts.isEmpty()) {
			calEvents = new ArrayList<MyEvent>();
			Districts = new ArrayList<MyDistrict>();
			regions = new ArrayList<MyRegion>();
			facilities = new ArrayList<MyFacility>();

			System.out.println("District is empty");
			readFacilityNurseInfo();
			// mainActivity.setUpWebView();
		}
	}

	
	public List<Farmer> processJSON(String data){
		JSONObject  obj =  new JSONObject();
		List<Farmer> myFarmers = new ArrayList<Farmer>();
		try {
			JSONArray farmers  = obj.getJSONArray("farmers");
			int farmersCnt=0;
		
			for(int i=0;i<farmersCnt;i++){
				JSONObject farmer  = farmers.getJSONObject(i);
				Farmer f = dbh.saveFarmer(farmer.getString("fname"),farmer.getString("lastName"),farmer.getString("nickname"),farmer.getString("community"),farmer.getString("village"),farmer.getString("district"),farmer.getString("region"),farmer.getString("age"),farmer.getString("gender"),farmer.getString("maritalStatus"),farmer.getString("numberOfChildren"),farmer.getString("numberOfDependants"),farmer.getString("education"),farmer.getString("cluster"),farmer.getString("farmID"));
				myFarmers.add(f);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return myFarmers;
	} 
	
	
	@SuppressWarnings("unchecked")
	public void readFacilityNurseInfo() {
		facNum = 0;
		nurseNum = 0;
		distNum = 0;
		regNum = 0;

		List<TargetDetails> targetSummary = new ArrayList<WebAppInterface.TargetDetails>();
		targetSummary.add(new TargetDetails(""));

		User u = dbh.getUser(getUid());

		if (u != null) {
			Districts.clear();
			regions.clear();
			dbh.resetData();
			String data = u.getSupervisorInfo();

			try {
				JSONObject obj = new JSONObject(data);
				JSONObject supervisor = obj.getJSONObject("data")
						.getJSONObject("supervisor");
				username = supervisor.getString("name");
				role = supervisor.getString("role");

				System.out.println("Role : " + role);
				JSONArray facilities = supervisor.getJSONArray("facilities");

				Long cid = 1L;
				Long eid = 1L;

				for (int i = 0; i < facilities.length(); i++) {
					String dname = facilities.getJSONObject(i).getString(
							"district");
					String did = facilities.getJSONObject(i).getString("did");
					String rname = facilities.getJSONObject(i).getString(
							"region");

					// Add facility info
					String fid = facilities.getJSONObject(i).getString("id");
					String fname = facilities.getJSONObject(i)
							.getString("name");
					MyFacility facility = new MyFacility();
					facility.name = fname;
					facility.facId = Long.parseLong(fid);
					facility.facType = (fname.contains("CHPS")) ? "CHPS" : "HC";

					// dbh.facilityAdd(Integer.parseInt(String.valueOf(fid)),
					// fname, rname,"),farmer.getString("acility.facType, dname, "");
					// Get facility event info
					JSONArray nurses = facilities.getJSONObject(i)
							.getJSONArray("nurses");
					for (int j = 0; j < nurses.length(); j++) {
						Long nid = Long.parseLong(nurses.getJSONObject(j)
								.getString("id"));

						String nname = nurses.getJSONObject(j).getString(
								"first_name")
								+ " "
								+ nurses.getJSONObject(j)
										.getString("last_name");
						String ntitle = nurses.getJSONObject(j).getString(
								"title");
						String nurseRole = nurses.getJSONObject(j).getString(
								"role");
						String ischn = nurses.getJSONObject(j).getString(
								"ischn");

						// dbh.nurseAdd(
						// fid,
						// nid,
						// nurses.getJSONObject(j).getString("username"),
						// nurses.getJSONObject(j).getString("first_name"),
						// nurses.getJSONObject(j).getString("first_name"),
						// nurses.getJSONObject(j).getString("gender"),
						// nurses.getJSONObject(j).getString(
						// "phone_number"), nurses
						// .getJSONObject(j).getString("group"),
						// nurses.getJSONObject(j).getString("role"),
						// nurses.getJSONObject(j).getString("title"),
						// nurses.getJSONObject(j).getString("ischn"),
						// nurses.getJSONObject(j).getString("device_id"),
						// nurses.getJSONObject(j).getString("status"),
						// nurses.getJSONObject(j).getString("myfac"));
						ArrayList<MyTarget> ts = new ArrayList<MyTarget>();
						ArrayList<MyCourse> cs = new ArrayList<MyCourse>();
						ArrayList<MyEvent> es = new ArrayList<MyEvent>();

						// Get courses
						JSONObject courses = nurses.getJSONObject(j)
								.optJSONObject("courses");
						if (courses != null) {
							Iterator<String> keys = courses.keys();
							while (keys.hasNext()) {
								String ctitle = (String) keys.next();
								JSONObject cinfo = courses
										.getJSONObject(ctitle);

								MyCourse c = new MyCourse();
								c.id = cid;
								c.title = ctitle;
								c.score = cinfo.getString("score");
								c.attempts = cinfo.getString("attempts");
								// c.time_taken = cinfo.getString("time_taken");
								c.last_accessed = cinfo
										.getString("last_accessed");
								c.status = cinfo.getString("percentcomplete");

								c.numOfAttempts = Integer
										.parseInt((c.attempts == "") ? "0"
												: c.attempts);
								c.finalScore = Integer
										.parseInt((c.score == "" || c.score
												.equalsIgnoreCase("Not taken")) ? "0"
												: c.score);
								c.percentageCompletion = Integer
										.parseInt((c.status == "") ? "0"
												: c.status);
								dbh.courseAdd(c.title, nid, facility.facId,
										facility.name, Integer.parseInt(did),
										dname, rname, c.attempts, c.score, "",
										c.last_accessed, c.status);
								/*
								 * Iterator<String> tkeys =
								 * cinfo.getJSONObject("topics").keys();
								 * while(tkeys.hasNext()) { MyTopic topic = new
								 * MyTopic(); topic.title = (String)
								 * tkeys.next(); topic.last_accessed =
								 * cinfo.getJSONObject
								 * ("topics").getJSONObject(topic
								 * .title).getString("last_accessed");
								 * topic.time_taken =
								 * cinfo.getJSONObject("topics"
								 * ).getJSONObject(topic
								 * .title).getString("time_taken"); topic.status
								 * =
								 * cinfo.getJSONObject("topics").getJSONObject(
								 * topic.title).getString("percentcomplete");
								 * c.topics.add(topic); }
								 */
								cs.add(c);
								cid = cid + 1;
							}
						}

						// Get targetst
						// MyTarget t = new MyTarget();
						// t.id = 1L;
						// t.progress = "Completed";
						// t.target = "Gain 5 pounds";
						// t.description = "Personal growth target";
						// t.justification = "None needed.";
						// ts.add(t);
						//
						// MyTarget t1 = new MyTarget();
						// t1.id = 2L;
						// t1.progress = "Missed";
						// t1.justification =
						// "Ebola killed most of the people.";
						// t1.target = "Register 2000 clients";
						// t1.description = "ANC Coverage target";
						// ts.add(t1);

						JSONObject targets = nurses.getJSONObject(j)
								.optJSONObject("targets");
						if (targets != null) {
							Iterator<String> targetkeys = targets.keys();

							System.out.println("Nurse : " + nname);
							while (targetkeys.hasNext()) {
								String caltitle = (String) targetkeys.next();
								JSONObject target = targets
										.getJSONObject(caltitle);

								String tr = target.getString("target");
								String ach = target.getString("achieved");
								tr = (tr.equalsIgnoreCase("")) ? "0" : tr;
								ach = (ach.equalsIgnoreCase("")) ? "0" : ach;

								int achieved = Integer.parseInt(ach);
								System.out.println("T : " + tr + " Ach : "
										+ ach);
								int tar =0 ;
								try{
									 tar = Integer.parseInt(tr);
								}catch(Exception ex){
									
								}
								
								String type = target.getString("type");
								String category = target.getString("category");
								String justification = target
										.getString("justification");
								String start = target.getString("start");
								String end = target.getString("end");
								long id = 0;
								
								try {
								 id = Long
										.parseLong(target.getString("id"));
								}catch(Exception ex){
									
								}
								
								//
								if (!category.equalsIgnoreCase("other")) {
									MyTarget myTarget = new MyTarget();

									myTarget.id = id;
									myTarget.achieved = achieved;
									myTarget.target = tar;
									myTarget.type = type;
									myTarget.category = category;
									myTarget.startDate = start;
									myTarget.dueDate = end;
									myTarget.justification = justification;
									ts.add(myTarget);
									facility.addTarget(myTarget);

									// dbh.targetAdd(fid, String.valueOf(nid),
									// String.valueOf(id), category, tar,
									// achieved, justification, start,
									// end, type);
								}
							}
						} else {
							System.out.println("Targets Null : " + j);

						}
						// Get events
						JSONObject calendar = nurses.getJSONObject(j)
								.optJSONObject("calendar");
						if (calendar != null) {
							Iterator<String> calkeys = calendar.keys();
							while (calkeys.hasNext()) {
								String caltitle = (String) calkeys.next();
								JSONObject event = calendar
										.getJSONObject(caltitle);

								String etitle = event.getString("title");
								String location = event.getString("location");
								String type = event.getString("type");
								Long estart = Long.parseLong(event
										.getString("start"));
								Long eend = Long.parseLong(event
										.getString("end"));
								
								String justification = "no justification";
								try {
								if(null != event.optString("justification")) { justification = event.getString("justification"); }
								}catch(Exception ex){
									//Log.e("WebAppInterface", ex.getMessage());
								}
								
								String comments = "no comments";
								try{
								if(null != event.optString("comments")) { comments = event.getString("comments"); }
								}catch(Exception ex){
									//Log.e("WebAppInterface", ex.getMessage());
								}
								
								String status = "unkown";
								try{
								if(null != event.optString("status")) { status = event.getString("status"); }
								}catch(Exception ex){
									//Log.e("WebAppInterface", ex.getMessage());
								}

								MyEvent ev = new MyEvent();
								ev.eventId = eid;
								ev.eventType = type;
								ev.location = location;
								ev.description = "";
								ev.startDate = estart;
								ev.endDate = eend;
								ev.nurseId = nid;
								ev.justification = justification;
								ev.comments = comments;
								ev.status = status;
								eid = eid + 1;
								es.add(ev);
								// dbh.eventAdd(String.valueOf(nid), (fid),
								// String.valueOf(ev.eventId), location,
								// type, estart, eend);
								facility.addEvent(eid, type, location, etitle,
										estart, eend,justification,comments,status);
							}
							facility.events = facility.getEvents();
						}

						nurseNum++;
						facility.addNurse(nid, nname, ntitle, dname, fname,
								ischn, nurseRole, es, cs, ts);
					}

					facNum++;

					int regionExist = hasRegion(rname);
					if (regionExist >= 0) {
					} else {
						regNum++;
						MyRegion region = new MyRegion();
						region.name = rname;
						region.id = regNum;
						regions.add(region);
						// dbh.regionAdd(rname);
						regionExist = hasRegion(rname);
					}
					int exists = hasDistrict(dname);
					MyDistrict dist = null;
					if (exists >= 0) {
						dist = Districts.get(exists);
						Districts.get(exists).addFacility(facility);
					} else {
						MyDistrict district = new MyDistrict();
						district.name = dname;
						district.regId = Integer.parseInt(did);
						district.addFacility(facility);
						district.region = regions.get(regionExist);
						Districts.add(district);
						distNum++;
						dist = district;
						// dbh.districtAdd(Integer.parseInt(did), dname);
					}

					// if (regionExist >= 0) {
					// int exits = hasDistrictInRegion(Integer.parseInt(did),
					// regionExist);
					// if (exists >= 0) {
					// regions.get(regionExist).districts.get(exits)
					// .addFacility(facility);
					// } el se {
					// regions.get(regionExist).addDistrict(dist);
					// }
					// }

				}
				int d = 0;
				for (MyDistrict district : Districts) {
					Districts.get(d).facilities = Districts.get(d)
							.getFacilities();
					int cnt = 0;
					for (MyRegion region : regions) {
						if (region.id == district.region.id) {
							regions.get(cnt).addDistrict(district);
						}
						cnt++;
					}
				}
			} catch (JSONException e) {
				Log.e("SupervisorMainActivity", e.getMessage());
			}
			// myDistricts = dbh.getDistrictData();

		} else {
			Districts.clear();
		}
	}

	private void readCalendarEvent(Context context) {
		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://com.android.calendar/events"),
				new String[] { "calendar_id", "title", "description",
						"dtstart", "dtend", "eventLocation", "_id as max_id" },
				null, null, "dtstart");
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
			} catch (NumberFormatException e) {
			}

			MyEvent payload = new MyEvent();
			payload.eventId = cursor.getLong(cursor.getColumnIndex("max_id"));
			payload.eventType = cursor.getString(1);
			payload.description = cursor.getString(2);
			payload.startDate = start;
			payload.endDate = end;
			payload.location = cursor.getString(5);
			addToPreviousLocations(cursor.getString(5));
			calEvents.add(payload);

			if (payload.isToday()) {
				todaysEventsNum++;
			} else if (payload.isTomorrow()) {
				tomorrowsEventsNum++;
			} else if (payload.isFuture()) {
				futureEventsNum++;
			} else if (payload.isPast()) {
				pastEventsNum++;
			}

			cursor.moveToNext();
		}

		cursor.close();
	}

	/**
	 * 
	 * 
	 *
	 */
	public class MyGroupItem {
		public String id;
		public String name;
		public String fieldName;

		public MyGroupItem(String id, String name, String fieldname) {
			this.id = id;
			this.name = name;
			this.fieldName = fieldname;
			// TODO Auto-generated constructor stub
		}

	}

	/**
	 * * Private classes *
	 */
	private class MyDistrict {
		public int regId;
		public String name;
		public int targetNo = 0;
		public int nursesCnt = 0;
		public int supervisorCnt = 0;
		public int eventCnt = 0;
		public int courseCnt = 0;
		public MyRegion region;

		List<TargetDetails> targetDetails = new ArrayList<WebAppInterface.TargetDetails>();
		public ArrayList<MyFacility> facilities = new ArrayList<MyFacility>();

		public void addFacility(MyFacility fac) {
			this.facilities.add(fac);
			eventCnt += fac.events.size();
			nursesCnt += fac.nurses.size();
			supervisorCnt += fac.supervisors.size();

			targetNo += fac.targets.size();
			System.out.println("Distr fac No :" + fac.name + "  -  " + targetNo
					+ " (" + name + ")");

		}

		public ArrayList<MyFacility> getFacilities() {
			Collections.sort(this.facilities, new FacilityNameComparator());
			return this.facilities;
		}
	}

	private class MyRegion {

		public String name;
		public int id;

		private ArrayList<MyDistrict> districts = new ArrayList<MyDistrict>();

		public void addDistrict(MyDistrict fac) {
			// int found = 1;
			// for (MyDistrict myDistrict : districts) {
			// if (myDistrict.regId == fac.regId) {
			// found = 0;
			// }
			// }
			// if (found == 1) {
			districts.add(fac);
			targetNo += fac.targetNo;
			eventCnt += fac.eventCnt;
			nursesCnt += fac.nursesCnt;
			supervisorCnt += fac.supervisorCnt;
			facilityNo += fac.facilities.size();
			// }
		}

		List<TargetDetails> targetDetails = new ArrayList<WebAppInterface.TargetDetails>();
		public int targetNo;
		public int targetAchieved;
		public int facilityNo = 0;
		public int nursesCnt = 0;
		public int supervisorCnt = 0;
		public int eventCnt = 0;
		public int targetCnt = 0;

		public ArrayList<MyDistrict> getDistricts() {
			Collections.sort(this.districts, new DistrictNameComparator());
			return this.districts;
		}
	}

	private class MyFacility {

		public long facId;
		public String name;
		public String facType;
		public int nursesCnt = 0;
		public int eventCnt = 0;
		public int targetCnt = 0;
		// 0263974262

		public ArrayList<MyEvent> events = new ArrayList<MyEvent>();
		public ArrayList<MyNurse> nurses = new ArrayList<MyNurse>();
		public ArrayList<MyNurse> supervisors = new ArrayList<MyNurse>();
		public ArrayList<MyTarget> targets = new ArrayList<MyTarget>();

		public void addNurse(long id, String name, String title,
				String district, String facility, String ischn, String role,
				ArrayList<MyEvent> ev, ArrayList<MyCourse> c,
				ArrayList<MyTarget> t) {
			MyNurse nu = new MyNurse();
			nu.id = id;
			nu.name = name;
			nu.title = title;
			nu.district = district;
			nu.facility = facility;
			nu.events = ev;
			nu.courses = c;
			nu.targets = t;
			nu.events = nu.getEvents();
			this.nursesCnt++;
			if (role.contains("Nurse")) {
				nu.title = "Community Health Nurse";
				this.nurses.add(nu);
			} else {
				nu.title = role;
				this.supervisors.add(nu);
			}
		}

		public ArrayList<MyNurse> getNurses() {
			Collections.sort(this.nurses, new NurseNameComparator());
			return this.nurses;
		}

		public ArrayList<MyNurse> getSupervisors() {
			Collections.sort(this.supervisors, new NurseNameComparator());
			return this.supervisors;
		}

		public void addTarget(MyTarget target) {

			this.targets.add(target);
			this.targetCnt++;
		}

		public boolean targetExist(MyTarget target) {
			for (MyTarget tg : targets) {
				if (tg.id == target.id)
					return true;
			}
			return false;
		}

		public void addEvent(long id, String type, String location,
				String desc, Long start, Long end,String justification,String comments,String status) {
			if (!eventExists(type, location, desc, start, end)) {
				MyEvent ev = new MyEvent();
				ev.eventId = id;
				ev.eventType = type;
				ev.location = location;
				ev.description = desc;
				ev.startDate = start;
				ev.endDate = end;
				ev.justification = justification;
				ev.comments = comments;
				ev.status = status;
				this.eventCnt++;
				this.events.add(ev);
			}
		}

		public ArrayList<MyEvent> getEvents() {
			Collections.sort(this.events, new EventTimeComparator());
			return this.events;
		}

		public ArrayList<MyTarget> getTargets() {
			Collections.sort(this.targets, new TargetTimeComparator());

			return this.targets;
		}

		private boolean eventExists(String type, String location, String desc,
				Long start, Long end) {
			for (MyEvent e : this.events) {
				if (type.equals(e.eventType) && location.equals(e.location)
						&& desc.equals(e.description) && start == e.startDate
						&& end == e.endDate  ) {
					return true;
				}
			}
			return false;
		}
	}

	private class MyNurse {

		public String name;
		public long id;
		public String title;
		public String district;
		public String facility;
		public ArrayList<MyEvent> events = new ArrayList<MyEvent>();
		public ArrayList<MyCourse> courses = new ArrayList<MyCourse>();
		public ArrayList<MyTarget> targets = new ArrayList<MyTarget>();

		public ArrayList<MyEvent> getEvents() {
			Collections.sort(this.events, new EventTimeComparator());

			return this.events;
		}
	}

	private class MySupervisor {

		public String name;
		public long id;
		public String title;
		public String district;
		public String facility;
		public ArrayList<MyEvent> events = new ArrayList<MyEvent>();
		public ArrayList<MyCourse> courses = new ArrayList<MyCourse>();
		public ArrayList<MyTarget> targets = new ArrayList<MyTarget>();

		public ArrayList<MyEvent> getEvents() {
			Collections.sort(this.events, new EventTimeComparator());

			return this.events;
		}
	}

	private class TargetDetails {

		public String name;
		public long number;
		public long completed;
		public long notCompleted;

		public TargetDetails(String name) {
			this.name = name;
			this.number = 0;
			this.completed = 0;
			this.notCompleted = 0;
		}

		public TargetDetails(String name, int number, int completed,
				int notCompleted) {

			this.name = name;
			this.number = number;
			this.completed = completed;
			this.notCompleted = notCompleted;
		}

	}

	private class MyCourse {

		public long id;
		public String title;
		public String status;
		public String score;
		public String attempts;
		int percentageCompletion;
		int numOfAttempts;
		int finalScore;
		// public ArrayList<MyTopic> topics = new ArrayList<MyTopic>();
		// public String time_taken;
		public String last_accessed;
	}

	/*
	 * private class MyTopic { public String title; public String last_accessed;
	 * public String time_taken; public String status; }
	 */
	private class MyTarget {

		public long id;
		public int target;
		public int achieved;
		public String type;
		public String justification;
		public String dueDate;
		public String startDate;
		public long time;
		public String category;

		public String getStartDate() {
			System.out.println("Start Date : " + startDate);
			return formatDate(startDate);
		}

		public long getTime() {
			String format = "MMM dd, yyy ";
			java.util.Date date = new java.util.Date();

			try {
				date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
						.parse(startDate);
			} catch (Exception e) {
				return 0l;
			}

			System.out.println("Date time : " + date.getTime());
			return date.getTime();
			// return 0l;
		}

		public String formatDate(String strDate) {
			String format = "MMM dd, yyy ";
			java.util.Date date = new java.util.Date();

			try {
				date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
						.parse(strDate);
			} catch (Exception e) {
				return "No-Date";
			}

			long milliSeconds = date.getTime();
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			return formatter.format(calendar.getTime());
		}

		public String getDueDate() {
			return formatDate(dueDate);
		}

	}

	@SuppressLint("SimpleDateFormat")
	private class MyEvent {

		public Object status;
		public String justification;
		public String comments;
		public long eventId;
		public String eventType;
		public String location;
		public String description;
		public Long startDate;
		public Long endDate;
		public long nurseId;
		public long facilityId;

		public boolean isToday() {
			long milliSeconds = this.startDate;
			String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date(
					System.currentTimeMillis()));
			return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
					.toString().equals(today)) ? true : false;
		}
		
		public boolean isYesterday()
    	{
    		boolean result;
    			LocalDate previous = new LocalDate(this.startDate);
		    			LocalDate now = new LocalDate().minusDays(1);
	    				
		    			if((now.getDayOfMonth()==previous.getDayOfMonth())
		    					&&(now.getMonthOfYear()==previous.getMonthOfYear()
		    					&&(now.getYear()==previous.getYear()))){
    				result= true;
    			}else {
    				result= false;
    			}
    			
    			return result;
    	}

		public boolean isTomorrow() {
			long milliSeconds = this.startDate;
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 1);
			String tomorrow = new SimpleDateFormat("MM/dd/yyyy")
					.format(new Date(c.getTimeInMillis()));
			return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
					.toString().equals(tomorrow)) ? true : false;
		}

		public boolean isFuture() {
			long milliSeconds = this.startDate;
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DATE, 2);
			return (milliSeconds >= c.getTimeInMillis()) ? true : false;
		}

		public boolean isPast() {
			long milliSeconds = this.startDate;
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);

			return (milliSeconds < c.getTimeInMillis()) ? true : false;
		}
		
		public boolean isPastThisMonth() {
			boolean result=false;
			long milliSeconds = this.startDate;
			LocalDate previous = new LocalDate(this.startDate);
			LocalDate now = new LocalDate().minusDays(2);
			 	    	
			if((previous.getDayOfMonth()<=now.getDayOfMonth())
				&&(previous.getMonthOfYear()==now.getMonthOfYear())
				&&(previous.getYear()==now.getYear())){
				result= true;
			}else {
				result= false;
			}
		
			return result;
			
			}
		
		public boolean isPastLastMonth() {
			boolean result = false;
			long milliSeconds = this.startDate;
			LocalDate previous = new LocalDate(this.startDate);
		 	LocalDate lastmonth = new LocalDate().minusMonths(1);
					
					if((previous.getMonthOfYear()==lastmonth.getMonthOfYear())
							&&(previous.getYear()==lastmonth.getYear())){
			if(previous.getMonthOfYear()==lastmonth.getMonthOfYear()){
				result= true;
			}else {
				result= false;
			}
			
			}
		
			return result;
			}

		/*
		 * public boolean isThisMonth() { return isThisMonth(false); } public
		 * boolean isThisMonth(boolean completed) { boolean resp = false;
		 * 
		 * long milliSeconds = this.startDate; Calendar c =
		 * Calendar.getInstance(); String today = new
		 * SimpleDateFormat("MM/yyyy").format(new Date(c.getTimeInMillis()));
		 * 
		 * // is it this month? resp = (DateFormat.format("MM/yyyy", new
		 * Date(milliSeconds)) .toString().equals(today)) ? true : false;
		 * 
		 * if (resp && completed) { resp = (milliSeconds < c.getTimeInMillis())
		 * ? true : false; }
		 * 
		 * return resp; }
		 */
		public String getDate(String format) {
			long milliSeconds = this.startDate;
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(milliSeconds);
			return formatter.format(calendar.getTime());
		}

	}

	/**
	 * * comparators *
	 */
	private class NurseNameComparator implements Comparator<MyNurse> {

		public int compare(MyNurse one, MyNurse two) {
			return one.name.compareToIgnoreCase(two.name);
		}
	}

	private class TargetTimeComparator implements Comparator<MyTarget> {

		public int compare(MyTarget one, MyTarget two) {
			return one.getTime() < two.getTime() ? -1 : one.getTime() > two
					.getTime() ? 1 : 0;
		}
	}

	private class EventTimeComparator implements Comparator<MyEvent> {

		public int compare(MyEvent one, MyEvent two) {
			return one.startDate < two.startDate ? -1
					: one.startDate > two.startDate ? 1 : 0;
		}
	}

	private class FacilityNameComparator implements Comparator<MyFacility> {

		public int compare(MyFacility one, MyFacility two) {
			return one.name.compareToIgnoreCase(two.name);
		}
	}

	private class DistrictNameComparator implements Comparator<MyDistrict> {

		public int compare(MyDistrict one, MyDistrict two) {
			return one.name.compareToIgnoreCase(two.name);
		}
	}

	private class CourseNameComparator implements Comparator<MyCourse> {

		public int compare(MyCourse one, MyCourse two) {
			return one.title.compareToIgnoreCase(two.title);
		}
	}

	public void submitComplete(Payload response) {
		if (response.isResult()) {
			User u = (User) response.getData().get(0);
			showToast("Data Successfully Loaded");
			DbHelper dbh = new DbHelper(mContext);
			dbh.updateUser(u);
			dbh.resetData();
			readFacilityNurseInfo();
			System.out.println("After Reload");
			mainActivity.setUpWebView();
		}

		System.out.println("Submit complete WebAppInterface");
	}

}

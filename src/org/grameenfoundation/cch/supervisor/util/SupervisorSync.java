package org.grameenfoundation.cch.supervisor.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.model.District;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.model.WebAppInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SupervisorSync {

	DbHelper dbh;
	String user;

List<District> myDistricts;

	public SupervisorSync(DbHelper helper) {
		this.dbh = helper;
		myDistricts = new ArrayList<District>();
	}

	@SuppressWarnings("unchecked")
	public void readFacilityNurseInfo() {
		int facNum = 0;
		int nurseNum = 0;

		User u = dbh.getUser(user);

		if (u != null) {

			String data = u.getSupervisorInfo();

			try {
				JSONObject obj = new JSONObject(data);
				JSONObject supervisor = obj.getJSONObject("data")
						.getJSONObject("supervisor");
				String username = supervisor.getString("name");
				String role = supervisor.getString("name");

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

					long facId = Long.parseLong(fid);
					String facType = (fname.contains("CHPS")) ? "CHPS" : "HC";

					dbh.facilityAdd(Integer.parseInt(String.valueOf(fid)),
							fname, rname, facType, dname, "");
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

						dbh.nurseAdd(
								fid,
								nid,
								nurses.getJSONObject(j).getString("username"),
								nurses.getJSONObject(j).getString("first_name"),
								nurses.getJSONObject(j).getString("first_name"),
								nurses.getJSONObject(j).getString("gender"),
								nurses.getJSONObject(j).getString(
										"phone_number"), nurses
										.getJSONObject(j).getString("group"),
								nurses.getJSONObject(j).getString("role"),
								nurses.getJSONObject(j).getString("title"),
								nurses.getJSONObject(j).getString("ischn"),
								nurses.getJSONObject(j).getString("device_id"),
								nurses.getJSONObject(j).getString("status"),
								nurses.getJSONObject(j).getString("myfac"));

						// Get courses
						JSONObject courses = nurses.getJSONObject(j)
								.optJSONObject("courses");
						if (courses != null) {
							Iterator<String> keys = courses.keys();
							while (keys.hasNext()) {
								String ctitle = (String) keys.next();
								JSONObject cinfo = courses
										.getJSONObject(ctitle);

//								dbh.courseAdd(ctitle, nid, facId,
//										cinfo.getString("attempts"),
//										cinfo.getString("scorce"), "",
//										cinfo.getString("last_accessed"),
//										cinfo.getString("percentcomplete"));

								cid = cid + 1;
							}
						}

						// Get targets

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
								int tar = Integer.parseInt(tr);
								String type = target.getString("type");
								String category = target.getString("category");
								String justification = target
										.getString("justification");
								String start = target.getString("start");
								String end = target.getString("end");
								long id = Long
										.parseLong(target.getString("id"));
								//
								if (!category.equalsIgnoreCase("other")) {

									dbh.targetAdd(fid, String.valueOf(nid),
											String.valueOf(id), category, tar,
											achieved, justification, start,
											end, type);
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
								;
								dbh.eventAdd(String.valueOf(nid), (fid),
										String.valueOf(eid), location, type,
										estart, eend);

							}
						}

						nurseNum++;

					}

					facNum++;
					dbh.districtAdd(Integer.parseInt(did), dname);
					dbh.regionAdd(rname);

				}
			} catch (JSONException e) {
				Log.e("SupervisorMainActivity", e.getMessage());
			}
//			myDistricts = dbh.getDistrictData(); 

		} else {

		}
	}

}

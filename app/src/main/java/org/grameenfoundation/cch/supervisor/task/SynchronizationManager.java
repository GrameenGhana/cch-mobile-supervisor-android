package org.grameenfoundation.cch.supervisor.task;

import android.util.Log;

import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Course;
import org.grameenfoundation.cch.supervisor.model.District;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.model.Facility;
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.model.Payload;
import org.grameenfoundation.cch.supervisor.model.Region;
import org.grameenfoundation.cch.supervisor.model.Target;
import org.grameenfoundation.cch.supervisor.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SynchronizationManager {
    private static final String TAG = SynchronizationManager.class.getSimpleName();

    private static final SynchronizationManager INSTANCE = new SynchronizationManager();
    private static final SynchronizationManager SERVICE_INSTANCE = new SynchronizationManager(false);
    private Map<String, SynchronizationListener> synchronizationListenerList = new HashMap<>();

    private Supervisor app;
    private boolean showDialog = true;
    private boolean synchronizing = false;

    private int maxSynchronizationSteps = 0;
    private int currentSynchronizationStep = 1;

    public SynchronizationManager() { app = (Supervisor) Supervisor.mAppContext; }
    public SynchronizationManager(Boolean showDialog) { this.showDialog = showDialog; }

    public static SynchronizationManager getInstance() { return INSTANCE; }
    public static SynchronizationManager getServiceInstance() { return SERVICE_INSTANCE; }

    public synchronized void start() {

        if (this.isSynchronizing())
            return;

        /* starts a new thread to begin the synchronization. The synchronization manager */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notifySynchronizationListeners("synchronizationStart");
                    synchronizing = true;

                    maxSynchronizationSteps = 6;
                    currentSynchronizationStep = 1;

                    uploadLogs();
                    updateLocations();
                    updateNurses();
                    updateCourses();
                    updateEvents();
                    updateTargets();

                    boolean keepChecking = true;
                    while (keepChecking) {
                        if (stepsCompleted()) {
                            keepChecking = false;
                            notifySynchronizationListeners("synchronizationComplete");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "IOException", e);
                    notifySynchronizationListeners("onSynchronizationError", new Throwable("Error connecting to the server"));
                } finally {
                    synchronizing = false;
                }
            }
        }).start();
    }

    public synchronized void registerListener(SynchronizationListener listener) {
        synchronizationListenerList.put(listener.getClass().getName(), listener);
    }

    public synchronized void unRegisterListener(SynchronizationListener listener) {
        synchronizationListenerList.remove(listener.getClass().getName());
    }

    public void notifySynchronizationListeners(String methodName, Object... args) {
        if (showDialog) {
            for (SynchronizationListener listener : synchronizationListenerList.values()) {
                try {
                    Class[] argTypes = null;
                    if (args != null) {
                        argTypes = new Class[args.length];
                        for (int index = 0; index < args.length; index++) {
                            argTypes[index] = args[index].getClass();
                        }
                    }

                    SynchronizationListener.class.getMethod(methodName, argTypes).invoke(listener, args);
                } catch (Exception ex) {
                    Log.e(SynchronizationManager.class.getName(), "Error executing listener method", ex);
                }
            }
        }
    }

    public boolean isSynchronizing() {
        return synchronizing;
    }

    private void updateSynchronizationStep() {
        currentSynchronizationStep++;
        //Log.d(TAG, "Step increased to "+currentSynchronizationStep);
    }

    private boolean stepsCompleted() {
        return (maxSynchronizationSteps == currentSynchronizationStep);
    }

    /** Helper methods **/
    public void updateLocations() {
        if (app.omUpdateLocationTask == null) {
            app.omUpdateLocationTask =  new UpdateDataTask("location",Supervisor.Db.getLastLocationUpdateTime());
            app.omUpdateLocationTask.setListener(new SubmitListener() {
                        @Override
                        public void submitComplete(Payload response) {
                            app.omUpdateLocationTask = null;
                            if (response.isResult()) {
                                User u = (User) response.getResponseData().get(0);
                                String json = (String) response.getResponseData().get(1);
                                processLocationInfo(u, json);
                            } else {
                                updateSynchronizationStep();
                                notifySynchronizationListeners("onSynchronizationError", new Throwable(response.getResultResponse()));
                            }
                        }
                    });
            app.omUpdateLocationTask.execute(new Payload());
        } else {
            updateSynchronizationStep();
        }
    }

    public void updateNurses() {

        if (app.omUpdateNurseTask == null) {
            app.omUpdateNurseTask = new UpdateDataTask("nurse", Supervisor.Db.getLastNurseUpdateTime());
            app.omUpdateNurseTask.setListener(new SubmitListener() {
                        @Override
                        public void submitComplete(Payload response) {
                            if (response.isResult()) {
                                String json = (String) response.getResponseData().get(1);
                                processNurseInfo(json);
                            } else {
                                updateSynchronizationStep();
                                notifySynchronizationListeners("onSynchronizationError", new Throwable(response.getResultResponse()));
                            }
                            app.omUpdateNurseTask = null;
                        }
                    });
            app.omUpdateNurseTask.execute(new Payload());
        } else {
            updateSynchronizationStep();
        }
    }

    public void updateCourses() {

        if (app.omUpdateCourseTask == null) {
            app.omUpdateCourseTask = new UpdateDataTask("course",Supervisor.Db.getLastCourseUpdateTime());
            app.omUpdateCourseTask.setListener(new SubmitListener() {
                        @Override
                        public void submitComplete(Payload response) {
                            app.omUpdateCourseTask = null;
                            if (response.isResult()) {
                                String json = (String) response.getResponseData().get(1);
                                processCourseInfo(json);
                            } else {
                                updateSynchronizationStep();
                                notifySynchronizationListeners("onSynchronizationError", new Throwable(response.getResultResponse()));
                            }
                        }
                    });
            app.omUpdateCourseTask.execute(new Payload());
        } else {
            updateSynchronizationStep();
        }
    }

    public void updateEvents() {

        if (app.omUpdateEventTask == null) {
                    app.omUpdateEventTask = new UpdateDataTask("event", Supervisor.Db.getLastEventUpdateTime());
                    app.omUpdateEventTask.setListener(new SubmitListener() {
                        @Override
                        public void submitComplete(Payload response) {
                            app.omUpdateEventTask = null;
                            if (response.isResult()) {
                                String json = (String) response.getResponseData().get(1);
                                processEventInfo(json);
                            } else {
                                updateSynchronizationStep();
                                notifySynchronizationListeners("onSynchronizationError", new Throwable(response.getResultResponse()));
                            }
                        }
                    });
                    app.omUpdateEventTask.execute(new Payload());
        } else {
            updateSynchronizationStep();
        }
    }

    public void updateTargets() {

        if (app.omUpdateTargetTask == null) {

            app.omUpdateTargetTask = new UpdateDataTask("target", Supervisor.Db.getLastTargetUpdateTime());

            app.omUpdateTargetTask.setListener(new SubmitListener() {
                        @Override
                        public void submitComplete(Payload response) {
                            app.omUpdateTargetTask = null;

                            if (response.isResult()) {
                                String json = (String) response.getResponseData().get(1);
                                processTargetInfo(json);
                            } else {
                                updateSynchronizationStep();
                                notifySynchronizationListeners("onSynchronizationError", new Throwable(response.getResultResponse()));
                            }
                        }
            });

            app.omUpdateTargetTask.execute(new Payload());

        } else {
            updateSynchronizationStep();
        }
    }

    public void uploadLogs() {
        if (app.omUploadLogsTask == null) {
            Payload mqp = Supervisor.Db.getUnsentLogs();
            app.omUploadLogsTask = new UploadLogsTask();
            app.omUploadLogsTask.setListener(new SubmitListener() {
                @Override
                public void submitComplete(Payload response) {
                    app.omUploadLogsTask = null;
                }
            });
            app.omUploadLogsTask.execute(mqp);
        }
    }

    // Request response Processing methods
    private void processLocationInfo(User u, String jsonString) {
        //Log.d(TAG, "Processing Location Info");

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject data = obj.getJSONObject("data");
            String role = data.getString("role");

            if (!role.isEmpty()) { u.setRole(role); Supervisor.Db.updateUser(u); }

            // Handle new info
            JSONArray regions = data.getJSONObject("regions").getJSONArray("new");
            for (int i = 0; i < regions.length(); i++) {
                 int rid = Integer.parseInt(regions.getJSONObject(i).getString("id"));
                 String rName = regions.getJSONObject(i).getString("name");
                 notifySynchronizationListeners("synchronizationUpdate", i, regions.length(), "Adding new regions", false);
                 Supervisor.Db.addRegion(rid, rName);
            }

            JSONArray districts = data.getJSONObject("districts").getJSONArray("new");
            for (int i = 0; i < districts.length(); i++) {
                int did = Integer.parseInt(districts.getJSONObject(i).getString("id"));
                String dName = districts.getJSONObject(i).getString("name");
                String rName = districts.getJSONObject(i).getString("region");
                notifySynchronizationListeners("synchronizationUpdate", i, districts.length(), "Adding new districts", false);
                Supervisor.Db.addDistrict(did, dName, rName);
            }

            JSONArray facilities = data.getJSONObject("facilities").getJSONArray("new");
            for (int i = 0; i < facilities.length(); i++) {
                long fid = Long.parseLong(facilities.getJSONObject(i).getString("id"));
                String fName = facilities.getJSONObject(i).getString("name");
                String fType = facilities.getJSONObject(i).getString("type");
                int did = Integer.parseInt(facilities.getJSONObject(i).getString("did"));
                String dName = facilities.getJSONObject(i).getString("district");
                String rName = facilities.getJSONObject(i).getString("region");
                notifySynchronizationListeners("synchronizationUpdate", i, facilities.length(), "Adding new facilities", false);
                Supervisor.Db.addFacility(fid, fName, fType, rName, dName, did);
            }

            // Handle updates
            regions = data.getJSONObject("regions").getJSONArray("updated");
            for (int i = 0; i < regions.length(); i++) {
                 String rName = regions.getJSONObject(i).getString("name");
                 Region r = Supervisor.Db.getRegion(rName);
                 if (r != null) {
                     r.id = Integer.parseInt(regions.getJSONObject(i).getString("id"));
                     r.name = rName;
                     notifySynchronizationListeners("synchronizationUpdate", i, regions.length(), "Updating existing regions", false);
                     Supervisor.Db.updateRegion(r);
                 }
            }

            districts = data.getJSONObject("districts").getJSONArray("updated");
            for (int i = 0; i < districts.length(); i++) {
                int id = Integer.parseInt(districts.getJSONObject(i).getString("id"));
                District d = Supervisor.Db.getDistrict(id);
                if (d != null) {
                    d.districtId = id;
                    d.name = districts.getJSONObject(i).getString("name");
                    d.region = districts.getJSONObject(i).getString("region");
                    notifySynchronizationListeners("synchronizationUpdate", i, districts.length(), "Updating existing districts", false);
                    Supervisor.Db.updateDistrict(d);
                }
            }

            facilities = data.getJSONObject("facilities").getJSONArray("updated");
            for (int i = 0; i < facilities.length(); i++) {
                int fid = Integer.parseInt(facilities.getJSONObject(i).getString("id"));
                Facility f = Supervisor.Db.getFacility(fid);
                if (f!=null) {
                    f.name = facilities.getJSONObject(i).getString("name");
                    f.facilityType = facilities.getJSONObject(i).getString("type");
                    f.districtId = Integer.parseInt(facilities.getJSONObject(i).getString("did"));
                    f.district = facilities.getJSONObject(i).getString("district");
                    f.region = facilities.getJSONObject(i).getString("region");
                    notifySynchronizationListeners("synchronizationUpdate", i, facilities.length(), "Updating existing facility", false);
                    Supervisor.Db.updateFacility(f);
                }
            }

            // Handle deletions
            regions = data.getJSONObject("regions").getJSONArray("deleted");
            for (int i = 0; i < regions.length(); i++) {
                int id = regions.getInt(i);
                notifySynchronizationListeners("synchronizationUpdate", i, regions.length(), "Deleting old regions", false);
                Supervisor.Db.removeRegion(id);
            }

            districts = data.getJSONObject("districts").getJSONArray("deleted");
            for (int i = 0; i < districts.length(); i++) {
                int id = districts.getInt(i);
                notifySynchronizationListeners("synchronizationUpdate", i, districts.length(), "Deleting old districts", false);
                Supervisor.Db.removeDistrict(id);
            }

            facilities = data.getJSONObject("facilities").getJSONArray("deleted");
            for (int i = 0; i < facilities.length(); i++) {
                int id = facilities.getInt(i);
                notifySynchronizationListeners("synchronizationUpdate", i, facilities.length(), "Deleting old facilities", false);
                Supervisor.Db.removeFacility(id);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        updateSynchronizationStep();

        Supervisor.Db.updateLocationUpdateTime();
    }

    private void processNurseInfo(String jsonString) {
        //Log.d(TAG, "Processing Nurse info");

        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONObject data = obj.getJSONObject("data");

            // Add new nurses
            JSONArray nurses = data.getJSONObject("nurses").getJSONArray("new");
            for (int j = 0; j < nurses.length(); j++) {
                String nid = nurses.getJSONObject(j).getString("id");
                String nName = nurses.getJSONObject(j).getString("name");
                String nTitle = nurses.getJSONObject(j).getString("title");
                String nRole = nurses.getJSONObject(j).getString("role");
                String is_chn = nurses.getJSONObject(j).getString("is_chn");
                String nGroup = nurses.getJSONObject(j).getString("group");
                String nStatus = nurses.getJSONObject(j).getString("status");
                String nGender = nurses.getJSONObject(j).getString("gender");
                String nPhone = nurses.getJSONObject(j).getString("phone_number");
                String nFacility = nurses.getJSONObject(j).getString("my_facility");
                String rName = nurses.getJSONObject(j).getString("region");
                String dName = nurses.getJSONObject(j).getString("district");
                String facility = nurses.getJSONObject(j).getString("facility");
                int did = Integer.valueOf(nurses.getJSONObject(j).getString("did"));
                int fid = Integer.valueOf(nurses.getJSONObject(j).getString("fid"));

                notifySynchronizationListeners("synchronizationUpdate", j, nurses.length(), "Adding new nurses", false);

                Supervisor.Db.addNurse(nid, nName, nTitle, nRole, Integer.parseInt(is_chn), nGroup,
                        nStatus, nGender, nPhone, nFacility, rName, dName,
                        facility, did, fid);
            }
            // Handle updates
            nurses = data.getJSONObject("nurses").getJSONArray("updated");
            for (int j = 0; j < nurses.length(); j++) {
                String nid = nurses.getJSONObject(j).getString("id");
                Nurse n = Supervisor.Db.getNurse(nid);
                if (n != null) {
                    n.nurseId = nurses.getJSONObject(j).getString("id");
                    n.name = nurses.getJSONObject(j).getString("name");
                    n.title = nurses.getJSONObject(j).getString("title");
                    n.role = nurses.getJSONObject(j).getString("role");
                    n.ischn = Integer.parseInt(nurses.getJSONObject(j).getString("is_chn"));
                    n.group = nurses.getJSONObject(j).getString("group");
                    n.status = nurses.getJSONObject(j).getString("status");
                    n.gender = nurses.getJSONObject(j).getString("gender");
                    n.phoneNumber = nurses.getJSONObject(j).getString("phone_number");
                    n.primaryFacility = nurses.getJSONObject(j).getString("my_facility");
                    n.region = nurses.getJSONObject(j).getString("region");
                    n.district = nurses.getJSONObject(j).getString("district");
                    n.facility = nurses.getJSONObject(j).getString("facility");
                    n.districtId = Integer.valueOf(nurses.getJSONObject(j).getString("did"));
                    n.facilityId = Integer.valueOf(nurses.getJSONObject(j).getString("fid"));
                    notifySynchronizationListeners("synchronizationUpdate", j, nurses.length(), "Updating existing nurses", false);
                    Supervisor.Db.updateNurse(n);
                }
            }

            nurses = data.getJSONObject("nurses").getJSONArray("deleted");
            for (int i = 0; i < nurses.length(); i++) {
                int id = nurses.getInt(i);
                notifySynchronizationListeners("synchronizationUpdate", i, nurses.length(), "Deleting old nurses", false);
                Supervisor.Db.removeNurse(id);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        updateSynchronizationStep();
        Supervisor.Db.updateNurseUpdateTime();
    }

    private void processCourseInfo(String jsonString) {
        //Log.d(TAG, "Processing Course info");

        try {
            Supervisor.Db.resetData(Supervisor.Db.CCH_COURSE_TABLE);

            JSONObject obj = new JSONObject(jsonString);
            JSONObject courses = obj.getJSONObject("data").getJSONObject("courses");

            JSONArray newC = courses.getJSONArray("new");
            for (int j = 0; j < newC.length(); j++) {
                JSONObject course = newC.getJSONObject(j);


                Course c = new Course();
                c.id = Long.parseLong(course.getString("cid"));
                c.nurseId = course.getString("nurseId");
                c.title =  course.getString("title");
                c.ksa = course.getString("ksa");
                c.timeTaken = course.getString("time_taken");
                c.last_accessed = course.getString("last_accessed");
                c.attempts = Integer.parseInt((course.getString("attempts").equals(""))
                        ? "0" : course.getString("attempts"));

                c.quiz_status = (course.getString("score").equals("") ||
                        course.getString("score").equalsIgnoreCase("Not taken"))
                        ? 0 : 1;

                c.score = (c.quiz_status ==0) ? 0 : Integer.parseInt(course.getString("score"));

                c.percentageCompletion = (course.getString("percentcomplete").equals(""))
                        ? 0 : Integer.parseInt(course.getString("percentcomplete"));

                String rName = course.getString("region");
                String dName = course.getString("district");
                String fName = course.getString("facility");
                int did = Integer.valueOf(course.getString("did"));
                int fid = Integer.valueOf(course.getString("fid"));

                notifySynchronizationListeners("synchronizationUpdate", j, newC.length(),  "Updating course info", false);

                Supervisor.Db.addCourse(c.nurseId, c.id, c.title, c.ksa, c.quiz_status,
                        c.percentageCompletion, c.score, c.timeTaken,
                        c.last_accessed, c.attempts, rName, dName, fName, did, fid);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        updateSynchronizationStep();
        Supervisor.Db.updateCourseUpdateTime();
    }

    private void processEventInfo(String jsonString) {
        //Log.d(TAG, "Processing Event info");

        try {
            Supervisor.Db.resetData(Supervisor.Db.CCH_EVENTS_TABLE);

            JSONObject obj = new JSONObject(jsonString);
            JSONObject events = obj.getJSONObject("data").getJSONObject("events");

            JSONArray newE = events.getJSONArray("new");
            for (int j = 0; j < newE.length(); j++) {
                JSONObject event = newE.getJSONObject(j);
                Event ev = new Event();
                ev.nurseId = event.getString("nurseId");
                ev.nurse = event.getString("nurse");
                ev.eventId = Integer.parseInt(event.getString("id"));
                ev.title = event.getString("title");
                ev.location = event.getString("location");
                ev.type = event.getString("type");
                ev.start = Long.parseLong(event.getString("start"));
                ev.end = Long.parseLong(event.getString("end"));
                ev.justification = event.getString("justification");
                ev.comments = event.getString("comments");
                ev.status = event.getString("status");
                String rName = event.getString("region");
                String dName = event.getString("district");
                String fName = event.getString("facility");
                int did = Integer.valueOf(event.getString("did"));
                int fid = Integer.valueOf(event.getString("fid"));

                notifySynchronizationListeners("synchronizationUpdate", j, events.length(), "Updating events info",false);

                Supervisor.Db.addEvent(ev.nurseId, ev.nurse, ev.eventId, ev.title, ev.location, ev.type,
                        ev.start, ev.end, ev.justification, ev.comments, ev.status,
                        rName, dName, fName, did, fid);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        updateSynchronizationStep();
        Supervisor.Db.updateEventUpdateTime();
    }

    private void processTargetInfo(String jsonString) {
        //Log.d(TAG, "Processing Target info");

        try {
            Supervisor.Db.resetData(Supervisor.Db.CCH_TARGET_TABLE);

            JSONObject obj = new JSONObject(jsonString);
            JSONObject targets = obj.getJSONObject("data").getJSONObject("targets");

            JSONArray newT = targets.getJSONArray("new");
            for (int j = 0; j < newT.length(); j++) {
                JSONObject target = newT.getJSONObject(j);
                Target myTarget = new Target();

                String tr = target.getString("target");
                String ach = target.getString("achieved");

                myTarget.targetId = Integer.parseInt(target.getString("id"));
                myTarget.nurseId = target.getString("nurseId");
                myTarget.target = (tr.equalsIgnoreCase("")) ? 0 : Integer.parseInt(tr);
                myTarget.type = target.getString("type");
                myTarget.category =  target.getString("category");
                myTarget.achieved = (ach.equalsIgnoreCase("")) ? 0 : Integer.parseInt(ach);
                myTarget.justification = target.getString("justification");
                myTarget.startDate = target.getString("start");
                myTarget.dueDate = target.getString("end");
                myTarget.status = target.getString("status");
                String rName = target.getString("region");
                String dName = target.getString("district");
                String fName = target.getString("facility");
                int did = Integer.valueOf(target.getString("did"));
                int fid = Integer.valueOf(target.getString("fid"));

                notifySynchronizationListeners("synchronizationUpdate", j, newT.length(), "Updating target info", false);

                Supervisor.Db.addTarget(myTarget.nurseId, myTarget.targetId,
                        myTarget.target, myTarget.type, myTarget.category,
                        myTarget.achieved, myTarget.justification,
                        myTarget.startDate, myTarget.dueDate, myTarget.status,
                        rName, dName, fName, did, fid);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        updateSynchronizationStep();
        Supervisor.Db.updateTargetUpdateTime();
    }
}

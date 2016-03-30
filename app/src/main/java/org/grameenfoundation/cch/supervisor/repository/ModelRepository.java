package org.grameenfoundation.cch.supervisor.repository;


import org.grameenfoundation.cch.supervisor.Supervisor;
import org.grameenfoundation.cch.supervisor.model.Course;
import org.grameenfoundation.cch.supervisor.model.District;
import org.grameenfoundation.cch.supervisor.model.Event;
import org.grameenfoundation.cch.supervisor.model.Facility;
import org.grameenfoundation.cch.supervisor.model.Nurse;
import org.grameenfoundation.cch.supervisor.model.Target;
import org.grameenfoundation.cch.supervisor.task.SynchronizationManager;
import org.grameenfoundation.cch.supervisor.util.ConnectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ModelRepository {

    public static final String TAG = ModelRepository.class.getSimpleName();

    // Nurses
    public static ArrayList<Nurse> getFacilityNurses(String facId, Boolean inProgress, Boolean eligible, Boolean passed) {
        ArrayList<Nurse> nurses = Supervisor.Db.getNurseByField(Supervisor.Db.CCH_FACILITY_ID, facId);
        return processNurses(nurses, inProgress, eligible, passed, false);
    }
    public static ArrayList<Nurse> getDistrictNurses(String dId, Boolean inProgress, Boolean eligible, Boolean passed) {
        ArrayList<Nurse> nurses = Supervisor.Db.getNurseByField(Supervisor.Db.CCH_DISTRICT_ID, dId);
        return processNurses(nurses, inProgress, eligible, passed, false);
    }

    public static ArrayList<Nurse> getFacilitySupervisors(String facId, Boolean inProgress, Boolean eligible, Boolean passed) {
        ArrayList<Nurse> nurses = Supervisor.Db.getNurseByField(Supervisor.Db.CCH_FACILITY_ID, facId);
        return processNurses(nurses, inProgress, eligible, passed, true);
    }
    public static ArrayList<Nurse> getDistrictSupervisors(String dId, Boolean inProgress, Boolean eligible, Boolean passed) {
        ArrayList<Nurse> nurses = Supervisor.Db.getNurseByField(Supervisor.Db.CCH_DISTRICT_ID, dId);
        return processNurses(nurses, inProgress, eligible, passed, true);
    }

    public static ArrayList<Nurse> processNurses(ArrayList<Nurse> nurses, Boolean inProgress, Boolean eligible, Boolean passed, Boolean flagSup) {
        ArrayList<Nurse> res = new ArrayList<>();
        Collections.sort(nurses, new NurseDistrictNameComparator());

        for (Nurse n: nurses) {
            boolean added = false;
            if (flagSup) {
                if (n.isSupervisor()) {
                    if (inProgress) { if (n.hasInProgressCourses()) { res.add(n); added = true; } }
                    if (eligible) { if (n.hasEligibleCourses() && !added) { res.add(n); added = true; } }
                    if (passed) { if (n.hasPassedCourses() && !added) { res.add(n); } }
                    if (!inProgress && !eligible && !passed) { res.add(n); } }
            } else {
                if (!n.isSupervisor()) {
                    if (inProgress) { if (n.hasInProgressCourses()) { res.add(n); added = true; } }
                    if (eligible) { if (n.hasEligibleCourses() && !added) { res.add(n); added = true; } }
                    if (passed) { if (n.hasPassedCourses() && !added) { res.add(n); } }
                    if (!inProgress && !eligible && !passed) { res.add(n); }
                }
            }
        }
        return res;
    }


    // Courses
    public static ArrayList<Course> getCourses(String nurseId, Boolean inProgress, Boolean eligible, Boolean passed) {
        ArrayList<Course> res = new ArrayList<>();
        ArrayList<Course> courses = Supervisor.Db.getCourses(nurseId);
        Collections.sort(courses, new CourseStatusComparator());

        for (Course c: courses) {
            boolean added = false;
            if (inProgress) { if (c.isInprogress()) { res.add(c); added=true; } }
            if (eligible) { if (c.isEligible() && !added) { res.add(c); added=true; } }
            if (passed) { if (c.isPassed() && !added) { res.add(c); } }
            if (!inProgress && !eligible && !passed) { res.add(c); }
        }
        return res;
    }

    public static void updateCourseStatus(Course course) {
        Long startTime = System.currentTimeMillis();
        Long endTime = System.currentTimeMillis();
        String data = "";
        try {
            JSONObject json = new JSONObject();
            json.put("userid", course.nurseId);
            json.put("courseid", course.courseId);
            json.put("status", course.ksa);
            json.put("supid", Supervisor.getUser().getUsername());
            data = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Supervisor.Db.updateCourse(course);
        Supervisor.Db.insertLog("Supervisor KSA", data, startTime.toString(), endTime.toString());

        if (ConnectionUtils.isNetworkConnected(Supervisor.mAppContext)) {
            SynchronizationManager.getInstance().uploadLogs();
        }
    }




    // Events
    public static ArrayList<Event> getNurseEvents(String nurseId, Boolean pending, Boolean incomplete, Boolean complete) {
        ArrayList<Event> events = Supervisor.Db.getEventsByField(Supervisor.Db.CCH_NURSE_ID, nurseId);
        return processEvents(events, pending, incomplete, complete);
    }

    public static ArrayList<Event> getFacilityEvents(String facId, Boolean pending, Boolean incomplete, Boolean complete) {
        ArrayList<Event> events = Supervisor.Db.getEventsByField(Supervisor.Db.CCH_FACILITY_ID, facId);
        return processEvents(events, pending, incomplete, complete);
    }

    private static ArrayList<Event> processEvents(ArrayList<Event> events, Boolean pending, Boolean incomplete, Boolean complete) {
        ArrayList<Event> res = new ArrayList<>();
        Collections.sort(events, new EventTimeComparator());

        boolean added;
        for (Event e: events) {
            added = false;
            if (pending) { if (e.isPending()) { res.add(e); added=true; } }
            if (incomplete) { if (e.isInComplete() && !added) { res.add(e); added=true; } }
            if (complete) { if (e.isComplete() && !added) { res.add(e); } }
            if (!pending && !incomplete && !complete) { res.add(e); }
        }
        return res;
    }


    // Locations
    public static ArrayList<Facility> getFacility() {
        ArrayList<Facility> facilities = Supervisor.Db.getFacilities();
        Collections.sort(facilities, new FacilityTypeComparator());
        return facilities;
    }

    public static ArrayList<District> getDistricts() {
        ArrayList<District> districts = Supervisor.Db.getDistricts();
        return districts;
    }


    /* Comparators */
    public static class EventTimeComparator implements Comparator<Event> {

        public int compare(Event one, Event two) {
            return one.start < two.start ? -1
                    : one.start > two.start ? 1 : 0;
        }
    }

    public static class TargetTimeComparator implements Comparator<Target> {

        public int compare(Target one, Target two) {
            return one.getTime() < two.getTime() ? -1 : one.getTime() > two
                    .getTime() ? 1 : 0;
        }
    }

    public static class CourseStatusComparator implements Comparator<Course> {

        public int compare(Course one, Course two) {
            int c;
            c = one.ksa.compareToIgnoreCase(two.ksa);
            if (c==0)
                c = one.statsText().compareToIgnoreCase(two.statsText());
            return c;
        }
    }

    public static class NurseDistrictNameComparator implements Comparator<Nurse> {
        public int compare(Nurse one, Nurse two) {
            return one.facility.compareToIgnoreCase(two.facility);
        }
    }

    public static class FacilityTypeComparator implements Comparator<Facility> {
        public int compare(Facility one, Facility two) {
            return one.facilityType.compareToIgnoreCase(two.facilityType);
        }
    }
}

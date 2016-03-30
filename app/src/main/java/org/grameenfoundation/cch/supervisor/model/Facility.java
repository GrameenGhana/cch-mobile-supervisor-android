package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;

public class Facility implements Serializable {
	public int id;
	public int facilityId;
	public String name;
	public String facilityType;
	public int districtId;
	public String district;
	public String region;

	/** counter variables **/
	public int cntSupervisors;
	public int cntNurses;
	public int cntEvents;
	public int cntTargets;

	public String statsText() {
		return cntNurses + " Nurses    " + cntSupervisors + " Supervisors    " + cntEvents + " Events";
	}
}

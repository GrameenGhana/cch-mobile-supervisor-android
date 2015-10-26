package org.grameenfoundation.cch.supervisor.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Target {
	int id;
	int targetId;
	String nurseId;
	String targetType;
	String targetCategory;

	public Target(int id, int targetId, String nurseId, String targetType,
			String targetCategory, int achieved, int targetNo,
			String justification, int completed, Date start, Date end) {
		super();
		this.id = id;
		this.targetId = targetId;
		this.nurseId = nurseId;
		this.targetType = targetType;
		this.targetCategory = targetCategory;
		this.achieved = achieved;
		this.targetNo = targetNo;
		this.justification = justification;
		this.completed = completed;
		this.start = start;
		this.end = end;
	}

	public Target(int id, int targetId, String nurseId, String targetType,
			String targetCategory, int achieved, int targetNo,
			String justification, int completed, String start, String end) {
		super();
		this.id = id;
		this.targetId = targetId;
		this.nurseId = nurseId;
		this.targetType = targetType;
		this.targetCategory = targetCategory;
		this.achieved = achieved;
		this.targetNo = targetNo;
		this.justification = justification;
		this.completed = completed;
		this.start = getStringFromDate(start);
		this.end = getStringFromDate(end);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getNurseId() {
		return nurseId;
	}

	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetCategory() {
		return targetCategory;
	}

	public void setTargetCategory(String targetCategory) {
		this.targetCategory = targetCategory;
	}

	public int getAchieved() {
		return achieved;
	}

	public void setAchieved(int achieved) {
		this.achieved = achieved;
	}

	public int getTargetNo() {
		return targetNo;
	}

	public void setTargetNo(int targetNo) {
		this.targetNo = targetNo;
	}

	public String getJustification() {
		return justification;
	}

	public void setJustification(String justification) {
		this.justification = justification;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	int achieved;
	int targetNo;
	String justification;
	int completed;
	Date start;
	Date end;

	Date getStringFromDate(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		try {
			return formatter.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}

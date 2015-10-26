package org.grameenfoundation.cch.supervisor.model;

import java.util.Date;

public class Course {
	int id;
	String title;
	int nurseId;
	String lastAcessed;
	double score;
	int precentageCompleted;
	int attempts;
	
	
	public Course(int id, String title, int nurseId, String lastAcessed,
			double score, int precentageCompleted,int attempts) {
		super();
		this.id = id;
		this.title = title;
		this.nurseId = nurseId;
		this.lastAcessed = lastAcessed;
		this.score = score;
		this.precentageCompleted = precentageCompleted;
		this.attempts = attempts;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getNurseId() {
		return nurseId;
	}
	public void setNurseId(int nurseId) {
		this.nurseId = nurseId;
	}
	public String getLastAcessed() {
		return lastAcessed;
	}
	public void setLastAcessed(String lastAcessed) {
		this.lastAcessed = lastAcessed;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public int getPrecentageCompleted() {
		return precentageCompleted;
	}
	public void setPrecentageCompleted(int precentageCompleted) {
		this.precentageCompleted = precentageCompleted;
	}


}

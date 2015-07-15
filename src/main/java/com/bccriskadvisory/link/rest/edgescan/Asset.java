package com.bccriskadvisory.link.rest.edgescan;

import java.time.ZonedDateTime;
import java.util.List;

public class Asset extends EdgescanObject {

	//basic
	private String name;
	private String hostname;
	private int priority;
	//detailed
	private String type;
	private boolean authenticated;
	private int host_count;
	private int assessment_count;
	private ZonedDateTime next_assessment_date;
	private List<Assessment> assessments;
	
	public String getName() {
		return name;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public int getPriority() {
		return priority;
	}

	public String getType() {
		return type;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public int getHostCount() {
		return host_count;
	}

	public int getAssessmentCount() {
		return assessment_count;
	}

	public ZonedDateTime getNextAssessmentDate() {
		return next_assessment_date;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}
}
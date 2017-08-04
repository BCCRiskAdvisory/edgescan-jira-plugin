/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bccriskadvisory.link.rest.edgescan;

import java.util.List;

import org.joda.time.DateTime;

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
	private DateTime next_assessment_date;
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

	public DateTime getNextAssessmentDate() {
		return next_assessment_date;
	}

	public List<Assessment> getAssessments() {
		return assessments;
	}
}
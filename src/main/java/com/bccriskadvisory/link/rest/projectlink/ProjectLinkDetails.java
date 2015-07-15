package com.bccriskadvisory.link.rest.projectlink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bccriskadvisory.link.rest.gson.GsonObject;

@SuppressWarnings("unused")
public class ProjectLinkDetails extends GsonObject {

	private String connectionName;
	
	private List<String> assets;
	private List<PriorityMapping> priorities;
	
	private IconObject userDetails;
	private IconObject issueTypeDetails;
	private BadgeObject openStatusDetails;
	private BadgeObject closeStatusDetails;
	
	private String lastUpdated;
	
	public ProjectLinkDetails() {
		assets = new ArrayList<>();
		priorities = new ArrayList<>();
	}
	
	public void addAsset(Integer key, String name) {
		assets.add(name);
	}
	
	public void addPriority(String riskName, String priorityName, String icon) {
		priorities.add(new PriorityMapping(riskName, priorityName, icon));
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public void setUserDetails(String name, String icon) {
		this.userDetails = new IconObject(name, icon);
	}

	public void setIssueTypeDetails(String name, String icon) {
		this.issueTypeDetails = new IconObject(name, icon);
	}

	public void setOpenStatusDetails(String name, String badgeColour) {
		this.openStatusDetails = new BadgeObject(name, badgeColour);
	}

	public void setCloseStatusDetails(String name, String badgeColour) {
		this.closeStatusDetails = new BadgeObject(name, badgeColour);
	}
	
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	private class PriorityMapping {
		
		private String riskName;
		private String priorityName;
		private String icon;
		
		public PriorityMapping(String edgescanRisk, String name, String icon) {
			this.riskName = edgescanRisk;
			this.priorityName = name;
			this.icon = icon;
		}
	}
	
	private class IconObject {
		
		private String name;
		private String icon;
		
		public IconObject(String name, String icon) {
			this.name = name;
			this.icon = icon;
		}
	}
	
	private class BadgeObject {
		
		private String name;
		private String badgeColour;
		
		public BadgeObject(String name, String badgeColour) {
			this.name = name;
			this.badgeColour = badgeColour;
		}
	}
}
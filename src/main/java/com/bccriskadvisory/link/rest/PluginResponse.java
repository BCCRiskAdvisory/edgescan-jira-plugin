package com.bccriskadvisory.link.rest;

import java.util.List;

import com.bccriskadvisory.jira.ao.PluginObject;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;
import com.bccriskadvisory.link.processor.ImportResults;
import com.bccriskadvisory.link.rest.form.components.FormStructure;
import com.bccriskadvisory.link.rest.gson.GsonObject;
import com.bccriskadvisory.link.rest.projectlink.ProjectLinkDetails;

@SuppressWarnings("unused")
public class PluginResponse extends GsonObject {
	
	private FormStructure form;
	
	private List<Connection> connections;
	private Connection connection;
	
	private List<ProjectLink> links;
	private ProjectLink link;
	private ProjectLinkDetails linkDetails;
	
	private ImportResults importResults;
	
	private List<String> errorMessages;
	
	public PluginResponse withFormStructure(FormStructure form) {
		this.form = form;
		return this;
	}

	public PluginResponse withConnections(List<Connection> connections) {
		this.connections = connections;
		return this;
	}
	
	public PluginResponse withConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public PluginResponse withLinks(List<ProjectLink> links) {
		this.links = links;
		return this;
	}

	public PluginResponse withLink(ProjectLink link) {
		this.link = link;
		return this;
	}

	public PluginResponse withLinkDetails(ProjectLinkDetails linkDetails) {
		this.linkDetails = linkDetails;
		return this;
	}
	
	public PluginResponse withImportResults(ImportResults importResults) {
		this.importResults = importResults;
		return this;
	}

	public PluginResponse withErrorMessages(List<String> messages) {
		this.errorMessages = messages;
		return this;
	}
}
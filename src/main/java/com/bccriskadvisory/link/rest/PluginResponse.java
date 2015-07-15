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
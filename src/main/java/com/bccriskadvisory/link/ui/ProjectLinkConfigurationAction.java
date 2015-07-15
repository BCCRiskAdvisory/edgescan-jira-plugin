package com.bccriskadvisory.link.ui;

import java.util.List;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.link.JiraPluginContext;

@SuppressWarnings("serial")
public class ProjectLinkConfigurationAction extends JiraWebActionSupport {

	private JiraPluginContext context;

	public ProjectLinkConfigurationAction(JiraPluginContext context) {
		this.context = context;
	}

	@Override
	protected String doExecute() throws Exception {
		List<Connection> connections = context.getConnectionService().index();
		
		if (connections.isEmpty()) {
			return "connectionNotFound";
		} else {
			return SUCCESS;
		}
	}
}
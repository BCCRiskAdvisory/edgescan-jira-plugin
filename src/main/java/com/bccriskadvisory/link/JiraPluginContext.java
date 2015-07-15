package com.bccriskadvisory.link;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.bccriskadvisory.jira.ao.connection.ConnectionService;
import com.bccriskadvisory.jira.ao.connection.ConnectionServiceImpl;
import com.bccriskadvisory.jira.ao.projectlink.ProjectLinkServiceImpl;
import com.bccriskadvisory.jira.ao.vulnerabilitylink.VulnerabilityLinkService;
import com.bccriskadvisory.jira.ao.vulnerabilitylink.VulnerabilityLinkServiceImpl;
import com.bccriskadvisory.jira.user.UnifiedUserManager;

public class JiraPluginContext {
	
	private final UnifiedUserManager userManager;
	private final ConstantsManager constantsManager;
	private final RequestFactory<?> requestFactory;
	private final LoginUriProvider loginUriProvider;
	private final TemplateRenderer templateRenderer;

	private final ProjectService projectService;
	private final IssueService issueService;
	private final WorkflowManager workflowManager;
	private final IssueWorkflowManager issueWorkflowManager;
	
	private final VulnerabilityLinkService vulnerabilityLinkService;
	private final ProjectLinkServiceImpl projectLinkService;
	private final ConnectionService connectionService;
	private final AvatarService avatarService;
	
	public JiraPluginContext(final ActiveObjects activeObjects,
			final ProjectService projectService, final UnifiedUserManager userManager,
			final ConstantsManager constantsManager,
			final RequestFactory<?> requestFactory, final IssueService issueService,
			final WorkflowManager workflowManager,
			final IssueWorkflowManager issueWorkflowManager, final LoginUriProvider loginUriProvider, 
			final AvatarService avatarService, final TemplateRenderer templateRenderer) {
		this.constantsManager = checkNotNull(constantsManager, "Constants Manager");
		this.avatarService = checkNotNull(avatarService, "Avatar Service");
		this.userManager = checkNotNull(userManager, "User Manager");
		this.requestFactory = checkNotNull(requestFactory, "Request Factory");
		
		this.loginUriProvider = checkNotNull(loginUriProvider, "Login URI provider");
		this.templateRenderer = checkNotNull(templateRenderer, "Template Renderer");
		
		this.projectService = checkNotNull(projectService, "Project Service");
		this.issueService = checkNotNull(issueService, "Issue Service");
		this.workflowManager = checkNotNull(workflowManager, "Workflow Manager");
		this.issueWorkflowManager = checkNotNull(issueWorkflowManager, "Issue Workflow Manager");
		
		checkNotNull(activeObjects, "Active Objects Service");
		this.connectionService = new ConnectionServiceImpl(activeObjects);
		this.vulnerabilityLinkService = new VulnerabilityLinkServiceImpl(activeObjects);
		this.projectLinkService = new ProjectLinkServiceImpl(activeObjects);
	}

	public ConnectionService getConnectionService() {
		return connectionService;
	}

	public ProjectService getProjectService() {
		return projectService;
	}

	public UnifiedUserManager getUserManager() {
		return userManager;
	}

	public ConstantsManager getConstantsManager() {
		return constantsManager;
	}

	public RequestFactory<?> getRequestFactory() {
		return requestFactory;
	}

	public IssueService getIssueService() {
		return issueService;
	}

	public VulnerabilityLinkService getVulnerabilityLinkService() {
		return vulnerabilityLinkService;
	}

	public WorkflowManager getWorkflowManager() {
		return workflowManager;
	}

	public IssueWorkflowManager getIssueWorkflowManager() {
		return issueWorkflowManager;
	}

	public ProjectLinkServiceImpl getProjectLinkService() {
		return projectLinkService;
	}

	public LoginUriProvider getLoginUriProvider() {
		return loginUriProvider;
	}

	public AvatarService getAvatarService() {
		return avatarService;
	}

	public TemplateRenderer getTemplateRenderer() {
		return templateRenderer;
	}
}
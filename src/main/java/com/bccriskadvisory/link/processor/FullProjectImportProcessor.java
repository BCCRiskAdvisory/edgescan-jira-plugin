package com.bccriskadvisory.link.processor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.DeleteValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.bccriskadvisory.jira.ao.vulnerabilitylink.VulnerabilityLink;
import com.bccriskadvisory.jira.ao.vulnerabilitylink.VulnerabilityLinkService;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.rest.edgescan.Vulnerability;
import com.google.common.collect.Maps;

public class FullProjectImportProcessor extends AbstractProjectImportProcessor {

	private Map<Integer, VulnerabilityLink> vulnerabilityIdToLink;
	private ReentrantLock importLock;

	public FullProjectImportProcessor(JiraPluginContext pluginContext, boolean testMode) {
		super(pluginContext, ImportMode.FULL, testMode);
	}

	@Override
	protected boolean importLockAcquired() {
		importLock = pluginContext.getProjectLinkService().getImportLock(link);
		importLock.lock();
		return true;
	}

	@Override
	protected void releaseImportLock() {
		importLock.unlock();
	}
	
	@Override
	protected void preProcess() {
		VulnerabilityLinkService vulnerabilityLinkService = pluginContext.getVulnerabilityLinkService();
		
		vulnerabilityIdToLink = Maps.newHashMap();
		
		for (VulnerabilityLink vulnerabilityLink : vulnerabilityLinkService.findBy("PROJECT_KEY = ?", link.getProjectKey())) {
			vulnerabilityIdToLink.put(vulnerabilityLink.getVulnerabilityId(), vulnerabilityLink);
		}
	}
	
	@Override
	protected void processVulnerability(Vulnerability vulnerability) {
		super.processVulnerability(vulnerability);
		markProcessed(vulnerability);
	}
	

	private void markProcessed(Vulnerability vulnerability) {
		vulnerabilityIdToLink.remove(vulnerability.getId());
	}

	@Override
	protected void postProcess() {
		for (VulnerabilityLink orphanedLink : vulnerabilityIdToLink.values()) {
			IssueService issueService = pluginContext.getIssueService();
			
			IssueResult issueResult = issueService.getIssue(user, orphanedLink.getIssueKey());
			
			if (issueResult.isValid()) {
				DeleteValidationResult validateResult = issueService.validateDelete(user, issueResult.getIssue().getId());
				if (validateResult.isValid()) {
					if (!testMode) {
						issueService.delete(user, validateResult);
						unlink(orphanedLink);
					}
					importResults.deleted();
				} else {
					recordValidationErrors(Optional.of(validateResult));
				}
			} else {
				if (!testMode) {
					unlink(orphanedLink);
				}
			}
		}
	}

	private void unlink(VulnerabilityLink orphanedLink) {
		pluginContext.getVulnerabilityLinkService().delete(orphanedLink);
	}
}
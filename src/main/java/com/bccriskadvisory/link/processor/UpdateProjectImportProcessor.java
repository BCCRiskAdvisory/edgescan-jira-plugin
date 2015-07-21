package com.bccriskadvisory.link.processor;

import java.util.concurrent.locks.ReentrantLock;

import com.bccriskadvisory.link.JiraPluginContext;

public class UpdateProjectImportProcessor extends AbstractProjectImportProcessor {

	private ReentrantLock importLock;

	public UpdateProjectImportProcessor(JiraPluginContext pluginContext, boolean testMode) {
		super(pluginContext, ImportMode.UPDATE, testMode);
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
	protected void preProcess() { }

	@Override
	protected void postProcess() { }
}
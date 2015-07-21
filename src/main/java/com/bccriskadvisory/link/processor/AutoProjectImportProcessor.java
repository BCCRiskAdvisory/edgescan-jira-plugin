package com.bccriskadvisory.link.processor;

import java.util.concurrent.locks.ReentrantLock;

import com.bccriskadvisory.link.JiraPluginContext;

public class AutoProjectImportProcessor extends AbstractProjectImportProcessor {

	private ReentrantLock importLock;

	public AutoProjectImportProcessor(JiraPluginContext pluginContext, boolean testMode) {
		super(pluginContext, ImportMode.AUTO, testMode);
	}

	@Override
	protected boolean importLockAcquired() {
		ReentrantLock importLock = pluginContext.getProjectLinkService().getImportLock(link);
		if (importLock.tryLock()) {
			this.importLock = importLock;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void releaseImportLock() {
		if (importLock != null) {
			importLock.unlock();
		}
	}

	@Override
	protected void preProcess() { }

	@Override
	protected void postProcess() { }
}

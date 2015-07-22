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

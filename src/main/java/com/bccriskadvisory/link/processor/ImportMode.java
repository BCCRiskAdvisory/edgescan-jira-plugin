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

import com.bccriskadvisory.link.JiraPluginContext;

public enum ImportMode {
	
	AUTO(true) {
		@Override
		public ProjectImportProcessor createProcessor( JiraPluginContext pluginContext, boolean testMode) {
			return new AutoProjectImportProcessor(pluginContext, testMode);
		}
	}, 
	UPDATE(true) {
		@Override
		public ProjectImportProcessor createProcessor( JiraPluginContext pluginContext, boolean testMode) {
			return new UpdateProjectImportProcessor(pluginContext, testMode);
		}
	}, 
	FULL(false) {
		@Override
		public ProjectImportProcessor createProcessor( JiraPluginContext pluginContext, boolean testMode) {
			return new FullProjectImportProcessor(pluginContext, testMode);
		}
	};
	
	private boolean isUpdate;
	
	private ImportMode(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	public abstract ProjectImportProcessor createProcessor(JiraPluginContext pluginContext, boolean testMode);

	public boolean isUpdate() {
		return isUpdate;
	}
	
	public static ImportMode fromString(String in) {
		switch (in.toLowerCase()) {
		case "auto":
			return AUTO;
		case "update":
			return UPDATE;
		case "full":
			return FULL;
		default:
			throw new IllegalArgumentException("Unrecognised import mode: " + in);
		}
	}
}
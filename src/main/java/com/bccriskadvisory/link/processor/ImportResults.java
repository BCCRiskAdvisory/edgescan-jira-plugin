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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bccriskadvisory.link.rest.PluginError;
import com.bccriskadvisory.link.rest.edgescan.Risk;
import com.bccriskadvisory.link.rest.gson.GsonObject;

@SuppressWarnings("unused")
public class ImportResults extends GsonObject {

	private int totalFound;
	private int totalIgnored;
	private int totalUnchanged;
	private int totalOpened;
	private int totalUpdated;
	private int totalClosed;
	private int totalFailed;
	
	private int deleted;
	
	private List<PluginError> errorMessages;
	private Map<String, ImportCount> breakdownByRisk;
	private ImportMode importMode;
	private boolean testMode;
	
	public ImportResults(ImportMode importMode, boolean testMode) {
		this.importMode = importMode;
		this.testMode = testMode;
		
		breakdownByRisk = new HashMap<>();
		errorMessages = new ArrayList<>();
		
		for (Risk risk : Risk.values()) {
			breakdownByRisk.put(risk.getKeyString(), new ImportCount());
		}
	}
	
	public void addError(PluginError errorString) {
		errorMessages.add(errorString);
	}
	
	public ImportCount forRisk(Risk risk) {
		return breakdownByRisk.get(risk.getKeyString());
	}
	
	public void deleted() {
		deleted++;
	}
	
	public class ImportCount {
		private int found;
		private int ignored;
		private int unchanged;
		private int opened;
		private int updated;
		private int closed;
		private int failed;
		
		private void found() {
			totalFound++;
			found++;
		}
		
		public void ignored() {
			found();
			totalIgnored++;
			ignored++;
		}
		
		public void unchanged() {
			found();
			totalUnchanged++;
			unchanged++;
		}
		
		public void opened() {
			found();
			totalOpened++;
			opened++;
		}
		
		public void updated() {
			found();
			totalUpdated++;
			updated++;
		}
		
		public void closed() {
			found();
			totalClosed++;
			closed++;
		}
		
		public void failed() {
			found();
			totalFailed++;
			failed++;
		}
	}
}
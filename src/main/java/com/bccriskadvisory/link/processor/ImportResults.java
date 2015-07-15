package com.bccriskadvisory.link.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private List<String> errorMessages;
	private Map<String, ImportCount> breakdownByRisk;
	private String importMode;
	private boolean testMode;
	
	public ImportResults(String importMode, boolean testMode) {
		this.importMode = importMode;
		this.testMode = testMode;
		
		breakdownByRisk = new HashMap<>();
		errorMessages = new ArrayList<>();
		
		for (Risk risk : Risk.values()) {
			breakdownByRisk.put(risk.getKeyString(), new ImportCount());
		}
	}
	
	public void addError(String errorString) {
		errorMessages.add(errorString);
	}
	
	public ImportCount forRisk(Risk risk) {
		return breakdownByRisk.get(risk.getKeyString());
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
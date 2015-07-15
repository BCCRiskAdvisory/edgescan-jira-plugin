package com.bccriskadvisory.link.monitor;

import java.util.Date;

import com.bccriskadvisory.link.JiraPluginContext;

public interface EdgescanMonitor {
	
    public long getInterval();
    public Date getLastRun();
    public void schedule();
	public abstract JiraPluginContext getPluginContext();

}
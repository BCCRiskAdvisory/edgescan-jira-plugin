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
package com.bccriskadvisory.link.monitor;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.extension.JiraStartedEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.bccriskadvisory.link.JiraPluginContext;
import com.bccriskadvisory.link.utility.AbstractLogSupported;
import com.bccriskadvisory.link.utility.Utilities;

public class EdgescanMonitorImpl extends AbstractLogSupported implements InitializingBean, DisposableBean, EdgescanMonitor {
	
	public static final String KEY = EdgescanMonitorImpl.class.getName() + ":instance";
	private static final String JOB_NAME = EdgescanMonitorImpl.class.getName() + ":job";

	private PluginScheduler scheduler;
	private EventPublisher eventPublisher;
	private JiraPluginContext pluginContext;
	
	private boolean scheduled;
	
	private long interval = 60000L;
	private LocalDateTime lastRun;

	public EdgescanMonitorImpl(PluginScheduler pluginScheduler, EventPublisher eventPublisher, JiraPluginContext pluginContext) {
		this.scheduler = pluginScheduler;
		this.eventPublisher = eventPublisher;
		this.pluginContext = pluginContext;
	}

	@Override
	public long getInterval() {
		return interval;
	}

	@Override
	public Date getLastRun() {
		return lastRun.toDate();
	}
	
	public void setLastRun(LocalDateTime lastRun) {
		this.lastRun = lastRun;
	}
	
	@EventListener
	public void onStartEvent(JiraStartedEvent startEvent) {
		schedule();
	}
	
	@EventListener
	public void onEnableEvent(PluginEnabledEvent enabledEvent) {
		schedule();
	}
	
	@Override
	public void schedule() {
		synchronized (this) {
			if (scheduled) return;

			try {
				Map<String, Object> jobDataMap = Collections.singletonMap(KEY, (Object) this);

				scheduler.scheduleJob(JOB_NAME, EdgescanLinkTask.class, jobDataMap, Utilities.now().toDate(), interval);
			} catch (Exception e) {
				getLog().error("Unable to schedule edgescan link task.");
			}

			scheduled = true;
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}
	
	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

	@Override
	public JiraPluginContext getPluginContext() {
		return pluginContext;
	}
}

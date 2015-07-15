package com.bccriskadvisory.link.ui;

import java.util.Map;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;

public class ProjectLinkSummaryContextProvider implements ContextProvider {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
	}

	@Override
	public Map<String, Object> getContextMap(Map<String, Object> context) {
		return context;
	}
}
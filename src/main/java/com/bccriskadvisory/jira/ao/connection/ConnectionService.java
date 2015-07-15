package com.bccriskadvisory.jira.ao.connection;

import com.atlassian.activeobjects.tx.Transactional;
import com.bccriskadvisory.jira.ao.ActiveObjectsService;

@Transactional
public interface ConnectionService extends ActiveObjectsService<Connection> {
	
}
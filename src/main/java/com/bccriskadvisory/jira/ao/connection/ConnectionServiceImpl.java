package com.bccriskadvisory.jira.ao.connection;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.bccriskadvisory.jira.ao.ActiveObjectsServiceImpl;

public class ConnectionServiceImpl extends ActiveObjectsServiceImpl<ConnectionEntity, Connection>implements ConnectionService {
	
	public ConnectionServiceImpl(final ActiveObjects activeObjects) {
		super(ConnectionEntity.class, activeObjects);
	}

	@Override
	protected Connection constructFromEntity(ConnectionEntity entity) {
		return new Connection(entity);
	}
}
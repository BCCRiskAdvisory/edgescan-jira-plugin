package com.bccriskadvisory.jira.ao.projectlink;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.bccriskadvisory.jira.ao.ActiveObjectsServiceImpl;

public class ProjectLinkServiceImpl extends ActiveObjectsServiceImpl<ProjectLinkEntity, ProjectLink> implements ProjectLinkService {

	public ProjectLinkServiceImpl(final ActiveObjects activeObjects) {
		super(ProjectLinkEntity.class, activeObjects);
	}

	@Override
	protected ProjectLink constructFromEntity(ProjectLinkEntity entity) {
		return new ProjectLink(entity);
	}
}
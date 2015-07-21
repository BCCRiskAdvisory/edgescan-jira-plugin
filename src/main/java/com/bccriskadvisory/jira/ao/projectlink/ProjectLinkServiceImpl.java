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
package com.bccriskadvisory.jira.ao.projectlink;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.bccriskadvisory.jira.ao.ActiveObjectsServiceImpl;
import com.google.common.collect.Maps;

public class ProjectLinkServiceImpl extends ActiveObjectsServiceImpl<ProjectLinkEntity, ProjectLink> implements ProjectLinkService {

	private Map<Integer, ReentrantLock> importLocks = Maps.newHashMap();
	
	public ProjectLinkServiceImpl(final ActiveObjects activeObjects) {
		super(ProjectLinkEntity.class, activeObjects);
	}

	@Override
	protected ProjectLink constructFromEntity(ProjectLinkEntity entity) {
		return new ProjectLink(entity);
	}
	
	@Override
	public synchronized ReentrantLock getImportLock(ProjectLink link) {
		ReentrantLock importLock = importLocks.get(link.getID());
		
		if (importLock == null) {
			importLock = new ReentrantLock();
			importLocks.put(link.getID(), importLock);
		}
		
		return importLock;
	}
}
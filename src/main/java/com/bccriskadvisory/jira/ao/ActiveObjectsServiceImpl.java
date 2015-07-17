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
package com.bccriskadvisory.jira.ao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.java.ao.Entity;

import com.atlassian.activeobjects.external.ActiveObjects;

public abstract class ActiveObjectsServiceImpl<E extends Entity, T extends PluginObject<E>> implements ActiveObjectsService<T> {

	private final Class<E> entityType;
	private final ActiveObjects activeObjects;
	private final ReadWriteLock rwLock;

	public ActiveObjectsServiceImpl(final Class<E> entityType, final ActiveObjects activeObjects) {
		this.entityType = entityType;
		this.activeObjects = activeObjects;
		this.rwLock = new ReentrantReadWriteLock();
	}

	@Override
	public List<T> index() {
		final E[] found;
		
		try (AutoReleaseLock lock = getAutoLock().read()) {
			found = activeObjects.find(entityType);
		}
			
		return convertList(found);
	}

	@Override
	public T find(final int id) {
		final E entity;
		
		try (AutoReleaseLock lock = getAutoLock().read()) {
			entity = activeObjects.get(entityType, id);
		}
		
		return constructFromEntity(entity);
	}

	@Override
	public List<T> findBy(final String query, final Object... params) {
		E[] entities;
		
		try (AutoReleaseLock lock = getAutoLock().read()) {
			entities = activeObjects.find(entityType, query, params);
		}
		
		return convertList(entities);
	}

	@Override
	public T create(final T newObject) {
		E newEntity;
		
		try (AutoReleaseLock lock = getAutoLock().write()) {
			newEntity = activeObjects.create(entityType);
			newObject.copyTo(newEntity);
			newEntity.save();
		}
		
		return constructFromEntity(newEntity);
	}

	@Override
	public T update(final T updatedObject) {
		E newEntity;
		
		try (AutoReleaseLock lock = getAutoLock().write()) {
			newEntity = activeObjects.get(entityType, updatedObject.getID());
			updatedObject.copyTo(newEntity);
			newEntity.save();
		}
		
		return constructFromEntity(newEntity);
	}

	@Override
	public boolean delete(final T toDelete) {
		try (AutoReleaseLock lock = getAutoLock().write()) {
			final int rowsDeleted = activeObjects.deleteWithSQL(entityType, "ID = ?", toDelete.getID());
			activeObjects.flushAll();
			return rowsDeleted > 0;
		}
	}
	
	private List<T> convertList(final E[] array) {
		final List<T> ret = new ArrayList<>();
		
		for (final E e : array) {
			ret.add(constructFromEntity(e));
		}
		
		return ret;
	}
	
	protected abstract T constructFromEntity(E entity);
	
	private AutoReleaseLock getAutoLock() {
		return new AutoReleaseLock();
	}
	
	private class AutoReleaseLock implements AutoCloseable {
		
		private Lock lock;

		public AutoReleaseLock read() {
			ensureLockNotAlreadyHeld();
			lock = rwLock.readLock();
			lock.lock();
			return this;
		}
		
		public AutoReleaseLock write() {
			ensureLockNotAlreadyHeld();
			lock = rwLock.writeLock();
			lock.lock();
			return this;
		}
		
		@Override
		public void close() {
			lock.unlock();
		}
		
		private void ensureLockNotAlreadyHeld() {
			if (lock != null) {
				throw new IllegalStateException("Lock is already acquired!");
			}
		}
	}
}
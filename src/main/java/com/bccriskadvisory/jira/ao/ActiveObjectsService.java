package com.bccriskadvisory.jira.ao;

import java.util.List;

public interface ActiveObjectsService<T> {

	List<T> index();
	
	T find(final int id);
	
	List<T> findBy(String query, Object... params);
	
	T create(T newObject);
	
	T update(T updatedObject);
	
	boolean delete(T toDelete);
}
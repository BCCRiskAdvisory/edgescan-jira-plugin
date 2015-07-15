package com.bccriskadvisory.jira.ao;

import net.java.ao.Entity;

public interface PluginObject<E extends Entity> {

	public abstract int getID();

	public abstract void copyTo(E entity);
}
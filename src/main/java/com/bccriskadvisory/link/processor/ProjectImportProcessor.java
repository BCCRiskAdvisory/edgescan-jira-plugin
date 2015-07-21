package com.bccriskadvisory.link.processor;

import com.bccriskadvisory.jira.ao.projectlink.ProjectLink;

public interface ProjectImportProcessor {

	public abstract ProjectImportProcessor initWithLink(ProjectLink link);

	public abstract ImportResults processImport();

}
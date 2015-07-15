package com.bccriskadvisory.link.utility;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AbstractLogSupported {

	private Logger log = LogManager.getLogger(getClass());

	protected Logger getLog() {
		return log;
	}
}
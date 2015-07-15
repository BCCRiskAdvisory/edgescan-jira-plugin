package com.bccriskadvisory.link.utility;

import java.util.concurrent.TimeUnit;

public class TimedTask extends AbstractLogSupported implements AutoCloseable {

	private final String taskDescription;
	private final long startTimeMillis;
	private boolean logInfo;

	public TimedTask(final String taskDescription) {
		this(taskDescription, false);
	}
	
	public TimedTask(final String taskDescription, boolean logInfo) {
		this.taskDescription = taskDescription;
		this.logInfo = logInfo;
		this.startTimeMillis = System.currentTimeMillis();
		logMessage("Task [" + taskDescription + "] started.");
	}
	
	@Override
	public void close() {
		long timeElapsed = System.currentTimeMillis() - startTimeMillis;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(minutes);
		
		logMessage(String.format("Task [%s] finished, in %d minutes and %d seconds.", taskDescription, minutes, seconds));
	}

	private void logMessage(final String message) {
		if (logInfo) {
			getLog().info(message);
		} else {
			getLog().debug(message);
		}
	}
}
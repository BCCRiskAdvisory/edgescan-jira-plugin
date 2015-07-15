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
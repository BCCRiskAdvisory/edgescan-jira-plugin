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

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class Utilities {

	private static final DateTimeZone UTC = DateTimeZone.UTC;

	public static boolean isNullOrEmpty(String input) {
		return Strings.isNullOrEmpty(input);
	}
	
	public static boolean isNotNullOrEmpty(String input) {
		return !isNullOrEmpty(input);
	}
	
	public static DateTime now() {
		return DateTime.now(UTC);
	}
	
	public static Date toDate(DateTime date) {
		if (date != null) {
			return date.toDate();
		} else {
			return null;
		}
	}
	
	public static DateTime fromDate(Date date) {
		if (date == null) return null;
		
		try {
			return new DateTime(date, UTC);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static DateTime fromTimestamp(Timestamp timestamp) {
		if (timestamp == null) return null;
		
		try {
			return new DateTime(timestamp, UTC);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static DateTime fromString(String dateString, DateTimeFormatter formatter) {
		if (dateString == null || formatter == null) return null;
		
		return DateTime.parse(dateString, formatter);
	}
}
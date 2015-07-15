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
import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

import com.google.common.base.Strings;

public class Utilities {

	private static final ZoneOffset UTC = ZoneOffset.UTC;

	public static boolean isNullOrEmpty(String input) {
		return Strings.isNullOrEmpty(input);
	}
	
	public static boolean isNotNullOrEmpty(String input) {
		return !isNullOrEmpty(input);
	}
	
	public static ZonedDateTime now() {
		return ZonedDateTime.now(UTC);
	}
	
	public static Date toDate(Optional<ZonedDateTime> date) {
		if (date.isPresent()) {
			return Date.from(date.get().toInstant());
		} else {
			return null;
		}
	}
	
	public static Optional<ZonedDateTime> fromDate(Date date) {
		if (date == null) return Optional.empty();
		
		try {
			return Optional.of(ZonedDateTime.ofInstant(date.toInstant(), UTC));
		} catch (DateTimeException e) {
			return Optional.empty();
		}
	}
	
	public static Optional<ZonedDateTime> fromTimestamp(Timestamp timestamp) {
		if (timestamp == null) return Optional.empty();
		
		try {
			return Optional.of(ZonedDateTime.ofInstant(timestamp.toInstant(), UTC));
		} catch (DateTimeException e) {
			return Optional.empty();
		}
	}
	
	public static Optional<ZonedDateTime> fromString(String dateString, DateTimeFormatter formatter) {
		if (dateString == null || formatter == null) return Optional.empty();
		
		try {
			return Optional.of(ZonedDateTime.parse(dateString, formatter));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}
}
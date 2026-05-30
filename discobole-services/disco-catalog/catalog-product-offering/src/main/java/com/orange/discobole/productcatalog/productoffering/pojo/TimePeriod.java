// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;

import java.time.OffsetDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.orange.discobole.processflow.annotation.Format;
import jakarta.validation.constraints.NotNull;

/**
 * A period of time, either as a deadline (endDateTime only) a startDateTime
 * only, or both
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class TimePeriod {
	@JsonProperty("endDateTime")
	@Format("date-time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	private OffsetDateTime endDateTime = null;

	@JsonProperty("startDateTime")
	@Format("date-time")
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	private OffsetDateTime startDateTime;

	public TimePeriod endDateTime(OffsetDateTime endDateTime) {
		this.endDateTime = endDateTime;
		return this;
	}

	/**
	 * End of the time period, using IETC-RFC-3339 format
	 *
	 * @return endDateTime
	 **/
	public OffsetDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(OffsetDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public TimePeriod startDateTime(OffsetDateTime startDateTime) {
		this.startDateTime = startDateTime;
		return this;
	}

	/**
	 * Start of the time period, using IETC-RFC-3339 format. If you define a start,
	 * you must also define an end
	 *
	 * @return startDateTime
	 **/
	public OffsetDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(OffsetDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
}

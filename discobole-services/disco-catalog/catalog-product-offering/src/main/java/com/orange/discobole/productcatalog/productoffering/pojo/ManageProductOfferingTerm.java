// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;

import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Array
public class ManageProductOfferingTerm {
	@NotNull
	private Duration duration;
	@NotBlank
	private String name;
	private String description;
	private TimePeriod validFor;

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}
}

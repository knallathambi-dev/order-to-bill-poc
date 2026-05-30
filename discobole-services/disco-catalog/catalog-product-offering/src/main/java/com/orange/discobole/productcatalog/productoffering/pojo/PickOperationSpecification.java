// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.processflow.annotation.ReadOnly;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Diksha Srivastava
 * @since 1.0
 *
 */
@Array
public class PickOperationSpecification {

	@NotBlank
	@JsonProperty("serviceSpecification.operationSpecification.id")
	@ReadOnly(true)
	private String id;
	private String description;
	@NotBlank
	private String name;
	private TimePeriod validFor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public void setValidFor(TimePeriod validFor) {
		this.validFor = validFor;
	}

}

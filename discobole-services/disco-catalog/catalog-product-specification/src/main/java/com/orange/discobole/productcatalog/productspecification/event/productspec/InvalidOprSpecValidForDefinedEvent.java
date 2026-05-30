// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.Map;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;

/**
 * The Class InvalidOprSpecValidForDefinedEvent generated when valid is not
 * within range of CFS.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class InvalidOprSpecValidForDefinedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final Map<String, TimePeriod> invalidValidForDefined;

	private InvalidOprSpecValidForDefinedEvent() {
		productSpecId = null;
		invalidValidForDefined = null;
	}

	public InvalidOprSpecValidForDefinedEvent(String productSpecId, Map<String, TimePeriod> invalidValidForSelected) {
		this.productSpecId = productSpecId;
		invalidValidForDefined = invalidValidForSelected;
	}

	@Override
	public String toString() {
		return "InvalidOprSpecValidForDefinedEvent [productSpecId=" + productSpecId + ", invalidValidForDefined="
				+ invalidValidForDefined + "]";
	}

	public String getProductSpecId() {
		return productSpecId;
	}

	public Map<String, TimePeriod> getInvalidValidForDefined() {
		return invalidValidForDefined;
	}

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;


public class InvalidProductSpecValidForEvent implements ProductSpecEvent {

	private final TimePeriod validFor;
	private final String error;
	
	private InvalidProductSpecValidForEvent() {
		this.validFor = null;
		this.error = null;
	}

	public InvalidProductSpecValidForEvent(TimePeriod validFor, String error) {
		this.validFor = validFor;
		this.error = error;
	}

	@Override
	public String toString() {
		return "ProductSpecValidForInvalidEvent [validFor=" + validFor + ", error=" + error + "]";
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public String getError() {
		return error;
	}

}

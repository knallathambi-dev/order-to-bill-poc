// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.productoffering;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

/**
 * The Class InvalidProductOfferingValidForEvent raises invalid event when
 * product offering valid for is not in range between product specification
 * valid for.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class InvalidProductOfferingValidForEvent implements ProductOfferingEvent {

	
	private final TimePeriod validFor;
	private final String reason;

	private InvalidProductOfferingValidForEvent() {
		
		this.validFor = null;
		this.reason = null;
	}

	public InvalidProductOfferingValidForEvent( TimePeriod validFor, String reason) {
		
		this.validFor = validFor;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "InvalidProductOfferingValidForEvent [validFor=" + validFor + ", reason=" + reason+ "]";
	}


	public TimePeriod getValidFor() {
		return validFor;
	}

	public String getReason() {
		return reason;
	}

	

}

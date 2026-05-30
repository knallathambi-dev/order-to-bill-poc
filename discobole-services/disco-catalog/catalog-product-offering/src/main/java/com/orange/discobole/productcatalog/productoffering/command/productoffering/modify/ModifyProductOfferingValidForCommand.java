// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

public class ModifyProductOfferingValidForCommand {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final TimePeriod validFor;

	/**
	 * @param validFor
	 */
	public ModifyProductOfferingValidForCommand(String productOfferingId,TimePeriod validFor) {
		this.productOfferingId = productOfferingId;
		this.validFor = validFor;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingValidForCommand [validFor=" + validFor + "]";
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}

	
}

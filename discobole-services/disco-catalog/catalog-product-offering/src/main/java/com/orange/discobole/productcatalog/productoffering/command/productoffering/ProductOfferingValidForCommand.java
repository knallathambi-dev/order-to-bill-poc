// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.TimePeriod;

/**
 * The Class ProductOfferingValidForCommand defines Product Offering Term using
 * Valid for.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class ProductOfferingValidForCommand {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final TimePeriod validFor;

	public ProductOfferingValidForCommand(String productOfferingId,TimePeriod validFor) {
		this.productOfferingId = productOfferingId;
		this.validFor = validFor;
	}

	@Override
	public String toString() {
		return "ProductOfferingValidForCommand [validFor=" + validFor +  "productOfferingId=" + productOfferingId + "]";
	}

	public TimePeriod getValidFor() {
		return validFor;
	}

}

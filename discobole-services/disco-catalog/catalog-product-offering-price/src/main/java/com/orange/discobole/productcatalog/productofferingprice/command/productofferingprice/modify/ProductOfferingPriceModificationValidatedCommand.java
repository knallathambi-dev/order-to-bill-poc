// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * This class acts as a command that gets triggered to save the {@code POP}.
 *
 * @author Rajan Chauhan
 * @since 1.0
 */
public final class ProductOfferingPriceModificationValidatedCommand {

	@TargetAggregateIdentifier
	private String popId;
	private String versionType;

	public ProductOfferingPriceModificationValidatedCommand(String popId, String versionType) {
		this.popId = popId;
		this.versionType = versionType;
	}

	public String getPopId() {
		return popId;
	}

	public void setPopId(String popId) {
		this.popId = popId;
	}

	public String getVersionType() {
		return versionType;
	}

	public void setVersionType(String versionType) {
		this.versionType = versionType;
	}
	
	@Override
	public String toString() {
		return "ProductOfferingPriceModificationValidatedCommand [popId=" + popId + ", versionType" + versionType + "]";
	}

}

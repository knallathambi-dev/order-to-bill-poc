// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering.modify;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;

public class ModifyProductOfferingCharacteristicCommand {
	
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics;

	/**
	 * @param pickAtomicProductOfferingCharacteristics
	 */
	public ModifyProductOfferingCharacteristicCommand(String productOfferingId,
			List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics) {
		this.productOfferingId = productOfferingId;
		this.pickAtomicProductOfferingCharacteristics = pickAtomicProductOfferingCharacteristics;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingCharacteristicCommand [pickAtomicProductOfferingCharacteristics="
				+ pickAtomicProductOfferingCharacteristics + "]";
	}

	public List<PickAtomicProductOfferingCharacteristic> getPickAtomicProductOfferingCharacteristics() {
		return pickAtomicProductOfferingCharacteristics;
	}

	public String getProductOfferingId() {
		return productOfferingId;
	}
	
	

}

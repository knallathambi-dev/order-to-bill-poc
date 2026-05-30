// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.command.productoffering;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationCharacteristicValueUse;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
/**
 * The Class SelectProductOfferingCharacteristicCommand to update ProductOffering characteristics.
 *
 * @author Shreya Sharma
 * @since 1.0
 */
public class SelectProductOfferingCharacteristicCommand
{   
	@TargetAggregateIdentifier
	private final String productOfferingId;
    private final List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics;

        public SelectProductOfferingCharacteristicCommand(String productOfferingId,List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics) {
            this.productOfferingId = productOfferingId;
        	this.pickAtomicProductOfferingCharacteristics = pickAtomicProductOfferingCharacteristics;
        }

    public List<PickAtomicProductOfferingCharacteristic> getPickAtomicProductOfferingCharacteristics() {
        return pickAtomicProductOfferingCharacteristics;
    }

        public String getProductOfferingId() {
		return productOfferingId;
	}

		@Override
        public String toString() {
            return "SelectProductOfferingCharacteristicCommand{" +
                    "pickAtomicProductOfferingCharacteristics=" + pickAtomicProductOfferingCharacteristics +
                    "productOfferingId=" + productOfferingId + '}';
        }
}

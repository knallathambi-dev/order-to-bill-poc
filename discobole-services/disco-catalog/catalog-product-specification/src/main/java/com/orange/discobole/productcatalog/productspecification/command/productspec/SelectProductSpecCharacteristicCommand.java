// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.command.productspec;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;

public final class SelectProductSpecCharacteristicCommand {

	@TargetAggregateIdentifier
	private final String productSpecId;
    private final List<ProductSpecificationCharacteristic> productSpecCharacteristics;
    private final List<UsageSpecification> usageSpecifications;

    public SelectProductSpecCharacteristicCommand(String productSpecId,List<ProductSpecificationCharacteristic> productSpecCharacteristics,List<UsageSpecification> usageSpecifications) {
        this.productSpecCharacteristics = productSpecCharacteristics;
		this.productSpecId = productSpecId;
		this.usageSpecifications=usageSpecifications;
    }

    public List<ProductSpecificationCharacteristic> getProductSpecCharacteristics() {
        return productSpecCharacteristics;
    }
    
	public List<UsageSpecification> getUsageSpecifications() {
		return usageSpecifications;
	}

	@Override
	public String toString() {
		return "SelectProductSpecCharacteristicCommand{" + "productSpecId=" + productSpecId
				+ ", productSpecCharacteristics=" + productSpecCharacteristics + ", usageSpecifications="
				+ usageSpecifications + '}';
	}

	public String getAggregateId() {
		return productSpecId;
	}
		
}

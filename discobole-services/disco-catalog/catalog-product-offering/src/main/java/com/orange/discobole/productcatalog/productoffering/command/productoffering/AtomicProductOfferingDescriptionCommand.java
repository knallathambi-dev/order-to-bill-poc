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

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

/**
 * The Class AtomicProductOfferingDescriptionCommand used to set the description
 * for selected product offering.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
public final class AtomicProductOfferingDescriptionCommand {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final String description;
	private final String name;
	private final String statusReason;
	private final String brand;
	private final ProductOfferingType type;
	private final Boolean isInstallable;

	public AtomicProductOfferingDescriptionCommand(String productOfferingId,String description, String name, String statusReason, String brand,
			ProductOfferingType type, Boolean isInstallable) {
		this.description = description;
		this.name = name;
		this.statusReason = statusReason;
		this.brand = brand;
		this.type = type;
		this.isInstallable = isInstallable;
		this.productOfferingId = productOfferingId;
	}

	@Override
	public String toString() {
		return "AtomicProductOfferingDescriptionCommand [description=" + description + ", name=" + name
				+ ", statusReason=" + statusReason + ", brand=" + brand + ", type=" + type + ", productOfferingId= " + productOfferingId + "]";
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public String getproductOfferingId() {
		return productOfferingId;
	}

	public String getBrand() {
		return brand;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public Boolean isIsInstallable() {
		return isInstallable;
	}

}

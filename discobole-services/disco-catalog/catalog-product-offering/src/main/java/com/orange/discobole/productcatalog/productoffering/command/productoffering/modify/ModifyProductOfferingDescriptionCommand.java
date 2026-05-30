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

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

/**
 * The Class AtomicProductOfferingDescriptionCommand used to set the description
 * for selected product offering.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
public class ModifyProductOfferingDescriptionCommand {
    
	@TargetAggregateIdentifier
	private String productOfferingId;
	private final String description;
	private final String name;
	private final String statusReason;
	private final String brand;
	private final ProductOfferingType type;
	private final Boolean isInstallable;

	/**
	 * @param description
	 * @param name
	 * @param statusReason
	 * @param brand
	 * @param type
	 * @param isInstallable
	 */
	public ModifyProductOfferingDescriptionCommand( String productOfferingId,String description, String name, String statusReason, String brand,
			ProductOfferingType type, Boolean isInstallable) {
		this.productOfferingId = productOfferingId;
		this.description = description;
		this.name = name;
		this.statusReason = statusReason;
		this.brand = brand;
		this.type = type;
		this.isInstallable = isInstallable;
	}

	@Override
	public String toString() {
		return "ModifyProductOfferingDescriptionCommand [description=" + description + ", name=" + name
				+ ", statusReason=" + statusReason + ", brand=" + brand + ", type=" + type + ", isInstallable="
				+ isInstallable + "]";
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

	/**
	 * @return the productOfferingId
	 */
	public String getProductOfferingId() {
		return productOfferingId;
	}


	public String getBrand() {
		return brand;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public Boolean getIsInstallable() {
		return isInstallable;
	}

}

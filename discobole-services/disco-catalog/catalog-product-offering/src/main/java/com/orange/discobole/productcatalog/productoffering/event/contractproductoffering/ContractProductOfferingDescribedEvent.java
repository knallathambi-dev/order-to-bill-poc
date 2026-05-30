// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.event.contractproductoffering;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.BillingType;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;

/**
 * The Class ContractProductOfferingDescribedEvent.
 *
 * @author BMKJ8547
 *
 */
public final class ContractProductOfferingDescribedEvent implements ContractProductOfferingEvent {
    
	@TargetAggregateIdentifier
	private final String productOfferingId;
	private final String description;
	private final String statusReason;
	private final String name;
	private final String brand;
	private final ProductOfferingType type;
	private final Boolean isInstallable;
	private final OffsetDateTime lastUpdate;
	private final BillingType billingType;

	/**
	 * @param productOfferingId
	 * @param description
	 * @param statusReason
	 * @param name
	 * @param brand
	 * @param type
	 * @param isInstallable
	 * @param lastUpdate
	 * @param billingType
	 */
	public ContractProductOfferingDescribedEvent(String productOfferingId, String description, String statusReason,
			String name, String brand, ProductOfferingType type, Boolean isInstallable, OffsetDateTime lastUpdate, BillingType billingType) {
		this.productOfferingId = productOfferingId;
		this.description = description;
		this.statusReason = statusReason;
		this.name = name;
		this.brand = brand;
		this.type = type;
		this.isInstallable = isInstallable;
		this.lastUpdate = lastUpdate;
		this.billingType = billingType;
	
	}

	public ContractProductOfferingDescribedEvent() {
		this.productOfferingId = null;
		this.description = null;
		this.statusReason = null;
		this.name = null;
		this.brand = null;
		this.type = null;
		this.isInstallable = null;
		this.lastUpdate = null;
		this.billingType = null;

	}

	@Override
	public String toString() {
		return "ContractProductOfferingDescribedEvent [productOfferingId=" + productOfferingId + ", description="
				+ description + ", statusReason=" + statusReason + ", name=" + name + ", brand=" + brand + ", type="
				+ type + ", isInstallable=" + isInstallable + ", lastUpdate=" + lastUpdate + ", billingType="
				+ billingType + "]";
	}


	public String getProductOfferingId() {
		return productOfferingId;
	}


	public String getDescription() {
		return description;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public String getName() {
		return name;
	}

	public String getBrand() {
		return brand;
	}

	public ProductOfferingType getType() {
		return type;
	}

	public Boolean IsInstallable() {
		return isInstallable;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public BillingType getBillingType() {
		return billingType;
	}

}

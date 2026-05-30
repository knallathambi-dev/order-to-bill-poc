// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.time.OffsetDateTime;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.productspec.ProductSpecCancelCommand}
 * is triggered.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ProductSpecModificationValidatedEvent implements ProductSpecEvent{
	
	@TargetAggregateIdentifier
	private final String productSpecificationId;
	private final ProductSpecificationLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;
	private final  String version ;
	private final ProductSpecification storedProductSpecification;
	public ProductSpecModificationValidatedEvent() {
		super();
		this.productSpecificationId = null;
		this.lifecycleStatus = null;
		this.lastUpdate = null;
		this.version = null;
		this.storedProductSpecification = null;
	}
	public ProductSpecModificationValidatedEvent(String productSpecificationId,
			ProductSpecificationLifecycle lifecycleStatus, OffsetDateTime lastUpdate, String version,
			ProductSpecification storedProductSpecification) {
		super();
		this.productSpecificationId = productSpecificationId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;
		this.version = version;
		this.storedProductSpecification = storedProductSpecification;
	}
	public String getProductSpecificationId() {
		return productSpecificationId;
	}
	public ProductSpecificationLifecycle getLifecycleStatus() {
		return lifecycleStatus;
	}
	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}
	public String getVersion() {
		return version;
	}
	public ProductSpecification getStoredProductSpecification() {
		return storedProductSpecification;
	}

	
}

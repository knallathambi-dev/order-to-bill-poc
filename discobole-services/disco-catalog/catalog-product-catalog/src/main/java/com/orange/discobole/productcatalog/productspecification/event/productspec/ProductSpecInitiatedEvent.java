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

import com.orange.discobole.productcatalog.catalog.dto.generated.common.EntityType;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ServiceSpecificationRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.StockItemType;

/**
 * This class represents an event that is raised when
 * {@code com.orange.bos.catalogconfigurator.command.InitiateProductSpecCommand}
 * is triggered.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public final class ProductSpecInitiatedEvent implements ProductSpecEvent {

	private final String productSpecId;
	private final ProductSpecificationLifecycle resourceState;
	private final String supportEntity;
	private final ServiceSpecificationRef serviceSpecificationRef;
	private final OffsetDateTime lastUpdate;
	private final StockItemType stockItemType;
	private EntityType baseType;

	private ProductSpecInitiatedEvent() {
		productSpecId = null;
		resourceState = null;
		supportEntity = null;
		serviceSpecificationRef = null;
		lastUpdate = null;
		stockItemType = null;
		baseType = null;
	}

	
	public ProductSpecInitiatedEvent(String productSpecId, ProductSpecificationLifecycle resourceState,
			String supportEntity, ServiceSpecificationRef serviceSpecificationRef, OffsetDateTime lastUpdate, StockItemType stockItemType , EntityType baseType) {
		super();
		this.productSpecId = productSpecId;
		this.resourceState = resourceState;
		this.supportEntity = supportEntity;
		this.serviceSpecificationRef = serviceSpecificationRef;
		this.lastUpdate = lastUpdate;
		this.stockItemType = stockItemType;
		this.baseType = baseType;
	}

	/**
	 * This method represents the {@code ProductSpecInitiatedEvent} object with its
	 * data.
	 *
	 * @return returns the string representation of
	 *         {@code ProductSpecInitiatedEvent} data
	 */
	
	@Override
	public String toString() {
		return "ProductSpecInitiatedEvent [productSpecId=" + productSpecId + ", aggregateId=" + ", resourceState="
				+ resourceState + ", supportEntity=" + supportEntity + ", serviceSpecificationRef="
				+ serviceSpecificationRef + ", lastUpdate=" + lastUpdate + ", stockItemType=" + stockItemType + ", baseType=" + baseType +"]";
	}
	
	/**
	 * This method is used to get product specification id.
	 *
	 * @return returns the product specification id
	 */
	public String getProductSpecId() {
		return productSpecId;
	}
	
	/**
	 * This method is used to get lifecycle state of product specification.
	 *
	 * @return returns the product specification lifecycle state
	 */
	public ProductSpecificationLifecycle getResourceState() {
		return resourceState;
	}

	/**
	 * This method is used to get the service specification reference with product
	 * specification.
	 *
	 * @return returns the reference of service specification
	 */
	public ServiceSpecificationRef getServiceSpecificationRef() {
		return serviceSpecificationRef;
	}

	/**
	 * This method is used to get the support entity of product specification.
	 *
	 * @return returns the support entity
	 */
	public String getSupportEntity() {
		return supportEntity;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	public StockItemType getStockItemType() { return stockItemType; }


	public EntityType getBaseType() {
		return baseType;
	}
}

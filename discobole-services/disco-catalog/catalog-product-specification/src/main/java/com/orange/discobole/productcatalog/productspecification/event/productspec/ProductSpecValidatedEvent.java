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

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecificationLifecycle;

/**
 * The Class ProductSpecValidatedEvent stores the updated lifecycle status of
 * product specification when the creation is complete.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public final class ProductSpecValidatedEvent implements ProductSpecEvent {

	@TargetAggregateIdentifier
	private final String productSpecificationId;
	private final ProductSpecificationLifecycle lifecycleStatus;
	private final OffsetDateTime lastUpdate;

	private ProductSpecValidatedEvent() {
		productSpecificationId = null;
		lifecycleStatus = null;
		lastUpdate = null;
	}

	public ProductSpecValidatedEvent(String productSpecificationId, ProductSpecificationLifecycle lifecycleStatus,
			OffsetDateTime lastUpdate) {
		this.productSpecificationId = productSpecificationId;
		this.lifecycleStatus = lifecycleStatus;
		this.lastUpdate = lastUpdate;

	}

	@Override
	public String toString() {
		return "ProductSpecValidatedEvent [ productSpecificationId="
				+ productSpecificationId + ", lifecycleStatus=" + lifecycleStatus + ", lastUpdate=" + lastUpdate + "]";
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

}

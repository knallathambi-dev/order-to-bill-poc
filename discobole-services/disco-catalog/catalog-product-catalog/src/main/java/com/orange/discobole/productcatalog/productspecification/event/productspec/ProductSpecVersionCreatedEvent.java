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

/**
 * The Class ProductSpecVersionCreatedEvent stores the created version
 * of product specification.
 *
 * @author Vivek Singh
 * @since 1.0
 */
public final class ProductSpecVersionCreatedEvent implements ProductSpecEvent {

	private final String productSpecificationId;
	private final String productSpecificationVersion;
	private final OffsetDateTime lastUpdate;

	public ProductSpecVersionCreatedEvent() {
		productSpecificationId = null;
		productSpecificationVersion = null;
		lastUpdate = null;
	}

	public ProductSpecVersionCreatedEvent(String productSpecificationId, String productSpecificationVersion,
			OffsetDateTime lastUpdate) {

		this.productSpecificationId = productSpecificationId;
		this.productSpecificationVersion = productSpecificationVersion;
		this.lastUpdate = lastUpdate;
	}

	public String getProductSpecificationId() {
		return productSpecificationId;
	}

	public String getProductSpecificationVersion() {
		return productSpecificationVersion;
	}

	public OffsetDateTime getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String toString() {
		return "ProductSpecVersionCreatedEvent{" +
				"productSpecificationId='" + productSpecificationId + '\'' +
				", productSpecificationVersion='" + productSpecificationVersion + '\'' +
				", lastUpdate=" + lastUpdate +
				'}';
	}
}

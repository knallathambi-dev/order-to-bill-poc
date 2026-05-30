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
import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductConfigurationSpec;

/**
 * The Class LinkProductSpecificationToStockItemEvent generated when productSpecificationId is associated
 * with stockItems.
 *
 * @author Varshika Choudhary
 * @since 1.0
 */
public class LinkProductSpecificationToStockItemEvent implements ProductSpecEvent {

    private final String productSpecificationId;
    private final List<ProductConfigurationSpec> productConfiguration;
    private final OffsetDateTime lastUpdate;

    public LinkProductSpecificationToStockItemEvent() {
        this.productSpecificationId = null;
        this.productConfiguration = null;
        this.lastUpdate = null;
    }

    public LinkProductSpecificationToStockItemEvent(String productSpecificationId, List<ProductConfigurationSpec> productConfiguration, OffsetDateTime lastUpdate) {
        this.productSpecificationId = productSpecificationId;
        this.productConfiguration = productConfiguration;
        this.lastUpdate = lastUpdate;
    }

    public String getProductSpecificationId() {
        return productSpecificationId;
    }

    public List<ProductConfigurationSpec> getProductConfiguration() {
        return productConfiguration;
    }

    public OffsetDateTime getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return "LinkProductSpecificationToStockItemEvent{" +
                "productSpecificationId='" + productSpecificationId + '\'' +
                ", ProductConfigurationSpec=" + productConfiguration +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}

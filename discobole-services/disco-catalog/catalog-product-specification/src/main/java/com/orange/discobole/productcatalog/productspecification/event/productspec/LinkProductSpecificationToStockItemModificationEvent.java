// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.ProductConfigurationSpec;

import java.time.OffsetDateTime;
import java.util.List;

public class LinkProductSpecificationToStockItemModificationEvent implements ProductSpecEvent {

    @TargetAggregateIdentifier
    private final String productSpecificationId;
    private final List<ProductConfigurationSpec> productConfiguration;
    private final OffsetDateTime lastUpdate;

    public LinkProductSpecificationToStockItemModificationEvent(String productSpecificationId, List<ProductConfigurationSpec> productConfiguration, OffsetDateTime lastUpdate) {
        this.productSpecificationId = productSpecificationId;
        this.productConfiguration = productConfiguration;
        this.lastUpdate = lastUpdate;
    }

    public LinkProductSpecificationToStockItemModificationEvent() {
        this.productSpecificationId = null;
        this.productConfiguration = null;
        this.lastUpdate = null;
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
        return "LinkProductSpecificationToStockItemModificationEvent{" + "productSpecificationId='" + productSpecificationId
                + ", ProductConfigurationSpec=" + productConfiguration + ", lastUpdate=" + lastUpdate + '}';
    }
}

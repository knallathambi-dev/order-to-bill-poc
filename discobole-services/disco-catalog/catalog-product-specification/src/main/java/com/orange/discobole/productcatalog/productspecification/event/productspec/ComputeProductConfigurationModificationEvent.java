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

import java.util.List;

public class ComputeProductConfigurationModificationEvent implements ProductSpecEvent {

    @TargetAggregateIdentifier
    private final String prodSpecId;
    private final List<ProductConfigurationSpec> productConfiguration;

    private ComputeProductConfigurationModificationEvent() {
        this.productConfiguration = null;
        this.prodSpecId = null;
    }

    public ComputeProductConfigurationModificationEvent(List<ProductConfigurationSpec> productSpecification, String prodSpecId) {
        this.productConfiguration = productSpecification;
        this.prodSpecId = prodSpecId;
    }

    @Override
    public String toString() {
        return "ComputeProductConfigurationModificationEvent{" + "productConfiguration=" + productConfiguration + ", prodSpecId='"
                + prodSpecId + '}';
    }

    public List<ProductConfigurationSpec> getProductConfiguration() {
        return productConfiguration;
    }

    public String getProdSpecId() {
        return prodSpecId;
    }

}

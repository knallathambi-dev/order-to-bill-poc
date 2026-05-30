// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.List;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductConfigurationSpec;

public class ComputeProductConfigurationEvent implements ProductSpecEvent {

    private final List<ProductConfigurationSpec> productConfiguration;
    private final String prodSpecId;

    private ComputeProductConfigurationEvent() {
        this.productConfiguration = null;
        this.prodSpecId = null;
    }

    public ComputeProductConfigurationEvent(List<ProductConfigurationSpec> productSpecification, String prodSpecId) {
        this.productConfiguration = productSpecification;
        this.prodSpecId = prodSpecId;
    }

    @Override
    public String toString() {
        return "ComputeProductConfigurationEvent{" +
                "productConfiguration=" + productConfiguration +
                ", prodSpecId='" + prodSpecId + '\'' +
                '}';
    }

    public List<ProductConfigurationSpec> getProductConfiguration() {
        return productConfiguration;
    }

    public String getProdSpecId() {
        return prodSpecId;
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductConfigurationSpec;

public class ProductConfigurationModificationEvent implements ProductSpecEvent{
    private final String prodSpecId;
    private final ProductConfigurationSpec productConfiguration;

    private ProductConfigurationModificationEvent() {
        this.productConfiguration = null;
        this.prodSpecId = null;
    }

    public ProductConfigurationModificationEvent(String prodSpecId, ProductConfigurationSpec productSpecification) {
        this.productConfiguration = productSpecification;
        this.prodSpecId = prodSpecId;
    }

    @Override
    public String toString() {
        return "ProductConfigurationModificationEvent{" + "productConfiguration=" + productConfiguration + ", prodSpecId='"
                + prodSpecId + '}';
    }

    public ProductConfigurationSpec getProductConfiguration() {
        return productConfiguration;
    }

    public String getProdSpecId() {
        return prodSpecId;
    }
}

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
import java.util.Map;

public final class ProductSpecCharacterisiticValuesSelectedEvent implements ProductSpecEvent {

    private final String productSpecId;
    private final Map<String, List<Object>> selectedServiceSpecCharacteristicValues;

    public ProductSpecCharacterisiticValuesSelectedEvent() {
        this.productSpecId = null;
        this.selectedServiceSpecCharacteristicValues = null;
    }

    public ProductSpecCharacterisiticValuesSelectedEvent(String productSpecId, Map<String, List<Object>> selectedServiceSpecCharacteristicValues) {
        this.productSpecId = productSpecId;
        this.selectedServiceSpecCharacteristicValues = selectedServiceSpecCharacteristicValues;
    }

    @Override
    public String toString() {
        return "ProductSpecCharacterisiticValuesSelectedEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", selectedServiceSpecCharacteristicValues=" + selectedServiceSpecCharacteristicValues +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

    public Map<String, List<Object>> getSelectedServiceSpecCharacteristicValues() {
        return selectedServiceSpecCharacteristicValues;
    }
}

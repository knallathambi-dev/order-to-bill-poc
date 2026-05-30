// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

import java.util.Set;

public final class InvalidProductSpecCharateristicsValuesSelectedEvent implements ProductSpecEvent {

    private final String productSpecId;
    private final Set<String> invalidCharacteristicsValuesSelected;

    public InvalidProductSpecCharateristicsValuesSelectedEvent() {
        productSpecId = null;
        invalidCharacteristicsValuesSelected = null;
    }

    public InvalidProductSpecCharateristicsValuesSelectedEvent(String productSpecId, Set<String> invalidCharacteristicsValuesSelected) {
        this.productSpecId = productSpecId;
        this.invalidCharacteristicsValuesSelected = invalidCharacteristicsValuesSelected;
    }

    @Override
    public String toString() {
        return "InvalidProductSpecCharateristicsValuesSelectedEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", invalidCharacteristicsValuesSelected=" + invalidCharacteristicsValuesSelected +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

    public Set<String> getInvalidCharacteristicsValuesSelected() {
        return invalidCharacteristicsValuesSelected;
    }
}

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

public final class InvalidProductSpecCharateristicSelectedEvent implements ProductSpecEvent {

    private final String productSpecId;
    private final Set<String> invalidCharacteristicsSelected;

    public InvalidProductSpecCharateristicSelectedEvent() {
        productSpecId=null;
        invalidCharacteristicsSelected=null;
    }

    public InvalidProductSpecCharateristicSelectedEvent(String productSpecId, Set<String> invalidCharacteristicsSelected) {
        this.productSpecId = productSpecId;
        this.invalidCharacteristicsSelected = invalidCharacteristicsSelected;
    }

    @Override
    public String toString() {
        return "InvalidProductSpecCharateristicSelectedEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", invalidCharacteristicsSelected=" + invalidCharacteristicsSelected +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

    public Set<String> getInvalidCharacteristicsSelected() {
        return invalidCharacteristicsSelected;
    }
}

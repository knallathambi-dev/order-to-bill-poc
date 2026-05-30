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

public final class InvalidOperationsSelectedEvent implements ProductSpecEvent {
    private final String productSpecId;
    private final Set<String> invalidOpsSelected;

    private InvalidOperationsSelectedEvent() {
        this.productSpecId= null;
        this.invalidOpsSelected = null;
    }

    public InvalidOperationsSelectedEvent(String productSpecId, Set<String> invalidOpsSelected) {
        this.productSpecId = productSpecId;
        this.invalidOpsSelected = invalidOpsSelected;
    }

    @Override
    public String toString() {
        return "InvalidOperationsSelectedEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                ", invalidOpsSelected=" + invalidOpsSelected +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }

    public Set<String> getInvalidOpsSelected() {
        return invalidOpsSelected;
    }
}

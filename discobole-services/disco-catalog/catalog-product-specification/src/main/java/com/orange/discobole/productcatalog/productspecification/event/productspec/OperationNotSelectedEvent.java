// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.event.productspec;

public final class OperationNotSelectedEvent implements ProductSpecEvent {

    private final String productSpecId;

    public OperationNotSelectedEvent(String productSpecId) {
        this.productSpecId = productSpecId;
    }

    public OperationNotSelectedEvent() {
        this.productSpecId = null;
    }

    @Override
    public String toString() {
        return "OperationNotSelectedEvent{" +
                "productSpecId='" + productSpecId + '\'' +
                '}';
    }

    public String getProductSpecId() {
        return productSpecId;
    }
}

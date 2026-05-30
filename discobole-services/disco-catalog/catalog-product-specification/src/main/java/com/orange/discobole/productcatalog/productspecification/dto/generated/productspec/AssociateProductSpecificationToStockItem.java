// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.dto.generated.productspec;

public class AssociateProductSpecificationToStockItem {
    private String productSpecId;
    private String stockItemId;

    public String getProductSpecId() {
        return productSpecId;
    }

    public void setProductSpecId(String productSpecId) {
        this.productSpecId = productSpecId;
    }

    public String getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(String stockItemId) {
        this.stockItemId = stockItemId;
    }
}

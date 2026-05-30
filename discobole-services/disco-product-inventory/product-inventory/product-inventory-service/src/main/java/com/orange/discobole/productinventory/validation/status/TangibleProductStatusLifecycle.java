// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.validation.status;

import com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType;
import com.orange.discobole.productinventory.dto.v1.ProductStatusType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.orange.discobole.productinventory.dto.v1.ProductOperationalStatusType.*;
class TangibleProductStatusLifecycle {

    public static final Map<ProductStatusType, Set<ProductOperationalStatusType>> MAP_TANGIBLE_PRODUCT_STATUS_MAIN_TO_OPERATION =
            Map.ofEntries(
                    Map.entry(ProductStatusType.CREATED, EnumSet.of(CREATED, CONFIRMED, PENDINGCANCEL, LOCKED, PENDINGDELIVERY, CANCELLED)),
                    Map.entry(ProductStatusType.SOLD, EnumSet.of(SOLD)),
                    Map.entry(ProductStatusType.ABORTED, EnumSet.of(ABORTED)),
                    Map.entry(ProductStatusType.CANCELLED, EnumSet.of(CANCELLED))

            );
    public static final Map<ProductStatusType, Set<ProductStatusType>> MAP_TANGIBLE_PRODUCT_STATUS_MAIN =
            Map.ofEntries(
                    Map.entry(ProductStatusType.CREATED, EnumSet.of(ProductStatusType.CREATED, ProductStatusType.SOLD, ProductStatusType.ABORTED, ProductStatusType.CANCELLED)),
                    Map.entry(ProductStatusType.SOLD, EnumSet.of(ProductStatusType.SOLD)),
                    Map.entry(ProductStatusType.ABORTED, EnumSet.of(ProductStatusType.ABORTED)),
                    Map.entry(ProductStatusType.CANCELLED, EnumSet.of(ProductStatusType.CANCELLED))

            );

    public static final Map<ProductOperationalStatusType, Set<ProductOperationalStatusType>> MAP_TANGIBLE_PRODUCT_STATUS_OPERATIONAL =
            Map.ofEntries(
                    Map.entry(ProductOperationalStatusType.CREATED, EnumSet.of(ProductOperationalStatusType.CREATED, ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.PENDINGCANCEL, ProductOperationalStatusType.ABORTED, SOLD, ProductOperationalStatusType.CANCELLED)),
                    Map.entry(ProductOperationalStatusType.CONFIRMED, EnumSet.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.SOLD, ProductOperationalStatusType.LOCKED, ProductOperationalStatusType.PENDINGDELIVERY)),
                    Map.entry(ProductOperationalStatusType.SOLD, EnumSet.of(ProductOperationalStatusType.SOLD)),
                    Map.entry(ProductOperationalStatusType.PENDINGCANCEL, EnumSet.of(ProductOperationalStatusType.PENDINGCANCEL, ProductOperationalStatusType.CANCELLED)),
                    Map.entry(ProductOperationalStatusType.CANCELLED, EnumSet.of(ProductOperationalStatusType.CANCELLED)),
                    Map.entry(ProductOperationalStatusType.ABORTED, EnumSet.of(ProductOperationalStatusType.ABORTED)),
                    Map.entry(ProductOperationalStatusType.PENDINGDELIVERY, EnumSet.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.SOLD, ProductOperationalStatusType.PENDINGDELIVERY, ProductOperationalStatusType.SOLD, ProductOperationalStatusType.LOCKED)),
                    Map.entry(ProductOperationalStatusType.LOCKED, EnumSet.of(ProductOperationalStatusType.LOCKED, ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.ABORTED, ProductOperationalStatusType.SOLD, ProductOperationalStatusType.PENDINGDELIVERY))

            );
    private TangibleProductStatusLifecycle() {

    }
}

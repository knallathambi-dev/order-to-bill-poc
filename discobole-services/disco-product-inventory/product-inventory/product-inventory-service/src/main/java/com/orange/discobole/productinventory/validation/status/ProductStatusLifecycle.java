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

class ProductStatusLifecycle {

    public static final Map<ProductStatusType, Set<ProductOperationalStatusType>> MAP_PRODUCT_STATUS_MAIN_TO_OPERATION =
            Map.ofEntries(
                    Map.entry(ProductStatusType.CREATED, EnumSet.of(CREATED, CONFIRMED, PENDINGACTIVE, PENDINGCANCEL, LOCKED)),
                    Map.entry(ProductStatusType.CANCELLED, EnumSet.of(CANCELLED)),
                    Map.entry(ProductStatusType.ABORTED, EnumSet.of(ABORTED)),
                    Map.entry(ProductStatusType.ACTIVE, EnumSet.of(ACTIVE, INDISTURBANCE, PENDINGACTIVE, PENDINGTERMINATE, PENDINGMODIFICATION, PENDINGMIGRATE, LOCKEDACTIVE)),
                    Map.entry(ProductStatusType.TERMINATED, EnumSet.of(TERMINATED))

            );

    public static final Map<ProductStatusType, Set<ProductStatusType>> MAP_PRODUCT_STATUS_MAIN =
            Map.ofEntries(
                    Map.entry(ProductStatusType.CREATED, EnumSet.of(ProductStatusType.CREATED, ProductStatusType.ACTIVE, ProductStatusType.ABORTED, ProductStatusType.CANCELLED)),
                    Map.entry(ProductStatusType.ACTIVE, EnumSet.of(ProductStatusType.ACTIVE, ProductStatusType.TERMINATED)),
                    Map.entry(ProductStatusType.TERMINATED, EnumSet.of(ProductStatusType.TERMINATED)),
                    Map.entry(ProductStatusType.ABORTED, EnumSet.of(ProductStatusType.ABORTED)),
                    Map.entry(ProductStatusType.CANCELLED, EnumSet.of(ProductStatusType.CANCELLED))

            );


    public static final Map<ProductOperationalStatusType, Set<ProductOperationalStatusType>> MAP_PRODUCT_STATUS_OPERATIONAL =
            Map.ofEntries(

                    Map.entry(ProductOperationalStatusType.CREATED, EnumSet.of(ProductOperationalStatusType.CREATED, ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.CANCELLED, ProductOperationalStatusType.ABORTED)),
                    Map.entry(ProductOperationalStatusType.CONFIRMED, EnumSet.of(ProductOperationalStatusType.CONFIRMED, ProductOperationalStatusType.PENDINGACTIVE, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.CANCELLED, ProductOperationalStatusType.LOCKED, ProductOperationalStatusType.ABORTED)),
                    Map.entry(ProductOperationalStatusType.PENDINGACTIVE, EnumSet.of(ProductOperationalStatusType.PENDINGACTIVE, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.PENDINGCANCEL, ProductOperationalStatusType.ABORTED, ProductOperationalStatusType.LOCKED)),
                    Map.entry(ProductOperationalStatusType.PENDINGCANCEL, EnumSet.of(ProductOperationalStatusType.PENDINGCANCEL, ProductOperationalStatusType.CANCELLED)),
                    Map.entry(ProductOperationalStatusType.LOCKED, EnumSet.of(ProductOperationalStatusType.LOCKED, ProductOperationalStatusType.PENDINGACTIVE, ProductOperationalStatusType.ABORTED, ProductOperationalStatusType.ACTIVE)),
                    Map.entry(ProductOperationalStatusType.LOCKEDACTIVE, EnumSet.of(ProductOperationalStatusType.LOCKEDACTIVE, ProductOperationalStatusType.PENDINGMODIFICATION, ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.TERMINATED)),
                    Map.entry(ProductOperationalStatusType.ACTIVE, EnumSet.of(ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.PENDINGMODIFICATION, ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.PENDINGMIGRATE, ProductOperationalStatusType.LOCKEDACTIVE)),
                    Map.entry(ProductOperationalStatusType.PENDINGMODIFICATION, EnumSet.of(ProductOperationalStatusType.PENDINGMODIFICATION, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.LOCKEDACTIVE)),
                    Map.entry(ProductOperationalStatusType.PENDINGTERMINATE, EnumSet.of(ProductOperationalStatusType.PENDINGTERMINATE, ProductOperationalStatusType.TERMINATED, ProductOperationalStatusType.LOCKEDACTIVE)),
                    Map.entry(ProductOperationalStatusType.PENDINGMIGRATE, EnumSet.of(ProductOperationalStatusType.PENDINGMIGRATE, ProductOperationalStatusType.ACTIVE, ProductOperationalStatusType.TERMINATED)),
                    Map.entry(ProductOperationalStatusType.CANCELLED, EnumSet.of(ProductOperationalStatusType.CANCELLED)),
                    Map.entry(ProductOperationalStatusType.TERMINATED, EnumSet.of(ProductOperationalStatusType.TERMINATED)),
                    Map.entry(ProductOperationalStatusType.ABORTED, EnumSet.of(ProductOperationalStatusType.ABORTED))
            );
    private ProductStatusLifecycle() {

    }
}

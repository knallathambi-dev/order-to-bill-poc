// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.suppliers;


import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.exception.model.FalloutNotFoundException;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.model.FalloutIncident;

import java.util.function.Supplier;

public class NotFoundExceptionSuppliers {

    private NotFoundExceptionSuppliers() {
    }

    public static Supplier<FalloutNotFoundException> falloutNotFoundException(String falloutId) {
        String message = String.format("Fallout with id %s does not exist", falloutId);
        return () -> new FalloutNotFoundException(message, FalloutIncident.class, falloutId);
    }

}

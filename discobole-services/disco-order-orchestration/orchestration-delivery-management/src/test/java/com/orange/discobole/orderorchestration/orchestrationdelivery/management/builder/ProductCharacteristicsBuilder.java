// Software Name: orchestration-delivery-management
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.management.builder;


import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.Characteristic;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.StringCharacteristic;

public class ProductCharacteristicsBuilder {

    public static Characteristic getConnectivityProductCharacteristic() {
        return StringCharacteristic.builder()
                .name("ICCID")
                .value("891004234814455936-cood")
                .valueType("string")
                .build();
    }


}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.scenario;

import java.util.List;
import java.util.Map;

public interface OffersData {

    String BASIC_OFFER_PATH = "src/test/resources/json/mobileBasicOffer.json";

    String MAX_PLUS_PATH = "src/test/resources/json/mobileMaxPlusOffer.json";

    Map<String, List<String>> BASIC_OFFER_RELIES_FROM_RELATIONS = Map.ofEntries(
            Map.entry("SIM card", List.of("Connectivity", "MobileLine"))
    );

    Map<String, List<String>> MAX_PLUS_OFFER_RELIES_FROM_RELATIONS = Map.ofEntries(
            Map.entry("SIM Card", List.of("Mobile Line", "Connectivity")),
            Map.entry("Mobile Line", List.of("Time Bundle"))
    );
}

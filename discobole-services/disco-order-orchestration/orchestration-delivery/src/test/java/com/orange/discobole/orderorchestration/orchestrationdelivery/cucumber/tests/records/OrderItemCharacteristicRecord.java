// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.cucumber.tests.records;

public record OrderItemCharacteristicRecord(String name, String value, String unitOfMeasure, String type, String validFrom, String validTo, String specificationId, String relatedNodeId, String relationType, String addressId, String country, String city, String streetName, String postCode) {
}

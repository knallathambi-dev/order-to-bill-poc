// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.util;

public interface DiscoServiceUrl {

    String getProductOfferingByIdUrl(String id);

    String getProductOfferingQualificationUrl();

    String getProductConfigurationItemsByIdUrl(String id);

    String getAvailableResourceByIdsUrl(String ids);

    String getProductManagementByIdUrl(String id);

    String getProductSpecificationByIdUrl(String id);

    String getProductSpecificationUrl();

    String getProductOfferingPriceUrl();

    String getProductInventoryUrl();

    String getOrderInventoryUrl();

    String getProductOrderInventoryByIdUrl(String id);

    String getPartyRoleManagementUrl();

    String getProductStockManagementUrl();

    String getProductStockManagementByIdUrl(String id);

    String getPaymentManagementByIdUrl(String id);

    String getAccountManagementByIdUrl(String id);

    String getAccountManagement();

    String getProductConfigurationItemsByIdMockedUrl(String id);

    String getServiceQualificationManagementUrl();

    String getAppointmentManagementByIdUrl(String id);

    String getReserveResourcesUrl();

    String getPartyManagementByIdUrl(String partyId);
}
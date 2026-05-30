// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;

import java.util.List;

public interface DiscoServiceUrl {

    String getProductSpecByIdUrl(String id);

    String getProductManagementUrl();

    String getServiceOrderingUrl();

    String getProductManagementByIDUrl(String id);

    String getServiceCatalogManagementUrl();

    String getProductOrderByIdUrl(String id);

    String getProductSpecificationsByIdsUrl(List<String> id);

    String createFallout();

    String getFalloutById(String id);

    String getFalloutByRelatedEntityId(String relatedEntityId);

}

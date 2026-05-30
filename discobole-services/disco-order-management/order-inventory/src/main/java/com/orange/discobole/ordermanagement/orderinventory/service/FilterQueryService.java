// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.service;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.MultiValueMap;

public interface FilterQueryService {

    String[] extractAndCacheFields(String fieldsQueryParameter);

    void validateFieldsToFetch(String... fieldPaths);

    Query createOptimizedQuery(MultiValueMap<String, Object> queryParameters, String[] projectionFields);
}
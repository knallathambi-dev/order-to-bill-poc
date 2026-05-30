// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service;

import com.orange.discobole.productinventory.dto.PageableTMF;
import com.orange.discobole.productinventory.validation.pageable.FieldsFetcher;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.MultiValueMap;

public interface FilterQueryService {

    String[] extractFields(String fieldsQueryParam, String... requiredFields);

    void validateFieldsToFetch(FieldsFetcher fieldsFetcher, String... fieldArray);

    Query createQuery(MultiValueMap<String, Object> attributes);

    Query createAndValidateQuery(PageableTMF pageable, FieldsFetcher fieldsFetcher, String[] requiredFields);


}

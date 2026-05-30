// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;

public interface CategoryEntityRelationshipService {
public void save(CategoryEntityRelationship categoryEntityRelationship);
CategoryEntityRelationship fetchEntityById(String id);
CategoryEntityRelationship fetchCategoryEntityByIdAndFieldList(String id, List<String> fieldList);
public void deleteCategory(String categoryId);

    Map<String, CategoryEntityRelationship> fetchEntitiesByIds(Set<String> ids);


   public void cleanupCategoryFromEntityRelationships(String categoryId);
}

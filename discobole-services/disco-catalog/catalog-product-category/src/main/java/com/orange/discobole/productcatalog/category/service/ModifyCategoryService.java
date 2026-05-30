// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.service;

import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOfferingRef;

public interface ModifyCategoryService {

	void modifyCategoryIdentityData(String categoryId, String name, String description, Boolean isRoot,
			String parentId,List<String> subcategoryIds, List<String> productOfferings);

	void initiateCategoryModification(String categoryId);

	void modifyAssociatedEntity(String categoryId, List<String> productOfferings);
	
	void modifyAssociatedEntity(String categoryId, Set<ProductOfferingRef> productOfferings,Boolean isAddition);

	void validateModifyCategory(String categoryId);

	void cancelCategoryModification(String categoryId);

}

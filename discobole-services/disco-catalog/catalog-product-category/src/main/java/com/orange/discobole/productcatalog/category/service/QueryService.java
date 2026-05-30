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

import com.orange.discobole.productcatalog.category.dto.generated.common.Category;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.category.dto.generated.common.ProductOffering;
/**
 * This interface corresponds to declare the operations related to
 * {@code ServiceSpecification}.
 *
 * @author Piyush Goel
 * @since 1.0
 */
public interface QueryService {
	/**
	 * Fetch category, realized on categoryId
	 *
	 * @param categoryId
	 * @param accessToken
	 * @return
	 */
	Category fetchCategoryById(String categoryId, String accessToken);
	CategoryEntityRelationship fetchCategoryEntityById(String categoryEntityId, String accessToken);

	List<Category> fetchCategoryEntityBySubCategoryId(String categoryEntityId, String accessToken);
	ProductOffering fetchProductOfferingById(String productOfferingId, String accessToken);
}

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

/**
 * Contract to process category related Commands.
 * @author BMKJ8547
 *
 */
public interface CategoryService {
/**
 * handles command for initiateCategoryCreation.
 * @param categoryType
 * @return
 */

/**
 * handles command for defineCategoryIdentityData
 * @param categoryId
 * @param name
 * @param description
 * @param isRoot
 * @param parentId
 * @param subcategoryIds :List
 * @return
 */
public String defineCategoryIdentityData(String name,String description,Boolean isRoot,String parentId,List<String> subcategoryIds,List<String> productOfferingIds);
/**
 * handles command for associateEntity.
 * @param categoryId
 * @param productOfferingIds :List
 */
public void associateEntity(String categoryId,List<String> productOfferingIds);
/**
 * handles command for cancel Category
 * @param categoryId
 */
public void cancelCategory(String categoryId);
/**
 * handles command for validateCategory
 * @param categoryId 
 */
public void validateCategory(String categoryId);
}

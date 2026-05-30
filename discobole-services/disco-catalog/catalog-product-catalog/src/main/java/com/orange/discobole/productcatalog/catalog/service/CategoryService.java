// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service;

import org.springframework.data.mongodb.core.query.Update;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * The Interface CategoryService for handling Category CRUD requests.
 *
 * @author Varshika Choudhary
 */

public interface CategoryService {

    void saveCategory(final Category category);
    
    public void removeCategory(String categoryId);
    
    void updateCategory(String categoryId, Update update);

    List<Category> fetchCategory(Map<String, Object> requestParams) throws UnsupportedEncodingException;

    Category fetchCategoryById(String id);

    Category fetchCategoryByIdAndFieldList(String id, List<String> fieldList);

    List<Category> fetchCategory(final Map<String, Object> requestParams, Long skip, Long limit, String fields) throws UnsupportedEncodingException;

    long countCategory(Map<String, Object> requestParams) throws UnsupportedEncodingException;

	Map<String, Object> fetchCategoryWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException;


}

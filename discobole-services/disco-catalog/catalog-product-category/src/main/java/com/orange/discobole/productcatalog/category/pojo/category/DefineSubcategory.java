// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.pojo.category;

import com.orange.discobole.processflow.annotation.Array;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;

import jakarta.validation.constraints.NotNull;

@Array
public class DefineSubcategory {
@NotNull
private String categoryId;

@NotNull
private CategoryEntityType categoryType;
public String getCategoryId() {
	return categoryId;
}
public void setCategoryId(String categoryId) {
	this.categoryId = categoryId;
}

public CategoryEntityType getCategoryType() {
	return categoryType;
}
public void setCategoryType(CategoryEntityType categoryType) {
	this.categoryType = categoryType;
}


}

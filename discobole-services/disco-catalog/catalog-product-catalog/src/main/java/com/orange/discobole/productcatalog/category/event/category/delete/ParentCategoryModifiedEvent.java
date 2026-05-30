// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category.delete;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class ParentCategoryModifiedEvent implements CategoryEvent {
private String categoryId;
private CategoryRef subCategory;
public ParentCategoryModifiedEvent(String categoryId, CategoryRef subCategory) {
	super();
	this.categoryId = categoryId;
	this.subCategory = subCategory;
}
public ParentCategoryModifiedEvent() {
	super();
	this.categoryId = null;
	this.subCategory = null;
}
public String getCategoryId() {
	return categoryId;
}
public void setCategoryId(String categoryId) {
	this.categoryId = categoryId;
}
public CategoryRef getSubCategory() {
	return subCategory;
}
public void setSubCategory(CategoryRef subCategory) {
	this.subCategory = subCategory;
}
@Override
public String toString() {
	return "ParentCategoryModifiedEvent [categoryId=" + categoryId + ", subCategory=" + subCategory + "]";
}


}

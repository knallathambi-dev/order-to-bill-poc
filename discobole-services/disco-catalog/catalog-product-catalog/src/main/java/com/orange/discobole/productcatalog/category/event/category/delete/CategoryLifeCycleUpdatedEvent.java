// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.event.category.delete;

import com.orange.discobole.productcatalog.category.event.category.CategoryEvent;

public class CategoryLifeCycleUpdatedEvent implements CategoryEvent {
private final String categoryId;
private final String lifeCycleStatus;
public CategoryLifeCycleUpdatedEvent(String categoryId, String lifeCycleStatus) {
	super();
	this.categoryId = categoryId;
	this.lifeCycleStatus = lifeCycleStatus;
}
public CategoryLifeCycleUpdatedEvent() {
	this.categoryId = null;
	this.lifeCycleStatus = null;
}
public String getCategoryId() {
	return categoryId;
}
public String getLifeCycleStatus() {
	return lifeCycleStatus;
}
@Override
public String toString() {
	return "CategoryLifeCycleUpdatedEvent [categoryId=" + categoryId + ", lifeCycleStatus=" + lifeCycleStatus + "]";
}

}

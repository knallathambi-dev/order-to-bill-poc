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
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryAssociateEntity;

import jakarta.validation.constraints.NotNull;

@Array
public class DefineEntity {
@NotNull
private String entityId;
@NotNull
private CategoryAssociateEntity entityType;
public String getEntityId() {
	return entityId;
}
public void setEntityId(String entityId) {
	this.entityId = entityId;
}
public CategoryAssociateEntity getEntityType() {
	return entityType;
}
public void setEntityType(CategoryAssociateEntity entityType) {
	this.entityType = entityType;
}

}

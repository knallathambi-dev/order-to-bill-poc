// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.constant;

import com.orange.discobole.productcatalog.category.pojo.category.*;

public class CategoryConstants {
    private CategoryConstants() {

    }
    public static final String ID = "id";
    public static final String ENTITY_TYPE=SelectEntityType.class.getSimpleName();
    public static final String CATEGORY_IDENTITY_DATA=DefineCategoryIdentityData.class.getSimpleName();
    public static final String CATEGORY_TYPE="categoryType";
    public static final String CATEGORY_ID="categoryId";
    public static final String DEFINE_ENTITY=DefineEntity.class.getSimpleName();
    public static final String DEFINE_SUBCATEGORY=DefineSubcategory.class.getSimpleName();
    public static final String ENTITY_ID="entityId";
    public static final String TYPE="entityType";
    public static final String DEFINE_VALIDITY_PERIOD=DefineValidityPeriod.class.getSimpleName();
    public static final String CATEGORY_CANCEL = CancelEntityOperation.class.getSimpleName();
    public static final String VALIDATE = ValidateEntityOperation.class.getSimpleName();
    public static final String SELECT_CATEGORY = SelectCategory.class.getSimpleName();
    public static final String SELECT_ENTITIES = SelectEntities.class.getSimpleName();

}

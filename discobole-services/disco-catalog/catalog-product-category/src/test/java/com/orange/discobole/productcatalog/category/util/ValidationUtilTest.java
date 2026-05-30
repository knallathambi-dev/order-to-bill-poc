// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.dto.generated.common.CategoryEntityType;
import com.orange.discobole.productcatalog.category.pojo.category.CategoryIdentityData;
import com.orange.discobole.productcatalog.category.pojo.category.SelectEntityType;
import com.orange.discobole.productcatalog.category.util.ValidationUtil;

/**
 * Utility class, use to map the object into required pojo.
 *
 * @author Ankur Singh
 * @since 1.0
 */
public class ValidationUtilTest extends CategoryApplicationTests {
	
	
                 
	@Test
    void testSelectEntityType() {
        SelectEntityType select=new SelectEntityType();
        select.setCategoryType(CategoryEntityType.PRODUCTOFFERINGPRICECATEGORY);
        Object obj=ValidationUtil.validatePojo(select, "category", "selectEntityType");
        assertNotNull(obj);
    }

 

    @Test
    void testCategoryIdentityData() {
        CategoryIdentityData identityData=new CategoryIdentityData();
        identityData.setName("test");
        identityData.setDescription("test-desc");
        identityData.setParentId("1");
        Object obj=ValidationUtil.validatePojo(identityData, "category", "categoryIdentityData");
        assertNotNull(obj);
    }



}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.catalog.CatalogApplicationTests;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.service.impl.CategoryEntityRelationshipServiceImpl;

class CategoryEntityRelationshipServiceImplTest extends CatalogApplicationTests {
@InjectMocks
private CategoryEntityRelationshipServiceImpl categoryEntityRelationshipService;
@Mock
MongoTemplate mongoTemplate;

CategoryEntityRelationship categoryEntityRelationship;
@BeforeEach
void init() {
	categoryEntityRelationship = new CategoryEntityRelationship();
    ReflectionTestUtils.setField(categoryEntityRelationshipService, "mongoTemplate", mongoTemplate);
}
@Test
void saveCategoryEntityTest() {
	categoryEntityRelationshipService.save(categoryEntityRelationship);
    Mockito.verify(mongoTemplate, Mockito.times(1)).save(categoryEntityRelationship);
}

@Test
void fetchCategoryByIdTest() {
	categoryEntityRelationship.id("category1");
    when(mongoTemplate.findById("category1", CategoryEntityRelationship.class)).thenReturn(categoryEntityRelationship);
    CategoryEntityRelationship returnedCategoryEntity = categoryEntityRelationshipService.fetchEntityById(categoryEntityRelationship.getId());
    assertEquals(categoryEntityRelationship.getId(),returnedCategoryEntity.getId());
}
@Test
void fetchProductOfferingByIdAndFieldListTest() {
	List<String> fieldList = List.of("type");
	
	CategoryEntityRelationship categoryEntity = new CategoryEntityRelationship();
	categoryEntity.id("productOff_id123").type("AtomicProductOffering");
	when(mongoTemplate.findOne(any(Query.class), eq(CategoryEntityRelationship.class))).thenReturn(categoryEntity);
	CategoryEntityRelationship returnedCategoryEntity = categoryEntityRelationshipService.
			fetchCategoryEntityByIdAndFieldList("productOff_id123", fieldList);
	assertEquals(categoryEntity.getId(),returnedCategoryEntity.getId());
	assertNotNull(returnedCategoryEntity.getType());

}
@Test
void deleteCategoryEntityTest() {
	categoryEntityRelationshipService.deleteCategory("categoryId");
	Query query = Query.query(Criteria.where("id").is("categoryId"));
    Mockito.verify(mongoTemplate, Mockito.times(1)).remove(query,CategoryEntityRelationship.class) ;
}
}

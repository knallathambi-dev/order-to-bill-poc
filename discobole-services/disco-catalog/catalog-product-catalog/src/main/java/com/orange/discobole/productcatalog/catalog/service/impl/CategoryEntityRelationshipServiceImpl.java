// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CategoryEntityRelationshipServiceImpl implements CategoryEntityRelationshipService {


	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryEntityRelationshipServiceImpl.class);

	@Resource
	private MongoTemplate mongoTemplate;
	@Override
	public void save(CategoryEntityRelationship categoryEntityRelationship) {
		 mongoTemplate.save(categoryEntityRelationship);
	}
	@Override
	public CategoryEntityRelationship fetchEntityById(String id) {
		 return mongoTemplate.findById(id, CategoryEntityRelationship.class);
	}

	public Map<String, CategoryEntityRelationship> fetchEntitiesByIds(Set<String> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			return Collections.emptyMap();
		}
		Query query = new Query(Criteria.where("_id").in(categoryIds));
		List<CategoryEntityRelationship> categoryEntities = mongoTemplate.find(query, CategoryEntityRelationship.class);
		return categoryEntities.stream().collect(Collectors.toMap(CategoryEntityRelationship::getId, Function.identity()));
	}

	@Override
	public CategoryEntityRelationship fetchCategoryEntityByIdAndFieldList(String id, List<String> fieldList) {
        Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
        return mongoTemplate.findOne(query,CategoryEntityRelationship.class);
    
	}
	@Override
	public void deleteCategory(String categoryId) {

		Query query = Query.query(Criteria.where("id").is(categoryId));
		mongoTemplate.remove(query, CategoryEntityRelationship.class);
	
	}

	@Override
	public void cleanupCategoryFromEntityRelationships(String categoryId) {
		if (categoryId == null || categoryId.isBlank()) return;

		Query query = Query.query(Criteria.where("categories.id").is(categoryId));
		Update update = new Update().pull("categories", new org.bson.Document("id", categoryId));

		UpdateResult result = mongoTemplate.updateMulti(query, update, CategoryEntityRelationship.class);
		if (result.getModifiedCount() == 0) {
			LOGGER.warn("No documents were updated for the given category.");

		}

	}


}

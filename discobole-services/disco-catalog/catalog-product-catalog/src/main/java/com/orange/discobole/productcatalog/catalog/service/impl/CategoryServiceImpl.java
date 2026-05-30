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
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Category;
import com.orange.discobole.productcatalog.catalog.service.CategoryService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

import jakarta.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * The CategoryServiceImpl is for handling Category CRUD requests.
 *
 * @author Varshika Choudhary
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	private static final String CATEGORY = "category";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	@Override
	public void saveCategory(Category category) {
		mongoTemplate.save(category);
	}

	@Override
	public void updateCategory(String categoryId, Update update) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(categoryId));
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Category.class);
		long modifiedCount = updateResult.getModifiedCount();
		if (modifiedCount == 0)
			throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_RESOURCE_NOT_FOUND);
	}

	/**
	 * Find Category instances from database according to the query passed which is
	 * build on filter criteria.
	 *
	 * @param requestParams the query containing the filter criteria.
	 * @return list of Category matching the filter criteria
	 */
	@Override
	public List<Category> fetchCategory(Map<String, Object> requestParams) throws UnsupportedEncodingException {

		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);

		Query query = new Query();

		for (Map.Entry<String, Set<Object>> entry : params.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).in(entry.getValue()));
		}
		return mongoTemplate.find(query, Category.class);
	}

	/**
	 * Find category instance w.r.t id parameter.
	 *
	 * @param id the id of category
	 * @return category
	 */
	@Override
	public Category fetchCategoryById(String id) {
		return mongoTemplate.findById(id, Category.class);
	}

	/**
	 * Find category instance w.r.t id parameter and only show fields of fieldList
	 * of category.
	 *
	 * @param id        the id of category
	 * @param fieldList the fieldList for category
	 * @return category
	 */
	@Override
	public Category fetchCategoryByIdAndFieldList(String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, Category.class);
	}

	/**
	 * Fetches the List of productSpec on the basis of parameters given.
	 *
	 * @param requestParams Map of Parameters to search for.
	 * @return List of ProductSpecification
	 */

	@Override
	public void removeCategory(String categoryId) {
		Query query = Query.query(Criteria.where("_id").is(categoryId));
		mongoTemplate.remove(query, Category.class);
	}

	@Override
	public Map<String, Object> fetchCategoryWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, CATEGORY, Category.class,mongoTemplate);
	}



	@Override
	public List<Category> fetchCategory(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException {
		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams,skip,limit,fields, Category.class );
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, CATEGORY, Category.class).getMappedResults()
				: mongoTemplate.findAll(Category.class);
	}

	@Override
	public long countCategory(Map<String, Object> requestParams) throws UnsupportedEncodingException {
	    return  QueryParamUtil.fetchCount(requestParams, CATEGORY, mongoTemplate, Category.class);
	}



}

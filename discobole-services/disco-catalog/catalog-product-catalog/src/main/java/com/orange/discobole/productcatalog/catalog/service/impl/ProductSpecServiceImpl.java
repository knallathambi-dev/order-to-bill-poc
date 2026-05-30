// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.UpdateResult;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

/**
 * The ProductSpecServiceImpl is implementation of ProductSpecService
 *
 * @author Vivek Singh
 * @see ProductSpecService
 * @since 1.0
 */
@Service
public class ProductSpecServiceImpl implements ProductSpecService {

	private static final String PRODUCT_SPECIFICATION = "productSpecification";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	/**
	 * Save/update the productSpec instance in database.
	 *
	 * @param productSpecification the productSpecification
	 */
	@Override
	public void saveProductSpecification(final ProductSpecification productSpecification) {
		mongoTemplate.save(productSpecification);
	}

	/**
	 * update the productSpec instance in database as a patch.
	 *
	 * @param productSpecId the productSpecificationId
	 * @param update        the update
	 */
	@Override
	public void updateProductSpecification(String productSpecId, Update update) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(productSpecId));
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ProductSpecification.class);
		long modifiedCount = updateResult.getModifiedCount();
		if (modifiedCount == 0)
			throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_RESOURCE_NOT_FOUND);
	}

	/**
	 * Fetches the List of productSpec on the basis of parameters given.
	 *
	 * @param requestParams Map of Parameters to search for.
	 * @return List of ProductSpecification
	 */
	@Override
	public List<ProductSpecification> fetchProductSpecification(final Map<String, Object> requestParams, Long skip,
			Long limit, String fields) throws UnsupportedEncodingException {

		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams, skip, limit, fields, ProductSpecification.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, PRODUCT_SPECIFICATION, ProductSpecification.class)
						.getMappedResults()
				: mongoTemplate.findAll(ProductSpecification.class);
	}


	/**
	 * Find productSpecification instance w.r.t id parameter and only show fields of
	 * fieldList of ProductSpecification.
	 *
	 * @param id        the id of productSpecification
	 * @param fieldList the fieldList for productSpecification
	 * @return productSpecification product specification
	 */
	@Override
	public ProductSpecification fetchProductSpecificationById(final String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, ProductSpecification.class);
	}

	/**
	 * Find productSpecification instance w.r.t id parameter.
	 *
	 * @param id the id of productSpecification
	 * @return productSpecification product specification
	 */
	@Override
	public ProductSpecification fetchProductSpecificationById(final String id) {
		return mongoTemplate.findById(id, ProductSpecification.class);
	}

	/**
	 * remove productSpec instance from mongodb w.r.t id parameter.
	 *
	 * @param productSpecId the id of productSpecification
	 */
	@Override
	public void removeProductSpecification(String productSpecId) {
		Query query = Query.query(Criteria.where("id").is(productSpecId));
		mongoTemplate.remove(query, ProductSpecification.class);
	}

	@Override
	public long countProductSpecification(Map<String, Object> requestParams) throws UnsupportedEncodingException {
		return  QueryParamUtil.fetchCount(requestParams, PRODUCT_SPECIFICATION, mongoTemplate, ProductSpecification.class);
	}
	@Override
	public Map<String, Object> fetchProductSpecificationWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, PRODUCT_SPECIFICATION, ProductSpecification.class, mongoTemplate);
	}

}

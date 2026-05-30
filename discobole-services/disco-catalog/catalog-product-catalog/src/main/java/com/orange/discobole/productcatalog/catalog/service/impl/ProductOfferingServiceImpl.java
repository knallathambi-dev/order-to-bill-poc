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
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingDto;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
import com.orange.discobole.productcatalog.catalog.util.ClassConversionUtil;
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
import java.util.stream.Collectors;

/**
 * The class ProductOfferingService to save and update the product offering
 * received.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Service
public class ProductOfferingServiceImpl implements ProductOfferingService {

	private static final String PRODUCT_OFFERING = "productOffering";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	/**
	 * Save/update ProductOffering in Database.
	 *
	 * @param productOffering to be saved
	 */
	@Override
	public void saveProductOffering(final ProductOffering productOffering) {
		ProductOfferingDto productOfferingDto = ClassConversionUtil.convertProductOfferingInDTO(productOffering);
		productOfferingDto.setAggregateId(productOffering.getId());
		mongoTemplate.save(productOfferingDto);
	}

	/**
	 * Fetch the ProductOffering on the basis of the parameters given.
	 *
	 * @param requestParams parameters to search upon
	 * @return List of filtered ProductOffering
	 */
	@Override
	public List<ProductOffering> fetchProductOffering(final Map<String, Object> requestParams, Long skip, Long limit,
			String fields) throws UnsupportedEncodingException {

		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams,skip,limit,fields, ProductOffering.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, PRODUCT_OFFERING, ProductOffering.class).getMappedResults()
				: mongoTemplate.findAll(ProductOffering.class);
	}

	/**
	 * Fetch the ProductOffering on the basis of id.
	 *
	 * @param id to search
	 * @return ProductOffering
	 */
	@Override
	public ProductOffering fetchProductOfferingById(final String id) {

		return mongoTemplate.findById(id, ProductOffering.class);
	}

	/**
	 * Find productOffering instance w.r.t id parameter and only show fields of
	 * fieldList of productOffering.
	 *
	 * @param id        the id of productOffering
	 * @param fieldList the fieldList for productOffering
	 * @return productOffering product offering
	 */
	@Override
	public ProductOffering fetchProductOfferingById(final String id, List<String> fieldList) {
		Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
		return mongoTemplate.findOne(query, ProductOffering.class);
	}

	/**
	 * remove productOffering instance from mongodb w.r.t id parameter.
	 *
	 * @param productOffId the id of productOffering
	 */
	@Override
	public void removeProductOffering(String productOffId) {
		Query query = Query.query(Criteria.where("id").is(productOffId));
		mongoTemplate.remove(query, ProductOffering.class);
	}

	@Override
	public void updateProductOffering(String poId, Update update) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(poId));
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ProductOffering.class);
		long modifiedCount = updateResult.getModifiedCount();
		if (modifiedCount == 0)
			throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_RESOURCE_NOT_FOUND);
		// purge related cache here
	}

	@Override
	public Map<String, String> fetchProductOfferingNamesByIds(Set<String> productOfferingIds) {
		if (productOfferingIds == null || productOfferingIds.isEmpty()) {
			return Collections.emptyMap();
		}
		Query query = new Query(Criteria.where("_id").in(productOfferingIds));
				query.fields().include("_id").include("name");
		List<ProductOffering> productOfferings = mongoTemplate.find(query, ProductOffering.class);
		return productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, ProductOffering::getName));
	}

	@Override
	public long countProductOffering(Map<String, Object> requestParams) throws UnsupportedEncodingException {
		return  QueryParamUtil.fetchCount(requestParams, PRODUCT_OFFERING, mongoTemplate,ProductOffering.class);

	}

	@Override
	public Map<String, Object> fetchProductOfferingWithCount(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	    return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, PRODUCT_OFFERING, ProductOffering.class, mongoTemplate);
	}

}

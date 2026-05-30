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
import java.util.*;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
import jakarta.annotation.Resource;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.mongodb.client.result.UpdateResult;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.catalog.util.ClassConversionUtil;
import com.orange.discobole.productcatalog.catalog.util.QueryParamUtil;

@Service
public class ProductOfferingPriceServiceImpl implements ProductOfferingPriceService {

	private static final String PRODUCT_OFFERING_PRICE = "productOfferingPrice";
	@Resource
	private MongoTemplate mongoTemplate;

	public static final String VALID_FOR_START_DATE_TIME = "validFor.startDateTime";

	@Override
	public void saveProductOfferingPrice(ProductOfferingPrice productOfferingPrice) {


		productOfferingPrice.setAggregateId(productOfferingPrice.getId());
		productOfferingPrice.id(productOfferingPrice.getId());
		mongoTemplate.save(productOfferingPrice);
	}

	@Override
	public void saveProductOfferingPriceWithRandomId(ProductOfferingPrice productOfferingPrice) {

		productOfferingPrice.setAggregateId(productOfferingPrice.getId());
		productOfferingPrice.id(UUID.randomUUID().toString());
		mongoTemplate.save(productOfferingPrice);
	}

	@Override
	public long countProductOfferingPrice(Map<String, Object> requestParams) throws UnsupportedEncodingException {
		return  QueryParamUtil.fetchCount(requestParams, PRODUCT_OFFERING_PRICE,mongoTemplate,ProductOfferingPrice.class);
}

	@Override
	public List<ProductOfferingPrice> getProductOfferingPrices(Map<String, Object> requestParams, Long skip, Long limit, String fields)
			throws UnsupportedEncodingException {

		Aggregation aggregation = QueryParamUtil.fetchEntityFiltered(requestParams,skip,limit,fields,ProductOfferingPrice.class);
		return aggregation != null
				? mongoTemplate.aggregate(aggregation, PRODUCT_OFFERING_PRICE, ProductOfferingPrice.class).getMappedResults()
				: mongoTemplate.findAll(ProductOfferingPrice.class);
	}

	/**
	 * Find productOfferingPrice instance w.r.t id parameter
	 *
	 * @param productOfferingPriceId the id of productOfferingPrice
	 * @return productOfferingPrice product Offering Price
	 */
	//@Override
	public ProductOfferingPrice getProductOfferingPriceByIdtest(String productOfferingPriceId) {
		return mongoTemplate.findById(productOfferingPriceId, ProductOfferingPrice.class);
	}
	@Override
	public ProductOfferingPrice getProductOfferingPriceById(String productOfferingPriceId) {
		//  Fetch as raw Document instead of directly mapping to ProductOfferingPrice
		Query query = new Query(Criteria.where("_id").is(productOfferingPriceId));
		Document doc = mongoTemplate.findOne(query, Document.class, "productOfferingPrice");
		if (doc == null) return null;

		//  Dynamic subclass instantiation based on 'type' field
		return convertDocumentToPOP(doc);
	}

	/**  Helper method to convert raw Document to correct subclass */
	private ProductOfferingPrice convertDocumentToPOP(Document doc) {
		String type = doc.getString("type");
		System.out.println("typedoc " + type);

		if (type == null) {
			type = doc.getString("@type");
		}

		// fallback to _class if still null
		if (type == null) {
			String className = doc.getString("_class");
			if (className != null) {
				if (className.endsWith("InstallmentCharge")) {
					type = "INSTALLMENTCHARGE";
				} else if (className.endsWith("ProductOfferingPriceCharge")) {
					type = "PRODUCTOFFERINGPRICECHARGE";
				} else if (className.endsWith("ProductOfferingPriceAlteration")) {
					type = "PRODUCTOFFERINGPRICEALTERATION";
				} else if (className.endsWith("TaxProductOfferingPriceAlteration")) {
					type = "TAXPRODUCTOFFERINGPRICEALTERATION";
				}
			}
		}

		if (type == null) {
			return mongoTemplate.getConverter()
					.read(ProductOfferingPrice.class, doc);
		}


		type = type.trim().toUpperCase();
		ProductOfferingPrice pop;

		switch (type) {
			case  "INSTALLMENTCHARGE":
				pop = mongoTemplate.getConverter().read(InstallmentCharge.class, doc);
				break;
			case "PRODUCTOFFERINGPRICECHARGE":
				pop = mongoTemplate.getConverter().read(ProductOfferingPriceCharge.class, doc);
				break;
			case "PRODUCTOFFERINGPRICEALTERATION":
				pop = mongoTemplate.getConverter().read(ProductOfferingPriceAlteration.class, doc);
				break;
			case "TAXPRODUCTOFFERINGPRICEALTERATION":
				pop = mongoTemplate.getConverter().read(TaxProductOfferingPriceAlteration.class, doc);
				break;
			default:
				pop = mongoTemplate.getConverter().read(ProductOfferingPrice.class, doc);
		}

		return pop;
	}


	/**
	 * Find productOfferingPrice instance w.r.t id parameter and only show fields of
	 * fieldList of ProductOfferingPrice.
	 *
	 * @param productOfferingPriceId the id of productOfferingPrice
	 * @param fieldList              the fieldList for productOfferingPrice
	 * @return productOfferingPrice product Offering Price
	 */


//@Override
public Object getProductOfferingPriceByIdtest(
				String productOfferingPriceId,
				List<String> fieldList) {

			List<String> projectionFields = new ArrayList<>(fieldList);


	//  REQUIRED discriminators
	if (!projectionFields.contains("_class")) {
		projectionFields.add("_class");
	}
	if (!projectionFields.contains("@type")) {
		projectionFields.add("@type");
	}
	if (!projectionFields.contains("type")) {
		projectionFields.add("type");
	}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(
							Criteria.where("_id").is(productOfferingPriceId)
					),
					Aggregation.project(
							projectionFields.toArray(new String[0])
					)
			);

			Document doc = mongoTemplate
					.aggregate(
							aggregation,
							PRODUCT_OFFERING_PRICE,
							Document.class
					)
					.getUniqueMappedResult();

			return doc != null ? convertDocumentToPOP(doc) : null;
		}




//	@Override
	public ProductOfferingPrice getProductOfferingPriceByIdtest1(
			String id,
			List<String> fieldList) {

		// 1️ Fetch FULL document (NO projection)
		Document fullDoc =
				mongoTemplate.findById(id, Document.class, PRODUCT_OFFERING_PRICE);

		if (fullDoc == null) {
			return null;
		}

		// 2️ Convert using FULL discriminator info
		ProductOfferingPrice pop = convertDocumentToPOP(fullDoc);

		// 3 Apply field filtering AFTER conversion (optional)
		if (fieldList != null && !fieldList.isEmpty()) {
			return filterFields(pop, fieldList);
		}

		return pop;
	}


	public ProductOfferingPrice getProductOfferingPriceById(
			String id,
			List<String> fieldList) {

		//  Load FULL document first
		Document fullDoc = mongoTemplate.findById(
				id,
				Document.class,
				PRODUCT_OFFERING_PRICE
		);

		if (fullDoc == null) {
			return null;
		}

		// 2️ Apply projection in-memory
		if (fieldList != null && !fieldList.isEmpty()) {
			Document projected = new Document();

			for (String field : fieldList) {
				if (fullDoc.containsKey(field)) {
					projected.put(field, fullDoc.get(field));
				}
			}

			//  Always include @type for polymorphic deserialization
			if (!projected.containsKey("@type")) {
				if (fullDoc.get("@type") != null) {
					projected.put("@type", fullDoc.get("@type"));
				} else if (fullDoc.get("type") != null) {
					projected.put("@type", fullDoc.get("type"));
				}
			}
			projected.put("_id", fullDoc.get("_id"));

			fullDoc = projected;
		}

		// 3 Convert safely
		return convertDocumentToPOP(fullDoc);
	}



	private ProductOfferingPrice filterFields(
			ProductOfferingPrice pop,
			List<String> fields) {

		ObjectMapper mapper = new ObjectMapper();

		// Convert object → Map
		Map<String, Object> map =
				mapper.convertValue(pop, new TypeReference<>() {});

		// Keep only requested fields
		map.keySet().retainAll(fields);

		// Convert back → SAME CLASS
		return mapper.convertValue(map, pop.getClass());
	}


	@Override
	public void removeProductOfferingPrice(String id) {
		Query query = Query.query(Criteria.where("id").is(id));
		mongoTemplate.remove(query, ProductOfferingPrice.class);
	}

	@Override
	public void updateProductOfferingPrice(String popId, Update update) {
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(popId));
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ProductOfferingPrice.class);
		long modifiedCount = updateResult.getModifiedCount();
		if (modifiedCount == 0)
			throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_RESOURCE_NOT_FOUND);
	}

//	@Override
	public Map<String, Object> fetchProductOfferingPriceWithCounttest(Map<String, Object> requestParams, Long skip, Long limit, String fields)
	        throws UnsupportedEncodingException {
	  return QueryParamUtil.fetchEntityMap(requestParams, skip, limit, fields, PRODUCT_OFFERING_PRICE, ProductOfferingPrice.class, mongoTemplate);
	}

	@Override
	public Map<String, Object> fetchProductOfferingPriceWithCount(
			Map<String, Object> requestParams,
			Long skip,
			Long limit,
			String fields) throws UnsupportedEncodingException {

		Map<String, Object> rawResult =
				QueryParamUtil.fetchEntityMap(
						requestParams,
						skip,
						limit,
						fields,
						PRODUCT_OFFERING_PRICE,
						mongoTemplate
				);

		List<Document> rawDocs = (List<Document>) rawResult.get("data");

		List<ProductOfferingPrice> result = new ArrayList<>();
		if (rawDocs != null) {
			for (Document doc : rawDocs) {
				result.add(convertDocumentToPOP(doc));
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("data", result);
		response.put("count", rawResult.get("count"));

		return response;
	}

	@Override
	public List<ProductOfferingPrice> getProductOfferingPricesByIdsWithSubtypes(Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		Query query = new Query(Criteria.where("_id").in(ids));
		List<Document> rawDocs = mongoTemplate.find(query, Document.class, PRODUCT_OFFERING_PRICE);

		List<ProductOfferingPrice> result = new ArrayList<>(rawDocs.size());

		for (Document doc : rawDocs) {
			try {
				result.add(convertDocumentToPOP(doc));   //convert
			} catch (Exception e) {
				result.add(mongoTemplate.getConverter().read(ProductOfferingPrice.class, doc));
			}
		}
		return result;
	}

}

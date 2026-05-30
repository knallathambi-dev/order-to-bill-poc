// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * Utility class for query parameters
 * 
 * @author Vivek Singh
 * @since 1.0
 *
 */
public class QueryParamUtil {

	private static final String COUNT = "count";
	private static final String EXACT = "exact";
	private static final String BEFORE = "before";
	private static final String AFTER = "after";
	private static final String RANGE = "range";
	private static final String CREATION_DATE = "creationDate";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";

	private static final Logger LOGGER = LogManager.getLogger(QueryParamUtil.class);

	/**
	 * Instantiates a new QueryParamUtil.
	 */
	private QueryParamUtil() {
	}

	/**
	 * Utility method to map the parameters with the passed value/range of values.
	 * 
	 * @param requestParams map of string to object where Strings are parameter and
	 *                      object can be suitable datatype
	 * @return map of String to set of object
	 */
	public static Map<String, Set<Object>> mapper(Map<String, Object> requestParams) {
		Map<String, Set<Object>> params = new HashMap<>();
		for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
			Set<Object> set = new HashSet<>();
			Object value = entry.getValue();
			if (null != value && !entry.getKey().equals("sort")) {
				if (value instanceof String) {
					String[] values = value.toString().split(",");
					for (int i = 0; i < values.length; i++) {
						values[i] = URLDecoder.decode(values[i], StandardCharsets.UTF_8);
					}
					set.addAll(Arrays.asList(values));
				} else {
					set.add(value);
				}
				params.put(entry.getKey(), set);
			}
		}
		return params;

	}

	/**
	 * Method to sort the product offering in an ordered fashion of
	 * ascending/descending direction.
	 *
	 * @param sortParams set of parameters.
	 * @return List of ordered product offering.
	 */

	public static List<Order> sortParameters(Class<?> type, Set<Object> sortParams) {
		return sortParams.stream().map(paramKey -> {
			String sortParamValue = (String) paramKey;
			boolean desc = false;
			boolean asc = false;
			if (sortParamValue.startsWith("-")) {
				desc = true;
				sortParamValue = sortParamValue.substring(1);
			} else if (sortParamValue.startsWith("+")) {
				asc = true;
				sortParamValue = sortParamValue.substring(1);
			}
			if (isValid(type, sortParamValue)) {
				if (desc) {
					return new Order(Sort.Direction.DESC, sortParamValue).ignoreCase();
				} else if (asc) {
					return new Order(Sort.Direction.ASC, sortParamValue).ignoreCase();
				} else {
					return new Order(Sort.Direction.ASC, sortParamValue).ignoreCase();
				}
			} else {
				throw new InvalidParameterException("Enter a valid key name ");
			}
		}).toList();
	}

	/**
	 * Utility method to validate the key used for sorting the product offering.
	 *
	 * @param sortParamValue sort is done on the basis of this value (key)
	 * @return boolean
	 */

	public static boolean isValid(Class<?> type, String sortParamValue) {
		Field[] fields = type.getDeclaredFields();
		Set<String> fieldSet = new HashSet<>();

		for (Field field : fields) {
			if (field.isAnnotationPresent(JsonProperty.class)) {
				String annotationValue = field.getAnnotation(JsonProperty.class).value();
				fieldSet.add(annotationValue);
			}
		}
		return true;
	}

	public static Query prepareFieldsFilter(List<String> fieldList, String entityId) {
		Query query = new Query();
		org.springframework.data.mongodb.core.query.Field fields = query.fields().include("href");
		for (String field : fieldList)
			fields.include(field);
		query.addCriteria(Criteria.where("_id").in(entityId));
		return query;
	}

	public static <T> Aggregation fetchEntityFilteredFacet(final Map<String, Object> requestParams, Long skip, Long limit,
			String fields, Class<T> className) {

		List<AggregationOperation> baseFilter = new ArrayList<>();
		Criteria criteria = buildCriteria(requestParams);

		if (criteria != null) {
			baseFilter.add(Aggregation.match(criteria));
		}

		SortOperation sort = buildSortOperation(requestParams, className);
		if (sort != null) {
			baseFilter.add(sort);
		}

		List<AggregationOperation> dataPipeline = new ArrayList<>(baseFilter);

		if (skip != null && skip > 0) {
			dataPipeline.add(Aggregation.skip(skip));
		}

		if (limit != null && limit > 0) {
			dataPipeline.add(Aggregation.limit(limit));
		}

		if (fields != null && !fields.isEmpty()) {
			dataPipeline.add(Aggregation.project(fields.split(",")));
		}

		if (dataPipeline.isEmpty()) {
			dataPipeline.add(Aggregation.match(new Criteria()));
		}

		List<AggregationOperation> countPipeline = new ArrayList<>(baseFilter);
		countPipeline.add(Aggregation.count().as(COUNT));

		if (countPipeline.isEmpty()) {
			countPipeline.add(Aggregation.match(new Criteria()));
			countPipeline.add(Aggregation.count().as(COUNT));
		}

		FacetOperation facet = Aggregation.facet(dataPipeline.toArray(new AggregationOperation[0])).as("data")
				.and(countPipeline.toArray(new AggregationOperation[0])).as(COUNT);

		return Aggregation.newAggregation(facet);

	}

	public static <T>Aggregation fetchEntityFiltered(final Map<String, Object> requestParams, Long skip, Long limit,
			String fields, Class<T> className) throws UnsupportedEncodingException {

		List<AggregationOperation> aggregations = new ArrayList<>();
		Criteria criteria = buildCriteria(requestParams);

		if (criteria != null) {
			aggregations.add(match(criteria));
		}

		SortOperation sortBy = buildSortOperation(requestParams, className);
		if (sortBy != null) {
			aggregations.add(sortBy);
		}

		if (skip != null) {
			aggregations.add(Aggregation.skip(skip));
		}

		if (fields != null) {
			aggregations.add(Aggregation.project(fields.split(",")));
		}

		if (limit != null) {
			aggregations.add(Aggregation.limit(limit));
		}

		return aggregations.isEmpty() ? null : Aggregation.newAggregation(aggregations);
	}

	public static Criteria buildCriteria(Map<String, Object> requestParams) {
		Map<String, Set<Object>> params = QueryParamUtil.mapper(requestParams);
		Criteria criteria = new Criteria();
		boolean criteriaInitialized = false;
		Set<Object> orConditions = params.get("orConditions");

		if (orConditions != null && !orConditions.isEmpty()) {
			List<Criteria> orCriteria = new ArrayList<>();
			for (Object x : orConditions) {
				handleOrCriteria(x, params, orCriteria);
			}
			if (!orCriteria.isEmpty()) {
				criteria.orOperator(orCriteria);
				criteriaInitialized = true;
			}
			params.remove("orConditions");
		}


		for (Map.Entry<String, Set<Object>> entry : new HashSet<>(params.entrySet())) {
			String key = entry.getKey();
			Set<Object> values = entry.getValue();
			criteria = handleCriteriaForKey(criteria, key, values, params, criteriaInitialized);
			criteriaInitialized = true;
		}

		return criteriaInitialized ? criteria : null;
	}

	public static Criteria applyDateTimeCriteria(Criteria criteria, String key, Set<Object> values, String mode) {
		List<Date> dates = values.stream()
				.map(v -> {
					String s = v.toString().replace("\"", "");
					return Date.from(OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant());
				})
				.toList();
		String field = CREATION_DATE;
		switch (mode) {
			case EXACT:
				if (dates.size() == 1) return criteria.and(field).is(dates.get(0));
				break;
			case BEFORE:
				if (dates.size() == 1) return criteria.and(field).lt(dates.get(0));
				break;
			case AFTER:
				if (dates.size() == 1) return criteria.and(field).gt(dates.get(0));
				break;
			case RANGE:
				if (dates.size() == 2) return criteria.and(field).gte(dates.get(0)).lte(dates.get(1));
				break;
			default:
				LOGGER.warn("Unknown date filter operation for key: {}", key);
		}
		return criteria;
	}

	public static Criteria applyPatternCriteria(Criteria criteria, String key, Set<Object> values,
			boolean criteriaInitialized) {
		List<Pattern> patterns = values.stream()
				.map(value -> Pattern.compile(Pattern.quote(value.toString()), Pattern.CASE_INSENSITIVE)).toList();
		return criteriaInitialized ? criteria.and(key).in(patterns) : Criteria.where(key).in(patterns);
	}

	public static Criteria applyDefaultCriteria(Criteria criteria, String key, Set<Object> values,
			boolean criteriaInitialized) {
		List<Object> valueList = new ArrayList<>(values);
		return criteriaInitialized ? criteria.and(key).in(valueList) : Criteria.where(key).in(valueList);
	}

	public static <T> SortOperation buildSortOperation(Map<String, Object> requestParams, Class<T> className) {
		if (requestParams.containsKey("sort") && requestParams.get("sort") != null) {
			String[] values = requestParams.get("sort").toString().split(",");
			Set<Object> sortParams = new LinkedHashSet<>(Arrays.asList(values));
			List<Order> orders = QueryParamUtil.sortParameters(className, sortParams);
			return sort(Sort.by(orders));
		}
		return null;
	}

	public static  <T> Map<String, Object> fetchEntityMap(Map<String, Object> requestParams, Long skip, Long limit,
			String fields, String entiry, Class<T> className, MongoTemplate mongoTemplate) {
		
		Aggregation aggregation = QueryParamUtil.fetchEntityFilteredFacet(requestParams, skip, limit, fields, className);

	    Document result = mongoTemplate.aggregate(aggregation, entiry, Document.class)
	                                   .getUniqueMappedResult();
	    Long count = 0L;
	    List<T> data = new ArrayList<>();
	    if (result != null && result.containsKey("data")) {
	        List<Document> rawData = (List<Document>) result.get("data");
	        for (Document doc : rawData) {
	            data.add(mongoTemplate.getConverter().read(className, doc));
	        }
	        List<Document> countDocs = (List<Document>) result.get(COUNT);
		    if (!countDocs.isEmpty()) {
		        Object countValue = countDocs.get(0).get(COUNT);
		        if (countValue instanceof Number number) {
		            count = number.longValue();
		        }
		    }
	    }
	    Map<String, Object> response = new HashMap<>();
	    response.put("data", data);
	    response.put(COUNT, count);
		return response;
	}

	public static <T> long fetchCount(Map<String, Object> requestParams, String entity, MongoTemplate mongoTemplate, Class<T> className) {
		
		Criteria criteria = QueryParamUtil.buildCriteria(requestParams);
	    List<AggregationOperation> operations = new ArrayList<>();

	    if (criteria != null) {
	        operations.add(Aggregation.match(criteria));
	    }

	    SortOperation sort = QueryParamUtil.buildSortOperation(requestParams, className);
	    if (sort != null) {
	        operations.add(sort);
	    }

	    operations.add(Aggregation.count().as(COUNT));

	    Aggregation countAggregation = Aggregation.newAggregation(operations);

	    Document result = mongoTemplate.aggregate(countAggregation, entity , Document.class)
	                                   .getUniqueMappedResult();
	    return result != null ? result.getInteger(COUNT, 0) : 0L;
	}

	private static Criteria applyCreationDateRange(Criteria criteria, Map<String, Set<Object>> params) {
		Date start = parseFirstOrNull(params.get(START_TIME));
		Date end   = parseFirstOrNull(params.get(END_TIME));

		String field = CREATION_DATE;
		if (start != null && end != null) {
			// inclusive start, exclusive end (reliable for ranges)
			return criteria.and(field).gte(start).lt(end);
		} else if (start != null) {
			return criteria.and(field).gte(start);
		} else if (end != null) {
			return criteria.and(field).lt(end);
		}
		return criteria; // nothing to add if both are null
	}

	private static Criteria buildRangeCriteriaFromParams(Map<String, Set<Object>> params) {
		Date start = parseFirstOrNull(params.get(START_TIME));
		Date end   = parseFirstOrNull(params.get(END_TIME));
		String field = CREATION_DATE;

		Criteria c = new Criteria();
		if (start != null && end != null) {
			return c.and(field).gte(start).lt(end);
		} else if (start != null) {
			return c.and(field).gte(start);
		} else if (end != null) {
			return c.and(field).lt(end);
		}
		return c; // empty criteria if neither bound is present
	}

	private static Date parseFirstOrNull(Set<Object> vals) {
		if (vals == null || vals.isEmpty()) return null;
		String s = vals.iterator().next().toString().replace("\"", ""); // handle accidental quotes
		return Date.from(OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant());
	}

	private static void handleOrCriteria(Object keyObj, Map<String, Set<Object>> params, List<Criteria> orCriteria) {
		String key = keyObj.toString();
		Set<Object> values = params.get(key);
		if (values == null || values.isEmpty()) return;

		switch (key) {
			case "exactTime":
				orCriteria.add(applyDateTimeCriteria(new Criteria(), key, values, EXACT));
				break;
			case "beforeTime":
				orCriteria.add(applyDateTimeCriteria(new Criteria(), key, values, BEFORE));
				break;
			case "afterTime":
				orCriteria.add(applyDateTimeCriteria(new Criteria(), key, values, AFTER));
				break;
			default:
				if (START_TIME.equals(key) || END_TIME.equals(key)) {
					orCriteria.add(buildRangeCriteriaFromParams(params)); // uses both bounds
				} else {
					List<Pattern> patterns = values.stream()
							.map(v -> Pattern.compile(Pattern.quote(v.toString()), Pattern.CASE_INSENSITIVE))
							.toList();
					orCriteria.add(Criteria.where(key).in(patterns));
					params.remove(key);
				}
		}
	}

	private static Criteria handleCriteriaForKey(Criteria criteria, String key, Set<Object> values, Map<String, Set<Object>> params, boolean criteriaInitialized) {
		if (values == null || values.isEmpty()) return criteria;

		if ("exactTime".equals(key)) {
			applyDateTimeCriteria(criteria, key, values, EXACT);
		} else if ("beforeTime".equals(key)) {
			applyDateTimeCriteria(criteria, key, values, BEFORE);
		} else if ("afterTime".equals(key)) {
			applyDateTimeCriteria(criteria, key, values, AFTER);
		} else if (START_TIME.equals(key) || END_TIME.equals(key)) {
			criteria = applyCreationDateRange(criteria, params); // use both bounds
			params.remove(START_TIME);                            // avoid double-adding
			params.remove(END_TIME);
		} else if ("name".equals(key) || "brand".equals(key) || "_id".equals(key)) {
			criteria = applyPatternCriteria(criteria, key, values, criteriaInitialized);
		} else {
			criteria = applyDefaultCriteria(criteria, key, values, criteriaInitialized);
		}

		return criteria;
	}


}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.service.impl;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.catalog.constant.ProcessEntity;
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.*;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "delete", havingValue = "true", matchIfMissing = true)
@Service
public class MongodbDataFilterServiceImpl implements MongodbDataFilterService {

    @Resource
    private MongoTemplate mongoTemplate;
    
    private static final String DOMAIN_EVENTS="domainevents";
    private static final String AGGREGATE_IDENTIFIER="aggregateIdentifier";


  
	@Override
    public Long filterProductSpecificsationData(OffsetDateTime delDate,Long interval,String intervalUnit) {
        OffsetDateTime deletionDateTime = delDate.minus(interval,selectDurationUnit(intervalUnit));
        Query query= new Query(Criteria.where(ProductSpecConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY).and(ProductSpecConstants.LAST_UPDATE).lt(deletionDateTime));
        return mongoTemplate.remove(query, ProductSpecification.class).getDeletedCount();
    }

    @Override
    public Long filterProductOfferingData(Long days) {
        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime fifteenDaysOld = today.minusDays(days);
        Query query= new Query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).lt(fifteenDaysOld));
        return mongoTemplate.remove(query, ProductOffering.class).getDeletedCount();

    }
    @Override
	public Long filterProductOfferingData(OffsetDateTime delDate, Long interval, String intervalUnit) {

		OffsetDateTime deletionDateTime = delDate.minus(interval, selectDurationUnit(intervalUnit));
		Query query = new Query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).lt(deletionDateTime));
		return mongoTemplate.remove(query, ProductOffering.class).getDeletedCount();

	}

	
	@Override
	public Long filterProductSpecificationData(Long days) {
        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime fifteenDaysOld = today.minusDays(days);
         Query query= new Query(Criteria.where(ProductSpecConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY).and(ProductSpecConstants.LAST_UPDATE).lt(fifteenDaysOld));
        return mongoTemplate.remove(query, ProductSpecification.class).getDeletedCount();

    }

	@Override
	public Long filterTemporaryProductSpecificsationData() {
		 Query query= new Query(Criteria.where(ProductSpecConstants.LIFE_CYCLE_STATUS).is(ProductSpecificationLifecycle.INSTUDY).and(ProductSpecConstants.LAST_UPDATE).exists(false));
	        return mongoTemplate.remove(query, ProductSpecification.class).getDeletedCount();
	}
	@Override
	public Long filterTemporaryProductOfferingData() {
		Query query = new Query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).exists(false));
		return mongoTemplate.remove(query, ProductOffering.class).getDeletedCount();
	}
	private ChronoUnit selectDurationUnit(String intervalUnit) {
		ChronoUnit unit;
		switch (intervalUnit) {
		case "HOURS":
			unit=ChronoUnit.HOURS;
			break;
		case "DAYS":
			unit=ChronoUnit.DAYS;
			break;
		default:
			throw new IllegalArgumentException("Please enter the units in HOURS or DAYS");

		}
		return unit;
	}

	
	@Override
	public Long filterProductSpecificationEventsData(OffsetDateTime delDate, Long interval, String intervalUnit) {
		OffsetDateTime deletionDateTime = delDate.minus(interval, selectDurationUnit(intervalUnit));
		Query query = new Query(Criteria.where(ProductSpecConstants.LIFE_CYCLE_STATUS)
				.is(ProductSpecificationLifecycle.INSTUDY).and(ProductSpecConstants.LAST_UPDATE).lt(deletionDateTime));
		List<String> list = mongoTemplate.find(query, ProductSpecification.class).stream().map(x -> x.getId())
				.toList();
		if (!list.isEmpty()) {
			Query query2 = new Query(Criteria.where(AGGREGATE_IDENTIFIER).in(list));
			return mongoTemplate.remove(query2, DOMAIN_EVENTS).getDeletedCount();
		}
		return 0L;
	}

	@Override
	public Long filterProductOfferingEventsData(OffsetDateTime delDate, Long interval, String intervalUnit) {
		OffsetDateTime deletionDateTime = delDate.minus(interval, selectDurationUnit(intervalUnit));
		Query query = new Query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS)
				.is(ProductOfferingLifecycle.INSTUDY).and(ProductOfferingConstants.LAST_UPDATE).lt(deletionDateTime));
		List<String> list = mongoTemplate.find(query, ProductOffering.class).stream().map(x -> x.getId())
				.toList();
		if (!list.isEmpty()) {
			Query query2 = new Query(Criteria.where(AGGREGATE_IDENTIFIER).in(list));
			return mongoTemplate.remove(query2, DOMAIN_EVENTS).getDeletedCount();
		}
		return 0L;
	}
	
	@Override
	public Long filterProductOfferingPriceEventData(OffsetDateTime lastUpdateDateTime, Long interval,
            String intervalUnit) {
        OffsetDateTime deletionDateTime = lastUpdateDateTime.minus(interval,selectDurationUnit(intervalUnit));
        Query query = new Query(Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingPriceLifecycle.UNAVAILABLE).and(ProductOfferingConstants.LAST_UPDATE).lt(deletionDateTime));
        List<String> list =    mongoTemplate.find(query, ProductOfferingPrice.class).stream().map(x -> x.getId())
        .toList();
        if (!list.isEmpty()) {
            Query query2 = new Query(Criteria.where(AGGREGATE_IDENTIFIER).in(list));
            return mongoTemplate.remove(query2, DOMAIN_EVENTS).getDeletedCount();
        }

        return 0L;
    }
	
	@Override
	public long filterProductOfferingPriceData(OffsetDateTime delDate, Long interval, String intervalUnit) {
		OffsetDateTime deletionDateTime = delDate.minus(interval, selectDurationUnit(intervalUnit));
		Query query = new Query(
				Criteria.where(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(ProductOfferingPriceLifecycle.UNAVAILABLE)
						.and(ProductOfferingConstants.LAST_UPDATE).lt(deletionDateTime));
		return mongoTemplate.remove(query, ProductOfferingPrice.class).getDeletedCount();
	}
	@Override
	public String processEntityCleanUp(ProceesEntityCleanUpDTO processCleanUpEntity){
		OffsetDateTime deletionDateTime = processCleanUpEntity.getDeletionStartDate();
		Criteria criteria=Criteria.where(ProductOfferingConstants.LAST_UPDATE).gte(deletionDateTime);
		if(!processCleanUpEntity.getLifeCycleStatus().equals("ALL")){
			criteria.and(ProductOfferingConstants.LIFE_CYCLE_STATUS).is(processCleanUpEntity.getLifeCycleStatus());
		}
		Query query = new Query(criteria);
		switch (ProcessEntity.fromValue(processCleanUpEntity.getProcessFlowSpecification())){
			case PRODUCTOFFERING:
				List<ProductOffering> productOfferings=mongoTemplate.find(new Query(criteria),ProductOffering.class);
				mongoTemplate.remove(query,ProductOffering.class);
				for (ProductOffering productoffering:productOfferings) {
					mongoTemplate.remove(new Query(Criteria.where(AGGREGATE_IDENTIFIER).is(productoffering.getId())),DOMAIN_EVENTS);
				}
				break;
			case PRODUCTOFFERINGPRICE:
				List<ProductOfferingPrice> productOfferingPrices=mongoTemplate.find(new Query(criteria),ProductOfferingPrice.class);
				mongoTemplate.remove(query,ProductOfferingPrice.class);
				for (ProductOfferingPrice productofferingPrice:productOfferingPrices) {
					mongoTemplate.remove(new Query(Criteria.where(AGGREGATE_IDENTIFIER).is(productofferingPrice.getId())),DOMAIN_EVENTS);
				}
				break;
			case PRODUCTSPECIFICATION:
				List<ProductSpecification> productSpecifications=mongoTemplate.find(new Query(criteria),ProductSpecification.class);
				mongoTemplate.remove(query,ProductSpecification.class);
				for (ProductSpecification productSpecification:productSpecifications) {
					mongoTemplate.remove(new Query(Criteria.where(AGGREGATE_IDENTIFIER).is(productSpecification.getId())),DOMAIN_EVENTS);
				}
				break;
			case CATEGORY:
				List<Category> categories=mongoTemplate.find(new Query(criteria),Category.class);
				mongoTemplate.remove(query,Category.class);
				for (Category category:categories) {
					mongoTemplate.remove(new Query(Criteria.where(AGGREGATE_IDENTIFIER).is(category.getId())),DOMAIN_EVENTS);
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid Process Flow Entity.");
		}
		return "success";
	}
}

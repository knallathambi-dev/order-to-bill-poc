// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;


import com.orange.discobole.processflow.exception.DiscoManagedClientException;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.orange.discobole.productcatalog.catalog.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductConfigurationSpec;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
import com.orange.discobole.productcatalog.catalog.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler class for handling events related to product specification.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Component
public class ProductSpecEventHandler {

	@Resource
	private ProductSpecService productSpecService;
	
	@Resource
	private MongodbDataFilterService mongodbDataFilterService;

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecEventHandler.class);
	/**
	 * saves product specification when initiated event is received.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecInitiatedEvent event) {
		ProductSpecification productSpecification = new ProductSpecification();
		productSpecification.id(event.getProductSpecId()).aggregateId(event.getProductSpecId()).lifecycleStatus(event.getResourceState())
				.supportEntity(SupportEntity.fromValue(event.getSupportEntity()))
				.stockItemType(event.getStockItemType())
				.addServiceSpecificationItem(event.getServiceSpecificationRef()).lastUpdate(event.getLastUpdate())
				.isBundle(false)
				.baseType(event.getBaseType()!=null?event.getBaseType().toString():null);
		productSpecService.saveProductSpecification(productSpecification);
	}


	/**
	 * updates product specification when relationship defined event is received.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecRelationDefinedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_SPECIFICATION_RELATIONSHIP, event.getProductSpecRelationships())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate())
				.set(ProductSpecConstants.POLICY_RULES, event.getPolicyRuleRef());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}

	/**
	 * updates product specification when described event is received.
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecIdentityDataEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.BRAND, event.getDefineIdentityData().getBrand())
				.set(ProductSpecConstants.DESCRIPTION, event.getDefineIdentityData().getDescription())
				.set(ProductSpecConstants.NAME, event.getDefineIdentityData().getName())
				.set(ProductSpecConstants.HREF, event.getHref())
				.set(ProductSpecConstants.PRODUCT_NUMBER, event.getDefineIdentityData().getProductNumber())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate())
				.set(ProductSpecConstants.TYPE, event.getType().toString())
				.set(ProductSpecConstants.RELATED_RESOURCE, event.getRelatedResource())
				.set(ProductSpecConstants.RELATED_PARTY, event.getRelatedParty())
				.set(ProductSpecConstants.VALID_FOR, event.getValidFor());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}
	

	/**
	 * updates product specification when operation defined event is received.
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecOpDefinedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.OPERATION_SPECIFICATION, event.getOperationSpecifications())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}

	public void handle(final ComputeProductConfigurationEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_CONFIGURATION, event.getProductConfiguration());
		productSpecService.updateProductSpecification(event.getProdSpecId(), update);
	}

	public void handle(final LinkProductSpecificationToStockItemEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_CONFIGURATION, event.getProductConfiguration())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	}

	/**
	 * this handler method will modify linkProductSpec to stockItem
	 * @param event
	 */
	public void handle(final LinkProductSpecificationToStockItemModificationEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_CONFIGURATION, event.getProductConfiguration())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	}

	
	/**
	 * updates product specification when characteristics defined event is received.
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecCharacteristicsDefinedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTIC, event.getProductSpecificationCharacteristics())
		        .set(ProductSpecConstants.USAGE_SPECIFICATION, event.getUsageSpec())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}

	public void handle(ProductSpecValidatedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	}

	public void handle(ProductSpecVersionCreatedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.VERSION, event.getProductSpecificationVersion())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	}

	/**
	 * Handle {@link ProductSpecCancelledEvent} call the remove of
	 * productSpecService to remove w.r.t. productSPecId
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecCancelledEvent event) {
		ProductSpecification productSpecification = productSpecService
				.fetchProductSpecificationById(event.getProductSpecification().getId());
		productSpecService.removeProductSpecification(productSpecification.getId());
	}

	/**
	 * updates product specification when relationship modified event is received.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecRelationModifiedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_SPECIFICATION_RELATIONSHIP, event.getProductSpecRelationships())
				.set(ProductSpecConstants.POLICY_RULES, event.getPolicyRuleRef())
				.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}

	/**
	 * updates product specification when described event is received.
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecDefineIdentityModifiedEvent event) {
		if ((event.getCurrentLifecycleStatus().equals(ProductSpecificationLifecycle.ACTIVE)) || (event.getCurrentLifecycleStatus().equals(ProductSpecificationLifecycle.INTEST))) {
			Update update = new Update();
			update.set(ProductSpecConstants.BRAND, event.getDefineIdentityData().getBrand())
					.set(ProductSpecConstants.DESCRIPTION, event.getDefineIdentityData().getDescription())
					.set(ProductSpecConstants.NAME, event.getDefineIdentityData().getName())
					.set(ProductSpecConstants.PRODUCT_NUMBER, event.getDefineIdentityData().getProductNumber())
					.set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate())
					.set(ProductSpecConstants.TYPE, event.getType().toString())
					.set(ProductSpecConstants.RELATED_RESOURCE, event.getRelatedResource())
					.set(ProductSpecConstants.RELATED_PARTY, event.getRelatedParty())
					.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus())
					.set(ProductSpecConstants.VALID_FOR, event.getValidFor());
			productSpecService.updateProductSpecification(event.getProductSpecId(), update);
		}else if(event.getCurrentLifecycleStatus().equals(ProductSpecificationLifecycle.LAUNCHED)){
			if(event.getLifecycleStatus().equals(ProductSpecificationLifecycle.RETIRED) || event.getLifecycleStatus().equals(ProductSpecificationLifecycle.UNAVAILABLE) ){
				Update update = new Update();
				update.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
				productSpecService.updateProductSpecification(event.getProductSpecId(), update);
			}else{
				throw new DiscoManagedClientException("only lifecycle status can be updated to retired");
			}
		} else if(event.getCurrentLifecycleStatus().equals(ProductSpecificationLifecycle.RETIRED)){
				Update update = new Update();
                update.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
				productSpecService.updateProductSpecification(event.getProductSpecId(), update);
		}
	}


	/**
	 * modifies product specification when characteristics modified event is
	 * received.
	 *
	 * @param event the event
	 */
	public void handle(ProductSpecCharacteristicsModifiedEvent event) {
		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_SPEC_CHARACTERISTIC, event.getProductSpecificationCharacteristics())
		      .set(ProductSpecConstants.USAGE_SPECIFICATION, event.getUsageSpecifications())
		      .set(ProductSpecConstants.PRODUCT_CONFIGURATION, new ArrayList<>())
			  .set(ProductSpecConstants.LAST_UPDATE, event.getLastUpdate());
		productSpecService.updateProductSpecification(event.getProductSpecId(), update);
	}
	/**
	 * deletes all dummy product specifications and its events when cron job runs
	 * 
	 * @param event : ProductSpecificationDeleteEvent
	 */
	public void handle(final ProductSpecificationDeleteEvent event) {
		 long count=mongodbDataFilterService.filterProductSpecificationEventsData(event.getLastUpdateDateTime(),event.getInterval(),event.getIntervalUnit());
		 LOGGER.debug("ProductSpecification Event records deleted - {}", count);
		 long count2=mongodbDataFilterService.filterProductSpecificsationData(event.getLastUpdateDateTime(),event.getInterval(),event.getIntervalUnit());
		 LOGGER.debug("ProductSpecification records deleted = {} with filters", count2);
		 
	}
	
	public void handle(final ProductSpecificationTemporaryDeleteEvent event) {
		LOGGER.debug("handling event ProductSpecificationTemporaryDeleteEvent {}",event);
		long count=mongodbDataFilterService.filterTemporaryProductSpecificsationData();
		LOGGER.debug("ProductSpecification records deleted - {}", count);
	}

	public void handle(final ProductSpecModificationValidatedEvent event) {
	if(event.getStoredProductSpecification().getLifecycleStatus().equals(ProductSpecificationLifecycle.ACTIVE)||event.getLifecycleStatus().equals(ProductSpecificationLifecycle.INTEST)){
			Update update = new Update();
			update.set(ProductSpecConstants.VERSION, event.getVersion());
			update.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	} else {
		Update update = new Update();
		update.set(ProductSpecConstants.VERSION, event.getVersion());
		productSpecService.updateProductSpecification(event.getProductSpecificationId(), update);
	}
	}

	public void handle(ProductConfigurationModificationEvent event) {
		ProductSpecification ps=productSpecService.fetchProductSpecificationById(event.getProdSpecId());
		List<ProductConfigurationSpec> existing=ps.getProductConfiguration()!=null?ps.getProductConfiguration():new ArrayList<>();
		existing.add(event.getProductConfiguration());

		Update update = new Update();
		update.set(ProductSpecConstants.PRODUCT_CONFIGURATION, existing);
		productSpecService.updateProductSpecification(event.getProdSpecId(), update);
	}
}
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
import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.DefineIdentityData;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.AllowedProductAction;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.PolicyRuleRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
import com.orange.discobole.productcatalog.catalog.service.RedisService;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * handler for Modified product offering events.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
@Component
public class ModifyProductOfferingEventHandler {

	private static final String ATOMIC_PRODUCT_OFFERING = "AtomicProductOffering";

	private static final String PRODUCT_OFFERING_UPDATED_WITH_DESCRIPTION = "ProductOffering updated with description - {}";

	private static final String UPDATED_CATEGORY_ENTITY_RELATIONSHIP = "Updated CategoryEntityRelationship  - {}";

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingEventHandler.class);

	@Resource
	private ProductOfferingService productOfferingService;
	@Resource
	private CategoryEntityRelationshipService categoryEntityRelationshipService;

	private static final String PRODUCTOFFERINGUPDATED = "ProductOffering updated - {}";

	@Resource
	private RedisService redisService;

	/**
	 * updates existing product offering with category.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingCategoryModifiedEvent event) {
		LOGGER.info("Handling AtomicProductOfferingCategoryModifiedEvent");
		LOGGER.debug("Handling AtomicProductOfferingCategoryModifiedEvent - {}", event);
		Set<CategoryRef> categories = new HashSet<>();
		CategoryEntityRelationship entity = categoryEntityRelationshipService
				.fetchEntityById(event.getProductOfferingId());
		if (null != entity) {
			categories.addAll(entity.getCategories());
		} else {
			entity = new CategoryEntityRelationship();
		}
		if (null != event.getAddCategories()) {
			categories.addAll(event.getAddCategories());
		}
		if (null != event.getDelCategories()) {
			categories.removeAll(event.getDelCategories());
		}
		entity.id(event.getProductOfferingId()).categories(categories).type(ATOMIC_PRODUCT_OFFERING);
		LOGGER.debug(UPDATED_CATEGORY_ENTITY_RELATIONSHIP, entity);
		categoryEntityRelationshipService.save(entity);
	}
	public void handle(AtomicProductOfferingIndirectCategoryModifiedEvent event){
		updateCategoriesForProductOfferings(event.getProductOfferingId(),event.getAddCategories(),event.getDelCategories(),ATOMIC_PRODUCT_OFFERING);
	}
	public void handle(BundleProductOfferingIndirectCategoryModifiedEvent event){
		updateCategoriesForProductOfferings(event.getProductOfferingId(),event.getAddCategories(),event.getDelCategories(),ATOMIC_PRODUCT_OFFERING);
	}
	public void handle(ContractProductOfferingIndirectCategoryModifiedEvent event){
		updateCategoriesForProductOfferings(event.getProductOfferingId(),event.getAddCategories(),event.getDelCategories(),ATOMIC_PRODUCT_OFFERING);
	}

	private void updateCategoriesForProductOfferings(String productOfferingId,Set<CategoryRef> addCategories,Set<CategoryRef> delCategories,String productOfferingType){
		Set<CategoryRef> categories = new HashSet<>();
		CategoryEntityRelationship entity = categoryEntityRelationshipService
				.fetchEntityById(productOfferingId);
		if (null != entity) {
			categories.addAll(entity.getCategories());
		} else {
			entity = new CategoryEntityRelationship();
		}
		if (null != addCategories) {
			categories.addAll(addCategories);
		}
		if (null != delCategories) {
			categories.removeAll(delCategories);
		}
		entity.id(productOfferingId).categories(categories).type(productOfferingType);
		LOGGER.debug(UPDATED_CATEGORY_ENTITY_RELATIONSHIP, entity);
		categoryEntityRelationshipService.save(entity);
	}

	/**
	 * updates existing product offering with Identity Data.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("Handling AtomicProductOfferingIdentityDataModifiedEvent");
		LOGGER.debug("Handling AtomicProductOfferingDescribedModifiedEvent - {}", event);

		Update update = new Update();
		DefineIdentityData identity = event.getIdentityData();

		if (event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE)
				|| event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.INTEST)) {

			update.set(ProductOfferingConstants.DESCRIPTION, identity.getDescription());
			update.set(ProductOfferingConstants.BRAND, identity.getBrand());
			update.set(ProductOfferingConstants.NAME, identity.getName());
			update.set(ProductOfferingConstants.IS_INSTALLABLE, identity.getIsInstallable());
			update.set(ProductOfferingConstants.IS_SELLABLE, identity.getIsSellable());
			update.set(ProductOfferingConstants.IS_VISIBLE, identity.getIsVisible());
			update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
			update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
			update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
			update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
			update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
			update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
			update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());

			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);

		} else if (event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.LAUNCHED)) {
			if (event.getLifecycleStatus().equals(ProductOfferingLifecycle.RETIRED)
					|| event.getLifecycleStatus().equals(ProductOfferingLifecycle.UNAVAILABLE)) {

				update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
				productOfferingService.updateProductOffering(event.getProductOfferingId(), update);

			} else {
				throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_PO_INVALID_PO_LIFECYCLE);
			}
		} else {
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		}
	}

	/**
	 * updates existing product offering with Operations.
	 *
	 * @param event the event
	 */
	public void handle(AtomicProductOfferingOperationModifiedEvent event) {
		LOGGER.info("Handling AtomicProductOfferingOperationModifiedEvent");
		LOGGER.debug("Handling AtomicProductOfferingOperationModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.commercialOperation(event.getOperationSpecifications()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);
//		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationSpecifications());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
        update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getProductOfferingTerm());
		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}


	public void handle(ModifyProductOfferingAllowedActionDefinedEvent event) {
		LOGGER.debug("modify allowedAction for PO {}", event.getProductOfferingId());
		// Fetch the allowed actions from the event
		List<AllowedProductAction> allowedAction = event.getAllowedAction();

		// Prepare the update object
		Update update = new Update();
		update.set(ProductOfferingConstants.ALLOWED_ACTIONS, allowedAction);
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		// Perform the update on the ProductOffering
		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * updates existing product offering LinkPOPtoOperations.
	 *
	 * @param event the event
	 */
	public void handle(LinkPOPtoOperModifiedEvent event) {
		LOGGER.debug("Handling LinkPOPtoOperModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProdOffId());
//		productOffering.commercialOperation(event.getOperationList()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);
//		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationList());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getProductOfferingTerm());

		productOfferingService.updateProductOffering(event.getProdOffId(), update);
	}

	/***
	 * Handler method to update existing product offering with product offering
	 * characteristics
	 *
	 * @param event the event
	 */
	public void handle(AtomicProductOfferingCharacteristicsModifiedEvent event) {
		LOGGER.debug("Handling AtomicProductOfferingCharacteristicsModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.prodSpecCharValueUse(event.getProductSpecificationCharacteristicValueUse())
//				.lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);
//		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.PROD_SPEC_CHAR_VALUE_USE, event.getProductSpecificationCharacteristicValueUse());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * Handler method to update existing product offering with relationships in
	 * query database.
	 *
	 * @param event event which needs to be updated
	 */
	public void handle(final AtomicProductOfferingRelationshipModifiedEvent event) {
		LOGGER.debug("Handling AtomicProductOfferingRelationshipModifiedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		List<ProductOfferingRelationship> poRelationships = productOffering.getProductOfferingRelationship();
		poRelationships.removeAll(event.getDeleteProductOfferingRelationships());
		poRelationships.addAll(event.getAddProductOfferingRelationships());
		productOffering.productOfferingRelationship(poRelationships).
				lastUpdate(event.getLastUpdate());
		productOfferingService.saveProductOffering(productOffering);
		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
	}

	public void handle(final AtomicProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.debug("Handling AtomicProductOfferingIncompaRelationshipModifiedEvent - {}", event);
		updateIncompatibleProductoffeeings(event.getProductOfferingId(),
				event.getDeleteProductOfferingRelationships(), event.getAddProductOfferingRelationships(), event.getLastUpdate(), event.getPolicyRuleRef());
	}

	public void handle(final BundleProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingIncompatibleRelationshipModifiedEvent - {}", event);
		updateIncompatibleProductoffeeings(event.getProductOfferingId(),
				event.getDeleteProductOfferingRelationships(), event.getAddProductOfferingRelationships(), event.getLastUpdate(), event.getPolicyRuleRef());
	}

	public void handle(final ContractProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingIncompaRelationshipModifiedEvent - {}", event);
		updateIncompatibleProductoffeeings(event.getProductOfferingId(),
				event.getDeleteProductOfferingRelationships(), event.getAddProductOfferingRelationships(), event.getLastUpdate(), event.getPolicyRuleRef());

	}

	public void handle(final ContractProductOfferingCategoryModifiedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingCategoryModifiedEvent - {}", event);
		Set<CategoryRef> categories = new HashSet<>();
		CategoryEntityRelationship entity = categoryEntityRelationshipService
				.fetchEntityById(event.getProductOfferingId());
		if (null != entity) {
			categories.addAll(entity.getCategories());
		} else {
			entity = new CategoryEntityRelationship();
		}
		if (null != event.getAddCategories()) {
			categories.addAll(event.getAddCategories());
		}
		if (null != event.getDelCategories()) {
			categories.removeAll(event.getDelCategories());
		}
		entity.id(event.getProductOfferingId()).categories(categories).type("Contract");
		LOGGER.debug(UPDATED_CATEGORY_ENTITY_RELATIONSHIP, entity);
		categoryEntityRelationshipService.save(entity);
	}

	public void handle(final ContractProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("Handling ContractProductOfferingIdentityDataModifiedEvent");
		LOGGER.debug("Handling ContractProductOfferingIdentityDataModifiedEvent - {}", event);

		Update update = new Update();

		if ((event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE)) ||
				(event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.INTEST))) {

			update.set(ProductOfferingConstants.DESCRIPTION, event.getIdentityData().getDescription());
			update.set(ProductOfferingConstants.BRAND, event.getIdentityData().getBrand());
			update.set(ProductOfferingConstants.NAME, event.getIdentityData().getName());
			update.set(ProductOfferingConstants.IS_SELLABLE, event.getIdentityData().getIsSellable());
			update.set(ProductOfferingConstants.IS_VISIBLE, event.getIdentityData().getIsVisible());
			update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIdentityData().getIsInstallable());
			update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
			update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
			update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
			update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
			update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
			update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
			update.set(ProductOfferingConstants.BILLING_TYPE, event.getIdentityData().getBillingType());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);

		} else if (event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.LAUNCHED)) {
			if (event.getLifecycleStatus().equals(ProductOfferingLifecycle.RETIRED) ||
					event.getLifecycleStatus().equals(ProductOfferingLifecycle.UNAVAILABLE)) {

				update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
				productOfferingService.updateProductOffering(event.getProductOfferingId(), update);

			} else {
				throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_PO_INVALID_PO_LIFECYCLE);
			}

		} else {
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		}
	}

	public void handle(final ContractProductOfferingRelationshipModifiedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingRelationshipModifiedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		List<ProductOfferingRelationship> poRelationships = productOffering.getProductOfferingRelationship();
		poRelationships.removeAll(event.getDeleteProductOfferingRelationships());
		poRelationships.addAll(event.getAddProductOfferingRelationships());
		productOffering.productOfferingRelationship(poRelationships).
				lastUpdate(event.getLastUpdate());
		productOfferingService.saveProductOffering(productOffering);
		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
	}

	public void handle(ContractProductOfferingOperModifiedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingOperModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.commercialOperation(event.getOperationSpecifications()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationSpecifications());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * Update contract product offering with Product offering
	 *
	 * @param event the event
	 * @author Ayush Khanna
	 */
	public void handle(ContractProductOfferingSelectedModifiedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingSelectedModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.globalMinCardinality(event.getGlobalMinCardinality())
//					.globalMaxCardinality(event.getGlobalMaxCardinality())
//					.bundledProductOffering(event.getBundleProductOffering()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.GLOBAL_MIN_CARDINALITY, event.getGlobalMinCardinality());
		update.set(ProductOfferingConstants.GLOBAL_MAX_CARDINALITY, event.getGlobalMaxCardinality());
		update.set(ProductOfferingConstants.BUNDLED_PRODUCT_OFFERING, event.getBundleProductOffering());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	public void handle(ContractProductOfferingModificationValidatedEvent event) {
		LOGGER.info("Handling ContractProductOfferingModificationValidatedEvent");
		LOGGER.debug("Handling ContractProductOfferingModificationValidatedEvent - {}", event);
		if(event.getProductOffering().getLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE)||event.getLifecycleStatus().equals(ProductOfferingLifecycle.INTEST)){
			Update update = new Update();
			update.set(ProductOfferingConstants.VERSION, event.getVersion());
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		} else {
			Update update = new Update();
			update.set(ProductOfferingConstants.VERSION, event.getVersion());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		}
		if(redisService != null){
			try {
				redisService.deleteProductOffering(event.getProductOfferingId());
			}catch (Exception exception){
				LOGGER.info("exception: {}", exception.getMessage());
			}
		}

	}

	private void updateIncompatibleProductoffeeings(String productOfferingId,
													Set<ProductOfferingRelationship> deleteProductOfferingRelationships, Set<ProductOfferingRelationship> addProductOfferingRelationships, OffsetDateTime lastUpdate, List<PolicyRuleRef> policyRuleRef) {
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(productOfferingId);
		Set<ProductOfferingRelationship> poRelationships = productOffering.getProductOfferingRelationship().stream().collect(Collectors.toSet());
		poRelationships.removeAll(deleteProductOfferingRelationships);
		poRelationships.addAll(addProductOfferingRelationships);
		productOffering.productOfferingRelationship(poRelationships.stream().toList());
		productOffering.policyRuleRef(policyRuleRef);
		productOffering.lastUpdate(lastUpdate);
		productOfferingService.saveProductOffering(productOffering);
		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
	}

	public void handle(final BundleProductOfferingCategoryModifiedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingCategoryModifiedEvent - {}", event);
		Set<CategoryRef> categories = new HashSet<>();
		CategoryEntityRelationship entity = categoryEntityRelationshipService
				.fetchEntityById(event.getProductOfferingId());
		if (null != entity) {
			categories.addAll(entity.getCategories());
		} else {
			entity = new CategoryEntityRelationship();
		}
		if (null != event.getAddCategories()) {
			categories.addAll(event.getAddCategories());
		}
		if (null != event.getDelCategories()) {
			categories.removeAll(event.getDelCategories());
		}
		entity.id(event.getProductOfferingId()).categories(categories).type("Contract");
		LOGGER.debug(UPDATED_CATEGORY_ENTITY_RELATIONSHIP, entity);
		categoryEntityRelationshipService.save(entity);
	}

	public void handle(final BundleProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("Handling BundleProductOfferingIdentityDataModifiedEvent");
		LOGGER.debug("Handling BundleProductOfferingIdentityDataModifiedEvent - {}", event);


		Update update = new Update();

		// Check current lifecycle status and build update accordingly
		if (event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE) ||
				event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.INTEST)) {

			update.set(ProductOfferingConstants.DESCRIPTION, event.getIdentityData().getDescription());
			update.set(ProductOfferingConstants.BRAND, event.getIdentityData().getBrand());
			update.set(ProductOfferingConstants.NAME, event.getIdentityData().getName());
			update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIdentityData().getIsInstallable());
			update.set(ProductOfferingConstants.IS_SELLABLE, event.getIdentityData().getIsSellable());
			update.set(ProductOfferingConstants.IS_VISIBLE, event.getIdentityData().getIsVisible());
			update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
			update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
			update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
			update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
			update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
			update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
			update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());

		} else if (event.getCurrentLifecycleStatus().equals(ProductOfferingLifecycle.LAUNCHED)) {
			if (event.getLifecycleStatus().equals(ProductOfferingLifecycle.RETIRED) ||
					event.getLifecycleStatus().equals(ProductOfferingLifecycle.UNAVAILABLE)) {

				update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());

			} else {
				throw new DiscoManagedClientException(ProductOfferingConstants.DISCO_PO_INVALID_PO_LIFECYCLE);
			}
		} else {
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
		}

		// Apply the update only if there is something to update
		if (!update.getUpdateObject().isEmpty()) {
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		} else {
			LOGGER.warn("No updates applied for ProductOffering with id: {}", event.getProductOfferingId());
		}
	}

	public void handle(final BundleProductOfferingRelationshipModifiedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingRelationshipModifiedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		List<ProductOfferingRelationship> poRelationships = productOffering.getProductOfferingRelationship();
		poRelationships.removeAll(event.getDeleteProductOfferingRelationships());
		poRelationships.addAll(event.getAddProductOfferingRelationships());
		productOffering.productOfferingRelationship(poRelationships).
				lastUpdate(event.getLastUpdate());
		productOfferingService.saveProductOffering(productOffering);
		LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
	}

	public void handle(BundleProductOfferingOperModifiedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingOperModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.commercialOperation(event.getOperationSpecifications()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationSpecifications());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * Update contract product offering with Product offering
	 *
	 * @param event the event
	 * @author Ayush Khanna
	 */
	public void handle(BundleProductOfferingSelectedModifiedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingSelectedModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.globalMinCardinality(event.getGlobalMinCardinality())
//					.globalMaxCardinality(event.getGlobalMaxCardinality())
//					.bundledProductOffering(event.getBundleProductOffering()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.GLOBAL_MIN_CARDINALITY, event.getGlobalMinCardinality());
		update.set(ProductOfferingConstants.GLOBAL_MAX_CARDINALITY, event.getGlobalMaxCardinality());
		update.set(ProductOfferingConstants.BUNDLED_PRODUCT_OFFERING, event.getBundleProductOffering());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);

	}

	public void handle(BundleProductOfferingModificationValidatedEvent event) {
		LOGGER.info("Handling BundleProductOfferingModificationValidatedEvent");
		LOGGER.debug("Handling BundleProductOfferingModificationValidatedEvent - {}", event);
		if(event.getProductOffering().getLifecycleStatus().equals(ProductOfferingLifecycle.ACTIVE)||event.getLifecycleStatus().equals(ProductOfferingLifecycle.INTEST)){
			Update update = new Update();
			update.set(ProductOfferingConstants.VERSION, event.getVersion());
			update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		} else {
			Update update = new Update();
			update.set(ProductOfferingConstants.VERSION, event.getVersion());
			productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		}
		if(redisService != null){
			try {
				redisService.deleteProductOffering(event.getProductOfferingId());
			}catch (Exception exception){
				LOGGER.info("exception: {}", exception.getMessage());
			}
		}
	}

}

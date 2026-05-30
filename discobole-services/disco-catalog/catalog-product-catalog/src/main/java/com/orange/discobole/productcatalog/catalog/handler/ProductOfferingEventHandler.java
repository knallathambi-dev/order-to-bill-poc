// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryEntityRelationship;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.CategoryRef;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.catalog.dto.generated.productoffering.AllowedProductAction;
import com.orange.discobole.productcatalog.catalog.service.CategoryEntityRelationshipService;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingService;
import com.orange.discobole.productcatalog.catalog.service.RedisService;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingModificationValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.ProductOfferingPolicyRuleAssociationModifiedEvent;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * handler for product offering events.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Component
public class ProductOfferingEventHandler {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingEventHandler.class);

	@Resource
	private ProductOfferingService productOfferingService;
	
	@Resource
	private MongodbDataFilterService mongodbDataFilterService; 
	
	@Resource
	private CategoryEntityRelationshipService categoryEntityRelationshipService;
	
	private static final String PRODUCTOFFERINGUPDATED = "ProductOffering updated - {}";

	@Resource
	private RedisService redisService;
	/**
	 * saves product offering when Type selected event is received.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final ProductOfferingTypeSelectedEvent event) {
		ProductOffering productOffering = new ProductOffering();
		productOffering.id(event.getProductOfferingId()).lifecycleStatus(ProductOfferingLifecycle.INSTUDY)
				.type(event.getType()).lastUpdate(event.getLastUpdate());
		productOfferingService.saveProductOffering(productOffering);
	}
	/**
	 * saves product offering when initiated event is received.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingInitiatedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.lifecycleStatus(event.getLifecycleStatus())
//				.productSpecification(event.getProductSpec()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS,event.getLifecycleStatus()).set(ProductOfferingConstants.PRODUCT_SPECIFICATION_REF,event.getProductSpec()).set(ProductOfferingConstants.LAST_UPDATE,event.getLastUpdate());
		productOfferingService.updateProductOffering(event.getProductOfferingId(),update);
	}

	/**
	 * updates existing product offering with description.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingIdentityDataDefinedEvent event) {
		LOGGER.info("Handling AtomicProductOfferingIdentityDataDefinedEvent");
		LOGGER.debug("Handling AtomicProductOfferingIdentityDataDefinedEvent - {}", event);

		Update update = new Update();
		update.set(ProductOfferingConstants.DESCRIPTION, event.getIdentityData().getDescription());
		update.set(ProductOfferingConstants.BRAND, event.getIdentityData().getBrand());
		update.set(ProductOfferingConstants.NAME, event.getIdentityData().getName());
		update.set(ProductOfferingConstants.IS_SELLABLE, event.getIdentityData().getIsSellable());
		update.set(ProductOfferingConstants.IS_VISIBLE, event.getIdentityData().getIsVisible());
		update.set(ProductOfferingConstants.IS_BUNDLE, event.getIdentityData().getIsBundle());
		update.set(ProductOfferingConstants.HREF, event.getHref());
		update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIdentityData().getIsInstallable());
		update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
		update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
		update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
		update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
		update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	/**
	 * updates existing bundle product offering with description.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingIdentityDataDefinedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingDescribedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.description(event.getIdentityData().getDescription()).brand(event.getIdentityData().getBrand()).
//					name(event.getIdentityData().getName())
//					.isSellable(event.getIdentityData().getIsSellable())
//					.isVisible(event.getIdentityData().getIsVisible())
//					.isBundle(event.getIdentityData().getIsBundle())
//					.href(event.getHref())
//					.isInstallable(event.getIdentityData().getIsInstallable()).statusReason(event.getStatusReason())
//					.channel(event.getChannels()).marketSegment(event.getMarketSegments()).relatedParty(event.getRelatedParties()).
//					productOfferingTerm(event.getPoTerms()).validFor(event.getValidity()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.DESCRIPTION, event.getIdentityData().getDescription());
		update.set(ProductOfferingConstants.BRAND, event.getIdentityData().getBrand());
		update.set(ProductOfferingConstants.NAME, event.getIdentityData().getName());
		update.set(ProductOfferingConstants.IS_SELLABLE, event.getIdentityData().getIsSellable());
		update.set(ProductOfferingConstants.IS_VISIBLE, event.getIdentityData().getIsVisible());
		update.set(ProductOfferingConstants.IS_BUNDLE, event.getIdentityData().getIsBundle());
		update.set(ProductOfferingConstants.HREF, event.getHref());
		update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIdentityData().getIsInstallable());
		update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
		update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
		update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
		update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
		update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	
	/**
	 * updates existing bundle product offering with description.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingIdentityDataDefinedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingDescribedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.description(event.getIdentityData().getDescription()).brand(event.getIdentityData().getBrand()).
//					name(event.getIdentityData().getName()).billingType(event.getIdentityData().getBillingType())
//					.isSellable(event.getIdentityData().getIsSellable())
//					.isVisible(event.getIdentityData().getIsVisible())
//					.isBundle(event.getIdentityData().getIsBundle())
//					.href(event.getHref())
//					.isInstallable(event.getIdentityData().getIsInstallable()).statusReason(event.getStatusReason())
//					.channel(event.getChannels()).marketSegment(event.getMarketSegments()).relatedParty(event.getRelatedParties()).
//					productOfferingTerm(event.getPoTerms()).validFor(event.getValidity()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.DESCRIPTION, event.getIdentityData().getDescription());
		update.set(ProductOfferingConstants.BRAND, event.getIdentityData().getBrand());
		update.set(ProductOfferingConstants.NAME, event.getIdentityData().getName());
		update.set(ProductOfferingConstants.BILLING_TYPE, event.getIdentityData().getBillingType());
		update.set(ProductOfferingConstants.IS_SELLABLE, event.getIdentityData().getIsSellable());
		update.set(ProductOfferingConstants.IS_VISIBLE, event.getIdentityData().getIsVisible());
		update.set(ProductOfferingConstants.IS_BUNDLE, event.getIdentityData().getIsBundle());
		update.set(ProductOfferingConstants.HREF, event.getHref());
		update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIdentityData().getIsInstallable());
		update.set(ProductOfferingConstants.STATUS_REASON, event.getStatusReason());
		update.set(ProductOfferingConstants.CHANNEL, event.getChannels());
		update.set(ProductOfferingConstants.MARKET_SEGMENT, event.getMarketSegments());
		update.set(ProductOfferingConstants.RELATED_PARTY, event.getRelatedParties());
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getPoTerms());
		update.set(ProductOfferingConstants.VALID_FOR, event.getValidity());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	/**
	 * updates existing product offering with category.
	 *
	 * @author Piyush Goel
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingCategoryDefinedEvent event) {
		LOGGER.debug("Handling AtomicProductOfferingCategoryDefinedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getProductOfferingId());
		if (null == entity) {
			entity = new CategoryEntityRelationship();
		}
	  Set<CategoryRef> categoryRefs=new HashSet<>(event.getCategories());
		entity.id(event.getProductOfferingId()).categories(categoryRefs).type(productOffering.getType().toString()).setLastUpdate(event.getLastUpdate());
		categoryEntityRelationshipService.save(entity);
	}
	
	/**
	 * updates existing product offering with category.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingCategoryDefinedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingCategoryDefinedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getProductOfferingId());
		if (null == entity) {
			entity = new CategoryEntityRelationship();
		}
		Set<CategoryRef> categoryRefs=new HashSet<>(event.getCategories());
		entity.id(event.getProductOfferingId()).categories(categoryRefs).type(productOffering.getType().toString())
		.setLastUpdate(event.getLastUpdate());
		categoryEntityRelationshipService.save(entity);
	}
	

	/**
	 * updates existing product offering with category.
	 *
	 * @author Ayush Khanna
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingCategoryDefinedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingCategoryDefinedEvent - {}", event);
		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
		CategoryEntityRelationship entity = categoryEntityRelationshipService.fetchEntityById(event.getProductOfferingId());
		if (null == entity) {
			entity = new CategoryEntityRelationship();
		}
		Set<CategoryRef> categoryRefs=new HashSet<>(event.getCategories());
		entity.id(event.getProductOfferingId()).categories(categoryRefs).type(productOffering.getType().toString())
		.setLastUpdate(event.getLastUpdate());
		categoryEntityRelationshipService.save(entity);
	}

	/**
	 * Handler method to update existing product offering with relationships in
	 * query database.
	 *
	 * @param event event which needs to be updated
	 */
	public void handle(final AtomicProductOfferingRelationshipDefinedEvent event) {
		LOGGER.debug("Handling AtomicProductOfferingRelationshipDefinedEvent - {}", event);

//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.productOfferingRelationship(event.getProductOfferingRelationships())
//				.lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_RELATIONSHIP, event.getProductOfferingRelationships());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	/**
	 * Handler method to update existing bundle product offering with relationships in
	 * query database.
	 *
	 * @author Vishal Vachaspati
	 * @param event event which needs to be updated
	 */
	public void handle(final BundleProductOfferingRelationshipDefinedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingRelationshipDefinedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.productOfferingRelationship(event.getProductOfferingRelationships())
//					.lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_RELATIONSHIP, event.getProductOfferingRelationships());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	/**
	 * Handler method to update existing bundle product offering with relationships in
	 * query database.
	 *
	 * @author Vishal Vachaspati
	 * @param event event which needs to be updated
	 */
	public void handle(final ProductOfferingPolicyRuleAssociationDefinedEvent event) {
		LOGGER.debug("Handling ProductOfferingPolicyRuleAssociationDefinedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.policyRuleRef(event.getProductOfferingPolicyRuleAssociation())
//					.lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.POLICY_RULE_REF, event.getProductOfferingPolicyRuleAssociation());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	/**
	 * Handler method to update existing contract product offering with relationships in
	 * query database.
	 *
	 * @author Ayush Khanna
	 * @param event event which needs to be updated
	 */
	public void handle(final ContractProductOfferingRelationshipDefinedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingRelationshipDefinedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.productOfferingRelationship(event.getProductOfferingRelationships())
//					.lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_RELATIONSHIP, event.getProductOfferingRelationships());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}


	/***
	 * Handler method to update existing product offering with product offering
	 * characteristics
	 *
	 * @author Shreya Sharma
	 * @param event the event
	 */
	public void handle(AtomicProductOfferingCharacteristicsDefinedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getproductOfferingId());
//		productOffering.prodSpecCharValueUse(event.getProductSpecificationCharacteristicValueUse())
//				.lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.PROD_SPEC_CHAR_VALUE_USE, event.getProductSpecificationCharacteristicValueUse());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getproductOfferingId(), update);
	}

	/**
	 * updates existing product offering with updated lifecycle status IN_TEST.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingValidatedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.lifecycleStatus(event.getLifecycleStatus()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	/**
	 * updates existing product offering with updated lifecycle status IN_TEST.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingValidatedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingValidatedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.lifecycleStatus(event.getLifecycleStatus()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	/**
	 * updates existing product offering with updated lifecycle status IN_TEST.
	 *
	 * @author Ayush Khanna
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingValidatedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingValidatedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.lifecycleStatus(event.getLifecycleStatus()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}


	/**
	 * updates existing product offering with a new created version.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingVersionCreatedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.version(event.getProductOfferingVersion()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.VERSION, event.getProductOfferingVersion());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	
	/**
	 * updates existing product offering with a new created version.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingVersionCreatedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingVersionCreatedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.version(event.getProductOfferingVersion()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.VERSION, event.getProductOfferingVersion());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * updates existing product offering with a new created version.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingVersionCreatedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingVersionCreatedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.version(event.getProductOfferingVersion()).lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.VERSION, event.getProductOfferingVersion());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}
	/**
	 * updates existing product offering with isBundle.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingBundleDefinedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.isBundle(event.isIsBundle()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.IS_BUNDLE, event.isIsBundle());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * Handle {@link ProductOffCancelledEvent} call the remove of
	 * productOfferingService to remove w.r.t. productSPecId
	 *
	 * @param event the event
	 */
	public void handle(ProductOffCancelledEvent event) {
//		ProductOffering productOffering = productOfferingService
//				.fetchProductOfferingById(event.getProductOffering().getId());
		productOfferingService.removeProductOffering(event.getProductOffering().getId());
	}

	/**
	 * Handle {@link AtomicProductOfferingOperDefinedEvent}.
	 *
	 * @author Varshika Choudhary
	 * @param event the event
	 */
	public void handle(AtomicProductOfferingOperDefinedEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.commercialOperation(event.getOperationSpecifications()).lastUpdate(event.getLastUpdate());
//		productOffering.productOfferingTerm(event.getProductOfferingTerm());
//		productOfferingService.saveProductOffering(productOffering);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		productOffering.commercialOperation(event.getOperationSpecifications()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationSpecifications());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getProductOfferingTerm());
		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}


	/**
	 * Handles the ProductOfferingAllowedActionDefinedEvent by updating the corresponding
	 * ProductOffering with the allowed actions.
	 */
	public void handle(ProductOfferingAllowedActionDefinedEvent event) {
		LOGGER.debug("Updating allowedAction for PO {}", event.getProductOfferingId());
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
	 * Handle {@link LinkPOPtoOperEvent}.
	 *
	 * @author Varshika Choudhary
	 * @param event the event
	 */
	public void handle(LinkPOPtoOperEvent event) {
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProdOffId());
//		productOffering.commercialOperation(event.getOperationList()).lastUpdate(event.getLastUpdate());
//		productOfferingService.saveProductOffering(productOffering);

		Update update = new Update();
		update.set(ProductOfferingConstants.COMMERCIAL_OPERATION, event.getOperationList());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
        update.set(ProductOfferingConstants.PRODUCT_OFFERING_TERM, event.getProductOfferingTerm());
		productOfferingService.updateProductOffering(event.getProdOffId(), update);
	}
	
	/**
	 * creates Bundle product offering when event is received.
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(CreateBundleProductOfferingEvent event) {
		LOGGER.debug("Handling CreateBundleProductOfferingEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.isSellable(event.getIsSellable()).isInstallable(event.getIsInstallable())
//					.isBundle(event.getIsBundle()).lastUpdate(event.getLastUpdate()).type(event.getType());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.IS_SELLABLE, event.getIsSellable());
		update.set(ProductOfferingConstants.IS_INSTALLABLE, event.getIsInstallable());
		update.set(ProductOfferingConstants.IS_BUNDLE, event.getIsBundle());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingConstants.TYPE, event.getType());

		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
	}

	/**
	 * update contract product offering Commercial Operation with Product offering
	 * operations
	 *
	 * @author Ayush Khanna
	 * @param event the event
	 */
	public void handle(ContractProductOfferingOperDefinedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingOperDefinedEvent - {}", event);
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
	 * update bundle product offering Commercial Operation with Product offering
	 * operations
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(BundleProductOfferingOperDefinedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingOper]DefinedEvent - {}", event);
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
	 * Update Bundle product offering with Product offering
	 * 
	 *
	 * @author Vishal Vachaspati
	 * @param event the event
	 */
	public void handle(BundleProductOfferingSelectedEvent event) {
		LOGGER.debug("Handling BundleProductOfferingOperDefinedEvent - {}", event);
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
	/**
	 * Update contract product offering with Product offering
	 * 
	 *
	 * @author Ayush Khanna
	 * @param event the event
	 */
	public void handle(ContractProductOfferingSelectedEvent event) {
		LOGGER.debug("Handling ContractProductOfferingSelectedEvent - {}", event);
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

	public void handle(ProductOfferingDeleteEvent event) {
		long eventDeletionCount=mongodbDataFilterService.filterProductOfferingEventsData(event.getLastUpdateDateTime(),event.getInterval(),event.getIntervalUnit());
		LOGGER.debug("ProductOffering Events records deleted - {}", eventDeletionCount);
		long entityDeletionCount=mongodbDataFilterService.filterProductOfferingData(event.getLastUpdateDateTime(),event.getInterval(),event.getIntervalUnit());
		 LOGGER.debug("ProductOffering records deleted - {}", entityDeletionCount);
		 
	}
	public void handle(ProductOfferingTemporaryDeleteEvent event) {
		 LOGGER.debug("ProductOfferingTemporaryDeleteEvent : {} ",event);
		 long count=mongodbDataFilterService.filterTemporaryProductOfferingData();
		 LOGGER.debug("ProductOffering records deleted - {}", count);
		 
	}


	public void handle(final ProductOfferingPolicyRuleAssociationModifiedEvent event) {
		LOGGER.debug("Handling ProductOfferingPolicyRuleAssociationModifiedEvent - {}", event);
//		ProductOffering productOffering = productOfferingService.fetchProductOfferingById(event.getProductOfferingId());
//		if (productOffering != null) {
//			productOffering.policyRuleRef(event.getProductOfferingPolicyRuleAssociation())
//					.lastUpdate(event.getLastUpdate());
//			productOfferingService.saveProductOffering(productOffering);
//			LOGGER.debug(PRODUCTOFFERINGUPDATED, productOffering);
//		}

		Update update = new Update();
		update.set(ProductOfferingConstants.POLICY_RULE_REF, event.getProductOfferingPolicyRuleAssociation());
		update.set(ProductOfferingConstants.LAST_UPDATE, event.getLastUpdate());
		productOfferingService.updateProductOffering(event.getProductOfferingId(), update);
		if(redisService != null){
			try {
				redisService.deleteProductOffering(event.getProductOfferingId());
			}catch (Exception exception){
				LOGGER.info("exception: {}", exception.getMessage());
			}
		}
	}

	public void handle(AtomicProductOfferingModificationValidatedEvent event) {
		LOGGER.info("Handling AtomicProductOfferingModificationValidatedEvent");
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
		// for modify only
		if(redisService != null){
			try {
				redisService.deleteProductOffering(event.getProductOfferingId());
			}catch (Exception exception){
				LOGGER.info("exception: {}", exception.getMessage());
			}
		}
	}
}
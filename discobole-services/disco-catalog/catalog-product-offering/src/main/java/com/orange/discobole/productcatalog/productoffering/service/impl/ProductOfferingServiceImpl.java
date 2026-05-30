// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.command.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.dto.policyrule.PolicyOfferingEvent;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingCategoryDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingCategoryDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingCategoryDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOfferingTypeSelectedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.SelectProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Implementation to process Product Offering Commands
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Service
public class ProductOfferingServiceImpl implements ProductOfferingService {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingServiceImpl.class);

	@Resource
	private Publisher publisher;


	private MongoEventStoreImpl mongoEventStoreImpl;

	@Resource
	private QueryService queryService;

	private final CommandGateway commandGateway;

	@Resource
	ApplicationContext appCtx;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public ProductOfferingServiceImpl(CommandGateway commandGateway,MongoEventStoreImpl mongoEventStoreImpl) {
		this.commandGateway = commandGateway;
		this.mongoEventStoreImpl= mongoEventStoreImpl;

	}

	@Override
	public String createProductOfferingType(final SelectProductOfferingType offeringType) {
		String aggregateId = UUID.randomUUID().toString();
		LOGGER.debug("Triggerring SelectProductOfferingTypeCommand with aggregateId : {}", aggregateId);
		return commandGateway
				.sendAndWait(new SelectProductOfferingTypeCommand(aggregateId, offeringType.getProductOfferingType(),
						offeringType.getIsSellable(), offeringType.getIsBundle(), offeringType.getIsInstallable()));

	}

	@Override
	public void createProductOffering(final String productSpecId, final String productOffId) {
		LOGGER.debug("Triggerring InitiateProductOfferingCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new InitiateProductOfferingCommand(productOffId, productSpecId));

	}

	@Override
	public void defineProductOffDesc(String productOffId, DefineIdentityData defineIdentityData, Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
									 Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason) {
		LOGGER.debug("Triggerring ProductOfferingDescriptionCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ProductOfferingIdentityDataCommand(productOffId, defineIdentityData, channelIds,marketSegmentIds,
				relatedParties, poTerms,validity, type,statusReason));
	}

	@Override
	public void defineContractProductOffDesc(String productOffId, DefineContractIdentityData defineData,
			Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
			Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason,
			OffsetDateTime lastUpdate) {
		LOGGER.debug("Triggerring processContractProductOfferingDescriptionCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ContractProductOfferingDescriptionCommand(productOffId, defineData,
				channelIds, marketSegmentIds, relatedParties, poTerms, validity, type, statusReason, lastUpdate));
	}


	/**
	 * function to define Product Offering categories
	 *
	 * @param productOfferingId product offering id
	 * @param categories        categories to be defined
	 */
	@Override
	public void defineProductOfferingCategory(final String productOfferingId, final List<String> categories) {
		LOGGER.debug("Triggerring DefineProductOfferingCategoryCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new DefineProductOfferingCategoryCommand(productOfferingId, categories));

	}


	public void updateProductOfferingCharacteristics(final String productOfferingId,
													 final List<PickAtomicProductOfferingCharacteristic> pickAtomicProductOfferingCharacteristics) {
		LOGGER.debug("Triggerring SelectProductOfferingCharacteristicCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new SelectProductOfferingCharacteristicCommand(productOfferingId,
				pickAtomicProductOfferingCharacteristics));

	}


	@Override
	public void defineProductOfferingEnitityRelationship(String productOfferingId,
			List<ProductOfferingRelationship> relationships) {
		LOGGER.debug("Triggerring ProductOfferingRelationshipCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ProductOfferingRelationshipCommand(productOfferingId, relationships));

	}
	
	@Override
	public void defineProductOfferingPolicyRuleAssociation(String productOfferingId, List<PolicyRuleRef> policyRules) {
		LOGGER.debug("Triggerring ProductOfferingPolicyRuleAssociation with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ProductOfferingPolicyRuleAssociationCommand(productOfferingId, policyRules));
	}

	@Override
	public void validateProductOffering(String productOfferingId) {
		LOGGER.info("Triggerring ProductOfferingValidatedCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ProductOfferingValidatedCommand(productOfferingId));
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();
		// Read and process events
		for (DomainEventMessage<Event> eventmsg : mongoEventStoreImpl.readEventsBackword(productOfferingId, 0)) {
			Event event = eventmsg.getPayload();
			if (!duplicateCheck.add(eventmsg.getPayloadType().getSimpleName())) continue;
			if (event instanceof ProductOfferingTypeSelectedEvent) break;
			processCategoryEvent(event, project);
			processRelationshipEvent(event, project);

		}

		// Publish collected events
		publisher.project(project);


	}

	private void processCategoryEvent(Event event, List<Event> projectEvents) {
		if (event instanceof AtomicProductOfferingCategoryDefinedEvent atomicEvent && !ObjectUtils.isEmpty(atomicEvent.getCategories())) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(atomicEvent.getProductOfferingId()), atomicEvent.getCategories());
			projectEvents.add(event);
		} else if (event instanceof BundleProductOfferingCategoryDefinedEvent bundleEvent && !ObjectUtils.isEmpty(bundleEvent.getCategories())) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(bundleEvent.getProductOfferingId()), bundleEvent.getCategories());
			projectEvents.add(event);
		} else if (event instanceof ContractProductOfferingCategoryDefinedEvent contractEvent && !ObjectUtils.isEmpty(contractEvent.getCategories())) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(contractEvent.getProductOfferingId()), contractEvent.getCategories());
			projectEvents.add(event);
		}
	}

	private void processRelationshipEvent(Event event, List<Event> projectEvents) {
		if (event instanceof AtomicProductOfferingRelationshipDefinedEvent atomicEvent) {
			handleIncompatibleRelationships(atomicEvent.getProductOfferingRelationships(),
					AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class);
			projectEvents.add(event);
		} else if (event instanceof BundleProductOfferingRelationshipDefinedEvent bundleEvent) {
			handleIncompatibleRelationships(bundleEvent.getProductOfferingRelationships(),
					BundleProductOfferingIncompatibleRelationshipModifiedEvent.class);
			projectEvents.add(event);
		} else if (event instanceof ContractProductOfferingRelationshipDefinedEvent contractEvent) {
			handleIncompatibleRelationships(contractEvent.getProductOfferingRelationships(),
					ContractProductOfferingIncompatibleRelationshipModifiedEvent.class);
			projectEvents.add(event);
		}
	}



	private void handleIncompatibleRelationships(List<ProductOfferingRelationship> relationships, Class<?> eventType) {
		for (ProductOfferingRelationship relationship : relationships) {
			if (relationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)) {
				projectProductofferingEvents(relationship.getId(), eventType.getSimpleName());
			}
		}
	}


	private Set<ProductOfferingRef> convertToProductOfferingRef(String productOfferingId) {
		HashSet<ProductOfferingRef> productOfferings=new HashSet<>();
		ProductOffering productOffering=queryService.fetchProductOfferingById(productOfferingId, null);
		 productOfferings.add(new ProductOfferingRef().id(productOffering.getId()).
				type(productOffering.getType().getValue()).baseType(productOffering.getBaseType()).name(productOffering.getName()).
				schemaLocation(productOffering.getSchemaLocation()));
		 return productOfferings;
	}

	private void triggerCategoryModificationEvents(Set<ProductOfferingRef> productOfferings, Set<CategoryRef> categories) {
		List<Event> categoryModificationEvents=new ArrayList<>();
		for(CategoryRef categoryRef:categories){
			categoryModificationEvents.add(new CategoryProductOfferingAssociationEvent(categoryRef.getId(),productOfferings,true));
		}
		publisher.project(categoryModificationEvents);
	}

	private void projectProductofferingEvents(String aggregateId, String eventName) {
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();
		List<DomainEventMessage<Event>> eventmsg = mongoEventStoreImpl.readEventsBackword(aggregateId, 0);
		for (int i = 0; i < eventmsg.size(); i++) {
			Event event = eventmsg.get(i).getPayload();
			LOGGER.debug("Events to publish - {}", event.getClass().getName());
			if (duplicateCheck.add(event.getClass().getName())
					&& event.getClass().getSimpleName().contains(eventName)) {
				project.add(event);
				break;
			}
		}
		publisher.project(project);
	}

	@Override
	public void defineProductOfferingOperation(String productOfferingId, List<CommercialOperation> operationSpecifications) {
		LOGGER.debug("Triggerring ProductOfferingOperationCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ProductOfferingOperationCommand(productOfferingId, operationSpecifications));

	}

	@Override
	public void defineSelectAllowedAction(String productOfferingId, List<AllowedProductAction> alloweddto) {
		LOGGER.debug("Triggerring ProductOfferingSelectAllowedActionCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ProductOfferingSelectAllowedActionCommand(productOfferingId, alloweddto));

	}

	@Override
	public void associatePOPtoOperationSpecification(List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList,
			String productOfferingId) {
		LOGGER.debug("Triggerring AssociatePOPtoOperationSpecificationCommand with aggregateId : {}",
				productOfferingId);
		commandGateway.sendAndWait(
				new AssociatePOPtoOperationSpecificationCommand(productOfferingId, associatePOPtoOperationSpecList));

	}

	@Override
	public void cancelProductOff(String productOffId) {
		LOGGER.debug("Triggerring ProductOffCancelCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ProductOffCancelCommand(productOffId));

	}

	@Override
	public void defineBundledProductOfferingOperation(String productOffId,
			List<CommercialOperation> operationSpecifications) {

		LOGGER.debug("Triggerring BundledProductOfferingOperationCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new BundledProductOfferingOperationCommand(productOffId, operationSpecifications));

	}

	@Override
	public void defineBundleProductOfferings(String productOffId, List<BundledProductOffering> productOfferBundlings,
			int globalMinCardinality, int globalMaxCardinality) {

		LOGGER.debug("Triggerring ManageProductOfferingBundlingCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ManageProductOfferingBundlingCommand(productOffId, productOfferBundlings,
				globalMinCardinality, globalMaxCardinality));

	}

	@Override
	public void deleteProductOffering(Long interval, OffsetDateTime delDate, String intervalUnit) {
		LOGGER.debug("Triggering ProductOfferingDeleteCommand");
		commandGateway.sendAndWait(
				new ProductOfferingDeleteCommand(UUID.randomUUID().toString(), delDate, interval, intervalUnit));

	}


	@Override
	public void atomicProductOfferingCategoryModifyByCategoryDeletion(String productOfferingId,
			Set<CategoryRef> addCategories, Set<CategoryRef> delCategories, OffsetDateTime lastUpdate) {
		LOGGER.debug(
				"Triggerring AtomicProductOfferingCategoryModifyByCategoryDeletionCommand with productOfferingId : {}",
				productOfferingId);
		commandGateway.sendAndWait(new AtomicProductOfferingCategoryModifyByCategoryDeletionCommand(productOfferingId,
				addCategories, delCategories, lastUpdate));

		List<DomainEventMessage<Event>> history = mongoEventStoreImpl.readEventsBackword(productOfferingId, 0);
		if (!(history == null || history.isEmpty())) {
			Event event = history.get(0).getPayload();
			if (event instanceof AtomicProductOfferingCategoryModifiedEvent) {
				publisher.project(List.of(event));
			}
		}
	}

	@Override
	public void processPolicyEvent(com.orange.discobole.productcatalog.productoffering.dto.policyrule.Event event) {
		PolicyOfferingEvent policyOfferingEvent = objectMapper.convertValue(event.getEvent(), PolicyOfferingEvent.class);
		String policyRuleId =  policyOfferingEvent.getPolciyRuleId();
		List<ProductOffering> productOfferings = policyOfferingEvent.getProductOfferingList();
		for(ProductOffering productOffering: productOfferings){
			// only lifecycle status can be modified in the launched state
			if(productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.LAUNCHED.getValue()) ||
					productOffering.getLifecycleStatus().getValue().equals(ProductOfferingLifecycle.RETIRED.getValue())){
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE, "you can not modify entity with launched or retired status", "");
			}
			List<PolicyRuleRef> policyRuleRefs = productOffering.getPolicyRuleRef();
			if (!ObjectUtils.isEmpty(policyRuleRefs)) {
				policyRuleRefs.removeIf(ref -> policyRuleId.equals(ref.getId()));
			}
			defineProductOfferingPolicyRuleAssociation(productOffering.getId(), policyRuleRefs);
		}
	}


}

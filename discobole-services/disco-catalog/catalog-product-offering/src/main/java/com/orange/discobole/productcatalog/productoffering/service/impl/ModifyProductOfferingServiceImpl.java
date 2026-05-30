// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.orange.discobole.productcatalog.productoffering.command.productoffering.ProductOfferingSelectAllowedActionCommand;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import jakarta.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.command.productoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.category.CategoryProductOfferingAssociationEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingIncompatibleRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.ProductOfferingModificationInitiatedEvent;
import com.orange.discobole.productcatalog.productoffering.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.DefineContractIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;

/**
 * Implementation to process Product Offering Commands
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Service
public class ModifyProductOfferingServiceImpl implements ModifyProductOfferingService {


	MongoEventStoreImpl mongoEventStoreImpl;

	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingServiceImpl.class);

	private final CommandGateway commandGateway;

	@Resource
	private Publisher publisher;

	@Resource
	private QueryService queryService;
	@Autowired
	public ModifyProductOfferingServiceImpl(CommandGateway commandGateway,MongoEventStoreImpl mongoEventStoreImpl) {
		this.commandGateway = commandGateway;
		this.mongoEventStoreImpl = mongoEventStoreImpl;

	}

	@Override
	public void cancelProductOfferingModification(String productOffId) {

		LOGGER.debug("Triggering ProductOfferingCancelCommand");
		commandGateway.sendAndWait(new ProductOfferingModificationCancelCommand(productOffId));

	}

	@Override
	public void initiatePOModification(String poId) {
		try {
			LOGGER.debug("Triggerring SelectProductOfferingTypeCommand with aggregateId : {}", poId);
			commandGateway.sendAndWait(new POModificationCommand(poId));
		} catch (AggregateNotFoundException ex) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_STATUS_NOT_MODIFIED);
		}
	}

	@Override
	public void modifyProductOffDesc(String productOffId, DefineIdentityData defineIdentityData, Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
									 Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason,ProductOfferingLifecycle lifecycleStatus) {
		LOGGER.debug("Triggerring ProductOfferingDescriptionCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ModifyProductOfferingIdentityDataCommand(productOffId, defineIdentityData,
				channelIds, marketSegmentIds, relatedParties, poTerms, validity, type, statusReason, lifecycleStatus));
	}
	@Override
	public void modifyContractProductOffDesc(String productOffId, DefineContractIdentityData defineData,
											 Set<String> channelIds, Set<String> marketSegmentIds, Set<RelatedParty> relatedParties,
											 Set<ProductOfferingTerm> poTerms, TimePeriod validity, ProductOfferingType type, String statusReason,
											 OffsetDateTime lastUpdate,ProductOfferingLifecycle lifecycleStatus) {
		LOGGER.debug("Triggerring processContractProductOfferingDescriptionCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ModifyContractProductOfferingDescriptionCommand(productOffId, defineData,
				channelIds, marketSegmentIds, relatedParties, poTerms, validity, type, statusReason, lastUpdate, lifecycleStatus));
	}

	@Override
	public void modifyProductOfferingCategory(String productOffId, List<String> categories) {

		LOGGER.debug("Triggering ModifyProductOfferingCategory");
		commandGateway.sendAndWait(new ModifyProductOfferingCategoryCommand(productOffId, categories, true));
	}


	@Override
	public void modifyProductOfferingOperation(String productOffId, List<CommercialOperation> operationSpecifications) {

		LOGGER.debug("Triggering ModifyProductOfferingOperations");
		commandGateway.sendAndWait(new ModifyProductOfferingOperationCommand(productOffId, operationSpecifications));

	}

	@Override
	public void modifyAssociatePOPtoOperationSpecification(
			List<AssociatePOPtoOperationSpec> associatePOPtoOperationSpecList, String productOffId) {
		LOGGER.debug("Triggering ModifyPOAssociatePOPtoOperationSpecification");
		commandGateway.sendAndWait(
				new ModifyAssociatePOPtoOperationSpecificationCommand(productOffId, associatePOPtoOperationSpecList));

	}


	@Override
	public void modifyProductOfferingCharacteristics(String productOffId,
			List<PickAtomicProductOfferingCharacteristic> pickProductOfferingCharacteristic) {

		LOGGER.debug("Triggering ModifyPOCharacteristics");
		commandGateway.sendAndWait(
				new ModifyProductOfferingCharacteristicCommand(productOffId, pickProductOfferingCharacteristic));

	}

	@Override
	public void modifyProductOfferingPolicyRuleAssociation(String productOffId, List<PolicyRuleRef> policyRules) {
		LOGGER.debug("Triggerring ModifyProductOfferingPolicyRuleAssociation with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ModifyProductOfferingPolicyRuleAssociationCommand(productOffId, policyRules));
	}


	@Override
	public void modifyProductOfferingRelationship(String productOffId, List<ProductOfferingRelationship> relationships) {
		commandGateway
				.sendAndWait(new ModifyProductOfferingRelationshipCommand(productOffId, relationships));

	}



	@Override
	public void validateProductOfferingModification(String productOfferingId,String versionType) {
		LOGGER.debug("Triggerring SelectProductOfferingTypeCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new AtomicProductOfferingModifiedValidatedCommand(productOfferingId));
		List<Event> project = new ArrayList<>();
		Set<String> duplicateCheck = new HashSet<>();
		AtomicProductOfferingCategoryModifiedEvent atomicCategoryEvent = null;
		BundleProductOfferingCategoryModifiedEvent bundleCategoryEvent = null;
		ContractProductOfferingCategoryModifiedEvent contractCategoryEvent = null;
		AtomicProductOfferingRelationshipModifiedEvent atomicRelEvent = null;
		BundleProductOfferingRelationshipModifiedEvent bundleRelEvent = null;
		ContractProductOfferingRelationshipModifiedEvent contractRelEvent = null;
		for (DomainEventMessage<Event> eventmsg : mongoEventStoreImpl.readEventsBackword(productOfferingId, 0)) {
			LOGGER.debug("Events to publish - {}", eventmsg.getClass().getName());
			Event event = eventmsg.getPayload();
			if (!duplicateCheck.add(eventmsg.getPayloadType().getSimpleName())) continue;
			if (event instanceof ProductOfferingModificationInitiatedEvent) break;
			if (event instanceof AtomicProductOfferingCategoryModifiedEvent) atomicCategoryEvent = (AtomicProductOfferingCategoryModifiedEvent) event;
			else if (event instanceof BundleProductOfferingCategoryModifiedEvent) bundleCategoryEvent = (BundleProductOfferingCategoryModifiedEvent) event;
			else if (event instanceof ContractProductOfferingCategoryModifiedEvent) contractCategoryEvent = (ContractProductOfferingCategoryModifiedEvent) event;
			else if (event instanceof AtomicProductOfferingRelationshipModifiedEvent) atomicRelEvent = (AtomicProductOfferingRelationshipModifiedEvent) event;
			else if (event instanceof BundleProductOfferingRelationshipModifiedEvent) bundleRelEvent = (BundleProductOfferingRelationshipModifiedEvent) event;
			else if (event instanceof ContractProductOfferingRelationshipModifiedEvent) contractRelEvent = (ContractProductOfferingRelationshipModifiedEvent) event;

			project.add(event);
		}
		Collections.reverse(project);
		LOGGER.debug("Triggering ProductOfferingModificationValidatedCommand");
		publisher.project(project);

		if (atomicCategoryEvent != null) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(atomicCategoryEvent.getProductOfferingId()),
					atomicCategoryEvent.getAddCategories(),
					atomicCategoryEvent.getDelCategories()
			);
		} else if (bundleCategoryEvent != null) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(bundleCategoryEvent.getProductOfferingId()),
					bundleCategoryEvent.getAddCategories(),
					bundleCategoryEvent.getDelCategories()
			);
		} else if (contractCategoryEvent != null) {
			triggerCategoryModificationEvents(
					convertToProductOfferingRef(contractCategoryEvent.getProductOfferingId()),
					contractCategoryEvent.getAddCategories(),
					contractCategoryEvent.getDelCategories()
			);
		}
		// Handle relationship modification events
		processRelationshipEvents(atomicRelEvent, bundleRelEvent, contractRelEvent);
	}

	private void processRelationshipEvents(
			AtomicProductOfferingRelationshipModifiedEvent atomicEvent,
			BundleProductOfferingRelationshipModifiedEvent bundleEvent,
			ContractProductOfferingRelationshipModifiedEvent contractEvent) {

		if (atomicEvent != null) {
			handleAtomicRelationshipEvents(atomicEvent);
		}
		if (bundleEvent != null) {
			handleBundleRelationshipEvents(bundleEvent);
		}
		if (contractEvent != null) {
			handleContractRelationshipEvents(contractEvent);
		}
	}

	private void handleAtomicRelationshipEvents(AtomicProductOfferingRelationshipModifiedEvent event) {
		String eventType = AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class.getSimpleName();
		for (ProductOfferingRelationship relationship : event.getDeleteProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
		}
		for (ProductOfferingRelationship relationship : event.getAddProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
		}
	}

	private void handleBundleRelationshipEvents(BundleProductOfferingRelationshipModifiedEvent event) {
		String eventType = BundleProductOfferingIncompatibleRelationshipModifiedEvent.class.getSimpleName();
		for (ProductOfferingRelationship relationship : event.getDeleteProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
		}
		for (ProductOfferingRelationship relationship : event.getAddProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
		}
	}

	private void handleContractRelationshipEvents(ContractProductOfferingRelationshipModifiedEvent event) {
		String eventType = ContractProductOfferingIncompatibleRelationshipModifiedEvent.class.getSimpleName();
		for (ProductOfferingRelationship relationship : event.getDeleteProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
		}
		for (ProductOfferingRelationship relationship : event.getAddProductOfferingRelationships()) {
			projectEvents(relationship.getId(), eventType);
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
	private void triggerCategoryModificationEvents(Set<ProductOfferingRef> productOfferings, Set<CategoryRef> addCategories,Set<CategoryRef> delCategories) {
		List<Event> categoryModificationEvents=new ArrayList<>();
		for(CategoryRef categoryRef:addCategories){
			categoryModificationEvents.add(new CategoryProductOfferingAssociationEvent(categoryRef.getId(),productOfferings,true));
		}
		for(CategoryRef categoryRef:delCategories){
			categoryModificationEvents.add(new CategoryProductOfferingAssociationEvent(categoryRef.getId(),productOfferings,false));
		}
		publisher.project(categoryModificationEvents);
	}
	private void projectEvents(String aggregateId, String eventName) {
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
	public void callPOAggregateForModification(String id, Set<CategoryRef> categories, boolean addition,String productOfferingType) {
		if (addition) {
			LOGGER.info("ModifyAdditionProductOfferingCategoryCommand command to add categories");
			commandGateway.sendAndWait(new ModifyAdditionProductOfferingCategoryCommand(id, categories,productOfferingType));
		} else {
			LOGGER.info("ModifyAdditionProductOfferingCategoryCommand command to delete categories");
			commandGateway.sendAndWait(new ModifyDeletionProductOfferingCategoryCommand(id, categories,productOfferingType));
		}
	}

	@Override
	public void modifyBundledProductOfferingOperation(String productOffId,
													  List<CommercialOperation> operationSpecifications) {

		LOGGER.debug("Triggerring BundledProductOfferingOperationCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ModifyBundledProductOfferingOperationCommand(productOffId, operationSpecifications));

	}

	@Override
	public void modifyBundleProductOfferings(String productOffId, List<BundledProductOffering> productOfferBundlings,
											 int globalMinCardinality, int globalMaxCardinality) {

		LOGGER.debug("Triggerring ManageProductOfferingBundlingCommand with aggregateId : {}", productOffId);
		commandGateway.sendAndWait(new ModifyManageProductOfferingBundlingCommand(productOffId, productOfferBundlings,
				globalMinCardinality, globalMaxCardinality));

	}

	@Override
	public void modifySelectAllowedAction(String productOfferingId, List<AllowedProductAction> alloweddto) {
		LOGGER.debug("Triggerring ModifyProductOfferingSelectAllowedActionCommand with aggregateId : {}", productOfferingId);
		commandGateway.sendAndWait(new ModifyProductOfferingSelectAllowedActionCommand(productOfferingId, alloweddto));

	}







}

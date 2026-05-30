// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.saga;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import com.orange.discobole.productcatalog.productoffering.command.productoffering.modify.ModifyProductOfferingIncompatibleRelationshipCommand;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationship;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingRelationshipType;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingType;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingRelationshipModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.AtomicProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingRelationshipModifiedEvent;

@Saga
public class ProductOfferingSaga {
	private static final String PRODUCT_OFFERING_ID = "productOfferingId";
	@Autowired
	private transient CommandGateway commandGateway;





	    @StartSaga
	    @SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	    public void handle( AtomicProductOfferingRelationshipDefinedEvent event) {
	    	List<String> incompatiblePORelationshipIds=event.getProductOfferingRelationships().stream().filter(poRelationship->
	    	poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
	    	map(ProductOfferingRelationship::getId).toList();
	    	for (String incompatiblepoRelationshipId : incompatiblePORelationshipIds) {
				SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
	    		commandGateway.send(new ModifyProductOfferingIncompatibleRelationshipCommand
                        (incompatiblepoRelationshipId, event.getProductOfferingId(), true,ProductOfferingType.ATOMICPRODUCTOFFERING));
			}

	    }
	@StartSaga
	@SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	public void handle(BundleProductOfferingRelationshipDefinedEvent event) {
		List<String> incompatiblePORelationshipIds=event.getProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).toList();
		for (String incompatiblepoRelationshipId : incompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.send(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId, event.getProductOfferingId(), true, ProductOfferingType.BUNDLEPRODUCTOFFERING));
		}

	}
	@StartSaga
	@SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	public void handle(ContractProductOfferingRelationshipDefinedEvent event) {
		List<String> incompatiblePORelationshipIds=event.getProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).toList();
		for (String incompatiblepoRelationshipId : incompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.send(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId, event.getProductOfferingId(), true,ProductOfferingType.CONTRACT));
		}

	}
		@StartSaga
	    @SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	    public void handle( AtomicProductOfferingRelationshipModifiedEvent event) {
	    	Set<String> addIncompatiblePORelationshipIds=event.getAddProductOfferingRelationships().stream().filter(poRelationship->
	    	poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
	    	map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
	    	Set<String> deleteIncompatiblePORelationshipIds=event.getDeleteProductOfferingRelationships().stream().filter(poRelationship->
	    	poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
	    	map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
	    	for (String incompatiblepoRelationshipId : addIncompatiblePORelationshipIds) {
				SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
	    		commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
	    				(incompatiblepoRelationshipId,event.getProductOfferingId(),true, ProductOfferingType.ATOMICPRODUCTOFFERING));
			}
	    	for (String incompatiblepoRelationshipId : deleteIncompatiblePORelationshipIds) {
				SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
	    		commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
	    				(incompatiblepoRelationshipId,event.getProductOfferingId(),false,ProductOfferingType.ATOMICPRODUCTOFFERING));
			}

	    }
	@StartSaga
	@SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	public void handle( BundleProductOfferingRelationshipModifiedEvent event) {
		Set<String> addIncompatiblePORelationshipIds=event.getAddProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
		Set<String> deleteIncompatiblePORelationshipIds=event.getDeleteProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
		for (String incompatiblepoRelationshipId : addIncompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId,event.getProductOfferingId(),true, ProductOfferingType.BUNDLEPRODUCTOFFERING));
		}
		for (String incompatiblepoRelationshipId : deleteIncompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId,event.getProductOfferingId(),false,ProductOfferingType.BUNDLEPRODUCTOFFERING));
		}

	}
	@StartSaga
	@SagaEventHandler(associationProperty = PRODUCT_OFFERING_ID)
	public void handle( ContractProductOfferingRelationshipModifiedEvent event) {
		Set<String> addIncompatiblePORelationshipIds=event.getAddProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
		Set<String> deleteIncompatiblePORelationshipIds=event.getDeleteProductOfferingRelationships().stream().filter(poRelationship->
						poRelationship.getRelationshipType().equals(ProductOfferingRelationshipType.INCOMPATIBLE)).
				map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
		for (String incompatiblepoRelationshipId : addIncompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId,event.getProductOfferingId(),true, ProductOfferingType.CONTRACT));
		}
		for (String incompatiblepoRelationshipId : deleteIncompatiblePORelationshipIds) {
			SagaLifecycle.associateWith(PRODUCT_OFFERING_ID,incompatiblepoRelationshipId);
			commandGateway.sendAndWait(new ModifyProductOfferingIncompatibleRelationshipCommand
					(incompatiblepoRelationshipId,event.getProductOfferingId(),false,ProductOfferingType.CONTRACT));
		}

	}

	}



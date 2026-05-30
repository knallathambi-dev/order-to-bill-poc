// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingIndirectCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.BundleProductOfferingModificationValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingIndirectCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.ContractProductOfferingModificationValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingIndirectCategoryModifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingModificationValidatedEvent;


import jakarta.annotation.Resource;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOfferingEventHandler {

	@Resource
	Publisher publisher;

	@EventHandler
	public void handle(ProductOfferingTypeSelectedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(CreateBundleProductOfferingEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(CreateContractProductOfferingEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingInitiatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingIdentityDataDefinedEvent event) {
		publisher.project(List.of(event));
	}





	@EventHandler
	public void handle(BundleProductOfferingIdentityDataDefinedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingIdentityDataDefinedEvent event) {
		publisher.project(List.of(event));
	}


	@EventHandler
	public void handle(LinkPOPtoOperEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(AtomicProductOfferingOperDefinedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingOperDefinedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingOperDefinedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingCharacteristicsDefinedEvent event) {
		publisher.project(List.of(event));
	}


	@EventHandler
	public void handle(AtomicProductOfferingBundleDefinedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingValidatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingSelectedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingSelectedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingVersionCreatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingValidatedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingValidatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingVersionCreatedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingVersionCreatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingCreationCompletedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ContractProductOfferingCreationCompletedEvent event) {
		publisher.project(List.of(event));
	}
	
	@EventHandler
	public void handle(ProductOffCancelledEvent event) {
		publisher.project(List.of(event));
	}



	@EventHandler
	public void handle(ProductOfferingDeleteEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(BundleProductOfferingModificationValidatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingModificationValidatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ContractProductOfferingModificationValidatedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(AtomicProductOfferingIndirectCategoryModifiedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(BundleProductOfferingIndirectCategoryModifiedEvent event) {
		publisher.project(List.of(event));
	}
	@EventHandler
	public void handle(ContractProductOfferingIndirectCategoryModifiedEvent event) {
		publisher.project(List.of(event));
	}
	
	@EventHandler
	public void handle(ProductOfferingPolicyRuleAssociationDefinedEvent event) {
		publisher.project(List.of(event));
	}

	@EventHandler
	public void handle(ProductOfferingAllowedActionDefinedEvent event) {
		publisher.project(List.of(event));
	}

}
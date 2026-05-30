// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering;


import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingCategoryDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingCreationCompletedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingOperDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingSelectedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.BundleProductOfferingVersionCreatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.CreateBundleProductOfferingEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.InvalidBundleProductOfferingsEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingCategoryDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingCreationCompletedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingIdentityDataDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingOperDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingRelationshipDefinedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingSelectedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.ContractProductOfferingVersionCreatedEvent;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.CreateContractProductOfferingEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.AtomicProductOfferingModificationValidatedEvent;
import com.orange.discobole.productcatalog.productoffering.projection.ProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * The Class Initializer to register event for Product Offering with the
 * Projection.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */

@Component
public class ProductOfferingInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ProductOfferingProjector projector;

	@Resource
	private ProductOfferingService productOfferingService;

	@PostConstruct
	private void init() {
		publisher.register(ProductOfferingTypeSelectedEvent.class, projector::handle);
		publisher.register(CreateBundleProductOfferingEvent.class, projector::handle);
		publisher.register(CreateContractProductOfferingEvent.class, projector::handle);
		publisher.register(ProductSpecSelectedEvent.class, projector::handle);
		publisher.register(ProductSpecStateVerifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingInitiatedEvent.class, projector::handle);
		publisher.register(InvalidProductSpecStatusEvent.class, projector::handle);
		publisher.register(InvalidProductOfferingCategorySelectedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingCategoryDefinedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingCategoryDefinedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingCategoryDefinedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingIdentityDataDefinedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingIdentityDataDefinedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingIdentityDataDefinedEvent.class, projector::handle);




		publisher.register(AtomicProductOfferingBundleDefinedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingValidatedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingValidatedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingValidatedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingVersionCreatedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingVersionCreatedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingVersionCreatedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingCreationCompletedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingCreationCompletedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingCreationCompletedEvent.class, projector::handle);
		publisher.register(ProductOffCancelledEvent.class, projector::handle);
		publisher.register(InvalidProductOffCancelledEvent.class, projector::handle);

		publisher.register(InvalidProductOfferingOperationsSelectedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingOperDefinedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingRelationshipSelectedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingRelationshipDefinedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingRelationshipDefinedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingRelationshipDefinedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingCharacteristicsDefinedEvent.class,projector::handle);
		publisher.register(InvalidAtomicProductOfferingCharacteristicsSelectedEvent.class, projector::handle);
		publisher.register(LinkPOPtoOperEvent.class, projector::handle);
		publisher.register(InvalidLinkPOPtoOperEvent.class, projector::handle);
		publisher.register(InvalidPopIdSelectedEvent.class, projector::handle);
		publisher.register(InvalidPOPStatusSelectedEvent.class, projector::handle);
		publisher.register(InvalidProductOfferingRelationshipSelectedEvent.class, projector::handle);
		publisher.register(InvalidProductOfferingRelationshipTypeEvent.class, projector::handle);
		publisher.register(BundleProductOfferingOperDefinedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingOperDefinedEvent.class, projector::handle);
		publisher.register(BundleProductOfferingSelectedEvent.class, projector::handle);
		publisher.register(ContractProductOfferingSelectedEvent.class, projector::handle);
		publisher.register(InvalidBundleProductOfferingsEvent.class, projector::handle);
		publisher.register(ProductOfferingDeleteEvent.class,projector::handle);
		publisher.register(ProductOfferingTemporaryDeleteEvent.class,projector::handle);
		publisher.register(ProductOfferingPolicyRuleAssociationDefinedEvent.class,projector::handle);
		publisher.register(ProductOfferingAllowedActionDefinedEvent.class,projector::handle);
		

	}

	/**
	 * This method is used to initiate service calls and triggering commands for
	 * test purpose.
	 *
	 * @param args the string argumentL̥
	 * @throws Exception the exception
	 */
	@Override
	public void run(final String... args) throws Exception {
		// method to initiate service calls and triggering commands for test purpose.
	}

}

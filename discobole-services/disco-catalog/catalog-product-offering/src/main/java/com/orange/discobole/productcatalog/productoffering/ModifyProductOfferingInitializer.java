// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.InvalidProductOfferinglifeCycleStatusSpecifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.projection.ModifyProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;

/**
 * The Class Initializer to register event for Modified Product Offering with
 * the Projection.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Component
public class ModifyProductOfferingInitializer implements CommandLineRunner {

	@Resource
	private Publisher publisher;

	@Resource
	private ModifyProductOfferingProjector projector;

	@Resource
	private ModifyProductOfferingService productOfferingService;

	@PostConstruct
	private void init() {
		publisher.register(InvalidProductOfferinglifeCycleStatusSpecifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingCategoryModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingIdentityDataModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingChannelModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingMarketModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingRelatedPartyModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingOperationModifiedEvent.class, projector::handle);
		publisher.register(LinkPOPtoOperModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingTermModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingCharacteristicsModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingRelationshipModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingValidForModifiedEvent.class, projector::handle);
		publisher.register(AtomicProductOfferingIncompatibleRelationshipModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingIncompatibleRelationshipModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingIncompatibleRelationshipModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingCategoryModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingRelationshipModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingOperModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingSelectedModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingIdentityDataModifiedEvent.class,projector::handle);
		publisher.register(ChildPOInfoModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingModificationValidatedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingCategoryModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingRelationshipModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingOperModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingSelectedModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingIdentityDataModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingModificationValidatedEvent.class,projector::handle);
		publisher.register(AtomicProductOfferingIndirectCategoryModifiedEvent.class,projector::handle);
		publisher.register(BundleProductOfferingIndirectCategoryModifiedEvent.class,projector::handle);
		publisher.register(ContractProductOfferingIndirectCategoryModifiedEvent.class,projector::handle);
		publisher.register(ProductOfferingPolicyRuleAssociationModifiedEvent.class,projector::handle);
		publisher.register(AtomicProductOfferingModificationValidatedEvent.class,projector::handle);
		publisher.register(ModifyProductOfferingAllowedActionDefinedEvent.class,projector::handle);
	}

	/**
	 * This method is used to initiate service calls and triggering commands for
	 * test purpose.
	 *
	 * @param args the string argument
	 * @throws Exception the exception
	 */
	@Override
	public void run(final String... args) throws Exception {
		// method to initiate service calls and triggering commands for test purpose.
	}

}

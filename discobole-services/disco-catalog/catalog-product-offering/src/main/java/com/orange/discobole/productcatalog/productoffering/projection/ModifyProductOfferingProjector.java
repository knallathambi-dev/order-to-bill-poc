// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.projection;


import com.orange.discobole.processflow.event.EventData;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productoffering.constant.OdacaConstants;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.InvalidProductOfferinglifeCycleStatusSpecifiedEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.ProductOffCancelledEvent;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * This class sends registered events on stream.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */
//@EnableBinding(ProductOfferingChannel.class)
@Component
public class ModifyProductOfferingProjector {

	private static final String PRODUCT_OFFERING_OUT = "productOffering-out-0";



	/**
	 * The Constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ModifyProductOfferingProjector.class);

	

	/**
	 * The product offering.
	 */
	@Autowired
	private StreamBridge bridge;

	/**
	 * Handles InvalidProductOfferinglifeCycleStatusSpecifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidProductOfferinglifeCycleStatusSpecifiedEvent event) {
		LOGGER.info("Sending InvalidProductOfferinglifeCycleStatusSpecifiedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED);
	}

	/**
	 * Handles AtomicProductOfferingCategoryModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingCategoryModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingDescribedModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingIdentityDataModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingChannelModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingChannelModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingChannelModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingMarketModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingMarketModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingMarketModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingRelatedPartyModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingRelatedPartyModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingRelatedPartyModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingOperationModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingOperationModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingOperationModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles LinkPOPtoOperModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final LinkPOPtoOperModifiedEvent event) {
		LOGGER.info("LinkPOPtoOperModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProdOffId()).build());
	}

	/**
	 * Handles AtomicProductOfferingTermModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingTermModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingTermModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingCharacteristicsModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingCharacteristicsModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingCharacteristicsModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingRelationshipModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingRelationshipModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingValidForModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingValidForModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingValidForModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffId()).build());
	}

	/**
	 * Handles {@link ProductSpecCancelledEvent}.
	 *
	 * @param event the event
	 */
	public void handle(ProductOffCancelledEvent event) {
		LOGGER.info("ProductOffCancelledEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffering().getId()).build());
	}

	public void handle(final AtomicProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingIncompaRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(final BundleProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.info("BundleProductOfferingIncompatibleRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(final ContractProductOfferingIncompatibleRelationshipModifiedEvent event) {
		LOGGER.info("ContractProductOfferingIncompatibleRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(final ContractProductOfferingCategoryModifiedEvent event) {
		LOGGER.info("ContractProductOfferingCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}


	public void handle(ContractProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("ContractProductOfferingIdentityDataModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(ContractProductOfferingModificationValidatedEvent event) {
		LOGGER.info("ContractProductOfferingModificationValidatedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(ContractProductOfferingOperModifiedEvent event) {
		LOGGER.info("ContractProductOfferingOperModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles ProductOfferingPolicyRuleAssociationModifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPolicyRuleAssociationModifiedEvent event) {
		LOGGER.info("ProductOfferingPolicyRuleAssociationModifiedEvent to catalog");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(ContractProductOfferingRelationshipModifiedEvent event) {
		LOGGER.info("ContractProductOfferingRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(ContractProductOfferingSelectedModifiedEvent event) {
		LOGGER.info("ContractProductOfferingSelectedModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(ChildPOInfoModifiedEvent event) {
		LOGGER.info("ChildPOInfoModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}


	public void handle(BundleProductOfferingCategoryModifiedEvent event) {
		LOGGER.info("BundleProductOfferingCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(BundleProductOfferingIdentityDataModifiedEvent event) {
		LOGGER.info("BundleProductOfferingIdentityDataModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(BundleProductOfferingModificationValidatedEvent event) {
		LOGGER.info("BundleProductOfferingModificationValidatedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(BundleProductOfferingOperModifiedEvent event) {
		LOGGER.info("BundleProductOfferingOperModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(BundleProductOfferingRelationshipModifiedEvent event) {
		LOGGER.info("BundleProductOfferingRelationshipModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(BundleProductOfferingSelectedModifiedEvent event) {
		LOGGER.info("BundleProductOfferingSelectedModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(AtomicProductOfferingIndirectCategoryModifiedEvent event) {
		LOGGER.info("AtomicProductOfferingIndirectCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	public void handle(BundleProductOfferingIndirectCategoryModifiedEvent event) {
		LOGGER.info("BundleProductOfferingIndirectCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	public void handle(ContractProductOfferingIndirectCategoryModifiedEvent event) {
		LOGGER.info("ContractProductOfferingIndirectCategoryModifiedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	public void handle(AtomicProductOfferingModificationValidatedEvent event){
		LOGGER.info("Sending AtomicProductOfferingModificationValidatedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());

	}

	public void handle(ModifyProductOfferingAllowedActionDefinedEvent event){
		LOGGER.info("Sending ModifyProductOfferingAllowedActionDefinedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());

	}

}

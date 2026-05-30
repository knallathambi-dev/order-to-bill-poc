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
import com.orange.discobole.productcatalog.productoffering.dto.InvalidCharacteristics;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * This class sends registered events on stream.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
//@EnableBinding(ProductOfferingChannel.class) commented as it is not required
@Component
public class ProductOfferingProjector {
	private static final String PRODUCT_OFFERING_OUT = "productOffering-out-0";



	/**
	 * The Constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingProjector.class);

	

	/**
	 * The product offering.
	 */

	@Autowired
	private StreamBridge bridge;

	/**
	 * Handles ProductOfferingTypeSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingTypeSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles CreateBundleProductOfferingEvent.
	 *
	 * @param event the event
	 */
	public void handle(final CreateBundleProductOfferingEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	/**
	 * Handles CreateContractProductOfferingEvent.
	 *
	 * @param event the event
	 */
	public void handle(final CreateContractProductOfferingEvent event) {
		LOGGER.info("CreateContractProductOfferingEvent to product offrering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles ProductSpecSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	/**
	 * Handles ProductSpecStateVerifiedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductSpecStateVerifiedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
	}

	/**
	 * Handles ProductOfferingInitiatedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingInitiatedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles InvalidProductSpecStatusEvent.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidProductSpecStatusEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductSpecId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE);
	}

	/**
	 * Handles ProductOfferingCharacteristicsDefinedEvent.
	 *
	 * @param event the event
	 * @author Shreya Sharma
	 */
	public void handle(final AtomicProductOfferingCharacteristicsDefinedEvent event) {
		LOGGER.info("AtomicProductOfferingCharacteristicsDefinedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getproductOfferingId()).build());
	}

	/**
	 * Handles InvalidAtomicProductOfferingCharacteristicsSelectedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final InvalidAtomicProductOfferingCharacteristicsSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		StringBuilder reason = new StringBuilder();
		for (InvalidCharacteristics invalidCharacteristics : event.getCharacteristics()) {
			reason.append(
					" Characteristic id " + invalidCharacteristics.getPickAtomicProductOfferingCharacteristic().getId()
							+ " : " + invalidCharacteristics.getReason() + ", ");
		}
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CHARACTRISTICS,reason.deleteCharAt(reason.length() - 2).toString(),null);

	}

	/**
	 * Handles ProductOfferingDescribedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final AtomicProductOfferingIdentityDataDefinedEvent event) {
		LOGGER.info("AtomicProductOfferingDescribedEvent to catalog channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles BundleProductOfferingDescribedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final BundleProductOfferingIdentityDataDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	/**
	 * Handles ContractProductOfferingDescribedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final ContractProductOfferingIdentityDataDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	

	



	/**
	 * Handles InvalidProductOfferingCategorySelectedEvent.
	 *
	 * @param event the event
	 * @author Piyush Goel
	 */
	public void handle(final InvalidProductOfferingCategorySelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CATEGORY_SELECTED,event.getInvalidCategories().toString(),null);
	}



	



	/**
	 * Handles AtomicProductOfferingCategoryDefinedEvent.
	 *
	 * @param event the event
	 * @author Piyush Goel
	 */
	public void handle(final AtomicProductOfferingCategoryDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles BundleProductOfferingCategoryDefinedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final BundleProductOfferingCategoryDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	/**
	 * Handles ContractProductOfferingCategoryDefinedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final ContractProductOfferingCategoryDefinedEvent event) {
		LOGGER.info("Sending ContractProductOfferingCategoryDefinedEvent to catalogProductOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}







	/**
	 * Handles AtomicProductOfferingRelationshipSelectedEvent.
	 *
	 * @param event the event *
	 */
	public void handle(final AtomicProductOfferingRelationshipSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingRelationshipDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingRelationshipDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles ProductOfferingPolicyRuleAssociationDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ProductOfferingPolicyRuleAssociationDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles BundleProductOfferingRelationshipDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingRelationshipDefinedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	/**
	 * Handles ContractProductOfferingRelationshipDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingRelationshipDefinedEvent event) {
		LOGGER.info("Sending ContractProductOfferingRelationshipDefinedEvent to ProductOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles InvalidProductOfferingRelationshipSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidProductOfferingRelationshipSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED_BPO_SAME);
	}

	/**
	 * Handles InvalidProductOfferingRelationshipTypeEvent.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidProductOfferingRelationshipTypeEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getInvalidProductOfferingRelationships()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATION);
	}

	/**
	 * Handles AtomicProductOfferingBundleDefinedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final AtomicProductOfferingBundleDefinedEvent event) {
		LOGGER.info("handle AtomicProductOfferingBundleDefinedEvent");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingValidatedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final AtomicProductOfferingValidatedEvent event) {
		LOGGER.info("handle AtomicProductOfferingValidatedEvent");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles BundleProductOfferingValidatedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final BundleProductOfferingValidatedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles ContractProductOfferingValidatedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final ContractProductOfferingValidatedEvent event) {
		LOGGER.info("Sending ContractProductOfferingTermDefinedEvent to ProductOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	

	/**
	 * Handles AtomicProductOfferingVersionCreatedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final AtomicProductOfferingVersionCreatedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles BundleProductOfferingVersionCreatedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final BundleProductOfferingVersionCreatedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handles ContractProductOfferingVersionCreatedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final ContractProductOfferingVersionCreatedEvent event) {
		LOGGER.info("Sending ContractProductOfferingVersionCreatedEvent to ProductOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handles AtomicProductOfferingCreationCompletedEvent.
	 *
	 * @param event the event
	 * @author Diksha Srivastava
	 */
	public void handle(final AtomicProductOfferingCreationCompletedEvent event) {
		LOGGER.info("handle ContractProductOfferingVersionCreatedEvent");
	}
	
	/**
	 * Handles BundleProductOfferingCreationCompletedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final BundleProductOfferingCreationCompletedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffering().getId()).build());
	}
	/**
	 * Handles ContractProductOfferingCreationCompletedEvent.
	 *
	 * @param event the event
	 * @author Vishal Vachaspati
	 */
	public void handle(final ContractProductOfferingCreationCompletedEvent event) {
		LOGGER.info("Sending ContractProductOfferingCreationCompletedEvent to ProductOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffering().getId()).build());
	}


	/**
	 * Handles {@link ProductSpecCancelledEvent}.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(ProductOffCancelledEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffering().getId()).build());
	}

	/**
	 * Handles {@link InvalidProductOffCancelledEvent}.
	 *
	 * @author Diksha Srivastava
	 * @param event the event
	 */
	public void handle(InvalidProductOffCancelledEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOffId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE_INSTUDY);
	}

	/**
	 * Handles invalid Product Offering operations selected event.
	 *
	 * @param event the event
	 * @author Varshika Choudhary
	 */
	public void handle(final InvalidProductOfferingOperationsSelectedEvent event) {
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_OPERATION,event.getInvalidOpsSelected().toString(),null);
	}
	

	/**
	 * Handle.
	 *
	 * @param event the event
	 *  @author Jagriti Pahwa
	 */
	public void handle(final ProductOfferingAllowedActionDefinedEvent event) {
		LOGGER.info("Sending ProductOfferingAllowedActionDefinedEvent to productOffering ");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final AtomicProductOfferingOperDefinedEvent event) {
		LOGGER.info("Sending ProductOfferingOperDefinedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final LinkPOPtoOperEvent event) {
		LOGGER.info("Sending LinkPOPtoOperEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProdOffId()).build());

	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidLinkPOPtoOperEvent event) {
		LOGGER.info("Sending InvalidLinkPOPtoOperEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_OPERATION_ID);
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidPopIdSelectedEvent event) {
		LOGGER.info("Sending InvalidPopIdSelectedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProdOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_ID);
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidPOPStatusSelectedEvent event) {
		LOGGER.info("Sending InvalidPOPStatusSelectedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_STATUS);
	}
	
	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingOperDefinedEvent event) {
		LOGGER.info("Sending BundleProductOfferingOperDefinedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	/**
	 * Handle ContractProductOfferingOperDefinedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingOperDefinedEvent event) {
		LOGGER.info("Sending ContractProductOfferingOperDefinedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final BundleProductOfferingSelectedEvent event) {
		LOGGER.info("Sending BundleProductOfferingSelectedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * HandleContractProductOfferingSelectedEvent.
	 *
	 * @param event the event
	 */
	public void handle(final ContractProductOfferingSelectedEvent event) {
		LOGGER.info("Sending ContractProductOfferingSelectedEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
	}
	
	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(final InvalidBundleProductOfferingsEvent event) {
		LOGGER.info("Sending InvalidBundleProductOfferingsEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getProductOfferingId()).build());
		throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BPO_NO_CHILD_ASSOCIATION);
	}
	
	public void handle(ProductOfferingDeleteEvent event){
		LOGGER.info("Sending ProductOfferingDeleteEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getAggregateId()).build());
	
	}
	public void handle(ProductOfferingTemporaryDeleteEvent event){
		LOGGER.info("Sending ProductOfferingTemporaryDeleteEvent to productOffering channel");
		bridge.send(PRODUCT_OFFERING_OUT, MessageBuilder.withPayload(EventData.from(event))
				.setHeader(OdacaConstants.PARTITION_KEY, event.getAggregateId()).build());
	
	}

}

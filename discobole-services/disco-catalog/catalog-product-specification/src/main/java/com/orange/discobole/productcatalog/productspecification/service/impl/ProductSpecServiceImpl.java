// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productspecification.command.productspec.*;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.eventstore.MongoEventStoreImpl;
import com.orange.discobole.productcatalog.productspecification.pojo.IdentityData;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This service implementation performs the operations for product
 * specification.
 *
 * @author Piyush Goel
 * @since 1.0
 */
@Service
public class ProductSpecServiceImpl implements ProductSpecService {

	private static final Logger LOGGER = LogManager.getLogger(ProductSpecServiceImpl.class);

	@Resource
	private Publisher publisher;

	@Resource
	private QueryService queryService;
	
	private final CommandGateway commandGateway;
	
	@Resource
	private ApplicationContext appCtx;
	
	@Resource
	private MongoEventStoreImpl mongoEventStoreImpl;

	@Autowired
	public ProductSpecServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
		
	}

	/**
	 * This method performs the operation to initiate the creation of product
	 * specification by CFS.
	 *
	 * @param serviceSpecId service specification id
	 * @return
	 */
	@Override
	public String initiateProductSpecCreation(final String serviceSpecId) {
		String productSpecId = UUID.randomUUID().toString();
		LOGGER.debug("Triggerring InitiateProductSpecCommand with productSpecId : {}",productSpecId);
		return commandGateway.sendAndWait(new InitiateProductSpecCommand(productSpecId,serviceSpecId));
	}

	/**
	 * This method performs the operation to initiate the creation of product
	 * specification by stock.
	 *
	 * @author Varshika Choudhary
	 * @param stockItemId stock item id
	 * @return
	 */
	@Override
	public String initiateStockItemProductSpecCreation(final String stockItemId) {
		String productSpecId = UUID.randomUUID().toString();
		LOGGER.debug("Triggerring InitiateStockItemProductSpecCommand with productSpecId : {}",productSpecId);
		return commandGateway.sendAndWait(new InitiateStockItemProductSpecCommand(productSpecId,stockItemId));
	}

	/**
	 * This method performs the operation to describe the product specification and 
	 *  the operation to update operation of the product specification.
	 *
	 * @param productSpecId      product specification id
	 * @param productDescription
	 * @param productName
	 */
	@Override
	public void initiateProductSpecDef(String productSpecId, IdentityData defineIdentityData,
			List<RelatedParty> relParty, List<RelatedResource> relResource,
			TimePeriod timePeriod, EntityType type) {
		LOGGER.debug("Triggering DefineIdentityProductSpecCommand with productSpecId : {}",productSpecId);
	    commandGateway.sendAndWait(new DefineIdentityProductSpecCommand(productSpecId,defineIdentityData, relParty, relResource,timePeriod, type));

		LOGGER.debug("Triggering ProductSpecOperationCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ProductSpecOperationCommand(productSpecId, null));

	}

	/**
	 * This method performs the operation to update characteristics of the product specification.
	 *
	 * @param productSpecId      product specification id
	 * @param productSpecificationCharacteristics
	 */
	@Override
	public void updateProductSpecCharacteristics(final String productSpecId,
			final List<ProductSpecificationCharacteristic> productSpecificationCharacteristics,List<UsageSpecification> usageSpecifications) {
		LOGGER.debug("Triggering SelectProductSpecCharacteristicCommand with productSpecId : {}",productSpecId);
		commandGateway.sendAndWait(new SelectProductSpecCharacteristicCommand(productSpecId,productSpecificationCharacteristics,usageSpecifications));
	}

	/**
	 * This method performs the operation to update StockItemCharcateristics of the product specification.
	 *
	 * @param productSpecId      product specification id
	 * @param productDescription
	 * @param productName
	 */
	@Override
	public void updateStockItemProductSpecCharacteristics(final String productSpecId,
												 final List<ProductSpecificationCharacteristic> stockItemCharacteristics) {
	
		 LOGGER.debug("Triggering SelectStockItemProductSpecCharacteristicCommand with productSpecId : {}", productSpecId);
		 commandGateway.sendAndWait(new SelectStockItemProductSpecCharacteristicCommand(productSpecId,stockItemCharacteristics));
		 LOGGER.debug("Triggering ComputeProductConfigurationCommand with productSpecId : {}", productSpecId);
		 commandGateway.sendAndWait(new ComputeProductConfigurationCommand(productSpecId));
		 LOGGER.debug("Triggering AssociateProductSpecificationToStockItemCommand with productSpecId : {}", productSpecId);
		 commandGateway.sendAndWait(new AssociateProductSpecificationToStockItemCommand(productSpecId));
	}

	/**
	 * This method performs the operation to update the product specification relationships. 
	 * 
	 * @param productSpecId
	 * @param productSpecificationRelationships
	 */
	@Override
	public void updateProductSpecRel(final String productSpecId,
			final List<ProductSpecificationRelationship> productSpecificationRelationships,
			List<PolicyRuleRef> policyRules) {
		LOGGER.debug("Triggering ProductSpecRelCommand with productSpecId : {}", productSpecId);
		commandGateway
				.sendAndWait(new ProductSpecRelCommand(productSpecId, productSpecificationRelationships, policyRules));
	}

	/**
	 * This method performs the operation to validate the product specification.
	 * 
	 * @param productSpecId
	 */
	@Override
	public void validateProductSpecification(final String productSpecId) {
		LOGGER.debug("Triggering ProductSpecValidatedCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ProductSpecValidatedCommand(productSpecId));
	}

	/**
	 * This method will cancel the PS creation process.
	 * 
	 * @param productSpecId
	 */
	@Override
	public void cancelProductSpec(String productSpecId) {
		LOGGER.debug("Triggering ProductSpecCancelCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ProductSpecCancelCommand(productSpecId));
	}

	/**
	 * This method performs the operation to initiate the product specification modification.
	 * 
	 * @param productSpecId
	 */
	@Override
	public void initiateProductSpecModification(String productSpecId) {
		try {
			LOGGER.debug("Triggering ProductSpecModificationCommand with productSpecId : {}", productSpecId);
			commandGateway.sendAndWait(new ProductSpecModificationCommand(productSpecId));
		} catch (AggregateNotFoundException ex) {
			throw new DiscoManagedClientException(ProductSpecConstants.DISCO_PS_INVALID_STATUS_NOT_MODIFIED);
		}
		
	}

	/**
	 * This method performs the operation to describe the product specification modification.
	 * 
	 * @param productSpecId
	 * @param brand
	 * @param productNumber
	 * @param productDescription
	 * @param productName
	 * @param type
	 */
	@Override
	public void modifyProductSpecDefineIdentity(String productSpecId, IdentityData defineIdentityData, List<RelatedParty> relParty,
			List<RelatedResource> relResource, TimePeriod timePeriod, EntityType type , ProductSpecificationLifecycle lifecycle ) {
		LOGGER.debug("Triggering ModifyProductSpecDescribeCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyDefineIdentityProductSpecCommand(productSpecId, defineIdentityData,relParty,relResource,timePeriod, type ,lifecycle));
	}

	/**
	 * This method will modify characteristics of the product specification.
	 * 
	 * @param productSpecId
	 * @param productSpecCharacteristics
	 */
	@Override
	public void modifyProductSpecCharacteristics(String productSpecId,
			List<ProductSpecificationCharacteristic> productSpecCharacteristics,List<UsageSpecification> usageSpecifications) {
		LOGGER.debug("Triggering ModifyProductSpecCharacteristicCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyProductSpecCharacteristicCommand(productSpecId,productSpecCharacteristics,usageSpecifications));
	}


	/**
	 * This method will modify relationship of the product specification.
	 * 
	 * @param productSpecId
	 * @param productSpecRelationships
	 */
	@Override
	public void modifyProductSpecRel(String productSpecId,
			List<ProductSpecificationRelationship> productSpecRelationships, List<PolicyRuleRef> policyRules) {
		LOGGER.debug("Triggering ModifyProductSpecRelCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyProductSpecRelCommand(productSpecId, productSpecRelationships, policyRules));
	}

	/**
	 * This method will validate the product specification modification and publish all events of PS modification process.
	 * 
	 * @param productSpecId
	 */
	@Override
	public void validateProductSpecificationModification(String productSpecId) {
		LOGGER.debug("Triggering ProductSpecModificationValidatedCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ProductSpecModificationValidatedCommand(productSpecId));

	}

	/**
	 * This method will cancel the product specification modification process.
	 * 
	 * @param productSpecId
	 */
	@Override
	public void cancelProductSpecModification(String productSpecId) {
		LOGGER.debug("Triggering ProductSpecCancelModificationCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ProductSpecCancelModificationCommand(productSpecId));
	}

	@Override
	public String deleteProductSpecification(OffsetDateTime delDate,Long interval,String intervalUnit) {
		String productSpecId = UUID.randomUUID().toString();
		LOGGER.debug("Triggering ProductSpecCancelModificationCommand with productSpecId : {}", productSpecId);
		return commandGateway.sendAndWait(new ProductSpecificationDeleteCommmand(productSpecId,delDate,interval,intervalUnit));
	}

	@Override
	public String temporaryDeleteProductSpecification() {
		return null;
	}

	@Override
	public void modifyStockItemProductSpecCharacteristics(final String productSpecId,
														  final List<ProductSpecificationCharacteristic> stockItemCharacteristics) {

		LOGGER.debug("Triggering SelectStockItemProductSpecCharacteristicCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyStockItemProductSpecCharacteristicCommand(productSpecId,stockItemCharacteristics));
		LOGGER.debug("Triggering ComputeProductConfigurationCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyComputeProductConfigurationCommand(productSpecId));
		LOGGER.debug("Triggering AssociateProductSpecificationToStockItemCommand with productSpecId : {}", productSpecId);
		commandGateway.sendAndWait(new ModifyAssociateProductSpecificationToStockItemCommand(productSpecId));
	}
	
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.aggregate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.orange.discobole.productcatalog.productspecification.dto.generated.common.*;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimePeriod;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.TimeRange;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecRelationship;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.productcatalog.productspecification.command.productspec.*;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.productspecification.constant.ResourceState;
import com.orange.discobole.productcatalog.productspecification.constant.ServiceSpecLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.constant.StockItemLifeCycleEnum;
import com.orange.discobole.productcatalog.productspecification.dto.Validation;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.OperationSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.ProductConfSpecCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.ProductConfSpecCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.ProductConfigurationSpec;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.RelatedResource;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.StockItemRef;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.StockItemType;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productspecification.dto.generated.productoffering.UsageSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.CharacteristicValueSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristic;
import com.orange.discobole.productcatalog.productspecification.dto.generated.stockitem.StockItemCharacteristicValue;
import com.orange.discobole.productcatalog.productspecification.event.productspec.*;
import com.orange.discobole.productcatalog.productspecification.exception.DiscoClientException;
import com.orange.discobole.productcatalog.productspecification.pojo.*;
import com.orange.discobole.productcatalog.productspecification.service.AdminQueryService;
import com.orange.discobole.productcatalog.productspecification.service.CommercialProductInstalledBaseQueryService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;
import com.orange.discobole.productcatalog.productspecification.util.TimePeriodValidityUtil;

import jakarta.annotation.Resource;

import org.springframework.util.ObjectUtils;


/**
 * The Class ProductSpecAggregate handles the business logic of different
 * commands.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class ProductSpecAggregate {

	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;
	private static final Logger LOGGER = LogManager.getLogger(ProductSpecAggregate.class);
	private static final String VERSION = "0.1";
	private static final String PARTIES = "parties";
	private static final String EVENTLIST = "eventList";
	private static final String RESOURCE = "resource";
	private static final String NOT_FOUND_MSG = " not found";
	private static final String DISCO_PS_SERVICE_SPECIFICATION_NOT_FOUND = "DISCO_PS_SERVICE_SPECIFICATION_NOT_FOUND";
	private static final String DISCO_PS_INVALID_SERVICESPEC_STATE = "DISCO_PS_INVALID_SERVICESPEC_STATE";
	private static final String DISCO_PS_STOCK_ITEM_NOT_FOUND = "DISCO_PS_STOCK_ITEM_NOT_FOUND";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_NAME = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_NAME";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_ID = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_ID";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUERANGE = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUERANGE";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_MULTIPLE_VALUE = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_MULTIPLE_VALUE";
	private static final String DISCO_PS_VALIDITY_INVALID_STARTTIME="DISCO_PS_VALIDITY_INVALID_STARTTIME";
	private static final String DISCO_PS_VALIDITY_INVALID_ENDTIME="DISCO_PS_VALIDITY_INVALID_ENDTIME";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE";
	private static final String DISCO_PS_INVALID_STOCKITEM_TYPE_FOUND = "DISCO_PS_INVALID_STOCKITEM_TYPE_FOUND";
	private static final String DISCO_PS_INVALID_STOCKITEM_LIST_FOUND = "DISCO_PS_INVALID_STOCKITEM_LIST_FOUND";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_DISTINCT_IDNAME = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_DISTINCT_IDNAME";
	private static final String DISCO_PS_INVALID_PS_USAGE_SELECTED = "DISCO_PS_INVALID_PS_USAGE_SELECTED";
	private static final String DISCO_PS_INVALID_PS_ID = "DISCO_PS_INVALID_PS_ID";
	private static final String DISCO_PS_INVALID_PS_PARTY_SELECTED = "DISCO_PS_INVALID_PS_PARTY_SELECTED";
	private static final String DISCO_PS_INVALID_PS_PARTYROLE_SELECTED = "DISCO_PS_INVALID_PS_PARTYROLE_SELECTED";
	private static final String DISCO_PS_INVALID_PS_LIFECYCLE = "DISCO_PS_INVALID_PS_LIFECYCLE";
	private static final String DISCO_PS_INVALID_PS_NOTFOUND = "DISCO_PS_INVALID_PS_NOTFOUND";
	private static final String DISCO_PS_INVALID_STOCKITEM_STATE = "DISCO_PS_INVALID_STOCKITEM_STATE";
	private static final String DISCO_PS_INVALID_PS_LIFECYCLE_INTEST = "DISCO_PS_INVALID_PS_LIFECYCLE_INTEST";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE_OUT_OF_RANGE = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE_OUT_OF_RANGE";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID = "DISCO_PS_INVALID_PS_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID";
	private static final String DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_CFS_REFERENCE_VALUE_MUST_BE_NOT_NULL = "DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_CFS_REFERENCE_VALUE_MUST_BE_NOT_NULL";
	private static final String DISCO_PS_INVALID_CFS = "DISCO_PS_INVALID_CFS";
	private static final String DISCO_PS_EXPIRED_CFS = "DISCO_PS_EXPIRED_CFS";

	private static final String DISCO_PS_INVALID_RELATIONSHIP = "DISCO_PS_INVALID_RELATIONSHIP";
	private static final String DISCO_PS_INVALID_VALIDFOR_STARTTIME_ENDTIME = "DISCO_PS_INVALID_VALIDFOR_STARTTIME_ENDTIME";
	private static final String DISCO_PS_INVALID_VALIDFOR_BEFORE_CURRENT_TIME = "DISCO_PS_INVALID_VALIDFOR_BEFORE_CURRENT_TIME";
	private static final String DISCO_PS_RELATIONSHIP_INVALID_VALIDFOR_STARTTIME = "DISCO_PS_RELATIONSHIP_INVALID_VALIDFOR_STARTTIME";
	@Autowired
	private CommercialProductInstalledBaseQueryService cpibQueryService;

	@Autowired
	private AdminQueryService adminQueryService;

	@Resource
	private ConfigurableProperties configurableProperties;

	@AggregateIdentifier
	private String productSpecID;
	private ProductSpecification productSpec;
	private String accessToken;
	// @Resource


	public ProductSpecAggregate() {
	}

	public ProductSpecAggregate(AccessTokenInterceptor accessTokenInterceptor) {
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean
	}

	/**
	 * ProductSpecAggregate: business logic for PS Creation by CFS
	 * AggregateLifecycle.apply() is used to map Event Handlers will generate events
	 * in db.
	 *
	 * @param command : InitiateProductSpecCommand
	 */

	@CommandHandler
	public ProductSpecAggregate(InitiateProductSpecCommand command, QueryService queryService) {
		LOGGER.info("Constructor ProductSpecAggregate -> InitiateProductSpecCommand : {} ", command);
		String serviceSpecId = command.getServiceSpecId();
		this.productSpecID = command.getProductSpecId();

		ServiceSpecification serviceSpec = queryService.getServiceSpecById(serviceSpecId,accessToken);
		if (null == serviceSpec) {
			throw new DiscoManagedClientException(DISCO_PS_SERVICE_SPECIFICATION_NOT_FOUND, serviceSpecId, null);
		} else {
			ServiceSpecificationRef serviceSpecRef = convert(serviceSpec);
			AggregateLifecycle.apply(new ServiceSpecSelectedEvent(command.getProductSpecId(), serviceSpecId));

			Event event = verifyServiceSpecState(serviceSpec);
			if (event instanceof ServiceSpecStateVerificationFailedEvent) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpec.getId(), null);
			}
			AggregateLifecycle.apply(event);
			TimePeriod timePeriod = serviceSpec.getValidFor();
			OffsetDateTime now = OffsetDateTime.now();
			OffsetDateTime validityEndDateTime = timePeriod.getEndDateTime();

// Case : Check if CFS is invalid
			if (validityEndDateTime != null && now.isAfter(validityEndDateTime)) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_CFS);
			}

			String productSpecId = this.productSpecID;
			EntityType baseType = (serviceSpec.getBaseType() != null
					&& "ShippingServiceSpecification".equals(serviceSpec.getBaseType()))
					? EntityType.SHIPPINGPRODUCTSPECIFICATION
					: null;
			OffsetDateTime lastUpdate = OffsetDateTime.now();
			AggregateLifecycle.apply(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
					serviceSpec.getType(), serviceSpecRef, lastUpdate, null, baseType));
		}
	}

	/**
	 * ProductSpecAggregate: business logic for PS Creation by StockItem
	 * AggregateLifecycle.apply() is used to map Event Handlers will generate events
	 * in db.
	 *
	 * @param command : the InitiateStockItemProductSpecCommand
	 */
	@CommandHandler
	public ProductSpecAggregate(InitiateStockItemProductSpecCommand command, QueryService queryService) {
		LOGGER.info("Constructor ProductSpecAggregate -> InitiateStockItemProductSpecCommand : {} ", command);
		String stockItemId = command.getStockItemTypeId();
		this.productSpecID = command.getProductSpecId();
		LOGGER.info("Product Spec test {}", stockItemId);
		StockItemType stockItemType = queryService.getStockItemTypeById(stockItemId,accessToken);
		if (null == stockItemType) {
			throw new DiscoManagedClientException(DISCO_PS_STOCK_ITEM_NOT_FOUND, stockItemId, null);
		} else {
			AggregateLifecycle.apply(new StockItemSelectedEvent(command.getProductSpecId(), stockItemId));
			String productSpecId = this.productSpecID;
			OffsetDateTime lastUpdate = OffsetDateTime.now();
			AggregateLifecycle.apply(new ProductSpecInitiatedEvent(productSpecId, ProductSpecificationLifecycle.INSTUDY,
					SupportEntity.STOCKITEMTYPE.toString(), null, lastUpdate, stockItemType, null));

		}
	}

	/**
	 * This method will describe the product specification with related resource
	 * ,related party and validity
	 *
	 * @param command
	 * @param queryService
	 */
	@CommandHandler
	public void processIdentityDefineProductSpecCommand(DefineIdentityProductSpecCommand command,
														QueryService queryService) {
		LOGGER.info("Method processIdentityDefineProductSpecCommand -> DefineIdentityProductSpecCommand : {} ",
				command);
		List<RelatedParty> selectRelatedParty = new ArrayList<>();
		List<RelatedResource> selectRelatedResource = new ArrayList<>();
		TimePeriod restriction = null;
		String href = configurableProperties.getCatprodcaturl() + "?id=" + this.productSpecID;
		String productSpecId = this.productSpec.getId();
		OffsetDateTime psStartDateTime = command.getValidFor().getStartDateTime();
		OffsetDateTime psEndDateTime = command.getValidFor().getEndDateTime();
		if (psStartDateTime.isBefore(OffsetDateTime.now().minusMinutes(5))) {
			throw new DiscoManagedClientException(DISCO_PS_VALIDITY_INVALID_STARTTIME);
		}
		if(psEndDateTime!=null && psEndDateTime.isBefore(psStartDateTime)){

			throw new DiscoManagedClientException(DISCO_PS_VALIDITY_INVALID_ENDTIME);
		}
		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			ServiceSpecification serviceSpecification = queryService
					.getServiceSpecById(this.productSpec.getServiceSpecification().get(0).getId(), accessToken);

			Event event = verifyServiceSpecState(serviceSpecification);
			if (event instanceof ServiceSpecStateVerificationFailedEvent) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpecification.getId(), null);
			}
			AggregateLifecycle.apply(event);
			AggregateLifecycle
					.apply(new ProductSpecRelResourceSelectedEvent(productSpecId, command.getRelatedResource()));
			selectRelatedResource = serviceSpecification.getRelatedResource();
			restriction = serviceSpecification.getValidFor();
			OffsetDateTime validityEndDateTime = restriction.getEndDateTime();
			if (validityEndDateTime != null && psStartDateTime.isAfter(validityEndDateTime)) {
				throw new DiscoManagedClientException(DISCO_PS_EXPIRED_CFS);
			}

		} else if (this.productSpec.getSupportEntity().equals(SupportEntity.STOCKITEMTYPE)) {
			List<StockItem> stockItemList = queryService
					.getStockItemByStockItemTypeId(this.productSpec.getStockItemType().getId(),accessToken);

			for (StockItem stockItem : stockItemList) {
				Event event = verifyStockItemState(stockItem);
				if (event instanceof StockItemStateVerificationFailedEvent) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, null, stockItem.getId());
				}
				AggregateLifecycle.apply(event);
			}
			AggregateLifecycle
					.apply(new ProductSpecRelResourceSelectedEvent(productSpecId, command.getRelatedResource()));
			selectRelatedResource = command.getRelatedResource();
			String stockItemTypeId = this.productSpec.getStockItemType().getId();
			StockItemType stockItemType = queryService.getStockItemTypeById(stockItemTypeId, accessToken);
			restriction = stockItemType.getValidFor();
		}
		selectRelatedParty = processRelatedParty(command.getRelatedParty());
		processValidFor(command.getValidFor(), restriction);

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecIdentityDataEvent(productSpecId, command.getDefineIdentityData(),
				selectRelatedParty, selectRelatedResource, command.getValidFor(), lastUpdate, command.getType(), href));

	}

	/*
	 * @EventSourcingHandler -- all state changes are defined in
	 * the @EventSourcingHandlers on method---- we are passing variables which
	 * required for the particular events
	 */

	/**
	 * updates the state of aggregate after applying ProductSpecIdentityDataEvent.
	 *
	 * @param event : ProductSpecIdentityDataEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecIdentityDataEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec.brand(event.getDefineIdentityData().getBrand())
				.description(event.getDefineIdentityData().getDescription())
				.name(event.getDefineIdentityData().getName())
				.href(event.getHref())
				.productNumber(event.getDefineIdentityData().getProductNumber()).relatedParty(event.getRelatedParty())
				.relatedResource(event.getRelatedResource()).validFor(event.getValidFor())
				.type(event.getType().toString());
	}

	/**
	 * updates the state of aggregate after applying ProductSpecInitiatedEvent.
	 *
	 * @param event : ProductSpecInitiatedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecInitiatedEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec.id(event.getProductSpecId()).addServiceSpecificationItem(event.getServiceSpecificationRef())
				.stockItemType(event.getStockItemType()).lifecycleStatus(event.getResourceState())
				.baseType(event.getBaseType() != null ? event.getBaseType().toString() : null);
	}

	/**
	 * updates the state of aggregate after applying ServiceSpecSelectedEvent.
	 *
	 * @param event : ServiceSpecSelectedEvent
	 */
	@EventSourcingHandler
	public void on(final ServiceSpecSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec = new ProductSpecification().supportEntity(SupportEntity.CFSSPEC);
	}

	/**
	 * updates the state of aggregate after applying StockItemSelectedEvent.
	 *
	 * @param event : StockItemSelectedEvent
	 */
	@EventSourcingHandler
	public void on(final StockItemSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec = new ProductSpecification().supportEntity(SupportEntity.STOCKITEMTYPE);
	}

	/**
	 * updates the state of aggregate after applying ProductSpecOpDefinedEvent.
	 *
	 * @param event : ProductSpecOpDefinedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecOpDefinedEvent event) {
		this.productSpec.setOperationSpecification(event.getOperationSpecifications());
	}

	/**
	 * Convert.
	 *
	 * @param serviceSpec the service spec
	 * @return the service specification ref
	 */
	private ServiceSpecificationRef convert(ServiceSpecification serviceSpec) {
		return new ServiceSpecificationRef().id(serviceSpec.getId()).name(serviceSpec.getName())
				.href(serviceSpec.getHref());
	}

	/**
	 * processProductSpecOperationCommand : business logic for PS Operations
	 *
	 * @param command : the ProductSpecOperationCommand
	 */
	@CommandHandler
	public void processProductSpecOperationCommand(ProductSpecOperationCommand command, QueryService queryService) {
		LOGGER.info("Method processProductSpecOperationCommand -> ProductSpecOperationCommand : {}", command);
		List<OperationSpecification> operationSelected = new ArrayList<>();
		String productSpecId = this.productSpec.getId();

		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			String serviceSpecId = this.productSpec.getServiceSpecification().get(0).getId();
			ServiceSpecification serviceSpec = queryService.getServiceSpecById(serviceSpecId, accessToken);
			Event event = verifyServiceSpecState(serviceSpec);
			if (event instanceof ServiceSpecStateVerificationFailedEvent) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpec.getId(), null);
			}
			AggregateLifecycle.apply(event);
			List<OperationSpecification> operationSpecs = serviceSpec.getOperationSpecification();

			for (OperationSpecification operationSpecification : operationSpecs) {
				String id = UUID.randomUUID().toString() + "." + operationSpecification.getId();
				operationSpecification.id(id);
				operationSpecification
						.isQualificationRequested(operationSpecification.isIsQualificationRequested() != null
								? operationSpecification.isIsQualificationRequested()
								: true);
				operationSelected.add(operationSpecification);
			}

		} else if (this.productSpec.getSupportEntity().equals(SupportEntity.STOCKITEMTYPE)) {

			String stockItemTypeId = this.productSpec.getStockItemType().getId();
			StockItemType stockItemType = queryService.getStockItemTypeById(stockItemTypeId, accessToken);

			OperationSpecification operationSpecification1 = new OperationSpecification();
			operationSpecification1.id("1");
			operationSpecification1.name("Add");
			operationSpecification1.description("add a product");
			operationSpecification1.validFor(stockItemType.getValidFor());

			OperationSpecification operationSpecification2 = new OperationSpecification();
			operationSpecification2.id("2");
			operationSpecification2.name("Return");
			operationSpecification2.description("Return a product");
			operationSpecification2.validFor(stockItemType.getValidFor());

			OperationSpecification operationSpecification3 = new OperationSpecification();
			operationSpecification3.id("3");
			operationSpecification3.name("Replace");
			operationSpecification3.description("Replace a product");
			operationSpecification3.validFor(stockItemType.getValidFor());

			operationSelected.add(operationSpecification1);
			operationSelected.add(operationSpecification2);
			operationSelected.add(operationSpecification3);
		}

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecOpDefinedEvent(productSpecId, operationSelected, lastUpdate));
	}

	/**
	 * Process.
	 *
	 * @param command the command : SelectProductSpecCharacteristicCommand
	 */
	@CommandHandler
	public void processSelectProductSpecCharacteristicCommand(SelectProductSpecCharacteristicCommand command,
															  QueryService queryService) {
		LOGGER.info(
				"Method processSelectProductSpecCharacteristicCommand -> SelectProductSpecCharacteristicCommand : {} ",
				command);
		List<UsageSpecification> usageSpec = new ArrayList<>();
		String serviceSpecId = this.productSpec.getServiceSpecification().get(0).getId();
		ServiceSpecification serviceSpec = queryService.getServiceSpecById(serviceSpecId, accessToken);
		Event event = verifyServiceSpecState(serviceSpec);
		if (event instanceof ServiceSpecStateVerificationFailedEvent) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpec.getId(), null);
		}
		AggregateLifecycle.apply(event);
		String productSpecId = this.productSpec.getId();
		List<ProductSpecificationCharacteristic> productSpecCharacteristics = command.getProductSpecCharacteristics();

		// characteritics
		List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics = new ArrayList<>();
		Set<String> invalidCfsCharacteristicsSelected = new HashSet<>();
		checkServiceSpecCharacteristicsHelper(productSpecCharacteristics, serviceSpec,
				selectedProductSpecificationCharacteristics, invalidCfsCharacteristicsSelected);

		if (!invalidCfsCharacteristicsSelected.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED, invalidCfsCharacteristicsSelected.toString(), null);
		} else {
			AggregateLifecycle
					.apply(new ProductSpecCharacteristicsSelectedEvent(this.productSpecID, productSpecCharacteristics));
		}
		// usage Spec
		usageSpec = processUsageSpecificaion(command.getUsageSpecifications(), serviceSpec.getUsageSpecification());

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecCharacteristicsDefinedEvent(productSpecId,
				selectedProductSpecificationCharacteristics, usageSpec, lastUpdate));

	}

	private void checkServiceSpecCharacteristicsHelper(List<ProductSpecificationCharacteristic> commandChar,
													   ServiceSpecification serviceSpecification,
													   List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics,
													   Set<String> invalidCfsCharacteristicsSelected) {
		List<CharacteristicSpecification> serviceChar = serviceSpecification.getServiceSpecCharacteristic();
		HashMap<String, String> cfsCharIdMap = buildCfsCharIdMap(serviceChar);
		HashMap<String, String> prodSpecIdCheck = validateProductSpecIdAndName(commandChar);
		validateCfsCharIdAndName(prodSpecIdCheck, cfsCharIdMap);
		HashMap<String, HashSet<String>> existingServiceSpecCharValuesMap = buildExistingServiceSpecCharValuesMap(serviceChar, cfsCharIdMap);

		HashMap<String, CharacteristicSpecification> existingServiceSpecsMap = buildExistingServiceSpecsMap(serviceChar, cfsCharIdMap);

		validateAndSelectProductSpecCharacteristics(
				commandChar, serviceSpecification, existingServiceSpecCharValuesMap, existingServiceSpecsMap,
				invalidCfsCharacteristicsSelected, selectedProductSpecificationCharacteristics
		);
		this.productSpec.setProductSpecCharacteristic(selectedProductSpecificationCharacteristics);
  }


	private HashMap<String, String> buildCfsCharIdMap(List<CharacteristicSpecification> serviceChar) {
		HashMap<String, String> cfsCharIdMap = new HashMap<>();
		for (CharacteristicSpecification itemCharacteristic : serviceChar) {
			cfsCharIdMap.put(itemCharacteristic.getId(), itemCharacteristic.getName());
		}
		return cfsCharIdMap;
	}


	private HashMap<String, String> validateProductSpecIdAndName(
			List<ProductSpecificationCharacteristic> commandChar) {

		HashMap<String, String> prodSpecIdCheck = new HashMap<>();
		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : commandChar) {
			if (prodSpecIdCheck.containsKey(productSpecificationCharacteristic.getId())
					|| prodSpecIdCheck.containsValue(productSpecificationCharacteristic.getName())) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_DISTINCT_IDNAME,
						productSpecificationCharacteristic.getName(), productSpecificationCharacteristic.getName());
			} else {
				prodSpecIdCheck.put(productSpecificationCharacteristic.getId(),
						productSpecificationCharacteristic.getName());
			}
		}
		return prodSpecIdCheck;
	}

	private void validateCfsCharIdAndName(
			HashMap<String, String> prodSpecIdCheck,
			HashMap<String, String> cfsCharIdMap) {

		for (Map.Entry<String, String> prodSpecEntry : prodSpecIdCheck.entrySet()) {
			if (cfsCharIdMap.containsKey(prodSpecEntry.getKey())) {
				String name = cfsCharIdMap.get(prodSpecEntry.getKey());
				if (!prodSpecEntry.getValue().equals(name)) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_NAME,
							prodSpecEntry.getKey(), null);
				}
			} else {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_ID,
						prodSpecEntry.getKey(), null);
			}
		}
	}

	private HashMap<String, HashSet<String>> buildExistingServiceSpecCharValuesMap(
			List<CharacteristicSpecification> serviceChar,
			HashMap<String, String> cfsCharIdMap) {

		HashMap<String, HashSet<String>> existingServiceSpecCharValuesMap = initializeEmptyValueMap(cfsCharIdMap);



		for (CharacteristicSpecification characteristic : serviceChar) {
			for (Map.Entry<String, String> entry : cfsCharIdMap.entrySet()) {
				if (characteristic.getId().equals(entry.getKey())) {
					List<CharacteristicValueSpecification> valueSpecs = characteristic.getCharacteristicValueSpecification() != null
							? characteristic.getCharacteristicValueSpecification()
							: new ArrayList<>();

					for (CharacteristicValueSpecification characteristicSpec : valueSpecs) {
						String valueWithUnit = buildValueWithUnit(characteristicSpec, characteristic.getType());
						existingServiceSpecCharValuesMap.get(entry.getKey()).add(valueWithUnit);
					}
				}
			}
		}

		return existingServiceSpecCharValuesMap;
	}



	private String buildValueWithUnit(CharacteristicValueSpecification characteristicSpec, String type) {
		if (characteristicSpec.getValue() != null) {
			return characteristicSpec.getValue()
					+ (characteristicSpec.getUnitOfMeasure() != null ? characteristicSpec.getUnitOfMeasure() : "");
		} else if (!"AddressCharacteristic".equalsIgnoreCase(type)) {
			return characteristicSpec.getValueFrom() + "-" + characteristicSpec.getValueTo()
					+ (characteristicSpec.getUnitOfMeasure() != null ? characteristicSpec.getUnitOfMeasure() : "");
		}
		return "";
	}

	private HashMap<String, HashSet<String>> initializeEmptyValueMap(HashMap<String, String> cfsCharIdMap) {
		HashMap<String, HashSet<String>> map = new HashMap<>();
		for (String key : cfsCharIdMap.keySet()) {
			map.put(key, new HashSet<>());
		}
		return map;
	}

	private HashMap<String, CharacteristicSpecification> buildExistingServiceSpecsMap(
			List<CharacteristicSpecification> serviceChar,
			HashMap<String, String> cfsCharIdMap) {

		HashMap<String, CharacteristicSpecification> existingServiceSpecsMap = new HashMap<>();
		for (CharacteristicSpecification characteristic : serviceChar) {
			for (Map.Entry<String, String> entry : cfsCharIdMap.entrySet()) {
				if (characteristic.getId().equals(entry.getKey())) {
					existingServiceSpecsMap.put(entry.getKey(), characteristic);
				}
			}
		}
		return existingServiceSpecsMap;
	}

	private void validateAndSelectProductSpecCharacteristics(
			List<ProductSpecificationCharacteristic> commandChar,
			ServiceSpecification serviceSpecification,
			HashMap<String, HashSet<String>> existingServiceSpecCharValuesMap,
			HashMap<String, CharacteristicSpecification> existingServiceSpecsMap,
			Set<String> invalidCfsCharacteristicsSelected,
			List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics) {

		//Create a map of characteristic id with key as servicespec part .This will be used during char modification step
		//and will abort reconstruction of id for any existing char
		HashMap<String,String> originaCharIdMap= buildMapForCharId();
		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : commandChar) {
			if (!existingServiceSpecCharValuesMap.containsKey(productSpecificationCharacteristic.getId())) {
				continue;
			}

			CharacteristicSpecification serviceSpecCharacteristic =
					existingServiceSpecsMap.get(productSpecificationCharacteristic.getId());
			HashSet<String> cfsCharacteristicValueReference =
					existingServiceSpecCharValuesMap.get(productSpecificationCharacteristic.getId());

			HashMap<String, Boolean> checkRepetation = initializeCheckRepetation(cfsCharacteristicValueReference);

			boolean valid = validateCharValue(
					productSpecificationCharacteristic,
					serviceSpecification,
					serviceSpecCharacteristic,
					cfsCharacteristicValueReference,
					checkRepetation,
					invalidCfsCharacteristicsSelected
			);

			if (valid) {
				enrichAndSelectCharacteristic(
						productSpecificationCharacteristic,
						serviceSpecCharacteristic,
						selectedProductSpecificationCharacteristics,
						originaCharIdMap
				);
			}
		}
	}

	private boolean validateCharValue(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			ServiceSpecification serviceSpecification,
			CharacteristicSpecification serviceSpecCharacteristic,
			HashSet<String> cfsCharacteristicValueReference,
			HashMap<String, Boolean> checkRepetation,
			Set<String> invalidCfsCharacteristicsSelected) {

		List<ProductSpecificationCharacteristicValue> values =
				productSpecificationCharacteristic.getProductSpecCharacteristicValue();
		boolean valid = true;

		for (ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue : values) {
			valid = processCharacteristicValue(
					productSpecificationCharacteristic,
					serviceSpecification,
					serviceSpecCharacteristic,
					cfsCharacteristicValueReference,
					checkRepetation,
					invalidCfsCharacteristicsSelected,
					productSpecificationCharacteristicValue
			);

			if (!valid) break;
		}
		return valid;
	}
	private HashMap<String,String> buildMapForCharId(){
		HashMap<String,String> charIdMap=new HashMap<>();
		if(this.productSpec.getProductSpecCharacteristic()!=null && !this.productSpec.getProductSpecCharacteristic().isEmpty()) {
			for (ProductSpecificationCharacteristic originalChar: this.productSpec.getProductSpecCharacteristic())
				charIdMap.put(originalChar.getId().substring(originalChar.getId().indexOf('.') + 1),originalChar.getId());
		}
		return charIdMap;
	}
	private void enrichAndSelectCharacteristic(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			CharacteristicSpecification serviceSpecCharacteristic,
			List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics,
			HashMap<String,String> originaCharIdMap) {

		productSpecificationCharacteristic
				.id(originaCharIdMap.getOrDefault(productSpecificationCharacteristic.getId(),(UUID.randomUUID().toString() + "." + productSpecificationCharacteristic.getId())))
				.valueType(serviceSpecCharacteristic.getValueType())
				.configurable(productSpecificationCharacteristic.isConfigurable() != null
						? productSpecificationCharacteristic.isConfigurable()
						: true)
				.isUnique(productSpecificationCharacteristic.isIsUnique() != null
						? productSpecificationCharacteristic.isIsUnique()
						: false)
				.type(serviceSpecCharacteristic.getType() != null
						? serviceSpecCharacteristic.getType()
						: "StringCharacteristic")
				.regex(serviceSpecCharacteristic.getRegex())
				.extensible(productSpecificationCharacteristic.isExtensible() != null
						? productSpecificationCharacteristic.isExtensible()
						: false);

		selectedProductSpecificationCharacteristics.add(productSpecificationCharacteristic);
	}
	private boolean processCharacteristicValue(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			ServiceSpecification serviceSpecification,
			CharacteristicSpecification serviceSpecCharacteristic,
			HashSet<String> cfsCharacteristicValueReference,
			HashMap<String, Boolean> checkRepetation,
			Set<String> invalidCfsCharacteristicsSelected,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {

		CharacteristicValueSpecification specCharValueData = fetchMatchingCharacteristicValue(
				productSpecificationCharacteristicValue, serviceSpecCharacteristic.getCharacteristicValueSpecification());
		String mappedValue = "";

		if ("ValidityCharacteristic".equalsIgnoreCase(serviceSpecCharacteristic.getType())) {
			validateTimeRange(serviceSpecCharacteristic, productSpecificationCharacteristicValue,
					productSpecificationCharacteristic.getId());
		} else if (!"AddressCharacteristic".equalsIgnoreCase(serviceSpecCharacteristic.getType())) {
			mappedValue = handleNonAddressCharacteristic(
					productSpecificationCharacteristic,
					specCharValueData,
					cfsCharacteristicValueReference,
					checkRepetation,
					invalidCfsCharacteristicsSelected,
					productSpecificationCharacteristicValue);
			if (mappedValue.isEmpty()) {
				return false;
			}
		}

		validateValidFor(serviceSpecification, productSpecificationCharacteristicValue);

		checkRepetation.put(mappedValue, true);
		return true;
	}

	private void validateTimeRange(
			CharacteristicSpecification serviceSpecCharacteristic,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue,
			String productSpecId) {

		boolean isInRange = isTimeRangeWithinRange(
				serviceSpecCharacteristic.getCharacteristicValueSpecification(),
				productSpecificationCharacteristicValue
		);
		if (!isInRange) {
			throw new DiscoManagedClientException(
					DISCO_PS_INVALID_PS_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID,
					productSpecId, null);
		}
	}

	private String handleNonAddressCharacteristic(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			CharacteristicValueSpecification specCharValueData,
			HashSet<String> cfsCharacteristicValueReference,
			HashMap<String, Boolean> checkRepetation,
			Set<String> invalidCfsCharacteristicsSelected,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {

		String mappedValue = mapValue(productSpecificationCharacteristic, productSpecificationCharacteristicValue);

		if (productSpecificationCharacteristicValue.getUnitOfMeasure() != null) {
			mappedValue = mappedValue + productSpecificationCharacteristicValue.getUnitOfMeasure();
		}

		if (Boolean.TRUE.equals(checkRepetation.get(mappedValue))) {
			throw new DiscoManagedClientException(
					DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_MULTIPLE_VALUE,
					productSpecificationCharacteristic.getId(), null);
		}

		validateSpecCharValueData(
				productSpecificationCharacteristic,
				specCharValueData,
				cfsCharacteristicValueReference,
				invalidCfsCharacteristicsSelected,
				mappedValue,
				productSpecificationCharacteristicValue);

		return mappedValue;
	}

	private String mapValue(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {

		if (productSpecificationCharacteristicValue.getCharacteristicReferenceValue() != null) {
			if (productSpecificationCharacteristicValue.getValue() == null) {
				throw new DiscoManagedClientException(
						DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE,
						productSpecificationCharacteristic.getId(), null);
			}
			return productSpecificationCharacteristicValue.getCharacteristicReferenceValue();
		} else {
			productSpecificationCharacteristicValue.setValue(null);
			if (productSpecificationCharacteristicValue.getValueFrom() == null
					|| productSpecificationCharacteristicValue.getValueTo() == null) {
				throw new DiscoManagedClientException(
						DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUERANGE,
						productSpecificationCharacteristic.getName(), productSpecificationCharacteristic.getName());
			} else {
				return productSpecificationCharacteristicValue.getValueFrom() + "-"
						+ productSpecificationCharacteristicValue.getValueTo();
			}
		}
	}

	private void validateSpecCharValueData(
			ProductSpecificationCharacteristic productSpecificationCharacteristic,
			CharacteristicValueSpecification specCharValueData,
			HashSet<String> cfsCharacteristicValueReference,
			Set<String> invalidCfsCharacteristicsSelected,
			String mappedValue,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {

		if (specCharValueData != null && specCharValueData.getValueFrom() != null && specCharValueData.getValueTo() != null) {
			if (productSpecificationCharacteristicValue.getCharacteristicReferenceValue() == null) {
				throw new DiscoManagedClientException(
						DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_CFS_REFERENCE_VALUE_MUST_BE_NOT_NULL,
						productSpecificationCharacteristic.getId() ,productSpecificationCharacteristic.getId());
			} else if (!isCharacteristicValueInRange(
					specCharValueData, productSpecificationCharacteristicValue.getCharacteristicReferenceValue())) {
				throw new DiscoManagedClientException(
						DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE_OUT_OF_RANGE,
						productSpecificationCharacteristic.getId(), null);
			}
		} else {
			if (!cfsCharacteristicValueReference.contains(mappedValue)) {

				invalidCfsCharacteristicsSelected.add(mappedValue);


//				// Empty string will indicate invalid match to main method
//				throw new ReturnFalseException(); // Or handle with boolean return instead of exception
			}
		}
	}

	private void validateValidFor(
			ServiceSpecification serviceSpecification,
			ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {

		TimePeriod restriction = serviceSpecification.getValidFor();
		Validation validation = TimePeriodValidityUtil.checkTimePeriodRestriction(
				productSpecificationCharacteristicValue.getValidFor(), restriction);
		if (!validation.isIsValid()) {
			throw new DiscoManagedClientException(
					DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE,
					productSpecificationCharacteristicValue.getValidFor().toString(),
					validation.getReason());
		}
	}


	private HashMap<String, Boolean> initializeCheckRepetation(HashSet<String> cfsCharacteristicValueReference) {
		HashMap<String, Boolean> checkRepetation = new HashMap<>();
		cfsCharacteristicValueReference.forEach(x -> checkRepetation.put(x, false));
		return checkRepetation;
	}


	private boolean isTimeRangeWithinRange(List<CharacteristicValueSpecification> characteristicValueSpecifications, ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue) {
		OffsetDateTime psFrom = productSpecificationCharacteristicValue.getTimeRange().getValidFrom();
		OffsetDateTime psTo =  productSpecificationCharacteristicValue.getTimeRange().getValidTo();

		for (CharacteristicValueSpecification spec : characteristicValueSpecifications) {
			OffsetDateTime cfsFrom = spec.getTimeRange().getValidFrom();
			OffsetDateTime cfsTo   = spec.getTimeRange().getValidTo();

			//End of bill cycle
			if (cfsTo != null && cfsTo.getYear() == 2100 && psTo!=null && psTo.getYear()==2100) {
				return true;
			}
			//infinity
			else if(cfsTo.getYear()==9999) {
				return true;
			}
			//timeRange
			else {

				boolean fromValid = psFrom == null || (cfsFrom != null && !psFrom.isBefore(cfsFrom));
				boolean toValid = psTo == null || (cfsTo != null && !psTo.isAfter(cfsTo));
				if (fromValid && toValid) {
					return true;  // Found at least one matching range
				}
			}
		}

		return false;  // No matching range found


	}

	public CharacteristicValueSpecification fetchMatchingCharacteristicValue(
			ProductSpecificationCharacteristicValue productSpecCharacteristicValue,
			List<CharacteristicValueSpecification> characteristicValueSpecifications) {

		String unitOfMeasure = productSpecCharacteristicValue.getUnitOfMeasure();

		if (unitOfMeasure == null) {
			return null;
		}
		Optional<CharacteristicValueSpecification> match = characteristicValueSpecifications.stream()
				.filter(spec -> unitOfMeasure.equals(spec.getUnitOfMeasure()))
				.findFirst();

		return match.orElse(null);
	}

	public boolean isCharacteristicValueInRange(CharacteristicValueSpecification specCharValueData,
												String characteristicReferenceValue) {
		String valueFromStr = specCharValueData.getValueFrom();
		String valueToStr = specCharValueData.getValueTo();

		try {
			double valueFrom = Double.parseDouble(valueFromStr);
			double valueTo = Double.parseDouble(valueToStr);
			double referenceValue = Double.parseDouble(characteristicReferenceValue);

			// Check if referenceValue is within the range [valueFrom, valueTo]
			return referenceValue >= valueFrom && referenceValue <= valueTo;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Values must be numeric", e);
		}}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecCharacteristicsSelectedEvent.
	 *
	 * @param event : ProductSpecCharacteristicsSelectedEvent
	 */
	@EventSourcingHandler
	private void on(ProductSpecCharacteristicsSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecCharacteristicsDefinedEvent.
	 *
	 * @param event : ProductSpecCharacteristicsDefinedEvent
	 */
	@EventSourcingHandler
	private void on(ProductSpecCharacteristicsDefinedEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec.setProductSpecCharacteristic(event.getProductSpecificationCharacteristics());
		this.productSpec.setUsageSpecification(event.getUsageSpec());
	}

	/**
	 * Process.
	 *
	 * @param command : SelectStockItemProductSpecCharacteristicCommand
	 *
	 */
	@CommandHandler
	public void processSelectStockItemProductSpecCharacteristicCommand(
			SelectStockItemProductSpecCharacteristicCommand command, QueryService queryService) {
		LOGGER.info(
				"Method processSelectStockItemProductSpecCharacteristicCommand -> SelectStockItemProductSpecCharacteristicCommand : {} ",
				command);
		List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics = new ArrayList<>();
		productSpecCharacteriticsHelper(this.productSpecID, command.getProductSpecCharacteristics(),
				selectedProductSpecificationCharacteristics, queryService);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecCharacteristicsDefinedEvent(this.productSpecID,
				selectedProductSpecificationCharacteristics, new ArrayList<>(), lastUpdate));
	}

	private void productSpecCharacteriticsHelper(String productSpecId,
												 List<ProductSpecificationCharacteristic> productSpecCharacteristics,
												 List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics,
												 QueryService queryService) {

		String stockItemTypeId = this.productSpec.getStockItemType().getId();
		List<StockItem> stockItemList = validateStockItems(queryService, stockItemTypeId);
		HashMap<String, String> stockItemCharIdMap = getStockItemCharacteristicsMap(queryService, stockItemTypeId);

		validateProductSpecCharacteristics(productSpecCharacteristics, stockItemCharIdMap);

		HashMap<String, HashSet<String>> existingStockItemCharValuesMap = getExistingStockItemCharValuesMap(stockItemList,
				stockItemCharIdMap);

		processProductSpecCharacteristics(productSpecId, productSpecCharacteristics,
				selectedProductSpecificationCharacteristics, existingStockItemCharValuesMap, stockItemCharIdMap);
	}

	private List<StockItem> validateStockItems(QueryService queryService, String stockItemTypeId) {
		List<StockItem> stockItemList = queryService.getStockItemByStockItemTypeId(stockItemTypeId, accessToken);
		if (stockItemList == null || stockItemList.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_LIST_FOUND, stockItemTypeId, null);
		}
		if (queryService.getStockItemTypeById(stockItemTypeId, accessToken) == null) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_TYPE_FOUND, stockItemTypeId, null);
		}
		return stockItemList;
	}

	private HashMap<String, String> getStockItemCharacteristicsMap(QueryService queryService, String stockItemTypeId) {
		List<StockItemCharacteristic> stockItemTypeCharacteristicList = queryService
				.getStockItemTypeById(stockItemTypeId, accessToken).getStockItemCharacteristic();

		HashMap<String, String> stockItemCharIdMap = new HashMap<>();
		for (StockItemCharacteristic itemCharacteristic : stockItemTypeCharacteristicList) {
			stockItemCharIdMap.put(itemCharacteristic.getId(), itemCharacteristic.getName());
		}
		return stockItemCharIdMap;
	}

	private void validateProductSpecCharacteristics(List<ProductSpecificationCharacteristic> productSpecCharacteristics,
													HashMap<String, String> stockItemCharIdMap) {

		HashMap<String, String> prodSpecIdCheck = new HashMap<>();
		for (ProductSpecificationCharacteristic characteristic : productSpecCharacteristics) {
			if (prodSpecIdCheck.containsKey(characteristic.getId())
					|| prodSpecIdCheck.containsValue(characteristic.getName())) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_DISTINCT_IDNAME, null,
						null);
			}
			prodSpecIdCheck.put(characteristic.getId(), characteristic.getName());
		}

		if (prodSpecIdCheck.size() < stockItemCharIdMap.size()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE, null, null);
		}
	}

	private HashMap<String, HashSet<String>> getExistingStockItemCharValuesMap(List<StockItem> stockItemList,
																			   HashMap<String, String> stockItemCharIdMap) {

		HashMap<String, HashSet<String>> existingStockItemCharValuesMap = new HashMap<>();
		for (String stockItemCharacteristicId : stockItemCharIdMap.keySet()) {
			existingStockItemCharValuesMap.put(stockItemCharacteristicId, new HashSet<>());
		}

		for (StockItem stockItem : stockItemList) {
			if(stockItem.getStockItemCharacteristicValue()== null)
			{continue;}
			for (StockItemCharacteristicValue value : stockItem.getStockItemCharacteristicValue()) {
				String characteristicId = value.getStockItemCharacteristic().getId();
				if (stockItemCharIdMap.containsKey(characteristicId)) {
					existingStockItemCharValuesMap.get(characteristicId).add(value.getValue());
				}
			}
		}
		return existingStockItemCharValuesMap;
	}

	private void processProductSpecCharacteristics(String productSpecId,
												   List<ProductSpecificationCharacteristic> productSpecCharacteristics,
												   List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics,
												   HashMap<String, HashSet<String>> existingStockItemCharValuesMap, HashMap<String, String> stockItemCharIdMap) {

		Set<String> invalidCharacteristicsSelected = new HashSet<>();

		for (ProductSpecificationCharacteristic characteristic : productSpecCharacteristics) {
			if (existingStockItemCharValuesMap.containsKey(characteristic.getId())) {
				validateCharacteristicValues(characteristic, existingStockItemCharValuesMap, invalidCharacteristicsSelected);
				if (invalidCharacteristicsSelected.isEmpty()) {
					configureAndAddCharacteristic(characteristic, selectedProductSpecificationCharacteristics);
				}
			}
		}

		if (!invalidCharacteristicsSelected.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE,
					invalidCharacteristicsSelected.toString(), null);
		}

		AggregateLifecycle.apply(new ProductSpecCharacteristicsSelectedEvent(productSpecId, productSpecCharacteristics));
		this.productSpec.setProductSpecCharacteristic(selectedProductSpecificationCharacteristics);
	}

	private void validateCharacteristicValues(ProductSpecificationCharacteristic characteristic,
											  HashMap<String, HashSet<String>> existingStockItemCharValuesMap, Set<String> invalidCharacteristicsSelected) {

		HashSet<String> stockItemCharacteristicValueReference = existingStockItemCharValuesMap.get(characteristic.getId());
		if(characteristic.getProductSpecCharacteristicValue()== null)
		{return;}
		for (ProductSpecificationCharacteristicValue value : characteristic.getProductSpecCharacteristicValue()) {
			if(value.getCharacteristicReferenceValue()== null)
			{continue;}
			if (!stockItemCharacteristicValueReference.contains(value.getCharacteristicReferenceValue())) {
				invalidCharacteristicsSelected.add(value.getCharacteristicReferenceValue());
				break;
			}
		}
	}

	private void configureAndAddCharacteristic(ProductSpecificationCharacteristic characteristic,
											   List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics) {

		characteristic.configurable(characteristic.isConfigurable() != null ? characteristic.isConfigurable() : true);
		characteristic.extensible(characteristic.isExtensible() != null ? characteristic.isExtensible() : false);
		characteristic.isUnique(characteristic.isIsUnique() != null ? characteristic.isIsUnique() : false);
		selectedProductSpecificationCharacteristics.add(characteristic);
	}

	/**
	 * Process.
	 *
	 * @param command      : ComputeProductConfigurationCommand
	 * @param queryService
	 */
	@CommandHandler
	public void processComputeProductConfigurationCommand(ComputeProductConfigurationCommand command,
														  QueryService queryService) {
		LOGGER.info("Method processComputeProductConfigurationCommand -> ComputeProductConfigurationCommand : {} ",
				command);
		String productSpecificationId = command.getProductSpecification();
		List<ProductConfigurationSpec> productConfigurationSpecList = new ArrayList<>();
		computeProductConfigurationHelper(productSpecificationId, productConfigurationSpecList, queryService);
		AggregateLifecycle
				.apply(new ComputeProductConfigurationEvent(productConfigurationSpecList, productSpecificationId));
	}

	private void computeProductConfigurationHelper(String productSpecificationId,
												   List<ProductConfigurationSpec> productConfigurationSpecList, QueryService queryService) {
		String stockItemTypeId = this.productSpec.getStockItemType().getId();
		StockItemType stockItemType = queryService.getStockItemTypeById(stockItemTypeId, accessToken);
		if (null == stockItemType) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_TYPE_FOUND, stockItemTypeId, null);
		}
		ProductSpecification productSpecification = this.productSpec;
		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef();
		productSpecificationRef.id(productSpecificationId);


		//check if characteritics are empty in stockItem type and requested
		if(ObjectUtils.isEmpty(stockItemType.getStockItemCharacteristic()) && ObjectUtils.isEmpty(productConfigurationSpecList)){
			handleProductConfigurationOnEmptyChar(productConfigurationSpecList,queryService,stockItemTypeId,productSpecificationRef,productSpecification);
		}else if (!(stockItemType.getStockItemCharacteristic() == null || stockItemType.getStockItemCharacteristic().isEmpty())) {
			handleProductConfigurationOnChar(productConfigurationSpecList,productSpecificationRef,productSpecification,stockItemType);
		}
	}

	private void handleProductConfigurationOnChar(List<ProductConfigurationSpec> productConfigurationSpecList, ProductSpecificationRef productSpecificationRef,
												  ProductSpecification productSpecification, StockItemType stockItemType) {

		List<List<String>> prodConfig = ComputeProductConfiguration(
				productSpecification.getProductSpecCharacteristic());
		if (prodConfig == null) {
			return;

		}
		HashMap<String, String> valueTypeOfStockChar = new HashMap<>();
		for (StockItemCharacteristic stockItem : stockItemType.getStockItemCharacteristic()) {
			valueTypeOfStockChar.put(stockItem.getName(), stockItem.getValueType());
		}

		Map<String, String> prodSpecCharValueIdMap = new HashMap<>();
		Map<String, String> prodSpecCharValueIdNameDescriptionMap = new HashMap<>();
		List<ProductSpecificationCharacteristic> productSpecificationCharacteristics = productSpecification
				.getProductSpecCharacteristic();
		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : productSpecificationCharacteristics) {
			for (ProductSpecificationCharacteristicValue characteristicValue : productSpecificationCharacteristic
					.getProductSpecCharacteristicValue()) {
				prodSpecCharValueIdMap.put(characteristicValue.getValue(),
						productSpecificationCharacteristic.getId());
				prodSpecCharValueIdNameDescriptionMap.put(characteristicValue.getValue(),
						productSpecificationCharacteristic.getName() + ","
								+ productSpecificationCharacteristic.getDescription());
			}
		}

		for (List<String> configuration : prodConfig) {
			ProductConfigurationSpec productConfigurationSpec = new ProductConfigurationSpec();
			productConfigurationSpec.productSpecification(productSpecificationRef);
			productConfigurationSpec.id(UUID.randomUUID().toString());
			productConfigurationSpec.setIsSellable(false);
			List<ProductConfSpecCharacteristicValue> confSpecCharacteristicValues = new ArrayList<>();
			for (String configValue : configuration) {
				if (prodSpecCharValueIdMap.containsKey(configValue)) {
					String[] nameAndDescription = prodSpecCharValueIdNameDescriptionMap.get(configValue)
							.split(",");

					confSpecCharacteristicValues.add(new ProductConfSpecCharacteristicValue().value(configValue)
							.validFor(this.productSpec.getValidFor())
							.valueType(valueTypeOfStockChar.get(nameAndDescription[0]))
							.enumerated(new ProductConfSpecCharacteristic()
									.id(prodSpecCharValueIdMap.get(configValue)).name(nameAndDescription[0])
									.description(nameAndDescription[1])
									.productSpecification(productSpecificationRef)
									.validFor(this.productSpec.getValidFor())));
				}
			}
			productConfigurationSpec.definedBy(confSpecCharacteristicValues);
			productConfigurationSpecList.add(productConfigurationSpec);
		}
		this.productSpec.setProductConfiguration(productConfigurationSpecList);
		productSpecification.setProductConfiguration(productConfigurationSpecList);


	}

	private void handleProductConfigurationOnEmptyChar(List<ProductConfigurationSpec> productConfigurationSpecList, QueryService queryService, String stockItemTypeId, ProductSpecificationRef productSpecificationRef, ProductSpecification productSpecification) {
		List<StockItem> stockItems = queryService.getStockItemByStockItemTypeId(stockItemTypeId, accessToken);
		List<StockItem> stockItemsWithEmptyCharacteristics = stockItems.stream().filter(x->ObjectUtils.isEmpty(x.getStockItemCharacteristics())).collect(Collectors.toList());
		if(!ObjectUtils.isEmpty(stockItemsWithEmptyCharacteristics)) {
			stockItemsWithEmptyCharacteristics.forEach(
					x -> {
						ProductConfigurationSpec productConfigurationSpec = new ProductConfigurationSpec();
						productConfigurationSpec.productSpecification(productSpecificationRef);
						productConfigurationSpec.id(UUID.randomUUID().toString());
						productConfigurationSpec.setIsSellable(false);
						productConfigurationSpec.setStockItemRef(new StockItemRef().id(x.getId()).name(x.getName()).validFor(x.getValidFor()));
						productConfigurationSpec.setDefinedBy(new ArrayList<>());
						productConfigurationSpecList.add(productConfigurationSpec);
						return;
					}

			);
		}
		this.productSpec.setProductConfiguration(productConfigurationSpecList);
		productSpecification.setProductConfiguration(productConfigurationSpecList);

	}

	/**
	 * updates the state of aggregate after applying
	 * ComputeProductConfigurationEvent.
	 *
	 * @param event : ComputeProductConfigurationEvent
	 */
	@EventSourcingHandler
	public void on(final ComputeProductConfigurationEvent event) {
		this.productSpecID = event.getProdSpecId();
		this.productSpec.setProductConfiguration(event.getProductConfiguration());
	}

	/**
	 * This method will compute configurations of PS
	 *
	 * @param productSpecificationCharacteristics
	 * @return two dimensional String list
	 */
	public List<List<String>> ComputeProductConfiguration(
			List<ProductSpecificationCharacteristic> productSpecificationCharacteristics) {

		HashMap<String, HashSet<String>> productSpecCharValuesMap = new HashMap<>();

		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : productSpecificationCharacteristics) {
			List<ProductSpecificationCharacteristicValue> characteristicValueList = productSpecificationCharacteristic
					.getProductSpecCharacteristicValue();
			if (!ObjectUtils.isEmpty(characteristicValueList)) {
				String id = productSpecificationCharacteristic.getId();
				productSpecCharValuesMap.put(id, new HashSet<>());
				for (ProductSpecificationCharacteristicValue productSpecificationCharacteristicValue : characteristicValueList) {
					HashSet<String> charValueSet = productSpecCharValuesMap.get(id);
					charValueSet.add(productSpecificationCharacteristicValue.getValue());
				}
			}
		}

		List<List<String>> prodSpecCharValues = new ArrayList<>();
		for (Map.Entry<String, HashSet<String>> entry : productSpecCharValuesMap.entrySet()) {
			prodSpecCharValues.add(new ArrayList<>(entry.getValue()));
		}

		return combinationGenerator(prodSpecCharValues, 0);
	}

	/**
	 * Compute Combinations of PS Characteristics
	 *
	 * @param input
	 * @param i
	 * @return two dimensional String list
	 */
	public List<List<String>> combinationGenerator(List<List<String>> input, int i) {

		if (i == input.size()) {
			List<List<String>> result = new ArrayList<>();
			result.add(new ArrayList<>());
			return result;
		}

		List<List<String>> result = new ArrayList<>();
		List<List<String>> recursive = combinationGenerator(input, i + 1);

		for (int j = 0; j < input.get(i).size(); j++) {
			for (List<String> strings : recursive) {
				List<String> newList = new ArrayList<>(strings);
				newList.add(input.get(i).get(j));
				result.add(newList);
			}
		}
		return result;
	}

	/**
	 * This method will process CFS usage specification.
	 *
	 * @param command : usage specifications
	 *
	 */
	public List<UsageSpecification> processUsageSpecificaion(List<UsageSpecification> receivedUsageSpecs,
															 List<UsageSpecification> serviceUsageSpecs) {
		LOGGER.info("processUsageSpecificaion : receivedUsageSpecs {}", receivedUsageSpecs);
		String productSpecId = this.productSpec.getId();
		AggregateLifecycle.apply(new ProductSpecUsageSelectedEvent(productSpecId, receivedUsageSpecs));

		if ((serviceUsageSpecs == null || serviceUsageSpecs.isEmpty()) && !receivedUsageSpecs.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_USAGE_SELECTED);
		}

		List<UsageSpecification> usageSelected = new ArrayList<>();
		List<UsageSpecification> invalidUsageSelected = new ArrayList<>();
		for (UsageSpecification receivedUsageSpec : receivedUsageSpecs) {
			boolean isValid = false;
			for (UsageSpecification serviceUsageSpec : serviceUsageSpecs) {
				if (receivedUsageSpec.getId().equals(serviceUsageSpec.getId()) && TimePeriodValidityUtil
						.checkTimePeriodRestriction(receivedUsageSpec.getValidFor(), serviceUsageSpec.getValidFor())
						.isIsValid()) {
					isValid = true;
					receivedUsageSpec.id(UUID.randomUUID().toString() + "." + receivedUsageSpec.getId());
					usageSelected.add(receivedUsageSpec);
					break;
				}
			}
			if (!isValid) {
				invalidUsageSelected.add(receivedUsageSpec);
			}
		}
		if (!invalidUsageSelected.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_USAGE_SELECTED);
		}
		return usageSelected;
	}

	/**
	 * updates the state of aggregate after applying ProductSpecUsageSelectedEvent.
	 *
	 * @param event : ProductSpecUsageSelectedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecUsageSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * Process.
	 *
	 * @param command the command
	 *
	 */

	@CommandHandler
	public void processProductSpecRelCommand(ProductSpecRelCommand command, QueryService queryService) {
		LOGGER.info("In processProductSpecRelCommand Method --> ProductSpecRelCommand : {} ", command);
		List<CFSRelationshipRestriction> cfsRelationship = queryService.fetchCFSRelationship(accessToken);
		String productSpecId = command.getProductSpecId();
		ProductSpecification currentProductSpec = queryService.fetchProductSpecById(productSpecId,accessToken);
		boolean restrict = (cfsRelationship!=null && !cfsRelationship.isEmpty()) && cfsRelationship.get(0).isRelationshipRestricted();

		if (restrict) {
			validateRestrictedRelationships(command.getProductSpecificationRelationships(), queryService,currentProductSpec);
		}

		ProductSpecification currentProductSpecificationRecreated = buildCurrentProductSpecification(command.getProductSpecificationRelationships(), queryService,currentProductSpec);

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecRelationDefinedEvent(
				this.productSpec.getId(),
				currentProductSpecificationRecreated.getProductSpecificationRelationship(),
				lastUpdate,
				command.getPolicyRuleRef()
		));
	}

	private void validateRestrictedRelationships(List<ProductSpecificationRelationship> productSpecificationRelationships, QueryService queryService,ProductSpecification productSpec) {
		Set<String> productSpecificationRelationshipTypes = new HashSet<>();
		Set<String> cfsRelationshipIdstarget = collectTargetIds(productSpecificationRelationships, queryService, productSpecificationRelationshipTypes);


		List<ServiceSpecificationRef> serviceSpecificationRefs = productSpec.getServiceSpecification();
		Set<String> cfsRelationshipType = new HashSet<>();
		Set<String> cfsRelationshipId = new HashSet<>();

		validateExistingRelationships(serviceSpecificationRefs, queryService, cfsRelationshipIdstarget, cfsRelationshipType, cfsRelationshipId);

		for (String psRelationType : productSpecificationRelationshipTypes) {
			if (!cfsRelationshipType.contains(psRelationType)) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_RELATIONSHIP);
			}
		}
	}

	private Set<String> collectTargetIds(List<ProductSpecificationRelationship> productSpecificationRelationships,
										 QueryService queryService,
										 Set<String> productSpecificationRelationshipTypes) {
		Set<String> cfsRelationshipIdstarget = new HashSet<>();
		for (ProductSpecificationRelationship productSpecificationRelationship : productSpecificationRelationships) {
			String targetpsid = productSpecificationRelationship.getId();
			ProductSpecification targetpsid2 = queryService.fetchProductSpecById(targetpsid,accessToken);
			List<ServiceSpecificationRef> serviceSpecificationRefss = targetpsid2.getServiceSpecification();
			for (ServiceSpecificationRef serviceSpecificationtarget : serviceSpecificationRefss) {
				cfsRelationshipIdstarget.add(serviceSpecificationtarget.getId());
			}
			String productSpecificationRelationshipTypess = productSpecificationRelationship.getRelationshipType().getValue();
			productSpecificationRelationshipTypes.add(productSpecificationRelationshipTypess);
		}
		return cfsRelationshipIdstarget;
	}

	private void validateExistingRelationships(List<ServiceSpecificationRef> serviceSpecificationRefs,
											   QueryService queryService,
											   Set<String> cfsRelationshipIdstarget,
											   Set<String> cfsRelationshipType,
											   Set<String> cfsRelationshipId) {
		for (ServiceSpecificationRef serviceSpecificationRef : serviceSpecificationRefs) {
			ServiceSpecification serviceSpecification = queryService.getServiceSpecById(serviceSpecificationRef.getId(),accessToken);
			for (ServiceSpecRelationship serviceSpecRelationship : serviceSpecification.getServiceSpecRelationship()) {
				String relationshipId = serviceSpecRelationship.getId();
				String relationshipType = serviceSpecRelationship.getType();
				cfsRelationshipType.add(relationshipType);
				cfsRelationshipId.add(relationshipId);
				if (!cfsRelationshipIdstarget.contains(relationshipId) || cfsRelationshipIdstarget.isEmpty()) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_RELATIONSHIP);
				}
			}
		}
	}

	private ProductSpecification buildCurrentProductSpecification(List<ProductSpecificationRelationship> requestRelationships, QueryService queryService,ProductSpecification productSpec) {
		ProductSpecification currentProductSpecification = new ProductSpecification();
		List<ProductSpecificationRelationship> productSpecificationRelationships = new ArrayList<>();
		List<String> productSpecIds = requestRelationships.stream()
				.map(ProductSpecificationRelationship::getId)
				.collect(Collectors.toList());
		List<ProductSpecification> productSpecs = queryService.fetchProductSpecifications(productSpecIds.stream().collect(Collectors.joining(",")),accessToken);
		if(productSpecs==null){
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_ID,null,productSpecIds.toString());
		}
		Map<String, ProductSpecification> productSpecificationMap = productSpecs.stream()
				.collect(Collectors.toMap(ProductSpecification::getId, Function.identity()));

		for (ProductSpecificationRelationship productSpecificationRelationship : requestRelationships) {
			String productSpecificationRelationshipId = productSpecificationRelationship.getId();
			if (productSpecificationRelationshipId == null) {
				continue;
			}
			ProductSpecification previousProductSpecification = productSpecificationMap.get(productSpecificationRelationshipId);
			if (previousProductSpecification == null) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_ID,null,productSpecificationRelationshipId);
			}
			ProductSpecificationRelationship currentProductSpecificationRelationship = new ProductSpecificationRelationship();
			currentProductSpecificationRelationship.setId(productSpecificationRelationshipId);
			//PS Relationship ValidFor validation check
			validatePSRelationshipValidity(productSpecificationRelationship.getValidFor(), productSpec.getValidFor(), productSpec.getId());
			currentProductSpecificationRelationship.setValidFor(productSpecificationRelationship.getValidFor());
			currentProductSpecificationRelationship.setRelationshipType(productSpecificationRelationship.getRelationshipType());
			productSpecificationRelationships.add(currentProductSpecificationRelationship);
		}
		currentProductSpecification.setProductSpecificationRelationship(productSpecificationRelationships);
		return currentProductSpecification;
	}
	private void validatePSRelationshipValidity(TimePeriod relationshipValidity, TimePeriod psCreated,String productSpecID) {
		OffsetDateTime now = OffsetDateTime.now();

		//startTime not null is checked at POJO level

		// Check if endTime is before startTime (strict)
		if (relationshipValidity.getEndDateTime() != null &&
				!relationshipValidity.getEndDateTime().isAfter(relationshipValidity.getStartDateTime())) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_VALIDFOR_STARTTIME_ENDTIME );
		}

		// Check if now is greater than startTime
		if (now.isAfter(relationshipValidity.getStartDateTime())) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_VALIDFOR_BEFORE_CURRENT_TIME );
		}

		// Check if relationshipValidity is greater than or equal to psCreated  start times
		if (psCreated.getStartDateTime().isAfter(relationshipValidity.getStartDateTime())) {
			throw new DiscoManagedClientException(DISCO_PS_RELATIONSHIP_INVALID_VALIDFOR_STARTTIME,productSpecID, productSpecID);
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecRelationDefinedEvent.
	 *
	 * @param event : ProductSpecRelationDefinedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecRelationDefinedEvent event) {
		this.productSpec.productSpecificationRelationship(event.getProductSpecRelationships())
				.policyRuleRef(event.getPolicyRuleRef());
	}

	/**
	 * Process.
	 *
	 * @param command the command
	 *
	 */
	public void processValidFor(TimePeriod input, TimePeriod restriction) {
		LOGGER.info("ProcessValidFor Method --> ProductSpecValidForCommand : {} ", input);
		AggregateLifecycle.apply(new ProductSpecValidForSelectedEvent(this.productSpecID, input));
		Validation validation = TimePeriodValidityUtil.checkTimePeriodRestriction(input, restriction);
		if (!validation.isIsValid()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE, input.toString(), validation.getReason());
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecValidForSelectedEvent.
	 *
	 * @param event : ProductSpecValidForSelectedEvent
	 */
	@EventSourcingHandler
	public void apply(ProductSpecValidForSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * Process related party of ps
	 *
	 * @param command : parties
	 *
	 */
	public List<RelatedParty> processRelatedParty(List<RelatedParty> parties) {
		LOGGER.info("ProcessRelatedParty Method called with parties: {}", parties);
		String productSpecId = this.productSpec.getId();
		AggregateLifecycle.apply(new ProductSpecRelatedPartySelectedEvent(productSpecId, parties));

		List<RelatedParty> partiesSelected = new ArrayList<>();
		List<Event> eventList = new ArrayList<>();
		if (null != parties) {
			Map<String, List<Object>> partyMap = getRelatedParty(parties, eventList);
			List<Object> selectedParty = partyMap.get(PARTIES);
			partiesSelected = selectedParty.stream().map(element -> (RelatedParty) element)
					.collect(Collectors.toList());

			List<Object> selectedPartyEvent = partyMap.get(EVENTLIST);
			eventList = selectedPartyEvent.stream().map(element -> (Event) element).collect(Collectors.toList());

			if (partiesSelected.isEmpty() && !eventList.isEmpty()) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_PARTY_SELECTED);
			}
		}

		return partiesSelected;
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecRelatedPartySelectedEvent.
	 *
	 * @param event : ProductSpecRelatedPartySelectedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecRelatedPartySelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * Process related resource
	 *
	 * @param command the command
	 *
	 */
	public List<RelatedResource> processRelatedResource(List<RelatedResource> resources,
														ServiceSpecification serviceSpec) {
		LOGGER.info("ProcessRelatedResource Method --> ProductSpecRelResourceCommand");
		String productSpecId = this.productSpec.getId();
		List<RelatedResource> resourcesSelected = new ArrayList<>();
		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)
				&& serviceSpec.getRelatedResource() != null) {
			List<RelatedResource> resourceSpecs = serviceSpec.getRelatedResource();
			List<Event> eventList = new ArrayList<>();
			if (null != resources && null != resourceSpecs) {
				Map<String, List<Object>> resourceMap = getResource(resources, resourceSpecs, eventList);
				List<Object> selectedResource = resourceMap.get(RESOURCE);
				resourcesSelected = selectedResource.stream().map(element -> (RelatedResource) element)
						.collect(Collectors.toList());

				List<Object> selectedResourceEvent = resourceMap.get(EVENTLIST);
				eventList = selectedResourceEvent.stream().map(element -> (Event) element).collect(Collectors.toList());

				if (resourcesSelected.isEmpty() && !eventList.isEmpty()) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_PARTYROLE_SELECTED);
				}
			}
		} else if (this.productSpec.getSupportEntity().equals(SupportEntity.STOCKITEMTYPE)) {
			resourcesSelected = resources;
		}
		AggregateLifecycle.apply(new ProductSpecRelResourceSelectedEvent(productSpecId, resources));
		return resourcesSelected;
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecRelResourceSelectedEvent.
	 *
	 * @param event : ProductSpecRelResourceSelectedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecRelResourceSelectedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * This process method validates the product specification and sets its
	 * lifecycle status to INTEST and creates a new version number.
	 *
	 * @param command the command : ProductSpecValidatedCommand
	 *
	 */
	@CommandHandler
	public void processProductSpecValidatedCommand(ProductSpecValidatedCommand command, QueryService queryService) {
		LOGGER.info("In processProductSpecValidatedCommand Method --> ProductSpecValidatedCommand : {} ", command);
		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			String serviceSpecId = this.productSpec.getServiceSpecification().get(0).getId();
			ServiceSpecification serviceSpec = queryService.getServiceSpecById(serviceSpecId, accessToken);
			Event event = verifyServiceSpecState(serviceSpec);
			if (event instanceof ServiceSpecStateVerificationFailedEvent) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpec.getId(), null);
			}
			AggregateLifecycle.apply(event);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecValidatedEvent(this.productSpec.getId(),
				ProductSpecificationLifecycle.INTEST, lastUpdate));
		this.productSpec.lifecycleStatus(ProductSpecificationLifecycle.INTEST);

		AggregateLifecycle.apply(new ProductSpecVersionCreatedEvent(this.productSpec.getId(), VERSION, lastUpdate));
		this.productSpec.version(VERSION);
		AggregateLifecycle.apply(new ProductSpecCreationCompletedEvent(command.getProductSpecId(), this.productSpec));
	}

	/**
	 * updates the state of aggregate after applying ProductSpecValidatedEvent.
	 *
	 * @param event : ProductSpecValidatedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecValidatedEvent event) {
		this.productSpecID = event.getProductSpecificationId();
		this.productSpec.lifecycleStatus(event.getLifecycleStatus());
	}

	/**
	 * updates the state of aggregate after applying ProductSpecVersionCreatedEvent.
	 *
	 * @param event : ProductSpecVersionCreatedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecVersionCreatedEvent event) {
		this.productSpecID = event.getProductSpecificationId();
		this.productSpec.version(event.getProductSpecificationVersion());
	}

	/**
	 * This method generate the event to remove the incomplete productSpec which
	 * have lifecycle inStudy.
	 *
	 * @param command the command
	 *
	 */
	@CommandHandler
	public void processProductSpecCancelCommand(ProductSpecCancelCommand command) {
		LOGGER.info("In processProductSpecCancelCommand Method --> ProductSpecCancelCommand : {} ", command);
		if (!isProductSpecStateValid(this.productSpec.getLifecycleStatus())) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_LIFECYCLE);
		}
		AggregateLifecycle.apply(new ProductSpecCancelledEvent(command.getProductSpecId(), this.productSpec));
	}

	/**
	 * updates the state of aggregate after applying ProductSpecCancelledEvent. e
	 *
	 * @param event : ProductSpecCancelledEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecCancelledEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * Verify service spec state.
	 *
	 * @param serviceSpec the service spec
	 * @return the event
	 */
	private Event verifyServiceSpecState(ServiceSpecification serviceSpec) {
		String serviceSpecId = serviceSpec.getId();
		String serviceSpecLifeCycleStatus = serviceSpec.getLifecycleStatus();
		if (!(ResourceState.ACTIVE.getResourceState().equalsIgnoreCase(serviceSpecLifeCycleStatus)
				|| ResourceState.LAUNCHED.getResourceState().equalsIgnoreCase(serviceSpecLifeCycleStatus))) {
			return new ServiceSpecStateVerificationFailedEvent(serviceSpecId,
					ServiceSpecLifeCycleEnum.from(serviceSpec.getLifecycleStatus()));
		} else {
			return new ServiceSpecStateVerifiedEvent(this.productSpecID, serviceSpecId,
					serviceSpec.getLifecycleStatus());
		}
	}

	/**
	 * Verify stock item state.
	 *
	 * @param stockItem
	 * @return the event
	 */
	private Event verifyStockItemState(StockItem stockItem) {
		String stockItemId = stockItem.getId();
		String stockItemLifeCycleStatus = stockItem.getState();
		if (!(ResourceState.ACTIVE.getResourceState().equalsIgnoreCase(stockItemLifeCycleStatus)
				|| ResourceState.LAUNCHED.getResourceState().equalsIgnoreCase(stockItemLifeCycleStatus))) {
			return new StockItemStateVerificationFailedEvent(stockItemId,
					StockItemLifeCycleEnum.from(stockItemLifeCycleStatus));
		} else {
			return new StockItemStateVerifiedEvent(this.productSpecID, stockItemId,
					StockItemLifeCycleEnum.from(stockItemLifeCycleStatus));
		}
	}

	/**
	 * Gets the related party.
	 *
	 * @param parties   the parties
	 * @param eventList the event list
	 * @return the related party
	 */
	private Map<String, List<Object>> getRelatedParty(List<RelatedParty> parties, List<Event> eventList) {

		Map<String, List<Object>> returnMap = new HashMap<>();
		String productSpecId = this.productSpec.getId();
		List<RelatedParty> partiesSelected = new ArrayList<>();
		Set<String> invalidPartySelected = new HashSet<>();

		for (RelatedParty party : parties) {
			if (null != party.getReferredType()) {
				if (null != party.getRole()) {
					partiesSelected.add(party);
				} else {
					eventList.add(new InvalidPartyRoleSelectedEvent(productSpecId, party.getRole()));
					invalidPartySelected.add(party.getRole());
				}
			}
		}

		if (!invalidPartySelected.isEmpty()) {
			eventList.add(new InvalidPartySelectedEvent(productSpecId, invalidPartySelected));
			partiesSelected.clear();
		}

		returnMap.put(PARTIES, List.copyOf(partiesSelected));
		returnMap.put(EVENTLIST, List.copyOf(eventList));
		return returnMap;
	}

	/**
	 * Gets the resource.
	 *
	 * @param resources     the resources
	 * @param resourceSpecs the resource specs
	 * @param eventList     the event list
	 * @return the resource
	 */
	private Map<String, List<Object>> getResource(List<RelatedResource> resources, List<RelatedResource> resourceSpecs,
												  List<Event> eventList) {
		Map<String, List<Object>> returnMap = new HashMap<>();
		List<RelatedResource> resourcesSelected = new ArrayList<>();
		String productSpecId = this.productSpec.getId();

		for (RelatedResource resource : resources) {
			if (resource.getRole() == null) {
				return handleMissingRole(productSpecId, resource, eventList, returnMap);
			}
			updateMissingResourceFields(resource, resourceSpecs);
			resourcesSelected.add(resource);
		}

		returnMap.put(RESOURCE, List.copyOf(resourcesSelected));
		returnMap.put(EVENTLIST, List.copyOf(eventList));
		return returnMap;
	}

	/**
	 * Updates missing fields (name or id) of a resource using the resourceSpecs list.
	 *
	 * @param resource      the resource to update
	 * @param resourceSpecs the list of resource specifications
	 */
	private void updateMissingResourceFields(RelatedResource resource, List<RelatedResource> resourceSpecs) {
		if (resource.getName() != null && resource.getId() != null) {
			return;
		}

		for (RelatedResource resourceSpec : resourceSpecs) {
			if (resource.getName() == null && resource.getId().equals(resourceSpec.getId())) {
				resource.setName(resourceSpec.getName());
			}
			if (resource.getId() == null && resource.getName().equals(resourceSpec.getName())) {
				resource.setId(resourceSpec.getId());
			}
		}
	}

	/**
	 * Handles the case where a resource is missing a role.
	 *
	 * @param productSpecId the product specification ID
	 * @param resource      the invalid resource
	 * @param eventList     the list of events
	 * @param returnMap     the return map to store results
	 * @return the updated return map
	 */
	private Map<String, List<Object>> handleMissingRole(String productSpecId, RelatedResource resource,
														List<Event> eventList, Map<String, List<Object>> returnMap) {
		eventList.add(new InvalidPartyRoleSelectedEvent(productSpecId, resource.getRole()));
		returnMap.put(RESOURCE, List.of());
		returnMap.put(EVENTLIST, List.copyOf(eventList));
		return returnMap;
	}


	/**
	 * Check the product spec lifeCycleState; if lifeCycle is inStudy return true,
	 * otherwise false.
	 *
	 * @param lifeCycleState the life cycle state
	 * @return the boolean
	 */
	private boolean isProductSpecStateValid(ProductSpecificationLifecycle lifeCycleState) {
		return lifeCycleState.equals(ProductSpecificationLifecycle.INSTUDY);
	}

	/**
	 * This method will generate events in DB
	 *
	 * @param command
	 * @param queryService
	 */
	@CommandHandler
	public void processProductSpecModificationCommand(ProductSpecModificationCommand command,
													  QueryService queryService) {
		LOGGER.info("In processProductSpecModificationCommand Method --> ProductSpecModificationCommand : {}", command);

		this.productSpecID = command.getProductSpecId();
		ProductSpecification productSpec = fetchProductSpecification(queryService, this.productSpecID);

		if (productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			processServiceSpecification(queryService, productSpec);
		} else {
			processStockItem(queryService, productSpec);
		}

		validateAndApplyProductSpecEvents(productSpec);
	}

	private ProductSpecification fetchProductSpecification(QueryService queryService, String productSpecId) {
		ProductSpecification productSpecificationObj = queryService.fetchProductSpecById(productSpecId, accessToken);
		if (productSpecificationObj == null) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_NOTFOUND, productSpecId, null);
		}
		return productSpecificationObj;
	}

	private void processServiceSpecification(QueryService queryService, ProductSpecification productSpec) {
		ServiceSpecification serviceSpec = queryService.getServiceSpecById(
				productSpec.getServiceSpecification().get(0).getId(), accessToken);
		Event event = verifyServiceSpecState(serviceSpec);
		if (event instanceof ServiceSpecStateVerificationFailedEvent) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpec.getId(), null);
		}
		AggregateLifecycle.apply(event);
	}

	private void processStockItem(QueryService queryService, ProductSpecification productSpec) {
		String stockItemTypeId = productSpec.getStockItemType().getId();
		StockItemType stockItemType = queryService.getStockItemTypeById(stockItemTypeId, accessToken);
		if (stockItemType == null) {
			throw new DiscoManagedClientException(DISCO_PS_STOCK_ITEM_NOT_FOUND, stockItemTypeId, null);
		}
		for (StockItem stockItem : queryService.getStockItemByStockItemTypeId(stockItemTypeId, accessToken)) {
			applyStockItemEvent(stockItem);
		}
	}

	private void applyStockItemEvent(StockItem stockItem) {
		Event event = verifyStockItemState(stockItem);
		if (event instanceof StockItemStateVerificationFailedEvent) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_STATE, stockItem.getId(), null);
		}
		AggregateLifecycle.apply(event);
	}

	private void validateAndApplyProductSpecEvents(ProductSpecification productSpec) {
		if (isValidLifecycleStatus(productSpec.getLifecycleStatus())) {
			AggregateLifecycle.apply(new ProductSpecStatusVerifiedEvent(productSpec.getId(),
					productSpec.getLifecycleStatus()));
			AggregateLifecycle.apply(new ProductSpecModificationInitiatedEvent(productSpec.getId(),
					productSpec, OffsetDateTime.now()));
		} else {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_LIFECYCLE_INTEST);
		}
	}

	private boolean isValidLifecycleStatus(ProductSpecificationLifecycle lifecycleStatus) {
		return lifecycleStatus.equals(ProductSpecificationLifecycle.INTEST) ||
				lifecycleStatus.equals(ProductSpecificationLifecycle.ACTIVE) ||
				lifecycleStatus.equals(ProductSpecificationLifecycle.LAUNCHED) ||
				lifecycleStatus.equals(ProductSpecificationLifecycle.RETIRED);
	}


	/**
	 * updates the state of aggregate after applying ServiceSpecStateVerifiedEvent.
	 *
	 * @param event : ServiceSpecStateVerifiedEvent
	 */
	@EventSourcingHandler
	public void on(final ServiceSpecStateVerifiedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * updates the state of aggregate after applying ProductSpecStatusVerifiedEvent.
	 *
	 * @param event : ProductSpecStatusVerifiedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecStatusVerifiedEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecModificationInitiatedEvent.
	 *
	 * @param event : ProductSpecModificationInitiatedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecModificationInitiatedEvent event) {
		this.productSpec = event.getProductSpec();
	}

	/**
	 * This method will generate events to modify description in
	 * ProductSpecification.
	 *
	 * @param command
	 * @param queryService
	 */
	@CommandHandler
	public void processModifyProductSpecDescribeCommand(ModifyDefineIdentityProductSpecCommand command,
														QueryService queryService) {
		LOGGER.info("In processModifyProductSpecDescribeCommand Method --> ModifyProductSpecDescribeCommand : {}", command);

		String productSpecId = this.productSpec.getId();
		ProductSpecification storedProductSpecification = fetchStoredProductSpecification(queryService, productSpecId);

		ProductSpecificationLifecycle lifecycleState = determineLifecycleState(command, storedProductSpecification);
		validateLifecycleState(storedProductSpecification.getLifecycleStatus(), lifecycleState, productSpecId);

		if (productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			processCFSSpec(command, queryService, productSpecId, lifecycleState, storedProductSpecification.getLifecycleStatus());
		} else {
			processStockItemSpec(command, queryService, productSpecId, lifecycleState, storedProductSpecification.getLifecycleStatus());
		}
	}

	private ProductSpecification fetchStoredProductSpecification(QueryService queryService, String productSpecId) {
		ProductSpecification storedProductSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
		if (storedProductSpecification == null) {
			throw new DiscoClientException("Product Specification with id: " + productSpec + NOT_FOUND_MSG);
		}
		return storedProductSpecification;
	}

	private ProductSpecificationLifecycle determineLifecycleState(ModifyDefineIdentityProductSpecCommand command,
																  ProductSpecification storedProductSpecification) {
		return command.getLifecycleStatus() != null ? command.getLifecycleStatus()
				: storedProductSpecification.getLifecycleStatus();
	}

	private void validateLifecycleState(ProductSpecificationLifecycle currentLifecycleState,
										ProductSpecificationLifecycle newLifecycleState, String productSpecId) {
		if (currentLifecycleState.equals(ProductSpecificationLifecycle.LAUNCHED)
				|| currentLifecycleState.equals(ProductSpecificationLifecycle.RETIRED)) {
			if (currentLifecycleState.equals(newLifecycleState)) {
				throw new DiscoManagedClientException(ProductSpecConstants.DISCO_PS_INVALID_PS_LIFECYCLE);
			}
			if (currentLifecycleState.equals(ProductSpecificationLifecycle.RETIRED)) {
				isValidStatus(newLifecycleState, productSpecId);
			}
		}
	}

	private void processCFSSpec(ModifyDefineIdentityProductSpecCommand command, QueryService queryService,
								String productSpecId, ProductSpecificationLifecycle lifecycleState,
								ProductSpecificationLifecycle currentLifecycleState) {

		ServiceSpecification serviceSpecification = queryService
				.getServiceSpecById(this.productSpec.getServiceSpecification().get(0).getId(), accessToken);
		Event event = verifyServiceSpecState(serviceSpecification);

		if (event instanceof ServiceSpecStateVerificationFailedEvent) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE, serviceSpecification.getId(), null);
		}
		AggregateLifecycle.apply(event);

		validateLifecycleForEvent(this.productSpec.getLifecycleStatus());
		applyCFSSpecEvents(command, productSpecId, lifecycleState, currentLifecycleState, serviceSpecification);
	}

	private void validateLifecycleForEvent(ProductSpecificationLifecycle lifecycleStatus) {
		if (!(lifecycleStatus.equals(ProductSpecificationLifecycle.INTEST)
				|| lifecycleStatus.equals(ProductSpecificationLifecycle.ACTIVE)
				|| lifecycleStatus.equals(ProductSpecificationLifecycle.LAUNCHED)
				|| lifecycleStatus.equals(ProductSpecificationLifecycle.RETIRED))) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_LIFECYCLE_INTEST);
		}
	}

	private void applyCFSSpecEvents(ModifyDefineIdentityProductSpecCommand command, String productSpecId,
									ProductSpecificationLifecycle lifecycleState, ProductSpecificationLifecycle currentLifecycleState,
									ServiceSpecification serviceSpecification) {

		AggregateLifecycle.apply(new ProductSpecRelResourceSelectedEvent(productSpecId, command.getRelatedResource()));

		List<RelatedResource> selectRelatedResource = serviceSpecification.getRelatedResource();
		List<RelatedParty> selectRelatedParty = processModifyProductSpecRelatedParty(command.getRelatedParty());
		processModifyProductSpecValidForCommand(command.getValidFor(), serviceSpecification.getValidFor());

		AggregateLifecycle.apply(new ProductSpecDefineIdentityModifiedEvent(productSpecId, command.getDefineIdentityData(),
				selectRelatedParty, selectRelatedResource, command.getValidFor(), OffsetDateTime.now(),
				command.getType(), lifecycleState, currentLifecycleState));
	}

	private void processStockItemSpec(ModifyDefineIdentityProductSpecCommand command, QueryService queryService,
									  String productSpecId, ProductSpecificationLifecycle lifecycleState,
									  ProductSpecificationLifecycle currentLifecycleState) {

		String stockItemTypeId = this.productSpec.getStockItemType().getId();
		StockItemType stockItemType = queryService.getStockItemTypeById(stockItemTypeId, accessToken);

		if (stockItemType == null) {
			throw new DiscoManagedClientException(DISCO_PS_STOCK_ITEM_NOT_FOUND, stockItemTypeId, null);
		}

		verifyStockItemsState(queryService, stockItemTypeId);
		validateLifecycleForEvent(this.productSpec.getLifecycleStatus());
		applyStockItemSpecEvents(command, productSpecId, lifecycleState, currentLifecycleState, stockItemType);
	}

	private void verifyStockItemsState(QueryService queryService, String stockItemTypeId) {
		List<StockItem> stockItemList = queryService.getStockItemByStockItemTypeId(stockItemTypeId, accessToken);
		for (StockItem stockItem : stockItemList) {
			Event event = verifyStockItemState(stockItem);
			if (event instanceof StockItemStateVerificationFailedEvent) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_STOCKITEM_STATE);
			}
			AggregateLifecycle.apply(event);
		}
	}

	private void applyStockItemSpecEvents(ModifyDefineIdentityProductSpecCommand command, String productSpecId,
										  ProductSpecificationLifecycle lifecycleState, ProductSpecificationLifecycle currentLifecycleState,
										  StockItemType stockItemType) {

		List<RelatedResource> selectRelatedResource = command.getRelatedResource();
		List<RelatedParty> selectRelatedParty = processModifyProductSpecRelatedParty(command.getRelatedParty());
		processModifyProductSpecValidForCommand(command.getValidFor(), stockItemType.getValidFor());

		AggregateLifecycle.apply(new ProductSpecDefineIdentityModifiedEvent(productSpecId, command.getDefineIdentityData(),
				selectRelatedParty, selectRelatedResource, command.getValidFor(), OffsetDateTime.now(),
				command.getType(), lifecycleState, currentLifecycleState));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecDefineIdentityModifiedEvent.
	 *
	 * @param event : ProductSpecDefineIdentityModifiedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecDefineIdentityModifiedEvent event) {
		this.productSpec.brand(event.getDefineIdentityData().getBrand())
				.description(event.getDefineIdentityData().getDescription())
				.name(event.getDefineIdentityData().getName())
				.productNumber(event.getDefineIdentityData().getProductNumber()).relatedParty(event.getRelatedParty())
				.relatedResource(event.getRelatedResource()).validFor(event.getValidFor())
				.lifecycleStatus(event.getLifecycleStatus()).type(event.getType().toString());
	}

	/**
	 * This method will generate events to modify characteristics in
	 * ProductSpecification.
	 *
	 * @param command
	 * @param queryService
	 */

	@CommandHandler
	public void processModifyProductSpecCharacteristicCommand(ModifyProductSpecCharacteristicCommand command,
															  QueryService queryService) {
		LOGGER.info(
				"In processModifyProductSpecCharacteristicCommand Method --> ModifyProductSpecCharacteristicCommand : {}",
				command);
		String serviceSpecId = this.productSpec.getServiceSpecification().get(0).getId();
		ServiceSpecification serviceSpec = queryService.getServiceSpecById(serviceSpecId, accessToken);
		Event event = verifyServiceSpecState(serviceSpec);
		if (event instanceof ServiceSpecStateVerificationFailedEvent) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_SERVICESPEC_STATE);
		}
		AggregateLifecycle.apply(event);
		String productSpecId = this.productSpec.getId();
		List<ProductSpecificationCharacteristic> productSpecCharacteristics = command.getProductSpecCharacteristics();
		// characteritics
		List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics = new ArrayList<>();
		Set<String> invalidCfsCharacteristicsSelected = new HashSet<>();
		checkServiceSpecCharacteristicsHelper(productSpecCharacteristics, serviceSpec,
				selectedProductSpecificationCharacteristics, invalidCfsCharacteristicsSelected);
		if (!invalidCfsCharacteristicsSelected.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALUE, invalidCfsCharacteristicsSelected.toString(), null);
		}
		List<UsageSpecification> selectedUsageSpec = processModifyProductSpecUsageSpec(command.getUsageSpecifications(),
				serviceSpec.getUsageSpecification());
		AggregateLifecycle.apply(new ProductSpecCharacteristicsModifiedEvent(productSpecId,
				selectedProductSpecificationCharacteristics, selectedUsageSpec, OffsetDateTime.now()));

	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecCharacteristicsModifiedEvent.
	 *
	 * @param event : ProductSpecCharacteristicsModifiedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecCharacteristicsModifiedEvent event) {
		this.productSpecID = event.getProductSpecId();
		this.productSpec.setProductSpecCharacteristic(event.getProductSpecificationCharacteristics());
		this.productSpec.setUsageSpecification(event.getUsageSpecifications());
	}

	/**
	 * This method validates Product Specification Characteristics
	 *
	 * @param productSpecCharacteristics
	 * @param invalidCharacteristicsSelected
	 * @param serviceSpecCharacteristics
	 * @param selectedProductSpecificationCharacteristics
	 */
	private void validateCharacteristics(List<ProductSpecificationCharacteristic> productSpecCharacteristics,
										 Set<String> invalidCharacteristicsSelected, List<CharacteristicSpecification> serviceSpecCharacteristics,
										 List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics) {
		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : productSpecCharacteristics) {
			boolean isValid = false;
			for (CharacteristicSpecification serviceSpecCharacteristic : serviceSpecCharacteristics) {
				if (serviceSpecCharacteristic.getId().equals(productSpecificationCharacteristic.getId())) {
					isValid = true;
					productSpecificationCharacteristic
							.id(UUID.randomUUID().toString() + "." + productSpecificationCharacteristic.getId())
							.valueType(serviceSpecCharacteristic.getValueType())
							.configurable(serviceSpecCharacteristic.isConfigurable())
							.isUnique(serviceSpecCharacteristic.isIsUnique())
							.regex(serviceSpecCharacteristic.getRegex())
							.extensible(serviceSpecCharacteristic.isExtensible());
					selectedProductSpecificationCharacteristics.add(productSpecificationCharacteristic);
					break;
				}
			}
			if (!isValid) {
				invalidCharacteristicsSelected.add(productSpecificationCharacteristic.getId());
			}
		}
	}

	/**
	 * This method will generate events to modify usageSpecification in
	 * ProductSpecification.
	 *
	 * @param command     usage specifications
	 * @param serviceSpec usage specifications
	 */
	public List<UsageSpecification> processModifyProductSpecUsageSpec(List<UsageSpecification> receivedUsageSpecs,
																	  List<UsageSpecification> serviceUsageSpecs) {
		LOGGER.info("ProcessModifyProductSpecUsageSpec Method");
		if ((serviceUsageSpecs == null || serviceUsageSpecs.isEmpty()) && !receivedUsageSpecs.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_USAGE_SELECTED);
		}
		List<UsageSpecification> usageSelected = new ArrayList<>();
		List<UsageSpecification> invalidUsageSelected = new ArrayList<>();
		validateUsageSpec(receivedUsageSpecs, serviceUsageSpecs, usageSelected, invalidUsageSelected);
		if (!invalidUsageSelected.isEmpty()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_USAGE_SELECTED);
		}
		return usageSelected;
	}

	/**
	 * This method validates Usage Specification
	 *
	 * @param receivedUsageSpecs
	 * @param serviceUsageSpecs
	 * @param usageSelected
	 * @param invalidUsageSelected
	 */
	public void validateUsageSpec(List<UsageSpecification> receivedUsageSpecs,
								  List<UsageSpecification> serviceUsageSpecs, List<UsageSpecification> usageSelected,
								  List<UsageSpecification> invalidUsageSelected) {
		for (UsageSpecification receivedUsageSpec : receivedUsageSpecs) {
			boolean isValid = false;
			for (UsageSpecification serviceUsageSpec : serviceUsageSpecs) {
				if (receivedUsageSpec.getId().equals(serviceUsageSpec.getId()) && TimePeriodValidityUtil
						.checkTimePeriodRestriction(receivedUsageSpec.getValidFor(), serviceUsageSpec.getValidFor())
						.isIsValid()) {
					isValid = true;
					receivedUsageSpec.id(UUID.randomUUID().toString() + "." + receivedUsageSpec.getId());
					usageSelected.add(receivedUsageSpec);
					break;
				}
			}
			if (!isValid) {
				invalidUsageSelected.add(receivedUsageSpec);
			}
		}
	}

	/**
	 * This method will generate events to modify relationship in
	 * ProductSpecification.
	 *
	 * @param command
	 * @param queryService
	 */
    @CommandHandler
    public void processProductSpecRelCommand(ModifyProductSpecRelCommand command, QueryService queryService) {
        LOGGER.info("In processProductSpecRelCommand Method --> ProductSpecRelCommand : {} ", command);
        List<CFSRelationshipRestriction> cfsRelationship = queryService.fetchCFSRelationship(accessToken);
        String productSpecId = command.getProductSpecId();
        ProductSpecification currentProductSpec = queryService.fetchProductSpecById(productSpecId,accessToken);
        boolean restrict = cfsRelationship.get(0).isRelationshipRestricted();

        if (restrict) {
            validateRestrictedRelationships(command.getProductSpecificationRelationships(), queryService,currentProductSpec);
        }

        ProductSpecification currentProductSpecificationRecreated = buildCurrentProductSpecification(command.getProductSpecificationRelationships(), queryService,currentProductSpec);

        OffsetDateTime lastUpdate = OffsetDateTime.now();
        AggregateLifecycle.apply(new ProductSpecRelationModifiedEvent(this.productSpec.getId(),
                currentProductSpecificationRecreated.getProductSpecificationRelationship(), command.getPolicyRuleRef(),
                OffsetDateTime.now()));
    }

	private Set<String> collectTargetRelationshipData(ModifyProductSpecRelCommand command, QueryService queryService,
													  Set<String> productSpecificationRelationshipTypes) {
		Set<String> cfsRelationshipIdstarget = new HashSet<>();
		for (ProductSpecificationRelationship productSpecificationRelationship : command.getProductSpecificationRelationships()) {
			String targetpsid = productSpecificationRelationship.getId();
			ProductSpecification targetpsid2 = queryService.fetchProductSpecById(targetpsid,accessToken);
			List<ServiceSpecificationRef> serviceSpecificationRefss = targetpsid2.getServiceSpecification();
			for (ServiceSpecificationRef serviceSpecificationtarget : serviceSpecificationRefss) {
				cfsRelationshipIdstarget.add(serviceSpecificationtarget.getId());
			}
			String productSpecificationRelationshipTypess = productSpecificationRelationship.getRelationshipType().getValue();
			productSpecificationRelationshipTypes.add(productSpecificationRelationshipTypess);
		}
		return cfsRelationshipIdstarget;
	}

	private void validateExistingProductSpec(ModifyProductSpecRelCommand command, QueryService queryService,
											 Set<String> cfsRelationshipIdstarget, Set<String> productSpecificationRelationshipTypes) {
		String productSpecId = command.getProductSpecId();
		LOGGER.info("Product Spec ID: {}", productSpecId);
		ProductSpecification productSpec = queryService.fetchProductSpecById(productSpecId,accessToken);
		List<ServiceSpecificationRef> serviceSpecificationRefs = productSpec.getServiceSpecification();
		Set<String> cfsRelationshipType = new HashSet<>();
		Set<String> cfsRelationshipId = new HashSet<>();

		for (ServiceSpecificationRef serviceSpecificationRef : serviceSpecificationRefs) {
			ServiceSpecification serviceSpecification = queryService.getServiceSpecById(serviceSpecificationRef.getId(),accessToken);
			for (ServiceSpecRelationship serviceSpecRelationship : serviceSpecification.getServiceSpecRelationship()) {
				String relationshipId = serviceSpecRelationship.getId();
				String relationshipType = serviceSpecRelationship.getType();
				cfsRelationshipType.add(relationshipType);
				cfsRelationshipId.add(relationshipId);
				if (!cfsRelationshipIdstarget.contains(relationshipId) || cfsRelationshipIdstarget.isEmpty()) {
					throw new DiscoManagedClientException(DISCO_PS_INVALID_RELATIONSHIP);
				}
			}
		}

		for (String psRelationType : productSpecificationRelationshipTypes) {
			if (!cfsRelationshipType.contains(psRelationType)) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_RELATIONSHIP);
			}
		}
	}

	private void applyProductSpecRelationModified(ModifyProductSpecRelCommand command, QueryService queryService) {
		ProductSpecification currentProductSpecification = new ProductSpecification();
		List<ProductSpecificationRelationship> productSpecificationRelationships = new ArrayList<>();
		List<String> productSpecIds = command.getProductSpecificationRelationships().stream()
				.map(ProductSpecificationRelationship::getId).collect(Collectors.toList());
		List<ProductSpecification> productSpecs = queryService.fetchProductSpecifications(
				productSpecIds.stream().collect(Collectors.joining(",")),accessToken);
		Map<String, ProductSpecification> productSpecificationMap = productSpecs.stream()
				.collect(Collectors.toMap(ProductSpecification::getId, Function.identity()));

		for (ProductSpecificationRelationship productSpecificationRelationship : command.getProductSpecificationRelationships()) {
			String productSpecificationRelationshipId = productSpecificationRelationship.getId();
			if (productSpecificationRelationshipId == null) {
				continue;
			}
			ProductSpecification previousProductSpecification = productSpecificationMap.get(productSpecificationRelationshipId);
			if (previousProductSpecification == null) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_ID);
			}

			ProductSpecificationRelationship currentProductSpecificationRelationship = new ProductSpecificationRelationship();
			currentProductSpecificationRelationship.setId(productSpecificationRelationshipId);
			currentProductSpecificationRelationship.setRelationshipType(productSpecificationRelationship.getRelationshipType());
			productSpecificationRelationships.add(currentProductSpecificationRelationship);
		}

		currentProductSpecification.setProductSpecificationRelationship(productSpecificationRelationships);
		AggregateLifecycle.apply(new ProductSpecRelationModifiedEvent(this.productSpec.getId(),
				currentProductSpecification.getProductSpecificationRelationship(), command.getPolicyRuleRef(),
				OffsetDateTime.now()));
	}


	/**
	 * updates the state of aggregate after applying
	 * ProductSpecRelationModifiedEvent.
	 *
	 * @param event : ProductSpecRelationModifiedEvent
	 */
	@EventSourcingHandler
	public void on(ProductSpecRelationModifiedEvent event) {
		this.productSpec.productSpecificationRelationship(event.getProductSpecRelationships());
		this.productSpec.policyRuleRef(event.getPolicyRuleRef());
	}

	/**
	 * This method will generate events to modify related party in
	 * ProductSpecification.
	 *
	 * @param command related party
	 *
	 */
	public List<RelatedParty> processModifyProductSpecRelatedParty(List<RelatedParty> parties) {
		LOGGER.info("Process ModifyProductSpecRelatedParty");
		List<RelatedParty> partiesSelected = new ArrayList<>();
		List<Event> eventList = new ArrayList<>();
		if (null != parties) {
			Map<String, List<Object>> partyMap = getRelatedParty(parties, eventList);
			List<Object> selectedParty = partyMap.get(PARTIES);
			partiesSelected = selectedParty.stream().map(element -> (RelatedParty) element)
					.collect(Collectors.toList());

			List<Object> selectedPartyEvent = partyMap.get(EVENTLIST);
			eventList = selectedPartyEvent.stream().map(element -> (Event) element).collect(Collectors.toList());

			if (partiesSelected.isEmpty() && !eventList.isEmpty()) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_PARTYROLE_SELECTED);
			}
		}
		return partiesSelected;
	}

	/**
	 * This method will generate events to modify related resource in
	 * ProductSpecification.
	 *
	 * @param command
	 * @param queryService
	 */
	public List<RelatedResource> processModifyProductSpecRelResource(List<RelatedResource> resources,
																	 ServiceSpecification serviceSpec) {
		LOGGER.info("Process ModifyProductSpecRelResource Method");
		String productSpecId = this.productSpec.getId();
		AggregateLifecycle.apply(new ProductSpecRelResourceSelectedEvent(productSpecId, resources));
		List<RelatedResource> resourceSpecs = serviceSpec.getRelatedResource();
		List<RelatedResource> resourcesSelected = new ArrayList<>();
		List<Event> eventList = new ArrayList<>();
		if (null != resources && null != resourceSpecs) {
			Map<String, List<Object>> resourceMap = getResource(resources, resourceSpecs, eventList);
			List<Object> selectedResource = resourceMap.get(RESOURCE);
			resourcesSelected = selectedResource.stream().map(element -> (RelatedResource) element)
					.collect(Collectors.toList());

			List<Object> selectedResourceEvent = resourceMap.get(EVENTLIST);
			eventList = selectedResourceEvent.stream().map(element -> (Event) element).collect(Collectors.toList());

			if (resourcesSelected.isEmpty() && !eventList.isEmpty()) {
				throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_PARTYROLE_SELECTED);

			}
		}
		return resourcesSelected;
	}

	/**
	 * This method will generate events in DB after checking validFor time
	 *
	 * @param command     time period
	 * @param serviceSpec time period
	 */
	public void processModifyProductSpecValidForCommand(TimePeriod input, TimePeriod restriction) {
		LOGGER.info("ProcessModifyProductSpecValidFor Method");
		Validation validation = TimePeriodValidityUtil.checkTimePeriodRestriction(input, restriction);
		if (!validation.isIsValid()) {
			throw new DiscoManagedClientException(DISCO_PS_INVALID_PS_CHARACTERISTICS_SELECTED_VALIDFOR_RANGE,input.toString(), validation.getReason());
		}
	}

	/**
	 * This method will generate ProductSpecModificationValidatedEvent event in DB
	 *  original one
	 * @param command
	 * @param queryService
	 */
	@CommandHandler
	public void processProductSpecModificationValidatedCommand(ProductSpecModificationValidatedCommand command,
															   QueryService queryService) {
		ProductSpecification modifiedProductSpecification = this.productSpec;
		ProductSpecification storedProductSpecification = queryService
				.fetchProductSpecById(modifiedProductSpecification.getId(), accessToken);
		String version = storedProductSpecification.getVersion();
		storedProductSpecification.setId(UUID.randomUUID().toString());
		if(!modifiedProductSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.LAUNCHED)){
			if(modifiedProductSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.ACTIVE)){
				version = version.substring(0, 2) + (Integer.parseInt(version.substring(2)) + 1);
				modifiedProductSpecification.setVersion(version);
			}
			else if(storedProductSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.INTEST)){
			}else{
				version = version.substring(0, 2) + (Integer.parseInt(version.substring(2)) + 1);
				modifiedProductSpecification.setVersion(version);
			}
		}else if(modifiedProductSpecification.getLifecycleStatus().equals(ProductSpecificationLifecycle.LAUNCHED)){
			version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
		}
		AggregateLifecycle.apply(new ProductSpecModificationValidatedEvent(modifiedProductSpecification.getId(),
				modifiedProductSpecification.getLifecycleStatus(), OffsetDateTime.now(), version,
				storedProductSpecification));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecModificationValidatedEvent.
	 *
	 * @param event : ProductSpecModificationValidatedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecModificationValidatedEvent event) {
		this.productSpecID = event.getProductSpecificationId();
		this.productSpec.setVersion(event.getVersion());
	}

	/**
	 * This method will generate ProductSpecModificationCancelledEvent in DB
	 *
	 * @param productSpecCancelModificationCommand
	 *
	 */
	@CommandHandler
	public void processProductSpecCancelModificationCommand(
			ProductSpecCancelModificationCommand productSpecCancelModificationCommand) {
		LOGGER.info(
				"In processProductSpecCancelModificationCommand Method --> ProductSpecCancelModificationCommand : {}",
				productSpecCancelModificationCommand);
		ProductSpecification productSpecification = this.productSpec;
		AggregateLifecycle.apply(new ProductSpecModificationCancelledEvent(
				productSpecCancelModificationCommand.getProductSpecId(), productSpecification));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecModificationCancelledEvent.
	 *
	 * @param event : ProductSpecModificationCancelledEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecModificationCancelledEvent event) {
		this.productSpecID = event.getProductSpecId();
	}

	/**
	 * Process the Associate POP to Operation Specification Command to link the POP
	 * with Atomic Product Offering.
	 *
	 * @param command the command
	 * @author Varshika Choudhary
	 */
	@CommandHandler
	public void processAssociateProductSpecificationToStockItemCommand(
			final AssociateProductSpecificationToStockItemCommand command, QueryService queryService) {
		LOGGER.info(
				"In processAssociateProductSpecificationToStockItemCommand Method --> AssociateProductSpecificationToStockItemCommand : {}",
				command);
		String productSpecificationId = command.getProdSpecId();
		List<ProductConfigurationSpec> prodConfigList = this.productSpec.getProductConfiguration();
		associateStockItemHelper(productSpecificationId, prodConfigList, queryService);
		AggregateLifecycle.apply(new LinkProductSpecificationToStockItemEvent(productSpecificationId, prodConfigList,
				OffsetDateTime.now()));
	}

	/**
	 * this helper method will be used to associate stock item to
	 * productSpecConfigurations
	 *
	 * @param productSpecificationId
	 * @param prodConfigList
	 * @param queryService
	 */
	private void associateStockItemHelper(String productSpecificationId, List<ProductConfigurationSpec> prodConfigList,
										  QueryService queryService) {
		ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecificationId, accessToken);
		List<StockItem> stockItemList = queryService
				.getStockItemByStockItemTypeId(productSpecification.getStockItemType().getId(), accessToken);

		HashMap<String, String> referenceValueMap = new HashMap<>();
		for (ProductSpecificationCharacteristic productSpecificationCharacteristic : this.productSpec
				.getProductSpecCharacteristic()) {
			List<ProductSpecificationCharacteristicValue> characteristicValueList = productSpecificationCharacteristic
					.getProductSpecCharacteristicValue();
			for (ProductSpecificationCharacteristicValue characteristicValue : characteristicValueList) {
				referenceValueMap.put(characteristicValue.getValue(),
						characteristicValue.getCharacteristicReferenceValue());
			}
		}
		for (StockItem stockItem : stockItemList) {
			HashMap<String, String> stockItemMap = new HashMap<>();
			List<StockItemCharacteristicValue> stockItemCharacteristicValueList = stockItem
					.getStockItemCharacteristicValue();
			for (StockItemCharacteristicValue stockItemCharacteristicValue : stockItemCharacteristicValueList) {
				stockItemMap.put(stockItemCharacteristicValue.getStockItemCharacteristic().getId(),
						stockItemCharacteristicValue.getValue());
			}

			for (ProductConfigurationSpec productConfigurationSpec : prodConfigList) {
				HashMap<String, String> configurationMap = new HashMap<>();
				List<ProductConfSpecCharacteristicValue> definedBy = productConfigurationSpec.getDefinedBy();
				for (ProductConfSpecCharacteristicValue confSpecCharacteristicValue : definedBy) {
					ProductConfSpecCharacteristic confSpecCharacteristic = confSpecCharacteristicValue.getEnumerated();
					String confSpecCharId = confSpecCharacteristic.getId();
					String confSpecCharValue = confSpecCharacteristicValue.getValue();
					configurationMap.put(confSpecCharId, referenceValueMap.get(confSpecCharValue));
				}

				if (stockItemMap.equals(configurationMap)) {
					productConfigurationSpec.setIsSellable(true);
					productConfigurationSpec
							.setStockItemRef(new StockItemRef().id(stockItem.getId()).name(stockItem.getName()));
					break;
				}
			}
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * LinkProductSpecificationToStockItemEvent.
	 *
	 * @param event : LinkProductSpecificationToStockItemEvent
	 */
	@EventSourcingHandler
	public void on(final LinkProductSpecificationToStockItemEvent event) {
		this.productSpecID = event.getProductSpecificationId();
		this.productSpec.setProductConfiguration(event.getProductConfiguration());
	}

	/**
	 * This Method will Modify ProductSpecCharacteristics.
	 *
	 * @param command : ModifyStockItemProductSpecCharacteristicCommand
	 *
	 */
	@CommandHandler
	public void modifyProcess(ModifyStockItemProductSpecCharacteristicCommand command, QueryService queryService) {
		LOGGER.info("Method Process -> ModifyStockItemProductSpecCharacteristicCommand : {} ", command);
		List<ProductSpecificationCharacteristic> selectedProductSpecificationCharacteristics = new ArrayList<>();
		productSpecCharacteriticsHelper(this.productSpecID, command.getProductSpecCharacteristics(),
				selectedProductSpecificationCharacteristics, queryService);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductSpecCharacteristicsModifiedEvent(this.productSpecID,
				selectedProductSpecificationCharacteristics, new ArrayList<>(), lastUpdate));
	}

	/**
	 * This method will modify Product Configurations
	 *
	 * @param command      : ComputeProductConfigurationCommand
	 * @param queryService
	 */
	@CommandHandler
	public void modifyProcess(ModifyComputeProductConfigurationCommand command, QueryService queryService) {
		LOGGER.info("Modifying ProductConfigurations -> ModifyComputeProductConfigurationCommand : {} ", command);
		String productSpecificationId = command.getProductSpecification();
		List<ProductConfigurationSpec> productConfigurationSpecList = new ArrayList<>();
		computeProductConfigurationHelper(productSpecificationId, productConfigurationSpecList, queryService);
		AggregateLifecycle.apply(
				new ComputeProductConfigurationModificationEvent(productConfigurationSpecList, productSpecificationId));
	}

	/**
	 * modify the state of aggregate after applying
	 * ComputeProductConfigurationEvent.
	 *
	 * @param event : ComputeProductConfigurationEvent
	 */
	@EventSourcingHandler
	public void on(final ComputeProductConfigurationModificationEvent event) {
		this.productSpecID = event.getProdSpecId();
		this.productSpec.setProductConfiguration(event.getProductConfiguration());
	}

	/**
	 * Process to Modify the Associate POP to Operation Specification Command to
	 * link the POP with Atomic Product Offering.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void modifyProcess(final ModifyAssociateProductSpecificationToStockItemCommand command,
							  QueryService queryService) {
		LOGGER.info(
				"In processAssociateProductSpecificationToStockItemCommand Method --> AssociateProductSpecificationToStockItemCommand : {}",
				command);
		String productSpecificationId = command.getProdSpecId();
		List<ProductConfigurationSpec> prodConfigList = this.productSpec.getProductConfiguration();
		associateStockItemHelper(productSpecificationId, prodConfigList, queryService);
		AggregateLifecycle.apply(new LinkProductSpecificationToStockItemModificationEvent(productSpecificationId,
				prodConfigList, OffsetDateTime.now()));
	}

	/**
	 * updates the state of aggregate after applying
	 * LinkProductSpecificationToStockItemModificationEvent.
	 *
	 * @param event : LinkProductSpecificationToStockItemEvent
	 */
	@EventSourcingHandler
	public void on(final LinkProductSpecificationToStockItemModificationEvent event) {
		this.productSpecID = event.getProductSpecificationId();
		this.productSpec.setProductConfiguration(event.getProductConfiguration());
	}

	/**
	 *
	 * @param command : ProductSpecificationDeleteCommmand
	 * @return Event List
	 */
	@CommandHandler
	public ProductSpecAggregate(ProductSpecificationDeleteCommmand command) {
		this.productSpecID = command.getAggregateId();
		AggregateLifecycle.apply(new ProductSpecificationDeleteEvent(command.getAggregateId(),
				command.getLastUpdateDateTime(), command.getInterval(), command.getIntervalUnit()));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductSpecificationDeleteEvent.
	 *
	 * @param event : ProductSpecificationDeleteEvent
	 */
	@EventSourcingHandler
	public void on(final ProductSpecificationDeleteEvent event) {
		this.productSpecID = event.getAggregateId();
	}

	/*
	 *
	 * @param command : ProductSpecificationTemporaryDeleteCommand
	 * @return Event List
	 */
	public List<Event> processProductSpecificationTemporaryDeleteCommand(
			ProductSpecificationTemporaryDeleteCommand command) {
		LOGGER.info(
				"In processProductSpecificationTemporaryDeleteCommand Method --> ProductSpecificationTemporaryDeleteCommand : {}",
				command);
		List<Event> eventList = new ArrayList<>();
		eventList.add(new ProductSpecificationTemporaryDeleteEvent(UUID.randomUUID().toString()));
		return List.copyOf(eventList);
	}

	private Map<String, ArrayList<String>> validateVersioning(ProductSpecification modifiedProductSpecification,
															  ProductSpecification storedProductSpecification) {
		Map<String, ArrayList<String>> validatedData = new HashMap<>();
		validatedData.put(VersionType.MINOR.getValue(), new ArrayList<>());
		validatedData.put(VersionType.MAJOR.getValue(), new ArrayList<>());
		validatedData.put(VersionType.NOVERSION.getValue(), new ArrayList<>());
		addToMap(validateIdentityData(modifiedProductSpecification, storedProductSpecification),
				DefineIdentityData.class.getSimpleName(), validatedData);
		addToMap(validateCharactersticSpecification(modifiedProductSpecification, storedProductSpecification),
				PickCharacteristicSpecification.class.getSimpleName(), validatedData);
		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			addToMap(
					validateRelationShip(modifiedProductSpecification.getProductSpecificationRelationship(),
							storedProductSpecification.getProductSpecificationRelationship()),
					DefineRelationship.class.getSimpleName(), validatedData);
			addToMap(
					validatePolicyRule(modifiedProductSpecification.getPolicyRuleRef(),
							storedProductSpecification.getPolicyRuleRef()),
					AssociatePolicyRuleRef.class.getSimpleName(), validatedData);
		}
		return validatedData;
	}


	private void addToMap(String versionType,String className,Map<String,ArrayList<String>> validatedData) {
		ArrayList<String> list=validatedData.get(versionType);
		list.add(className);
		validatedData.put(versionType, list);
	}
	private String validateIdentityData(ProductSpecification modifiedProductSpecification, ProductSpecification storedProductSpecification) {

		if(modifiedProductSpecification.getRelatedParty().size()!=storedProductSpecification.getRelatedParty().size()) {
			return VersionType.MAJOR.getValue();
		}
		List<RelatedParty> commonRelatedParties = modifiedProductSpecification.getRelatedParty().stream()
				.filter(modifiedRelatedParty -> storedProductSpecification.getRelatedParty().stream()
						.anyMatch(storedRelatedParty -> storedRelatedParty.equals(modifiedRelatedParty)))
				.collect(Collectors.toList());
		if (commonRelatedParties.size() != modifiedProductSpecification.getRelatedParty().size()) {
			return VersionType.MAJOR.getValue();
		}

		if (modifiedProductSpecification.getRelatedResource().size() != storedProductSpecification.getRelatedResource()
				.size()) {
			return VersionType.MAJOR.getValue();
		}

		List<RelatedResource> commonRelatedResources = modifiedProductSpecification.getRelatedResource().stream()
				.filter(modifiedRelatedResource -> storedProductSpecification.getRelatedResource().stream()
						.anyMatch(storedRelatedResource -> storedRelatedResource.equals(modifiedRelatedResource)))
				.collect(Collectors.toList());
		if (commonRelatedResources.size() != modifiedProductSpecification.getRelatedResource().size()) {
			return VersionType.MAJOR.getValue();
		}

		if (!modifiedProductSpecification.getValidFor().equals(storedProductSpecification.getValidFor())) {
			return VersionType.MAJOR.getValue();
		}

		if (!modifiedProductSpecification.getDescription().equals(storedProductSpecification.getDescription())
				|| !modifiedProductSpecification.getName().equals(storedProductSpecification.getName())
				|| !modifiedProductSpecification.getProductNumber().equals(storedProductSpecification.getProductNumber())
				|| !modifiedProductSpecification.getBrand().equals(storedProductSpecification.getBrand())) {
			return VersionType.MINOR.getValue();

		}

		return VersionType.NOVERSION.getValue();
	}

	private String validateCharactersticSpecification(ProductSpecification modifiedProductSpecification,
													  ProductSpecification storedProductSpecification) {
		List<ProductSpecificationCharacteristic> modifiedList = modifiedProductSpecification
				.getProductSpecCharacteristic();
		List<ProductSpecificationCharacteristic> storedList = storedProductSpecification.getProductSpecCharacteristic();

		if (modifiedList.size() != storedList.size()) {
			return VersionType.MAJOR.getValue();
		}

		modifiedList.sort(Comparator.comparing(ProductSpecificationCharacteristic::getId));
		storedList.sort(Comparator.comparing(ProductSpecificationCharacteristic::getId));

		for (int i = 0; i < modifiedList.size(); i++) {
			ProductSpecificationCharacteristic modified = modifiedList.get(i);
			ProductSpecificationCharacteristic stored = storedList.get(i);

			if (isMajorChange(modified, stored)) {
				return VersionType.MAJOR.getValue();
			}

			String versionType = this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)
					? validateCfsSpec(modified, stored)
					: validateStockSpec(modified, stored);

			if (!versionType.equals(VersionType.NOVERSION.getValue())) {
				return versionType;
			}

			if (isMinorChange(modified, stored)) {
				return VersionType.MINOR.getValue();
			}
		}

		return storedProductSpecification.getUsageSpecification() != null
				? validateUsageSpecification(modifiedProductSpecification.getUsageSpecification(),
				storedProductSpecification.getUsageSpecification())
				: VersionType.NOVERSION.getValue();
	}

	private boolean isMajorChange(ProductSpecificationCharacteristic modified,
								  ProductSpecificationCharacteristic stored) {
		if (this.productSpec.getSupportEntity().equals(SupportEntity.CFSSPEC)) {
			return !getTrimmedId(modified).equals(getTrimmedId(stored))
					|| !modified.getValueType().equals(stored.getValueType());
		}
		return !modified.getId().equals(stored.getId()) || !modified.getName().equals(stored.getName());
	}

	private String validateCfsSpec(ProductSpecificationCharacteristic modified,
								   ProductSpecificationCharacteristic stored) {
		String valueVersionType = validategetProductSpecCharacteristicValueCFS(
				modified.getProductSpecCharacteristicValue(), stored.getProductSpecCharacteristicValue());
		if (valueVersionType.equals(VersionType.MINOR.getValue())) {
			return valueVersionType;
		}

		return validateProductSpecCharRelationship(modified.getProductSpecCharRelationship(),
				stored.getProductSpecCharRelationship());
	}

	private String validateStockSpec(ProductSpecificationCharacteristic modified,
									 ProductSpecificationCharacteristic stored) {
		return validategetProductSpecCharacteristicValueStock(modified.getProductSpecCharacteristicValue(),
				stored.getProductSpecCharacteristicValue());
	}

	private boolean isMinorChange(ProductSpecificationCharacteristic modified,
								  ProductSpecificationCharacteristic stored) {
		if (modified.getDescription() == null || modified.getName() == null) {
			return stored.getDescription() != null || stored.getName() != null;
		}
		return !modified.getDescription().equals(stored.getDescription())
				|| !modified.getName().equals(stored.getName());
	}

	private String getTrimmedId(ProductSpecificationCharacteristic characteristic) {
		return characteristic.getId().substring(characteristic.getId().indexOf('.'));
	}


	private String validategetProductSpecCharacteristicValueStock(
			List<ProductSpecificationCharacteristicValue> modifiedProductSpecificationCharacteristicValues,
			List<ProductSpecificationCharacteristicValue> storedProductSpecificationCharacteristicValues) {
		int valueCount = 0;
		if (modifiedProductSpecificationCharacteristicValues.size() != storedProductSpecificationCharacteristicValues
				.size()) {
			return VersionType.MAJOR.getValue();
		}

		while (valueCount < modifiedProductSpecificationCharacteristicValues.size()) {
			if (!(modifiedProductSpecificationCharacteristicValues.get(valueCount)
					.equals(storedProductSpecificationCharacteristicValues.get(valueCount)))) {
				return VersionType.MAJOR.getValue();
			}
			valueCount++;
		}
		return VersionType.NOVERSION.getValue();
	}

	private String validateProductSpecCharRelationship(
			List<ProductSpecificationCharacteristicRelationship> modifiedProductSpecificationCharacteristicRelationships,
			List<ProductSpecificationCharacteristicRelationship> storedProductSpecificationCharacteristicRelationships) {
		int relationshipCount = 0;
		if (modifiedProductSpecificationCharacteristicRelationships
				.size() != storedProductSpecificationCharacteristicRelationships.size()) {
			return VersionType.MINOR.getValue();
		}
		while (relationshipCount < modifiedProductSpecificationCharacteristicRelationships.size()) {
			if (!(modifiedProductSpecificationCharacteristicRelationships.get(relationshipCount)
					.equals(storedProductSpecificationCharacteristicRelationships.get(relationshipCount)))) {
				return VersionType.MINOR.getValue();
			}
			relationshipCount++;
		}
		return VersionType.NOVERSION.getValue();
	}

	private String validatePolicyRule(List<PolicyRuleRef> modifiedPolicyRuleRefs,
									  List<PolicyRuleRef> storedPolicyRuleRefs) {
		if (modifiedPolicyRuleRefs.size() != storedPolicyRuleRefs.size()) {
			return VersionType.MAJOR.getValue();
		}

		List<PolicyRuleRef> commonPolicyRuleRef = modifiedPolicyRuleRefs.stream()
				.filter(modifiedPolicyRuleRef -> storedPolicyRuleRefs.stream()
						.anyMatch(storedPolicyRuleRef -> storedPolicyRuleRef.equals(modifiedPolicyRuleRef)))
				.collect(Collectors.toList());
		if (commonPolicyRuleRef.size() != modifiedPolicyRuleRefs.size()) {
			return VersionType.MAJOR.getValue();
		}

		return VersionType.NOVERSION.getValue();
	}

	private String validategetProductSpecCharacteristicValueCFS(
			List<ProductSpecificationCharacteristicValue> modifiedProductSpecificationCharacteristicValues,
			List<ProductSpecificationCharacteristicValue> storedProductSpecificationCharacteristicValues) {
		int valueCount = 0;
		if (modifiedProductSpecificationCharacteristicValues.size() != storedProductSpecificationCharacteristicValues
				.size()) {
			return VersionType.MINOR.getValue();
		}

		while (valueCount < modifiedProductSpecificationCharacteristicValues.size()) {
			if (!(modifiedProductSpecificationCharacteristicValues.get(valueCount)
					.equals(storedProductSpecificationCharacteristicValues.get(valueCount)))) {
				return VersionType.MINOR.getValue();
			}
			valueCount++;
		}
		return VersionType.NOVERSION.getValue();
	}

	private String validateRelationShip(
			List<ProductSpecificationRelationship> modifiedProductSpecificationRelationships,
			List<ProductSpecificationRelationship> storedProductSpecificationRelationships) {
		if (modifiedProductSpecificationRelationships.size() != storedProductSpecificationRelationships.size()) {
			return VersionType.MAJOR.getValue();
		}

		List<ProductSpecificationRelationship> commonProductSpecificationRelationships = modifiedProductSpecificationRelationships
				.stream()
				.filter(modifiedProductSpecificationRelationship -> storedProductSpecificationRelationships.stream()
						.anyMatch(storedProductSpecificationRelationship -> storedProductSpecificationRelationship
								.equals(modifiedProductSpecificationRelationship)))
				.collect(Collectors.toList());
		if (commonProductSpecificationRelationships.size() != modifiedProductSpecificationRelationships.size()) {
			return VersionType.MAJOR.getValue();
		}

		return VersionType.NOVERSION.getValue();
	}

	private String validateUsageSpecification(List<UsageSpecification> modifiedUsageSpecifications,
											  List<UsageSpecification> storedUsageSpecifications) {

		if (modifiedUsageSpecifications.size() != storedUsageSpecifications.size()) {
			return VersionType.MAJOR.getValue();
		} else {
			List<UsageSpecification> commonUsageSpecifications = modifiedUsageSpecifications.stream()
					.filter(modifiedUsageSpecification -> storedUsageSpecifications.stream()
							.anyMatch(storedUsageSpecification -> storedUsageSpecification.getId()
									.substring(storedUsageSpecification.getId().indexOf('.'))
									.equals(modifiedUsageSpecification.getId()
											.substring(modifiedUsageSpecification.getId().indexOf('.')))))
					.collect(Collectors.toList());
			if (commonUsageSpecifications.size() != modifiedUsageSpecifications.size()) {
				return VersionType.MAJOR.getValue();
			}

		}
		List<UsageSpecification> commonUsageSpecifications = modifiedUsageSpecifications.stream()
				.filter(modifiedUsageSpecification -> storedUsageSpecifications.stream()
						.anyMatch(storedUsageSpecification -> storedUsageSpecification.getName()
								.equals(modifiedUsageSpecification.getName())))
				.collect(Collectors.toList());
		if (commonUsageSpecifications.size() != modifiedUsageSpecifications.size()) {
			return VersionType.MINOR.getValue();
		}
		return VersionType.NOVERSION.getValue();
	}

	private boolean isValidStatus(ProductSpecificationLifecycle lifecycleState, String productSpecId) {
		if(lifecycleState.equals(ProductSpecificationLifecycle.OBSOLETE)){
			if(adminQueryService.fetchCPIBConfiguration(accessToken)){// admin API call
				if(cpibQueryService.fetchProductByProductSpecId(productSpecID,accessToken)){ // CPIB API call
					throw new DiscoManagedClientException("Entiy is in use in CPIB");
				}
			}
		}else {
			throw new DiscoManagedClientException("only lifecycle status can be updated to obsolete");
		}
		return true;
	}

}

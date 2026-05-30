// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.aggregate;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.orange.discobole.productcatalog.productoffering.dto.generated.productcatalogadministration.FrequencyTypes;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.AssociatePOPtoTerm;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.CommercialOperationRef;
import jakarta.annotation.Resource;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.orange.discobole.processflow.event.Event;
import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.productoffering.util.ChannelCache;
import com.orange.discobole.productcatalog.productoffering.util.MarketSegmentCache;
import com.orange.discobole.productcatalog.productoffering.command.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.command.productoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productoffering.constant.ProductOffConstants;
import com.orange.discobole.productcatalog.productoffering.dto.InvalidCharacteristics;
import com.orange.discobole.productcatalog.productoffering.dto.Validation;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.*;
import com.orange.discobole.productcatalog.productoffering.dto.productoffering.AssociatePOPtoOperationSpec;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.ChildPOInfoAddedEvent;
import com.orange.discobole.productcatalog.productoffering.event.bundleproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.contractproductoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.*;
import com.orange.discobole.productcatalog.productoffering.event.productoffering.modify.*;
import com.orange.discobole.productcatalog.productoffering.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productoffering.mapper.TimePeriodMapper;
import com.orange.discobole.productcatalog.productoffering.pojo.DefineIdentityData;
import com.orange.discobole.productcatalog.productoffering.pojo.ProductCharValue;
import com.orange.discobole.productcatalog.productoffering.pojo.productoffering.PickAtomicProductOfferingCharacteristic;
import com.orange.discobole.productcatalog.productoffering.projection.ProductOfferingProjector;
import com.orange.discobole.productcatalog.productoffering.service.AdminQueryService;
import com.orange.discobole.productcatalog.productoffering.service.CommercialProductInstalledBaseQueryService;
import com.orange.discobole.productcatalog.productoffering.service.ModifyProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.util.CommonUtil;
import com.orange.discobole.productcatalog.productoffering.util.ConverterUtil;
import com.orange.discobole.productcatalog.productoffering.util.TimePeriodValidityUtil;
import org.springframework.util.ObjectUtils;

/**
 * The Class handles the business logic of different commands.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class ProductOfferingAggregate {

	private static final String STRING_CHARACTERISTIC = "StringCharacteristic";

	@Resource
	private ConfigurableProperties configurableProperties;

	@Resource
	private ProductOfferingProjector projector;

	@Resource
	private Publisher publisher;
	@AggregateIdentifier
	private String stateProductOfferingId;
	ProductOffering stateProductOffering;
	private CommercialProductInstalledBaseQueryService cpibQueryService;
	private AdminQueryService adminQueryService;
	private String accessToken;
	@Resource
	private AccessTokenInterceptor accessTokenInterceptor;

	private ChannelCache channelCache;

	private MarketSegmentCache marketSegmentCache;


	ProductOfferingAggregate() {
	}

	@Autowired
	public ProductOfferingAggregate(ChannelCache channelCache, MarketSegmentCache marketSegmentCache,
									CommercialProductInstalledBaseQueryService cpibQueryService,
									AdminQueryService adminQueryService) {
		this.channelCache = channelCache;
		this.marketSegmentCache = marketSegmentCache;
		this.cpibQueryService = cpibQueryService;
		this.adminQueryService = adminQueryService;
	}


	public ProductOfferingAggregate(AccessTokenInterceptor accessTokenInterceptor) {
		this.accessToken = accessTokenInterceptor.getToken(); // <-- this line runs when Spring instantiates the bean
	}

	/**
	 * Process the Select product offering Type command and generates product
	 * offering id.
	 *
	 * @param command the command
	 *
	 */
	@CommandHandler
	public ProductOfferingAggregate(SelectProductOfferingTypeCommand command) {
		this.stateProductOfferingId = command.getproductOfferingId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingTypeSelectedEvent(stateProductOfferingId, lastUpdate,
				command.getIsSellable(), command.getIsBundle(), command.getIsInstallable(),
				command.getProductOfferingType(), ProductOfferingLifecycle.INSTUDY));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingTypeSelectedEvent .
	 *
	 * @param ProductOfferingTypeSelectedEvent
	 */
	@EventSourcingHandler
	public void on(ProductOfferingTypeSelectedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering = new ProductOffering();
		stateProductOffering.id(event.getProductOfferingId()).isBundle(event.getIsBundle())
				.isSellable(event.getIsSellable()).isInstallable(event.getIsInstallable()).type(event.getType())
				.lifecycleStatus(event.getLifeCycleStatus());
	}

	/**
	 * updates the state of aggregate after applying
	 * CreateBundleProductOfferingEvent .
	 *
	 * @param CreateContractProductOfferingEvent
	 */

	@EventSourcingHandler
	public void on(CreateBundleProductOfferingEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
	}

	/**
	 * Process the Initiate product offering command and generates product offering
	 * id.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processInitiateProductOfferingCommand(final InitiateProductOfferingCommand command,
													  QueryService queryService) {
		ProductSpecification productSpecification = queryService.fetchProductSpecById(command.getProductSpecId(), accessToken);
		ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
		TimePeriod timePeriod = productSpecification.getValidFor();
		OffsetDateTime now = OffsetDateTime.now();
		OffsetDateTime validityEndDateTime = timePeriod.getEndDateTime();
		// Case 1: Check if the Product Specification is valid
		if (validityEndDateTime != null && now.isAfter(validityEndDateTime)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS);
		}
		if (ProductSpecificationLifecycle.ACTIVE == productSpecState
				|| ProductSpecificationLifecycle.LAUNCHED == productSpecState) {
			AggregateLifecycle.apply(new ProductSpecStateVerifiedEvent(command.getProductOfferingId(),
					command.getProductSpecId(), productSpecState));

		} else {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
		}

		ProductSpecificationRef productSpecificationRef = new ProductSpecificationRef().id(productSpecification.getId())
				.name(productSpecification.getName()).href(productSpecification.getHref())
				.version(productSpecification.getVersion()).baseType(productSpecification.getBaseType())
				.schemaLocation(productSpecification.getSchemaLocation()).type(productSpecification.getType());
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new AtomicProductOfferingInitiatedEvent(productSpecificationRef,
				command.getProductOfferingId(), ProductOfferingLifecycle.INSTUDY, lastUpdate));

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingInitiatedEvent .
	 *
	 * @param AtomicProductOfferingInitiatedEvent
	 */

	@EventSourcingHandler
	public void on(AtomicProductOfferingInitiatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.productSpecification(event.getProductSpec());
		this.stateProductOffering.lifecycleStatus(event.getLifecycleStatus());
	}

	/**
	 * updates the state of aggregate after applying ProductSpecStateVerifiedEvent .
	 *
	 * @param ProductSpecStateVerifiedEvent
	 */

	@EventSourcingHandler
	public void on(ProductSpecStateVerifiedEvent event) {
		this.stateProductOfferingId = event.getProductofferingId();
	}

	/**
	 * Process the atomic offer description command and updates the description of
	 * product offering.
	 *
	 * @param command the command
	 * @author Diksha Srivastava
	 */
	@CommandHandler
	public void processProductOfferingDescriptionCommand(final ProductOfferingIdentityDataCommand command,
			QueryService queryService) {

		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		OffsetDateTime poStartDateTime = command.getValidity().getStartDateTime();
		OffsetDateTime poEndDateTime=command.getValidity().getEndDateTime();
		List<ChannelRef> channels = processProductOfferingChannel(command.getChannelIds(), queryService);
		List<MarketSegmentRef> marketSegments = processProductOfferingMarket(command.getMarketSegmentIds(),queryService);
		List<RelatedParty> relatedParties = processProductOfferingRelatedParty(command.getRelatedParties(), queryService);
		List<ProductOfferingTerm> poTerms = processProductOfferingTerm(command.getPoTerms(), queryService);
		String href = configurableProperties.getCatprodcaturl() + "?id=" + this.stateProductOfferingId;
		if (command.getIdentityData().getIsSellable() == null) {
			command.getIdentityData().setIsSellable(false);
			
		} 
		if (command.getIdentityData().getIsVisible() == null) {
		    command.getIdentityData().setIsVisible(true); // Default value
		}

		// Use primitive boolean values to avoid null unboxing
		boolean isSellable = command.getIdentityData().getIsSellable();
		boolean isVisible  = command.getIdentityData().getIsVisible();

		 // Check isSellable and isVisible
	    if (isSellable && !isVisible) {
	        throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_ATTRIBUTE);
	    }

		if (poStartDateTime.isBefore(OffsetDateTime.now().minusMinutes(5))) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_STARTDATIME);
		}
		if(poEndDateTime!=null && poEndDateTime.isBefore(poStartDateTime)){

			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_ENDDATIME);
		}

		switch (command.getType()) {
		case ATOMICPRODUCTOFFERING:
			String productSpecId = this.stateProductOffering.getProductSpecification().getId();
			ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, null);
			ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
			TimePeriod timePeriod = productSpecification.getValidFor();
			OffsetDateTime validityEndDateTime = timePeriod.getEndDateTime();
			if (validityEndDateTime != null && poStartDateTime.isAfter(validityEndDateTime)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_EXPIRED_PS);
			}
			processProductOfferingValidForCommand(command.getValidity(), productSpecification);
			processIsBundle(command.getIdentityData(), false);
			if (!(ProductSpecificationLifecycle.ACTIVE == productSpecState
					|| ProductSpecificationLifecycle.LAUNCHED == productSpecState)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
			}
			AggregateLifecycle.apply(new AtomicProductOfferingIdentityDataDefinedEvent(productOfferingId,
					command.getIdentityData(), channels, marketSegments, relatedParties, poTerms,
					processProductOfferingValidForCommand(command.getValidity(), productSpecification),
					command.getType(), command.getStatusReason(), lastUpdate, href));
			break;
		case BUNDLEPRODUCTOFFERING:
			processIsBundle(command.getIdentityData(), true);
			AggregateLifecycle.apply(new BundleProductOfferingIdentityDataDefinedEvent(productOfferingId,
					command.getIdentityData(), channels, marketSegments, relatedParties, poTerms, command.getValidity(),
					command.getType(), command.getStatusReason(), lastUpdate, href));
			break;
		default:
			break;
		}

	}

	private void processIsBundle(DefineIdentityData identityData, boolean flag) {
		identityData.setIsBundle(flag);
	}

	/**
	 * Process the contract offer description command and updates the description of
	 * product offering.
	 *
	 * @param command the command
	 * @author Vishal Vachaspati
	 */
	@CommandHandler
	public void processContractProductOfferingDescriptionCommand(
			final ContractProductOfferingDescriptionCommand command, QueryService queryService) {

		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		List<ChannelRef> channels = processProductOfferingChannel(command.getChannelIds(), queryService);
		List<MarketSegmentRef> marketSegments = processProductOfferingMarket(command.getMarketSegmentIds(),
				queryService);
		List<RelatedParty> relatedParties = processProductOfferingRelatedParty(command.getRelatedParties(),
				queryService);
		List<ProductOfferingTerm> poTerms = processProductOfferingTerm(command.getPoTerms(),queryService);
		command.getIdentityData().setIsSellable(true);
		command.getIdentityData().setIsVisible(true);
		command.getIdentityData().setIsBundle(true);
		String href = configurableProperties.getProductOfferingUrl() + "?id=" + this.stateProductOfferingId;
		AggregateLifecycle.apply(new ContractProductOfferingIdentityDataDefinedEvent(productOfferingId,
				command.getIdentityData(), channels, marketSegments, relatedParties, poTerms, command.getValidity(),
				command.getType(), command.getStatusReason(), lastUpdate, href));
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingDescribedEvent .
	 *
	 * @param AtomicProductOfferingDescribedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingIdentityDataDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.brand(event.getIdentityData().getBrand()).name(event.getIdentityData().getName())
				.statusReason(event.getStatusReason()).type(event.getType())
				.isSellable(event.getIdentityData().getIsSellable())
				.isVisible(event.getIdentityData().getIsVisible())
				.isBundle(event.getIdentityData().getIsBundle())
				.href(event.getHref())
				.isInstallable(event.getIdentityData().getIsInstallable()).channel(event.getChannels())
				.productOfferingTerm(event.getPoTerms()).marketSegment(event.getMarketSegments())
				.validFor(event.getValidity()).relatedParty(event.getRelatedParties());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingDescribedEvent .
	 *
	 * @param BundleProductOfferingDescribedEvent
	 */

	@EventSourcingHandler
	public void on(final BundleProductOfferingIdentityDataDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.brand(event.getIdentityData().getBrand()).name(event.getIdentityData().getName())
				.statusReason(event.getStatusReason()).type(event.getType())
				.isSellable(event.getIdentityData().getIsSellable())
				.isVisible(event.getIdentityData().getIsVisible())
				.isBundle(event.getIdentityData().getIsBundle())
				.href(event.getHref())
				.isInstallable(event.getIdentityData().getIsInstallable()).channel(event.getChannels())
				.productOfferingTerm(event.getPoTerms()).marketSegment(event.getMarketSegments())
				.validFor(event.getValidity()).relatedParty(event.getRelatedParties());

	}

	@EventSourcingHandler
	public void on(final ContractProductOfferingIdentityDataDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.billingType(event.getIdentityData().getBillingType()).brand(event.getIdentityData().getBrand())
				.name(event.getIdentityData().getName()).statusReason(event.getStatusReason()).type(event.getType())
				.isSellable(event.getIdentityData().getIsSellable())
				.isVisible(event.getIdentityData().getIsVisible())
				.isBundle(event.getIdentityData().getIsBundle())
				.href(event.getHref())
				.isInstallable(event.getIdentityData().getIsInstallable()).channel(event.getChannels())
				.productOfferingTerm(event.getPoTerms()).marketSegment(event.getMarketSegments())
				.validFor(event.getValidity()).relatedParty(event.getRelatedParties());
	}

	/**
	 * Process the atomic Product Offering Characteristic command.
	 *
	 * @param command the command
	 * @author Shreya Sharma
	 */
	@CommandHandler
	public void processSelectProductOfferingCharacteristicCommand(SelectProductOfferingCharacteristicCommand command,
																  QueryService queryService) {
		List<PickAtomicProductOfferingCharacteristic> userProductOfferingCharacteristic = new ArrayList<>();
		List<PickAtomicProductOfferingCharacteristic> productOfferingCharacteristic = new ArrayList<>();
		String productOfferingId = this.stateProductOffering.getId();
		List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics = new ArrayList<>();
		//check for duplicate as well
		HashSet<String> duplicateNames=new HashSet<>();
		categorizeCharacteristics(command.getPickAtomicProductOfferingCharacteristics(), duplicateNames, productOfferingCharacteristic, userProductOfferingCharacteristic);

		if (!userProductOfferingCharacteristic.isEmpty()) {
			validateAndAddUserChar(userProductOfferingCharacteristic, selectedProductOfferingCharacteristics);
		}

		if (!productOfferingCharacteristic.isEmpty()) {
			processProductCharacteristics(productOfferingCharacteristic, queryService, this.stateProductOffering.getProductSpecification().getId(), selectedProductOfferingCharacteristics);
		}
		AggregateLifecycle.apply(new AtomicProductOfferingCharacteristicsDefinedEvent(productOfferingId,
				selectedProductOfferingCharacteristics, OffsetDateTime.now()));

	}
	private void checkAndSetCharId(PickAtomicProductOfferingCharacteristic poCharacteristic,String productOfferingId,int index){
		if (poCharacteristic.getId()!=null){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CHAR_NOTNULL_ID,poCharacteristic.getId(),poCharacteristic.getId());
		}
		else{
			//set char id: prodOff+seq(1,2,3,...)
			poCharacteristic.setId(productOfferingId + "." + index);
		}

	}
	private void checkDuplicateCharName(HashSet<String> duplicateName,PickAtomicProductOfferingCharacteristic poCharacteristic){
		if(duplicateName.contains(poCharacteristic.getName())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_CHAR_DUPLICATE_NAME,poCharacteristic.getName(),poCharacteristic.getName());
		}
		else
			duplicateName.add(poCharacteristic.getName());
	}
	private void validateAndAddUserChar(List<PickAtomicProductOfferingCharacteristic> userProductOfferingCharacteristic,List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics){
		for(PickAtomicProductOfferingCharacteristic userChar: userProductOfferingCharacteristic){
			checkCharNullNameAndDescription(userChar);
			userChar.setValidFor(new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod().startDateTime(stateProductOffering.getValidFor().getStartDateTime()).endDateTime(stateProductOffering.getValidFor().getEndDateTime()));
			checkCharDuplicateUserCharValue(userChar);
			userChar.setMinCardinality(1);
			userChar.setMaxCardinality(1);
			ProductSpecificationCharacteristicValueUse characteristicValueUse = createCharacteristicValueUse(
					userChar, null);
			characteristicValueUse.setType(STRING_CHARACTERISTIC);
			selectedProductOfferingCharacteristics.add(characteristicValueUse);
		}
	}
	private void checkCharNullNameAndDescription(PickAtomicProductOfferingCharacteristic userChar){
		if(userChar.getName()==null || userChar.getDescription()==null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_CHAR_NULL_NAME_OR_DESC);
		}
	}
	private void validateValidityPeriod(TimePeriod charValidity,TimePeriod constraintValidityPeriod,String name){
		Validation timeValidate = TimePeriodValidityUtil.checkTimePeriodRestriction(
				charValidity, constraintValidityPeriod);
		if(!timeValidate.isIsValid()){
			String msg=name+". "+timeValidate.getReason();
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_USER_CHAR_VALIDITY,msg,msg);
		}
	}
	private void checkCharDuplicateUserCharValue(PickAtomicProductOfferingCharacteristic userChar){
		HashSet<String> duplicateValue=new HashSet<>();
		List<ProductCharValue> productSpecCharacteristicValue=new ArrayList<>();
		for(ProductCharValue charValue: userChar.getProductSpecCharacteristicValue()){
			if(charValue.getValue()==null){
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_USER_CHAR_NULL_VALUE,userChar.getName(),userChar.getName());
			}
			else if(duplicateValue.contains(charValue.getValue())){
				String msg=userChar.getName()+". Duplicate Value Found: "+charValue.getValue();
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_USER_CHAR_DUPLICATE_VALUE,msg,msg);
			}
			else {
				duplicateValue.add(charValue.getValue());
				ProductCharValue newCharValue=new ProductCharValue();
				newCharValue.setValue(charValue.getValue());
				newCharValue.setIsDefault(charValue.getDefault());
				newCharValue.setValidFor(new com.orange.discobole.productcatalog.productoffering.pojo.TimePeriod().startDateTime(userChar.getValidFor().getStartDateTime()).endDateTime(userChar.getValidFor().getEndDateTime()));
				productSpecCharacteristicValue.add(newCharValue);

			}
		}
		userChar.setProductSpecCharacteristicValue(productSpecCharacteristicValue);
	}

	private void validateProductSpecificationStateInCharacteristics(ProductSpecification productSpecification) {

		if (!(productSpecification.getLifecycleStatus() == ProductSpecificationLifecycle.ACTIVE
				|| productSpecification.getLifecycleStatus() == ProductSpecificationLifecycle.LAUNCHED)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
		}
	}

	private void processCharacteristics(List<PickAtomicProductOfferingCharacteristic> productOfferingCharacteristic,
										List<ProductSpecificationCharacteristic> productSpecificationCharacteristic,
										List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics,
										Set<InvalidCharacteristics> invalidCharacteristics, Map<String, Boolean> checkDuplicateId) {
		Validation validateCharac = null;
		for (PickAtomicProductOfferingCharacteristic productOfferChar : productOfferingCharacteristic) {
			validateCharac = new Validation();
			validateCharac.setValid(false);
			boolean idCheck = false;
			if (checkDuplicateId.containsKey(productOfferChar.getId())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_CHARACTRISTICS_ID_OR_NAME_REPEATED,productOfferChar.getId(),productOfferChar.getId());
			}
			checkDuplicateId.put(productOfferChar.getId(), false);
			Validation validation = validateCharacteristicInSelectCharCommand(productOfferChar,
					productSpecificationCharacteristic, selectedProductOfferingCharacteristics, validateCharac, idCheck,
					checkDuplicateId);

			if (!validation.isIsValid()) {
				InvalidCharacteristics invalidCharacteristic = new InvalidCharacteristics();
				invalidCharacteristic.setPickAtomicProductOfferingCharacteristic(productOfferChar);
				invalidCharacteristic.setReason(validateCharac.getReason());
				invalidCharacteristics.add(invalidCharacteristic);
			}
		}

	}

	private Validation validateCharacteristicInSelectCharCommand(
			PickAtomicProductOfferingCharacteristic productOfferChar,
			List<ProductSpecificationCharacteristic> productSpecificationCharacteristic,
			List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics,
			Validation validateCharac, boolean idCheck, Map<String, Boolean> checkDuplicateId) {

		for (ProductSpecificationCharacteristic productSpecChar : productSpecificationCharacteristic) {
			if (productSpecChar.getId().equals(productOfferChar.getId())
					&& productSpecChar.getName().equals(productOfferChar.getName())) {
				if (productOfferChar.getMinCardinality() > productOfferChar.getMaxCardinality()) {
					throw new DiscoManagedClientException(
							ProductOffConstants.DISCO_PO_MINCARDINALITY_LESSOREQUAL_MAXCARDINALITY,productOfferChar.getId(),productOfferChar.getId());
				}
				idCheck = true;
				checkDuplicateId.put(productOfferChar.getId(), true);
				validateCharacteristics(selectedProductOfferingCharacteristics, validateCharac, productOfferChar,
						productSpecChar);
				break;

			}
		}
		if (!idCheck) {
			validateCharac.setReason("Characteristic Id or Name entered is invalid");
		}
		return validateCharac;
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingCharacteristicsDefinedEvent .
	 *
	 * @param AtomicProductOfferingCharacteristicsDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingCharacteristicsDefinedEvent event) {
		this.stateProductOfferingId = event.getproductOfferingId();
		stateProductOffering.prodSpecCharValueUse(event.getProductSpecificationCharacteristicValueUse());

	}

	/**
	 * Validate characteristics.
	 *
	 * @param selectedProductOfferingCharacteristics the selected product offering
	 *                                               characteristics
	 * @param validateCharac                         the validate charac
	 * @param productOfferChar                       the product offer char
	 * @param productSpecChar                        the product spec char
	 * @return true, if successful
	 */

	private void validateCharacteristics(
			List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics,
			Validation validateCharac, PickAtomicProductOfferingCharacteristic productOfferChar,
			ProductSpecificationCharacteristic productSpecChar) {

		if (!CommonUtil.checkNumericRange(productOfferChar, productSpecChar)) {
			invalidValidation(validateCharac,
					"MinCardinality and MaxCardinality of Product Offering should be within the range of Product Spec");
		}

		if(!"AddressCharacteristic".equalsIgnoreCase(productSpecChar.getType())) {
			if (!validateCharacteristicValue(productOfferChar, productOfferChar.getProductSpecCharacteristicValue(),
					productSpecChar.getProductSpecCharacteristicValue(), validateCharac)) {
				String msg=productOfferChar.getId()+"\n "+validateCharac.getReason();
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CHARACTRISTICS_VALUE,msg,msg);
			}

			if (!validateTimeRange(productOfferChar, productSpecChar)) {
				throw new DiscoManagedClientException(
						ProductOffConstants.DISCO_PO_INVALID_PO_CHARACTERISTICS_VALUE_TIME_RANGE_INVALID,
						productOfferChar.getId(), null);
			}
		}

		validateAndAddCharacteristic(selectedProductOfferingCharacteristics, validateCharac, productOfferChar,
				productSpecChar);
	}

	private Validation invalidValidation(Validation validateCharac, String reason) {
		validateCharac.setReason(reason);
		return validateCharac;
	}

	private boolean validateTimeRange(PickAtomicProductOfferingCharacteristic productOfferChar,
									  ProductSpecificationCharacteristic productSpecChar) {
		if (!"validityCharacteristic".equalsIgnoreCase(productSpecChar.getType())) {
			return true; // Skip check if not a validity characteristic
		}

		return isTimeRangeWithinRange(productOfferChar.getProductSpecCharacteristicValue(),
				productSpecChar.getProductSpecCharacteristicValue());
	}

	private Validation validateAndAddCharacteristic(
			List<ProductSpecificationCharacteristicValueUse> selectedProductOfferingCharacteristics,
			Validation validateCharac, PickAtomicProductOfferingCharacteristic productOfferChar,
			ProductSpecificationCharacteristic productSpecChar) {

		Validation timeValidate = TimePeriodValidityUtil.checkTimePeriodRestriction(
				TimePeriodMapper.toGenerated(productOfferChar.getValidFor()), productSpecChar.getValidFor());

		if (!timeValidate.isIsValid()) {
			String msg="for Characteristics Id: "+productOfferChar.getId()+". "+ timeValidate.getReason();
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_VALID_FOR,msg,msg);
		}

		ProductSpecificationCharacteristicValueUse characteristicValueUse = createCharacteristicValueUse(
				productOfferChar, productSpecChar);
		selectedProductOfferingCharacteristics.add(characteristicValueUse);

		validateCharac.setValid(true);
		return validateCharac;
	}

	private ProductSpecificationCharacteristicValueUse createCharacteristicValueUse(
			PickAtomicProductOfferingCharacteristic productOfferChar,
			ProductSpecificationCharacteristic productSpecChar) {

		ProductSpecificationCharacteristicValueUse characteristicValueUse = new ProductSpecificationCharacteristicValueUse();
		characteristicValueUse.id(productOfferChar.getId()).description(productOfferChar.getDescription())
				.validFor(TimePeriodMapper.toGenerated(productOfferChar.getValidFor()))
				.maxCardinality(productOfferChar.getMaxCardinality())
				.minCardinality(productOfferChar.getMinCardinality()).name(productOfferChar.getName()).baseType(productOfferChar.getBaseType())
				.valueType(productOfferChar.getValueType()).productSpecCharacteristicValue(
						ConverterUtil.convert(productOfferChar.getProductSpecCharacteristicValue()));

		characteristicValueUse
				.setType((productSpecChar!=null && productSpecChar.getType() != null) ? productSpecChar.getType() : STRING_CHARACTERISTIC);
		return characteristicValueUse;
	}

	private boolean isTimeRangeWithinRange(
			List<ProductCharValue> productOfferCharValueList,
			List<ProductSpecificationCharacteristicValue> productSpecCharValueList) {

		for (ProductCharValue productCharValue : productOfferCharValueList) {
			if (!isTimeRangeProductOfferingCharValues(productCharValue)) {
				continue;
			}

			if (!hasMatchingSpecRange(productCharValue, productSpecCharValueList)) {
				return false; // Fail fast if any PO value doesn't match
			}
		}

		return true; // All PO ranges had at least one matching PS range
	}
	private boolean isTimeRangeProductSpecificationCharValues(ProductSpecificationCharacteristicValue productSpecValue) {
		return productSpecValue.getTimeRange() != null && (productSpecValue.getTimeRange().getValidTo() != null || productSpecValue.getTimeRange().getValidFrom() != null);
	}

	private boolean isTimeRangeProductOfferingCharValues(ProductCharValue productCharValue) {
		return productCharValue.getTimeRange() != null && (productCharValue.getTimeRange().getValidTo() != null || productCharValue.getTimeRange().getValidFrom() != null);
	}

	private boolean checkTimeRange(OffsetDateTime poFrom, OffsetDateTime poTo, OffsetDateTime psFrom, OffsetDateTime psTo) {
		// Skip PS ranges that have special end dates
		if (isSpecialDate(psTo)) {
			return false;  // signal: ignore this PS range
		}

		boolean fromOk = (poFrom == null || (psFrom != null && !poFrom.isBefore(psFrom)));
		boolean toOk = (poTo == null || (psTo != null && !poTo.isAfter(psTo)));

		return fromOk && toOk;
	}

	private boolean isSpecialDate(OffsetDateTime date) {
		if (date == null) {
			return false;
		}
		int year = date.getYear();
		return year == 2100;
	}

	private boolean hasMatchingSpecRange(ProductCharValue productCharValue,
										 List<ProductSpecificationCharacteristicValue> productSpecCharValueList) {
		OffsetDateTime poFrom = productCharValue.getTimeRange().getValidFrom();
		OffsetDateTime poTo = productCharValue.getTimeRange().getValidTo();

		for (ProductSpecificationCharacteristicValue specValue : productSpecCharValueList) {
			if (!isTimeRangeProductSpecificationCharValues(specValue)) {
				continue;
			}

			OffsetDateTime psFrom = specValue.getTimeRange().getValidFrom();
			OffsetDateTime psTo = specValue.getTimeRange().getValidTo();

			if (isSpecialDate(psTo)) {
				continue;
			}

			if (checkTimeRange(poFrom, poTo, psFrom, psTo)) {
				return true; // Found a matching range
			}
		}

		return false; // No match found
	}


	/**
	 * Validate Characteristic Value.
	 *
	 * @param productOfferChar   the product offer char
	 * @param prodOfferCharValue the prod offer char value
	 * @param prodSpecCharValue  the prod spec char value
	 * @param validateCharac     the validate charac
	 * @return true, if successful
	 * @author Shreya Sharma
	 */
	public static boolean validateCharacteristicValue(PickAtomicProductOfferingCharacteristic productOfferChar,
													  List<ProductCharValue> prodOfferCharValue, List<ProductSpecificationCharacteristicValue> prodSpecCharValue,
													  Validation validateCharac) {
		boolean validate = CommonUtil.checkCharacteristicValueRange(prodOfferCharValue, prodSpecCharValue,
				validateCharac, productOfferChar);
		if (!validate && validateCharac.getReason() == null) {
			validateCharac.setReason("Characteristic value should match or be within range of Product Spec");
		}
		return validate;
	}


	/**
	 * Process.
	 *
	 * @param command the command
	 *
	 */
	@CommandHandler
	public void processDefineProductOfferingCategoryCommand(final DefineProductOfferingCategoryCommand command,
			QueryService queryService, Publisher publisher) {
		List<String> selectedCategoryIds = command.getCategories();
		List<String> invalidCategories = new ArrayList<>();
		Set<CategoryRef> addCategories = new HashSet<>();
		List<Category> categories = new ArrayList<>();
		if(!ObjectUtils.isEmpty(selectedCategoryIds)) {
			categories = queryService.fetchCategoryByIds(selectedCategoryIds, accessToken);
		}

		Map<String, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));
		for (String categoryId : selectedCategoryIds) {
			Category category = categoryMap.get(categoryId);
			validateCategory(category);
			CategoryRef categoryRef = convert(category);
			addCategories.add(categoryRef);
		}
		if (!invalidCategories.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CATEGORY_SELECTED,
					"" + invalidCategories, null);

		}
		switch (this.stateProductOffering.getType()) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply(new AtomicProductOfferingCategoryDefinedEvent(this.stateProductOffering.getId(),
					addCategories, OffsetDateTime.now()));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingCategoryDefinedEvent(this.stateProductOffering.getId(),
					addCategories, OffsetDateTime.now()));
			break;
		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingCategoryDefinedEvent(this.stateProductOffering.getId(),
					addCategories, OffsetDateTime.now()));
			break;
		default:
			break;
		}

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingCategoryDefinedEvent .
	 *
	 * @param AtomicProductOfferingCategoryDefinedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingCategoryDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.setCategory(event.getCategories());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingCategoryDefinedEvent .
	 *
	 * @param BundleProductOfferingCategoryDefinedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingCategoryDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.setCategory(event.getCategories());

	}

	@EventSourcingHandler
	public void on(final ContractProductOfferingCategoryDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.setCategory(event.getCategories());

	}

	private void validateCategory(Category category) {
		if (null == category) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_CATEGORY);
		}

		if (!Boolean.TRUE.equals(this.stateProductOffering.isIsSellable())) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_CATEGORYCHECK1);
		}

		if (category.getSubCategory() != null && !category.getSubCategory().isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_CATEGORY_CHECK2);
		}

	}

	/**
	 * Process.
	 *
	 * @param command the command
	 */
	private List<ChannelRef> processProductOfferingChannel(Set<String> selectedChannelsIds, QueryService queryService) {
		List<ChannelRef> selectedChannels = new ArrayList<>();
		List<String> invalidChannels = new ArrayList<>();
		List<ChannelAdmin> channels = channelCache.computeIfAbsent("channels", key -> queryService.fetchChannels(accessToken));
		for (String channelId : selectedChannelsIds) {
			boolean isValidChannel = false;
			for (ChannelAdmin channel : channels) {
				if (channelId.equals(channel.getId())) {
					isValidChannel = true;
					ChannelRef channelRef = convertChannelAdminIntoRef(channel);
					selectedChannels.add(channelRef);
					this.stateProductOffering.addChannelItem(channelRef);
					break;
				}
			}
			if (!isValidChannel) {
				invalidChannels.add(channelId);
			}
		}

		if (!invalidChannels.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_CHANNEL,
					invalidChannels.toString(), invalidChannels.toString());
		}
		return selectedChannels;
	}

	private ChannelRef convertChannelAdminIntoRef(ChannelAdmin channel) {
		return new ChannelRef().id(channel.getId()).name(channel.getName());
	}

	/**
	 * Process the Product Offering operation comand.
	 *
	 * @param command the command
	 * @author Varshika Choudhary
	 */
	@CommandHandler
	public void processProductOfferingOperationCommand(ProductOfferingOperationCommand command,QueryService queryService) {
		String productSpecId = this.stateProductOffering.getProductSpecification().getId();
		ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
		ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
		if (!(ProductSpecificationLifecycle.ACTIVE == productSpecState
				|| ProductSpecificationLifecycle.LAUNCHED == productSpecState)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
		}
		String productOfferingId = this.stateProductOffering.getId();
		List<CommercialOperation> operations = command.getProductOfferingOperationSpecification();
		List<OperationSpecification> operationSpecs = productSpecification.getOperationSpecification();
		List<CommercialOperation> operationSelected = new ArrayList<>();
		Set<String> invalidOpsSelected = new HashSet<>();
		Map<String, TimePeriod> invalidValidFor = new HashMap<>();
		checkCommercialOperation(operations, operationSpecs, operationSelected, invalidOpsSelected, invalidValidFor);

		if (!invalidOpsSelected.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_OPERATION,
					invalidOpsSelected.toString(), invalidOpsSelected.toString());
		} else if (!invalidValidFor.isEmpty()) {
			String msg="for Operation ID: "+invalidValidFor.keySet();
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_VALID_FOR,msg,msg);
		}

		List<ProductOfferingTerm> updatedPOTerm = updateCommercialOperationInTerms( operationSelected);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new AtomicProductOfferingOperDefinedEvent(productOfferingId, operationSelected, updatedPOTerm, lastUpdate));
	}

	/**
	 * update term when action is updated
	 *
	 * @param productOfferingId
	 * @param operationSelected
	 */
	private List<ProductOfferingTerm> updateCommercialOperationInTerms( List<CommercialOperation> operationSelected) {
		Set<String> selectedOperationIds = operationSelected.stream()
				.map(CommercialOperation::getId)
				.collect(Collectors.toSet());

		List<ProductOfferingTerm> savedPOTerm = new ArrayList<>();
		if (this.stateProductOffering.getProductOfferingTerm() != null) {
			savedPOTerm = this.stateProductOffering.getProductOfferingTerm();
		}

		return  new ArrayList<>(savedPOTerm.stream()
				.map(pot -> {
					if (!ObjectUtils.isEmpty(pot.getCommercialOperation())) {
						pot.setCommercialOperation(
								pot.getCommercialOperation().stream()
										.filter(x -> selectedOperationIds.contains(x.getId()))
										.toList()
						);
					}
					return pot;
				})
				.toList());
	}

	private void checkCommercialOperation(List<CommercialOperation> operations,
			List<OperationSpecification> operationSpecs, List<CommercialOperation> operationSelected,
			Set<String> invalidOpsSelected, Map<String, TimePeriod> invalidValidFor) {
		for (CommercialOperation operation : operations) {
			boolean isValid = false;
			boolean isTimePeriod = false;
			for (OperationSpecification operationSpecification : operationSpecs) {
				if (operation.getId().equals(operationSpecification.getId()) && operation.getName().equals(operationSpecification.getName())) {
					isValid = true;
					TimePeriod input = operation.getValidFor();
					TimePeriod restriction = operationSpecification.getValidFor();
					if (TimePeriodValidityUtil.checkTimePeriodRestriction(input, restriction).isIsValid()) {
						isTimePeriod = true;
						operationSelected.add(operation);
						break;
					}
				}
			}
			if (!isValid) {
				invalidOpsSelected.add(operation.getId());
			} else if (!isTimePeriod) {
				invalidValidFor.put(operation.getId(), operation.getValidFor());
			}
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingOperDefinedEvent .
	 *
	 * @param AtomicProductOfferingOperDefinedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingOperDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.commercialOperation(event.getOperationSpecifications());
		this.stateProductOffering.productOfferingTerm(event.getProductOfferingTerm());
	}

	/**
	 * Convert.
	 *
	 * @param category the category
	 * @return the category ref
	 */
	private CategoryRef convert(final Category category) {
		return new CategoryRef().baseType(category.getBaseType()).href(category.getHref()).id(category.getId())
				.schemaLocation(category.getSchemaLocation()).type(category.getType()).version(category.getVersion())
				.referredType("category");
	}

	/**
	 * Process the Product Offering Market command and define Market reference in
	 * product offering.
	 *
	 * @param selectedMarketIds
	 * @param queryService
	 * @author Diksha Srivastava
	 */



	private List<MarketSegmentRef> processProductOfferingMarket(Set<String> selectedMarketIds,
			QueryService queryService) {
		List<MarketSegmentRef> definedMarket = new ArrayList<>();
		List<String> invalidMarketSegments = new ArrayList<>();
		List<MarketSegmentAdmin> validMarketSegments = marketSegmentCache.computeIfAbsent("validMarketSegments", key -> queryService.fetchMarketSegments(accessToken));

		for (String segmentId : selectedMarketIds) {
			boolean isValidMarket = false;

			for (MarketSegmentAdmin marketSegment : validMarketSegments) {
				if (segmentId.equals(marketSegment.getId())) {
					isValidMarket = true;
					MarketSegmentRef marketSegmentRef = convertMarketSegmentAdminIntoRef(marketSegment);
					definedMarket.add(marketSegmentRef);
					this.stateProductOffering.addMarketSegmentItem(marketSegmentRef); // Assuming this is needed like in
																						// Channel
					break;
				}
			}

			if (!isValidMarket) {
				invalidMarketSegments.add(segmentId);
			}
		}

		if (!invalidMarketSegments.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_MARKET_SEGMENT,
					invalidMarketSegments.toString(), null);
		}

		return definedMarket;
	}

	private MarketSegmentRef convertMarketSegmentAdminIntoRef(MarketSegmentAdmin marketSegment) {
		return new MarketSegmentRef().id(marketSegment.getId()).name(marketSegment.getName());
	}

	/**
	 * Process the Product Offering Related Party command and define Related Party
	 * in product offering.
	 *
	 * @param relatedParties the Set
	 * @param queryService   the queryService
	 * @author Diksha Srivastava
	 */

	public List<RelatedParty> processProductOfferingRelatedParty(Set<RelatedParty> relatedParties,
			QueryService queryService) {
		return new ArrayList<>(relatedParties.stream().toList());

	}

	/**
	 * Process the product offering select allowed action command.
	 *
	 * @param command the command
	 * @throws Exception
	 */

	@CommandHandler
	public void processProductOfferingSelectAllowedActionCommand(
			ProductOfferingSelectAllowedActionCommand command) {

		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		/* ---------------------------------------------------
		 * 1. Only for CONTRACT Product Offering
		 * --------------------------------------------------- */
		if (this.stateProductOffering.getType() != ProductOfferingType.CONTRACT) {
			return; // optional step for non-contract PO
		}

		List<AllowedProductAction> allowedAction = command.getSelectAllowedAction();

		/* ---------------------------------------------------
		 * 2. allowedAction[] cannot be null
		 * --------------------------------------------------- */
		if (allowedAction == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_ALLOWED_ACTION_MANDATORY);
		}

		/* ---------------------------------------------------
		 * 3. At least one allowedAction is mandatory
		 * --------------------------------------------------- */
		if (allowedAction.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_ALLOWED_ACTION_MANDATORY);
		}

		/* ---------------------------------------------------
		 * 4. Validate actions subset
		 * --------------------------------------------------- */

		Map<String, CommercialOperation> commercialOperationById =
				this.stateProductOffering.getCommercialOperation()
						.stream()
						.collect(Collectors.toMap(
								CommercialOperation::getId,
								Function.identity()
						));


		Set<String> invalidActions = new HashSet<>();

		for (AllowedProductAction dto : allowedAction) {
			CommercialOperationRef actionRef = dto.getAction();

			if (actionRef == null || actionRef.getId() == null) {
				invalidActions.add(null);
				continue;
			}
			CommercialOperation matchingOperation =
					commercialOperationById.get(actionRef.getId());

			if (matchingOperation == null) {
				// ID NOT FOUND IN PO COMMERCIAL OPERATIONS
				invalidActions.add(actionRef.getId());
				continue;
			}
			actionRef.setName(matchingOperation.getName());

		}

		if (!invalidActions.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_UNRECONIZED_ACTIONS);
		}

		/* ---------------------------------------------------
		 * 5. Validate channels against ProvideIdentityData
		 * --------------------------------------------------- */
		Set<String> identityChannelIds =
				this.stateProductOffering.getChannel()
						.stream()
						.map(ChannelRef::getId)
						.collect(Collectors.toSet());

		Set<String> invalidChannels = new HashSet<>();

		for (AllowedProductAction dto : allowedAction) {
			if (dto.getChannelRef() != null) {
				for (ChannelRef ch : dto.getChannelRef()) {
					if (!identityChannelIds.contains(ch.getId())) {
						invalidChannels.add(ch.getId());
					}
				}
			}
		}

		if (!invalidChannels.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_UNRECONIZED_CHANNEL);
		}

		/* ---------------------------------------------------
		 * 6. Apply Event
		 * --------------------------------------------------- */
		AggregateLifecycle.apply(
				new ProductOfferingAllowedActionDefinedEvent(
						productOfferingId,
						allowedAction,
						lastUpdate
				)
		);
	}


	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingAllowedActionDefinedEvent .
	 *
	 * @param ProductOfferingAllowedActionDefinedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductOfferingAllowedActionDefinedEvent  event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.setAllowedAction(event.getAllowedAction());
		this.stateProductOffering.setLastUpdate(event.getLastUpdate());
	}


	/**
	 * Process to modify product offering select allowed action command.
	 *
	 * @param command the command
	 * @throws Exception
	 */

	@CommandHandler
	public void processModifyProductOfferingSelectAllowedActionCommand(
			ModifyProductOfferingSelectAllowedActionCommand command) {

		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		/* ---------------------------------------------------
		 * 1. Only for CONTRACT Product Offering
		 * --------------------------------------------------- */
		if (this.stateProductOffering.getType() != ProductOfferingType.CONTRACT) {
			return; // optional step for non-contract PO
		}

		List<AllowedProductAction> allowedAction = command.getSelectAllowedAction();

		/* ---------------------------------------------------
		 * 2. allowedAction[] cannot be null
		 * --------------------------------------------------- */
		if (allowedAction == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_ALLOWED_ACTION_MANDATORY);
		}

		/* ---------------------------------------------------
		 * 3. At least one allowedAction is mandatory
		 * --------------------------------------------------- */
		if (allowedAction.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_ALLOWED_ACTION_MANDATORY);
		}

		/* ---------------------------------------------------
		 * 4. Validate actions subset
		 * --------------------------------------------------- */

		Map<String, CommercialOperation> commercialOperationById =
				this.stateProductOffering.getCommercialOperation()
						.stream()
						.collect(Collectors.toMap(
								CommercialOperation::getId,
								Function.identity()
						));


		Set<String> invalidActions = new HashSet<>();

		for (AllowedProductAction dto : allowedAction) {
			CommercialOperationRef actionRef = dto.getAction();

			if (actionRef == null || actionRef.getId() == null) {
				invalidActions.add(null);
				continue;
			}
			CommercialOperation matchingOperation =
					commercialOperationById.get(actionRef.getId());

			if (matchingOperation == null) {
				// ID NOT FOUND IN PO COMMERCIAL OPERATIONS
				invalidActions.add(actionRef.getId());
				continue;
			}
			actionRef.setName(matchingOperation.getName());

		}

		if (!invalidActions.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_UNRECONIZED_ACTIONS);
		}

		/* ---------------------------------------------------
		 * 5. Validate channels against ProvideIdentityData
		 * --------------------------------------------------- */
		Set<String> identityChannelIds =
				this.stateProductOffering.getChannel()
						.stream()
						.map(ChannelRef::getId)
						.collect(Collectors.toSet());

		Set<String> invalidChannels = new HashSet<>();

		for (AllowedProductAction dto : allowedAction) {
			if (dto.getChannelRef() != null) {
				for (ChannelRef ch : dto.getChannelRef()) {
					if (!identityChannelIds.contains(ch.getId())) {
						invalidChannels.add(ch.getId());
					}
				}
			}
		}

		if (!invalidChannels.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_UNRECONIZED_CHANNEL);
		}

		/* ---------------------------------------------------
		 * 6. Apply Event
		 * --------------------------------------------------- */
		AggregateLifecycle.apply(
				new ModifyProductOfferingAllowedActionDefinedEvent(
						productOfferingId,
						allowedAction,
						lastUpdate
				)
		);
	}


	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingAllowedActionDefinedEvent .
	 *
	 * @param ModifyProductOfferingAllowedActionDefinedEvent
	 */
	@EventSourcingHandler
	public void on(final ModifyProductOfferingAllowedActionDefinedEvent  event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.setAllowedAction(event.getAllowedAction());
		this.stateProductOffering.setLastUpdate(event.getLastUpdate());
	}



	/**
	 * Process the product offering relationship command.
	 *
	 * @param command the command
	 * @throws Exception
	 */
	@CommandHandler
	public void productOfferingRelationshipCommand(ProductOfferingRelationshipCommand command,
			QueryService queryService, ModifyProductOfferingService modifyProductOfferingService) {

		ProductOffering currentProductOffering = this.stateProductOffering;
		for(ProductOfferingRelationship productOfferingRelationship: command.getProductOfferingRelationships()){
			if (Objects.equals(productOfferingRelationship.getId(), this.stateProductOfferingId)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATION);
			}
			if(productOfferingRelationship.getValidFor()==null)
				continue;
			TimePeriod validFor=productOfferingRelationship.getValidFor();
			if( validFor.getStartDateTime().isBefore(OffsetDateTime.now().minusMinutes(5))){
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_VALIDFOR_BEFORE_CURRENT_TIME,productOfferingRelationship.getId(),productOfferingRelationship.getId());
			}
			else if(validFor.getEndDateTime()!=null && validFor.getEndDateTime().isBefore(validFor.getStartDateTime())){
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_VALIDFOR_STARTTIME_ENDTIME,productOfferingRelationship.getId(),productOfferingRelationship.getId());

			}
		}

		if (this.stateProductOffering.getType().toString()
				.equalsIgnoreCase(ProductOfferingType.ATOMICPRODUCTOFFERING.toString())) {
			productOfferingAtomicRelationship(queryService, command, currentProductOffering);

		} else {
			productOfferingBundleAndContractRelationship(queryService, command, currentProductOffering);
		}

	}

	/**
	 * Process the product offering relationship command.
	 *
	 * @param command the command
	 * @throws Exception
	 */
	@CommandHandler
	public void productOfferingPolicyRuleAssociationCommand(ProductOfferingPolicyRuleAssociationCommand command,
			QueryService queryService, ModifyProductOfferingService modifyProductOfferingService) {

		AggregateLifecycle.apply(new ProductOfferingPolicyRuleAssociationDefinedEvent(command.getProductOfferingId(),
				command.getPolicyRuleRef(), OffsetDateTime.now()));

	}

	@CommandHandler
	public void modifyProductOfferingPolicyRuleAssociationCommand(ModifyProductOfferingPolicyRuleAssociationCommand command,
															QueryService queryService, ModifyProductOfferingService modifyProductOfferingService) {

		AggregateLifecycle.apply(new ProductOfferingPolicyRuleAssociationModifiedEvent(command.getProductOfferingId(),
				command.getPolicyRuleRef(), OffsetDateTime.now()));

	}



	private void productOfferingBundleAndContractRelationship(QueryService queryService,
			ProductOfferingRelationshipCommand command, ProductOffering currentProductOffering
			) {
		final List<Event> eventList = new ArrayList<>();
		List<ProductOfferingRelationship> relationshipNeedToSet = new ArrayList<>();
		String currentProductOfferingType = this.stateProductOffering.getType().toString();
		List<String> productOfferingIds = command.getProductOfferingRelationships().stream().map(ProductOfferingRelationship::getId).toList();
        List<ProductOffering> productOfferings = new ArrayList<>();
        if(!ObjectUtils.isEmpty(productOfferingIds)) {
            productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
        }
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		for (ProductOfferingRelationship productOfferingRelationship : command.getProductOfferingRelationships()) {
			String productOfferingRelationshipId = productOfferingRelationship.getId();
			ProductOffering productOfferingInRelationship = productOfferingMap.get(productOfferingRelationshipId);

			String relationProductOfferingType = productOfferingInRelationship.getType().toString();
			validateProductOfferingRelationship(currentProductOfferingType, relationProductOfferingType, productOfferingInRelationship);
			// This part represents the original "else" block logic
			if (productOfferingRelationship.getRelationshipType() == ProductOfferingRelationshipType.INCOMPATIBLE) {
				// Placeholder for future handling if needed
			}
			relationshipNeedToSet.add(productOfferingRelationship);

		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();

		if (eventList.isEmpty()) {
			currentProductOffering.setProductOfferingRelationship(relationshipNeedToSet);
			if (this.stateProductOffering.getType().equals(ProductOfferingType.BUNDLEPRODUCTOFFERING)) {
				AggregateLifecycle.apply(new BundleProductOfferingRelationshipDefinedEvent(
						currentProductOffering.getId(), command.getProductOfferingRelationships(), lastUpdate));
			} else if (this.stateProductOffering.getType().equals(ProductOfferingType.CONTRACT)) {
				AggregateLifecycle.apply(new ContractProductOfferingRelationshipDefinedEvent(
						currentProductOffering.getId(), command.getProductOfferingRelationships(), lastUpdate));

			}

		}

	}

	private void validateProductOfferingRelationship(String currentType, String relationType, ProductOffering relatedPO) {
		if (!Objects.equals(currentType, relationType)) {

			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED);
		}
		if (!isValidLifecycle(relatedPO.getLifecycleStatus())) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE);
		}
	}

	private boolean isValidLifecycle(ProductOfferingLifecycle status) {
		return ProductOfferingLifecycle.ACTIVE == status
				|| ProductOfferingLifecycle.LAUNCHED == status
				|| ProductOfferingLifecycle.INTEST == status;
	}

	private void productOfferingAtomicRelationship(QueryService queryService,
			ProductOfferingRelationshipCommand command, ProductOffering currentProductOffering
			) {
		List<ProductOfferingRelationship> relationshipNeedToSet = new ArrayList<>();
		List<String> productOfferingIds = command.getProductOfferingRelationships().stream().map(ProductOfferingRelationship::getId).toList();
		List<ProductOffering> productOfferings = new ArrayList<>();
		if(!ObjectUtils.isEmpty(productOfferingIds)) {
			productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
		}
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		for (ProductOfferingRelationship productOfferingRelationship : command.getProductOfferingRelationships()) {
			String productOfferingRelationshipId = productOfferingRelationship.getId();
			ProductOffering productOfferingInRelationship = productOfferingMap.get(productOfferingRelationshipId);
			//Checking PO Type must be same
			if(!productOfferingInRelationship.getType().equals(ProductOfferingType.ATOMICPRODUCTOFFERING)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED);
			}
			// checking po lifecycle must be active/launched/test to make relationship
			else if (!(ProductOfferingLifecycle.ACTIVE == productOfferingInRelationship.getLifecycleStatus()
					|| ProductOfferingLifecycle.LAUNCHED == productOfferingInRelationship.getLifecycleStatus()
					|| ProductOfferingLifecycle.INTEST == productOfferingInRelationship.getLifecycleStatus())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE);
			} else {
				relationshipNeedToSet.add(productOfferingRelationship);
			}
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		currentProductOffering.setProductOfferingRelationship(relationshipNeedToSet);
		AggregateLifecycle.apply(new AtomicProductOfferingRelationshipDefinedEvent(currentProductOffering.getId(),
				command.getProductOfferingRelationships(), lastUpdate));
	}

	private void evaluateProductOfferingRelatonships(ProductOffering storedProductOffering,
			List<ProductOfferingRelationship> selectedProductOfferingRelationships, QueryService queryService) {
		Set<ProductOfferingRelationship> deletePORelationships = new HashSet<>();
		Set<ProductOfferingRelationship> addPORelationships = new HashSet<>();
		for (ProductOfferingRelationship productOfferingRelationship : storedProductOffering
				.getProductOfferingRelationship()) {
			if (!selectedProductOfferingRelationships.contains(productOfferingRelationship)) {
				deletePORelationships.add(productOfferingRelationship);
			}
		}
		List<String> productOfferingIds = selectedProductOfferingRelationships.stream().map(ProductOfferingRelationship::getId).toList();
		List<ProductOffering> productOfferings = new ArrayList<>();
		if(!ObjectUtils.isEmpty(productOfferingIds)) {
			productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
		}
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		for (ProductOfferingRelationship productOfferingRelationship : selectedProductOfferingRelationships) {
			ProductOffering productOfferingInRelationship = productOfferingMap.get(productOfferingRelationship.getId());
			if (productOfferingInRelationship == null) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED);
			}
			if (!storedProductOffering.getProductOfferingRelationship().contains(productOfferingRelationship)) {
				addPORelationships.add(productOfferingRelationship);
			}
		}
		applyProductOfferingRelationshipModifiedEvent(deletePORelationships, addPORelationships,
				storedProductOffering.getType(), storedProductOffering.getId());
	}

	private void applyProductOfferingRelationshipModifiedEvent(Set<ProductOfferingRelationship> deletePORelationships,
			Set<ProductOfferingRelationship> addPORelationships, ProductOfferingType poType, String productOfferingId) {
		switch (poType) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply((new AtomicProductOfferingRelationshipModifiedEvent(productOfferingId,
					addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply((new BundleProductOfferingRelationshipModifiedEvent(productOfferingId,
					addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		case CONTRACT:
			AggregateLifecycle.apply((new ContractProductOfferingRelationshipModifiedEvent(productOfferingId,
					addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		default:
			break;
		}

	}

	private void applyProductOfferingIncompatibleRelationshipModifiedEvent(
			Set<ProductOfferingRelationship> deletePORelationships, Set<ProductOfferingRelationship> addPORelationships,
			ProductOfferingType poType, String productOfferingId) {
		switch (poType) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply((new AtomicProductOfferingIncompatibleRelationshipModifiedEvent(productOfferingId,
					addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply((new BundleProductOfferingIncompatibleRelationshipModifiedEvent(productOfferingId,
					addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		case CONTRACT:
			AggregateLifecycle
					.apply((new ContractProductOfferingIncompatibleRelationshipModifiedEvent(productOfferingId,
							addPORelationships, deletePORelationships, OffsetDateTime.now())));
			break;
		default:
			break;
		}

	}

	private ProductOfferingRelationship createProductOfferingRelationship(ProductOffering currentProductOffering) {
		return new ProductOfferingRelationship().id(currentProductOffering.getId())
				.href(currentProductOffering.getHref()).relationshipType(ProductOfferingRelationshipType.INCOMPATIBLE)
				.baseType(currentProductOffering.getBaseType()).validFor(currentProductOffering.getValidFor())
				.type(currentProductOffering.getType().getValue())
				.schemaLocation(currentProductOffering.getSchemaLocation());
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingRelationshipDefinedEvent .
	 *
	 * @param AtomicProductOfferingRelationshipDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingRelationshipDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.productOfferingRelationship(event.getProductOfferingRelationships());
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPolicyRuleAssociationDefinedEvent .
	 *
	 * @param ProductOfferingPolicyRuleAssociationDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final ProductOfferingPolicyRuleAssociationDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.policyRuleRef(event.getProductOfferingPolicyRuleAssociation());
	}

	@EventSourcingHandler
	public void on(final ProductOfferingPolicyRuleAssociationModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.policyRuleRef(event.getProductOfferingPolicyRuleAssociation()).lastUpdate(event.getLastUpdate());
	}


	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingRelationshipDefinedEvent .
	 *
	 * @param ContractProductOfferingRelationshipDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final BundleProductOfferingRelationshipDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.productOfferingRelationship(event.getProductOfferingRelationships());
	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingRelationshipDefinedEvent .
	 *
	 * @param ContractProductOfferingRelationshipDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final ContractProductOfferingRelationshipDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.productOfferingRelationship(event.getProductOfferingRelationships());
	}

	/**
	 * Processes the valid for of product offering.
	 *
	 * @param inputTimePeriod the TimePeriod
	 */
	public TimePeriod processProductOfferingValidForCommand(TimePeriod inputTimePeriod,
			ProductSpecification productSpecification) {
		if (this.stateProductOffering.getType().equals(ProductOfferingType.ATOMICPRODUCTOFFERING)) {
			TimePeriod restriction = productSpecification.getValidFor();
			if (!TimePeriodValidityUtil.checkTimePeriodRestriction(inputTimePeriod, restriction).isIsValid()) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_VALID_FOR);
			}
		}
		return inputTimePeriod;
	}

	/**
	 * This method is used to process term
	 *
	 * @param poTerms
	 * @param queryService
	 * @return
	 */
	private List<ProductOfferingTerm> processProductOfferingTerm(Set<ProductOfferingTerm> poTerms, QueryService queryService) {
		if (poTerms == null || poTerms.isEmpty()) {
			return new ArrayList<>();
		}

		validateProductOfferingTerm(poTerms, queryService);
		List<ProductOfferingTerm> savedPOTerm = this.stateProductOffering.getProductOfferingTerm();

		if (savedPOTerm != null && !savedPOTerm.isEmpty()) {
			Map<String, ProductOfferingTerm> savedTermMap = savedPOTerm.stream()
					.collect(Collectors.toMap(
							y -> y.getDuration().getAmount() + "-" + y.getDuration().getUnits(),
							Function.identity()
					));

			poTerms.forEach(x -> {
				String requestKey = x.getDuration().getAmount() + "-" + x.getDuration().getUnits();
				ProductOfferingTerm matched = savedTermMap.get(requestKey);
				if (matched != null) {
					x.setCommercialOperation(matched.getCommercialOperation());
				}
			});
		}

		// Return the updated list
		return new ArrayList<>(poTerms.stream().toList());
	}

	private void validateProductOfferingTerm(Set<ProductOfferingTerm> poTerms, QueryService queryService) {
		List<String> invalidPoTerm = new ArrayList<>();
		List<FrequencyTypes> fetchedFrequencyTypes = queryService.fetchFrequency(accessToken);
		for (ProductOfferingTerm requestedPOTerm : poTerms) {
			boolean isValidUnit = false;
			for (FrequencyTypes frequencyType : fetchedFrequencyTypes) {
				if (requestedPOTerm.getDuration().getUnits().equals(frequencyType.getFrequencyCode())) {
					isValidUnit = true;
					break;
				}
			}

			if (!isValidUnit) {
				invalidPoTerm.add(requestedPOTerm.getDuration().getUnits());
			}
		}

		if (!invalidPoTerm.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_TERM_UNIT, invalidPoTerm.toString(), invalidPoTerm.toString());
		}
	}

	/**
	 * This process method validates the product offerring and sets its lifecycle
	 * status to IN_STUDY and creates a new version number and sets isBundle to
	 * false for product offering.
	 *
	 * @param command the command
	 * @author Diksha Srivastava
//	 */

	@CommandHandler
	public void processProductOfferingValidatedCommand(ProductOfferingValidatedCommand command,
													   QueryService queryService) {
		String version = "0.1";
		if (isAtomicProductOffering()) {
			processAtomicProductOffering(queryService, version);
		} else {
			processNonAtomicProductOffering(queryService, version);
		}
		applyCategoryProjection();


	}

	private boolean isAtomicProductOffering() {
		return this.stateProductOffering.getType().toString()
				.equalsIgnoreCase(ProductOfferingType.ATOMICPRODUCTOFFERING.toString());
	}

	private void processAtomicProductOffering(QueryService queryService, String version) {

		String productSpecId = this.stateProductOffering.getProductSpecification().getId();
		ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
		ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
		if (!isValidProductSpecState(productSpecState)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(
				new AtomicProductOfferingBundleDefinedEvent(this.stateProductOffering.getId(), false, lastUpdate));
		AggregateLifecycle.apply(new AtomicProductOfferingValidatedEvent(this.stateProductOffering.getId(),
				ProductOfferingLifecycle.INTEST, lastUpdate));
		AggregateLifecycle.apply(new AtomicProductOfferingVersionCreatedEvent(this.stateProductOffering.getId(),
				version, lastUpdate));
		this.stateProductOffering.version(version);
		AggregateLifecycle.apply(new AtomicProductOfferingCreationCompletedEvent(this.stateProductOfferingId,
				this.stateProductOffering));

	}



	private boolean isValidProductSpecState(ProductSpecificationLifecycle state) {
		return state == ProductSpecificationLifecycle.ACTIVE || state == ProductSpecificationLifecycle.LAUNCHED;
	}


	private void processNonAtomicProductOffering(QueryService queryService, String version) {
		List<ProductOfferingRelationship> productOfferingRelationship = this.stateProductOffering
				.getProductOfferingRelationship();
		List<String> productOfferingIds = productOfferingRelationship.stream().map(ProductOfferingRelationship::getId).toList();
		List<ProductOffering> productOfferings = new ArrayList<>();
		if(!ObjectUtils.isEmpty(productOfferingIds)) {
			productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
		}
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		validateProductOfferingsState(productOfferingRelationship, productOfferingMap);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		this.stateProductOffering.lifecycleStatus(ProductOfferingLifecycle.INTEST);
		this.stateProductOffering.version(version);
		switch (this.stateProductOffering.getType()) {
			case BUNDLEPRODUCTOFFERING:
				AggregateLifecycle.apply(new BundleProductOfferingValidatedEvent(this.stateProductOffering.getId(),
						ProductOfferingLifecycle.INTEST, lastUpdate));

				AggregateLifecycle.apply(new BundleProductOfferingVersionCreatedEvent(this.stateProductOffering.getId(),
						version, lastUpdate));

				AggregateLifecycle.apply(new BundleProductOfferingCreationCompletedEvent(this.stateProductOffering));
				break;
			case CONTRACT:
				AggregateLifecycle.apply(new ContractProductOfferingValidatedEvent(this.stateProductOffering.getId(),
						ProductOfferingLifecycle.INTEST, lastUpdate));

				AggregateLifecycle.apply(new ContractProductOfferingVersionCreatedEvent(
						this.stateProductOffering.getId(), version, lastUpdate));

				AggregateLifecycle.apply(new ContractProductOfferingCreationCompletedEvent(this.stateProductOffering));
				break;
			default:
				break;
		}
	}

	private void validateProductOfferingsState(List<ProductOfferingRelationship> relationships,
											   Map<String, ProductOffering> productOfferingMap) {
		for (ProductOfferingRelationship relation : relationships) {
			ProductOfferingLifecycle lifecycleStatus = productOfferingMap.get(relation.getId()).getLifecycleStatus();
			if (!isValidProductOfferingState(lifecycleStatus)) {
				throw new DiscoManagedClientException(
						ProductOffConstants.DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED);
			}
		}
	}


	private boolean isValidProductOfferingState(ProductOfferingLifecycle state) {
		return state == ProductOfferingLifecycle.ACTIVE
				|| state == ProductOfferingLifecycle.LAUNCHED
				|| state == ProductOfferingLifecycle.INTEST;
	}

	private void applyCategoryProjection() {
		List<String> categoryIds = this.stateProductOffering.getCategory().stream()
				.map(CategoryRef::getId)
				.toList();
		AggregateLifecycle.apply(new ProductOfferingInternalProjectionEvent(this.stateProductOffering.getId(), categoryIds));
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingBundleDefinedEvent .
	 *
	 * @param AtomicProductOfferingBundleDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingBundleDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.isBundle(event.isIsBundle());

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingValidatedEvent .
	 *
	 * @param AtomicProductOfferingValidatedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingValidatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.lifecycleStatus(event.getLifecycleStatus());

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingVersionCreatedEvent .
	 *
	 * @param AtomicProductOfferingVersionCreatedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingVersionCreatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.version(event.getProductOfferingVersion());

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingCreationCompletedEvent .
	 *
	 * @param AtomicProductOfferingCreationCompletedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingCreationCompletedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering = event.getProductOffering();
	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingValidatedEvent .
	 *
	 * @param ContractProductOfferingValidatedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingValidatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.lifecycleStatus(event.getLifecycleStatus());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingVersionCreatedEvent .
	 *
	 * @param ContractProductOfferingVersionCreatedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingVersionCreatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.version(event.getProductOfferingVersion());

	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingCreationCompletedEvent .
	 *
	 * @param ContractProductOfferingCreationCompletedEvent
	 */
	@EventSourcingHandler
	public void on(final ContractProductOfferingCreationCompletedEvent event) {
		// to be Implemented
	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingValidatedEvent .
	 *
	 * @param ContractProductOfferingValidatedEvent
	 */
	@EventSourcingHandler
	public void on(final ContractProductOfferingValidatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.lifecycleStatus(event.getLifecycleStatus());

	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingVersionCreatedEvent .
	 *
	 * @param ContractProductOfferingVersionCreatedEvent
	 */
	@EventSourcingHandler
	public void on(final ContractProductOfferingVersionCreatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.version(event.getProductOfferingVersion());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingCreationCompletedEvent .
	 *
	 * @param BundleProductOfferingCreationCompletedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingCreationCompletedEvent event) {
		this.stateProductOffering = event.getProductOffering();
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingInternalProjectionEvent .
	 *
	 * @param ProductOfferingInternalProjectionEvent
	 */
	@EventSourcingHandler
	public void on(final ProductOfferingInternalProjectionEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();

	}

	/**
	 * This method generate the event to remove the incomplete product offering
	 * which have lifecycle inStudy.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processProductOffCancelCommand(ProductOffCancelCommand command) {
		String productOffId = command.getProductOffId();
		ProductOffering productOffering = this.stateProductOffering;
		if (!isProductOffStateValid(productOffering.getLifecycleStatus())) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_STATE_INSTUDY);

		}
		AggregateLifecycle.apply(new ProductOffCancelledEvent(productOffId, productOffering));

	}

	/**
	 * updates the state of aggregate after applying ProductOffCancelledEvent .
	 *
	 * @param ProductOffCancelledEvent
	 */

	@EventSourcingHandler
	public void on(final ProductOffCancelledEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();

	}

	/**
	 * Check the product offering lifeCycleState; if lifeCycle is inStudy return
	 * true, otherwise false.
	 *
	 * @param lifeCycleState the life cycle state
	 * @return the boolean
	 */
	private boolean isProductOffStateValid(ProductOfferingLifecycle lifeCycleState) {
		return lifeCycleState.equals(ProductOfferingLifecycle.INSTUDY);
	}

	/**
	 * Process the Associate POP to Operation Specification Command to link the POP
	 * with Atomic Product Offering.
	 *
	 * @param command the command
	 * @author Varshika Choudhary
	 */
	@CommandHandler
	public void processAssociatePOPtoOperationSpecificationCommand(
			final AssociatePOPtoOperationSpecificationCommand command, QueryService queryService) {
		validateProductSpecificationState(queryService);

		List<CommercialOperation> associatePOPtoOperation = new ArrayList<>();
		List<ProductOfferingTerm> associatePOPOperationToTerm = new ArrayList<>();

		processPriceAssociationCreationAndModification(command.getAssociatePOPtoOperationSpecList(), associatePOPtoOperation, associatePOPOperationToTerm, queryService);
		applyAggregrateLifeycleEvent(associatePOPtoOperation, associatePOPOperationToTerm);
	}

	private void processPriceAssociationCreationAndModification(List<AssociatePOPtoOperationSpec> requestedAssociatePOPtoOperationSpecList, List<CommercialOperation> associatePOPtoOperation, List<ProductOfferingTerm> associatePOPOperationToTerm, QueryService queryService) {

		//commercial operation
		List<CommercialOperation> savedOperationSpecifications = this.stateProductOffering.getCommercialOperation();
		for (CommercialOperation operation : savedOperationSpecifications) {
			operation.carries(new ArrayList<>());
			associatePOPtoOperation.add(operation);
		}

		//term
		List<ProductOfferingTerm> productOfferingTerm = this.stateProductOffering.getProductOfferingTerm();
		Set<String> uniqueTermDurations = new HashSet<>();
		if (!productOfferingTerm.isEmpty()) {
			for (ProductOfferingTerm term : productOfferingTerm) {
				Duration quantity = term.getDuration();
				String quantityKey = quantity.getAmount() + "-" + quantity.getUnits().toLowerCase();
				uniqueTermDurations.add(quantityKey);
				term.commercialOperation(new ArrayList<>());
				associatePOPOperationToTerm.add(term);
			}
		}

		List<String> productOfferingPriceIds =

				requestedAssociatePOPtoOperationSpecList.stream()
						.map(AssociatePOPtoOperationSpec::getProductOfferingPriceId)
						.toList();
		if(!productOfferingPriceIds.isEmpty()) {
		List<ProductOfferingPrice> productOfferingPrices = queryService.fetchProductOfferingPriceByIds(productOfferingPriceIds, accessToken);
		Map<String, ProductOfferingPrice> productOfferingPriceMap = productOfferingPrices.stream().collect(Collectors.toMap(ProductOfferingPrice::getId, Function.identity()));


			processAssociations(requestedAssociatePOPtoOperationSpecList, savedOperationSpecifications, associatePOPtoOperation, uniqueTermDurations, associatePOPOperationToTerm, productOfferingPriceMap);
		}
	}


	private void applyAggregrateLifeycleEvent(List<CommercialOperation> associatePOPtoOperation,
											  List<ProductOfferingTerm> productOfferingTerm) {
		String productOfferingId = this.stateProductOffering.getId();
		if (associatePOPtoOperation.isEmpty()) {
			AggregateLifecycle
					.apply(new LinkPOPtoOperEvent(productOfferingId, this.stateProductOffering.getCommercialOperation(),productOfferingTerm, OffsetDateTime.now()));
		} else {
			AggregateLifecycle
					.apply(new LinkPOPtoOperEvent(productOfferingId, associatePOPtoOperation,productOfferingTerm, OffsetDateTime.now()));

		}
	}

	private void handleErrors(Set<String> invalidOpsSelected, Set<String> invalidPopId,
			Map<String, String> invalidPOPStatus) {
		if (!invalidPopId.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_ID);
		} else if (!invalidOpsSelected.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_OPERATION_ID);

		} else if (!invalidPOPStatus.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_STATUS);
		}
	}

	private void processAssociations(List<AssociatePOPtoOperationSpec> requestedAssociatePOPtoOperationSpecList,
									 List<CommercialOperation> savedOperationSpecifications, List<CommercialOperation> associatePOPtoOperation,
									 Set<String> uniqueDurations, List<ProductOfferingTerm> associatePOPOperationToTerm, Map<String, ProductOfferingPrice> productOfferingPriceMap) {

		Set<String> invalidOpsSelected = new HashSet<>();
		Set<String> invalidPopId = new HashSet<>();
		Map<String, String> invalidPOPStatus = new HashMap<>();
		for (AssociatePOPtoOperationSpec requestedAssociatePOPtoOperationItem : requestedAssociatePOPtoOperationSpecList) {
			//check if term association is  valid
			if (requestedAssociatePOPtoOperationItem.getProductOfferingTerm() != null) {
				Duration duration = CommonUtil.convertToDuration(requestedAssociatePOPtoOperationItem.getProductOfferingTerm().getDuration());
				String targetDurationKey = duration.getAmount() + "-" + duration.getUnits().trim().toLowerCase();
				if (!uniqueDurations.contains(targetDurationKey)) {
					throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_TERM_ASSOCIATE_CHARGE_STEP);
				}
			}

			String operSpecId = requestedAssociatePOPtoOperationItem.getOperationSpecId();
			String productOfferingPriceId = requestedAssociatePOPtoOperationItem.getProductOfferingPriceId();
			boolean isValid = false;
			for (CommercialOperation operation : savedOperationSpecifications) {
				if (operation.getId().equals(operSpecId)) {
					associatePOP(associatePOPtoOperation, invalidPopId, productOfferingPriceId, operation,
							invalidPOPStatus, productOfferingPriceMap, requestedAssociatePOPtoOperationItem.getProductOfferingTerm(), associatePOPOperationToTerm);
					isValid = true;
					break;
				}
			}
			if (!isValid) {
				invalidOpsSelected.add(requestedAssociatePOPtoOperationItem.getOperationSpecId());
			}
		}
		handleErrors(invalidOpsSelected, invalidPopId, invalidPOPStatus);
	}

	private void validateProductSpecificationState(QueryService queryService) {
		if (this.stateProductOffering.getType().toString()
				.equalsIgnoreCase(ProductOfferingType.ATOMICPRODUCTOFFERING.toString())) {
			String productSpecId = this.stateProductOffering.getProductSpecification().getId();
			ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
			ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
			if (!(ProductSpecificationLifecycle.ACTIVE == productSpecState
					|| ProductSpecificationLifecycle.LAUNCHED == productSpecState)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);

			}
		}
	}


	/**
	 * updates the state of aggregate after applying LinkPOPtoOperEvent .
	 *
	 * @param LinkPOPtoOperEvent
	 */
	@EventSourcingHandler
	public void on(final LinkPOPtoOperEvent event) {
		this.stateProductOfferingId = event.getProdOffId();
		this.stateProductOffering.commercialOperation(event.getOperationList());
		this.stateProductOffering.productOfferingTerm(event.getProductOfferingTerm());
	}

	/**
	 * Associate POP validates pop based on id and sets the data in operation
	 * specification.
	 *
	 * @param queryService
	 * @param associatePOPtoOperation     the associate PO pto operation
	 * @param invalidPopId                the invalid pop id
	 * @param productOfferingPriceId      the product offering price id
	 * @param operation                   the operation
	 * @param invalidPOPStatus            the invalid POP status
	 * @param associatePOPOperationToTerm
	 * @param uniqueDurations
	 */
	private void associatePOP(List<CommercialOperation> associatePOPtoOperation, Set<String> invalidPopId,
							  String productOfferingPriceId, CommercialOperation operation, Map<String, String> invalidPOPStatus,
							  Map<String, ProductOfferingPrice> productOfferingPriceMap, AssociatePOPtoTerm  associatePOPtoTerm, List<ProductOfferingTerm> associatePOPOperationToTerm) {
		ProductOfferingPrice productOfferingPrice = productOfferingPriceMap.get(productOfferingPriceId);
		if (productOfferingPrice == null) {
			invalidPopId.add(productOfferingPriceId);
			return;

		}
		validatePOPAssociation(productOfferingPrice);
		validatePOPLifecycleStatus(productOfferingPrice, invalidPOPStatus, productOfferingPriceId);
		ProductOfferingPriceRef popRef = new ProductOfferingPriceRef().id(productOfferingPrice.getId())
				.name(productOfferingPrice.getName()).type(productOfferingPrice.getType().toString())
				.href(productOfferingPrice.getHref()).baseType(productOfferingPrice.getBaseType())
				.schemaLocation(productOfferingPrice.getSchemaLocation());


		//term association is sent
		if (associatePOPtoTerm != null) {
			associateToTerm(associatePOPtoTerm, associatePOPOperationToTerm, operation, popRef);
		}
		else {
			associatePOPtoOperation.stream()
					.filter(x -> x.getId().equals(operation.getId()))
					.findFirst()
					.ifPresent(x -> {
						List<ProductOfferingPriceRef> carries = new ArrayList<>(x.getCarries()); // make mutable copy
						carries.add(popRef);
						x.setCarries(carries); // set the updated list back
					});
		}
	}

	private void validatePOPAssociation(ProductOfferingPrice productOfferingPrice) {
		if (!productOfferingPrice.getType().equals(ProductOfferingPriceType.INSTALLMENTCHARGE)
			&& (isInvalidContractPrepaidAssociation(productOfferingPrice)) ){
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_ASSOCIATION);

		}
	}

	private boolean isInvalidContractPrepaidAssociation(ProductOfferingPrice productOfferingPrice) {
		return this.stateProductOffering.getType().equals(ProductOfferingType.CONTRACT)
				&& productOfferingPrice.getPriceType().equals(PriceType.NRC)
				&& productOfferingPrice.getType().equals(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE)
				&& this.stateProductOffering.getBillingType().equals(BillingType.PREPAID)
				&& !productOfferingPrice.getImmediatePayment();


	}

	private void validatePOPLifecycleStatus(ProductOfferingPrice productOfferingPrice,
			Map<String, String> invalidPOPStatus, String productOfferingPriceId) {
		if (productOfferingPrice.getLifecycleStatus().equals(ProductOfferingPriceLifecycle.OBSOLETE)
				|| productOfferingPrice.getLifecycleStatus().equals(ProductOfferingPriceLifecycle.RETIRED)
				|| productOfferingPrice.getLifecycleStatus().equals(ProductOfferingPriceLifecycle.UNAVAILABLE)) {
			invalidPOPStatus.put(productOfferingPriceId, productOfferingPrice.getLifecycleStatus().toString());
		}
	}

	private void associateToTerm(
			AssociatePOPtoTerm associatePOPtoTerm,
			List<ProductOfferingTerm> associatePOPOperationToTerm,
			CommercialOperation operation,
			ProductOfferingPriceRef popRef) {

        Duration targetDuration = CommonUtil.convertToDuration(associatePOPtoTerm.getDuration());
		String targetDurationKey = buildDurationKey(targetDuration);

		associatePOPOperationToTerm.stream()
				.filter(term -> buildDurationKey(term.getDuration()).equals(targetDurationKey))
				.forEach(term -> addPOPToTermOperation(term, operation, popRef));
	}


	private void addPOPToTermOperation(ProductOfferingTerm term, CommercialOperation operation, ProductOfferingPriceRef popRef) {
		List<CommercialOperation> operations = term.getCommercialOperation();

		Optional<CommercialOperation> existingOp = operations.stream()
				.filter(op -> op.getId().equals(operation.getId()))
				.findFirst();

		if (existingOp.isPresent()) {
			addPOPToOperation(existingOp.get(), popRef);
		} else {
			CommercialOperation copiedOperation = new CommercialOperation(operation);
			copiedOperation.setCarries(List.of(popRef));
			operations.add(copiedOperation);
		}
	}

	private void addPOPToOperation(CommercialOperation operation, ProductOfferingPriceRef popRef) {
		List<ProductOfferingPriceRef> carries = new ArrayList<>(operation.getCarries());
		carries.add(popRef);
		operation.setCarries(carries);
	}

	private String buildDurationKey(Duration duration) {
		return duration.getAmount() + "-" + duration.getUnits().trim().toLowerCase();
	}



	/**
	 * Process the Bundled Product Offering Operation command and define Commercial
	 * Operations in product offering.
	 *
	 * @param command the command
	 * @return the list
	 * @author Vishal Vachaspati
	 */
	@CommandHandler
	public void processBundledProductOfferingOperationCommand(BundledProductOfferingOperationCommand command) {
		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		switch (this.stateProductOffering.getType()) {
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingOperDefinedEvent(productOfferingId,
					command.getBundledproductOffOperationSpecification(), lastUpdate));
			break;
		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingOperDefinedEvent(productOfferingId,
					command.getBundledproductOffOperationSpecification(), lastUpdate));
			break;
		default:
			break;
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingOperDefinedEvent .
	 *
	 * @param ContractProductOfferingOperDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final BundleProductOfferingOperDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.commercialOperation(event.getOperationSpecifications());

	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingOperDefinedEvent .
	 *
	 * @param ContractProductOfferingOperDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final ContractProductOfferingOperDefinedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.commercialOperation(event.getOperationSpecifications());

	}

	/**
	 * Manage the Bundled Product Offering command and define Bundle Product
	 * Offerings in product offering.
	 *
	 * @param command the command
	 * @return the list
	 * @author Vishal Vachaspati
	 */
	@CommandHandler
	public void processManageProductOfferingBundlingCommand(ManageProductOfferingBundlingCommand command,
			QueryService queryService) {
		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductOffering productOffering = queryService.fetchProductOfferingById(productOfferingId, accessToken);
		TimePeriod timePeriod = productOffering.getValidFor();
		List<BundledProductOffering> bundledProductOfferings = new ArrayList<>();
		if (command.getBundleProductOfferings() != null) {
			validityUnderlying(command.getBundleProductOfferings(), timePeriod, queryService);
			managePOBundlingHelper(command.getBundleProductOfferings(), command.getGlobalMaxCardinality(),
					command.getGlobalMinCardinality(), queryService, bundledProductOfferings);
			AggregateLifecycle.apply(new ChildPOInfoAddedEvent(this.stateProductOfferingId, bundledProductOfferings,
					lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
		}
		applyEventsForPOBundling(command, productOfferingId, bundledProductOfferings, lastUpdate);
	}

    private void validityUnderlying(List<BundledProductOffering> requestBundledProductOfferingss,TimePeriod timePeriod,QueryService queryService) {
		List<String> productOfferingIds = requestBundledProductOfferingss.stream().map(BundledProductOffering::getId).toList();
		List<ProductOffering> productOfferings = new ArrayList<>();
		if(!ObjectUtils.isEmpty(productOfferingIds)) {
			productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);

		}
		if(productOfferings==null ||productOfferings.isEmpty()){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST,
					productOfferingIds.toString(), null);
		}
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		for (BundledProductOffering bundledProductOffering : requestBundledProductOfferingss) {
			ProductOffering po = fetchAndValidateProductOffering(bundledProductOffering,productOfferingMap);
			OffsetDateTime bundledEndDateTime = po.getValidFor().getEndDateTime();
			OffsetDateTime startDateTime = timePeriod.getStartDateTime();
			OffsetDateTime now = OffsetDateTime.now();
			if (bundledEndDateTime != null && now.isAfter(bundledEndDateTime)) {
				String res=po.getId()+"- "+po.getName();
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO,res,res);
			}
			if (bundledEndDateTime != null && startDateTime.isAfter(bundledEndDateTime)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_EXPIRED_PO);
			}
		}
	}

	private void managePOBundlingHelper(List<BundledProductOffering> requestBundledProductOfferings,
			int globalMaxCardinality, int globalMinCardinality, QueryService queryService,
			List<BundledProductOffering> bundledProductOfferings) {

		validateCardinality(globalMaxCardinality, globalMinCardinality);
		int sumOfNumberRelOfferLowerLimit = 0;
		int sumOfNumberRelOfferUpperLimit = 0;
		// main set or outer set
		Set<String> offeringIds = new HashSet<>();
		List<String> productOfferingIds = requestBundledProductOfferings.stream().map(BundledProductOffering::getId).toList();
		List<ProductOffering> productOfferings = new ArrayList<>();
		if(!ObjectUtils.isEmpty(productOfferingIds)) {
			productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
		}
		if(productOfferings==null ||productOfferings.isEmpty()){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST,
					productOfferingIds.toString(), null);
		}
		Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));

		for (BundledProductOffering bundledProductOffering : requestBundledProductOfferings) {
			ProductOffering po = fetchAndValidateProductOffering(bundledProductOffering,productOfferingMap);
			validateBundlesList(bundledProductOffering, offeringIds, queryService, productOfferingMap);
			if(this.stateProductOffering.getType().equals(ProductOfferingType.CONTRACT) && this.stateProductOffering.getBillingType().equals(BillingType.PREPAID)) {
				getImmediatePaymentData(po, queryService);
			}

			bundledProductOffering.type(po.getType().getValue());
			bundledProductOfferings.add(bundledProductOffering);
			BundledProductOfferingOption bundledProductOfferingOption = bundledProductOffering
					.getBundledProductOfferingOption();
			validateOfferingLimits(bundledProductOfferingOption,po);
			sumOfNumberRelOfferLowerLimit += bundledProductOfferingOption.getNumberRelOfferLowerLimit();
			sumOfNumberRelOfferUpperLimit += bundledProductOfferingOption.getNumberRelOfferUpperLimit();

		}
		if (globalMaxCardinality > sumOfNumberRelOfferUpperLimit){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_BPO_GLOBAL_MAX_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT);
		}
		else if(globalMaxCardinality < sumOfNumberRelOfferLowerLimit){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_BPO_GLOBAL_MAX_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT);
		}

		if(globalMinCardinality < sumOfNumberRelOfferLowerLimit){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_BPO_GLOBAL_MIN_CARDINALITY_LESS_THAN_CUMULATIVE_LOWER_LIMIT);
		}
		else if(globalMinCardinality > sumOfNumberRelOfferUpperLimit){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_BPO_GLOBAL_MIN_CARDINALITY_GREATER_THAN_CUMULATIVE_UPPER_LIMIT);
		}

	}


	private void validateBundlesList(BundledProductOffering bundledProductOffering, Set<String> offeringIds, QueryService queryService, Map<String, ProductOffering> productOfferingMap) {
		ProductOffering productOffering = productOfferingMap.get(bundledProductOffering.getId());
		if(offeringIds.contains(productOffering.getId())){
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_INVALID_PO_HIERARCHY,productOffering.getName(),null);
		}else{
			if(ProductOfferingType.ATOMICPRODUCTOFFERING.equals(productOffering.getType())){
				offeringIds.add(productOffering.getId());
			} else {
				bfs(productOffering, offeringIds, queryService);
			}
		}
	}




	private void bfs(ProductOffering productOffering, Set<String> offeringIds, QueryService queryService) {
		Set<String> visitedSet = new HashSet<>();
		Queue<ProductOffering> queue = new LinkedList<>();
		visitedSet.add(productOffering.getId());
		queue.add(productOffering);

		while (!queue.isEmpty()) {
			ProductOffering currentOffering = queue.poll();
			processCurrentOffering(currentOffering, offeringIds, visitedSet, queue, queryService);
		}
	}


	private void processCurrentOffering(ProductOffering currentOffering, Set<String> offeringIds,
										Set<String> visitedSet, Queue<ProductOffering> queue, QueryService queryService) {

		if (currentOffering == null) {
			return; // Avoid NPE
		}

		if (offeringIds.contains(currentOffering.getId())) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_INVALID_PO_HIERARCHY,
					currentOffering.getName(), null);
		} else {
			offeringIds.add(currentOffering.getId());
		}

		List<BundledProductOffering> childPOList = currentOffering.getBundledProductOffering();
		if (childPOList != null) { // Avoid NPE
			List<ProductOffering> productOfferings = fetchChildProductOfferings(childPOList, queryService);
			Map<String, ProductOffering> productOfferingMap = productOfferings.stream()
					.collect(Collectors.toMap(ProductOffering::getId, Function.identity()));

			enqueueChildOfferings(childPOList, visitedSet, queue, productOfferingMap);
		}

	}


	private List<ProductOffering> fetchChildProductOfferings(List<BundledProductOffering> childPOList,
															 QueryService queryService) {

		List<String> productOfferingIds = childPOList.stream()
				.map(BundledProductOffering::getId)
				.toList();

		if (ObjectUtils.isEmpty(productOfferingIds)) {
			return new ArrayList<>();
		}

		return queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
	}

	private void enqueueChildOfferings(List<BundledProductOffering> childPOList, Set<String> visitedSet,
									   Queue<ProductOffering> queue, Map<String, ProductOffering> productOfferingMap) {

		for (BundledProductOffering bpo : childPOList) {
			if (!visitedSet.contains(bpo.getId())) {
				visitedSet.add(bpo.getId());
				ProductOffering offering = productOfferingMap.get(bpo.getId());
				queue.add(offering);
			}
		}
	}
	private ProductOffering fetchAndValidateProductOffering(BundledProductOffering bundledProductOffering, Map<String, ProductOffering> productOfferingMap) {
		ProductOffering po =productOfferingMap.get(bundledProductOffering.getId());
		if (po == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BUNDLING_PO_DOES_NOT_EXIST,
					bundledProductOffering.getId(), null);
		}
		if (po.getType().equals(ProductOfferingType.CONTRACT)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_NOT_CPO_CANNOTBE_CHILDFOR_PO);
		}

		return po;
	}

	private void validateCardinality(int globalMaxCardinality, int globalMinCardinality) {
		if (globalMinCardinality > globalMaxCardinality) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_BPO_MINMAX_CARDINALITY);
		}
	}

	private void validateOfferingLimits(BundledProductOfferingOption bundledProductOfferingOption,ProductOffering po) {
		int numberRelOfferLowerLimit = bundledProductOfferingOption.getNumberRelOfferLowerLimit();
		int numberRelOfferUpperLimit = bundledProductOfferingOption.getNumberRelOfferUpperLimit();
		int numberRelOfferDefault = bundledProductOfferingOption.getNumberRelOfferDefault();
		if (numberRelOfferLowerLimit > numberRelOfferDefault || numberRelOfferLowerLimit > numberRelOfferUpperLimit
				|| numberRelOfferDefault > numberRelOfferUpperLimit) {
			String res=po.getId()+"- "+po.getName();
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BPO_OPERATION_LIMIT,res,res);
		}
	}


	private void 	getImmediatePaymentData(ProductOffering productOffering, QueryService queryService) {
		checkCommercialOperations(productOffering,queryService);
		if(productOffering.getType()== ProductOfferingType.BUNDLEPRODUCTOFFERING){
			List<String> productOfferingIds = productOffering.getBundledProductOffering().stream().map(BundledProductOffering::getId).toList();
			List<ProductOffering> productOfferings = new ArrayList<>();
			if(!ObjectUtils.isEmpty(productOfferingIds)) {
				productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
			}
			Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
			for (BundledProductOffering bundledProductOffering : productOffering.getBundledProductOffering()) {
				ProductOffering po = productOfferingMap.get(bundledProductOffering.getId());

					if (po != null && this.stateProductOffering.getType().equals(ProductOfferingType.CONTRACT)) {
						getImmediatePaymentData(po, queryService);

				}
			}
		}
	}

	private void checkCommercialOperations(ProductOffering productOffering, QueryService queryService) {
		for (CommercialOperation commOperation : productOffering.getCommercialOperation()) {
			if(commOperation.getCarries() != null) {
				List<String> productOfferingPriceIds = commOperation.getCarries().stream().map(ProductOfferingPriceRef::getId).toList();
				List<ProductOfferingPrice> productOfferingPrices = queryService.fetchProductOfferingPriceByIds(productOfferingPriceIds, accessToken);
				Map<String, ProductOfferingPrice> productOfferingPriceMap = productOfferingPrices.stream().collect(Collectors.toMap(ProductOfferingPrice::getId, Function.identity()));
				for (ProductOfferingPriceRef popRef : commOperation.getCarries()) {
					if (this.stateProductOffering.getBillingType().equals(BillingType.PREPAID) && popRef.getType().equals(ProductOfferingPriceType.PRODUCTOFFERINGPRICECHARGE.toString())) {
						checkImmediatePaymentFlag(popRef,  productOfferingPriceMap);
					}
				}
			}
		}
	}

	private void checkImmediatePaymentFlag(ProductOfferingPriceRef popRef, Map<String, ProductOfferingPrice> productOfferingPriceMap) {
		ProductOfferingPrice productOfferingPrice = productOfferingPriceMap.get(popRef.getId());
		Boolean immediatePayment = productOfferingPrice.getImmediatePayment(); // may be null

		if (this.stateProductOffering.getBillingType().equals(BillingType.PREPAID)
				&& productOfferingPrice.getPriceType().equals(PriceType.NRC)
				&& (immediatePayment == null || !immediatePayment)) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_POP_ASSOCIATION);
		}
	}

	/**
	 * This method is to apply events for bundle and contract product offering
	 *
	 * @param command
	 * @param productOfferingId
	 * @param bundledProductOfferings
	 * @param lastUpdate
	 */
	private void applyEventsForPOBundling(ManageProductOfferingBundlingCommand command, String productOfferingId,
			List<BundledProductOffering> bundledProductOfferings, OffsetDateTime lastUpdate) {
		switch (this.stateProductOffering.getType()) {
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingSelectedEvent(productOfferingId, bundledProductOfferings,
					lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
			break;
		case CONTRACT:
			AggregateLifecycle
					.apply(new ContractProductOfferingSelectedEvent(productOfferingId, bundledProductOfferings,
							lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
			break;
		default:
			break;
		}

	}

	/**
	 * updates the state of aggregate after applying ChildPOInfoAddedEvent .
	 *
	 * @param ChildPOInfoAddedEvent
	 */
	@EventSourcingHandler
	public void on(final ChildPOInfoAddedEvent event) {
		stateProductOffering.bundledProductOffering(event.getBundleProductOffering());
	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingSelectedEvent .
	 *
	 * @param BundleProductOfferingSelectedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingSelectedEvent event) {

		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.globalMinCardinality(event.getGlobalMinCardinality())
				.globalMaxCardinality(event.getGlobalMaxCardinality())
				.bundledProductOffering(event.getBundleProductOffering());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingSelectedEvent .
	 *
	 * @param ContractProductOfferingSelectedEvent
	 */
	@EventSourcingHandler
	public void on(final ContractProductOfferingSelectedEvent event) {

		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.globalMinCardinality(event.getGlobalMinCardinality())
				.globalMaxCardinality(event.getGlobalMaxCardinality())
				.bundledProductOffering(event.getBundleProductOffering());

	}

	@CommandHandler
	public void processProductOfferingModificationCancelCommand(ProductOfferingModificationCancelCommand command) {
		ProductOffering productOffering = this.stateProductOffering;
		AggregateLifecycle.apply(new ModifyProductOffCancelledEvent(command.getProductOffId(), productOffering));
	}

	/**
	 * updates the state of aggregate after applying ModifyProductOffCancelledEvent
	 * .
	 *
	 * @param ModifyProductOffCancelledEvent
	 */

	@EventSourcingHandler
	public void on(final ModifyProductOffCancelledEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering = event.getProductOffering();
	}

	@CommandHandler
	public void processPOModificationCommand(POModificationCommand command, QueryService queryService) {
		String productOfferingId = command.getPoId();
		ProductOffering po = queryService.fetchProductOfferingById(productOfferingId, accessToken);
		if (null == po) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_NOT_EXSISTS, productOfferingId, null);
		} else {
			ProductOfferingLifecycle lifecycleStatus = po.getLifecycleStatus();
			if (lifecycleStatus.equals(ProductOfferingLifecycle.INTEST)
					|| lifecycleStatus.equals(ProductOfferingLifecycle.LAUNCHED)
					|| lifecycleStatus.equals(ProductOfferingLifecycle.ACTIVE)
					|| lifecycleStatus.equals(ProductOfferingLifecycle.RETIRED)) {
				AggregateLifecycle.apply(new ProductOfferingModificationInitiatedEvent(productOfferingId, po,
						OffsetDateTime.now(), lifecycleStatus));
			} else {
				throw new DiscoManagedClientException(
						ProductOffConstants.DISCO_PO_INVALID_PO_STATE_INTEST_ACTIVE_LAUNCHED);
			}
		}

	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingModificationInitiatedEvent .
	 *
	 * @param ProductOfferingModificationInitiatedEvent
	 */
	@EventSourcingHandler
	public void on(final ProductOfferingModificationInitiatedEvent event) {
		this.stateProductOffering = event.getProductOffering();
	}

	/**
	 * Modify the Product Offering Description command and define Product Offerings
	 * in product offering.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processModifyProductOfferingIdentityDataCommand(ModifyProductOfferingIdentityDataCommand command,
			QueryService queryService) {
		String productOfferingId = this.stateProductOffering.getId();
		ProductOffering po = queryService.fetchProductOfferingById(productOfferingId, accessToken);
		if (null == po) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_NOT_EXSISTS, productOfferingId, null);
		}
		ProductOfferingLifecycle lifecycleState = po.getLifecycleStatus();
		ProductOfferingLifecycle currentlifecycleState = po.getLifecycleStatus();
		if (command.getLifecycleStatus() != null) {
			lifecycleState = command.getLifecycleStatus();
		}
		if ((currentlifecycleState.equals(ProductOfferingLifecycle.LAUNCHED)
				|| currentlifecycleState.equals(ProductOfferingLifecycle.RETIRED))
				&& (currentlifecycleState.equals(lifecycleState))) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE);
		}
		if (currentlifecycleState.equals(ProductOfferingLifecycle.RETIRED)) {
			isValidStatus(lifecycleState, productOfferingId);
		}
		List<ChannelRef> channels = processProductOfferingChannel(command.getChannelIds(), queryService);
		List<MarketSegmentRef> marketSegments = processProductOfferingMarket(command.getMarketSegmentIds(),
				queryService);
		List<RelatedParty> relatedParties = processProductOfferingRelatedParty(command.getRelatedParties(),
				queryService);
		List<ProductOfferingTerm> poTerms = processProductOfferingTerm(command.getPoTerms(),queryService);
		if (command.getIdentityData().getIsSellable() == null) {
			command.getIdentityData().setIsSellable(false);
		}
		if (command.getType().equals(ProductOfferingType.ATOMICPRODUCTOFFERING)) {
			String productSpecId = this.stateProductOffering.getProductSpecification().getId();
			ProductSpecification productSpecification = queryService.fetchProductSpecById(productSpecId, accessToken);
			ProductSpecificationLifecycle productSpecState = productSpecification.getLifecycleStatus();
			processProductOfferingValidForCommand(command.getValidity(), productSpecification);

			if (!(ProductSpecificationLifecycle.ACTIVE == productSpecState
					|| ProductSpecificationLifecycle.LAUNCHED == productSpecState)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_STATE);
			}
			AggregateLifecycle.apply(new AtomicProductOfferingIdentityDataModifiedEvent(productOfferingId,
					command.getIdentityData(), channels, marketSegments, relatedParties, poTerms,
					processProductOfferingValidForCommand(command.getValidity(), productSpecification),
					command.getType(), command.getStatusReason(), OffsetDateTime.now(), lifecycleState,
					currentlifecycleState));
		} else {
			AggregateLifecycle.apply(new BundleProductOfferingIdentityDataModifiedEvent(productOfferingId,
					command.getIdentityData(), channels, marketSegments, relatedParties, poTerms,
					processProductOfferingValidForCommand(command.getValidity(), null), command.getType(),
					command.getStatusReason(), OffsetDateTime.now(), lifecycleState, currentlifecycleState));
		}
	}

	private boolean isValidStatus(ProductOfferingLifecycle lifecycleState, String productOfferingId) {
		if (lifecycleState.equals(ProductOfferingLifecycle.OBSOLETE)) {
			// admin API call
				if (adminQueryService.fetchCPIBConfiguration() && cpibQueryService.fetchProductByProductOfferingId(productOfferingId)) { // CPIB API call
					throw new DiscoManagedClientException("Entiy is in use in CPIB");

			}
		} else {
			throw new DiscoManagedClientException("only lifecycle status can be updated to obsolete");
		}
		return true;
	}

	@CommandHandler
	public void processModifyContractProductOfferingDescriptionCommand(
			final ModifyContractProductOfferingDescriptionCommand command, QueryService queryService) {

		String productOfferingId = this.stateProductOffering.getId();
		ProductOffering po = queryService.fetchProductOfferingById(productOfferingId, accessToken);
		if (null == po) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_NOT_EXSISTS, productOfferingId, null);
		}
		ProductOfferingLifecycle lifecycleState = po.getLifecycleStatus();
		ProductOfferingLifecycle currentlifecycleState = po.getLifecycleStatus();
		if (command.getLifecycleStatus() != null) {
			lifecycleState = command.getLifecycleStatus();
		}
		if ((currentlifecycleState.equals(ProductOfferingLifecycle.LAUNCHED)
				|| currentlifecycleState.equals(ProductOfferingLifecycle.RETIRED))
				&& (currentlifecycleState.equals(lifecycleState))) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_LIFECYCLE);
		}
		if (currentlifecycleState.equals(ProductOfferingLifecycle.RETIRED)) {
			isValidStatus(lifecycleState, productOfferingId);
		}
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		List<ChannelRef> channels = processProductOfferingChannel(command.getChannelIds(), queryService);
		List<MarketSegmentRef> marketSegments = processProductOfferingMarket(command.getMarketSegmentIds(),
				queryService);
		List<RelatedParty> relatedParties = processProductOfferingRelatedParty(command.getRelatedParties(),
				queryService);
		List<ProductOfferingTerm> poTerms = processProductOfferingTerm(command.getPoTerms(), queryService);
		command.getIdentityData().setIsSellable(true);
		AggregateLifecycle.apply(new ContractProductOfferingIdentityDataModifiedEvent(productOfferingId,
				command.getIdentityData(), channels, marketSegments, relatedParties, poTerms, command.getValidity(),
				command.getType(), command.getStatusReason(), lastUpdate, lifecycleState, currentlifecycleState));
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingIdentityDataModifiedEvent .
	 *
	 * @param AtomicProductOfferingIdentityDataModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingIdentityDataModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.brand(event.getIdentityData().getBrand()).name(event.getIdentityData().getName())
				.statusReason(event.getStatusReason())
				.isSellable(event.getIdentityData().getIsSellable())
				.isVisible(event.getIdentityData().getIsVisible())
				.type(event.getType()).isInstallable(event.getIdentityData().getIsInstallable())
				.channel(event.getChannels()).productOfferingTerm(event.getPoTerms())
				.marketSegment(event.getMarketSegments()).validFor(event.getValidity())
				.relatedParty(event.getRelatedParties()).lifecycleStatus(event.getLifecycleStatus());

	}

	@EventSourcingHandler
	public void on(final BundleProductOfferingIdentityDataModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.brand(event.getIdentityData().getBrand()).name(event.getIdentityData().getName())
				.statusReason(event.getStatusReason()).isSellable(event.getIdentityData().getIsSellable()).isVisible(event.getIdentityData().getIsVisible())
				.type(event.getType()).isInstallable(event.getIdentityData().getIsInstallable())
				.channel(event.getChannels()).productOfferingTerm(event.getPoTerms())
				.marketSegment(event.getMarketSegments()).validFor(event.getValidity())
				.relatedParty(event.getRelatedParties()).lifecycleStatus(event.getLifecycleStatus());

	}

	@EventSourcingHandler
	public void on(final ContractProductOfferingIdentityDataModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.description(event.getIdentityData().getDescription())
				.brand(event.getIdentityData().getBrand()).name(event.getIdentityData().getName())
				.statusReason(event.getStatusReason()).type(event.getType())
				.isSellable(event.getIdentityData().getIsSellable())
				.isVisible(event.getIdentityData().getIsVisible())
				.billingType(event.getIdentityData().getBillingType())
				.isInstallable(event.getIdentityData().getIsInstallable()).channel(event.getChannels())
				.productOfferingTerm(event.getPoTerms()).marketSegment(event.getMarketSegments())
				.validFor(event.getValidity()).relatedParty(event.getRelatedParties())
				.lifecycleStatus(event.getLifecycleStatus());

	}

	/**
	 * Modify the Product Offering Category command and define Product Offerings in
	 * product offering.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processModifyProductOfferingCategoryCommand(ModifyProductOfferingCategoryCommand command,
			QueryService queryService, Publisher publisher) {

		List<String> selectedCategoryIds = command.getCategories();

		ProductOffering productOffering = queryService.fetchProductOfferingById(stateProductOfferingId, accessToken);
		CategoryEntityRelationship entity = queryService.fetchCategoryEntityById(stateProductOfferingId);
		Set<CategoryRef> deleteCategories = new HashSet<>();
		if (entity != null) {
			deleteCategories = entity.getCategories();
		}
		Set<CategoryRef> addCategories = new HashSet<>();
		for (CategoryRef categoryRef : productOffering.getCategory()) {
			if (selectedCategoryIds.remove(categoryRef.getId())) {
				deleteCategories = deleteCategories.stream().filter(x -> !x.getId().equals(categoryRef.getId()))
						.collect(Collectors.toSet());
			}
		}
		List<Category> categories = new ArrayList<>();
		if(!ObjectUtils.isEmpty(selectedCategoryIds)) {
			categories = queryService.fetchCategoryByIds(selectedCategoryIds, accessToken);
		}
		Map<String, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));
		for (String categoryId : selectedCategoryIds) {
			Category category = categoryMap.get(categoryId);
			validateCategory(category);
			CategoryRef categoryRef = convert(category);
			addCategories.add(categoryRef);

		}
		switch (this.stateProductOffering.getType()) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply(new AtomicProductOfferingCategoryModifiedEvent(this.stateProductOffering.getId(),
					addCategories, deleteCategories, OffsetDateTime.now()));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingCategoryModifiedEvent(this.stateProductOffering.getId(),
					addCategories, deleteCategories, OffsetDateTime.now()));
			break;

		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingCategoryModifiedEvent(this.stateProductOffering.getId(),
					addCategories, deleteCategories, OffsetDateTime.now()));
			break;
		}

	}

	@CommandHandler
	public void process(ModifyAdditionProductOfferingCategoryCommand command) {
		ProductOfferingType.fromValue(command.getProductOfferingType());
		switch (ProductOfferingType.fromValue(command.getProductOfferingType())) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply(new AtomicProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), command.getCategories(), null, OffsetDateTime.now()));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), command.getCategories(), null, OffsetDateTime.now()));
			break;
		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), command.getCategories(), null, OffsetDateTime.now()));
			break;
		}

	}

	@CommandHandler
	public void process(ModifyDeletionProductOfferingCategoryCommand command) {
		ProductOfferingType.fromValue(command.getProductOfferingType());
		switch (ProductOfferingType.fromValue(command.getProductOfferingType())) {
		case ATOMICPRODUCTOFFERING:
			AggregateLifecycle.apply(new AtomicProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), null, command.getCategories(), OffsetDateTime.now()));
			break;
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), null, command.getCategories(), OffsetDateTime.now()));
			break;
		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingIndirectCategoryModifiedEvent(
					command.getProductOfferingId(), null, command.getCategories(), OffsetDateTime.now()));
			break;
		}

	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingCategoryModifiedEvent .
	 *
	 * @param AtomicProductOfferingCategoryModifiedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingCategoryModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.setCategory(event.getAddCategories());
	}

	@EventSourcingHandler
	public void on(final BundleProductOfferingCategoryModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.setCategory(event.getAddCategories());
	}

	@EventSourcingHandler
	public void on(final ContractProductOfferingCategoryModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.setCategory(event.getAddCategories());
	}

	/**
	 * Modify the Product Offering operation command and define Product Offerings in
	 * product offering.
	 *
	 * @param command the command
	 */

	@CommandHandler
	public void processModifyProductOfferingOperationCommand(ModifyProductOfferingOperationCommand command,
			QueryService queryService) {

		String productOfferingId = this.stateProductOffering.getId();
		ProductOffering po = queryService.fetchProductOfferingById(productOfferingId, accessToken);
		ProductSpecification productSpecification = queryService.fetchProductSpecById(po.getProductSpecification().getId(), accessToken);
		//for special case if ModifyAssociatePOPtoOperationSpecification run before ModifyProductOfferingOperation
		List<CommercialOperation> stateOperations = this.stateProductOffering.getCommercialOperation();
		List<CommercialOperation> requestedOperations = command.getProductOffOperationSpecification();
		List<OperationSpecification> psOperations = productSpecification.getOperationSpecification();
		List<CommercialOperation> operationSelected = new ArrayList<>();
		Set<String> invalidOpsSelected = new HashSet<>();
		Map<String, TimePeriod> invalidValidFor = new HashMap<>();
		for (CommercialOperation requestedOperation : requestedOperations) {
			boolean isValid = false;
			boolean isTimePeriod = false;
			for (OperationSpecification psOperation : psOperations) {
				if (requestedOperation.getId().equals(psOperation.getId())) {
					isValid = true;
					TimePeriod input = requestedOperation.getValidFor();
					TimePeriod restriction = psOperation.getValidFor();
					if (TimePeriodValidityUtil.checkTimePeriodRestriction(input, restriction).isIsValid()) {
						isTimePeriod = true;
						requestedOperation.setCarries(getAssociatedPOP(stateOperations, requestedOperation));
						operationSelected.add(requestedOperation);
						break;
					}
				}
			}
			if (!isValid) {
				invalidOpsSelected.add(requestedOperation.getId());
			} else if (!isTimePeriod) {
				invalidValidFor.put(requestedOperation.getId(), requestedOperation.getValidFor());
			}
		}
		if (!invalidOpsSelected.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_OPERATION, invalidOpsSelected.toString(), null);
		} else if (!invalidValidFor.isEmpty()) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_VALID_FOR);
		}

		List<ProductOfferingTerm> updatedPOTerm = updateCommercialOperationInTerms( operationSelected);
		AggregateLifecycle.apply(new AtomicProductOfferingOperationModifiedEvent(productOfferingId, operationSelected,updatedPOTerm, OffsetDateTime.now()));
	}

	private List<ProductOfferingPriceRef> getAssociatedPOP(List<CommercialOperation> prev,
			CommercialOperation operation) {
		List<CommercialOperation> op = prev.stream()
				.filter(x -> x.getId().equals(operation.getId()) && x.getName().equals(operation.getName()))
				.toList();
		if (!op.isEmpty()) {
			return op.get(0).getCarries();
		}
		return new ArrayList<>();
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingOperationModifiedEvent .
	 *
	 * @param AtomicProductOfferingOperationModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingOperationModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.commercialOperation(event.getOperationSpecifications());
		this.stateProductOffering.productOfferingTerm(event.getProductOfferingTerm());

	}

	@CommandHandler
	public void processModifyBundledProductOfferingOperationCommand(
			ModifyBundledProductOfferingOperationCommand command) {
		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		switch (this.stateProductOffering.getType()) {
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle.apply(new BundleProductOfferingOperModifiedEvent(productOfferingId,
					command.getBundledproductOffOperationSpecification(), lastUpdate));
			break;
		case CONTRACT:
			AggregateLifecycle.apply(new ContractProductOfferingOperModifiedEvent(productOfferingId,
					command.getBundledproductOffOperationSpecification(), lastUpdate));
			break;
		default:
			break;
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingOperDefinedEvent .
	 *
	 * @param ContractProductOfferingOperDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final BundleProductOfferingOperModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.commercialOperation(event.getOperationSpecifications());

	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingOperDefinedEvent .
	 *
	 * @param ContractProductOfferingOperDefinedEvent
	 */

	@EventSourcingHandler
	public void on(final ContractProductOfferingOperModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.commercialOperation(event.getOperationSpecifications());

	}

	@CommandHandler
	public void processModifyManageProductOfferingBundlingCommand(ModifyManageProductOfferingBundlingCommand command,
			QueryService queryService) {
		String productOfferingId = this.stateProductOffering.getId();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		List<BundledProductOffering> bundledProductOfferings = new ArrayList<>();
		if (command.getBundleProductOfferings() != null) {

			managePOBundlingHelper(command.getBundleProductOfferings(), command.getGlobalMaxCardinality(),
					command.getGlobalMinCardinality(), queryService, bundledProductOfferings);
			AggregateLifecycle.apply(new ChildPOInfoModifiedEvent(this.stateProductOfferingId, bundledProductOfferings,
					lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
		}
		applyEventsForPOBundlingModification(command, productOfferingId, bundledProductOfferings, lastUpdate);
	}

	/**
	 * This method is to apply events for bundle and contract product offering
	 *
	 * @param command
	 * @param productOfferingId
	 * @param bundledProductOfferings
	 * @param lastUpdate
	 */
	private void applyEventsForPOBundlingModification(ModifyManageProductOfferingBundlingCommand command,
			String productOfferingId, List<BundledProductOffering> bundledProductOfferings, OffsetDateTime lastUpdate) {
		switch (this.stateProductOffering.getType()) {
		case BUNDLEPRODUCTOFFERING:
			AggregateLifecycle
					.apply(new BundleProductOfferingSelectedModifiedEvent(productOfferingId, bundledProductOfferings,
							lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
			break;
		case CONTRACT:
			AggregateLifecycle
					.apply(new ContractProductOfferingSelectedModifiedEvent(productOfferingId, bundledProductOfferings,
							lastUpdate, command.getGlobalMinCardinality(), command.getGlobalMaxCardinality()));
			break;
		default:
			break;
		}

	}

	/**
	 * updates the state of aggregate after applying ChildPOInfoModifiedEvent .
	 *
	 * @param ChildPOInfoModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final ChildPOInfoModifiedEvent event) {

		stateProductOffering.bundledProductOffering(event.getBundleProductOffering());

	}

	/**
	 * updates the state of aggregate after applying
	 * BundleProductOfferingSelectedModifiedEvent .
	 *
	 * @param BundleProductOfferingSelectedModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final BundleProductOfferingSelectedModifiedEvent event) {

		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.globalMinCardinality(event.getGlobalMinCardinality())
				.globalMaxCardinality(event.getGlobalMaxCardinality())
				.bundledProductOffering(event.getBundleProductOffering());

	}

	/**
	 * updates the state of aggregate after applying
	 * ContractProductOfferingSelectedModifiedEvent .
	 *
	 * @param ContractProductOfferingSelectedModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final ContractProductOfferingSelectedModifiedEvent event) {

		this.stateProductOfferingId = event.getProductOfferingId();
		stateProductOffering.globalMinCardinality(event.getGlobalMinCardinality())
				.globalMaxCardinality(event.getGlobalMaxCardinality())
				.bundledProductOffering(event.getBundleProductOffering());

	}

	/**
	 * Modify the Product Offering associatePop command and define Product Offerings
	 * in product offering.
	 *
	 * @param command the command
	 */


	@CommandHandler
	public void processModifyAssociatePOPtoOperationSpecificationCommand(
			ModifyAssociatePOPtoOperationSpecificationCommand command, QueryService queryService) {
		validateProductSpecificationState(queryService);
		String productOfferingId = this.stateProductOffering.getId();

		List<CommercialOperation> associatePOPtoOperation = new ArrayList<>();
		List<ProductOfferingTerm> associatePOPOperationToTerm = new ArrayList<>();

		processPriceAssociationCreationAndModification(command.getAssociatePOPtoOperationSpecList(), associatePOPtoOperation, associatePOPOperationToTerm, queryService);
		AggregateLifecycle.apply(new LinkPOPtoOperModifiedEvent(productOfferingId, associatePOPtoOperation, associatePOPOperationToTerm, OffsetDateTime.now()));
	}

	/**
	 * updates the state of aggregate after applying LinkPOPtoOperModifiedEvent .
	 *
	 * @param LinkPOPtoOperModifiedEvent
	 */

	@EventSourcingHandler
	public void on(final LinkPOPtoOperModifiedEvent event) {
		this.stateProductOffering.commercialOperation(event.getOperationList());
		this.stateProductOfferingId = event.getProdOffId();
		this.stateProductOffering.productOfferingTerm(event.getProductOfferingTerm());
	}

	/**
	 * Modify the Product Offering Characteristic command and define Product
	 * Offerings in product offering.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processModifyProductOfferingCharacteristicCommand(
			ModifyProductOfferingCharacteristicCommand command,
			QueryService queryService) {

		String productOfferingId = this.stateProductOffering.getId();
		List<PickAtomicProductOfferingCharacteristic> incomingCharacteristics =
				command.getPickAtomicProductOfferingCharacteristics();

		if (incomingCharacteristics == null || incomingCharacteristics.isEmpty()) {
			return;
		}

		List<PickAtomicProductOfferingCharacteristic> userCharacteristics = new ArrayList<>();
		List<PickAtomicProductOfferingCharacteristic> productCharacteristics = new ArrayList<>();
		List<ProductSpecificationCharacteristicValueUse> selectedCharacteristics = new ArrayList<>();
		HashSet<String> duplicateNames = new HashSet<>();

		categorizeCharacteristics(incomingCharacteristics, duplicateNames, productCharacteristics, userCharacteristics);

		if (!userCharacteristics.isEmpty()) {
			validateAndAddUserChar(userCharacteristics, selectedCharacteristics);
		}

		if (!productCharacteristics.isEmpty()) {
			ProductOffering po = queryService.fetchProductOfferingById(productOfferingId, accessToken);
			processProductCharacteristics(productCharacteristics, queryService, po.getProductSpecification().getId(), selectedCharacteristics);
		}

		// Apply event after processing
		AggregateLifecycle.apply(
				new AtomicProductOfferingCharacteristicsModifiedEvent(
						productOfferingId,
						selectedCharacteristics,
						OffsetDateTime.now()
				)
		);
	}

	private void categorizeCharacteristics(
			List<PickAtomicProductOfferingCharacteristic> incomingCharacteristics,
			HashSet<String> duplicateNames,
			List<PickAtomicProductOfferingCharacteristic> productCharacteristics,
			List<PickAtomicProductOfferingCharacteristic> userCharacteristics) {

		for (PickAtomicProductOfferingCharacteristic poCharacteristic : incomingCharacteristics) {
			checkDuplicateCharName(duplicateNames, poCharacteristic);

			if (poCharacteristic.getBaseType() == null) {
				productCharacteristics.add(poCharacteristic);
			} else if (poCharacteristic.getBaseType().equals(ProductOffConstants.USER_PRODOFF_CHAR)) {
				if (poCharacteristic.getId() == null) {
					checkAndSetCharId(poCharacteristic, this.stateProductOffering.getId(), userCharacteristics.size() + 1);
				}
				userCharacteristics.add(poCharacteristic);
			} else {
				throw new DiscoManagedClientException(
						ProductOffConstants.DISCO_PO_INVALID_PO_CHAR_INVALID_BASETYPE,
						poCharacteristic.getName(),
						poCharacteristic.getName()
				);
			}
		}
	}

	private void processProductCharacteristics(
			List<PickAtomicProductOfferingCharacteristic> productCharacteristics,
			QueryService queryService,
			String productSpecificationId,
			List<ProductSpecificationCharacteristicValueUse> selectedCharacteristics) {


		ProductSpecification productSpecification = queryService
				.fetchProductSpecById( productSpecificationId, accessToken);

		validateProductSpecificationStateInCharacteristics(productSpecification);

		List<ProductSpecificationCharacteristic> specCharacteristics = productSpecification.getProductSpecCharacteristic();

		Set<InvalidCharacteristics> invalidCharacteristics = new LinkedHashSet<>();
		HashMap<String, Boolean> checkDuplicateId = new HashMap<>();

		processCharacteristics(
				productCharacteristics,
				specCharacteristics,
				selectedCharacteristics,
				invalidCharacteristics,
				checkDuplicateId
		);

		if (!invalidCharacteristics.isEmpty()) {
			String reason = invalidCharacteristics.stream()
					.map(ic -> ic.getPickAtomicProductOfferingCharacteristic().getId())
					.collect(Collectors.joining(", ", "[", "]"));
			throw new DiscoManagedClientException(
					ProductOffConstants.DISCO_PO_INVALID_PO_CHARACTRISTICS,
					reason,
					reason
			);
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingCharacteristicsModifiedEvent .
	 *
	 * @param AtomicProductOfferingCharacteristicsModifiedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingCharacteristicsModifiedEvent event) {
		this.stateProductOffering.prodSpecCharValueUse(event.getProductSpecificationCharacteristicValueUse());
		this.stateProductOfferingId = event.getProductOfferingId();
	}

	/**
	 * Modify the Product Offering relationship command and define Product Offerings
	 * in product offering.
	 *
	 * @param command the command
	 */
	@CommandHandler
	public void processModifyProductOfferingRelationshipCommand(ModifyProductOfferingRelationshipCommand command,
			QueryService queryService) {
		Set<String> productOfferingRelationshipIds = command.getProductOfferingRelationships().stream()
				.map(ProductOfferingRelationship::getId).collect(Collectors.toSet());
		for (String productOfferingRelationshipId : productOfferingRelationshipIds) {
			if (Objects.equals(productOfferingRelationshipId, this.stateProductOfferingId)) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PO_RELATIONSHIP_SELECTED);
			}

		}

		evaluateProductOfferingRelatonships(queryService.fetchProductOfferingById(this.stateProductOffering.getId(), null),
				command.getProductOfferingRelationships(), queryService);

	}

	@CommandHandler
	public void modifyProductOfferingIncompatibleRelationshipCommand(
			ModifyProductOfferingIncompatibleRelationshipCommand command, QueryService queryService) {
		ProductOfferingRelationship poRelationship = createProductOfferingRelationship(
				queryService.fetchProductOfferingById(command.getAddIncompatibleProductOfferingsRelationship(), accessToken));
		if (command.getIdAdd().booleanValue()) {
			applyProductOfferingIncompatibleRelationshipModifiedEvent(new HashSet<>(), Set.of(poRelationship),
					command.getProductOfferingType(), command.getProductOfferingId());
		} else {
			applyProductOfferingIncompatibleRelationshipModifiedEvent(Set.of(poRelationship), new HashSet<>(),
					command.getProductOfferingType(), command.getProductOfferingId());

		}
	}

	@EventSourcingHandler
	public void on(AtomicProductOfferingIncompatibleRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
	}

	@EventSourcingHandler
	public void on(BundleProductOfferingIncompatibleRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
	}

	@EventSourcingHandler
	public void on(ContractProductOfferingIncompatibleRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingRelationshipModifiedEvent .
	 *
	 * @param AtomicProductOfferingRelationshipModifiedEvent
	 */
	@EventSourcingHandler
	public void on(final AtomicProductOfferingRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		List<ProductOfferingRelationship> poRelatioships = new ArrayList<>();
		if (!CollectionUtils.isEmpty(event.getAddProductOfferingRelationships())) {
			poRelatioships.addAll(event.getAddProductOfferingRelationships());
		}
		if (!CollectionUtils.isEmpty(event.getDeleteProductOfferingRelationships())) {
			poRelatioships.addAll(event.getDeleteProductOfferingRelationships());
		}

		this.stateProductOffering.productOfferingRelationship(poRelatioships);
	}

	@EventSourcingHandler
	public void on(final BundleProductOfferingRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		List<ProductOfferingRelationship> poRelatioships = new ArrayList<>();
		if (!CollectionUtils.isEmpty(event.getAddProductOfferingRelationships())) {
			poRelatioships.addAll(event.getAddProductOfferingRelationships());
		}
		if (!CollectionUtils.isEmpty(event.getDeleteProductOfferingRelationships())) {
			poRelatioships.addAll(event.getDeleteProductOfferingRelationships());
		}

		this.stateProductOffering.productOfferingRelationship(poRelatioships);
	}

	@EventSourcingHandler
	public void on(final ContractProductOfferingRelationshipModifiedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		List<ProductOfferingRelationship> poRelatioships = new ArrayList<>();
		if (!CollectionUtils.isEmpty(event.getAddProductOfferingRelationships())) {
			poRelatioships.addAll(event.getAddProductOfferingRelationships());
		}
		if (!CollectionUtils.isEmpty(event.getDeleteProductOfferingRelationships())) {
			poRelatioships.addAll(event.getDeleteProductOfferingRelationships());
		}

		this.stateProductOffering.productOfferingRelationship(poRelatioships);
	}

	/**
	 * Modify the Product Offering valid for command and define Product Offerings in
	 * product offering.
	 *
	 * @param command the command
	 */

	/**
	 * Modify the Product Offering Validated command and define Product Offerings in
	 * product offering.
	 *
	 * @param command the command
	 */


	@CommandHandler
	public List<Event> process(AtomicProductOfferingModifiedValidatedCommand command, QueryService queryService) {
		List<Event> eventList = new ArrayList<>();
		ProductOffering modifiedProductOffering = this.stateProductOffering;
		ProductOffering storedProductOffering = queryService.fetchProductOfferingById(command.getProductOffId(), accessToken);

		// Check if Product Offering is transitioning to LAUNCHED
		if (ProductOfferingLifecycle.LAUNCHED.equals(modifiedProductOffering.getLifecycleStatus())) {
			validateProductLifecycle(modifiedProductOffering, queryService);
		}
		updateVersion(modifiedProductOffering, storedProductOffering);

		applyLifecycleEvent(modifiedProductOffering, storedProductOffering);

		return eventList;
	}

	// Validate lifecycle status based on Product Offering type
	private void validateProductLifecycle(ProductOffering modifiedProductOffering, QueryService queryService) {
		if (!ProductOfferingLifecycle.LAUNCHED.equals(modifiedProductOffering.getLifecycleStatus())) {
			return; // No validation required for non-launched states
		}

		if (ProductOfferingType.ATOMICPRODUCTOFFERING.equals(modifiedProductOffering.getType())) {
			validateProductSpecLifecycle(queryService, modifiedProductOffering.getProductSpecification().getId());
		} else if (isBundleOrContract(modifiedProductOffering)) {
			validateBundledProductOfferings(queryService, modifiedProductOffering.getBundledProductOffering());
		}
	}

	// Validate product specification lifecycle status
	private void validateProductSpecLifecycle(QueryService queryService, String productSpecId) {
		ProductSpecification productSpec = queryService.fetchProductSpecById(productSpecId, accessToken);
		if (!ProductSpecificationLifecycle.LAUNCHED.equals(productSpec.getLifecycleStatus())) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_PS_LIFECYCLE_STATUS);
		}
	}

	// Validate bundled product offerings' lifecycle status
	private void validateBundledProductOfferings(QueryService queryService, List<BundledProductOffering> bundledProducts) {
		List<String> productOfferingIds = bundledProducts.stream().map(BundledProductOffering::getId).collect(Collectors.toList());
        List<ProductOffering> productOfferings = new ArrayList<>();
        if(!ObjectUtils.isEmpty(productOfferingIds)) {
            productOfferings = queryService.fetchProductOfferingsByIds(productOfferingIds, accessToken);
        }
        Map<String, ProductOffering> productOfferingMap = productOfferings.stream().collect(Collectors.toMap(ProductOffering::getId, Function.identity()));
		for (BundledProductOffering bpos : bundledProducts) {
			ProductOffering bpo = productOfferingMap.get(bpos.getId());
			if (!ProductOfferingLifecycle.LAUNCHED.getValue().equalsIgnoreCase(bpo.getLifecycleStatus().getValue())) {
				throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_INVALID_BPO_LIFECYCLE_STATUS,
						bpo.getId(), bpo.getId());
			}
		}
	}

	// Determine if a product offering is a bundle or contract
	private boolean isBundleOrContract(ProductOffering offering) {
		return ProductOfferingType.BUNDLEPRODUCTOFFERING.equals(offering.getType())
				|| ProductOfferingType.CONTRACT.equals(offering.getType());
	}

	// Update the version of the modified product offering
	private void updateVersion(ProductOffering modifiedProductOffering, ProductOffering storedProductOffering) {
		String version = modifiedProductOffering.getVersion();

		if (ProductOfferingLifecycle.LAUNCHED.equals(modifiedProductOffering.getLifecycleStatus())) {
			version = (Integer.parseInt(version.substring(0, 1)) + 1) + ".0";
		} else if (ProductOfferingLifecycle.ACTIVE.equals(modifiedProductOffering.getLifecycleStatus())
				|| !ProductOfferingLifecycle.INTEST.equals(storedProductOffering.getLifecycleStatus())) {
			version = version.substring(0, 2) + (Integer.parseInt(version.substring(2)) + 1);
		}

		modifiedProductOffering.setVersion(version);
	}

	// Apply the appropriate lifecycle event based on the product type
	private void applyLifecycleEvent(ProductOffering modifiedProductOffering, ProductOffering storedProductOffering) {
		if (stateProductOffering == null) {
			throw new DiscoManagedClientException(ProductOffConstants.DISCO_PO_NOT_EXSISTS, this.stateProductOfferingId,
					null);
		}

		Event event;
		switch (stateProductOffering.getType()) {
		case ATOMICPRODUCTOFFERING:
			event = new AtomicProductOfferingModificationValidatedEvent(this.stateProductOfferingId,
					storedProductOffering, modifiedProductOffering.getLifecycleStatus(), OffsetDateTime.now(),
					modifiedProductOffering.getVersion());
			break;
		case CONTRACT:
			event = new ContractProductOfferingModificationValidatedEvent(this.stateProductOfferingId,
					modifiedProductOffering, modifiedProductOffering.getLifecycleStatus(), OffsetDateTime.now(),
					modifiedProductOffering.getVersion());
			break;
		default:
			event = new BundleProductOfferingModificationValidatedEvent(this.stateProductOfferingId,
					storedProductOffering, modifiedProductOffering.getLifecycleStatus(), OffsetDateTime.now(),
					modifiedProductOffering.getVersion());
			break;
		}

		AggregateLifecycle.apply(event);
	}

	/**
	 * updates the state of aggregate after applying
	 * AtomicProductOfferingModificationValidatedEvent .
	 *
	 * @param AtomicProductOfferingModificationValidatedEvent
	 */

	@EventSourcingHandler
	public void on(final AtomicProductOfferingModificationValidatedEvent event) {
		this.stateProductOfferingId = event.getProductOfferingId();
		this.stateProductOffering.lifecycleStatus(event.getLifecycleStatus());

	}

	/**
	 * This method will apply an event and will save into db
	 *
	 * @param command : AtomicProductOfferingCategoryModifyByCategoryDeletionCommand
	 */
	@CommandHandler
	public void processAtomicProductOfferingCategoryModifyByCategoryDeletionCommand(
			AtomicProductOfferingCategoryModifyByCategoryDeletionCommand command) {
		this.stateProductOfferingId = command.getProductOfferingId();
		AggregateLifecycle.apply(new AtomicProductOfferingCategoryModifiedEvent(command.getProductOfferingId(),
				command.getAddCategories(), command.getDelCategories(), command.getLastUpdate()));
	}

	@CommandHandler
	public ProductOfferingAggregate(ProductOfferingDeleteCommand command) {
		AggregateLifecycle.apply(new ProductOfferingDeleteEvent(command.getAggregateId(),
				command.getLastUpdateDateTime(), command.getInterval(), command.getIntervalUnit()));

	}

	@EventSourcingHandler
	public void on(ProductOfferingDeleteEvent event) {
		this.stateProductOfferingId = event.getAggregateId();
	}
}

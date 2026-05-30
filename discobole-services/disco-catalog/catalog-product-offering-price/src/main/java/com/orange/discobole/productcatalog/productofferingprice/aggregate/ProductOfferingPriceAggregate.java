// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.aggregate;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.*;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceTaxAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.*;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Currency;
import com.orange.discobole.productcatalog.productofferingprice.event.*;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineInstallmentChargeIdentityData;
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
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.InitiateProductOfferingPriceModificationCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceAlterationIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ModifyProductOfferingPriceChargeIdentityDataCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.POPModificationCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationCancelCommand;
import com.orange.discobole.productcatalog.productofferingprice.command.productofferingprice.modify.ProductOfferingPriceModificationValidatedCommand;
import com.orange.discobole.productcatalog.productofferingprice.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productofferingprice.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceAlterationIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.BillingType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ChargeCycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.CommercialOperation;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.POPRelationshipType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceAlterationType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProrationType;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceAlterationIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceChargeIdentityDataModifiedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationCancelledEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationInitiatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.ProductOfferingPriceModificationValidatedEvent;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;
import com.orange.discobole.productcatalog.productofferingprice.util.TimePeriodValidityUtil;

import jakarta.annotation.Resource;

/**
 * The Class ProductOfferingPriceAggregate handles the business logic of
 * different commands.
 *
 * @author Ankur Singh
 * @since 1.0
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
@Aggregate
public class ProductOfferingPriceAggregate {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceAggregate.class);

	@Resource
	private ConfigurableProperties configurableProperties;

	@AggregateIdentifier
	private String productOfferingPriceId;

	ProductOfferingPrice productOfferingPrice;
	private static String version = "1.0";

	@Autowired
	public ProductOfferingPriceAggregate() {
	}

	/**
	 * Process the initiation of product offering price command and generate the
	 * product offering price id.
	 *
	 * @param command:InitiateProductOfferingPriceCommand
	 */
	@CommandHandler
	public ProductOfferingPriceAggregate(InitiateProductOfferingPriceCommand command) {
		LOGGER.info("Constructor ProductOfferingPriceAggregate -> InitiateProductOfferingPriceCommand : {}", command);
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		ProductOfferingPriceType productOfferingPriceType = command.getProductOfferingPriceType();
		AggregateLifecycle.apply((new ProductOfferingPriceInitiatedEvent(command.getProductOfferingPriceId(),
				productOfferingPriceType, lastUpdate, ProductOfferingPriceLifecycle.UNAVAILABLE)));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceInitiatedEvent .
	 *
	 * @param event:ProductOfferingPriceInitiatedEvent
	 */


	@EventSourcingHandler
	public void on(ProductOfferingPriceInitiatedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		// Instantiate the correct subclass based on POP type
		switch (event.getProductOfferingPriceType()) {

			case PRODUCTOFFERINGPRICECHARGE:
				this.productOfferingPrice = new ProductOfferingPriceCharge();
				break;

			case PRODUCTOFFERINGPRICEALTERATION:
				this.productOfferingPrice = new ProductOfferingPriceAlteration();
				break;

			case PRODUCTOFFERINGTAXALTERATION:
				this.productOfferingPrice = new TaxProductOfferingPriceAlteration();
				break;

			case INSTALLMENTCHARGE:
				this.productOfferingPrice = new InstallmentCharge();
				break;

			default:
				this.productOfferingPrice = new ProductOfferingPrice();
				break;
		}

		// Set common fields
		this.productOfferingPrice
				.id(event.getProductOfferingPriceId())
				.type(event.getProductOfferingPriceType().toString())
				.lastUpdate(event.getLastUpdate())
				.lifecycleStatus(event.getLifecycle());
	}

	/**
	 * Process the ProductOfferingPriceDeleteCommand for Product offering price and
	 * delete POP(CleanUp JOB)
	 *
	 * @param command:ProductOfferingPriceDeleteCommand
	 *
	 */
	@CommandHandler
	public ProductOfferingPriceAggregate(ProductOfferingPriceDeleteCommand command) {
		LOGGER.info("Constructor ProductOfferingPriceAggregate -> ProductOfferingPriceDeleteCommand : {}", command);
		AggregateLifecycle.apply(new ProductOfferingPriceDeleteEvent(UUID.randomUUID().toString(),
				command.getLastUpdateDateTime(), command.getInterval(), command.getIntervalUnit()));

	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceDeleteEvent.
	 *
	 * @param event:ProductOfferingPriceDeleteEvent
	 */
	@EventSourcingHandler
	public void on(ProductOfferingPriceDeleteEvent event) {
		this.productOfferingPriceId = event.getAggregateId();
	}

	/**
	 * Process the ProductOfferingPriceChargeIdentityDataCommand for Product
	 * offering price and defines the charges.
	 *
	 * @param command:ProductOfferingPriceChargeIdentityDataCommand
	 *
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceChargeIdentityDataCommand command, QueryService queryService) {
		LOGGER.info("ProductOfferingPriceChargeIdentityData Command Handler: {}", command);
		String popId = command.getProductOfferingPriceId();
		DefineProductOfferingPriceChargeIdentityData identityData = command.getIdentityData();

		validatePricePoPC(identityData);
		ProrationType prorationType = identityData.getProrationType() != null ? identityData.getProrationType()
				: ProrationType.NOPRORATION;
		PriceType priceType = identityData.getPriceType();

		ChargeCycle chargeCycle = null;
		if (priceType.equals(PriceType.RC)) {
			if(identityData.getImmediatePayment()==null) identityData.setImmediatePayment(false);
			chargeCycle = identityData.getChargeCycle() != null ? identityData.getChargeCycle()
					: ChargeCycle.CYCLEFORWARD;
		} else {
			if (identityData.getChargeCycle() != null) {
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_CYCLECHARGE_NRC);
			}
		}

		validatePriceTypePoPC(priceType);
		validateRecurringChargePoPC(priceType, identityData);
		validateImmediatePaymentPoPC(priceType, identityData.getImmediatePayment());
		validateProrationTypePoPC(priceType, prorationType);

		// Extract the current POP's currency
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);
		List<ProductOfferingPriceRelationship> popRelationships = processProductOfferingPriceRelationships(
				command.getProductOfferingPriceRelationships(), queryService, priceType,identityData,currentPopCurrency);

		validateLifecycleStatusPoP(command.getDefinePOPStatusValidityPeriod().getLifecycleStatus());
		validateTimePeriodPoP(command.getDefinePOPStatusValidityPeriod().getValidFor());

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		String href = configurableProperties.getCatprodcaturl() + "?id=" + this.productOfferingPriceId;

		AggregateLifecycle.apply(new ProductOfferingPriceChargeIdentityDataDescribedEvent(popId, identityData.getName(),
				identityData.getDescription(), priceType, identityData.getImmediatePayment(),
				identityData.getRecurringChargePeriodLength(),
				identityData.getRecurringChargePeriodType() != null
						? identityData.getRecurringChargePeriodType()
						: null,
				identityData.getPrice(), identityData.getUnitOfMeasure(), prorationType, popRelationships, chargeCycle,
				command.getDefinePOPStatusValidityPeriod().getLifecycleStatus(),
				command.getDefinePOPStatusValidityPeriod().getValidFor(), lastUpdate, href));

		AggregateLifecycle.apply(new ProductOfferingPriceVersionCreatedEvent(popId, version, OffsetDateTime.now()));
		AggregateLifecycle.apply(new ProductOfferingPriceCreationCompletedEvent(popId, this.productOfferingPrice));
	}

	private void validatePricePoPC(DefineProductOfferingPriceChargeIdentityData identityData) {
		if ((identityData.getPrice().getUnit() == null) || (identityData.getPrice().getValue() == null)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_IDENTITYDATA);
		}
	}

	private void validatePriceTypePoPC(PriceType priceType) {
		if (!validatePriceType(priceType)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE);
		}
	}

	private void validateRecurringChargePoPC(PriceType priceType,
			DefineProductOfferingPriceChargeIdentityData identityData) {
		Integer recurringChargePeriodLength = identityData.getRecurringChargePeriodLength();
		String recurringChargePeriodType = identityData.getRecurringChargePeriodType() != null
				? identityData.getRecurringChargePeriodType()
				: null;

		if (!priceType.equals(PriceType.RC)
				&& (recurringChargePeriodLength != null || recurringChargePeriodType != null)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE_RC);
		}
		if (priceType.equals(PriceType.RC)
				&& (recurringChargePeriodLength == null || recurringChargePeriodType == null)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE_RC);
		}
	}

	private void validateImmediatePaymentPoPC(PriceType priceType, Boolean immediatePayment) {
		if (Boolean.TRUE.equals(immediatePayment) && !priceType.equals(PriceType.NRC)) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE);
		}
	}

	private void validateProrationTypePoPC(PriceType priceType, ProrationType prorationType) {
		if (priceType.equals(PriceType.NRC) && !prorationType.equals(ProrationType.NOPRORATION)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_PRORATIONTYPE);
		}
	}

	private void validateLifecycleStatusPoP(ProductOfferingPriceLifecycle status) {
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || status == null) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
	}

	private void validateTimePeriodPoP(TimePeriod validFor) {
		if (validFor.getStartDateTime().isBefore(OffsetDateTime.now().minusMinutes(5))){
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_VALIDITY_INVALID_STARTTIME);

		}
		if (!TimePeriodValidityUtil.isTimePeriodValid(validFor)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_VALIDFOR);
		}

	}

	//Validate currency
	private void validateCurrency(String currency, QueryService queryService) {
		if (currency != null) {
			List<Currency> validCurrencies = queryService.fetchCurrency();
			boolean isValid = false;

			for (Currency c : validCurrencies) {
				if (currency.equals(c.getCode())) {
					isValid = true;
					break;
				}
			}

			if (!isValid) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISO_POP_INVALID_POP_CURRENCY
				);
			}
		}
	}

	//Validate frequency
	private void validateFrequency(String frequency, QueryService queryService) {
		if (frequency != null) {
			List<Frequency> validFrequencies = queryService.fetchFrequency();
			boolean isValid = false;

			for (Frequency f : validFrequencies) {
				if (frequency.equals(f.getFrequencyCode())) {
					isValid = true;
					break;
				}
			}

			if (!isValid) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISO_POP_INVALID_POP_FREQUENCY
				);
			}
		}
	}



	private List<ProductOfferingPriceRelationship> processProductOfferingPriceRelationships(
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships,
			QueryService queryService,
			PriceType priceType,
			Object identityData,
			String currentPopCurrency) {

		if (productOfferingPriceRelationships == null) {
			 return Collections.emptyList();
		}

		Map<Integer, List<ProductOfferingPrice>> popaWithSamePriority = new HashMap<>();
		List<String> invalidCurrencies = new ArrayList<>();
		POPRelationshipType existingRelationshipsType = null;

		boolean isCharge = identityData instanceof DefineProductOfferingPriceChargeIdentityData;
		boolean isInstallment = identityData instanceof DefineInstallmentChargeIdentityData;



		for (ProductOfferingPriceRelationship priceRelationship : productOfferingPriceRelationships) {

			ProductOfferingPrice pop = queryService.getProductOfferingPrice(priceRelationship.getId());
			        String type  = pop.getType();

					if(isInstallment && "ProductOfferingPriceCharge".equals(type)){
					throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POPC_CAN_NOT_ASSOCIATED_WITH_INSTALLMENT);


					}

			if(isCharge && "InstallmentCharge".equals(type)){
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_INSTALLMENT_CAN_NOT_ASSOCIATED_WITH_POPC);


			}




			existingRelationshipsType =
					validateProductOfferingPriceRelationshipsType(existingRelationshipsType,
							priceRelationship, pop);

			// Dispatch different identityData types
			if (isCharge) {
				validateProductOfferingPriceRelationships(
						priceType,
						popaWithSamePriority,
						priceRelationship,
						(DefineProductOfferingPriceChargeIdentityData) identityData,
						currentPopCurrency,
						pop,
						invalidCurrencies
				);
			} else {
				validateProductOfferingPriceRelationships(
						priceType,
						popaWithSamePriority,
						priceRelationship,
						(DefineInstallmentChargeIdentityData) identityData,
						currentPopCurrency,
						pop,
						invalidCurrencies
				);
			}
		}

		if (!invalidCurrencies.isEmpty()) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_UNIT,
					invalidCurrencies.toString(),
					invalidCurrencies.toString()
			);
		}

		// Check priorities
		// Check priorities
		if (!popaWithSamePriority.isEmpty()) {
			AtomicBoolean hasError = new AtomicBoolean(false);

			popaWithSamePriority.forEach((priority, pops) -> {
				if (pops.size() > 1) {
					hasError.set(true);
					InvalidPOPAHavingSamePriorityEvent evt = new InvalidPOPAHavingSamePriorityEvent();
					evt.setProductOfferingPrices(pops);
					evt.setMessage("More than one POPA have same priority.");
				}
			});

			popaWithSamePriorityError(hasError, popaWithSamePriority);
		}

		return productOfferingPriceRelationships;
	}

	public void popaWithSamePriorityError(AtomicBoolean flag,Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority){
		if (flag.get()) {
			StringBuilder msg=new StringBuilder();
			for (Map.Entry<Integer, List<ProductOfferingPrice>> e:pOPAWithSamePriority.entrySet()){
				msg.append("[{ ");
				for(ProductOfferingPrice pop:e.getValue()){
					msg.append(pop.getId()).append(",");
				}
				msg.append(" }], ");
			}
			msg.deleteCharAt(msg.length() - 2);
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_RELATIONSHIP_POPA_PRIORITY,null,msg.toString());
		}
	}
	/**
	 * updates the state of aggregate after applying
	 * event:ProductOfferingPriceChargeIdentityDataDescribedEvent .
	 *
	 * @param event:ProductOfferingPriceChargeIdentityDataDescribedEvent
	 */


	@EventSourcingHandler
	public void on(ProductOfferingPriceChargeIdentityDataDescribedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		// Cast to the correct subclass
		ProductOfferingPriceCharge charge =
				(ProductOfferingPriceCharge) this.productOfferingPrice;

		// Parent class fluent methods
		charge.name(event.getName());
		charge.description(event.getDescription());
		charge.href(event.getHref());
		charge.price(event.getPrice());
		charge.lifecycleStatus(event.getLifecycle());
		charge.validFor(event.getValidFor());
		charge.lastUpdate(event.getLastUpdate());

		// Subclass-only methods
		charge.setPriceType(event.getPriceType());
		charge.setRecurringChargePeriodLength(event.getRecurringChargePeriodLength());
		charge.setRecurringChargePeriodType(event.getRecurringChargePeriodType());
		charge.setProrationType(event.getProrationType());
		charge.setUnitOfMeasure(event.getUnitOfMeasure());
		charge.setPopRelationship(event.getProductOfferingPriceRelationships());
		charge.setChargeCycle(event.getChargeCycle());
	}


	/**
	 * Process the productOfferingPriceAlterationIdentityDataCommand for product
	 * offering price to define the altered price in terms of price or in
	 * percentage.
	 *
	 * @param command:ProductOfferingPriceAlterationIdentityDataCommand
	 *
	 *
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceAlterationIdentityDataCommand command,QueryService queryService) {
		LOGGER.info("ProductOfferingPriceAlterationIdentityData Command Handler : {}", command);
		String popId = this.productOfferingPriceId;
		DefineProductOfferingPriceAlterationIdentityData identityData = command.getIdentityData();
		PriceAlterationType alterationType = identityData.getPriceAlterationType();

		if (null == PriceAlterationType.fromValue(alterationType.toString())) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_TYPE);
		}

		ProrationType prorationType = identityData.getProrationType() != null ? identityData.getProrationType()
				: ProrationType.NOPRORATION;
		PriceType priceType = identityData.getPriceType();
		if (!validatePriceType(priceType)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE);
		}

		if (!priceType.equals(PriceType.RC) && null != identityData.getApplicationDuration()) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_PRICE_TYPE_NOT_RC);
		}


		if (priceType.equals(PriceType.NRC) && (!prorationType.equals(ProrationType.NOPRORATION))) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_PRORATIONTYPE);
		}
        //Validate currency and Application duration
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);
		if (identityData.getApplicationDuration() != null) {
			validateFrequency(identityData.getApplicationDuration().getUnits(), queryService);
		}

		ProductOfferingPriceLifecycle status = command.getDefinePOPStatusValidityPeriod().getLifecycleStatus();
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || null == status) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
		TimePeriod validFor = command.getDefinePOPStatusValidityPeriod().getValidFor();
		validateTimePeriodPoP(validFor);
		String href = configurableProperties.getProductOfferingPriceUrl() + "?id=" + this.productOfferingPriceId;
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingPriceAlterationIdentityDataDescribedEvent(productOfferingPriceId,
				identityData.getName(), identityData.getDescription(), identityData.getPriceType(),
				identityData.getApplicationDuration(), identityData.getPriority(), prorationType,
				identityData.getPercentage(), identityData.getPrice(), identityData.getUnitOfMeasure(), status,
				validFor, lastUpdate, href, priceType.equals(PriceType.RC) ? command.getIdentityData().getApplicationOffset() : null));

		AggregateLifecycle.apply(new ProductOfferingPriceVersionCreatedEvent(popId, version, OffsetDateTime.now()));

		AggregateLifecycle.apply(new ProductOfferingPriceCreationCompletedEvent(popId, this.productOfferingPrice));

	}






	@EventSourcingHandler
	public void on(ProductOfferingPriceAlterationIdentityDataDescribedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		if (this.productOfferingPrice instanceof ProductOfferingPriceAlteration alteration) {


			// Base class fluent methods
			alteration.name(event.getName());
			alteration.description(event.getDescription());
			alteration.price(event.getPrice());
			alteration.href(event.getHref());
			alteration.lifecycleStatus(event.getLifecycle());
			alteration.validFor(event.getValidFor());
			alteration.lastUpdate(event.getLastUpdate());

			// Subclass-specific setters
			alteration.setPriceType(event.getPriceType());
			alteration.setApplicationDuration(event.getApplicationDuration());
			alteration.setPriority(event.getPriority());
			alteration.setProrationType(event.getProrationType());
			alteration.setPercentage(event.getPercentage());
			alteration.setUnitOfMeasure(event.getUnitOfMeasure());
		}
	}


	private POPRelationshipType validateProductOfferingPriceRelationshipsType(POPRelationshipType existingRelationshipsType,ProductOfferingPriceRelationship priceRelationship,ProductOfferingPrice productOfferingPriceAlteration){
		if(productOfferingPriceAlteration==null)
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_ASSOCIATED_POPA_IS_NOT_FOUND,priceRelationship.getId(),priceRelationship.getId());

		if(POPRelationshipType.REPLACEDBY.equals(priceRelationship.getRelationshipType())){
			if (productOfferingPriceAlteration.getType() .equals(ProductOfferingPriceType.PRODUCTOFFERINGTAXALTERATION.getValue())) {
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_ASSOCIATED_WITH_TAXPOPA_THROUGH_ALTERED_BY);
			}
			if(existingRelationshipsType==null){
				return POPRelationshipType.REPLACEDBY;
			}
			else if( POPRelationshipType.REPLACEDBY.equals(existingRelationshipsType)){
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_RELATION_MULTIPLE_REPLACEDBY);
			}
			else if(POPRelationshipType.ALTEREDBY.equals(existingRelationshipsType)){
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_RELATION_BOTH_ALTEREDBY_AND_REPLACEDBY_PRESENT);
			}
		}
		else if(POPRelationshipType.ALTEREDBY.equals(priceRelationship.getRelationshipType()) && productOfferingPriceAlteration.getType().equals(ProductOfferingPriceType.PRODUCTOFFERINGPRICEALTERATION.getValue())){
			if(existingRelationshipsType==null ){
				return POPRelationshipType.ALTEREDBY;
			}
			else if(POPRelationshipType.REPLACEDBY.equals(existingRelationshipsType)){
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISCO_POP_INVALID_RELATION_BOTH_ALTEREDBY_AND_REPLACEDBY_PRESENT);
			}
		}
		return existingRelationshipsType;
	}
	private void validateProductOfferingPriceRelationships(
	        PriceType priceType,
	        Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority,
	        ProductOfferingPriceRelationship priceRelationship,
	        DefineProductOfferingPriceChargeIdentityData identityData,
	        String currentPopCurrency,
			ProductOfferingPrice productOfferingPriceAlteration,List<String> invalidCurrencies) {

		if(!validateCurrencyConsistency(productOfferingPriceAlteration, currentPopCurrency,invalidCurrencies))
			return;

		validateTypeConsistency(productOfferingPriceAlteration, priceType);

		validateLifecycleStatus(productOfferingPriceAlteration);

		validateReplacedByRelationship(productOfferingPriceAlteration, priceRelationship);

		validateAlteredByRelationship(productOfferingPriceAlteration, priceRelationship, identityData);

		addToPriorityMap(pOPAWithSamePriority, productOfferingPriceAlteration);

		setRelationshipType(priceRelationship, productOfferingPriceAlteration);

	}

	private void validateProductOfferingPriceRelationships(
			PriceType priceType,
			Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority,
			ProductOfferingPriceRelationship priceRelationship,
			DefineInstallmentChargeIdentityData identityData,
			String currentPopCurrency,
			ProductOfferingPrice productOfferingPriceAlteration,List<String> invalidCurrencies) {

		if(!validateCurrencyConsistency(productOfferingPriceAlteration, currentPopCurrency,invalidCurrencies))
			return;




		validateLifecycleStatus(productOfferingPriceAlteration);

		validateReplacedByRelationship(productOfferingPriceAlteration, priceRelationship);

		validateAlteredByRelationship(productOfferingPriceAlteration, priceRelationship, identityData);

		addToPriorityMap(pOPAWithSamePriority, productOfferingPriceAlteration);

		setRelationshipType(priceRelationship, productOfferingPriceAlteration);

	}


	private boolean validateCurrencyConsistency(ProductOfferingPrice alteration, String currentPopCurrency,List<String> invalidCurrencies) {
		boolean isPercentageBased = false;


		if (alteration instanceof ProductOfferingPriceAlteration alt ) {


			isPercentageBased = alt.getPercentage() != null && alt.getPrice() == null;
		}
		else if (alteration instanceof TaxProductOfferingPriceAlteration tax) {
			isPercentageBased = tax.getPercentage() != null && tax.getPrice() == null;
		}

	    if (isPercentageBased) return true;

	    boolean alterationHasPrice = alteration.getPrice() != null;
	    boolean currentHasCurrency = currentPopCurrency != null;

	    if (alterationHasPrice && currentHasCurrency) {
	        if (!currentPopCurrency.equals(alteration.getPrice().getUnit())) {
				invalidCurrencies.add(alteration.getId());
				return false;
	        }
	    }
		else if (alterationHasPrice != currentHasCurrency) {
			invalidCurrencies.add(alteration.getId());
			return false;
	    }
		return true;
	}



	private void validateTypeConsistency(ProductOfferingPrice pop, PriceType priceType) {

		PriceType alterationPriceType = null;

		if (pop instanceof ProductOfferingPriceAlteration alteration) {
			alterationPriceType = alteration.getPriceType();
		} else if (pop instanceof ProductOfferingPriceCharge charge) {
			alterationPriceType = charge.getPriceType();
		}

		if (pop.getType() != null
				&& !pop.getType().equals("TaxProductOfferingPriceAlteration")
				&& alterationPriceType != null
				&& !priceType.equals(alterationPriceType)) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_DIFFERENT_POPA_PRICETYPE
			);
		}
	}


	private void validateLifecycleStatus(ProductOfferingPrice alteration) {
	    if (!ProductOfferingPriceLifecycle.LAUNCHED.equals(alteration.getLifecycleStatus())) {
	        throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_LIFECYCLE_STATUS);
	    }
	}


	private void validateReplacedByRelationship(
			ProductOfferingPrice alteration,
			ProductOfferingPriceRelationship relationship) {

		if (POPRelationshipType.REPLACEDBY.equals(relationship.getRelationshipType())
				&& alteration instanceof ProductOfferingPriceAlteration productOfferingPriceAlteration
				&& (productOfferingPriceAlteration.getPercentage() != null)) {

			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INCONSISTENT_USEOF_POPA_RELATIONSHIP_TYPE
			);
		}
	}




	private void validateAlteredByRelationship(ProductOfferingPrice alteration, ProductOfferingPriceRelationship relationship, DefineProductOfferingPriceChargeIdentityData identityData) {
		if (POPRelationshipType.ALTEREDBY.equals(relationship.getRelationshipType()) && (alteration.getPrice() != null && identityData.getPrice() != null
				&& alteration.getPrice().getValue() != null && identityData.getPrice().getValue() != null
				&& alteration.getPrice().getValue().compareTo(identityData.getPrice().getValue()) > 0)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_PRICE);

		}
	}

	private void validateAlteredByRelationship(ProductOfferingPrice alteration, ProductOfferingPriceRelationship relationship, DefineInstallmentChargeIdentityData  identityData) {
		if (POPRelationshipType.ALTEREDBY.equals(relationship.getRelationshipType()) && (alteration.getPrice() != null && identityData.getPrice() != null
				&& alteration.getPrice().getValue() != null && identityData.getPrice().getValue() != null
				&& alteration.getPrice().getValue().compareTo(identityData.getPrice().getValue()) > 0)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_PRICE);

		}
	}


	private void addToPriorityMap(Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority, ProductOfferingPrice pop) {
		if (pop.getType().equals("TaxProductOfferingPriceAlteration"))
			return;
		if (pop instanceof ProductOfferingPriceAlteration alteration) {

			Integer priority = alteration.getPriority();
			pOPAWithSamePriority.computeIfAbsent(priority, k -> new ArrayList<>()).add(alteration);
		}
	}

	private void setRelationshipType(ProductOfferingPriceRelationship relationship, ProductOfferingPrice alteration) {
	    if (alteration.getType() != null) {
	        relationship.setType(alteration.getType());
	    }
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceVersionCreatedEvent .
	 *
	 * @param event:ProductOfferingPriceVersionCreatedEvent
	 */
	@EventSourcingHandler
	public void on(ProductOfferingPriceVersionCreatedEvent event) {
		this.productOfferingPrice.version(event.getVersion()).lastUpdate(event.getLastUpdate());
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceCreationCompletedEvent .
	 *
	 * @param event:ProductOfferingPriceCreationCompletedEvent
	 */
	@EventSourcingHandler
	public void on(ProductOfferingPriceCreationCompletedEvent event) {
		this.productOfferingPrice = event.getProductOfferingPrice();
	}

	/**
	 * Process the productOfferingPriceTaxAlterationIdentityDataCommand for product
	 * offering price to define the tax in terms of price or in
	 * percentage.
	 *
	 * @param command:ProductOfferingPriceTaxAlterationIdentityDataCommand
	 *
	 *
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceTaxAlterationIdentityDataCommand command,QueryService queryService) {
		LOGGER.info("ProductOfferingPriceTaxAlterationIdentityDataCommand  Handler : {}", command);
		String popId = this.productOfferingPriceId;
		DefineProductOfferingPriceTaxAlterationIdentityData identityData = command.getIdentityData();
		PriceAlterationType alterationType = identityData.getPriceAlterationType();

		if (null == PriceAlterationType.fromValue(alterationType.toString())) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_TYPE);
		}
         //Validate currency
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);


		ProductOfferingPriceLifecycle status = command.getDefinePOPStatusValidityPeriod().getLifecycleStatus();
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || null == status) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
		TimePeriod validFor = command.getDefinePOPStatusValidityPeriod().getValidFor();
		validateTimePeriodPoP(validFor);
		String href = configurableProperties.getProductOfferingPriceUrl() + "?id=" + this.productOfferingPriceId;
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent(productOfferingPriceId,
				identityData.getName(), identityData.getDescription(),
				identityData.getPercentage(), identityData.getPrice(), status,
				validFor, lastUpdate, href));

		AggregateLifecycle.apply(new ProductOfferingPriceVersionCreatedEvent(popId, version, OffsetDateTime.now()));

		AggregateLifecycle.apply(new ProductOfferingPriceCreationCompletedEvent(popId, this.productOfferingPrice));

	}



	@CommandHandler
	public void handler(ProductOfferingPriceInstallmentChargeIdentityDataCommand command,QueryService queryService) {
		LOGGER.info("ProductOfferingPriceInstallmentChargeIdentityDataCommand  Handler : {}", command);
		String popId = this.productOfferingPriceId;
		DefineInstallmentChargeIdentityData identityData = command.getIdentityData();
		//Validate currency
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);

		List<ProductOfferingPriceRelationship> popRelationships = processProductOfferingPriceRelationships(
				command.getProductOfferingPriceRelationships(), queryService, null,identityData,currentPopCurrency);



		ProductOfferingPriceLifecycle status = command.getDefinePOPStatusValidityPeriod().getLifecycleStatus();
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || null == status) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
		TimePeriod validFor = command.getDefinePOPStatusValidityPeriod().getValidFor();
		validateTimePeriodPoP(validFor);
		String href = configurableProperties.getProductOfferingPriceUrl() + "?id=" + this.productOfferingPriceId;
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent(productOfferingPriceId,
				identityData.getName(), identityData.getDescription(), identityData.getPartner(),
				identityData.getExternalId(), identityData.getDownPayment(),
				identityData.getInterestRate(), identityData.getApplicationDuration(), identityData.getPrice(),popRelationships, status,
				validFor, lastUpdate, href));

		AggregateLifecycle.apply(new ProductOfferingPriceVersionCreatedEvent(popId, version, OffsetDateTime.now()));

		AggregateLifecycle.apply(new ProductOfferingPriceCreationCompletedEvent(popId, this.productOfferingPrice));

	}



	/**
	 * Process the cancel command to abort the creation of product offering price
	 * creation.
	 *
	 * @param command :ProductOfferingPriceCancelCommand
	 *
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceCancelCommand command) {
		LOGGER.info("ProductOfferingPriceCancel Command Handler : {}", command);
		String popId = command.getProductOfferingPriceId();
		ProductOfferingPrice pop = this.productOfferingPrice;
		if (!ProductOfferingPriceLifecycle.UNAVAILABLE.equals(pop.getLifecycleStatus())) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS_UNAVAILABLE);
		}
		AggregateLifecycle.apply(new ProductOfferingPriceCancelledEvent(popId, pop));

	}


	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent .
	 *
	 * @param ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent
	 */




	@EventSourcingHandler
	public void on(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		// Cast to correct subclass
		InstallmentCharge installmentCharge =
				(InstallmentCharge) this.productOfferingPrice;

		// Parent class fluent methods
		installmentCharge.name(event.getName());
		installmentCharge.description(event.getDescription());
		installmentCharge.getPartner();
		installmentCharge.getExternalId();
		installmentCharge.getDownPayment();
		installmentCharge.getPrice();
		installmentCharge.getApplicationDuration();
		installmentCharge.price(event.getPrice());
		installmentCharge.setPopRelationship(event.getProductOfferingPriceRelationships());
		installmentCharge.lifecycleStatus(event.getLifecycle());
		installmentCharge.validFor(event.getValidFor());
		installmentCharge.lastUpdate(event.getLastUpdate());
		installmentCharge.href(event.getHref());



	}


	@EventSourcingHandler
	public void on(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		// Cast to correct subclass
		TaxProductOfferingPriceAlteration tax =
				(TaxProductOfferingPriceAlteration) this.productOfferingPrice;

		// Parent class fluent methods
		tax.name(event.getName());
		tax.description(event.getDescription());
		tax.price(event.getPrice());
		tax.lifecycleStatus(event.getLifecycle());
		tax.validFor(event.getValidFor());
		tax.lastUpdate(event.getLastUpdate());

		// Child-only setters (specific to TaxProductOfferingPriceAlteration)
		tax.setPercentage(event.getPercentage());

	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceCancelledEvent .
	 *
	 * @param event:ProductOfferingPriceCancelledEvent
	 */
	@EventSourcingHandler
	public void on(ProductOfferingPriceCancelledEvent event) {
		this.productOfferingPrice = event.getProductOfferingPrice();
	}

	/**
	 * Function to validate the price type.
	 *
	 * @param priceType the price type
	 * @return true or false
	 */
	private boolean validatePriceType(PriceType priceType) {
		for (PriceType priceTypeEnum : PriceType.values()) {
			if (priceTypeEnum.equals(priceType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Process the InitiateProductOfferingPriceModificationCommand for Product
	 * offering price
	 *
	 * @param command : InitiateProductOfferingPriceModificationCommand
	 */
	@CommandHandler
	public void handler(InitiateProductOfferingPriceModificationCommand command) {
		LOGGER.info("InitiateProductOfferingPriceModification Command Handler : {}", command);
		ProductOfferingPriceType productOfferingPriceType = command.getProductOfferingPriceType();
		String popId = UUID.randomUUID().toString();
		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingPriceInitiatedEvent(popId, productOfferingPriceType, lastUpdate,
				ProductOfferingPriceLifecycle.UNAVAILABLE));
	}

	/**
	 * Process the ProductOfferingPriceChargeIdentityDataCommand for Product
	 * offering price and modify the charges.
	 *
	 * @param command : ModifyProductOfferingPriceChargeIdentityDataCommand
	 */
	@CommandHandler
	public void handler(ModifyProductOfferingPriceChargeIdentityDataCommand command, QueryService queryService) {
		LOGGER.info("ModifyProductOfferingPriceChargeIdentityData Command Handler : {}", command);

		String popId = this.productOfferingPrice.getId();
		DefineProductOfferingPriceChargeIdentityData identityData = command.getIdentityData();
		validatePrice(identityData);

		// Extract the current POP's currency
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);

		List<ProductOfferingPriceRelationship> popRelationships = processModifyProductOfferingPriceRelationships(
				command.getProductOfferingPriceRelationships(), queryService,currentPopCurrency);

		validatePriceType(identityData.getPriceType(), identityData.getRecurringChargePeriodLength(),
				identityData.getRecurringChargePeriodType());

		validateImmediatePayment(identityData, popId, queryService);

		validateLifecycleStatus(command.getDefinePOPStatusValidityPeriod().getLifecycleStatus());

		validateTimePeriod(command.getDefinePOPStatusValidityPeriod().getValidFor());

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		applyChanges(command, popId, identityData, popRelationships, lastUpdate);
	}

	private void validatePrice(DefineProductOfferingPriceChargeIdentityData identityData) {
		if ((identityData.getPrice().getUnit() == null && identityData.getPrice().getValue() != null)
				|| (identityData.getPrice().getUnit() != null && identityData.getPrice().getValue() == null)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_IDENTITYDATA);
		}
	}

	private void validatePriceType(PriceType priceType, Integer recurringChargePeriodLength,
			String recurringChargePeriodType) {
		if (!validatePriceType(priceType)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}

		if (!priceType.equals(PriceType.RC)
				&& (recurringChargePeriodLength != null || recurringChargePeriodType != null)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE_RC);
		}
	}

	private void validateImmediatePayment(
			DefineProductOfferingPriceChargeIdentityData identityData,
			String popId,
			QueryService queryService) {

		if (identityData.getImmediatePayment().equals(true)) {
			if (!identityData.getPriceType().equals(PriceType.NRC)) {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE
				);
			}
		} else if (identityData.getPriceType().equals(PriceType.NRC)) {

			// Cast base class to the correct subclass
			Boolean currentImmediatePayment = null;
			if (this.productOfferingPrice instanceof ProductOfferingPriceCharge productOfferingPriceCharge) {
				currentImmediatePayment =
						productOfferingPriceCharge.getImmediatePayment();
			}

			if (currentImmediatePayment != null &&
					!identityData.getImmediatePayment().equals(currentImmediatePayment)) {

				List<ProductOffering> productOfferings =
						queryService.fetchProductOfferingsByProductOfferingPriceId(popId);

				validatePrepaidProductOfferings(popId, productOfferings);
			}
		}
	}


	private void validatePrepaidProductOfferings(String popId, List<ProductOffering> productOfferings) {
		if (productOfferings != null) {
			for (ProductOffering po : productOfferings) {
				for (CommercialOperation co : po.getCommercialOperation()) {
					if (co.getCarries().get(0).getId().equals(popId)
							&& po.getBillingType().equals(BillingType.PREPAID)) {
						throw new DiscoManagedClientException(
								ProductOfferingPriceConstants.DISO_POP_CANNOT_MODIFY_PAYMENT);
					}
				}
			}
		}
	}

	private void validateLifecycleStatus(ProductOfferingPriceLifecycle status) {
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || status == null) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
	}

	private void validateTimePeriod(TimePeriod validFor) {
		if (!TimePeriodValidityUtil.isTimePeriodValid(validFor)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_VALIDFOR);
		}
	}

	private void applyChanges(ModifyProductOfferingPriceChargeIdentityDataCommand command, String popId,
			DefineProductOfferingPriceChargeIdentityData identityData,
			List<ProductOfferingPriceRelationship> popRelationships, OffsetDateTime lastUpdate) {
		ProductOfferingPriceLifecycle status = command.getDefinePOPStatusValidityPeriod().getLifecycleStatus();
		TimePeriod validFor = command.getDefinePOPStatusValidityPeriod().getValidFor();

		AggregateLifecycle.apply(new ProductOfferingPriceChargeIdentityDataModifiedEvent(popId, identityData.getName(),
				identityData.getDescription(), identityData.getPriceType(), identityData.getImmediatePayment(),
				identityData.getRecurringChargePeriodLength(), identityData.getRecurringChargePeriodType(),
				identityData.getPrice(), identityData.getUnitOfMeasure(), popRelationships, status, validFor,
				lastUpdate));

		this.productOfferingPrice.lifecycleStatus(status);
		this.productOfferingPrice.version(version);
	}

	private List<ProductOfferingPriceRelationship> processModifyProductOfferingPriceRelationships(
			List<ProductOfferingPriceRelationship> productOfferingPriceRelationships, QueryService queryService,String currentPopCurrency) {
		List<Event> eventList = new ArrayList<>();

		PriceType priceType = null;

		if (this.productOfferingPrice instanceof ProductOfferingPriceAlteration alteration) {
			priceType = alteration.getPriceType();
		} else if (this.productOfferingPrice instanceof ProductOfferingPriceCharge charge) {
			priceType = charge.getPriceType();
		}

		List<ProductOfferingPriceRelationship> relationships = productOfferingPriceRelationships;
		if (null != relationships) {
			Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority = new HashMap<>();
			List<String> invalidCurrencies=new ArrayList<>();
			POPRelationshipType existingRelationshipsType=null;
			for (ProductOfferingPriceRelationship priceRelationship : relationships) {
				ProductOfferingPrice productOfferingPriceAlteration = queryService.getProductOfferingPrice(priceRelationship.getId());
				existingRelationshipsType=validateProductOfferingPriceRelationshipsType(existingRelationshipsType,priceRelationship,productOfferingPriceAlteration);
				validateProductOfferingPriceRelationship(priceType, pOPAWithSamePriority,
						priceRelationship,currentPopCurrency,productOfferingPriceAlteration,invalidCurrencies);
			}
			if(!invalidCurrencies.isEmpty()){
				throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_UNIT,invalidCurrencies.toString(),invalidCurrencies.toString());
			}
			if (!pOPAWithSamePriority.isEmpty()) {
				AtomicBoolean flag = new AtomicBoolean(false);
				pOPAWithSamePriority.forEach((k, v) -> {
					InvalidPOPAHavingSamePriorityEvent samePriorityEvent = new InvalidPOPAHavingSamePriorityEvent();
					if (v.size() > 1) {
						flag.set(true);
						samePriorityEvent.setProductOfferingPrices(v);
						samePriorityEvent.setMessage("More than one POPA have same priority.");
						eventList.add(samePriorityEvent);
					}
				});
				popaWithSamePriorityError(flag,pOPAWithSamePriority);
			}
		}
		return relationships;
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceChargeIdentityDataModifiedEvent.
	 *
	 * @param event: ProductOfferingPriceChargeIdentityDataModifiedEvent
	 */



	@EventSourcingHandler
	private void on(ProductOfferingPriceChargeIdentityDataModifiedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		// Cast base reference to the correct subtype
		ProductOfferingPriceCharge charge =
				(ProductOfferingPriceCharge) this.productOfferingPrice;

		// Parent class fluent methods (exist in the base POP)
		charge.name(event.getName());
		charge.description(event.getDescription());
		charge.price(event.getPrice());
		charge.lifecycleStatus(event.getLifecycle());
		charge.validFor(event.getValidFor());
		charge.lastUpdate(event.getLastUpdate());

		// Subclass-specific fields (defined only in ProductOfferingPriceCharge)
		charge.setPriceType(event.getPriceType());
		charge.setImmediatePayment(event.getImmediatePayment());
		charge.setRecurringChargePeriodLength(event.getRecurringChargePeriodLength());
		charge.setRecurringChargePeriodType(event.getRecurringChargePeriodType());
		charge.setUnitOfMeasure(event.getUnitOfMeasure());
		charge.setPopRelationship(event.getProductOfferingPriceRelationships());

	}


	/**
	 * Process the ModifyProductOfferingPriceAlterationIdentityDataCommand for
	 * product offering price to modify the altered price in terms of price or in
	 * percentage.
	 *
	 * @param command
	 */
	@CommandHandler
	public void handler(ModifyProductOfferingPriceAlterationIdentityDataCommand command,QueryService queryService) {
		LOGGER.info("ModifyProductOfferingPriceAlterationIdentityData Command Handler : {}", command);
		String popId = this.productOfferingPrice.getId();
		DefineProductOfferingPriceAlterationIdentityData identityData = command.getIdentityData();

		PriceAlterationType alterationType = identityData.getPriceAlterationType();

		if (null == PriceAlterationType.fromValue(alterationType.toString())) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPA_TYPE);
		}

		PriceType priceType = identityData.getPriceType();
		if (!validatePriceType(priceType)) {
			throw new DiscoManagedClientException(
					ProductOfferingPriceConstants.DISO_POP_INVALID_IMMEDIATE_PAYMENT_WITH_CURRNET_PRICE_TYPE);
		}

		if (!priceType.equals(PriceType.RC) && null != identityData.getApplicationDuration()) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE_DURATION);
		}

		if (priceType.equals(PriceType.RC) && (null == identityData.getApplicationDuration())) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_TYPE_DURATION);
		}
		//Validate currency appduration
		String currentPopCurrency = identityData.getPrice() != null ? identityData.getPrice().getUnit() : null;
		validateCurrency(currentPopCurrency, queryService);
		if (identityData.getApplicationDuration() != null) {
			validateFrequency(identityData.getApplicationDuration().getUnits(), queryService);
		}

		ProductOfferingPriceLifecycle status = command.getDefinePOPStatusValidityPeriod().getLifecycleStatus();
		if (ProductOfferingPriceLifecycle.OBSOLETE.equals(status) || null == status) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POPC_LIFECYCLE_STATUS);
		}
		TimePeriod validFor = command.getDefinePOPStatusValidityPeriod().getValidFor();
		if (!TimePeriodValidityUtil.isTimePeriodValid(validFor)) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_INVALID_POP_VALIDFOR);
		}

		OffsetDateTime lastUpdate = OffsetDateTime.now();
		AggregateLifecycle.apply(new ProductOfferingPriceAlterationIdentityDataModifiedEvent(popId,
				identityData.getName(), identityData.getDescription(), identityData.getPriceType(),
				identityData.getApplicationDuration(), identityData.getPriority(), identityData.getPercentage(),
				identityData.getPrice(), identityData.getUnitOfMeasure(), status, validFor, lastUpdate, priceType.equals(PriceType.RC) ? command.getIdentityData().getApplicationOffset() : null));

		this.productOfferingPrice.lifecycleStatus(status);
		this.productOfferingPrice.version(version);
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceAlterationIdentityDataModifiedEvent.
	 *
	 * @param event: ProductOfferingPriceAlterationIdentityDataModifiedEvent
	 */


	@EventSourcingHandler
	private void on(ProductOfferingPriceAlterationIdentityDataModifiedEvent event) {

		this.productOfferingPriceId = event.getProductOfferingPriceId();

		if (this.productOfferingPrice instanceof ProductOfferingPriceAlteration alteration ) {

			// Base class fluent methods
			alteration.name(event.getName());
			alteration.description(event.getDescription());
			alteration.price(event.getPrice());
			alteration.lifecycleStatus(event.getLifecycle());
			alteration.validFor(event.getValidFor());
			alteration.lastUpdate(event.getLastUpdate());

			// Subclass-specific setters
			alteration.setPriceType(event.getPriceType());
			alteration.setApplicationDuration(event.getApplicationDuration());
			alteration.setPriority(event.getPriority());
			alteration.setPercentage(event.getPercentage());
			alteration.setUnitOfMeasure(event.getUnitOfMeasure());
		}
	}


	private void validateProductOfferingPriceRelationship(
	        PriceType priceType,
	        Map<Integer, List<ProductOfferingPrice>> pOPAWithSamePriority,
	        ProductOfferingPriceRelationship priceRelationship,
	        String currentPopCurrency,ProductOfferingPrice alteration,List<String> invalidCurrencies) {

	    validateCurrencyConsistency(alteration, currentPopCurrency,invalidCurrencies);

	    validateTypeConsistency(alteration, priceType);

	    validateReplacedByRelationship(alteration, priceRelationship);

	    addToPriorityMap(pOPAWithSamePriority, alteration);
	}


	/**
	 * This method will cancel POP modification process.
	 *
	 * @param command : productOfferingPriceModificationCancelCommand
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceModificationCancelCommand command) {
		LOGGER.info("ProductOfferingPriceModification Command Handler : {}", command);
		ProductOfferingPrice pop = this.productOfferingPrice;
		AggregateLifecycle.apply(new ProductOfferingPriceModificationCancelledEvent(productOfferingPriceId, pop));
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceModificationCancelledEvent.
	 *
	 * @param event: ProductOfferingPriceModificationCancelledEvent
	 */
	@EventSourcingHandler
	private void on(ProductOfferingPriceModificationCancelledEvent event) {
		this.productOfferingPriceId = event.getProductOfferingPriceId();
		this.productOfferingPrice = event.getProductOfferingPrice();
	}

	/**
	 * This method will initiate POP Modification when POP exist with lifeCycle
	 * unavailable/launched.
	 *
	 * @param command      : POPModificationCommand
	 * @param queryService : QueryService
	 */
	@CommandHandler
	public void handler(POPModificationCommand command, QueryService queryService) {
		LOGGER.info("POPModification Command Handler : {}", command);
		String popId = command.getPopId();
		ProductOfferingPrice pop = queryService.getProductOfferingPrice(popId);
		if (null == pop) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_POP_NOT_FOUND, popId, null);
		} else {

			ProductOfferingPriceLifecycle lifecycleStatus = pop.getLifecycleStatus();
			if (lifecycleStatus.equals(ProductOfferingPriceLifecycle.LAUNCHED)) {
				AggregateLifecycle.apply(new ProductOfferingPriceModificationInitiatedEvent(popId, pop,
						OffsetDateTime.now(), lifecycleStatus));
			} else {
				throw new DiscoManagedClientException(
						ProductOfferingPriceConstants.DISCO_POP_INVALID_STATUS_NOT_MODIFIED);
			}
		}
	}

	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceModificationInitiatedEvent.
	 *
	 * @param event: ProductOfferingPriceModificationInitiatedEvent
	 */
	@EventSourcingHandler
	private void on(ProductOfferingPriceModificationInitiatedEvent event) {
		this.productOfferingPriceId = event.getProductOfferingPriceId();
		this.productOfferingPrice = event.getProductOfferingPrice();
	}

	/**
	 * This method will validate the Product Offering Price Modification Process
	 *
	 * @param command      : ProductOfferingPriceModificationValidatedCommand
	 * @param queryService : QueryService
	 */
	@CommandHandler
	public void handler(ProductOfferingPriceModificationValidatedCommand command, QueryService queryService) {
		LOGGER.info("ProductOfferingPriceModificationValidated Command Handler : {}", command);
		String popId = command.getPopId();
		ProductOfferingPrice existingPOP = queryService.getProductOfferingPrice(popId);
		if (null == existingPOP) {
			throw new DiscoManagedClientException(ProductOfferingPriceConstants.DISO_POP_POP_NOT_FOUND, popId, null);
		} else {
			ProductOfferingPrice modifiedPOP = this.productOfferingPrice;
			AggregateLifecycle.apply(new ProductOfferingPriceModificationValidatedEvent(modifiedPOP.getId(),
					existingPOP, modifiedPOP.getLifecycleStatus(), OffsetDateTime.now(), version));

		}

	}






	/**
	 * this method validate POP alteration field.
	 *
	 * @param modifiedPOP
	 * @param existingPOP
	 * @return
	 */


	/**
	 * this method validate POP charge field.
	 *
	 * @param modifiedPOP
	 * @param existingPOP
	 * @return
	 */


	/**
	 * this method checks for common field of POPC and POPA.
	 *
	 * @param modifiedPOP
	 * @param existingPOP
	 * @return string: major or minor
	 */


	/**
	 *
	 * @param versionType
	 * @param className
	 * @param validatedData
	 */


	/**
	 * updates the state of aggregate after applying
	 * ProductOfferingPriceModificationValidatedEvent.
	 *
	 * @param event: ProductOfferingPriceModificationValidatedEvent
	 */
	@EventSourcingHandler
	private void on(final ProductOfferingPriceModificationValidatedEvent event) {
		this.productOfferingPriceId = event.getProductOfferingPriceId();
		this.productOfferingPrice.lifecycleStatus(event.getLifecycleStatus()).version(event.getVersion());
	}

}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.handler;


import com.orange.discobole.productcatalog.catalog.constant.ProductOfferingPriceConstants;
import com.orange.discobole.productcatalog.catalog.constant.ProductSpecConstants;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.Money;
import com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.catalog.service.MongodbDataFilterService;
import com.orange.discobole.productcatalog.catalog.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.catalog.service.RedisService;
import com.orange.discobole.productcatalog.productofferingprice.event.*;
import com.orange.discobole.productcatalog.productofferingprice.event.modify.*;

import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Component
public class ProductOfferingPriceEventHandler {

	private static final Logger LOGGER = LogManager.getLogger(ProductOfferingPriceEventHandler.class);


	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	@Resource
	private MongodbDataFilterService mongodbDataFilterService;

	@Resource
	private RedisService redisService;

	public void handle(final ProductOfferingPriceInitiatedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceInitiatedEvent - {}", event);
		ProductOfferingPrice productOfferingPrice = new ProductOfferingPrice();
		productOfferingPrice.id(event.getProductOfferingPriceId()).type(event.getProductOfferingPriceType().getValue())
				.lastUpdate(event.getLastUpdate()).lifecycleStatus(event.getLifecycle());
		productOfferingPriceService.saveProductOfferingPrice(productOfferingPrice);
		LOGGER.debug("ProductOfferingPrice created - {}", productOfferingPrice);
	}

	public void handle(ProductOfferingPriceChargeIdentityDataDescribedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceChargeIdentityDataDescribedEvent - {}", event);


		Update update = new Update();
		Money price = new Money();
		price.setUnit(event.getPrice().getUnit());
		price.setValue(customRoundPrice(event.getPrice().getValue(),event.getPrice().getUnit()));

		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PRICE, price);
		update.set(ProductOfferingPriceConstants.UNIT_OF_MEASURE, event.getUnitOfMeasure());
		update.set(ProductOfferingPriceConstants.PRICE_TYPE, event.getPriceType());
		update.set(ProductOfferingPriceConstants.IMMEDIATE_PAYMENT, event.getImmediatePayment());
		update.set(ProductOfferingPriceConstants.HREF, event.getHref());
		update.set(ProductOfferingPriceConstants.PRORATION_TYPE, event.getProrationType());
		update.set(ProductOfferingPriceConstants.RECURRING_CHARGE_PERIOD_LENGTH, event.getRecurringChargePeriodLength());
		update.set(ProductOfferingPriceConstants.RECURRING_CHARGE_PERIOD_TYPE, event.getRecurringChargePeriodType());
		update.set(ProductOfferingPriceConstants.POP_RELATIONSHIP, event.getProductOfferingPriceRelationships());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.CHARGE_CYCLE, event.getChargeCycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());
		update.set("_class", "com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPriceCharge");

		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);
	}

	public void handle(ProductOfferingPriceAlterationIdentityDataDescribedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceAlterationIdentityDataDescribedEvent - {}", event);

		Update update = new Update();
		if (event.getPrice() != null) {
			Money price = new Money();
			price.setUnit(event.getPrice().getUnit());
			price.setValue(customRoundPrice(event.getPrice().getValue(), event.getPrice().getUnit()));
			update.set(ProductOfferingPriceConstants.PRICE, price);
		} else {
			update.unset(ProductOfferingPriceConstants.PRICE);
		}
		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PRICE_TYPE, event.getPriceType());
		update.set(ProductOfferingPriceConstants.UNIT_OF_MEASURE, event.getUnitOfMeasure());
		update.set(ProductOfferingPriceConstants.HREF, event.getHref());
		update.set(ProductOfferingPriceConstants.PRORATION_TYPE, event.getProrationType());
		update.set(ProductOfferingPriceConstants.PERCENTAGE, event.getPercentage());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingPriceConstants.APPLICATION_DURATION, event.getApplicationDuration());
		update.set(ProductOfferingPriceConstants.PRIORITY, event.getPriority());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.APPLICATION_OFFSET, event.getApplicationOffset());
		update.set("_class", "com.orange.discobole.productcatalog.catalog.dto.generated.common.ProductOfferingPriceAlteration");

		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);

	}

	public void handle(ProductOfferingPriceInstallmentPlanIdentityDataDescribedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceInstallmentChargedentityDataDescribedEvent - {}", event);

		Update update = new Update();
		if (event.getPrice() != null) {
			Money price = new Money();
			price.setUnit(event.getPrice().getUnit());
			price.setValue(customRoundPrice(event.getPrice().getValue(), event.getPrice().getUnit()));
			update.set(ProductOfferingPriceConstants.PRICE, price);
		} else {
			update.unset(ProductOfferingPriceConstants.PRICE);
		}
		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PARTNER, event.getPartner());
		update.set(ProductOfferingPriceConstants.EXTERNALID, event.getExternalId());
		update.set(ProductOfferingPriceConstants.DOWNPAYMENT, event.getDownPayment());
		update.set(ProductOfferingPriceConstants.INTERESTRATE, event.getInterestRate());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingPriceConstants.APPLICATION_DURATION, event.getApplicationDuration());
		update.set(ProductOfferingPriceConstants.POP_RELATIONSHIP,event.getProductOfferingPriceRelationships());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingPriceConstants.HREF, event.getHref());

		update.set("_class", "com.orange.discobole.productcatalog.catalog.dto.generated.common.InstallmentCharge");




		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);

	}



	public void handle(ProductOfferingPriceTaxAlterationIdentityDataDescribedEvent event) {



		Money price = new Money();
		if(event.getPrice()!=null) {
			price.setUnit(event.getPrice().getUnit());
			price.setValue(customRoundPrice(event.getPrice().getValue(), event.getPrice().getUnit()));
		} else {
			price = null;
		}

		Update update = new Update();
		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PRICE, price);
		update.set(ProductOfferingPriceConstants.HREF, event.getHref());
		update.set(ProductOfferingPriceConstants.PERCENTAGE, event.getPercentage());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());

		update.set("_class", "com.orange.discobole.productcatalog.catalog.dto.generated.common.TaxProductOfferingPriceAlteration");


		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);


	}


	public void handle(ProductOfferingPriceVersionCreatedEvent event) {


		Update update = new Update();
		update.set(ProductOfferingPriceConstants.VERSION, event.getVersion());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);

	}

	public void handle(ProductOfferingPriceCancelledEvent event) {
//		ProductOfferingPrice productOfferingPrice = productOfferingPriceService
//				.getProductOfferingPriceById(event.getProductOfferingPrice().getId());
		productOfferingPriceService.removeProductOfferingPrice(event.getProductOfferingPrice().getId());
	}


	public void handle(ProductOfferingPriceAlterationIdentityDataModifiedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceAlterationIdentityDataModifiedEvent - {}", event);
//		ProductOfferingPrice productOfferingPrice = productOfferingPriceService
//				.getProductOfferingPriceById(event.getProductOfferingPriceId());
//		productOfferingPrice.name(event.getName()).description(event.getDescription()).price(event.getPrice())
//				.priceType(event.getPriceType()).unitOfMeasure(event.getUnitOfMeasure())
//                .percentage(event.getPercentage()).lastUpdate(event.getLastUpdate()).applicationDuration(event.getApplicationDuration())
//				.priority(event.getPriority()).lifecycleStatus(event.getLifecycle()).validFor(event.getValidFor());
//		productOfferingPriceService.saveProductOfferingPrice(productOfferingPrice);

		Update update = new Update();
		if (event.getPrice() != null) {
			Money price = new Money();
			price.setUnit(event.getPrice().getUnit());
			price.setValue(customRoundPrice(event.getPrice().getValue(), event.getPrice().getUnit()));
			update.set(ProductOfferingPriceConstants.PRICE, price);
		} else {
			update.unset(ProductOfferingPriceConstants.PRICE);
		}
		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PRICE_TYPE, event.getPriceType());
		update.set(ProductOfferingPriceConstants.UNIT_OF_MEASURE, event.getUnitOfMeasure());
		update.set(ProductOfferingPriceConstants.PERCENTAGE, event.getPercentage());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());
		update.set(ProductOfferingPriceConstants.APPLICATION_DURATION, event.getApplicationDuration());
		update.set(ProductOfferingPriceConstants.PRIORITY, event.getPriority());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.APPLICATION_OFFSET, event.getApplicationOffset());


		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);
	}

	public void handle(ProductOfferingPriceChargeIdentityDataModifiedEvent event) {
		LOGGER.debug("Handling ProductOfferingPriceChargeIdentityDataModifiedEvent - {}", event);
//		ProductOfferingPrice productOfferingPrice = productOfferingPriceService
//				.getProductOfferingPriceById(event.getProductOfferingPriceId());
//
//		productOfferingPrice.name(event.getName()).description(event.getDescription()).price(event.getPrice())
//				.unitOfMeasure(event.getUnitOfMeasure()).priceType(event.getPriceType())
//				.immediatePayment(event.getImmediatePayment())
//				.recurringChargePeriodLength(event.getRecurringChargePeriodLength())
//				.popRelationship(event.getProductOfferingPriceRelationships())
//				.lifecycleStatus(event.getLifecycle())
//				.validFor(event.getValidFor())
//				.recurringChargePeriodType(event.getRecurringChargePeriodType()).lastUpdate(event.getLastUpdate());
//		productOfferingPriceService.saveProductOfferingPrice(productOfferingPrice);
//
		Money price = new Money();
		price.setUnit(event.getPrice().getUnit());
		price.setValue(customRoundPrice(event.getPrice().getValue(),event.getPrice().getUnit()));
		Update update = new Update();
		update.set(ProductOfferingPriceConstants.NAME, event.getName());
		update.set(ProductOfferingPriceConstants.DESCRIPTION, event.getDescription());
		update.set(ProductOfferingPriceConstants.PRICE, price);
		update.set(ProductOfferingPriceConstants.UNIT_OF_MEASURE, event.getUnitOfMeasure());
		update.set(ProductOfferingPriceConstants.PRICE_TYPE, event.getPriceType());
		update.set(ProductOfferingPriceConstants.IMMEDIATE_PAYMENT, event.getImmediatePayment());
		update.set(ProductOfferingPriceConstants.RECURRING_CHARGE_PERIOD_LENGTH, event.getRecurringChargePeriodLength());
		update.set(ProductOfferingPriceConstants.POP_RELATIONSHIP, event.getProductOfferingPriceRelationships());
		update.set(ProductOfferingPriceConstants.LIFECYCLE_STATUS, event.getLifecycle());
		update.set(ProductOfferingPriceConstants.VALID_FOR, event.getValidFor());
		update.set(ProductOfferingPriceConstants.RECURRING_CHARGE_PERIOD_TYPE, event.getRecurringChargePeriodType());
		update.set(ProductOfferingPriceConstants.LAST_UPDATE, event.getLastUpdate());

		productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);
	}


	public void handle(ProductOfferingPriceDeleteEvent event) {

		long countEvents = mongodbDataFilterService.filterProductOfferingPriceEventData(event.getLastUpdateDateTime(),
				event.getInterval(), event.getIntervalUnit());
		long count = mongodbDataFilterService.filterProductOfferingPriceData(event.getLastUpdateDateTime(),
				event.getInterval(), event.getIntervalUnit());

		LOGGER.debug("ProductOfferingPrice events records deleted - {}", countEvents);
		LOGGER.debug("ProductOfferingPrice records deleted - {}", count);

	}

	public void handle(ProductOfferingPriceModificationValidatedEvent event) {
		if(!event.getProductOfferingPrice().getVersion().equals(event.getVersion())) {
			Update update = new Update();
			productOfferingPriceService.saveProductOfferingPriceWithRandomId(event.getProductOfferingPrice());
			update.set(ProductSpecConstants.VERSION, event.getVersion());
			update.set(ProductSpecConstants.LIFE_CYCLE_STATUS, event.getLifecycleStatus());
			productOfferingPriceService.updateProductOfferingPrice(event.getProductOfferingPriceId(), update);
			if(redisService != null){
				try {
					redisService.deleteProductOfferingPrice(event.getProductOfferingPriceId());
				}catch (Exception exception){
					LOGGER.info("exception: {}", exception.getMessage());
				}
			}
		}
	}

	public float customRoundPrice(float value, String unit) {
		BigDecimal bd = new BigDecimal(Float.toString(value));
		int digits = Currency.getInstance(unit).getDefaultFractionDigits();
		return bd.setScale(digits, RoundingMode.HALF_UP).floatValue();
	}
}

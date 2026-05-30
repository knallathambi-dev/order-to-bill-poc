// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.productofferingprice.dto.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Quantity;

class ProductOfferingPriceChargeIdentityDataMapperTest {

	@InjectMocks
	ProductOfferingPriceChargeIdentityDataMapper objectPOPCIdentityDataMapper;

	@Test
	void testPOPCIdentityDataMapper() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData();
		identityData.setName("productOfferingPrice");
		identityData.setDescription("productOfferingPrice1");
		identityData.setPriceType(PriceType.RC);
		identityData.setRecurringChargePeriodLength(10);
		identityData.setRecurringChargePeriodType("month");
		identityData.setPrice(new Money().value(100.0f).unit("inr"));
		identityData.setUnitOfMeasure(new Quantity().amount(1.0f).units("inr"));
		assertNotNull(ProductOfferingPriceChargeIdentityDataMapper.toGenerated(identityData));

	}

	@Test
	void testPOPCIdentityDataMapper_WhenDataNull() {
		assertNull(ProductOfferingPriceChargeIdentityDataMapper.toGenerated(null));
	}
	
	@Test
	void testPOPCIdentityDataMapper_whenUnitOfMeasureNull() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData();
		identityData.setName("productOfferingPrice");
		identityData.setDescription("productOfferingPrice1");
		identityData.setPriceType(PriceType.RC);
		identityData.setRecurringChargePeriodLength(10);
		identityData.setRecurringChargePeriodType("month");
		identityData.setPrice(new Money().value(100.0f).unit("inr"));
		identityData.setUnitOfMeasure(null);
		
		DefineProductOfferingPriceChargeIdentityData expected = new DefineProductOfferingPriceChargeIdentityData();
		expected.setName(identityData.getName());
		expected.setDescription(identityData.getDescription());
		expected.setPrice(null);
		expected.setPriceType(identityData.getPriceType());
		expected.setRecurringChargePeriodLength(identityData.getRecurringChargePeriodLength());
		expected.setRecurringChargePeriodType(identityData.getRecurringChargePeriodType());
		expected.setUnitOfMeasure(null);
		
		DefineProductOfferingPriceChargeIdentityData actual=ProductOfferingPriceChargeIdentityDataMapper.toGenerated(identityData);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getRecurringChargePeriodType(),actual.getRecurringChargePeriodType());
		assertNotNull(identityData.toString());

	}

	@Test
	void testPOPCIdentityDataMapper_WhenPriceNull() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData();
		identityData.name("productOfferingPrice").description("productOfferingPrice1").priceType(PriceType.RC)
				.recurringChargePeriodLength(10).recurringChargePeriodType("month").price(null)
				.unitOfMeasure(new Quantity().amount(1.0f).units("inr"));

		DefineProductOfferingPriceChargeIdentityData expected = new DefineProductOfferingPriceChargeIdentityData()
				.name(identityData.getName()).description(identityData.getDescription()).price(null)
				.priceType(identityData.getPriceType())
				.recurringChargePeriodLength(identityData.getRecurringChargePeriodLength())
				.recurringChargePeriodType(identityData.getRecurringChargePeriodType())
				.unitOfMeasure(new com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.Quantity()
						.amount(identityData.getUnitOfMeasure().getAmount())
						.units(identityData.getUnitOfMeasure().getUnits()));

		DefineProductOfferingPriceChargeIdentityData actual = ProductOfferingPriceChargeIdentityDataMapper
				.toGenerated(identityData);
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getRecurringChargePeriodType(),actual.getRecurringChargePeriodType());
		assertNotNull(expected.toString());
	}
}

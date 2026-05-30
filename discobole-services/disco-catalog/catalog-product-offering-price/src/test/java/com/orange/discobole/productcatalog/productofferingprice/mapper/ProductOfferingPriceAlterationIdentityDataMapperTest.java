// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Quantity;

class ProductOfferingPriceAlterationIdentityDataMapperTest {

	@InjectMocks
	ProductOfferingPriceAlterationIdentityDataMapper popAlterationIdentityDataMapper;

	@Test
	void testToGenerate() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData();
		identityData.setName("productOfferingPrice");
		identityData.setDescription("productOfferingPrice1");
		identityData.setPriceType(PriceType.RC);
		identityData.setPrice(new Money().value(100.0f).unit("inr"));
		identityData.setUnitOfMeasure(new Quantity().amount(1.0f).units("inr"));
		assertNotNull(ProductOfferingPriceAlterationIdentityDataMapper.toGenerated(identityData));

	}
	
	@Test
	void testToGenerate_DataNull() {
		assertNull(ProductOfferingPriceAlterationIdentityDataMapper.toGenerated(null));
	}
	
	@Test
	void testToGenerate_PriceNull() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData();
		identityData.name("productOfferingPrice").description("productOfferingPrice1").priceType(PriceType.RC).price(null).unitOfMeasure(new Quantity().amount(1.0f).units("inr"));
		assertNotNull(ProductOfferingPriceAlterationIdentityDataMapper.toGenerated(identityData));

	}
	
	@Test
	void testToGenerate_MeasureUnitNull() {
		com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData identityData = new com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceAlterationIdentityData();
		identityData.setName("productOfferingPrice");
		identityData.setDescription("productOfferingPrice1");
		identityData.setPriceType(PriceType.RC);
		identityData.setPrice(new Money().value(100.0f).unit("inr"));
		identityData.setUnitOfMeasure(null);
		assertNotNull(ProductOfferingPriceAlterationIdentityDataMapper.toGenerated(identityData));

	}
}

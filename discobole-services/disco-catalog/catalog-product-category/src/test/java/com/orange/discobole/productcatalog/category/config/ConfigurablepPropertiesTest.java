// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.orange.discobole.productcatalog.category.CategoryApplicationTests;
import com.orange.discobole.productcatalog.category.config.ConfigurableProperties;


public class ConfigurablepPropertiesTest extends CategoryApplicationTests {

@InjectMocks
private ConfigurableProperties config;

@Test
public void testConfigurableProperties() {
	config.setCategoryEntityUrl("");
	config.setCategoryUrl("");
	config.setCfsQuery("");
	config.setChannelUrl("");
	config.setMarketUrl("");
	config.setEventType(new ArrayList<>());
	config.setProductOfferingPriceUrl("");
	config.setProductOfferingUrl("");
	config.setProductSpecQuery("");
	config.setStockItemTypeUrl("");
	config.setStockItemUrl("");
	assertNotNull(config.getCategoryEntityUrl());
	assertNotNull(config.getCategoryUrl());
	assertNotNull(config.getCfsQuery());
	assertNotNull(config.getChannelUrl());
	assertNotNull(config.getMarketUrl());
	assertNotNull(config.getEventType());
	assertNotNull(config.getProductOfferingPriceUrl());
	assertNotNull(config.getProductOfferingUrl());
	assertNotNull(config.getProductSpecQuery());
	assertNotNull(config.getStockItemTypeUrl());
	assertNotNull(config.getStockItemUrl());
}

}

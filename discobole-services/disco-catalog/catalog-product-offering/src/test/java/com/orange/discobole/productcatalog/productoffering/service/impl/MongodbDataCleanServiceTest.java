// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.productoffering.ProductOfferingApplicationTests;
import com.orange.discobole.productcatalog.productoffering.config.CronConfig;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductSpecificationLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.SupportEntity;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;
import com.orange.discobole.productcatalog.productoffering.service.impl.MongodbDataCleanService;


class MongodbDataCleanServiceTest extends ProductOfferingApplicationTests {
	
	private MongodbDataCleanService mongodbDataCleanService;
	@Mock
	private QueryService queryService;
	
	@Mock
	private ProductOfferingService productOfferingService;
	
	@Mock 
	 private CronConfig cronConfig;
	
	private ProductSpecification actualProductSpecification;
	
	private ProductOffering actualProductOffering;
	
	private ProductOfferingPrice actualProductOfferingPrice;
	@BeforeEach
	void setUp() {
		
		mongodbDataCleanService =new MongodbDataCleanService();
		ReflectionTestUtils.setField(mongodbDataCleanService, "queryService", queryService);
		ReflectionTestUtils.setField(mongodbDataCleanService, "productOfferingService", productOfferingService);
		ReflectionTestUtils.setField(mongodbDataCleanService, "cronConfig", cronConfig);
		actualProductSpecification=new ProductSpecification().id("productSpecId")
				.lifecycleStatus(ProductSpecificationLifecycle.INSTUDY).supportEntity(SupportEntity.CFSSPEC).lastUpdate(OffsetDateTime.parse("2021-09-27T05:06:35.939+00:00"));
		actualProductOffering=new ProductOffering().lifecycleStatus(ProductOfferingLifecycle.INSTUDY).id("productOfferingId").lastUpdate(OffsetDateTime.parse("2021-09-27T05:06:35.939+00:00"));
		actualProductOfferingPrice=new ProductOfferingPrice().lifecycleStatus(ProductOfferingPriceLifecycle.UNAVAILABLE).id("productOfferingPriceId").lastUpdate(OffsetDateTime.parse("2021-09-27T05:06:35.939+00:00"));
	}
	@Test
	void testScheduleProductOffering() {
		Mockito.doNothing().when(productOfferingService).deleteProductOffering(40L, OffsetDateTime.now(),"DAYS");
		Mockito.when(cronConfig.getJobInterval()).thenReturn(40L);
		Mockito.when(cronConfig.getIntervalUnit()).thenReturn("DAYS");
		mongodbDataCleanService.productOfferingDataClean();
		verify(productOfferingService).deleteProductOffering(any(Long.class),any(OffsetDateTime.class),any(String.class));
		
	}
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.orange.discobole.productcatalog.productspecification.ProductSpecificationApplicationTests;
import com.orange.discobole.productcatalog.productspecification.config.ConfigurableProperties;
import com.orange.discobole.productcatalog.productspecification.config.CronConfig;
import com.orange.discobole.productcatalog.productspecification.dto.generated.common.ProductSpecification;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;



class MongodbDataCleanServiceTest extends ProductSpecificationApplicationTests {
	
	private MongodbDataCleanService mongodbDataCleanService;
	
	@Mock
	private ProductSpecService productSpecService;
	

	
	@Mock
	private ConfigurableProperties configurableProperties;
	
	private ProductSpecification actualProductSpecification;
	
	@Mock 
	private CronConfig cronConfig;
	
	@BeforeEach
	void setUp() {
		
		mongodbDataCleanService = new MongodbDataCleanService();
		ReflectionTestUtils.setField(mongodbDataCleanService, "productSpecService",
				productSpecService);
		ReflectionTestUtils.setField(mongodbDataCleanService, "cronConfig", cronConfig);
	}
	
	@Test
	void testScheduleProductOfferingPrice() {
		Mockito.when(cronConfig.getJobInterval()).thenReturn(40L);
		Mockito.when(cronConfig.getIntervalUnit()).thenReturn("DAYS");
		mongodbDataCleanService.productSpecDataClean();
		verify(productSpecService).deleteProductSpecification(any(OffsetDateTime.class), 
			    any(Long.class),          
			    any(String.class));
				
	}
}

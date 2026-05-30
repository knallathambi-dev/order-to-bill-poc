// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.service.impl;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.productofferingprice.config.CronConfig;
import com.orange.discobole.productcatalog.productofferingprice.service.ProductOfferingPriceService;
import com.orange.discobole.productcatalog.productofferingprice.service.QueryService;

import jakarta.annotation.Resource;

@ConditionalOnExpression("${cron.job-enabled:true}")
@Service
public class MongodbDataCleanService {

	private static final Logger LOGGER = LogManager.getLogger(MongodbDataCleanService.class);
	
	@Resource
	private QueryService queryService;
	@Resource
	private ProductOfferingPriceService productOfferingPriceService;

	@Resource
	private CronConfig cronConfig;

	/**
	 * This method clean the dummy POP from the database when cron job runs.
	 * 
	 * @param 
	 * @return 
	 */
	@Scheduled(cron = "#{@productOfferingPriceExpression}")
	public void productOfferingPriceDataClean() {
		LOGGER.info("Product Offering Price Data Cleaning Started");
		OffsetDateTime delDate = OffsetDateTime.now();
		productOfferingPriceService.deleteProductOfferingPrice(cronConfig.getJobInterval(), delDate,
				cronConfig.getIntervalUnit());
	}
}

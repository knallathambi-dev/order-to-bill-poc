// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.service.impl;

import java.time.OffsetDateTime;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.productoffering.config.CronConfig;
import com.orange.discobole.productcatalog.productoffering.service.ProductOfferingService;
import com.orange.discobole.productcatalog.productoffering.service.QueryService;

@ConditionalOnExpression("${cron.job-enabled:true}")
@Service
public class MongodbDataCleanService {

	@Resource
	private QueryService queryService;

	@Resource
	private ProductOfferingService productOfferingService;

	@Resource
	private CronConfig cronConfig;

	@Scheduled(cron = "#{@productOfferingExpression}")
	public void productOfferingDataClean() {
		OffsetDateTime delDate = OffsetDateTime.now();
		productOfferingService.deleteProductOffering(cronConfig.getJobInterval(), delDate,
				cronConfig.getIntervalUnit());

	}

}

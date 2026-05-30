// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.service.impl;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orange.discobole.productcatalog.productspecification.config.CronConfig;
import com.orange.discobole.productcatalog.productspecification.service.ProductSpecService;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

import java.time.OffsetDateTime;
@ConditionalOnExpression("${cron.job-enabled:true}")
@Service

public class MongodbDataCleanService {

	@Resource
	private QueryService queryService;
	

	
	@Resource
	private ProductSpecService productSpecService;
	

	
	 @Resource 
	 private CronConfig cronConfig;
	 
	
	
	@Scheduled(cron= "#{@productSpecExpression}")
	public void productSpecDataClean() {
		OffsetDateTime delDate=OffsetDateTime.now();
		productSpecService.deleteProductSpecification(delDate,cronConfig.getJobInterval(),cronConfig.getIntervalUnit());	
	}
	

	



}


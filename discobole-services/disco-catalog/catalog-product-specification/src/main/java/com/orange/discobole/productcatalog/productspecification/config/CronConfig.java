// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "cron")
public class CronConfig {
	private String productSpecExpression;
	private String productOfferingExpression; 
	private String productOfferingPriceExpression;
	private Long jobInterval;
	private String intervalUnit;

	
	  @Bean
	 public String productSpecExpression() { 
		  return this.productSpecExpression;
		  }
	 
	@Bean
	public String productOfferingExpression() {
		return this.productOfferingExpression;
	}
	
	  @Bean
	  public String productOfferingPriceExpression() { 
	  return this.productOfferingPriceExpression;
	  }
	 

	public void setProductSpecExpression(String productSpecExpression) {
		this.productSpecExpression = productSpecExpression;
	}
	public void setProductOfferingExpression(String productOfferingExpression) {
		this.productOfferingExpression = productOfferingExpression;
	}
	public void setProductOfferingPriceExpression(String productOfferingPriceExpression) {
		this.productOfferingPriceExpression = productOfferingPriceExpression;
	}
	
	public Long getJobInterval() {
		return jobInterval;
	}

	public void setJobInterval(Long jobInterval) {
		this.jobInterval = jobInterval;
	}

	public String getIntervalUnit() {
		return intervalUnit;
	}

	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}
	
}

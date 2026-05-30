// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice;

import com.orange.discobole.processflow.EnableProcessFlow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class is used to start the product-offering-price application.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@EnableScheduling
//@EnableEurekaClient
@SpringBootApplication
@EnableProcessFlow
public class ProductOfferingPriceApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProductOfferingPriceApplication.class, args);
	}
}

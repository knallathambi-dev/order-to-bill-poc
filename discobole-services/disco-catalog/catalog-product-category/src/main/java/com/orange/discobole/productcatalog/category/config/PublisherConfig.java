// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.discobole.processflow.infra.Publisher;

/**
 * The Class PublisherConfig instantiates different publisher for different
 * process.
 *
 * @author Saurabh Shakya
 * @since 1.0
 */
@Configuration
public class PublisherConfig {
	@Bean
	public Publisher publisher() {
		return new Publisher();
	}
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code com.orange.bos.productoffering.config.ApplicationContextConfig}
 * class is used to read application context configurations.
 *
 * @author Vishal Vachaspati
 * @since 1.0
 */

@Configuration
public class ApplicationContextConfig {
	@Bean
	ApplicationContext getContext() {
		return new AnnotationConfigApplicationContext();
	}
}

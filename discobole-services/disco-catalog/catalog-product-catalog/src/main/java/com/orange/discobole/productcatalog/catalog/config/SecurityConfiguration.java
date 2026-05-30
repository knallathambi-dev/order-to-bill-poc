// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * The Class SecurityConfiguration which loads roles of user from application
 * property file.
 *
 * @author Diksha Srivastava
 * @since 1.0
 */
@Configuration("securityConfiguration")
public class SecurityConfiguration {

	@Value("#{'${role}'.split(',')}")
	private List<String> roles;

	public List<String> getRoles() {
		return roles;
	}
}

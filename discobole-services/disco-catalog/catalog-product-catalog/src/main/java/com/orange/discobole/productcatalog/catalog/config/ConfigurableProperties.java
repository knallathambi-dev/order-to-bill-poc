// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.catalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code com.orange.bos.catalogconfigurator.config.ConfigurableProperties}
 * class is used to read properties from config-server. All the properties are
 * required to be configured in this class.
 *
 * @author Vivek Singh
 * @since 1.0
 */
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "config")
public class ConfigurableProperties {

	
	private String commandPsUpdate;
	private String cpibProductUrl;
	private String adminCPIBConfigUrl;

	public String getCommandPsUpdate() {
		return commandPsUpdate;
	}

	public void setCommandPsUpdate(String commandPsUpdate) {
		this.commandPsUpdate = commandPsUpdate;
	}

	public String getCpibProductUrl() {
		return cpibProductUrl;
	}

	public void setCpibProductUrl(String cpibProductUrl) {
		this.cpibProductUrl = cpibProductUrl;
	}

	public String getAdminCPIBConfigUrl() {
		return adminCPIBConfigUrl;
	}

	public void setAdminCPIBConfigUrl(String adminCPIBConfigUrl) {
		this.adminCPIBConfigUrl = adminCPIBConfigUrl;
	}
}

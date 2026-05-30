// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service;

import com.orange.disco.admin.ProductInventoryConfiguration;

import java.util.List;

public interface ProductInventoryConfigService {

    ProductInventoryConfiguration createCPIBConfig(ProductInventoryConfiguration cpibConfiguration);

    ProductInventoryConfiguration getCPIBConfigById(String cpibConfigId);

    ProductInventoryConfiguration updateCPIBConfig(ProductInventoryConfiguration cpibConfiguration);

    List<ProductInventoryConfiguration> getAllCPIBConfig();

    void deleteCPIBConfig(String id);
}


// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.repository;

import com.orange.disco.admin.ProductInventoryConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductInventoryConfigRepository extends MongoRepository<ProductInventoryConfiguration,String> {
}

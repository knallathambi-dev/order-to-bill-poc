// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.productcatalog.policyrule.dto.ProductOffering;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.dto.ProductSpecification;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ProductCatalogQueryService {

    public String fetchProductOfferingById(String productOfferingId);

    public String fetchProductSpecificationById(String productSpecificationId);

    public List<ProductOffering> fetchProductOfferingByPolicyRule(String policyRuleRefId);

    public List<ProductSpecification> fetchProductSpecificationByPolicyRule(String policyRuleRefId);

    public String fetchProductofferingPriceById(String productOfferingPriceId);

    ProductOffering fetchPOById(String productOfferingId);

    List<ProductOfferingPrice> fetchProductOfferingPriceByIds(List<String> productOfferingPriceIds);

    String removePolicyRule(String policyRuleId);
}

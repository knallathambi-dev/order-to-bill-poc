// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.admin.PolicyRule;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;

import java.util.List;
import java.util.Map;

public interface PolicyRuleService {

    PolicyRule createPolicyRule(PolicyRule policyRule);

    List<PolicyRule> getAllPolicyRule(Long skip,Long limit);

    PolicyRule getPolicyRuleById(String policyRuleId);

    PolicyRule updatePolicyRule(PolicyRule policyRule );

    void deletePolicyRule(String PolicyRuleId);

    List<PolicyRule> getPolicyRuleByIds(List<String> ids);

    List<ProductOfferingPrice> getPOPByPO(String poId);

    Map<String, Object> fetchPolicyRuleWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields);
}

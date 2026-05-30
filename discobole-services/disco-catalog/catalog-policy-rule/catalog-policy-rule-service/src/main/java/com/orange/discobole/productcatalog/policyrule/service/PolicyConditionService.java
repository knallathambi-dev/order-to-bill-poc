// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyCondition;
import com.orange.discobole.admin.PolicyEvent;

import java.util.List;
import java.util.Map;

public interface PolicyConditionService {

    PolicyCondition createPolicyCondition(PolicyCondition policyCondition);

    List<PolicyCondition> getAllPolicyCondition(Long skip,Long limit);

    PolicyCondition getPolicyConditionById(String policyConditionId);

    PolicyCondition updatePolicyCondition(PolicyCondition policyCondition);

    void deletePolicyCondition(String policyConditionId);

    List<PolicyCondition> getPolicyConditionByIds(List<String> ids);

    Map<String, Object> fetchPolicyConditionWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields);
}

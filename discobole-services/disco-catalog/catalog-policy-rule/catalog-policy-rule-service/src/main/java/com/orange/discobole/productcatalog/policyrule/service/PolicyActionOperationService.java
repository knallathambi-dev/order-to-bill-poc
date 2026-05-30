// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyAction;
import com.orange.discobole.admin.PolicyActionOperation;
import com.orange.discobole.admin.PolicyCondition;
import com.orange.discobole.admin.PolicyEvent;

import java.util.List;
import java.util.Map;

public interface PolicyActionOperationService {

    PolicyActionOperation createPolicyActionOperation(PolicyActionOperation policyActionOperation);

    List<PolicyActionOperation> getAllPolicyActionOperation(Long skip,Long limit);

    PolicyActionOperation getPolicyActionOperationById(String policyActionOperationId);

    PolicyActionOperation updatePolicyActionOperation(PolicyActionOperation policyActionOperation);

    void deletePolicyActionOperation(String policyActionOperationId);

    List<PolicyActionOperation> getPolicyActionOperationByIds(List<String> ids);

    Map<String, Object> policyActionOperationWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields);
}

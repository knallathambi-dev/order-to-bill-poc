// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.service;

import com.orange.discobole.admin.PolicyDomainRef;
import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.admin.PolicyRule;

import java.util.List;
import java.util.Map;

public interface PolicyEventService {

    PolicyEvent createPolicyEvent(PolicyEvent policyEvent);

    List<PolicyEvent> getAllPolicyEvent(Long skip,Long limit);

    PolicyEvent getPolicyEventById(String policyEventId);

    PolicyEvent updatePolicyEvent(PolicyEvent policyEvent);

    void deletePolicyEvent(String policyEventId);

    List<PolicyEvent> getPolicyEventByIds(List<String> ids);

    Map<String, Object> fetchPolicyEventWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields);
}

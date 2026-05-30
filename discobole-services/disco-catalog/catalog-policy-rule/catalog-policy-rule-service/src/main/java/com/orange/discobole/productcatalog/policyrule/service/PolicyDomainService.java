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

import java.util.List;

public interface PolicyDomainService {

    PolicyDomainRef createPolicyDomain(PolicyDomainRef policyDomain);

    List<PolicyDomainRef> getAllPolicyDomain(Long skip,Long limit);

    PolicyDomainRef getPolicyDomainById(String policyDomainId);

    PolicyDomainRef updatePolicyDomain(PolicyDomainRef policyDomain);

    void deletePolicyDomain(String policyDomainId);
}

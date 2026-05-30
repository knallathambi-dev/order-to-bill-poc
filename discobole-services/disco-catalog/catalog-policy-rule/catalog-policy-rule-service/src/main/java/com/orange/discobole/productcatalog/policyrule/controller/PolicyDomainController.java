// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.controller;

import com.orange.discobole.admin.PolicyDomainRef;
import com.orange.discobole.productcatalog.policyrule.service.PolicyDomainService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="policyDomain")
@RestController
@RequestMapping("/policyRule/v1")
public class PolicyDomainController {

    private final PolicyDomainService policyDomainService;

    @Autowired
    public PolicyDomainController(PolicyDomainService policyDomainService) {
        this.policyDomainService = policyDomainService;
    }

    @PostMapping("/domain")
    public ResponseEntity<String> createPolicyDomain(@RequestBody PolicyDomainRef policyDomainRef) {
        this.policyDomainService.createPolicyDomain(policyDomainRef);
        return ResponseEntity.status(HttpStatus.CREATED).body("created successfully");
    }

    @GetMapping("/domain")
    public ResponseEntity <List<PolicyDomainRef>> getAllPolicyDomain(@RequestParam(name = "offset", required = false) Long offset,@RequestParam(name = "limit", required = false) Long limit) {
        return ResponseEntity.ok().body(policyDomainService.getAllPolicyDomain(offset,limit));
    }

    @GetMapping("/domain/{id}")
    public ResponseEntity <PolicyDomainRef> getPolicyDomainById(@PathVariable String id) {
        return ResponseEntity.ok().body(policyDomainService.getPolicyDomainById(id));
    }

    @PatchMapping("/domain/{id}")
    public ResponseEntity <String> updatePolicyDomain(@PathVariable String id, @RequestBody PolicyDomainRef policyDomainRef) {
        policyDomainRef.setId(id);
        this.policyDomainService.updatePolicyDomain(policyDomainRef);
        return ResponseEntity.status(HttpStatus.OK).body("updated successfully");
    }

    @DeleteMapping("/domain/{id}")
    public ResponseEntity<String> deletePolicyDomain(@PathVariable String id) {
        this.policyDomainService.deletePolicyDomain(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }
}

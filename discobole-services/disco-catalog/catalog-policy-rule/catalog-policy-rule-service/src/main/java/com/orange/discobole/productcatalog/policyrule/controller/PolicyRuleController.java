// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.controller;

import com.orange.discobole.admin.PolicyRule;
import com.orange.discobole.productcatalog.policyrule.dto.ProductOfferingPrice;
import com.orange.discobole.productcatalog.policyrule.service.PolicyRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="policyRule",description = "A policy rule definition.")
@RestController
@RequestMapping("/policyRule/v1")
public class PolicyRuleController {

    private final PolicyRuleService policyRuleService;

    public PolicyRuleController(PolicyRuleService policyRuleService){
        this.policyRuleService = policyRuleService;
    }

    @PostMapping("/rule")
    public ResponseEntity<PolicyRule> createPolicyRule(@RequestBody PolicyRule policyRule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.policyRuleService.createPolicyRule(policyRule));
    }

    @GetMapping("/rule")
    public ResponseEntity <List<PolicyRule>> getAllPolicyRule(@RequestParam(name = "offset", required = false) Long offset,
                                                              @RequestParam(name = "limit", required = false) Long limit,
                                                              @RequestParam(name = "fields", required = false) String fields,
                                                              @RequestParam(name = "orConditions", required = false) String orConditions,
                                                              @RequestParam(name = "sort", required = false) final String sort,
                                                              @RequestParam(name = "id", required = false) String id,
                                                              @RequestParam(name = "state", required = false) String state,
                                                              @RequestParam(name = "name", required = false) String name,
                                                              @RequestParam(name = "creationDate", required = false) String creationDate,
                                                              @RequestParam(name = "exactTime", required = false) String exactTime,
                                                              @RequestParam(name = "beforeTime", required = false) String beforeTime,
                                                              @RequestParam(name = "afterTime", required = false) String afterTime,
                                                              @RequestParam(name = "startTime", required = false) String startTime,
                                                              @RequestParam(name = "endTime", required = false) String endTime,
                                                              @RequestParam(name = "description", required = false) String description,
                                                              @RequestParam(name = "executionStrategy", required = false) String executionStrategy,
                                                              @RequestParam(name = "isConjustiveNormalForm", required = false) String isConjustiveNormalForm,
                                                              @RequestParam(name = "sequencedAction", required = false) String sequencedAction,
                                                              @RequestParam(name = "sequencedValue", required = false) String sequencedValue,
                                                              @RequestParam(name = "version", required = false) String version,
                                                              @RequestParam(name = "note", required = false) String note,
                                                              @RequestParam(name = "policyAction.id", required = false) String policyActionId,
                                                              @RequestParam(name = "policyCondition.id", required = false) String policyConditionId,
                                                              @RequestParam(name = "policyDomain.id", required = false) String policyDomainId,
                                                              @RequestParam(name = "policyDomain.name", required = false) String policyDomainName,
                                                              @RequestParam(name = "policyEvent.id", required = false) String policyEventId,
                                                              @RequestParam(name = "relatedParty.id", required = false) String relatedPartyId,
                                                              @RequestParam(name = "lastUpdate", required = false) String lastUpdate) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);
        requestParams.put("name", name);
        requestParams.put("state", state);
        requestParams.put("orConditions", orConditions);
        requestParams.put("sort", sort);
        requestParams.put("creationDate", creationDate);
        requestParams.put("description", description);
        requestParams.put("executionStrategy", executionStrategy);
        requestParams.put("isConjustiveNormalForm", isConjustiveNormalForm);
        requestParams.put("sequencedAction", sequencedAction);
        requestParams.put("sequencedValue", sequencedValue);
        requestParams.put("version", version);
        requestParams.put("policyAction.id", policyActionId);
        requestParams.put("policyCondition._id", policyConditionId);
        requestParams.put("policyDomain._id", policyDomainId);
        requestParams.put("policyEvent._id", policyEventId);
        requestParams.put("relatedParty._id", relatedPartyId);
        requestParams.put("lastUpdate", lastUpdate);
        requestParams.put("policyDomain.name", policyDomainName);
        requestParams.put("exactTime", exactTime);
        requestParams.put("beforeTime", beforeTime);
        requestParams.put("afterTime", afterTime);
        requestParams.put("startTime", startTime);
        requestParams.put("endTime", endTime);


        Map<String, Object> policyRuleWithCunt = policyRuleService.fetchPolicyRuleWithCount(requestParams,offset, limit, fields) ;

        long totalRecords = (long) policyRuleWithCunt.get("count");
        @SuppressWarnings("unchecked")
        List<PolicyRule> poicyRules = (List<PolicyRule>) policyRuleWithCunt.get("data");

        if (null == poicyRules) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(poicyRules);
    }

    @GetMapping("/rule/ids")
    public ResponseEntity <List<PolicyRule>> getPolicyRuleByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok().body(policyRuleService.getPolicyRuleByIds(ids));
    }

    @GetMapping("/rule/{id}")
    public ResponseEntity <PolicyRule> getPolicyRuleById(@PathVariable String id) {
        return ResponseEntity.ok().body(policyRuleService.getPolicyRuleById(id));
    }

    @PatchMapping("/rule/{id}")
    public ResponseEntity <PolicyRule> updatePolicyRule(@PathVariable String id, @RequestBody PolicyRule policyRule) {
        policyRule.setId(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.policyRuleService.updatePolicyRule(policyRule));
    }

    @DeleteMapping("/rule/{id}")
    public ResponseEntity<String> deletePolicyRule(@PathVariable String id) {
        this.policyRuleService.deletePolicyRule(id);
        return ResponseEntity.status(HttpStatus.OK).body("Policy rule deleted successfully");
    }

    @GetMapping("/po/{id}")
    public ResponseEntity<List<ProductOfferingPrice>> getPOPByPO(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(policyRuleService.getPOPByPO(id));
    }
}

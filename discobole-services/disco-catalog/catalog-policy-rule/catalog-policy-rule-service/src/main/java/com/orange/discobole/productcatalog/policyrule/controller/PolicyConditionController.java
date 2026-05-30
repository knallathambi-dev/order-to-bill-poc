// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.controller;

import com.orange.discobole.admin.PolicyCondition;
import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.productcatalog.policyrule.service.PolicyConditionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="policyCondition",description = "A policy condition definition.")
@RestController
@RequestMapping("/policyRule/v1")
public class PolicyConditionController {
    private final PolicyConditionService policyConditionService;

    @Autowired
    public PolicyConditionController(PolicyConditionService policyConditionService) {
        this.policyConditionService = policyConditionService;
    }

    @PostMapping("/condition")
    public ResponseEntity<PolicyCondition> createPolicyCondition(@RequestBody PolicyCondition policyCondition) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.policyConditionService.createPolicyCondition(policyCondition));
    }

    @GetMapping("/condition")
    public ResponseEntity <List<PolicyCondition>> getAllPolicyCondition(@RequestParam(name = "offset", required = false) Long offset,
                                                                        @RequestParam(name = "limit", required = false) Long limit,
                                                                        @RequestParam(name = "fields", required = false) String fields,
                                                                        @RequestParam(name = "orConditions", required = false) String orConditions,
                                                                        @RequestParam(name = "sort", required = false) final String sort,
                                                                        @RequestParam(name = "id", required = false) String id,
                                                                        @RequestParam(name = "name", required = false) String name,
                                                                        @RequestParam(name = "creationDate", required = false) String creationDate,
                                                                        @RequestParam(name = "exactTime", required = false) String exactTime,
                                                                        @RequestParam(name = "beforeTime", required = false) String beforeTime,
                                                                        @RequestParam(name = "afterTime", required = false) String afterTime,
                                                                        @RequestParam(name = "startTime", required = false) String startTime,
                                                                        @RequestParam(name = "endTime", required = false) String endTime,
                                                                        @RequestParam(name = "description", required = false) String description,
                                                                        @RequestParam(name = "lastUpdate", required = false) String lastUpdate) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("_id", id);
        requestParams.put("name", name);
        requestParams.put("lastUpdate", lastUpdate);
        requestParams.put("orConditions", orConditions);
        requestParams.put("sort", sort);
        requestParams.put("creationDate", creationDate);
        requestParams.put("description", description);
        requestParams.put("exactTime", exactTime);
        requestParams.put("beforeTime", beforeTime);
        requestParams.put("afterTime", afterTime);
        requestParams.put("startTime", startTime);
        requestParams.put("endTime", endTime);


        Map<String, Object> policyConditionWithCunt = policyConditionService.fetchPolicyConditionWithCount(requestParams,offset, limit, fields) ;

        long totalRecords = (long) policyConditionWithCunt.get("count");
        @SuppressWarnings("unchecked")
        List<PolicyCondition> poicyConditions = (List<PolicyCondition>) policyConditionWithCunt.get("data");

        if (null == poicyConditions) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(poicyConditions);
    }

    @GetMapping("/condition/ids")
    public ResponseEntity <List<PolicyCondition>> getPolicyConditionByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok().body(policyConditionService.getPolicyConditionByIds(ids));
    }

    @GetMapping("/condition/{id}")
    public ResponseEntity <PolicyCondition> getPolicyConditionById(@PathVariable String id) {
        return ResponseEntity.ok().body(policyConditionService.getPolicyConditionById(id));
    }

    @PatchMapping("/condition/{id}")
    public ResponseEntity <PolicyCondition> updatePolicyCondition(@PathVariable String id, @RequestBody PolicyCondition policyCondition) {
        policyCondition.setId(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.policyConditionService.updatePolicyCondition(policyCondition));
    }

    @DeleteMapping("/condition/{id}")
    public ResponseEntity<String> deletePolicyCondition(@PathVariable String id) {
        this.policyConditionService.deletePolicyCondition(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }
}

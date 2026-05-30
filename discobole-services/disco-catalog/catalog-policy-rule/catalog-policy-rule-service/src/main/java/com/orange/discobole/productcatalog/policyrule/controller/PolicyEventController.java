// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.policyrule.controller;

import com.orange.discobole.admin.PolicyEvent;
import com.orange.discobole.admin.PolicyRule;
import com.orange.discobole.productcatalog.policyrule.service.PolicyEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="policyEvent")
@RestController
@RequestMapping("/policyRule/v1")
public class PolicyEventController {
    private final PolicyEventService policyEventService;

    @Autowired
    public PolicyEventController(PolicyEventService policyEventService){
        this.policyEventService = policyEventService;
    }

    @PostMapping("/event")
    public ResponseEntity<PolicyEvent> createPolicyEvent(@RequestBody PolicyEvent policyEvent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.policyEventService.createPolicyEvent(policyEvent));
    }

    @GetMapping("/event")
    public ResponseEntity <List<PolicyEvent>> getAllPolicyEvent(@RequestParam(name = "offset", required = false) Long offset,
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
            @RequestParam(name = "lastUpdate", required = false) String lastUpdate){
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


        Map<String, Object> policyEventWithCunt = policyEventService.fetchPolicyEventWithCount(requestParams,offset, limit, fields) ;

        long totalRecords = (long) policyEventWithCunt.get("count");
        @SuppressWarnings("unchecked")
        List<PolicyEvent> poicyEvents = (List<PolicyEvent>) policyEventWithCunt.get("data");

        if (null == poicyEvents) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(totalRecords));

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(poicyEvents);
    }

    @GetMapping("/event/ids")
    public ResponseEntity <List<PolicyEvent>> getPolicyEventByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok().body(policyEventService.getPolicyEventByIds(ids));
    }

    @GetMapping("/event/{id}")
    public ResponseEntity <PolicyEvent> getPolicyEventById(@PathVariable String id) {
        return ResponseEntity.ok().body(policyEventService.getPolicyEventById(id));
    }

    @PatchMapping("/event/{id}")
    public ResponseEntity <PolicyEvent> updatePolicyEvent(@PathVariable String id, @RequestBody PolicyEvent policyEvent) {
        policyEvent.setId(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.policyEventService.updatePolicyEvent(policyEvent));
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<String> deletePolicyEvent(@PathVariable String id) {
        this.policyEventService.deletePolicyEvent(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
    }
}

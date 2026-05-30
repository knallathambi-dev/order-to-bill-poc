// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class GetProductStepDefinition {

    private final TestRestTemplate rest = new TestRestTemplate();
    private String queryParamRelatedParyID;
    private String queryParamOffset;
    private String queryParamLimit;
    private ResponseEntity<Object> responseEntity;

    @Given("^I have queryParam relatedPartyId (.+)$")
    public void whenHaveQueryParamWithRelatedPartyId(String relatedPartyId) {
        this.queryParamRelatedParyID = "relatedParty.partyOrPartyRole.id=" + relatedPartyId;
    }

    @And("^I have queryParam offset (.+)$")
    public void whenHaveQueryParamWithOffset(int offset) {
        this.queryParamOffset = "offset=" + offset;

    }

    @And("^I have queryParam limit (.+)$")
    public void whenHaveQueryParamWithLimit(int limit) {
        this.queryParamLimit = "limit=" + limit;
    }

    @When("^I make a GET request to \"([^\"]*)\"$")
    public void whenMakeAPostRequestTo(String endPoint) {
        String uri = "http://localhost:8080" + endPoint + "?" + this.queryParamRelatedParyID + "&" + this.queryParamOffset + "&" + this.queryParamLimit;
        responseEntity = rest.getForEntity(uri, Object.class);
    }

    @Then("^the response status should be (\\d+)$")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertEquals(Boolean.TRUE, this.responseEntity.getStatusCode().value() == expectedStatusCode);
    }

    @And("^the list product  should be returned$")
    public void theListProductShouldbeReturned() {
    }
}
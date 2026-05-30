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
import org.springframework.http.*;

import static org.junit.Assert.assertEquals;

public class ProductCreationStepDefinition {

    private final TestRestTemplate rest = new TestRestTemplate();
    private final HttpHeaders headers = new HttpHeaders();
    private ResponseEntity<Object> responseEntity;
    private HttpEntity<Object> requestEntity;


    public ProductCreationStepDefinition() {
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }


    @Given("^I have body with (.+)$")
    public void whenHaveBodyWithStatus(String status) {
        String requestBody = "[{\"status\": \"" + status + "\"}]";
        requestEntity = new HttpEntity<>(requestBody, headers);
    }

    @When("^I make a POST request to \"([^\"]*)\"$")
    public void whenMakeAPostRequestTo(String endPoint) {
        String uri = "http://localhost:8080" + endPoint;
        responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, Object.class);
    }


    @Then("^the response status code should be (\\d+)$")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertEquals(Boolean.TRUE, this.responseEntity.getStatusCode().value() == expectedStatusCode);
    }

    @And("^the response body should contain product resource$")
    public void theResponseBodyShouldContainProductResource() {
    }
}
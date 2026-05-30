// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.cucumber.stepdef;

import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ProductFilterStepDefinitions {

    private RestTemplate restTemplate;
    private ResponseEntity<Object> responseEntity;
    private Map<String, Object> filter;

    private Map<String, Object> additionalParameters;

    @DataTableType
    public ProductFilter defineProductFilter(Map<String, String> entry) {
        return new ProductFilter(entry.get("fields"), Integer.parseInt(entry.get("offset")), Integer.parseInt(entry.get("limit")));
    }


    @Given("I have request filter:")
    public void givenIHaveRequestFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    @Given("I have additional URL parameters:")
    public void givenIHaveAdditionalURLParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    @When("I make a GET request to filter {string}")
    public void whenIMakeAGetRequestToFilter(String endpoint) {
        String url = "http://localhost:8080" + endpoint;
        restTemplate = new RestTemplate();

        // Create the request URL with the filter parameters
        String fields = (String) filter.getOrDefault("fields", "");
        // Convert offset and limit from String to Integer
        int offset = Integer.parseInt(filter.get("offset").toString());
        int limit = Integer.parseInt(filter.get("limit").toString());


        String requestUrl = String.format("%s?fields=%s&offset=%d&limit=%d", url, fields, offset, limit);
        // Add additional parameters to the request URL
        if (additionalParameters != null) {
            for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue().toString();
                requestUrl += "&" + paramName + "=" + paramValue;
            }
        }

        // Make the GET request and retrieve the response
        responseEntity = restTemplate.getForEntity(requestUrl, Object.class);
    }

    @Then("the response status filter should be {int}")
    public void theResponseStatusFilterShouldBe(int expectedStatus) {
        // Verify that the response status code matches the expected value
        assertEquals(expectedStatus, responseEntity.getStatusCode().value());
    }


    @Then("the page list product should be returned")
    public void thenThePageListProductShouldBeReturned() {
        Object responseBody = responseEntity.getBody();
        Assert.assertNotNull(responseBody);
    }

    /**********************************************************************************/

    public static class ProductFilter {
        private String fields;
        private int offset;
        private int limit;

        public ProductFilter(String fields, int offset, int limit) {
            this.fields = fields;
            this.offset = offset;
            this.limit = limit;
        }

        public String getFields() {
            return fields;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }
}




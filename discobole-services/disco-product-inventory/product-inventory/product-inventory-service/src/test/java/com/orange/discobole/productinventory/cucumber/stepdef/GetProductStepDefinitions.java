// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.cucumber.stepdef;

import com.orange.discobole.productinventory.dto.v1.Product;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class GetProductStepDefinitions {
    private final RestTemplate localRestTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080/tmf-api/productInventory/v4/product/";
    private ResponseEntity<Product> localResponseEntity;
    private Product product;

    @Given("there is a product with ID {string}")
    public void thereIsProductWithId(String id) {
        // Send a GET request to retrieve the product with the given ID and store it in a variable
        product = localRestTemplate.getForObject(baseUrl + id, Product.class);
    }

    @When("I request the product with ID {string}")
    public void whenRequestTheProductWithID(String id) {
        // Send a GET request to retrieve the product with the given ID
        localResponseEntity = localRestTemplate.getForEntity(baseUrl + id, Product.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        // Verify that the response status code matches the expected value
        assertEquals(expectedStatus, localResponseEntity.getStatusCode().value());
    }

    @Then("the product details should be returned")
    public void theProductDetailsShouldBeReturned(DataTable dataTable) {
        // Verify that the product details match the expected values in the DataTable
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            assertEquals(row.get("ID"), product.getId());
            assertEquals(row.get("description"), product.getDescription());
            assertEquals(row.get("href"), product.getHref());
        }
    }

    @Given("there is no product with ID {string}")
    public void there_is_no_product_with_ID(String id) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product> responseEntity = restTemplate.getForEntity(baseUrl + id, Product.class);
        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() == null || responseEntity.getBody().equals(""));

    }


    @Then("an error message should be returned with {string}")
    public void anErrorMessageShouldBeReturned(String expectedErrorMessage) {
        // Verify that the response body contains the expected error message
        try {
            // Verify that the response body contains the expected error message
            String responseBody = Objects.requireNonNull(localResponseEntity.getBody()).toString();
            if (localResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                assertTrue(responseBody.contains(expectedErrorMessage));
            } else {
                assertEquals(expectedErrorMessage, responseBody);
            }
        } catch (NullPointerException e) {
            fail("responseEntity.getBody() returned null");
        }
    }

}

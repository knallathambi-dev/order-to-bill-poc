// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productofferingprice.bdd.stepdef;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.productcatalog.productofferingprice.bdd.config.BOSHttpClient;
import com.orange.discobole.productcatalog.productofferingprice.bdd.models.POPInformation;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.common.ProductOfferingPrice;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.PriceType;
import com.orange.discobole.productcatalog.productofferingprice.dto.generated.pop.ProductOfferingPriceLifecycle;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.DefineProductOfferingPriceChargeIdentityData;
import com.orange.discobole.productcatalog.productofferingprice.pojo.productofferingprice.Money;


public class ProductOfferingPriceVersion {
	String qwerty = "";
	String processFlowId = "";
	String taskFlowId = "";
	String popId = "";
	ProductOfferingPrice pop = new ProductOfferingPrice();

	private static String expectedName="cisco updated";
	private static String expectedDesc="cisco description updated";
	
	@Autowired
	BOSHttpClient bosHttpClient;

	@Resource
	private ObjectMapper objectMapper;

	File popInfo = new File("src/test/resources/popInformation.json");
	ObjectMapper mapper = new ObjectMapper();

	@Given("catalog admin is logged in")
	public void catalog_admin_is_logged_in() {
		// comment explaining the method is empty
	}

	@Given("catalog admin creates a new {string}")
	public void catalog_admin_creates_a_new(String process) throws IOException {
		ProcessFlow processFlow;
		try {
			processFlow = bosHttpClient.createProductOfferingPriceProcessFlow("ProductOfferingPriceCreation");
			processFlowId = processFlow.getId();
			taskFlowId = processFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
			TaskFlow popTypeTaskFlow = bosHttpClient.selectPOPTypeRequest(processFlowId, taskFlowId,
					"ProductOfferingPriceCharge");
			taskFlowId = popTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
			TaskFlow popcTaskFlow = bosHttpClient.selectPOPCRequest(processFlowId, taskFlowId,
					new DefineProductOfferingPriceChargeIdentityData().name("cisco").description("cisco description")
							.price(new Money().unit("INR").value(25.5F)).priceType(PriceType.RC));

			taskFlowId = popcTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();

			TaskFlow relationshipTaskFlow = bosHttpClient.selectPOPRelationshipRequest(processFlowId, taskFlowId, new ArrayList<>());
			taskFlowId = relationshipTaskFlow.getLinks().getNextTaskstoBePerformed().get(0)
					.getTaskFlowSpecificationId();

			 bosHttpClient.selectPOPValidityRequest(processFlowId, taskFlowId, "launched");
			popId = (String) popTypeTaskFlow.getCharacteristic().get(0).getValue();
			popInfo.createNewFile();
			POPInformation info = new POPInformation(popId, null);
			mapper.writeValue(popInfo, info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@When("Product Offering Price entity is validated and the lifecycle status changes to {string} automatically")
	public void product_offering_price_entity_is_validated_and_the_lifecycle_status_changes_to_automatically(
			String lifecycle) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals(ProductOfferingPriceLifecycle.LAUNCHED, productOfferingPrice.getLifecycleStatus());
	}

	@Then("the version will automatically will be set to {string}")
	public void the_version_will_automatically_will_be_set_to(String version) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals(version, productOfferingPrice.getVersion());
	}

	@Given("catalog admin is in {string} flow")
	public void catalog_admin_is_in_flow(String string)  {
		ProcessFlow processFlow;
		try {
			processFlow = bosHttpClient.createProductOfferingPriceProcessFlow("ProductOfferingPriceModification");
			processFlowId = processFlow.getId();
			taskFlowId = processFlow.getLinks().getNextTaskstoBePerformed().get(1).getTaskFlowSpecificationId();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Given("Product Offering Price Type is selected")
	public void product_offering_price_type_is_selected() {
		try {
			TaskFlow taskFlow = bosHttpClient.selectPOPTypeRequest(processFlowId, taskFlowId,
					"ProductOfferingPriceCharge");
			taskFlowId = taskFlow.getLinks().getNextTaskstoBePerformed().get(1).getTaskFlowSpecificationId();
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	@Given("Product Offering Price ID is selected")
	public void product_offering_price_id_is_selected() throws IOException {
		try {
			LinkedHashMap<String, String> root = mapper.readValue(popInfo, LinkedHashMap.class);
			popId = root.get("popId");

			TaskFlow taskFlow = bosHttpClient.selectPOPRequest(processFlowId, taskFlowId, popId);
			taskFlowId = taskFlow.getLinks().getNextTaskstoBePerformed().get(1).getTaskFlowSpecificationId();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Given("version type {string} is selected")
	public void version_type_is_selected(String versionType) {
		try {
			TaskFlow taskFlow = bosHttpClient.selectPOPVersionRequest(processFlowId, taskFlowId, versionType);
			taskFlowId = taskFlow.getLinks().getNextTaskstoBePerformed().get(1).getTaskFlowSpecificationId();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@When("Product Offering Price is in lifecycle state {string}")
	public void product_offering_price_is_in_lifecycle_state_active_or(String lifecycle) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals(ProductOfferingPriceLifecycle.fromValue(lifecycle), productOfferingPrice.getLifecycleStatus());

	}

	@Then("user modifies editable attributes for {string} version")
	public void user_modifies_editable_attributes_for_version(String string) {

		try {
			TaskFlow taskFlow = bosHttpClient.selectPOPCRequest(processFlowId, taskFlowId,
					new DefineProductOfferingPriceChargeIdentityData().name("cisco updated").description("cisco description updated")
							.price(new Money().unit("INR").value(25.5F)).priceType(PriceType.RC));
			taskFlowId = taskFlow.getLinks().getNextTaskstoBePerformed().get(3).getTaskFlowSpecificationId();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Then("user saves the changes for the {string}")
	public void user_saves_the_changes_for_the(String string) {
		try {
			TaskFlow validatedTaskFlow = bosHttpClient.selectPOPValidateRequest(processFlowId, taskFlowId, true);
			assertTrue(validatedTaskFlow.getLinks().getNextTaskstoBePerformed().isEmpty());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Then("{string} version is created for the Product Offering ID and version is incremented by {double}.{int}")
	public void version_is_created_for_the_product_offering_id_and_version_is_incremented_by(String string,
			Double double1, Integer int1) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals("0.2.0", productOfferingPrice.getVersion());

	}

	@Then("the previous version of the Product Offering Price ID is set to lifecycle status {string}")
	public void the_previous_version_of_the_product_offering_price_id_is_set_to_lifecycle_status(String lifecycleStatus) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId+"0.1.0");
		assertEquals(ProductOfferingPriceLifecycle.fromValue(lifecycleStatus), productOfferingPrice.getLifecycleStatus());

	}

	@Then("the {string} version replaces the original {string} and all related entity associations are updated to the latest minor version")
	public void the_version_replaces_the_original_and_all_related_entity_associations_are_updated_to_the_latest_minor_version(
			String string, String string2) {

		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals(expectedDesc, productOfferingPrice.getDescription());
		assertEquals(expectedName, productOfferingPrice.getName());
		
	}

	@Then("there shall be no impact to the CPIB for existing customers")
	public void there_shall_be_no_impact_to_the_cpib_for_existing_customers() {
		// comment explaining  the method is empty

	}
	
	//major
	@Then("user modifies editable attributes applicable for {string} version")
	public void userModifiesAttr(String string) {
		try {
			TaskFlow taskFlow = bosHttpClient.selectPOPCRequest(processFlowId, taskFlowId,
					new DefineProductOfferingPriceChargeIdentityData().name("name updated").description("desc updated")
							.price(new Money().unit("INR").value(25.5F)).priceType(PriceType.NRC)).priority(2);
			taskFlowId = taskFlow.getLinks().getNextTaskstoBePerformed().get(4).getTaskFlowSpecificationId();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Then("{string} version is created for the Product Offering PriceID")
	public void version_is_created_for_the_product_offering_price_id(String string) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals("1.2.0", productOfferingPrice.getVersion());
	}

	@Then("the previous version of the Product Offering Price ID is set to {string}")
	public void previousVersionSetToUnavailable(String lifecycleStatus) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId+"0.2.0");
		assertEquals(ProductOfferingPriceLifecycle.fromValue(lifecycleStatus), productOfferingPrice.getLifecycleStatus());
	}

	@Then("the {string} version replaces the original {string} and all related entity associations are updated to the latest major version")
	public void the_version_replaces_the_original_and_all_related_entity_associations_are_updated_to_the_latest_major_version(
			String string, String string2) {
		ProductOfferingPrice productOfferingPrice = bosHttpClient.getPOP(popId);
		assertEquals(PriceType.NRC, productOfferingPrice.getType());
	}

}

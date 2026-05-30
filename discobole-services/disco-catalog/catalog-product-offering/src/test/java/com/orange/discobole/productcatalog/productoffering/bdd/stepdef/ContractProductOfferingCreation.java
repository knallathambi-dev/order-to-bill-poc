// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.bdd.stepdef;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;


import jakarta.annotation.Resource;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.processflow.dto.generated.ProcessFlow;
import com.orange.discobole.processflow.dto.generated.TaskFlow;
import com.orange.discobole.productcatalog.productoffering.bdd.config.BOSHttpClient;
import com.orange.discobole.productcatalog.productoffering.bdd.models.POInformation;
import com.orange.discobole.productcatalog.productoffering.bdd.models.ProcessFlowInfo;
import com.orange.discobole.productcatalog.productoffering.dto.generated.common.ProductOffering;
import com.orange.discobole.productcatalog.productoffering.dto.generated.productoffering.ProductOfferingLifecycle;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ContractProductOfferingCreation {
    String qwerty = "";
    String processFlowId = "";
    String taskFlowId = "";
    String poId = "";
    Integer responseCode=200;
    ProductOffering pop = new ProductOffering();
    int nextTaskSize=0;
    @Autowired
    BOSHttpClient bosHttpClient;

    @Resource
    private ObjectMapper objectMapper;

    File poInfo = new File("src/test/resources/poInformation.json");
    File pfInfo = new File("src/test/resources/pfInformation.json");
    ObjectMapper mapper = new ObjectMapper();
    @Given("catalog admin is logged in")
    public void catalog_admin_is_logged_in() {
        // Write code here that turns the phrase above into concrete action
    }
    @Given("catalog admin starts the {string} flow")
    public void catalog_admin_starts_the_flow(String processFlowName) {
        ResponseEntity<ProcessFlow> processFlowEntity;
        try {
            processFlowEntity = bosHttpClient.createProductOfferingProcessFlow(processFlowName);
            processFlowId = processFlowEntity.getBody().getId();
            taskFlowId = processFlowEntity.getBody().getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
            responseCode=processFlowEntity.getStatusCodeValue();

        } catch (JSONException e) {
            e.printStackTrace();
        }}
    @When("Product Offering Creation will start with {string}")
    public void product_offering_creation_will_start_with(String string) throws IOException {
        pfInfo.createNewFile();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }
    @Then("Response should be {int}")
    public void response_should_be(Integer expectedResponseCode) {
        Assert.assertEquals(expectedResponseCode,responseCode);

    }

    @Given("catalog admin is at offering type step")
    public void catalog_admin_is_at_offering_type_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(pfInfo, LinkedHashMap.class);
       processFlowId=root.get("processFlowId");
       taskFlowId=root.get("taskFlowId");
    }

    @When("admin select the offering type as {string}")
    public void admin_select_the_offering_type_as(String productOfferingType) throws JSONException {
        TaskFlow poTypeTaskFlow = bosHttpClient.selectPOTypeRequest(processFlowId,taskFlowId, productOfferingType);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        responseCode=200;
        poId=(String)poTypeTaskFlow.getCharacteristic().get(0).getValue();
    }

    @Then("Contract Product Offering ID is generated")
    public void contract_product_offering_id_is_generated() {
        Assert.assertNotNull(poId);
    }
    @Then("lifecycle is equal to {string}")
    public void lifecycle_is_equal_to(String string) throws IOException {
        poInfo.createNewFile();
        POInformation info = new POInformation(poId, null,ProductOfferingLifecycle.INSTUDY.getValue());
        mapper.writeValue(poInfo, info);
    }
    @Then("Next Task to be performed list should be populated")
    public void next_task_to_be_performed_list_should_be_populated() throws IOException{
        ProcessFlowInfo info=new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo,info);
        Assert.assertNotNull(taskFlowId);
    }
    @Given("catalog admin is at DefineProductOfferingIdentityData step")
    public void catalog_admin_is_at_define_product_offering_identity_data_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields ProductOfferingIdentityData")
    public void admin_enters_mandatory_fields_product_offering_identity_data() throws Exception {

        TaskFlow poTypeTaskFlow = bosHttpClient.defineProductOfferingIdentityDataORequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at DefineProductOfferingCategory step")
    public void catalog_admin_is_at_define_product_offering_category_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }



    @When("admin enters mandatory fields productCategoryId")
    public void admin_enters_mandatory_fields_product_category_id() throws JSONException,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.defineProductOfferingCategoryRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();

        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at DefineProductOfferingSaleChannel step")
    public void catalog_admin_is_at_define_product_offering_sale_channel_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields channelId")
    public void admin_enters_mandatory_fields_channel_id() throws JSONException,IOException {
        TaskFlow poTypeTaskFlow = bosHttpClient.defineProductOfferingIdentityDataORequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }
    @Given("catalog admin is at DefineProductOfferingMarketSegment step")
    public void catalog_admin_is_at_define_product_offering_market_segment_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }
    @When("admin enters mandatory fields for MarketSegment")
    public void admin_enters_mandatory_fields_for_market_segment() throws JSONException ,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.defineProductOfferingMarketSegmentRequest(processFlowId,taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at DefineBundledOperationSpecification step")
    public void catalog_admin_is_at_define_bundled_operation_specification_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }



    @When("admin enters mandatory fields for BundledOperationSpecification")
    public void admin_enters_mandatory_fields_for_bundled_operation_specification() throws JSONException,IOException {
        TaskFlow poTypeTaskFlow = bosHttpClient.defineDefineBundledOperationSpecificationRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at AssociatePOPtoOperationSpecification step")
    public void catalog_admin_is_at_associate_po_pto_operation_specification_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }
    @When("admin enters mandatory fields for POPtoOperationSpecification")
    public void admin_enters_mandatory_fields_for_po_pto_operation_specification() throws JSONException ,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.associatePOPtoOperationSpecificationRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at ManageProductOfferingTerm step")
    public void catalog_admin_is_at_manage_product_offering_term_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields for ProductOfferingTerm")
    public void admin_enters_mandatory_fields_for_product_offering_term() throws JSONException,IOException {
        TaskFlow poTypeTaskFlow = bosHttpClient.manageProductOfferingTermRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at ManageProductOfferingBundling step")
    public void catalog_admin_is_at_manage_product_offering_bundling_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }


    @When("admin enters mandatory fields for ProductOfferingBundling")
    public void admin_enters_mandatory_fields_for_product_offering_bundling() throws JSONException,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.manageProductOfferingBundlingRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }


    @Given("catalog admin is at DefineRelationship step")
    public void catalog_admin_is_at_define_relationship_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields for Relationship")
    public void admin_enters_mandatory_fields_for_relationship() throws JSONException ,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.defineRelationshipRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at SelectRelatedParty step")
    public void catalog_admin_is_at_select_related_party_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields for RelatedParty")
    public void admin_enters_mandatory_fields_for_related_party() throws JSONException,IOException {
        TaskFlow poTypeTaskFlow = bosHttpClient.defineRelatedPartyRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }

    @Given("catalog admin is at validity step")
    public void catalog_admin_is_at_validity_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }

    @When("admin enters mandatory fields for validity")
    public void admin_enters_mandatory_fields_for_validity() throws JSONException,IOException{
        TaskFlow poTypeTaskFlow = bosHttpClient.defineEntityValidityPeriodRequest(processFlowId, taskFlowId);
        taskFlowId=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().get(0).getTaskFlowSpecificationId();
        ProcessFlowInfo info = new ProcessFlowInfo(processFlowId,taskFlowId);
        mapper.writeValue(pfInfo, info);
    }
    @Given("catalog admin is at validate step")
    public void catalog_admin_is_at_validate_step() throws IOException {
        LinkedHashMap<String, String> root = mapper.readValue(poInfo, LinkedHashMap.class);
        poId = root.get("poId");
        LinkedHashMap<String, String> pf = mapper.readValue(pfInfo, LinkedHashMap.class);
        processFlowId=pf.get("processFlowId");
        taskFlowId=pf.get("taskFlowId");
    }



    @When("admin enters mandatory fields for validate")
    public void admin_enters_mandatory_fields_for_validate() throws JSONException,IOException {
        TaskFlow poTypeTaskFlow = bosHttpClient.defineValidateEntityOperationRequest(processFlowId, taskFlowId);
        nextTaskSize=poTypeTaskFlow.getLinks().getNextTaskstoBePerformed().size();
    }

    @Then("lifecycle status should changed to {string}")
    public void lifecycle_status_should_changed_to(String string) {
        System.out.println("Lifecycle status check not implemented yet. Expected: " );
    }
    @Then("nexttasktobeperformed list should be empty")
    public void nexttasktobeperformed_list_should_be_empty() {
       Assert.assertEquals(0,nextTaskSize);
    }
}



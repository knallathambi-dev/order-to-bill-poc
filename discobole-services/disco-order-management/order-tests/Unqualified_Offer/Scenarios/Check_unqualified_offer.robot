*** Settings ***
Resource            ../Keywords/step_definition.robot
Resource            ../../Global_Configuration/Integration_Variables.robot
Resource            ../../Global_Configuration/Global_Variables.robot

Library             String

*** Variables ***
${PRICE}
${Product_Offering_Id}
${name}     Mobile Package Max
${name2}     Mobile Package Comfort
*** Test Cases ***

Aquisition Qualified Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Unqualified_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                            Set Commercial Eligibility to False
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
    ${response}             Given the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

                            # Step 3 : Confirm Configuration [Valid]
    ${request_body}         When the user uses the PATCH request body of "confirm_configuration"
    ${request_body}         And the user enters the value "ConfirmConfigurationIsProcessed" in the "name" field on the path "[0]" in the "${request_body}"
    ${updated_request_body}    Set Variable    ${request_body}
    ${configID}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${updated_request_body}    And the user enters the value "${configID}" in the "configuration.id" field on the path "[0]['value']" in the "${updated_request_body}"
    ${updated_request_body}    And the user enters the value "confValidated" in the "configuration.state" field on the path "[0]['value']" in the "${updated_request_body}"
    ${response}             Then the user executes the PATCH api with the "confirm_configuration" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${updated_request_body}"
                            And the "${response}" status code should be "200"

    ${relatedEntity_id}     And the user get the relatedEntity ID from "${response}"
                            And the user checks that the status of productOrder "${relatedEntity_id}" is "draft"
                            # step 4 : Cross channel Modification
    ${response}             the user get the response "${response}"
                            # Step 5 : Validate Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "validate_order"
    ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${relatedEntity_id}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            # Delete    -   Step 6 : Pay Order [Valid]
    ${request_body}         When the user uses the PATCH request body of "pay_order"
    ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            And the "${response}" status code should be "200"
                            And the nextTaskstoBePerformed List of "${response}" is empty
                            And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
                            And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                            #And Check Pattern "${valid_configuration_ID}" and Set the Price is "${PRICE}"
                            The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
                            #And the user checks that the price of productOrder "${relatedEntity_id}" is "${PRICE}"
    [Teardown]             Teardown Test Case

    sleep   60s
Aquisition Unqualified Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Unqualified_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name2}
                            Set Commercial Eligibility to true
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
    ${response}             Given the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
                            And the "${response}" status code should be "201"
                            # Step 2 : Select Offer [Valid]
    ${request_body}         When the user uses the PATCH request body of "selectOfferOrContract"
    ${request_body}         And the user enters the value "PickMainOfferOrContractProduct" in the "name" field on the path "[0]" in the "${request_body}"
    ${request_body}         And the user enters the value "${Product_Offering_Id}" in the "id" field on the path "[0]['value']" in the "${request_body}"
    ${request_body}         And the user enters the value "${productOffering_referredType}" in the "referredType" field on the path "[0]['value']" in the "${request_body}"
    ${response}             Then the user executes the PATCH api with the "selectOfferOrContract" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                            the "${response}" should be "unqualified"
                            And the "${response}" status code should be "200"
                            # Step  : Identify party

*** Settings ***
Resource            ../Termination_use_case/Scenarios/Termination_prepaid_mobile_offer.robot
Resource            ../Global_Configuration/Integration_Variables.robot
Resource            ../Global_Configuration/Global_Variables.robot
Resource            ../Global_Configuration/configurator_keywords.robot
Resource            ../Global_Configuration/Global_Keyword.robot

Library             String

*** Variables ***
${PRICE}
${name}         Mobile Package Comfort
${DESCRIPTION_FIELD}    description


*** Test Cases ***

Valid Aquisition Prepaid Mobile Offer Comfort Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
    #
        ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
        ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                                   # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

                              Wait Until Keyword Succeeds    7 min    10s    Check Status Completed of "${relatedEntity_id}"
                              and termination mobile Offer         ${relatedEntity_id}
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case






#***************************************************** data pass + data bundle *************************************************************************************************************************





Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1 GB and data bundle 10 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB and data bundle 10GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2 GB and data bundle 10 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB and data bundle 10GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case

#----------------------------------------------------------with mobile phone samsung black 256----------------------------------------------------------------------------------------------
Valid Aquisition Prepaid Mobile Offer Comfort Instore with samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created

        ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
        ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                                   # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10 GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case






#***************************************************** data pass + data bundle *************************************************************************************************************************





Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1 GB and data bundle 10 GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB and data bundle 10GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2 GB and data bundle 10 GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB and data bundle 10GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Instore samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Home delivery samsung black 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case

#----------------------------------------------------------with mobile phone samsung Purple 256----------------------------------------------------------------------------------------------
Valid Aquisition Prepaid Mobile Offer Comfort Instore with samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created

        ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
        ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                                   # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10 GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case






#***************************************************** data pass + data bundle *************************************************************************************************************************





Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1 GB and data bundle 10 GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB and data bundle 10GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2 GB and data bundle 10 GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB and data bundle 10GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Instore samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Home delivery samsung Purple 256GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "256GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case

#----------------------------------------------------------with mobile phone samsung Purple 512----------------------------------------------------------------------------------------------
Valid Aquisition Prepaid Mobile Offer Comfort Instore with samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created

        ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
        ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                                   # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10 GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case






#***************************************************** data pass + data bundle *************************************************************************************************************************





Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1 GB and data bundle 10 GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB and data bundle 10GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2 GB and data bundle 10 GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB and data bundle 10GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Instore samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Home delivery samsung Purple 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Purple"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case

#----------------------------------------------------------with mobile phone samsung black 512----------------------------------------------------------------------------------------------
Valid Aquisition Prepaid Mobile Offer Comfort Instore with samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created

        ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
        ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                                   # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10 GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 10GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 20GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                     Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 50GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data bundle 80GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case






#***************************************************** data pass + data bundle *************************************************************************************************************************





Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1 GB and data bundle 10 GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB and data bundle 10GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 20GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 50GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 1GB data bundle 80GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "1"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2 GB and data bundle 10 GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB and data bundle 10GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 20GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 50GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Instore samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"


    [Teardown]             Teardown Test Case
Valid Aquisition Prepaid Mobile Offer Comfort with data pass 2GB data bundle 80GB Home delivery samsung Black 512GB
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Comfort_Aquisition

                                Given This token must be created
                                ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data pass "2"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator samsung memory "512GB" and Color "Black"
     ${response}                And the user executes the POST api "${Api_POST_PF}" with the endpoint "${EndPoint_Om_Oc}" to create a process flow
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"

    [Teardown]             Teardown Test Case

*** Settings ***
Resource            ../Termination_use_case/Scenarios/Termination_prepaid_mobile_offer.robot
Resource            ../Global_Configuration/Integration_Variables.robot
Resource            ../Global_Configuration/Global_Variables.robot
Resource            ../Global_Configuration/configurator_keywords.robot
Resource            ../Global_Configuration/Global_Keyword.robot

Library             String

*** Variables ***
${PRICE}

${DESCRIPTION_FIELD}    description
${Product_Offering_Id}
${name}     Smart Mobile and Internet Contract
*** Test Cases ***
Aquisition smart mobile netflix basic , insurance basic , device 256 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance basic , device 256 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance basic , device 512 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance basic , device 512 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix standard , insurance basic , device 256 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case
Aquisition smart mobile netflix standard , insurance basic , device 256 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case
Aquisition smart mobile netflix standard , insurance basic , device 512 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case


Aquisition smart mobile netflix standard , insurance basic , device 512 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 256 Lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 256 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 512 Lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 512 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance basic , device 256 Lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance Basic , device 256 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 512 Lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix basic , insurance premium , device 512 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance premium , device 256 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance premium , device 256 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case



Aquisition smart mobile netflix Standard , insurance premium , device 512 Purple and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance premium , device 512 lilac and hbo
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case



Aquisition smart mobile netflix Standard , insurance Basic , device 256 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance Basic , device 256 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case



Aquisition smart mobile netflix Standard , insurance Basic , device 512 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance Basic , device 512 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Basic"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Basic , insurance premium , device 256 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Basic , insurance premium , device 256 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case



Aquisition smart mobile netflix Basic , insurance premium , device 512 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Basic , insurance premium , device 512 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Basic"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case




Aquisition smart mobile netflix Standard , insurance premium , device 256 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance premium , device 256 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "256 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case



Aquisition smart mobile netflix Standard , insurance premium , device 512 Purple and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Purple"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case

Aquisition smart mobile netflix Standard , insurance premium , device 512 lilac and hbo max
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Smart_mobile_Internet_Aquisition

                            Given This token must be created
                            # Step 1 : Create Process Flow
                            ${Product_Offering_Id}      the customer get product id from catalog for ${name}
                             ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                             and extract id from Configurator "${response}"
                             and Update Product Configuration "Instore"
                             and Update "add" Characteristique HBO "HBO Max"
                             and Update "add" Characteristique device Insurance "Premium"
                             and Update "add" Characteristique Netflix "Standard"
                             Update Product Configurator discouted samsung memory "512 GB" and Color "Lilac"
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
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id}"
    [Teardown]             Teardown Test Case
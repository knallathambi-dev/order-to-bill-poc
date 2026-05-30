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
${Product_Offering_Id_MAX}
${Product_Offering_Id}  ac691fc8-1077-44ca-965c-3e2bff3177d3
${name}     Mobile Package Relax
*** Test Cases ***


Valid Prepaid Mobile Offer Relax Data bundle 10 Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

#***************************************************** data pass 2 GB ***************************************************************************************


Valid Prepaid Mobile Offer Relax Data bundle 10 + data Pass 2 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + data Pass 2 GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + data Pass 2 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + data Pass 2 GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + data Pass 2 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + data Pass 2 GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + data Pass 2 GB Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + data Pass 2 GB Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case



#***************************************************** samsung S24 ***************************************************************************************


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

#***************************************************** Pixel ***************************************************************************************


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Pink Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Purple Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator samsung memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Purple Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Black Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Black Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Pink Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Pink Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Pink Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Pink Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Pink Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

#***************************************************** samsung S24 + data pass***************************************************************************************


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Black + 2gb data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Black + 2 gb data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Black + 2gb data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 256 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 256 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 256 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 256 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + samsung 512 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + samsung 512 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + samsung 512 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + samsung 512 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

#***************************************************** Pixel ***************************************************************************************


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Pink + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 256 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 256 Purple + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 256 pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator samsung memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Purple + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 256 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "256GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Black + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Black + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Black"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Pink + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 10 + Pixel 128 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "10"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Pink + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 20 + Pixel 128 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "20"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Pink + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 50 + Pixel 128 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "50"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Pink + 2GB data Instore
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Instore"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case

Valid Prepaid Mobile Offer Relax Data bundle 80 + Pixel 128 Pink + 2GB data Home delivery
    [Documentation]         This test case consists of testing the acquisition of a prepaid offer for an identified customer by demonstrating the necessary steps.
    [Tags]                      Test_Suite_Package_Relax_Aquisition

                                Given This token must be created
                                ${response}    the customer create a product Configuration for ${Product_Offering_Id}
                                and extract id from Configurator "${response}"
                                and Update Product Configuration "Home delivery"
                                and Update "add" Characteristique Data Bundle "80"
                                and Update "add" Characteristique Data pass "2"

                                and Update Product Configurator Pixel memory "128GB" and Color "Pink"
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

        ${relatedEntity_id_Comfort}     And the user get the relatedEntity ID from "${response}"
                                And the user checks that the status of productOrder "${relatedEntity_id_Comfort}" is "draft"
                                # step 4 : Cross channel Modification
       ${response}             the user get the response "${response}"
                                # Step 5 : Validate Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "validate_order"
        ${request_body}         And the user enters the value "ValidateOrderByCustomer" in the "name" field on the path "[0]" in the "${request_body}"
        ${request_body}         And the user enters the value "${relatedEntity_id_Comfort}" in the "productOrderId" field on the path "[0]['value']" in the "${request_body}"
        ${request_body}         And the user enters the value "orderValidatedByCustomer" in the "orderValidationStatus" field on the path "[0]['value']" in the "${request_body}"
        ${response}             Then the user executes the PATCH api with the "validate_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                # Delete    -   Step 6 : Pay Order [Valid]
        ${request_body}         When the user uses the PATCH request body of "pay_order"
        ${response}             Then the user executes the PATCH api with the "pay_order" href "['_links']['nextTaskstoBePerformed'][1]['href']" of the "${response}" using specific "${request_body}"
                                And the "${response}" status code should be "200"
                                And the nextTaskstoBePerformed List of "${response}" is empty
                                And Verify Shipment Relationships of productOrder "${relatedEntity_id_Comfort}"

              #                  And the user checks that the status of productOrder "${relatedEntity_id}" is "accepted"
               #                 And the user checks that the status PI of productOrder "${relatedEntity_id}" is "Confirmed"
                               # Assuming you have extracted all the required IDs into variables
    [Teardown]             Teardown Test Case


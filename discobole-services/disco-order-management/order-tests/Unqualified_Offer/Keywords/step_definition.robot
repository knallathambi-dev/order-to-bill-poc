*** Settings ***
Resource            ../../Global_Configuration/Integration_Variables.robot
Resource            ../../Global_Configuration/Global_Variables.robot
Resource            ../../Global_Configuration/Manage_Token.robot
Library    RequestsLibrary
Library    Collections
Library    JSONLibrary
Library             String

*** Variables ***
${computedProductConfigurationID}
${productConfiguration}
${GLOBAL_CONFIG_IDS}

*** Keywords ***
the customer get product id from catalog for ${name}
    [RETURN]    ${Product_Offering_Id}
    &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
    Create Session    session    ${EndPoint_Catalog}

    ${response}=    GET Request    session    /productCatalogManagement/v1/productOffering?name=${name}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Log    Response data: ${Return_response}    # Log the entire response for debugging

    # Initialize Package_ID to avoid undefined variable error if no match is found
    ${Package_ID}=    Set Variable    None

    FOR    ${product}    IN    @{Return_response}
        ${Package_Name}=    Get From Dictionary    ${product}    name
        ${productOffering_type}=    Get From Dictionary    ${product}    @type
        ${lifecyclestatus}=    Get From Dictionary    ${product}    lifecycleStatus

        # Log the values being checked for each product
        Log    Checking Product: Name=${Package_Name}, Type=${productOffering_type}, Status=${lifecyclestatus}

        # Check if the product matches the required conditions
        ${is_matching}=    Evaluate    '${Package_Name}' == '${name}' and '${productOffering_type}' == 'Contract' and '${lifecyclestatus}' == 'launched'
        Run Keyword If    ${is_matching}      Set Global Variable    ${Product_Offering_Id}    ${product['id']}
            Log    Matched Product ID: ${Product_Offering_Id}
    END


    # Log final result or error if no valid ID was found
    Log    Matched Product ID: ${Product_Offering_Id}



the user executes the POST api "${api}" with the endpoint "${end-point}" to create a process flow
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=     OperatingSystem.Get File      Global_Configuration/DATA/DataUnqualified/body_for_PF_creation.json
    ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF    '${productOffering_referredType}' == 'productOffering'
                &{headers}=    Create Dictionary    Content-Type=application/json    Authorization=Bearer ${OM_TOKEN}    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
                                      Create Session    session    ${end-point}
                                      ${response}=    POST Request    session    ${api}    headers=&{headers}    data=${body}
           ELSE
                     Fail    The '@referredType' in ${MY_FILE} does not equal "productOffering". Cannot proceed with the test.
           END

    Create Session    session    ${end-point}
       Set Log Level    NONE
        ${response}=    POST Request    session  ${api}?${log-tag}          headers=&{headers}         data=${body}
        Set Log Level    INFO
            Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
            Log    PATCH Response: Status=${response.content}

the "${response}" status code should be "${code_response}"
    Log  ${response.content}
    Should Be Equal As Strings    ${response.status_code}    ${code_response}

the user enters the value "${value}" in the "${key}" field on the path "${path}" in the "${request_body}"
    [Return]            ${request_body}
    Set To Dictionary   ${request_body['characteristic']${path}}               ${key}                    ${value}

the user uses the PATCH request body of "${file_name}"
    [Return]            ${Content_File}
    IF          '${file_name}' == 'selectOfferOrContract'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_select.json
    ELSE IF     '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_confirm.json
    ELSE IF     '${file_name}' == 'validate_order'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_validate.json
    ELSE
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_pay.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
the user executes the PATCH api with the "${nextTaskToBePerformend}" href "${api}" of the "${response}" using specific "${request_body}"
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF          '${nextTaskToBePerformend}' != 'validate_order'
        ${response}=        evaluate    json.loads('''${response.content}''')      json
    END
    Create Session    session    ${EndPoint_Om_Oc}
     Set Log Level    NONE
        ${response}=    PATCH Request    session  ${response${api}}?${log-tag}          headers=&{headers}         data=${request_body}
        Set Log Level    INFO
            # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response: Status=${response.content}

the user get the relatedEntity ID from "${response}"
    [Return]       ${data['relatedEntity'][0]['id']}
    ${data}=        evaluate    json.loads('''${response.content}''')      json
${relatedEntity_id} =    Run Keyword If    "'relatedEntity' in ${data} and ${data['relatedEntity']}",    Get From List,    ${data['relatedEntity']},    0

Parse Response Data
    [Arguments]    ${response_data}
    ${json}=    Evaluate    json.loads($response_data)    json
    ${filtered_products}=    Create List
    FOR    ${product}    IN    @{json}
        ${product_type}=    Run Keyword And Return Status    Evaluate    type(${product}) == dict    # Check if the type is a dictionary
        Run Keyword If    ${product_type}    Log    Product is a dictionary
    END

Log Product ID If Found
    [Arguments]    ${products}
    Run Keyword If    ${products}    Iterate Products    ${products}

Iterate Products
    [Arguments]    ${products}
    FOR    ${product}    IN    @{products}
        ${product_id}=    Get From Dictionary    ${product}    id
        Log    Product ID with productOffering @type 'Contract': ${product_id}
    END
the user checks that the status of productOrder "${relatedEntity_id}" is "${state_verif}"
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_POI}
    ${response}=    GET Request    session  /productOrderingManagement/v1/productOrder/${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    Should Contain              ${response.text}                "state":"${state_verif}"
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['state']}         ${state_verif}

the user checks that the status PI of productOrder "${relatedEntity_id}" is "${state_verif}"
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${expected_status}=    Set Variable    ${Return_response[0]['operationalStatusChange'][1]['status']}
    Should Be Equal As Strings      ${expected_status}         ${state_verif}

the nextTaskstoBePerformed List of "${response}" is empty
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['_links']['nextTaskstoBePerformed']}       []

the user get the response "${response}"
    [Return]       ${data}
    ${data}=        evaluate    json.loads('''${response.content}''')      json


The user checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}"
    &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
    Create Session    session    ${EndPoint_POI}
    ${response}=    GET Request    session  /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Contain    ${Return_response}    orderTotalPrice
    ${order_total_price}=    Get From Dictionary    ${Return_response}    orderTotalPrice
    Length Should Be Greater Than    ${order_total_price}    0

Length Should Be Greater Than
    [Arguments]    ${list}    ${length}
    ${list_length}=    Get Length    ${list}
    Should Be True    ${list_length} > ${length}

Reset Test Environment
    ${reset_actions}    Create List
    Run Keyword If    '${reset_actions}' != '[]'    Run Keyword And Ignore Error    @{reset_actions}

the user uses the PATCH request body of "${file_name}" for unqualified
    [Return]            ${Content_File}
    IF          '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_confirm.json
    ELSE IF     '${file_name}' == 'identify_party'
            ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DataUnqualified/body_identify.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json

the "${response}" should be "unqualified"
        ${responseEv}    Evaluate    json.loads('''${response.content}''')    json
        Log    ${responseEv}
        # Set global variable for description
        ${description}    Set Variable    '${responseEv["description"]}'
        Log    ${description}
        Should Be Equal As Strings    ${description}    'The selected offer is unqualified'

# ********************* configurator *********************************************


the customer create a product Configuration for ${Product_Offering_Id}
     [Return]             ${response.content}
     &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
     ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/POST Product Configurator.json
     ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
         Log    ${body}
     Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['productOffering']}      id=${Product_Offering_Id}
     ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
     Log    ${jsonMisAJour}
     Create Session    session    ${EndPoint_Product_Configurator}
     Set Log Level    NONE
                                   ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                   Set Log Level    INFO
                                       # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                   Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                   Log    PATCH Response: Status=${response.content}

extract id from Configurator "${response}"
          ${Return_response}=    Evaluate    json.loads('''${response}''')    json
          Log    ${Return_response}
          ${Product_Conf}=    Get From Dictionary    ${Return_response}    id
          Set Global Variable    ${GLOBAL_CONFIG_IDS}    ${GLOBAL_CONFIG_IDS} ${Return_response['id']}
          Log    ${GLOBAL_CONFIG_IDS}
           FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
               ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
               ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
               Run Keyword If    '${product_offering_name}' == 'Shipping'    Set Global Variable    ${productConfiguration}    ${productConfiguration} ${item['productConfiguration']['id']}
               Run Keyword If    '${product_offering_name}' == 'Shipping'    Set Global Variable    ${computedProductConfigurationID}    ${computedProductConfigurationID} ${item['id']}
           END

           # Log the results
           Log    ${computedProductConfigurationID}
           Log    ${productConfiguration}

Update Product Configuration "${shpping}"
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${computedProductConfigID}=      Strip String    ${computedProductConfigurationID}
    ${productConfig}=  Strip String    ${productConfiguration}
      &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Shipement.json
      ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

      ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
      ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}
      ${NEW_PRODUCT_ID}                   Set Variable        ${productConfig}
      ${NEW_SHIPPING_VALUE}               Set Variable        ${shpping}


      # Update main ID
      Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

      # Update IDs in requestProductConfigurationItem
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_PRODUCT_ID}

      # Update Memory Value
      # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
      # Accéder au dictionnaire de la caractéristique
      Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${NEW_SHIPPING_VALUE}
      ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
      Log    ${jsonMisAJour}
      Create Session    session    ${EndPoint_Product_Configurator}
      Set Log Level    NONE
                                    ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                    Set Log Level    INFO
                                        # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                    Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                    Log    PATCH Response: Status=${response.content}


Teardown Test Case
    # Reset global variables to empty string (as a null alternative)
    Set Global Variable    ${computedProductConfigurationID}    ${EMPTY}
    Set Global Variable    ${productConfiguration}              ${EMPTY}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    Set Global Variable    ${RingBackTonePCId}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_CONTRACT_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_BUNDLE_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_MOBILELINE_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_RINGBACKTONE_IDS}                  ${EMPTY}
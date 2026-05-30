*** Settings ***
Resource            ../../Global_Configuration/Integration_Variables.robot
Resource            ../../Global_Configuration/Global_Variables.robot
Resource            ../../Global_Configuration/Manage_Token.robot
Library    RequestsLibrary
Library    Collections
Library    JSONLibrary
Library             String

*** Variables ***
${operationalStatusContract}
${CONFIG_IDS}
${ProductId}

*** Keywords ***

Check Operational Status Terminate Completed of "${relatedEntity_id}"
     &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
     Create Session    session    ${EndPoint_PI}
     ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
     Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
    ${status}=    Get From Dictionary    ${Return_response[0]}    status
    ${Operationalstatus}=    Get From Dictionary    ${Return_response[0]}    operationalStatus
    Should Be Equal As Strings    ${status}    Active
    Should Be Equal As Strings    ${operationalStatus}    Active

the customer create for termination a product Configuration for ${GLOBAL_CONTRACT_IDS}
     [Return]             ${response.content}
     &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
     ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/POST Product Configurator Termination.json
     ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
         Log    ${body}
     Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['product']}      id=${GLOBAL_CONTRACT_IDS}
     ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
     Log    ${jsonMisAJour}
     Create Session    session    ${EndPoint_Product_Configurator}
     Set Log Level    NONE
                                   ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                   Set Log Level    INFO
                                       # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                   Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                   Log    PATCH Response: Status=${response.content}

extract id from Configurator for termination "${response}"
          ${Return_response}=    Evaluate    json.loads('''${response}''')    json
          Log    ${Return_response}
          ${Product_Conf}=    Get From Dictionary    ${Return_response}    id
          Set Global Variable    ${CONFIG_IDS}    ${CONFIG_IDS} ${Return_response['id']}
          Log    ${CONFIG_IDS}

executes for termination the POST api "${api}" with the endpoint "${end-point}" to create a process flow "${related_EntityID}" for termination
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=     OperatingSystem.Get File      Global_Configuration/DATA/DATAModification/body_for_PF_creation_Modif.json
    ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${body['relatedEntity'][0]}    id=${related_EntityID}
    Log    ${body}

    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF    '${productOffering_referredType}' == 'productOffering'
                &{headers}=    Create Dictionary    Content-Type=application/json    Authorization=Bearer ${OM_TOKEN}    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
                                      Create Session    session    ${end-point}
                                      ${response}=    POST Request    session    ${api}    headers=&{headers}    data=${body}
           ELSE
                     Fail    The '@referredType' in ${MY_FILE} does not equal "productOffering". Cannot proceed with the test.
           END

    Create Session    session    ${end-point}
    ${response}=    POST Request    session  ${api}?${log-tag}          headers=&{headers}         data=${body}

the "${response}" status code should be "${code_response}" for termination
    Log  ${response.content}
    Should Be Equal As Strings    ${response.status_code}    ${code_response}

enters the value "${value}" in the "${key}" field on the path "${path}" in the "${request_body}" for termination
    [Return]            ${request_body}
    Set To Dictionary   ${request_body['characteristic']${path}}               ${key}                    ${value}

uses the PATCH request body of "${file_name}"
    [Return]            ${Content_File}
    IF          '${file_name}' == 'selectOfferOrContract'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_select_Max.json
    ELSE IF     '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_confirm_Max.json
    ELSE IF     '${file_name}' == 'validate_order'
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_validate_Max.json
    ELSE
        ${MY_FILE}=         OperatingSystem.Get File                    Global_Configuration/DATA/DATAMax/body_pay_Max.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json

checks that the Operationalstatus PI of productOrder "${relatedEntity_id}" is "PendingTerminate" for Termination
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
           FOR    ${item}    IN    @{Return_response}
               ${typeContract}=    Get From Dictionary    ${item['productOffering']}    @type
               Run Keyword If    '${typeContract}' == 'Contract'    Set Global Variable    ${operationalStatusContract}    ${operationalStatusContract} ${item['operationalStatus']}
           END

           # Log the results
           Log    ${operationalStatusContract}
           ${operationalStatusContract} =    Evaluate    '${operationalStatusContract}'.strip()
           Should Be Equal As Strings    ${operationalStatusContract}    PendingTerminate


executes the PATCH api with the "${nextTaskToBePerformend}" href "${api}" of the "${response}" using specific "${request_body}" for termination
    [Return]             ${response}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   post-valid-pf-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json       Authorization=Bearer ${OM_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    IF          '${nextTaskToBePerformend}' != 'validate_order'
        ${response}=        evaluate    json.loads('''${response.content}''')      json
    END
    Create Session    session    ${EndPoint_Om_Oc}
    ${response}=    PATCH Request    session  ${response${api}}?${log-tag}          headers=&{headers}         data=${request_body}

get the relatedEntity ID from "${response}" for termination
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
        Log    Product ID with productOffering @type 'Contract': ${product_id}
    END

checks that the Operationalstatus PI of productOrder "${relatedEntity_id}" is "PendingModification" for termination
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
           FOR    ${item}    IN    @{Return_response}
               ${typeContract}=    Get From Dictionary    ${item['productOffering']}    @type
               Run Keyword If    '${typeContract}' == 'Contract'    Set Global Variable    ${operationalStatusContract}    ${operationalStatusContract} ${item['operationalStatus']}
           END

           # Log the results
           Log    ${operationalStatusContract}
           ${operationalStatusContract} =    Evaluate    '${operationalStatusContract}'.strip()
           Should Be Equal As Strings    ${operationalStatusContract}    PendingModification

checks that the status of productOrder "${relatedEntity_id}" is "${state_verif}" for termination
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_POI}
    ${response}=    GET Request    session  /productOrderingManagement/v1/productOrder/${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    Should Contain              ${response.text}                "state":"${state_verif}"
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['state']}         ${state_verif}

checks that the status PI of productOrder "${relatedEntity_id}" is "${state_verif}" for termination
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${expected_status}=    Set Variable    ${Return_response[0]['operationalStatusChange'][1]['status']}
    Should Be Equal As Strings      ${expected_status}         ${state_verif}

checks on POI that the version PS of productOrder "${relatedEntity_id}" exist for termination
      &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      Create Session    session    ${EndPoint_POI}
      ${response}=    GET Request    session    /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
      Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
      ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
      FOR    ${item}    IN    @{Return_response['productOrderItem']}
          ${product_spec}=    Get From Dictionary    ${item}    product    productSpecification
          Log   ${product_spec}
          Run Keyword If    ${product_spec} and 'version' in ${product_spec}    Log    Version exists and is: ${product_spec['version']}

      END
checks on POI that the version PO of productOrder "${relatedEntity_id}" exist for termination
      &{headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${OM_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      Create Session    session    ${EndPoint_POI}
      ${response}=    GET Request    session    /productOrderingManagement/v1/productOrder/${relatedEntity_id}    headers=&{headers}
      Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
      ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
      FOR    ${item}    IN    @{Return_response['productOrderItem']}
          ${product_offer}=    Get From Dictionary    ${item}    productOffering
          Log   ${product_offer}
          Run Keyword If    ${product_offer} and 'version' in ${product_offer}    Log    Version exists and is: ${product_offer['version']}

      END

checks on PI that the version PS of productOrder "${relatedEntity_id}" exist for termination
        &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
          Create Session    session    ${EndPoint_PI}
          ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
          Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
          ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          FOR    ${product}    IN    @{Return_response}
                  ${product_Spec}=    Get From Dictionary    ${product}    productSpecification    default=None
                  Log    ${product_Spec}
                  Run Keyword If    ${product_Spec} and 'version' in ${product_Spec}
                  ...    Log    Version exists and is: ${product_Spec['version']}
                  ...  ELSE
                  ...    Log    'productSpecification' does not contain 'version' or is None
              END


checks on PI that the version PO of productOrder "${relatedEntity_id}" exist for termination
      &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
               Create Session    session    ${EndPoint_PI}
               ${response}=    GET Request    session  /productManagement/v1/product?productOrderItem.productOrderId=${relatedEntity_id}          headers=&{headers}
               Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
               ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
               FOR    ${product}    IN    @{Return_response}
                      ${product_offer}=    Get From Dictionary    ${product}    productOffering
                      Log   ${product_offer}
                      Run Keyword If    ${product_offer} and 'version' in ${product_offer}    Log    Version exists and is: ${product_offer['version']}
               END

the nextTaskstoBePerformed List of "${response}" is empty for termination
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['_links']['nextTaskstoBePerformed']}       []

continues the operation in the SHOP instead of the WEB for termination
    [Return]       ${last_object}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_Om_Oc}
    ${response}=    GET Request    session  /processManagement/v1/processFlow?relatedParty.id=231-mf20        headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${last_object}=    Get From List    ${Return_response}    -1

uses the PATCH request body of "${file_name}" for termination
    [Return]            ${Content_File}
    IF          '${file_name}' == 'selectOfferOrContract'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_select_for_modification.json
    ELSE IF     '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_confirm_for_modification.json
    ELSE IF     '${file_name}' == 'validate_order'
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_validate_for_modification.json
    ELSE
        ${MY_FILE}=         OperatingSystem.Get File                    DATA/body_pay_for_modification.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json

checks that the orderTotalPrice list exists in the productOrder "${relatedEntity_id}" for termination
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

uses the PATCH request body of "${file_name}" for unqualified for termination
    [Return]            ${Content_File}
    IF          '${file_name}' == 'confirm_configuration'
        ${MY_FILE}=         OperatingSystem.Get File                    DataUnqualified/body_confirm.json
    ELSE IF     '${file_name}' == 'identify_party'
            ${MY_FILE}=         OperatingSystem.Get File                    DataUnqualified/body_identify.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
get the response "${response}" for termination
    [Return]       ${data}
    ${data}=        evaluate    json.loads('''${response.content}''')      json

Teardown Termination
    # Reset global variables to empty string (as a null alternative)
    Set Global Variable    ${operationalStatusContract}    ${EMPTY}
    Set Global Variable    ${CONFIG_IDS}    ${EMPTY}

get Product id from PI ${GLOBAL_CONTRACT_IDS}
    [RETURN]    ${ProductId}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${OM_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${EndPoint_PI}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.productOrderId=${GLOBAL_CONTRACT_IDS}          headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}   200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
          Log    ${Return_response}
    ${first_object}=   Get From List    ${Return_response}    0
    ${ProductId}=       Get From Dictionary    ${first_object}    id
    Log    The first id is: ${ProductId}
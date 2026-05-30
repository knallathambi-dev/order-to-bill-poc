*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
#Resource            ../../../../Global_Configuration/Integration_Variables.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

Post_with_date
    [Arguments]          ${date}      ${code_verif}       ${reason-verif}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    Set To Dictionary   ${Content_File}    startDate   ${date}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}     Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_valid_hasParent
    [Arguments]          ${contract_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-valid-IsUnder- ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_hasParent.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('@type_value', '${contract_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}     Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201

Post_with_Tow_hasParent
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-tow-IsUnder- ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_with_Tow_IsUnder.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}     Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The new product cannot be created due to an invalid relationship with the product of ID: 668e9281c463cc2d2758db63
    #Parent product Not Contract must have exactly one hasParent relationships.

Post_with_bad_hasParent
    [Arguments]          ${file_name}           ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-IsUnder- ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}     Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_nullable_date
    [Arguments]          ${date}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-nullable-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    Set To Dictionary   ${Content_File}    startDate   ${date}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_with_creationDate
    [Arguments]          ${date_input}              ${date_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-creation-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    Set To Dictionary   ${Content_File}    creationDate   ${date_input}
    ${New_Content_File}    BuiltIn.Convert To String   ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${CreationDate}    Evaluate    json.loads('''${response.content}''')['creationDate']
    Should not be empty         ${CreationDate}
    ${timestamp_actual}         Evaluate        datetime.datetime.strptime('''${CreationDate}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
    ${timestamp_input}          Evaluate        datetime.datetime.strptime('''${date_verif}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
    ${difference}               Evaluate        ${timestamp_actual} - ${timestamp_input}
    ${difference}               Convert To Integer    ${difference}
    ${is_greater}               BuiltIn.Evaluate      ${difference} == 0
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_with_empty_creationDate
    [Arguments]          ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-empty-creationDate-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=     evaluate  json.loads('''${MY_FILE}''')    json
    Set To Dictionary   ${Content_File}    creationDate   ${date_input}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       Invalid 'creationDate' Field

Post_with_remove_field
    [Arguments]           ${source}        ${field}      ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-remove-field-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Remove From Dictionary      ${Content_File${source}}        ${field}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json         Authorization=Bearer ${CPIB_TOKEN}       Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${field}' == 'productOrderItem'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    ELSE
        Should Be Equal As Strings      ${Return_response['code']}          ${code_23}
        Should Be Equal As Strings      ${Return_response['reason']}        ${missing_reason}
    END
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_Bundles
    [Arguments]          ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bundles-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Remove From Dictionary      ${Content_File["productRelationship"][0]["product"]["productRelationship"][0]["product"]}       productRelationship
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_with_status
    [Arguments]          ${field}        ${status_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-status-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    ${field}     ${status_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}        Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_productOffering
    [Arguments]          ${source}     ${field}       ${field_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-productOffering-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File${source}}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}        Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_removed_data
    [Arguments]          ${source}     ${field}        ${code_verif}       ${reason-verif}       ${message_verif}   ${file}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-removed-data-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Remove From Dictionary      ${Content_File${source}}        ${field}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_valid_productOffering
    [Arguments]          ${source}     ${field}       ${field_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-valid-productOffering-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File${source}}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_with_valid_productOffering_for_sells
    [Arguments]          ${source}     ${field}       ${field_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-valid-productOffering-sells-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request_for_sells.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File${source}}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_with_invalid_productOffering_for_sells
    [Arguments]          ${source}     ${field}       ${field_value}        ${code_verif}           ${reason-verif}         ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-invalid-productOffering-sells-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request_for_sells.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File${source}}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_field_productSpecification
    [Arguments]      ${field_name}    ${field_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-field-productSpecification-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File["productRelationship"][0]["product"]["productRelationship"][0]["product"]["productRelationship"][0]["product"]["productSpecification"]}     ${field_name}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_productSpecification
    [Arguments]          ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-productSpecification-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${productSpecification_object}      Create Dictionary
    ...    id=82f3bab6-63a1-4008-96a3-8d411d0c5b38
    ...    name=Time Bundle
    ...    @type=ProductSpecificationRef
    Set To Dictionary    ${Content_File}    productSpecification        ${productSpecification_object}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session   ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_id_productOfferingPrice
    [Arguments]          ${field_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-id-productOfferingPrice-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request_for_POPrice.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${id_object}    Create Dictionary    id         ${field_value}
    ${product_price_list}    Set Variable    ${Content_File["productRelationship"][0]["product"]["productRelationship"][0]["product"]["productPrice"]}
    Set To Dictionary    ${product_price_list}[0]   productOfferingPrice        ${id_object}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${modified_json_string}    Set Variable    ${New_Content_File.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_recurringChargePeriod
    [Arguments]          ${field_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-recurringChargePeriod-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_recurringChargePeriod.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    IF    '${field_value}' == '${EMPTY}'
        ${EMPTY_JSON}=    Create Dictionary
        Set To Dictionary    ${MY_FILE['productPrice'][0]}    recurringChargePeriod    ${EMPTY_JSON}
    ELSE
        Remove From Dictionary    ${MY_FILE['productPrice'][0]}    recurringChargePeriod
    END
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_field_recurringChargePeriod
    [Arguments]     ${field_name}     ${field_value}    ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-field-recurringChargePeriod-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_recurringChargePeriod.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    Set To Dictionary    ${MY_FILE['productPrice'][0]['recurringChargePeriod']}    ${field_name}     ${field_value}
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    log     ${JSON_DATA}
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    #Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_bad_productRelationship
    [Arguments]          ${code_verif}       ${reason-verif}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-bad-productRelationship-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${MY_BLOCK}=   OperatingSystem.Get File          Test_Cases/Product/Data/productRelationship_sells.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${ProductRelationship_json}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File["productRelationship"][0]}   testnow     ${ProductRelationship_json}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_with_valid_status_&_operationalStatus
    [Arguments]          ${status_value}       ${operationalStatus_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-status-and-operationalStatus-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    status     ${status_value}
    Set To Dictionary    ${Content_File}    operationalStatus     ${operationalStatus_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    ${status_output}=    Evaluate    json.loads('''${response.content}''')['statusChange'][0]['status']
    Should Be Equal As Strings              ${status_value}      ${status_output}
    ${status_change_date}=    Evaluate    json.loads('''${response.content}''')['statusChange'][0]['changeDate']
    Should Not Be Empty    ${status_change_date}
    ${operationalStatusChange_output}=    Evaluate    json.loads('''${response.content}''')['operationalStatusChange'][0]['status']
    Should Be Equal As Strings              ${operationalStatus_value}      ${operationalStatusChange_output}
    ${operationalStatusChange_change_date}=    Evaluate    json.loads('''${response.content}''')['operationalStatusChange'][0]['changeDate']
    Should Not Be Empty    ${operationalStatusChange_change_date}

Post_valid
    [Arguments]         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/${file_name}.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json

    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    Run Keyword If    '${file_name}' == 'Post_body_request'                             Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN             ${id}


Post_invalid_@type
    [Arguments]       ${@type_value}          ${@type_replace}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invvalid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('${@type_value}', '${@type_replace}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    IF    '${@type_replace}' == 'worng_@Type'
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        Should Contain                  ${Return_response['message']}       Product Offering with id :
        Should Contain                  ${Return_response['message']}       has invalid @type value
    ELSE
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        Should Contain                  ${Return_response['message']}       productRelationship[0].product.productOffering.atType must not be empty
    END

Post_nonExistant_@type
    [Arguments]       ${@type_value}          ${@type_replace}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invvalid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('${@type_value}', '${@type_replace}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}


Post_empty_@type
    [Arguments]       ${@type_value}          ${@type_replace}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-empty-@type-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('${@type_value}', '${@type_replace}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}

Post_valid_@type
    [Arguments]             ${@type_replace}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request_for_@type.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('value_@type', '${@type_replace}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_valid_without_name
    [Arguments]          ${level}        ${name_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-for-name-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request_for_name.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    IF      '${level}' == 'Contract'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['name']
    ELSE IF     '${level}' == 'BundleProductOffering'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['name']
    ELSE IF     '${level}' == 'AtomicProductOffering'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['productRelationship'][0]['product']['name']
    ELSE
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['productRelationship'][0]['product']['productRelationship'][0]['product']['name']
    END
    Should Be Equal As Strings      ${name_product}      ${name_verif}
    RETURN             ${id}

Post_valid_with_name
    [Arguments]          ${level}        ${name_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-for-name-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=      OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request_for_name.json
    ${MY_FILE}=      evaluate  json.loads('''${MY_FILE}''')    json
    ${Content_File}    BuiltIn.Convert To String  ${MY_FILE}
    ${json_string}     Global_Keywords.Replace Template Values    ${Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    IF      '${level}' == 'Contract'
        Set To Dictionary    ${MY_DATA}     name      ${name_verif}
    ELSE IF     '${level}' == 'BundleProductOffering'
        Set To Dictionary    ${MY_DATA["productRelationship"][0]["product"]}     name      ${name_verif}
    ELSE IF     '${level}' == 'AtomicProductOffering'
        Set To Dictionary    ${MY_DATA["productRelationship"][0]["product"]["productRelationship"][0]["product"]}     name      ${name_verif}
    ELSE
        Set To Dictionary    ${MY_DATA["productRelationship"][0]["product"]["productRelationship"][0]["product"]["productRelationship"][0]["product"]}     name      ${name_verif}
    END
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product        headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    IF      '${level}' == 'Contract'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['name']
    ELSE IF     '${level}' == 'BundleProductOffering'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['name']
    ELSE IF     '${level}' == 'AtomicProductOffering'
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['productRelationship'][0]['product']['name']
    ELSE
        ${name_product}         Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['productRelationship'][0]['product']['productRelationship'][0]['product']['name']
    END
    Should Be Equal As Strings      ${name_product}      ${name_verif}
    RETURN             ${id}

Post_valid_physical
    [Arguments]         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id_product}               Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${product_serial_number}    Set Variable        ${json_object['productRelationship'][0]['product']['productRelationship'][0]['product']['productRelationship'][0]['product']['productSerialNumber']}
    Should not be empty         ${product_serial_number}
    ${Contract_id}                          Set Variable        ${json_object['id']}
    ${BundleProductOffering_id}            Set Variable        ${json_object['productRelationship'][0]['product']['id']}
    ${AtomicProductOffering_id}             Set Variable        ${json_object['productRelationship'][0]['product']['productRelationship'][0]['product']['id']}
    ${physicalProduct_id}                   Set Variable        ${json_object['productRelationship'][0]['product']['productRelationship'][0]['product']['productRelationship'][0]['product']['id']}
    ${parentContract_id}                    Set Variable        ${json_object['productRelationship'][0]['product']['productRelationship'][0]['product']['productRelationship'][1]['product']['id']}
    Should Be Equal As Strings              ${Contract_id}      ${parentContract_id}
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN            ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}

Post_valid_physical_SN
    [Arguments]         ${file_name}
    #${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id_product}               Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${product_serial_number}    Set Variable        ${json_object['productRelationship'][0]['product']['productSerialNumber']}
    Should not be empty         ${product_serial_number}
    ${Contract_id}                          Set Variable        ${json_object['id']}
    ${BundleProductOffering_id}            Set Variable        ${json_object['productRelationship'][0]['product']['id']}
    RETURN            ${Contract_id}       ${BundleProductOffering_id}

Post_valid_atomic_physical
    [Arguments]         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id_product}               Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${product_serial_number}=    Set Variable    ${json_object['productRelationship'][0]['product']['productSerialNumber']}
    Should not be empty         ${product_serial_number}
    ${Contract_id}                          Set Variable        ${json_object['id']}
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN            ${Contract_id}

Post_valid_Shipment
    [Arguments]         ${file_name}
    #${AtomicProductOffering_id}        ${ShipmentProduct_id}       #${parentContract_id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id_product}               Evaluate    json.loads('''${response.content}''')['productRelationship'][0]['product']['id']
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${productSerialNumber}                  Set Variable    ${json_object['productRelationship'][0]['product']['productSerialNumber']}
    Should not be empty                     ${product_serial_number}
    ${Contract_id}                          Set Variable        ${json_object['id']}
    ${BundleProductOffering_id}             Set Variable        ${json_object['productRelationship'][0]['product']['id']}
    ${AtomicProductOffering_id}             Set Variable        ${json_object['productOffering']['id']}
    ${ShipmentProduct_id}                   Set Variable        ${json_object['productRelationship'][0]['product']['productRelationship'][0]['product']['id']}
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN            ${Contract_id}       ${BundleProductOffering_id}

post_invalid_Physical_product
    [Arguments]         ${file_name}        ${level_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-Physical-product-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       Tangible Product can not be instantiated at this level: ${level_value}

post_Physical_product_without_PSN
    [Arguments]         ${code_verif}           ${reason_verif}         ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-Physical-product-without-PSN-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_Invalid_Body_request_for_physicalProduct_without_product_serial_number.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

post_shipment_product_worng_supportEntity
    [Arguments]         ${code_verif}           ${reason_verif}         ${PS_id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-Physical-product-without-PSN-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request_for_shipment_without_CFSSpec.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Contain                  ${Return_response['message']}       Product Specification with id :
    Should Contain                  ${Return_response['message']}       ${PS_id} has not CFS Spec type in support entity or shipping product specification in @baseType

post_Physical_product_worng_PSN
    [Arguments]    ${PSN_value}       ${code_verif}           ${reason_verif}         ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-Physical-product-without-PSN-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_Invalid_Body_request_for_physicalProduct_with_realizingResource.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('5678', '${PSN_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

post_Physical_product_worng_PSN_BR
    [Arguments]    ${PSN_value}       ${code_verif}           ${reason_verif}         ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-Physical-product-without-PSN-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_Invalid_Body_request_for_physicalProduct_with_realizingResource.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('5678', '${PSN_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

post_shipment_product_worng_stock
    [Arguments]        ${code_verif}           ${reason_verif}         ${PS_id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   pos-shipment-product-worng-stock-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File         Test_Cases/Product/Data/Post_body_request_for_shipment_without_stock.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Contain                  ${Return_response['message']}       Product Specification with id :
    Should Contain                  ${Return_response['message']}       ${PS_id} has not stock item type in support Entity

post_Physical_product_realizingResource
    [Arguments]         ${realizingResource_id}              ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   pos-Physical-product-supportEntity-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_Invalid_Body_request_for_physicalProduct_with_realizingResource.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('65x', '${realizingResource_id}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})     string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')          json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF  '${realizingResource_id}' == '69x'
        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}
    ELSE
        Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
        Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
        Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
        Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}
    END


Post_for_attach
    [Arguments]         ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-for-attach-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}    Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN             ${id}

Post_for_attach_AtomicProductOffering
    [Arguments]         ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-for-attach-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    log         ${response}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Atomic_ProductOffering_id}    Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${Atomic_ProductOffering_id}
    ${response_dict} =    Evaluate    json.loads('''${response.content}''')    json
    log     ${response_dict}
    #${AtomicProductOffering_id} =       Get From Dictionary     ${response_dict}        productRelationship[0].product.id
    # Step 1: Get the productRelationship list
    ${product_relationships} =      Collections.Get From Dictionary         ${response_dict}        productRelationship

    # Step 2: Access the first item in the productRelationship list
    ${first_product_relationship} =     Get From List          ${product_relationships}        0

    # Step 3: Get the product dictionary from the first relationship
    ${product} =        Collections.Get From Dictionary       ${first_product_relationship}       product

    # Step 4: Now get the id from the product dictionary
    ${Spec_ProductOffering_id} =       Collections.Get From Dictionary         ${product}      id
    RETURN             ${Atomic_ProductOffering_id}      ${Spec_ProductOffering_id}

Invalid_Post_for_attach
    [Arguments]         ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-post-valid-attach-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       Product Offering with id : ${Handset_productOffering_id} has invalid relationship that doesn't exists in catalog with id ${Contract_Product_ID}
    #Should Be Equal As Strings      ${Return_response['message']}       The new product cannot be created due to an invalid relationship with the product of ID: ${id_value}

Post_for_attach_with_bad_id
    [Arguments]         ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-attach-with-bad-id-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The product with id ${id_value} does not exist

Post_for_attach_with_bad_field
    [Arguments]               ${field}      ${field_value}    ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-attach-with-bad-field-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF  '${field_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        #Should Be Equal As Strings      ${Return_response['message']}      Invalid Input
        Should Be Equal As Strings      ${Return_response['message']}       Invalid '${field}' Field
    ELSE
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_23}
        Should Be Equal As Strings      ${Return_response['reason']}        ${missing_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${field} must not be null
    END

Post_for_attach_with_operationalStatus
    [Arguments]               ${field}      ${field_value}    ${id_value}     ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-attach-with-bad-field-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    ${field}     ${field_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json


Bad_post_for_attach
    [Arguments]             ${id_value}       ${Status_value}      ${OperationelStatus_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-post-attach-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    status                  ${Status_value}
    Set To Dictionary    ${Content_File}    operationalStatus       ${OperationelStatus_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF  '${OperationelStatus_value}' == 'PendingCancel' or '${OperationelStatus_value}' == 'PendingTerminate'
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        #Should Be Equal As Strings      ${Return_response['message']}       The operational status should be created or confirmed
        Should Be Equal As Strings      ${Return_response['message']}       The status should be Created
    ELSE
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        Should Be Equal As Strings      ${Return_response['message']}       Invalid 'operationalStatus' Field
        #The status should be Created

    END

Bad_post_for_attach_status
    [Arguments]             ${id_value}       ${Status_value}      ${OperationelStatus_value}         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-post-attach-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    Set To Dictionary    ${Content_File}    status                  ${Status_value}
    Set To Dictionary    ${Content_File}    operationalStatus       ${OperationelStatus_value}
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string_1}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string_1.replace('id_value', '${id_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')              json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF  '${OperationelStatus_value}' == 'PendingCancel' or '${OperationelStatus_value}' == 'PendingTerminate'
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        #Should Be Equal As Strings      ${Return_response['message']}       The operational status should be created or confirmed
        Should Be Equal As Strings      ${Return_response['message']}       The status should be Created
    ELSE
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
        Should Be Equal As Strings      ${Return_response['message']}       Invalid 'status' Field
        #The status should be Created

    END

Post_valid_for_productCharacteristic
    [Arguments]         ${file_name}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-productCharacteristic-      ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file_name}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive       Authorization=Bearer ${CPIB_TOKEN}
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product         headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${id}    Evaluate    json.loads('''${response.content}''')["productRelationship"][0]["product"]["id"]
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN             ${id}

Post_valid_with_json
    [Arguments]             ${file}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-with-json-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file}.json
    ${MY_DATA}=         Create List
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    Should Contain         ${response.text}          "relationshipType":"rootProduct"
    ${id}    Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_invalid_with_json
    [Arguments]             ${file}         ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-valid-with-json-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/${file}.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${MY_DATA}=         Create List
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}



Post_invalid_ValidityCharacteristic
    [Arguments]     ${field_verif}        ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ValidityCharacteristic.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    IF      '${field_verif}' == 'value'
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('30', 'string_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ELSE
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('Day', 'worng_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    END
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_valid_ValidityCharacteristic_UnitOfMeasure
    [Arguments]     ${field_verif}        ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ValidityCharacteristic.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    IF      '${field_verif}' == 'X'
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('30', 'string_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ELSE
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('Day', 'worng_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    END
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        Created



Post_invalid_ValidityCharacteristic_Exclusion_Validation
    [Arguments]     ${field_verif}        ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ValidityCharacteristic_Exclusion_Validation.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    IF      '${field_verif}' == 'value'
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('30', '"string_value"')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ELSE
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('day', 'worng_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    END
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_invalid_ValidityCharacteristic_Exclusion_Validation_with_invalid_validto
    [Arguments]     ${field_verif}        ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ValidityCharacteristic_with_invalid_validto.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    IF      '${field_verif}' == 'value'
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('30', '"string_value"')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ELSE
        ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('day', 'worng_value')}
        ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    END
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_invalid_ObjectCharacteristic
    [Arguments]     ${field_value}        ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ObjectCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ObjectCharacteristic.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string_2}    Set Variable    ${modified_json_string.replace('2024-04-30T10:19:06Z', '${field_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string_2})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_valid_ValidityCharacteristic
    [Arguments]    ${field_verif}    ${field_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_ValidityCharacteristic.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${MY_DATA}=         Create List
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    IF      '${field_verif}' == 'unit'
        ${modified_json_string}    Set Variable    ${modified_json_string.replace('day', '${field_value}')}
    ELSE
        ${modified_json_string}    Set Variable    ${modified_json_string.replace('"duration": 30', '"duration":"30"')}
    END
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}               Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.text}       Id generated by automation ツ
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}

Post_tow_ValidityCharacteristic
    [Arguments]    ${code_verif}       ${reason-verif}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-invalid-ValidityCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_duplicated_ValidityCharacteristic.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${MY_DATA}=         Create List
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

Post_for_calcul_terminationDate
    [Arguments]    ${duration_value}       ${unit_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-for-calcul-terminationDate-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_for_calcul_terminationDate.json
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('567', '${${duration_value}}')}
    ${modified_json_string}    Set Variable    ${modified_json_string.replace('unit_value', '${unit_value}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}             Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    RETURN       ${id}

Post_with_bad_list
    [Arguments]    ${list_name}     ${list_type}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   post-with-empty-list-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    IF     '${list_type}' == '${EMPTY}'
        ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request.json
    ELSE
        ${MY_FILE}=    OperatingSystem.Get File          Test_Cases/Product/Data/Post_body_request_without_${list_name}.json
    END
    ${Content_File}=    evaluate  json.loads('''${MY_FILE}''')      json
    ${empty_list}=    Create List
    IF    '${list_type}' == '${EMPTY}'
        Set To Dictionary    ${Content_File}        ${list_name}=${empty_list}
    END
    ${New_Content_File}    BuiltIn.Convert To String  ${Content_File}
    ${json_string}     Global_Keywords.Replace Template Values    ${New_Content_File}
    ${modified_json_string}    Set Variable    ${json_string.replace('productOrderId_value', '${log-tag}')}
    ${JSON_DATA}=       Evaluate    json.dumps(${modified_json_string})       string
    ${MY_DATA}=         Evaluate    json.loads('''${JSON_DATA}''')            json
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    POST Request    session  /productInventoryManagement/v1/product           headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    201
    ${id}             Evaluate    json.loads('''${response.content}''')['id']
    Should Contain    ${response.headers['location']}    ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    IF    '${list_type}' == '${EMPTY}'
        Should Contain    ${response.text}    "${list_name}":[]
    ELSE
        Should Not Contain    ${response.text}    "${list_name}"
    END

# Send POST request with customizable parameters
Post_Product_With_Variables
    [Arguments]
    ${file_name}
    ${expected_status}
    ${expected_error}
    ${additional_headers}
    ${replace_value}
    ${set_old_format}
    ${remove_application_duration}
    ${extra_field}
    ${amount}
    ${units}
    # Load JSON template
    ${file_path}=    Set Variable    Test_Cases/Product/Data/${file_name}.json
    ${file_content}=    OperatingSystem.Get File    ${file_path}
    ${json_obj}=    Evaluate    json.loads('''${file_content}''')    json

    # Prepare JSON payload
    ${json_str}=    BuiltIn.Convert To String    ${json_obj}
    ${json_str}=    Global_Keywords.Replace Template Values    ${json_str}

    # Modify applicationDuration
    Run Keyword If    ${set_old_format}    Set ApplicationDuration Old Format    12
    Run Keyword If    ${remove_application_duration}    Remove ApplicationDuration Completely
    Run Keyword If    '${extra_field}' != 'None'    Set ApplicationDuration With Extra Field    12    months    ${extra_field}
    Run Keyword If    '${amount}' != 'None' and '${units}' != 'None'    Set ApplicationDuration In Body    ${amount}    ${units}

    # Replace placeholder with unique log tag
    ${log_tag}=    Catenate    post-valid-    ${uuid}
    ${json_str}=    Set Variable    ${json_str.replace('${replace_value}', '${log_tag}')}
    ${json_data}=    Evaluate    json.dumps(${json_str})    string
    ${body}=    Evaluate    json.loads('''${json_data}''')    json

    # Setup headers
    &{headers}=    Create Dictionary
    ...    Content-Type=application/json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive
    ...    Authorization=Bearer ${CPIB_TOKEN}
    Run Keyword If    ${additional_headers}    Set To Dictionary    &{headers}    ${additional_headers}

    # Send request
    Create Session    session    ${ENDPOINT}
    ${response}=    POST Request    session    /productInventoryManagement/v1/product    headers=&{headers}    data=${body}

    # Verify response
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    ${expected_status}
    Run Keyword If    '${expected_error}' != 'None'    Should Contain    ${response.text}    ${expected_error}
    ${id}=    Evaluate    json.loads('''${response.content}''')['id']
    Run Keyword If    ${expected_status} == 201    Should Contain    ${response.headers['location']}    ${ENDPOINT}/productInventoryManagement/v1/product/${id}
    Run Keyword If    '${file_name}' == 'Post_body_request'    Should Contain    ${response.text}    "relationshipType":"rootProduct"
    RETURN    ${id}

# 3. Set applicationDuration to old format (integer)
Set ApplicationDuration Old Format
    [Arguments]    ${value}
    ${file_content}=    OperatingSystem.Get File    Test_Cases/Product/Data/${FILE_NAME}.json
    ${json}=    Evaluate    json.loads('''${file_content}''')    json
    ${product_price}=    Get From Dictionary    ${json}    productPrice
    Run Keyword If    '${product_price}' == 'None'    Fail    'productPrice' list not found
    ${first_item}=    Get From List    ${product_price}    0
    Set To Dictionary    ${first_item}    applicationDuration=${value}
    Set To List    ${product_price}    0    ${first_item}
    Set To Dictionary    ${json}    productPrice=${product_price}
    Set Suite Variable    ${UPDATED_BODY}    ${json}

# 4. Remove applicationDuration
Remove ApplicationDuration Completely
    ${file_content}=    OperatingSystem.Get File    Test_Cases/Product/Data/${FILE_NAME}.json
    ${json}=    Evaluate    json.loads('''${file_content}''')    json
    ${product_price}=    Get From Dictionary    ${json}    productPrice
    Run Keyword If    '${product_price}' == 'None'    Fail    'productPrice' list not found
    ${first_item}=    Get From List    ${product_price}    0
    Remove From Dictionary    ${first_item}    applicationDuration
    Set To List    ${product_price}    0    ${first_item}
    Set To Dictionary    ${json}    productPrice=${product_price}
    Set Suite Variable    ${UPDATED_BODY}    ${json}

# 5. Set applicationDuration with extra field
Set ApplicationDuration With Extra Field
    [Arguments]    ${amount}    ${units}    ${extra_field}=None
    ${file_content}=    OperatingSystem.Get File    Test_Cases/Product/Data/${FILE_NAME}.json
    ${json}=    Evaluate    json.loads('''${file_content}''')    json
    ${product_price}=    Get From Dictionary    ${json}    productPrice
    Run Keyword If    '${product_price}' == 'None'    Fail    'productPrice' list not found
    ${first_item}=    Get From List    ${product_price}    0
    &{duration}=    Create Dictionary    amount=${amount}    units=${units}
    Run Keyword If    '${extra_field}' != 'None'    Set To Dictionary    ${duration}    extra_field=${extra_field}
    Set To Dictionary    ${first_item}    applicationDuration=${duration}
    Set To List    ${product_price}    0    ${first_item}
    Set To Dictionary    ${json}    productPrice=${product_price}
    Set Suite Variable    ${UPDATED_BODY}    ${json}




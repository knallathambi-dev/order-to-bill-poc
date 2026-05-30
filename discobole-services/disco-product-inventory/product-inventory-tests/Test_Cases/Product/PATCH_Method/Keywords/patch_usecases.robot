*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/Staging_Variables.robot
Resource            ../../../../Resources/Common_Keywords.robot

*** Keywords ***

patch_status
    [Arguments]        ${id}       ${status}      ${operationalStatus}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${current_date}=    Get Current Date
    ${one_hour_earlier}=    Subtract Time From Date    ${current_date}    1 hour
    ${lastUpdateDate_value}    Convert Date    ${one_hour_earlier}    result_format=%Y-%m-%dT%H:%M


patch_field
    [Arguments]        ${id}       ${field_name}      ${field_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${update_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/${field_name}
    ...    value=${field_value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${update_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    Should Contain         ${response.text}          "${field_name}":"${field_value}"

patch_nested_field
    [Arguments]        ${id}    ${field_name}      ${field_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-nested-field-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value_json}      Create Dictionary
    ...    ${field_name}=${field_value}
    ${data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value_json}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${current_date}=    Get Current Date
    ${one_hour_earlier}=    Subtract Time From Date    ${current_date}    1 hour
    ${lastUpdateDate_value}    Convert Date    ${one_hour_earlier}    result_format=%Y-%m-%dT%H:%M
    Should Contain         ${response.text}          "lastUpdateDate":"${lastUpdateDate_value}

invalid_patch_status
    [Arguments]        ${id_value}       ${status}      ${operationalStatus}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400


invalid_patch_product_by_id_with_statuses
    [Arguments]        ${id_value}       ${status}      ${operationalStatus}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    ${message}=         Set Variable             ${Return_response['message']}
    RETURN     ${message}


invalid_patch_operationalStatus_for_tangible_product
    [Arguments]        ${id_value}       ${status}      ${operationalStatus}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    ${message}=         Set Variable             ${Return_response['message']}
    RETURN     ${message}

invalid_patch_operationalStatus_for_Atomic_product
    [Arguments]        ${id_value}       ${status}      ${operationalStatus}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    ${message}=         Set Variable             ${Return_response['message']}
    RETURN     ${message}


invalid_terminated_status
    [Arguments]        ${id_value}      ${status}      ${operationalStatus}       ${id_inner}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Contain                  ${Return_response['message']}       Product with Id :
    Should Contain                  ${Return_response['message']}       ${id_value} can not be terminated because product inner Relationship with id:
    Should Contain                  ${Return_response['message']}       ${id_inner} not in a final status

valid_terminated_status
    [Arguments]        ${id_value}      ${status}      ${operationalStatus}       ${id_inner}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json


valid_aborted_status
    [Arguments]        ${id_value}      ${status}      ${operationalStatus}       ${id_inner}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json

invalid_aborted_status
    [Arguments]        ${id_value}      ${status}      ${operationalStatus}       ${id_inner}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Contain                  ${Return_response['message']}       Product with Id :
    Should Contain                  ${Return_response['message']}       ${id_value} can not be aborted because product inner Relationship with id:
    Should Contain                  ${Return_response['message']}       ${id_inner} not a final status

patch_worng_status
    [Arguments]        ${id_value}       ${status}      ${operationalStatus}        ${msg_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   invalid-patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_value}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${msg_verif}


patch_with_migrateFrom
    [Arguments]        ${id_First}     ${id_Second}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-migrateFrom-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_DATA}      Create Dictionary
    ...    id=${id_Second}
    ...    href=/productInventoryManagement/v1/product/${id_Second}
    ...    @type=ProductRef
    ${value_DATA}      Create Dictionary
    ...    relationshipType=migrateFrom
    ...    product=${product_DATA}
    ...    @type=ProductRelationShip
    ${patch_for_data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_DATA}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

patch_with_bundlesMigrate
    [Arguments]        ${id_First}     ${id_Second}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bundlesMigrate-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_DATA}      Create Dictionary
    ...    id=${id_Second}
    #...    href=/productInventoryManagement/v1/product/${id_Second}
    ...    @type=ProductRef
    ${value_DATA}      Create Dictionary
    ...    relationshipType=bundlesMigrate
    ...    product=${product_DATA}
    #...    @type=ProductRelationShip
    ${patch_for_data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_DATA}
    log     ${patch_for_data}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}      ${patch_for_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    log         ${response}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

patch_with_reliesOn
    [Arguments]        ${id_First}     ${id_Second}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-reliesOn-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_DATA}      Create Dictionary
    ...    id=${id_Second}
    ...    href=/productInventoryManagement/v1/product/${id_Second}
    ...    @type=ProductRef
    ${value_DATA}      Create Dictionary
    ...    relationshipType=ReliesOn
    ...    product=${product_DATA}
    ...    @type=ProductRelationShip
    ${patch_for_data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_DATA}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    log         ${response}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200


patch_with_reliesOn_reliesFrom
    [Arguments]    ${id_First}    ${id_Second}
    # Generate a unique UUID for the log-tag
    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-reliesOn-    ${uuid}
    # Remove spaces from log-tag
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}

    # Create the product reference for the second product
    ${product_DATA1}    Create Dictionary
    ...    id=${id_Second}
    ...    @type=ProductRef

    ${product_DATA2}    Create Dictionary
    ...    id=${id_First}
    ...    @type=ProductRef
    # Create the value for the "reliesOn" relationship

    ${value_RELIES_ON}    Create Dictionary
    ...    relationshipType=reliesOn
    ...    product=${product_DATA1}
    ...    @type=ProductRef

    # Create the patch data for "reliesOn"
    ${patch_for_data_RELIES_ON}    Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_RELIES_ON}

    # Create the value for the "reliesFrom" relationship
    ${value_RELIES_FROM}    Create Dictionary
    ...    relationshipType=reliesFrom
    ...    product=${product_DATA2}
    ...    @type=ProductRef

    # Create the patch data for "reliesFrom"
    ${patch_for_data_RELIES_FROM}    Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_Second}/productRelationship/-
    ...    value=${value_RELIES_FROM}

    # Create a list to hold both patch operations
    ${MY_DATA}=    Create List
    Append To List    ${MY_DATA}    ${patch_for_data_RELIES_ON}
    Append To List    ${MY_DATA}    ${patch_for_data_RELIES_FROM}

    # Set up headers with Authorization token
    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Content-Type=application/json-patch+json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive

    # Create a session and send the PATCH request
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product
    ...    headers=&{headers}    data=${MY_DATA}

    # Assert the response status is 200 OK
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200


patch_invalid_body
    [Arguments]        ${id}    ${field_name}         ${field_value}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-invalid-body-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/${field_name}
    ...    value=${field_value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json

patch_invalid_status
    [Arguments]        ${id}       ${status}      ${operationalStatus}      ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${Status_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/status
    ...    value=${status}
    ${operationalStatus_data}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/operationalStatus
    ...    value=${operationalStatus}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${Status_data}
    Append To list      ${MY_DATA}     ${operationalStatus_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}


patch_exist_productOrderItem
    [Arguments]        ${id}      ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-exist-productOrderItem-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=1001ddrr8ssx
    ...    orderItemId=2
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

patch_bad_productOrderItem
    [Arguments]        ${id}    ${productOrderId_value}       ${code_verif}      ${reason_verif}   ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-productOrderItem-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${productOrderId_value}
    ...    orderItemId=${log-tag}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_bad_orderItemId
    [Arguments]        ${id}    ${productOrderId_value}        ${orderItemId_value}      ${code_verif}      ${reason_verif}          ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-orderItemId-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${productOrderId_value}
    ...    orderItemId=${orderItemId_value}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason_verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_worng_productOrderItem
    [Arguments]        ${id}    ${productOrderId_value}         ${orderItemId_value}     ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-worng-productOrderItem-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${productOrderId_value}
    ...    orderItemId=${orderItemId_value}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_same_relationship
    [Arguments]        ${id}    ${relationshipType_value}       ${id_value}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-same-relationship-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_json}      Create Dictionary
    ...    id=${id_value}
    ${value_json}      Create Dictionary
    ...    relationshipType=${relationshipType_value}
    ...    product=${product_json}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productRelationship/-
    ...    value=${value_json}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}         Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_bad_relationship
    [Arguments]        ${id}    ${relationshipType_value}       ${id_value}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-relationship-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_json}      Create Dictionary
    ...    id=${id_value}
    ${value_json}      Create Dictionary
    ...    relationshipType=${relationshipType_value}
    ...    product=${product_json}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productRelationship/-
    ...    value=${value_json}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_valid_productOrderItem
    [Arguments]        ${id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-valid-productOrderItem-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${log-tag}
    ...    orderItemId=${log-tag}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response[0]['productOrderItem'][1]['productOrderId']}        ${log-tag}
    Should Be Equal As Strings      ${Return_response[0]['productOrderItem'][1]['orderItemId']}        ${log-tag}

patch_valid_string_productCharacteristic
    [Arguments]        ${id}        ${valueType}        ${value_content}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-valid-string-productCharacteristic-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${log-tag}
    ...    orderItemId=${log-tag}
    ${data_valueType}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/valueType
    ...    value=${valueType}
    ${data_value}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/value
    ...    value=${value_content}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}    ${data_valueType}
    Append To list      ${MY_DATA}    ${data_value}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response[0]['productCharacteristic'][0]['valueType']}        ${valueType}
    Should Be Equal As Strings      ${Return_response[0]['productCharacteristic'][0]['value']}            ${value_content}

patch_valid_boolean_productCharacteristic
    [Arguments]        ${id}        ${valueType}        ${value_content}        ${value_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-valid-boolean-productCharacteristic-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${log-tag}
    ...    orderItemId=${log-tag}
    ${data_valueType}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/valueType
    ...    value=${valueType}
    ${data_value}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/value
    ...    value=${value_content}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}    ${data_valueType}
    Append To list      ${MY_DATA}    ${data_value}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400

patch_bad_productCharacteristic
    [Arguments]        ${id}        ${valueType}        ${value_content}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-productCharacteristic-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${log-tag}
    ...    orderItemId=${log-tag}
    ${data_valueType}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/valueType
    ...    value=${valueType}
    ${data_value}      Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id}/productCharacteristic/0/value
    ...    value=${value_content}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}    ${data_valueType}
    Append To list      ${MY_DATA}    ${data_value}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400

patch_duplicates_couple
    [Arguments]        ${id}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-duplicates-couple-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${value}      Create Dictionary
    ...    productOrderId=${log-tag}
    ...    orderItemId=${log-tag}
    ${data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id}/productOrderItem/-
    ...    value=${value}
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}     ${data}
    Append To list      ${MY_DATA}     ${data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

patch_bad_body
    [Arguments]        ${id}    ${MY_DATA}       ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-body-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_24}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

patch_bad_list
    [Arguments]        ${id}    ${MY_DATA}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-body-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_23}
    Should Be Equal As Strings      ${Return_response['reason']}        ${missing_reason}

patch_empty_list
    [Arguments]        ${id}    ${MY_DATA}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bad-body-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Be Equal As Strings        ${response.text}        []

patch_invalid_attribute
    [Arguments]        ${id_value}      ${field_name}       ${field_value}        ${@type_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-invalid-attribute-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    ${MY_DATA}      Create Dictionary
    ...    ${field_name}=${field_value}
    ...    @type=${@type_value}
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product/${id_value}             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    405
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        METHOD_NOT_ALLOWED
    Should Be Equal As Strings      ${Return_response['code']}          ${code_61}
    Should Be Equal As Strings      ${Return_response['reason']}        ${not_allowed_reason}
    Should Be Equal As Strings      ${Return_response['message']}       PATCH method not supported by that resource <${field_name}>

patch_invalid_@type
    [Arguments]        ${id_value}      ${@type_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-invalid-attribute-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    ${MY_DATA}      Create Dictionary
    ...    @type=${@type_value}
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product/${id_value}             headers=&{headers}         data=${MY_DATA}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    405
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        METHOD_NOT_ALLOWED
    Should Be Equal As Strings      ${Return_response['code']}          ${code_61}
    Should Be Equal As Strings      ${Return_response['reason']}        ${not_allowed_reason}
    Should Be Equal As Strings      ${Return_response['message']}       PATCH method not supported by that resource <@type>

patch_with_reliesOnMigrate
    [Arguments]        ${id_First}     ${id_Second}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-bundlesMigrate-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    ${product_DATA}      Create Dictionary
    ...    id=${id_Second}
    #...    href=/productInventoryManagement/v1/product/${id_Second}
    ...    @type=ProductRef
    ${value_DATA}      Create Dictionary
    ...    relationshipType=reliesOnMigrate
    ...    product=${product_DATA}
    #...    @type=ProductRelationShip
    ${patch_for_data}      Create Dictionary
    ...    op=add
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_DATA}
    log     ${patch_for_data}
    #${MY_DATA}      Set Variable            ${patch_for_data}          #${patch_for_data}         #"["+${patch_for_data}+"]"
    ${MY_DATA}=         Create List
    Append To list      ${MY_DATA}      ${patch_for_data}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json-patch+json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session  /productInventoryManagement/v1/product             headers=&{headers}         data=${MY_DATA}
    log         ${response}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

patch_with_bundles
    [Arguments]    ${id_First}    ${id_Second}
    # Generate a unique UUID for the log-tag
    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-reliesOn-    ${uuid}
    # Remove spaces from log-tag
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}

    # Create the product reference for the second product
    ${product_DATA1}    Create Dictionary
    ...    id=${id_Second}
    ...    @type=ProductRef

    ${product_DATA2}    Create Dictionary
    ...    id=${id_First}
    ...    @type=ProductRef
    # Create the value for the "reliesOn" relationship

    ${value_RELIES_ON}    Create Dictionary
    ...    relationshipType=bundles
    ...    product=${product_DATA1}
    ...    @type=ProductRef

    # Create the patch data for "reliesOn"
    ${patch_for_data_RELIES_ON}    Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${id_First}/productRelationship/-
    ...    value=${value_RELIES_ON}

patch_product_by_id
    [Arguments]    ${product_id}    ${inner_id}    ${nested_id}

    ${log-tag}=    Evaluate    str(uuid.uuid4())    modules=uuid
    ${log-tag}=    Catenate   patch-product-     ${log-tag}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}

    ${nested_product}=    Create Dictionary
    ...    id=${nested_id}
    ...    status=Active
    ...    operationalStatus=Active

    ${nested_relationship}=    Create Dictionary
    ...    relationshipType=bundles
    ...    product=${nested_product}

    ${inner_product}=    Create Dictionary
    ...    id=${inner_id}
    ...    productRelationship=@{EMPTY}
    Set To Dictionary    ${inner_product}    productRelationship=${nested_relationship}

    ${outer_relationship}=    Create Dictionary
    ...    relationshipType=bundles
    ...    product=${inner_product}

    ${body}=    Create Dictionary
    ...    productRelationship=${outer_relationship}
    ...    status=Active
    ...    operationalStatus=Active

    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Content-Type=application/json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive

    Create Session    session    ${end-point-cpib}

    ${response}=    PATCH Request
    ...    session
    ...    /productManagement/v1/product/${product_id}
    ...    headers=&{headers}
    ...    json=${body}

    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200
    Log    ${response.text}


patch_product_by_id_with_statuses_and_relations

    [Arguments]    ${product_id}    ${inner_id}    ${nested_id}    ${status}    ${operationalStatus}

    ${nested_product}=    Create Dictionary
    ...    id=${nested_id}
    ...    status=${status}
    ...    operationalStatus=${operationalStatus}

    ${nested_relationship}=    Create Dictionary
    ...    relationshipType=bundles
    ...    product=${nested_product}

    ${inner_product}=    Create Dictionary
    ...    id=${inner_id}
    ...    productRelationship=@{EMPTY}
    Set To Dictionary    ${inner_product}    productRelationship=${nested_relationship}

    ${outer_relationship}=    Create Dictionary
    ...    relationshipType=bundles
    ...    product=${inner_product}

    ${body}=    Create Dictionary
    ...    productRelationship=${outer_relationship}
    ...    status=${status}
    ...    operationalStatus=${operationalStatus}

    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Content-Type=application/json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive

    Create Session    session    ${end-point-cpib}

    ${response}=    PATCH Request
    ...    session
    ...    /productInventoryManagement/v1/product/${product_id}
    ...    headers=&{headers}
    ...    json=${body}

    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

    Log    Headers : ${headers}
    Log    Statut HTTP : ${response.status_code}
    Log    Corps réponse : ${response.text}
    Log    ${response.text}

patch_product_by_id_with_statuses
    [Arguments]    ${product_id}        ${Product}      ${status}       ${operationalStatus}

    ${body}=    Create Dictionary
    ...    @type=${Product}
    ...    status=${status}
    ...    operationalStatus=${operationalStatus}

    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
#   ...    Content-Type=merge-patch+json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive

    Create Session    session    ${end-point-cpib}

    ${response}=    PATCH Request
    ...    session
    ...    /productInventoryManagement/v1/product/${product_id}
    ...    headers=&{headers}
    ...    json=${body}

    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    200

    Log    Headers : ${headers}
    Log    Statut HTTP : ${response.status_code}
    Log    Corps réponse : ${response.text}
    Log    ${response.text}


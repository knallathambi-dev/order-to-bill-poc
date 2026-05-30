*** Settings ***
Resource            ../../../Global_Configuration/Manage_Token.robot
Resource            ../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

get_with_status
    [Arguments]    ${check_value}      ${serviceName_value}        ${version_value}        ${message_value}
    ${uuid}=       Evaluate             uuid.uuid4()            modules=uuid
    ${log-tag}=    Catenate             get-for-compliance      ${uuid}
    ${log-tag}=    Replace String       ${log-tag}              ${space}            ${empty}
    &{headers}=    Create Dictionary    Content-Type=application/json    Authorization=Bearer ${CPIB_TOKEN}    Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /${check_value}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['serviceName']}     ${serviceName_value}
    Should Be Equal As Strings      ${Return_response['version']}         ${version_value}
    Should Be Equal As Strings      ${Return_response['message']}         ${message_value}

get_with_version
    [Arguments]    ${check_value}        ${version_value}
    ${uuid}=       Evaluate             uuid.uuid4()            modules=uuid
    ${log-tag}=    Catenate             get-for-compliance      ${uuid}
    ${log-tag}=    Replace String       ${log-tag}              ${space}            ${empty}
    &{headers}=    Create Dictionary    Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}    Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /${check_value}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['version']}         ${version_value}

get_version
    [Arguments]    ${check_value}
    ${uuid}=       Evaluate             uuid.uuid4()            modules=uuid
    ${log-tag}=    Catenate             get-for-compliance      ${uuid}
    ${log-tag}=    Replace String       ${log-tag}              ${space}            ${empty}
    &{headers}=    Create Dictionary    Content-Type=application/json     Authorization=Bearer ${CPIB_TOKEN}    Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /${check_value}   headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json

get_with_doc
    [Arguments]    ${check_value}
    ${uuid}=       Evaluate             uuid.uuid4()            modules=uuid
    ${log-tag}=    Catenate             get-for-compliance      ${uuid}
    ${log-tag}=    Replace String       ${log-tag}              ${space}            ${empty}
    &{headers}=    Create Dictionary    Content-Type=application/json        Authorization=Bearer ${CPIB_TOKEN}         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /${check_value}    headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    Should not be empty     ${response.content}

get_with_conf
    [Arguments]    ${check_value}
    ${uuid}=       Evaluate             uuid.uuid4()            modules=uuid
    ${log-tag}=    Catenate             get-for-compliance      ${uuid}
    ${log-tag}=    Replace String       ${log-tag}              ${space}            ${empty}
    &{headers}=    Create Dictionary    Content-Type=application/json        Authorization=Bearer ${CPIB_TOKEN}         Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /${check_value}    headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    Should not be empty     ${response.content}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Integers         ${Return_response['paginationLimit']}                       10000
    Should Be Equal As Integers         ${Return_response['exportLimit']}                           1073741824  # Defined 1 GB in the file application.yaml
    #Should Be Equal As Strings          ${Return_response['productCatalogUrl']}                     http://catalog-product-catalog-integration:8080
    #Should Be Equal As Strings          ${Return_response['resourceInventoryManagementUrl']}        http://mock-server-integration:8080/api/resourceInventoryManagement/v1/resource
    Should Be Equal As Strings          ${Return_response['productCatalogUrl']}                     http://catalog-product-catalog-staging:8080
    Should Be Equal As Strings          ${Return_response['resourceInventoryManagementUrl']}        http://mock-server-staging:8080/api/resourceInventoryManagement/v1/resource
    Should Be True                      ${Return_response['productCatalogEnabled']}
    Should Be True                      ${Return_response['resourceInventoryManagementEnabled']}

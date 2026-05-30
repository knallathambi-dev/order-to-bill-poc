*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

delete_jobSpecification
    [Arguments]        ${id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-delete-JobSpecification-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    DELETE Request    session  /productInventoryManagement/v1/jobSpecification/${id}         headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    204

bad_delete_jobSpecification
    [Arguments]        ${id}        ${code_verif}       ${reason-verif}      ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-delete-JobSpecification-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    DELETE Request    session  /productInventoryManagement/v1/jobSpecification/${id}       headers=&{headers}
    Should Be True  '${response.status_code}'=='404'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

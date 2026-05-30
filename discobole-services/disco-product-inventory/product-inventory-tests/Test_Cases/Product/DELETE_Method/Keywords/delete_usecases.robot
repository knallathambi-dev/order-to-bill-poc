*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Keywords ***

delete_product
    [Arguments]        ${id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   valid-delete-product-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    DELETE Request    session  /productInventoryManagement/v1/product/${id}         headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    204

bad_delete_product
    [Arguments]        ${id}        ${code_verif}       ${reason-verif}      ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-delete-product-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    DELETE Request    session  /productInventoryManagement/v1/product/${id}       headers=&{headers}
    Should Be True  '${response.status_code}'=='404'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

check_remove_relationships
    [Arguments]        ${id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   check-remove-relationships-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    #${CPIB_TOKEN}=      Set Variable        eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI2MTg4OTIsImlhdCI6MTc3MjU4MzMyNiwiYXV0aF90aW1lIjoxNzcyNTgyODkyLCJqdGkiOiJmYWMyYTJkNS03OWJjLTQ0ZGEtOTYxNC1jOTc5YzRlYTIyN2UiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6IjQ1ZTIwNGU2LTQyZWItNDk2MS04NWUzLWM3MzE4N2IwNDM3MSIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.v68REGDbiQWKDjV-jRbbx4ZXiG3Y5c76BfM68mZHZiK8jG0Os1Hvuzqt1hz1JaJT4LA9WfTeWSuUGWDEICwavDhzyRTJ2kc1gEdhmxbFF-9gKTHrPH8JaCkhHz05Wxubek63aPoYD-i74SwunaWak2rOGT-vzYzTqR9KkgD_Cf3_wATP7z4F1vJGxJOdOwMD7OYTNHv9jm5SEe_5gI-s37v1YcV-fIgJD2Xj5UMSnyh4ieyPKM1QXFYPfZ1fKrEZpvoyCE526r5Gs2fhKhY4XhAOPUnoEyrwGG-YmUWAMgNY9tEPcFYgzdOgpehU15pM4_MhZQtfs439DxVbSV2ZnQ
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}        headers=&{headers}
    Should Be True  '${response.status_code}'=='200'
    Should Contain         ${response.text}        ${id}



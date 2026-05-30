*** Settings ***
Resource            ../Global_Configuration/Manage_Token.robot
#Resource            ../Global_Configuration/Staging_Variables.robot
Resource            ../Global_Configuration/${Environnement_used}_Variables.robot


*** Keywords ***
Log Result To Text File
    [Arguments]    ${story}     ${result}      ${test_name}      ${id}
    ${line}=    Catenate    Story: ${story} ------- Result: ${result} ------- Test Name: ${test_name} ------- ID:${id}\n
    Append To File    ${OUTPUT_FILE}    ${line}

Evaluate Test Step
    [Arguments]    ${main_status}    ${expected_main}    ${oper_status}    ${expected_oper}
    ${condition}=    Evaluate    '${main_status}' == '${expected_main}' and '${oper_status}' == '${expected_oper}'
    Run Keyword If    ${condition}    Return From Keyword    Pass
    Run Keyword If    NOT ${condition}      Return From Keyword    Fail
    Log    Condition failed: Main Status: ${main_status}, Expected: ${expected_main}, Operational Status: ${oper_status}, Expected: ${expected_oper}

Check Error
    [Arguments]     ${field_value}
    [Return]        ${message}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?id=${field_value}           headers=&{headers}
    #Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206
    Should Be True  '${response.status_code}'=='200'
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${message}           Set Variable        ${json_object[0].get("message")}

Check_Main_Status
    [Arguments]     ${field_value}
    [Return]        ${status}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token super Admin
    #${CPIB_TOKEN}=      Set Variable        eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI1ODg0MzksImlhdCI6MTc3MjU1NDEzMiwiYXV0aF90aW1lIjoxNzcyNTUyNDM5LCJqdGkiOiIyYWYwNDk5My03N2Y2LTRlYzUtYmI3ZC0xZGM5NmYwNTg4ZmUiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6ImI1OGRhNTRlLWMwZWQtNDNhZC05ZGIzLTA3YmQ1MzQ2MTQ2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.CtN1i82azspwyUP_jjRvKDCdHLNXN6Mt8vaSt98wfHLIzrTJDYk2e2h2e-yh5YdO9ZLi8esnOHC1xIHmmLtd1D6NGkisouo0yXiGUG3HDuNHIcRbCxMNY30az1rBBpGjjjVAtglT-d3w0qF1d3kkCrJjjQFBv8n4H6Zg5Ri0dWDa0FudjvmzuKbYTV_yTmHFyQR2e2bgsZP2spcN883QxvB9OOBDTNP-2Ax7Ce1RphRWV4YEuUBKlqALQxiltaIH_IMTOj-6U8ArSJIGlvNlob7-qmXjpUNS2kFZ-jFlyhSKrPQnkMR2cb80M_E4Cjp767O6ex8PGmt8lVIP1YRWRw

    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?id=${field_value}           headers=&{headers}
    #Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206
    Should Be True  '${response.status_code}'=='200'
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${status}           Set Variable        ${json_object[0]['status']}

Check_Operational_Status
    [Arguments]     ${field_value}
    [Return]        ${operationalStatus}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token super Admin
    #${CPIB_TOKEN}=      Set Variable        eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI1ODg0MzksImlhdCI6MTc3MjU1NDEzMiwiYXV0aF90aW1lIjoxNzcyNTUyNDM5LCJqdGkiOiIyYWYwNDk5My03N2Y2LTRlYzUtYmI3ZC0xZGM5NmYwNTg4ZmUiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6ImI1OGRhNTRlLWMwZWQtNDNhZC05ZGIzLTA3YmQ1MzQ2MTQ2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.CtN1i82azspwyUP_jjRvKDCdHLNXN6Mt8vaSt98wfHLIzrTJDYk2e2h2e-yh5YdO9ZLi8esnOHC1xIHmmLtd1D6NGkisouo0yXiGUG3HDuNHIcRbCxMNY30az1rBBpGjjjVAtglT-d3w0qF1d3kkCrJjjQFBv8n4H6Zg5Ri0dWDa0FudjvmzuKbYTV_yTmHFyQR2e2bgsZP2spcN883QxvB9OOBDTNP-2Ax7Ce1RphRWV4YEuUBKlqALQxiltaIH_IMTOj-6U8ArSJIGlvNlob7-qmXjpUNS2kFZ-jFlyhSKrPQnkMR2cb80M_E4Cjp767O6ex8PGmt8lVIP1YRWRw
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?id=${field_value}           headers=&{headers}
    #Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206
    Should Be True  '${response.status_code}'=='200'
    ${json_object}      Evaluate    json.loads('''${response.content}''')
    ${operationalStatus}           Set Variable        ${json_object[0]['operationalStatus']}

Evaluate_Error_Message
    [Arguments]    ${actual_message}    ${expected_message}
    Run Keyword If    '${actual_message}' == '${expected_message}'      Should Be Equal    ${actual_message}   ${expected_message}
    Run Keyword If    '${actual_message}' != '${expected_message}'      Fail    Messages are different. Actual: ${actual_message}, Expected: ${expected_message}

Add Date
    [Arguments]     ${PAST_PLANED_DATE}     ${MARGIN_DAYS}
    [Return]        ${new_date}
    ${formatted_date}    Convert Date    ${PAST_PLANED_DATE}    result_format=%Y-%m-%dT%H:%M:%S
    ${new_date}          Add Time To Date    ${formatted_date}    days=${MARGIN_DAYS}    result_format=%Y-%m-%dT%H:%M:%SZ


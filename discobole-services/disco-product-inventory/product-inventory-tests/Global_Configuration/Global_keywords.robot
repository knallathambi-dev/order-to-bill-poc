*** Settings ***
Resource            ../Global_Configuration/Global_Variables.robot

*** Keywords ***
Get_date
    [Documentation]     The Get_date keyword retrieves the current date and time based on the specified time zone and result format.
    ${current}=         Get Current Date    time_zone=UTC               result_format=%Y-%m-%dT%H:%M:%SZ
    RETURN            ${current}

Replace Template Values
    [Documentation]     Template is the input "string" -not a json dictionary- used as template.
    ...    Values are the dictionary of name, and value pairs to be be used to replace the templated info.
    ...    Based on replace string keyword all placeholders will be replaced using one name, value pair.
    ...    It returns the template with replaced values rendered.
    [Arguments]    ${template}
    ${MY_FILE}=      OperatingSystem.Get File         Global_Configuration/Integration_catalog_IDs.json
    ${values}=       evaluate  json.loads('''${MY_FILE}''')    json
    ${items}    Get Dictionary Keys    ${values}
    ${result}    Set Variable    ${template}
    FOR  ${key}  IN  @{items}
        Log    ${key}
        Log    ${values}[${key}]
        ${result}    Replace String    ${result}    \{{${key}\}}    ${values}[${key}]
    END
    RETURN    ${result}

Check_Event_Kafka
    [Documentation]     The Check_Event_Kafka keyword is used to verify the latest message in a Kafka topic, ensuring it contains specific attributes like ID and termination date.
    [Arguments]     ${id}       ${timestamp_termination}
    ${consumer_command}    Set Variable    kafka-console-consumer.sh --bootstrap-server ${KafkaHost}:${KafkaPort} --topic ${topic} --from-latest --max-messages 1
    ${output}    Run Process    ${consumer_command}    alias=${topic}    encoding=UTF-8    shell=True    stdout=PIPE    stderr=PIPE
    ${lines}    Split To Lines    ${output.stdout}
    ${last_message}    Set Variable    ${lines[-1]}
    Should Contain    ${last_message}       "id": "${id}"
    Should Contain    ${last_message}       "terminationDate": ${timestamp_termination}

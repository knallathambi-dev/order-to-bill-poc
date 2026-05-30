*** Settings ***
Resource        ../Keywords/export_usecases.robot

*** Test Cases ***

#Export_All_Data
#    [Tags]      Export
#
#    Get_token
#    export_json_all

Export_JSON_Data_by_Valid_relatedParty_ID
    [Tags]      Export

    Get_token
    export_json_with_filter                  relatedParty.partyOrPartyRole.id             ${Valid_relatedParty_id}

Export_JSON_Data_by_Valid_relatedParty.partyOrPartyRole.id
    [Tags]      Export
    Get_token
    export_json_with_filter                  relatedParty.partyOrPartyRole.id             J27012026

Export_JSON_Data_by_nonExistent_relatedParty_ID
    [Tags]      Export
    export_json_with_filter                  relatedParty.partyOrPartyRole.id             worng_value

Export_JSON_Data_by_Empty_relatedParty_ID
    [Tags]      Export
    export_json_with_bad_filter              relatedParty.partyOrPartyRole.id             ${EMPTY}

Export_JSON_Data_by_Created_Status
    [Tags]      Export
    Get_token
    export_json_with_filter                  status                      Created

Export_JSON_Data_by_Cancelled_Status
    [Tags]      Export
    export_json_with_filter                  status                      Cancelled

Export_JSON_Data_by_Aborted_Status
    [Tags]      Export
    export_json_with_filter                  status                      Aborted

Export_JSON_Data_by_Active_Status
    [Tags]      Export
    export_json_with_filter                  status                      Active

Export_JSON_Data_by_Terminated_Status
    [Tags]      Export
    export_json_with_filter                  status                      Terminated

Export_JSON_Data_by_Sold_Status
    [Tags]      Export
    export_json_with_filter                  status                      Sold

Export_JSON_Data_by_nonExistent_Status
    [Tags]      Export
    export_json_with_bad_filter              status                      worng_value

Export_JSON_Data_by_Empty_Status
    [Tags]      Export
    export_json_with_bad_filter              status                      ${EMPTY}

Export_JSON_Data_by_Valid_startDate.gte
    [Tags]      Export
    export_json_with_date_filter             startDate       gte         ${Valid_Date_with_Time}

Export_JSON_Data_by_Empty_startDate.gte
    [Tags]      Export
    export_json_with_bad_date_filter         startDate       gte         ${EMPTY}

Export_JSON_Data_by_Invalid_startDate.gte
    [Tags]      Export
    export_json_with_bad_date_filter         startDate       gte         worng_value

Export_JSON_Data_by_Valid_startDate.lte
    [Tags]      Export
    export_json_with_date_filter             startDate       lte         ${Valid_Date_with_Time}

Export_JSON_Data_by_Empty_startDate.lte
    [Tags]      Export
    export_json_with_bad_date_filter         startDate       lte         ${EMPTY}

Export_JSON_Data_by_Valid_creationDate.gte
    [Tags]      Export
    export_json_with__date_filter            creationDate    gte         ${Valid_Date_with_Time}

Export_JSON_Data_by_Empty_creationDate.gte
    [Tags]      Export
    export_json_with_bad_date_filter         creationDate    gte         ${EMPTY}

Export_JSON_Data_by_Valid_creationDate.lte
    [Tags]      Export
    export_json_with__date_filter            creationDate       lte             ${Valid_Date_with_Time}

Export_JSON_Data_by_Empty_creationDate.lte
    [Tags]      Export
    export_json_with_bad_date_filter         creationDate    lte         ${EMPTY}

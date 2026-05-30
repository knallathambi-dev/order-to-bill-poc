*** Settings ***
Library    RequestsLibrary
Library    Collections
Library    DateTime

Resource            ../../../Global_Configuration/Manage_Token.robot
Resource            ../../../Global_Configuration/${Environnement_used}_Variables.robot


*** Test Cases ***
Valid Report of ProductOfferingOptions
    Get_token
    get_List_Product_Offering_Options

Valid Report Retrieval By Status - Product Status (Single Day)

    Get_token
    ${response}=        get_by_Product_Status       ReportProductByStatus           collectDate=2025-02-12          Day

Valid Report Retrieval By Status for Multiple Days (Granularity: Day)
   Get_token
   ${response}=        get_by_Product_Status       ReportProductByStatus           collectDate.gte=2025-02-12           Day

Valid Report Retrieval By Status with Granularity Week
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByStatus          collectDate=2025-02-12               Week

Valid Report Retrieval By Status with Granularity Month
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByStatus           collectDate.lte=2025-02-12          Month

Valid Report Retrieval By Status with Granularity Year
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByStatus           collectDate=2025-02-12              Year

Valid Report Retrieval By Status with Product Offer ID Filter
    Get_token
    ${productOfferIds}=     Create List    67ac6817dab3e427034ba41e
    ${response}=            Get Product Report    ReportProductByStatus    2025-02-12    Day    ${productOfferIds}

Valid Report Retrieval By Status with Multiple Product Offer IDs
    Get_token
    ${productOfferIds}=     Create List    67ac6817dab3e427034ba41e    67ac67e9dab3e427034ba414
    ${response}=            Get Product Report    ReportProductByStatus         2025-02-12          Day         ${productOfferIds}

Valid Report Retrieval By Offer - Product Status (Single Day)

    Get_token
    ${response}=        get_by_Product_Status       ReportProductByOffer           collectDate=2025-02-12          Day

Valid Report Retrieval By Offer for Multiple Days (Granularity: Day)
   Get_token
   ${response}=        get_by_Product_Status       ReportProductByOffer           collectDate.gte=2025-02-12           Day

Valid Report Retrieval By Offer with Granularity Week
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByOffer          collectDate=2025-02-12               Week

Valid Report Retrieval By Offer with Granularity Month
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByOffer           collectDate.lte=2025-02-12          Month

Valid Report Retrieval By Offer with Granularity Year
    Get_token
    ${response}=        get_by_Product_Status       ReportProductByOffer           collectDate=2025-02-12              Year

Valid Report Retrieval By Offer with Product Offer ID Filter
    Get_token
    ${productOfferIds}=     Create List    67ac6817dab3e427034ba41e
    ${response}=            Get Product Report    ReportProductByOffer    2025-02-12    Day    ${productOfferIds}

Valid Report Retrieval By Offer with Multiple Product Offer IDs
    Get_token
    ${productOfferIds}=     Create List    67ac6817dab3e427034ba41e    67ac67e9dab3e427034ba414
    ${response}=            Get Product Report    ReportProductByOffer    2025-02-12    Day    ${productOfferIds}

# Invalid cases

Invalid Report Retrieval By Status with Incorrect Date Format
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByStatus    collectDate=invalid-date-format    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Granularity Value By Status
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByStatus    collectDate=2025-02-05    granularity=InvalidGranularity
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Report ID Retrieval By Status
    Get_token
    ${report_id}=    Set Variable    invalid_report_id
    ${at_type}=    Set Variable    ReportProductByStatus
    ${params}=    Create Dictionary    @type=${at_type}
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports/${report_id}    ${params}    404    Resource not found

Invalid Product Offer ID Retrieval By Status
    [Tags]      bug_IPCEISCPIB-4541
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    productOfferId=!@#$$%^    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid productOfferId

Invalid Missing Required Parameter for Report Retrieval By Status
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByStatus    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Missing @type Parameter By Status
    Get_token
    ${params}=    Create Dictionary    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Missing request parameter

Invalid Date Range: collectDate.gte > collectDate.lte By Status
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByStatus    collectDate.gte=2025-02-10    collectDate.lte=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Duplicate Product Offer ID By Status
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    productOfferId=67ac6817dab3e427034ba41e,67ac6817dab3e427034ba41e    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    200    productOfferId
    Should Not Contain    ${response.text}    productOfferId=product_1

Invalid Report Retrieval By Offer with Incorrect Date Format
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    collectDate=invalid-date-format    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Granularity Value By Offer
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    collectDate=2025-02-05    granularity=InvalidGranularity
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value


Invalid Product Offer ID Retrieval By Offer
    [Tags]      bug_IPCEISCPIB-4541
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    productOfferId=!@#$$%^    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid productOfferId

Invalid Missing Required Parameter for Report Retrieval By Offer
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Missing @type Parameter By Offer
    Get_token
    ${params}=    Create Dictionary    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Missing request parameter

Invalid Date Range: collectDate.gte > collectDate.lte By Offer
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    collectDate.gte=2025-02-10    collectDate.lte=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}    400    Invalid query-string parameter value

Invalid Duplicate Product Offer ID By Offer
    [Tags]
    Get_token
    ${params}=    Create Dictionary    @type=ReportProductByOffer    productOfferId=67ac6817dab3e427034ba41e,67ac6817dab3e427034ba41e    collectDate=2025-02-05    granularity=Day
    ${response}=    generic_api_request    /productInventoryManagement/v1/reports    ${params}      200         productOfferId




*** Keywords ***


GET Report
    [Arguments]    ${type}    ${id}
    ${headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/reports/${type}/${id}    headers=${headers}
    Should Be True    '${response.status_code}' == '200' or '${response.status_code}' == '206'
    Should Not Be Empty    ${response.text}

Invalid Report
    [Arguments]    ${type}    ${id}
    ${headers}=    Create Dictionary    Content-Type=application/json    Accept=*/*    Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/reports/${type}/${id}    headers=${headers}
    Should Be True    '${response.status_code}' == '404'
    Should Not Be Empty    ${response.text}

get_List_Product_Offering_Options

    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate    get-with-op-and-date-    ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br           Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session      /productInventoryManagement/v1/reports/productOfferingOptions        headers=&{headers}
    Should Be True    '${response.status_code}' == '200' or '${response.status_code}' == '206'
    Should Not Be Empty    ${response.text}


get_by_Product_Status

    [Arguments]     ${Type}    ${date_input}    ${granularity}

    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate    get-with-op-and-date-    ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br           Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/reports?@type=${Type}&${date_input}&granularity=${granularity}        headers=&{headers}
    Should Be True    '${response.status_code}' == '200' or '${response.status_code}' == '206'
    Should Not Be Empty    ${response.text}

Get Product Report
    [Arguments]    ${Type}    ${date_input}    ${granularity}    ${productOfferIds}

    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate    get-with-op-and-date-    ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary
    ...    Content-Type=application/json
    ...    Accept=*/*
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive
    Create Session    session    ${end-point-cpib}

    # Joindre les productOfferIds séparés par une virgule
    ${productOfferIds_str}=    Catenate    SEPARATOR=,    @{productOfferIds}

    &{query_params}=    Create Dictionary
    ...    @type=${Type}
    ...    collectDate=${date_input}
    ...    granularity=${granularity}
    ...    productOfferId=${productOfferIds_str}

    ${response}=    GET Request    session    /productInventoryManagement/v1/reports    params=&{query_params}    headers=&{headers}
    Should Not Be Empty    ${response.text}

generic_api_request
    [Arguments]    ${endpoint}    ${params}    ${expected_status_code}    ${expected_message}
    ${uuid}=    Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate    get-with-op-and-date-    ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*      Authorization=Bearer ${CPIB_TOKEN}          Accept-Encoding=gzip, deflate, br           Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  ${endpoint}    params=&{params}    headers=&{headers}
    Should Be Equal As Numbers    ${response.status_code}    ${expected_status_code}
#    Should Contain    ${response.text}    ${expected_message}
    RETURN    ${response}

Get Collect Dates From Response
    [Arguments]    ${response}
    ${dates}=    Create List
    FOR    ${item}   IN    @{response['collectDate']}
        ${collect_date}=    Get From Dictionary    ${item}    collectDate
        Append To List    ${dates}    ${collect_date}
    END
    RETURN    ${dates}

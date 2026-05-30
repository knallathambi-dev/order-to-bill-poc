*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***


Get_Product_with_empty_Status
    [Tags]  Get_Method
    Get_token
    get_with_bad_status             ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        	status must not be empty

Get_Product_with_nullable_Status
    [Tags]  Get_Method
    Get_token
    get_with_bad_status             ${NULL}         ${code_28}       ${invalid_paramter_reason}        status value is not a valid type

Get_Product_with_invalid_Status
    [Tags]  Get_Method
    Get_token
    get_with_bad_status             worng_status    ${code_28}       ${invalid_paramter_reason}        status value is not a valid type

Get_Product_with_Created_Status
    [Tags]  Get_Method
    Get_token
    get_with_exist_status           Created

Get_Product_with_Cancelled_Status
    [Tags]  Get_Method
    Get_token
    get_with_status                 Cancelled

Get_Product_with_Aborted_Status
    [Tags]  Get_Method
    Get_token
    get_with_status                 Aborted

Get_Product_with_Active_Status
    [Tags]  Get_Method
    Get_token
    get_with_exist_status           Active

Get_Product_with_Terminated_Status
    [Tags]  Get_Method
    Get_token
    get_with_status                Terminated

Get_Product_with_Sold_Status
    [Tags]  Get_Method
    Get_token
    get_with_status                Sold

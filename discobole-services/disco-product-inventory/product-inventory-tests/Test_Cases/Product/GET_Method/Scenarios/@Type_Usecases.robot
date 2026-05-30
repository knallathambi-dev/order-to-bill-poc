*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_@Type
    [Tags]  Get_Method
    Get_token
    get_valid_with_@Type          Product

Get_Product_By_valid_@Type
    [Tags]  Get_Method
    Get_token
    get_valid_with_@Type          PhysicalProduct

Get_Product_By_nonExistent_@Type
    [Tags]  Get_Method
    Get_token
    get_bad_@Type                 worng_@type

Get_Product_By_empty_@Type
    [Tags]  Get_Method
    Get_token
    get_empty_@Type               ${EMPTY}          ${code_28}       ${invalid_paramter_reason}        @type must not be empty

*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_True_isRoot
    [Tags]  Get_Method
    Get_token
    get_valid_with_isRoot_equal_true           true

Get_Product_By_False_isRoot
    [Tags]  Get_Method
    Get_token
    get_valid_with_isRoot_equal_false           false

Get_Product_By_empty_isRoot
    [Tags]  Get_Method
    Get_token
    get_with_bad_isRoot             ${EMPTY}


Get_Product_By_nullable_isRoot
    [Tags]  Get_Method
    Get_token
    get_with_bad_isRoot             ${NULL}

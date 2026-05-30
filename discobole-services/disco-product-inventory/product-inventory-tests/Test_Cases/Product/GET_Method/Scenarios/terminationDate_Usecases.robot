*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2024-03-01T17:42:09Z

*** Test Cases ***


GreaterEqual_with_Valid_terminationDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     terminationDate       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Empty_terminationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 terminationDate       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_terminationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 terminationDate       gte       >=       ${NULL}

LessEqual_with_Valid_terminationDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     terminationDate       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Empty_terminationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 terminationDate       lte       <=      ${EMPTY}

LessEqual_with_Nullable_terminationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 terminationDate       lte       <=      ${NULL}

*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2024-03-01T17:42:09Z

*** Test Cases ***

GreaterEqual_with_Valid_creationDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     creationDate       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Empty_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 creationDate       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 creationDate       gte       >=       ${NULL}

LessEqual_with_Valid_creationDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     creationDate       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Empty_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 creationDate       lte       <=      ${EMPTY}

LessEqual_with_Nullable_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 creationDate       lte       <=      ${NULL}

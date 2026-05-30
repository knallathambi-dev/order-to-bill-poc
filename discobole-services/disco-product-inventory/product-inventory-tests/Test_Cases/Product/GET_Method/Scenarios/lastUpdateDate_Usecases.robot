*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2025-11-03T09:02:47Z
${Valid_Date_without_Time}                      2025-11-03

*** Test Cases ***

# Greater Usecases


GreaterEqual_with_Valid_lastUpdateDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     lastUpdateDate       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Valid_lastUpdateDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       gte       >=      ${Valid_Date_without_Time}

GreaterEqual_with_Empty_lastUpdateDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_lastUpdateDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       gte       >=       ${NULL}

# Less Usecases

LessEqual_with_Valid_lastUpdateDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     lastUpdateDate       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Valid_lastUpdateDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       lte       <=      ${Valid_Date_without_Time}

LessEqual_with_Empty_lastUpdateDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       lte       <=      ${EMPTY}

LessEqual_with_Nullable_lastUpdateDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 lastUpdateDate       lte       <=      ${NULL}

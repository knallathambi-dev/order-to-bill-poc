*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2025-11-03T09:02:47Z
${Valid_Date_without_Time}                      2025-11-03

*** Test Cases ***


# Greater Usecases

GreaterEqual_with_Valid_orderDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     orderDate       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Valid_orderDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       gte       >=      ${Valid_Date_without_Time}

GreaterEqual_with_Empty_orderDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_orderDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       gte       >=       ${NULL}

# Less Usecases

LessEqual_with_Valid_orderDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     orderDate       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Valid_orderDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       lte       <=      ${Valid_Date_without_Time}

LessEqual_with_Empty_orderDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       lte       <=      ${EMPTY}

LessEqual_with_Nullable_orderDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 orderDate       lte       <=      ${NULL}

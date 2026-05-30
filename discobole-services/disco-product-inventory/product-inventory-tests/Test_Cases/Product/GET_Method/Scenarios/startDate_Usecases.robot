*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2023-11-03T09:02:47Z
${Valid_Date_without_Time}                      2023-11-03
${Invalid_Date_without_TimeZone_offset}         2023-11-03T09:02:47
${Invalid_Date_Format_with_/}                   2023/11/03T09:02:47Z
${Invalid_Date_Format_with_/_Reversed}          03/11/2023T09:02:47Z
${Invalid_Date_Format_with_Reversed}            03-11-2023T09:02:47Z
${Min_Date_with_Time}                           2023-11-03T09:02:47Z
${Max_Date_with_Time}                           2023-11-20T09:02:47Z
${Min_Date_without_Time}                        2023-11-03
${Max_Date_without_Time}                        2023-11-15

*** Test Cases ***

Get_with_Existent_Valid_startDate_with_time
    [Tags]  Get_Method
    Get_token
    get_with_date          startDate       ${Valid_Date_with_Time}

Get_with_Existent_Valid_startDate_without_time
    [Tags]  Get_Method
    Get_token
    get_with_bad_date           startDate       ${Valid_Date_without_Time}

Get_with_No_Existent_Valid_startDate
    [Tags]  Get_Method
    Get_token
    ${Date_Now}             Get_date
    get_with_date           startDate       ${Date_Now}

Get_with_nullable_startDate
    [Tags]  Get_Method
    Get_token
    get_with_bad_date       startDate       ${NULL}

Get_with_empty_startDate
    [Tags]  Get_Method
    Get_token
    get_with_bad_date       startDate       ${EMPTY}

# Invalid Format
Get_with_Invalid_startDate_Without_Timestamp
    [Tags]  Get_Method
    Get_token
    get_with_bad_date       startDate       ${Valid_Date_without_Time}

Get_with_Invalid_startDate_Without_TimeZone_offset
    [Tags]  Get_Method
    Get_token
    get_with_bad_date       startDate       ${Invalid_Date_without_TimeZone_offset}

Get_with_Invalid_startDate_With_Different_Format
    [Tags]  Get_Method
    Get_token
    get_with_bad_date       startDate       ${Invalid_Date_Format_with_Reversed}
    get_with_bad_date       startDate       ${Invalid_Date_Format_with_/}
    get_with_bad_date       startDate       ${Invalid_Date_Format_with_/_Reversed}

# ANDED Usecases

Get_ANDED_with_Tow_valid_startDate_with_time
    [Tags]  Get_Method
    Get_token
    get_anded_with_2_dates        startDate           ${Min_Date_with_Time}                 ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ

Get_ANDED_with_Tow_valid_startDate_without_time
    [Tags]  Get_Method
    Get_token
    Invalid_get_anded_with_2_dates         startDate           ${Min_Date_without_Time}              ${Max_Date_without_Time}        %Y-%m-%d

Get_ANDED_with_Min_startDate_grater_than_Max_startDate
    [Tags]  Get_Method
    Get_token
    get_anded_with_2_dates        startDate           ${Max_Date_with_Time}                 ${Min_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ


Get_ANDED_with_One_Valid_startDate_&_One_Invalid_startDate
    [Tags]  Get_Method
    Get_token
    bad_get_anded_with_2_dates      startDate       ${Min_Date_with_Time}                 worng_date                      %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      startDate       worng_date                            ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_startDate_&_One_Empty_startDate
    [Tags]  Get_Method
    Get_token
    bad_get_anded_with_2_dates      startDate       ${Min_Date_with_Time}                 ${EMPTY}                        %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      startDate       ${EMPTY}                              ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_startDate_&_One_Nullable_startDate
    [Tags]  Get_Method
    Get_token
    bad_get_anded_with_2_dates      startDate       ${Min_Date_with_Time}                 ${NULL}                         %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      startDate       ${NULL}                               ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

# Operations Usecases

GreaterEqual_with_Valid_startDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     startDate       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Valid_startDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       gte       >=      ${Valid_Date_without_Time}

GreaterEqual_with_Empty_startDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_startDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       gte       >=       ${NULL}

LessEqual_with_Valid_startDate_with_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_date                     startDate       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Valid_startDate_without_Time
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       lte       <=      ${Valid_Date_without_Time}

LessEqual_with_Empty_startDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       lte       <=      ${EMPTY}

LessEqual_with_Nullable_startDate
    [Tags]  Get_Method
    Get_token
    get_with_op_bad_date                 startDate       lte       <=      ${NULL}

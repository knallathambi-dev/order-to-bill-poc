*** Settings ***
Resource        ../Keywords/get_Job_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2025-01-29T10:41:21Z
${Valid_Date_without_Time}                      29-08-2025
${Invalid_Date_without_TimeZone_offset}         2025-01-29T10:41:21
${Invalid_Date_Format_with_/}                   2025/01/29T10:41:21Z
${Invalid_Date_Format_with_/_Reversed}          25/01/2025T10:41:21Z
${Invalid_Date_Format_with_Reversed}            25-01-2025T10:41:21Z
${Min_Date_with_Time}                           2025-01-29T10:41:21Z
${Max_Date_with_Time}                           2025-01-31T10:41:21Z
${Min_Date_without_Time}                        2025-01-29
${Max_Date_without_Time}                        2025-01-31

*** Test Cases ***


Get_with_Existent_Valid_endDate_with_time
    Get_token
    get_with_date           executionPeriod.endDateTime       ${Valid_Date_with_Time}

Get_with_No_Existent_Valid_endDate
    ${Date_Now}             Get_date
    get_with_date           executionPeriod.endDateTime       ${Date_Now}

Get_with_nullable_endDate
    get_with_bad_date       executionPeriod.endDateTime       ${NULL}

Get_with_empty_endDate
    get_with_bad_date       executionPeriod.endDateTime       ${EMPTY}

# Invalid Format
Get_with_Invalid_endDate_Without_Timestamp
    get_with_bad_date       executionPeriod.endDateTime       ${Valid_Date_without_Time}

Get_with_Invalid_endDate_Without_TimeZone_offset
    get_with_bad_date       executionPeriod.endDateTime       ${Invalid_Date_without_TimeZone_offset}

Get_with_Invalid_endDate_With_Different_Format
    get_with_bad_date       executionPeriod.endDateTime       ${Invalid_Date_Format_with_Reversed}
    get_with_bad_date       executionPeriod.endDateTime       ${Invalid_Date_Format_with_/}
    get_with_bad_date       executionPeriod.endDateTime       ${Invalid_Date_Format_with_/_Reversed}

# ANDED Usecases

Get_ANDED_with_Tow_valid_endDate_with_time
    get_anded_with_2_dates        executionPeriod.endDateTime           ${Min_Date_with_Time}                 ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ

Get_ANDED_with_Min_endDate_grater_than_Max_endDate
    get_anded_with_2_dates        executionPeriod.endDateTime           ${Max_Date_with_Time}                 ${Min_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ


Get_ANDED_with_One_Valid_endDate_&_One_Invalid_endDate
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       ${Min_Date_with_Time}                 worng_date                      %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       worng_date                            ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_endDate_&_One_Empty_endDate
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       ${Min_Date_with_Time}                 ${EMPTY}                        %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       ${EMPTY}                              ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_endDate_&_One_Nullable_endDate
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       ${Min_Date_with_Time}                 ${NULL}                         %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      executionPeriod.endDateTime       ${NULL}                               ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

# Operations Usecases

GreaterEqual_with_Valid_endDate_with_Time
    Get_token
    get_with_op_date                     executionPeriod.endDateTime       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Valid_endDate_without_Time
    get_with_op_bad_date                 executionPeriod.endDateTime       gte       >=      ${Valid_Date_without_Time}

GreaterEqual_with_Empty_endDate
    get_with_op_bad_date                 executionPeriod.endDateTime       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_endDate
    get_with_op_bad_date                 executionPeriod.endDateTime       gte       >=       ${NULL}

LessEqual_with_Valid_endDate_with_Time
    Get_token
    get_with_op_date                     executionPeriod.endDateTime       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Valid_endDate_without_Time
    get_with_op_bad_date                 executionPeriod.endDateTime       lte       <=      ${Valid_Date_without_Time}

LessEqual_with_Empty_endDate
    get_with_op_bad_date                 executionPeriod.endDateTime       lte       <=      ${EMPTY}

LessEqual_with_Nullable_endDate
    get_with_op_bad_date                 executionPeriod.endDateTime       lte       <=      ${NULL}

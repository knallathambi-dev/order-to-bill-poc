*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot

*** Variables ***

${Valid_Date_with_Time}                         2025-02-20T10:41:21Z
${Valid_Date_without_Time}                      20-02-2025
${Invalid_Date_without_TimeZone_offset}         2025-02-20T10:41:21
${Invalid_Date_Format_with_/}                   2025/02/20T10:41:21Z
${Invalid_Date_Format_with_/_Reversed}          20/02/2025T10:41:21Z
${Invalid_Date_Format_with_Reversed}            20-02-2025T10:41:21Z
${Min_Date_with_Time}                           2025-02-20T10:41:21Z
${Max_Date_with_Time}                           2025-02-23T10:41:21Z
${Min_Date_without_Time}                        2025-02-20
${Max_Date_without_Time}                        2025-02-23

*** Test Cases ***

Get_with_Existent_Valid_startDate_with_time
    Get_token
    get_with_date           activePeriod.startDateTime       ${Valid_Date_with_Time}



Get_with_No_Existent_Valid_startDate
    Get_token
    ${Date_Now}             Get_date
    get_with_date           activePeriod.startDateTime       ${Date_Now}

Get_with_nullable_startDate
    Get_token
    get_with_bad_date       activePeriod.startDateTime       ${NULL}

Get_with_empty_startDate
    Get_token
    get_with_bad_date       activePeriod.startDateTime       ${EMPTY}

# Invalid Format
Get_with_Invalid_startDate_Without_Timestamp
    Get_token
    get_with_bad_date       activePeriod.startDateTime       ${Valid_Date_without_Time}

Get_with_Invalid_startDate_Without_TimeZone_offset
    Get_token
    get_with_bad_date       activePeriod.startDateTime       ${Invalid_Date_without_TimeZone_offset}

Get_with_Invalid_startDate_With_Different_Format
    Get_token
    get_with_bad_date       activePeriod.startDateTime       ${Invalid_Date_Format_with_Reversed}
    get_with_bad_date       activePeriod.startDateTime       ${Invalid_Date_Format_with_/}
    get_with_bad_date       activePeriod.startDateTime       ${Invalid_Date_Format_with_/_Reversed}

# ANDED Usecases

Get_ANDED_with_Tow_valid_startDate_with_time
    Get_token
    get_anded_with_2_dates        activePeriod.startDateTime           ${Min_Date_with_Time}                 ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ

Get_ANDED_with_One_Valid_startDate_&_One_Invalid_startDate
    Get_token
    bad_get_anded_with_2_dates      activePeriod.startDateTime       ${Min_Date_with_Time}                 worng_date                      %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      activePeriod.startDateTime       worng_date                            ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_startDate_&_One_Empty_startDate
    Get_token
    bad_get_anded_with_2_dates      activePeriod.startDateTime       ${Min_Date_with_Time}                 ${EMPTY}                        %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      activePeriod.startDateTime       ${EMPTY}                              ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

Get_ANDED_with_One_Valid_startDate_&_One_Nullable_startDate
    Get_token
    bad_get_anded_with_2_dates      activePeriod.startDateTime       ${Min_Date_with_Time}                 ${NULL}                         %Y-%m-%dT%H:%M:%SZ          lte
    bad_get_anded_with_2_dates      activePeriod.startDateTime       ${NULL}                               ${Max_Date_with_Time}           %Y-%m-%dT%H:%M:%SZ          gte

# Operations Usecases

GreaterEqual_with_Valid_startDate_with_Time
    Get_token
    get_with_op_date                     activePeriod.startDateTime       gte       >=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

GreaterEqual_with_Valid_startDate_without_Time
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       gte       >=      ${Valid_Date_without_Time}

GreaterEqual_with_Empty_startDate
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       gte       >=       ${EMPTY}

GreaterEqual_with_Nullable_startDate
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       gte       >=       ${NULL}

LessEqual_with_Valid_startDate_with_Time
    Get_token
    get_with_op_date                     activePeriod.startDateTime       lte       <=      ${Valid_Date_with_Time}              %Y-%m-%dT%H:%M:%SZ

LessEqual_with_Valid_startDate_without_Time
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       lte       <=      ${Valid_Date_without_Time}

LessEqual_with_Empty_startDate
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       lte       <=      ${EMPTY}

LessEqual_with_Nullable_startDate
    Get_token
    get_with_op_bad_date                 activePeriod.startDateTime       lte       <=      ${NULL}

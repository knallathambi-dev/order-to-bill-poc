*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot

*** Test Cases ***

Get_Tasks_with_valid_offset
    Get_token
    get_with_offset             2

Get_Tasks_with_invalid_offset
    get_with_bad_parameter      offset=-3          ${code_28}      ${invalid_paramter_reason}       Offset should not be negative

Get_Tasks_with_empty_offset
    get_with_bad_parameter      offset=${EMPTY}    ${code_28}      ${invalid_paramter_reason}       offset must not be empty

Get_Tasks_with_nullable_offset
    get_with_bad_parameter      offset=${NULL}     ${code_28}      ${invalid_paramter_reason}       offset value is not a valid type

Get_Tasks_with_offset_grater_than_total
    get_with_bad_parameter      offset=1000000000     ${code_28}      ${invalid_paramter_reason}    The offset provided exceeds the total number of available items.

Get_Tasks_with_valid_limit
    get_with_limit              2

Get_Tasks_with_invalid_limit
    get_with_bad_parameter      limit=-3          ${code_28}      ${invalid_paramter_reason}       	Limit should not be negative

Get_Tasks_with_empty_limit
    get_with_bad_parameter       limit=${EMPTY}      ${code_28}      ${invalid_paramter_reason}       limit must not be empty

Get_Tasks_with_nullable_limit
    get_with_bad_parameter       limit=${NULL}      ${code_28}      ${invalid_paramter_reason}       limit value is not a valid type

Get_Tasks_with_valid_offset_&_valid_limit
    get_with_offset_&_limit             2                3

Get_Tasks_with_valid_offset_&_invalid_limit
    get_with_bad_offset_or_limit        2               -6                                          Limit should not be negative

Get_Tasks_with_invalid_offset_&_valid_limit
    get_with_bad_offset_or_limit       -3                2                                           	Offset should not be negative

Get_Tasks_with_invalid_offset_&_invalid_limit
    get_with_bad_offset_or_limit       -2               -3                                           	Offset and limit should not be negative

Get_Tasks_with_valid_offset_&_Max_limit
    get_with_bad_offset_or_limit        2               10001                                       Limit should not exceed max limit

Get_Tasks_with_Max_offset_&_valid_limit
    get_with_bad_offset_or_limit        10001               3                                       The offset provided exceeds the total number of available items.

Get_Tasks_with_valid_offset_&_empty_limit
    get_with_bad_offset_or_limit        2            ${EMPTY}                                        limit must not be empty

Get_Tasks_with_empty_offset_&_valid_limit
    get_with_bad_offset_or_limit     ${EMPTY}            2                                           offset must not be empty

Get_Tasks_with_empty_offset_&_empty_limit
    get_with_bad_offset_or_limit     ${EMPTY}        ${EMPTY}                                        offset must not be empty

Get_Tasks_with_valid_offset_&_nullable_limit
    get_with_bad_offset_or_limit     2               ${NULL}                                         limit value is not a valid type

Get_Tasks_with_nullable_offset_&_valid_limit
    get_with_bad_offset_or_limit    ${NULL}          6                                               offset value is not a valid type

Get_Tasks_with_nullable_offset_&_nullable_limit
    get_with_bad_offset_or_limit    ${NULL}         ${NULL}                                          offset value is not a valid type

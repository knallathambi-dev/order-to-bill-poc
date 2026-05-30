*** Settings ***
Resource        ../Keywords/get_Job_usecases.robot

*** Test Cases ***


Get_Job_with_empty_Status
    Get_token
    get_with_bad_status             ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        	status must not be empty

Get_Job_with_nullable_Status
    get_with_bad_status             ${NULL}         ${code_28}       ${invalid_paramter_reason}        status value is not a valid type

Get_Job_with_invalid_Status
    get_with_bad_status             worng_status    ${code_28}       ${invalid_paramter_reason}        status value is not a valid type

Get_Job_with_NotStarted_Status
    get_with_exist_status           NotStarted

Get_Job_with_Running_Status
    get_with_exist_status           Running

Get_Job_with_Succeeded_Status
    get_with_exist_status           Succeeded

Get_Job_with_Failed_Status
    get_with_exist_status           Failed

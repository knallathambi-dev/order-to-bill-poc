*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot

*** Test Cases ***

Get_JobSpecification_with_empty_LifeCycleStatus
    Get_token
    get_with_bad_LifeCycleStatus            ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        	Unsupported filter parameter: status

Get_JobSpecification_with_nullable_LifeCycleStatus
    get_with_bad_LifeCycleStatus             ${NULL}         ${code_28}       ${invalid_paramter_reason}        Unsupported filter parameter: status

Get_JobSpecification_with_invalid_LifeCycleStatus
    get_with_bad_LifeCycleStatus             worng_status    ${code_28}       ${invalid_paramter_reason}        Unsupported filter parameter: status

Get_JobSpecification_with_Created_LifeCycleStatus
    get_with_exist_LifeCycleStatus                  Created

Get_JobSpecification_with_Active_LifeCycleStatus
    get_with_exist_LifeCycleStatus                  Active

Get_JobSpecification_with_Terminated_LifeCycleStatus
    get_with_exist_LifeCycleStatus                  Terminated

# check if the status is Suspended or Suspend
#Get_JobSpecification_with_Suspended_LifeCycleStatus
#    get_with_exist_LifeCycleStatus                  Suspend

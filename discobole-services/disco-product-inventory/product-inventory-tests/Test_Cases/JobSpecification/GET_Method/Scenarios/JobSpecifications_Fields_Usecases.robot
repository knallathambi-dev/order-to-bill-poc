*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot

*** Test Cases ***

Get_JobSpecifications_with_empty_Field
    Get_token
    get_with_bad_field            ${EMPTY}      ${code_28}      ${invalid_paramter_reason}       fields must not be empty

Get_JobSpecifications_with_nonExistent_Field
    Get_token
    get_with_bad_field            worng_field   ${code_28}          ${invalid_paramter_reason}    worng_field not included in product fields

Get_JobSpecifications_without_Field
    Get_token
    get_without_fields

Get_JobSpecifications_with_Id_Field
    Get_token
    get_with_specific_field       id

Get_JobSpecifications_with_Name_Field
    Get_token
    get_with_specific_field       name

Get_JobSpecifications_with_@Type_Field
    Get_token
    get_with_specific_field       @type

Get_JobSpecifications_with_Status_Field
    Get_token
    get_with_specific_field       lifecycleStatus

Get_JobSpecifications_with_startDateTime_Field
    Get_token
    get_with_activePeriod           activePeriod.startDateTime.gte=2024-11-22T18:18:14Z

Get_JobSpecifications_with_endDateTime_Field
    Get_token
    get_with_activePeriod           activePeriod.endDateTime.gte=2024-11-22T18:18:14Z

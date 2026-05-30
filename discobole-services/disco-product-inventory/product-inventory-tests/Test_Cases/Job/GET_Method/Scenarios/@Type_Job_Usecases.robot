*** Settings ***
Resource        ../Keywords/get_Job_usecases.robot

*** Test Cases ***

Filtering_Job_by_TerminationJobSpecification_@type
    Get_token
    get_valid_field_Job             @type          TerminationJob

Filtering_Job_by_ExportJobSpecification_@type
    Get_token
    get_valid_field_Job            @type          ExportJob

Filtering_Job_by_ExportJobSpecification_@type
    Get_token
    get_valid_field_Job             @type          PurgeJob

Filtering_Job_by_nonExistent_@type
    Get_token
    get_bad_field_Job               @type          worng_@type

Filtering_Job_by_empty_@type
    Get_token
    get_empty_field_Job             @type          ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        @type must not be empty



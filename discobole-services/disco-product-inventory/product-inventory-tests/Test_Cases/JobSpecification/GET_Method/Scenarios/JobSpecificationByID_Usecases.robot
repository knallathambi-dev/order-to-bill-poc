*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot
Resource        ../Keywords/post_JobSpecifications_usecases.robot
Resource        ../../../../Resources/Common_Keywords.robot

*** Test Cases ***
# Not yet implemented
Get_TerminationJobSpecification_JobSpecification_By_valid_id
    Get_token
    ${Valid_TerminationJobSpecification_JobSpecification_id}=   Post_JobSpecification_with_QueryStatus          TerminationJobSpecification         Terminated             Post_TerminationJobSpecification_JobSpecification_for_Products_body_request
    valid_get_jobSpec            ${Valid_TerminationJobSpecification_JobSpecification_id}

Get_ExportJobSpecification_JobSpecification_By_valid_id
     Get_token
     ${Valid_ExportJobSpecification_JobSpecification_id}=   Post_JobSpecification_with_QueryStatus         ExportJobSpecification              Created             Post_ExportJobSpecification_JobSpecification_body_request
     valid_get_jobSpec             ${Valid_ExportJobSpecification_JobSpecification_id}

Get_PurgeJobSpecification_JobSpecification_By_valid_id
    [Tags]    authorization_problem
    Get_token
    ${Valid_PurgeJobSpecification_JobSpecification_id}=         Post_JobSpecification_with_QueryStatus          PurgeJobSpecification          Terminated          Post_PurgeJobSpecification_JobSpecification_for_Products_body_request
    valid_get_jobSpec             ${Valid_PurgeJobSpecification_JobSpecification_id}

Get_JobSpecification_By_nonExistent_id
    Get_token
    invalid_get         worng_id

Get_JobSpecification_By_empty_id
    Get_token
    empty_get            ${EMPTY}

Get_One_JobSpecification_with_empty_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_bad_field      ${Valid_ExportJobSpecification_JobSpecification_id}         ${EMPTY}        fields must not be empty

Get_One_JobSpecification_with_nonExistent_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_field          ${Valid_ExportJobSpecification_JobSpecification_id}         worng_field

Get_One_JobSpecification_with_Id_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_field          ${Valid_ExportJobSpecification_JobSpecification_id}       id

Get_One_JobSpecification_with_Name_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_field          ${Valid_ExportJobSpecification_JobSpecification_id}       name

Get_One_JobSpecification_with_@Type_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_field          ${Valid_ExportJobSpecification_JobSpecification_id}       @type

Get_One_JobSpecification_with_Status_Field
    Get_token
    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created     Post_ExportJobSpecification_JobSpecification_body_request
    get_one_with_field          ${Valid_ExportJobSpecification_JobSpecification_id}       lifecycleStatus

# For reccurent case
Get_One_JobSpecification_with_startDate_Field
    Get_token
    ${Valid_PurgeJobSpecification_JobSpecification_id}=         Post_JobSpecification_with_QueryStatus          ExportJobSpecification          Terminated          Post_ExportJobSpecification_JobSpecification_ImmediateJobScheduler_body_request
    log        ${Valid_PurgeJobSpecification_JobSpecification_id}
    sleep      1min
    get_one_with_field          ${Valid_PurgeJobSpecification_JobSpecification_id}              activePeriod.startDateTime

Get_One_JobSpecification_with_endDate_Field
    Get_token
    ${Valid_PurgeJobSpecification_JobSpecification_id}=         Post_JobSpecification_with_QueryStatus          ExportJobSpecification          Terminated          Post_ExportJobSpecification_JobSpecification_ImmediateJobScheduler_body_request
    log        ${Valid_PurgeJobSpecification_JobSpecification_id}
    sleep      1min
    get_one_with_field          ${Valid_PurgeJobSpecification_JobSpecification_id}             activePeriod.endDateTime







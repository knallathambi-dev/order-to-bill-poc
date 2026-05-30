*** Settings ***
Resource        ../Keywords/get_Job_usecases.robot
Resource       ../../../JobSpecification/POST_Method/Keywords/post_JobSpecifications_usecases.robot
*** Test Cases ***

Valid_DownloadLink_Job_ExportJobSpecification
    Get_token
#    ${Valid_ExportJobSpecification_JobSpecification_id}=        Post_JobSpecification_with_QueryStatus      ExportJobSpecification          Created         Post_ExportJobSpecification_JobSpecification_ImmediateJobScheduler_body_request
#    ${Valid_ExportJobSpecification_Job_id}=        valid_get_with_jobSpecification_id       ${Valid_ExportJobSpecification_JobSpecification_id}
#    Sleep    10s
    ${Valid_ExportJobSpecification_Job_id}=         Set Variable        6745a65b15365d26ce994f17
    valid_download_link_job            ${Valid_ExportJobSpecification_Job_id}


Invalid_Get_Download_Link_Job_By_nonExistent_id
    Get_token
    invalid_download_link          worng_id          NOT_FOUND           ${code_60}          ${notfound_reason}              The Job with id worng_id does not exist



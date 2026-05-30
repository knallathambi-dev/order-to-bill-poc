*** Settings ***
Resource        ../Keywords/get_Job_usecases.robot

*** Test Cases ***

Valid_Default_Sorting_By_startDate
    Get_token
    get_with_sorting        ${EMPTY}            executionPeriod.startDateTime

Valid_ASC_Sorting_By_startDate
    Get_token
    get_with_sorting        +                   executionPeriod.startDateTime

Valid_DSC_Sorting_By_startDate
    Get_token
    get_with_sorting        -                   executionPeriod.startDateTime

Valid_Default_Sorting_By_endDate
    Get_token
    get_with_sorting        ${EMPTY}            executionPeriod.endDateTime

Valid_ASC_Sorting_By_endDate
    Get_token
    get_with_sorting        +                   executionPeriod.endDateTime

Valid_DSC_Sorting_By_endDate
    Get_token
    get_with_sorting        -                   executionPeriod.endDateTime

Invalid_Duplicated_Sorting_By_endDate
    Get_token
    get_duplicated_sorting                      executionPeriod.endDateTime

Valid_Default_Sorting_By_status
    Get_token
    get_with_sorting        ${EMPTY}            status

Valid_ASC_Sorting_By_status
    Get_token
    get_with_sorting        +                   status

Valid_DSC_Sorting_By_status
    Get_token
    get_with_sorting        -                   status

Invalid_Sorting_By_Worng_Field
    Get_token
    get_invalid_sorting     worng_field         sort value is not a valid type

Invalid_Sorting_By_Empty_Field
    Get_token
    get_invalid_sorting     ${EMPTY}            sort must not be empty

Invalid_Sorting_By_nonExistent_Field
    Get_token
    get_invalid_sorting     worng_sort          sort value is not a valid type


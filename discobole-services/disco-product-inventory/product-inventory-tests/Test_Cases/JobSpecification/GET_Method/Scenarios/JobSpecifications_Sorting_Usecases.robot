*** Settings ***
Resource        ../Keywords/get_JobSpecifications_usecases.robot

*** Test Cases ***

Valid_Default_Sorting_By_startDate
    Get_token
    get_with_sorting        ${EMPTY}            activePeriod.startDateTime

Valid_ASC_Sorting_By_startDate
    get_with_sorting        +                   activePeriod.startDateTime

Valid_DSC_Sorting_By_startDate
    get_with_sorting        -                   activePeriod.startDateTime

Valid_Default_Sorting_By_endDate
    get_with_sorting        ${EMPTY}            activePeriod.endDateTime

Valid_ASC_Sorting_By_endDate
    get_with_sorting        +                   activePeriod.endDateTime

Valid_DSC_Sorting_By_endDate
    get_with_sorting        -                   activePeriod.endDateTime

Valid_Default_Sorting_By_creationDate
    get_with_sorting        ${EMPTY}            creationDate

Valid_ASC_Sorting_By_creationDate
    get_with_sorting        +                   creationDate

Valid_DSC_Sorting_By_creationDate
    get_with_sorting        -                   creationDate
#updte with id purge job
#Invalid_Duplicated_Sorting_By_endDate
#    get_duplicated_sorting                      activePeriod.endDateTime

Invalid_Sorting_By_Worng_Field
    get_invalid_sorting     worng_field         sort value is not a valid type

Invalid_Sorting_By_Empty_Field
    get_invalid_sorting     ${EMPTY}            sort must not be empty

Invalid_Sorting_By_nonExistent_Field
    get_invalid_sorting     worng_sort          sort value is not a valid type


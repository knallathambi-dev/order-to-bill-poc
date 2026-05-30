*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Valid_Default_Sorting_By_startDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        ${EMPTY}            startDate

Valid_ASC_Sorting_By_startDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        +                   startDate

Valid_DSC_Sorting_By_startDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        -                   startDate

Valid_Default_Sorting_By_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        ${EMPTY}            creationDate

Valid_ASC_Sorting_By_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        +                   creationDate

Valid_DSC_Sorting_By_creationDate
    [Tags]  Get_Method
    Get_token
    get_with_sorting        -                   creationDate

Invalid_Duplicated_Sorting_By_startDate
    [Tags]  Get_Method
    Get_token
    get_duplicated_sorting                      startDate

Valid_Default_Sorting_By_productRelationshipType
    [Tags]  Get_Method
    Get_token
    get_with_sorting        ${EMPTY}            productRelationship.relationshipType

Valid_ASC_Sorting_By_productRelationshipType
    [Tags]  Get_Method
    Get_token
    get_with_sorting        +                   productRelationship.relationshipType

Valid_DSC_Sorting_By_productRelationshipType
    [Tags]  Get_Method
    Get_token
    get_with_sorting        -                   productRelationship.relationshipType

Invalid_Duplicated_Sorting_By_productRelationshipType
    [Tags]  Get_Method      To_check
    Get_token
    get_duplicated_sorting                      productRelationship.relationshipType

Invalid_Sorting_By_Worng_Field
    [Tags]  Get_Method
    Get_token
    get_invalid_sorting     worng_field         sort value is not a valid type

Invalid_Sorting_By_Empty_Field
    [Tags]  Get_Method
    Get_token
    get_invalid_sorting     ${EMPTY}            sort must not be empty

Invalid_Sorting_By_nonExistent_Field
    [Tags]  Get_Method
    Get_token
    get_invalid_sorting     worng_sort          sort value is not a valid type




*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Products_with_empty_Field
    [Tags]  Get_Method
    Get_token
    get_with_bad_field            ${EMPTY}      ${code_28}      ${invalid_paramter_reason}       fields must not be empty

Get_Products_with_nonExistent_Field
    [Tags]  Get_Method
    Get_token
    get_with_bad_field            worng_field   ${code_28}      ${invalid_paramter_reason}    worng_field not included in product fields

Get_Products_without_Field
    [Tags]  Get_Method
    Get_token
    get_without_fields

Get_Products_with_Name_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       name

Get_Products_with_isBundle_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       isBundle

Get_Products_with_Status_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       status

Get_Products_with_operationalStatus_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       operationalStatus

Get_Products_with_productOrderItem_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       productOrderItem

Get_Products_with_productRelationship_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       productRelationship

Get_Products_with_relatedParty_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       relatedParty

Get_Products_with_productOffering_Field
    [Tags]  Get_Method
    Get_token
    get_with_specific_field       productOffering

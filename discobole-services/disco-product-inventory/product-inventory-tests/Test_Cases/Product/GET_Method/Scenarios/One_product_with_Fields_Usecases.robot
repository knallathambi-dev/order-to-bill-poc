*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_One_Product_with_empty_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                Post_valid          Post_body_request
    get_one_with_bad_field      ${valid_id}         ${EMPTY}              fields must not be empty

Get_One_Product_with_nonExistent_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                Post_valid          Post_body_request
    get_one_with_field          ${valid_id}         worng_field

Get_One_Product_with_nullable_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                  Post_valid            Post_body_request
    get_one_product_with_field    ${valid_id}           ${NULL}

Get_One_Product_with_isBundle_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field                 ${valid_id}      isBundle       True

Get_One_Product_with_Status_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field                 ${valid_id}      status         Created

Get_One_Product_with_operationalStatus_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field                 ${valid_id}      operationalStatus   Confirmed

Get_One_Product_with_productOrderItem_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field_2               ${valid_id}      productOrderItem

Get_One_Product_with_productRelationship_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field_2               ${valid_id}      productRelationship

Get_One_Product_with_relatedParty_Field
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field_2               ${valid_id}      relatedParty

Get_Products_with_productOffering_Field_2
    [Tags]  Get_Method
    Get_token
    ${valid_id}=                                        Post_valid       Post_body_request
    get_one_product_with_specific_field_2               ${valid_id}      productOffering

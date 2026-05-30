*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Filtering_by_Unsupported filter
    [Tags]  Get_Method
    Get_token
    Unsupported_filtering   tag_test          Data_value

Get_Contract_Product_By_valid_id
    [Tags]  Get_Method
    Get_token
    ${valid_id}=        Post_valid                  Post_body_request
    valid_get           ${valid_id}                 Product

Get_Physical_Product_By_valid_id
    [Tags]  Get_Method
    Get_token
    ${Product_id}       Post_valid_physical         Post_body_request_for_physical_product
    valid_get           ${Product_id}[3]            PhysicalProduct

Get_Shipement_Product_By_valid_id
    [Tags]  Get_Method      To_check
    Get_token
    ${Product_id}       Post_valid_Shipment         Post_body_request_for_shipment_product
    valid_get           ${Product_id}[1]            ShipmentProduct

Get_Product_By_nonExistent_id
    [Tags]  Get_Method
    Get_token
    invalid_get         worng_id

Get_Product_By_empty_id
    [Tags]  Get_Method    bug_IPCEISCPIB-4167 
    Get_token
    empty_get          ${EMPTY_id}

Filtering_by_Valid_id
    [Tags]  Get_Method
    Get_token
    ${valid_id}=        Post_valid      Post_body_request
    valid_filtering     id              ${valid_id}

Filtering_by_nonExistent_id
    [Tags]  Get_Method
    Get_token
    invalid_filtering   id          worng_id

Filtering_by_Empty_id
    [Tags]  Get_Method
    Get_token
    invalid_filtering   id          ${EMPTY}







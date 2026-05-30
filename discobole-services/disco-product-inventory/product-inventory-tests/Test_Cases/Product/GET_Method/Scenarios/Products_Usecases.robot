*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Products
    [Tags]  Get_Method
    Get_token
    get_all_products

Get_Physical_Product
    [Tags]  Get_Method
    Get_token
    get_all_tangible_products       PhysicalProduct

Get_Shipment_Product
    [Tags]  Get_Method
    Get_token
    get_all_tangible_products       ShipmentProduct

Filtering_Products_by_Name
    [Tags]  Get_Method
    Get_token
    get_valid_name_product           name           ${Smart_Contract_name}

Filtering_Products_by_nonExistent_Name
    [Tags]  Get_Method
    Get_token
    get_bad_name_product             name           worng_name

Filtering_Products_by_empty_Name
    [Tags]  Get_Method
    Get_token
    get_empty_name_product           name           ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        name must not be empty

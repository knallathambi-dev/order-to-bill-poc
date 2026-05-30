*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Physical_Product_with_Contract
    Get_token
    Post_valid_physical     Post_body_request_for_physical_product              #Post_body_request_for_solded_physical_product

Create_Shipment_Product_with_Contract
    Post_valid_Shipment                 Post_body_request_for_shipment_product

Invalid_Create_Physical_Product_at_level_BundleProductOffering
    post_invalid_Physical_product           Post_Invalid_Body_request_for_physicalProduct_level_BundlePO           BundleProductOffering

Invalid_Create_Physical_Product_at_level_Contract
    post_invalid_Physical_product           Post_Invalid_Body_request_for_physicalProduct_level_Contract           Contract

Inavlid_Create_Physical_Product_without_product_serial_number
    post_Physical_product_without_PSN       ${code_23}             ${missing_reason}             Empty product serial number

Inavlid_Create_Physical_Product_with_worng_product_serial_number
    Get_token
    post_Physical_product_worng_PSN         1234                ${code_60}             ${notfound_reason}             Inventory Resource with id 65x doesn't exist

Inavlid_Create_Physical_Product_with_empty_product_serial_number
    Get_token
    post_Physical_product_worng_PSN_BR         ${EMPTY}            ${code_23}             ${missing_reason}              Empty product serial number

Inavlid_Create_Physical_Product_with_noExistent_realizingResource
    post_Physical_product_realizingResource         69x                     Inventory Resource with id 69x serial number 67890543219876511223 is different from 5678

Inavlid_Create_Physical_Product_with_not_reserved_realizingResource
    post_Physical_product_realizingResource         68x                     Inventory Resource with id 68x doesn't exist        #Inventory Resource with id 68x status is not reserved

Inavlid_Create_Physical_Product_with_not_tangible_realizingResource
    post_Physical_product_realizingResource         67x                     Inventory Resource with id 67x doesn't exist        #Inventory Resource with id 67x referredType is not Tangible


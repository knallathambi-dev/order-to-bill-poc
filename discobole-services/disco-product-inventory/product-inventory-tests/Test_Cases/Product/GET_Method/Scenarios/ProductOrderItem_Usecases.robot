*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_productOrderId
    [Tags]  Get_Method
    Get_token
    get_valid_productOrderItem          productOrderId          ${Valid_productOrderId}

Get_Product_By_nonExistent_productOrderId
    [Tags]  Get_Method
    Get_token
    get_bad_productOrderItem            productOrderId          worng_productOrderId

Get_Product_By_empty_productOrderId
    [Tags]  Get_Method
    Get_token
    get_empty_productOrderItem          productOrderId          ${EMPTY}                    ${code_28}       ${invalid_paramter_reason}        productOrderItem.productOrderId must not be empty

Get_Product_By_valid_orderItemId
    [Tags]  Get_Method
    Get_token
    get_valid_productOrderItem          orderItemId             ${Valid_orderItemId}

Get_Product_By_nonExistent_orderItemId
    [Tags]  Get_Method
    Get_token
    get_bad_productOrderItem            orderItemId             worng_orderItemId

Get_Product_By_empty_orderItemId
    [Tags]  Get_Method
    Get_token
    get_empty_productOrderItem          orderItemId             ${EMPTY}                    ${code_28}       ${invalid_paramter_reason}        	productOrderItem.orderItemId must not be empty

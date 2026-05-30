*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_productSpecification_id
    [Tags]  Get_Method
    Get_token
    get_valid_productSpecification          id          ${Mobile_Line_productSpecification_id}

Get_Product_By_nonExistent_productSpecification_id
    [Tags]  Get_Method
    Get_token
    get_bad_productSpecification            id          nonExistent_id

Get_Product_By_empty_productSpecification_id
    [Tags]  Get_Method
    Get_token
    get_empty_productSpecification          id          ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        productSpecification.id must not be empty

Get_Product_By_valid_Contract_productSpecification_@type
    [Tags]  Get_Method
    Get_token
    get_valid_productSpecification          @type       ProductSpecificationRef

Get_Product_By_nonExistent_productSpecification_@type
    [Tags]  Get_Method
    Get_token
    get_bad_productSpecification            @type       worng_type

Get_Product_By_empty_productSpecification_@type
    [Tags]  Get_Method
    Get_token
    get_empty_productSpecification          @type       ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        productSpecification.@type must not be empty



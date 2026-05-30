*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_productOffering_id
    [Tags]  Get_Method
    Get_token
    get_valid_productOffering          id          ${Valid_productOffering_id}

Get_Product_By_nonExistent_productOffering_id
    [Tags]  Get_Method
    Get_token
    get_bad_productOffering            id          worng_id

Get_Product_By_empty_productOffering_id
    [Tags]  Get_Method
    Get_token
    get_empty_productOffering          id          ${EMPTY}         ${code_28}       ${invalid_paramter_reason}         productOffering.id must not be empty

Get_Product_By_valid_productOffering_name
    [Tags]  Get_Method
    Get_token
    get_valid_productOffering          name        ${Mobile_Package__productOffering_name}

Get_Product_By_nonExistent_productOffering_name
    [Tags]  Get_Method
    Get_token
    get_bad_productOffering            name        worng_name

Get_Product_By_empty_productOffering_name
    [Tags]  Get_Method
    Get_token
    get_empty_productOffering          name        ${EMPTY}         ${code_28}       ${invalid_paramter_reason}         productOffering.name must not be empty

Get_Product_By_valid_Contract_productOffering_@type
    [Tags]  Get_Method
    Get_token
    get_valid_productOffering          @type       Contract

Get_Product_By_valid_BundleProductOffering_productOffering_@type
    [Tags]  Get_Method
    Get_token
    get_valid_productOffering          @type       BundleProductOffering

Get_Product_By_valid_AtomicProductOffering_productOffering_@type
    [Tags]  Get_Method
    Get_token
    get_valid_productOffering          @type       AtomicProductOffering

Get_Product_By_nonExistent_productOffering_@type
    [Tags]  Get_Method
    Get_token
    get_bad_productOffering            @type       worng_type

Get_Product_By_empty_productOffering_@type
    [Tags]  Get_Method
    Get_token
    get_empty_productOffering          @type       ${EMPTY}         ${code_28}       ${invalid_paramter_reason}        productOffering.@type must not be empty




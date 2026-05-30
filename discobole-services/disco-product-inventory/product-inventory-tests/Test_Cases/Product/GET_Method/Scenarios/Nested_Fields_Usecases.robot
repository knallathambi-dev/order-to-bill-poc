*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_with_productOffering.id_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productOffering       id

Get_Product_with_productOffering.name_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productOffering       name

Get_Product_with_productOffering.@type_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productOffering       @type

Get_Product_with_invalid_nested_Field
    [Tags]  Get_Method
    Get_token
    get_with_bad_nested_field     productOffering       worng_field

Get_Product_with_nullable_nested_Field
    [Tags]  Get_Method
    Get_token
    get_with_bad_nested_field     productOffering       ${NULL}

Get_Product_with_empty_nested_Field
    [Tags]  Get_Method
    Get_token
    get_with_bad_nested_field     productOffering       ${EMPTY}

Get_Product_with_productCharacteristic.id_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productCharacteristic       id

Get_Product_with_productCharacteristic.name_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productCharacteristic       name

Get_Product_with_productCharacteristic.valueType_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productCharacteristic       valueType

Get_Product_with_productCharacteristic.@type_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productCharacteristic       @type

Get_Product_with_productCharacteristic.value_Field
    [Tags]  Get_Method
    Get_token
    get_with_nested_field         productCharacteristic       value

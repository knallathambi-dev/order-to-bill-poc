*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Valid_creation_with_Valid_productCharacteristic_@type_ValidityCharacteristic
    Get_token
    Post_valid      Post_body_for_ValidityCharacteristic

Invalid_creation_with_Valid_productCharacteristic_ValidityCharacteristic_with_Validto
    Get_token
    Post_invalid_ValidityCharacteristic_Exclusion_Validation_with_invalid_validto        validTo          ${code_24}      ${invalid_reason}         Invalid 'validTo' date format

Invalid_creation_ValidityCharacteristic_with_bad_unitOfMeasure
    # To check with PO
    Get_token
    Post_valid_ValidityCharacteristic_UnitOfMeasure             unitOfMeasure           ${code_24}      ${invalid_reason}         Invalid Input

Valid_creation_ValidityCharacteristic_with_hour_unit
    Get_token
    Post_valid_ValidityCharacteristic       unitOfMeasure          Hour

Valid_creation_ValidityCharacteristic_with_day_unit
    Get_token
    Post_valid_ValidityCharacteristic       unitOfMeasure          Day

Valid_creation_ValidityCharacteristic_with_month_unit
    Get_token
    Post_valid_ValidityCharacteristic       unitOfMeasure          Month

Exclusion_Validation_between_Value_UnitOfMeasure_and_ValidTo
    Get_token
    Post_invalid_ValidityCharacteristic_Exclusion_Validation        unitOfMeasure          ${code_24}      ${invalid_reason}         Either 'value' and 'unitOfMeasure' must be present together or 'validTo', but not both






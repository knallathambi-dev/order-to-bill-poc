*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Products_By_StringCharacteristic_@Type
    [Tags]  Get_Method
    Get_token
    get_productCharacteristic           @type           StringCharacteristic

Get_Products_By_ValidityCharacteristic_@Type
    [Tags]  Get_Method
    Get_token
    get_productCharacteristic           @type           ValidityCharacteristic

Get_Products_By_ObjectCharacteristic_@Type
    [Tags]  Get_Method
    Get_token
    get_productCharacteristic           @type           ObjectCharacteristic

Get_Products_By_nonExistent_@Type
    [Tags]  Get_Method
    Get_token
    get_bad_productCharacteristic       @type           worng_value

Get_Products_By_Empty_@Type
    [Tags]  Get_Method
    Get_token
    get_bad_productCharacteristic       @type           ${EMPTY}

Get_Products_By_Valid_name
    [Tags]  Get_Method
    Get_token
    get_productCharacteristic           name            Validity

Get_Products_By_nonExistent_name
    [Tags]  Get_Method
    Get_token
    get_bad_productCharacteristic       name            worng_value

Get_Products_By_Empty_name
    [Tags]  Get_Method
    Get_token
    get_bad_productCharacteristic       name            ${EMPTY}

# Orange FR

Get_Products_By_Valid_MSISDN
    [Tags]  Get_Method
    Get_token
    get_valid_productCharacteristic         MSISDN           ${Valid_MSISDN}

Get_Products_By_incomplete_MSISDN
    [Tags]  Get_Method
    Get_token
    get_incomplete_productCharacteristic         MSISDN           415*              ${Valid_MSISDN}

Get_Products_By_Any_MSISDN
    [Tags]  Get_Method
    Get_token
    get_incomplete_productCharacteristic         MSISDN           *             ${Valid_MSISDN}

Get_Products_By_nonExistent_MSISDN
    [Tags]  Get_Method
    Get_token
    get_invalid_productCharacteristic       MSISDN           nonExistent_MSISDN

Get_Products_By_Empty_MSISDN_name
    [Tags]  Get_Method
    Get_token
    get_empty_productCharacteristic         ${EMPTY}         ${Valid_MSISDN}

Get_Products_By_Empty_MSISDN_value
    [Tags]  Get_Method
    Get_token
    get_empty_productCharacteristic         MSISDN           ${EMPTY}

Get_Products_By_Missing_MSISDN_name
    [Tags]  Get_Method
    Get_token
    get_missing_productCharacteristic       value            ${Valid_MSISDN}

Get_Products_By_Missing_MSISDN_value
    [Tags]  Get_Method
    Get_token
    get_missing_productCharacteristic       name             MSISDN

Get_Products_By_Valid_IMSI
    [Tags]  Get_Method
    Get_token
    get_valid_productCharacteristic         IMSI             ${Valid_IMSI}

Get_Products_By_incomplete_IMSI
    [Tags]  Get_Method
    Get_token
    get_incomplete_productCharacteristic         IMSI             310*              ${Valid_IMSI}

Get_Products_By_Any_IMSI
    [Tags]  Get_Method
    Get_token
    get_incomplete_productCharacteristic         IMSI             *                 ${Valid_IMSI}

Get_Products_By_nonExistent_IMSI
    [Tags]  Get_Method
    Get_token
    get_invalid_productCharacteristic       IMSI             nonExistent_IMSI

Get_Products_By_Empty_IMSI_name
    [Tags]  Get_Method
    Get_token
    get_empty_productCharacteristic         ${EMPTY}         ${Valid_IMSI}

Get_Products_By_Empty_IMSI_value
    [Tags]  Get_Method
    Get_token
    get_empty_productCharacteristic         IMSI             ${EMPTY}

Get_Products_By_Missing_IMSI_name
    [Tags]  Get_Method
    Get_token
    get_missing_productCharacteristic       value            ${Valid_IMSI}

Get_Products_By_Missing_IMSI_value
    [Tags]  Get_Method
    Get_token
    get_missing_productCharacteristic       name             IMSI

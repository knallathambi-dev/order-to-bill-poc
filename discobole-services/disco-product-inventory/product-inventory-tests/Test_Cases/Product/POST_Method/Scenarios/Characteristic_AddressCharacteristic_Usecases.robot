*** Settings ***
Library           Collections
Library           String
Library           BuiltIn

Suite Setup       Log    === STARTING AddressCharacteristic TEST SUITE ===
Suite Teardown    Log    === FINISHED AddressCharacteristic TEST SUITE ===

*** Variables ***
${VALID_ADDRESS}               {'@type': 'AddressCharacteristic', 'addressId': '1', 'subUnitNumber': 'Apt 101', 'streetName': 'MG Road', 'postcode': '122001', 'city': 'Gurugram', 'country': 'India'}
${MISSING_POSTCODE}            {'@type': 'AddressCharacteristic', 'addressId': '2', 'subUnitNumber': 'Apt 102', 'streetName': 'Park Ave', 'postcode': '', 'city': 'Delhi', 'country': 'India'}
${MISSING_CITY}                {'@type': 'AddressCharacteristic', 'addressId': '3', 'subUnitNumber': 'Bldg 10', 'streetName': 'Main St', 'postcode': '75001', 'city': '', 'country': 'India'}
${INVALID_POSTCODE_FORMAT}     {'@type': 'AddressCharacteristic', 'addressId': '4', 'subUnitNumber': 'Floor 3', 'streetName': 'Church St', 'postcode': 'ABC123', 'city': 'Mumbai', 'country': 'India'}
${EMPTY_STREET}                {'@type': 'AddressCharacteristic', 'addressId': '5', 'subUnitNumber': 'Apt 202', 'streetName': '', 'postcode': '560001', 'city': 'Bangalore', 'country': 'India'}
${EMPTY_COUNTRY}               {'@type': 'AddressCharacteristic', 'addressId': '6', 'subUnitNumber': 'Suite 7', 'streetName': 'Ocean Blvd', 'postcode': '110001', 'city': 'Chennai', 'country': ''}

*** Keywords ***
Create Valid Address Characteristic
    [Arguments]    ${address}
    Log    Creating address with data: ${address}
    Should Be Equal As Strings    ${address['@type']}    'AddressCharacteristic'
    Dictionary Should Contain Key    ${address}    addressId
    Dictionary Should Contain Key    ${address}    streetName
    Dictionary Should Contain Key    ${address}    postcode
    Dictionary Should Contain Key    ${address}    city
    Dictionary Should Contain Key    ${address}    country

Validate Address Fields
    [Arguments]    ${address}
    Should Not Be Empty    ${address['addressId']}
    Should Not Be Empty    ${address['streetName']}
    Should Not Be Empty    ${address['postcode']}
    Should Not Be Empty    ${address['city']}
    Should Not Be Empty    ${address['country']}
    Should Match Regexp    ${address['postcode']}    ^\d{5,6}$

Create Invalid Address And Expect Failure
    [Arguments]    ${address}
    Run Keyword And Expect Error    *    Create Valid Address Characteristic    ${address}
    Log    Correctly failed validation for: ${address}

*** Test Cases ***

Missing Required Field - Postcode

    Create Invalid Address And Expect Failure    ${MISSING_POSTCODE}

Missing Required Field - City

    Create Invalid Address And Expect Failure    ${MISSING_CITY}

Invalid Postcode Format

    Create Invalid Address And Expect Failure    ${INVALID_POSTCODE_FORMAT}

Empty Street Name

    Create Invalid Address And Expect Failure    ${EMPTY_STREET}

Empty Country Field

    Create Invalid Address And Expect Failure    ${EMPTY_COUNTRY}




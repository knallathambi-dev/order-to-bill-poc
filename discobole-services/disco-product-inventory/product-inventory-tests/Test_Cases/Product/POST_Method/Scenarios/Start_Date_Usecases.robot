*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Test Cases ***

Create_Product_with_valid_start_date

    Get_token
    ${date}=                Get_date
    Post_with_date          ${date}         ${code_24}       ${invalid_reason}       The start date should not be provided in POST request

Create_Product_with_empty_start_date
    [Tags]      IPCEISCPIB-862
    Post_with_date          ${EMPTY}        ${code_24}       ${invalid_reason}       Invalid 'startDate' Field

Create_Product_with_nullable_start_date
    Post_nullable_date      ${NULL}

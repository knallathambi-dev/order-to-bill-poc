*** Settings ***
Resource        ../Keywords/post_usecases.robot

*** Variables ***

${Valid_Future_Date}                    2023-04-19T09:50:09Z
${Valid_Past_Date}                      2025-04-19T09:50:09Z


*** Test Cases ***

Create_Product_with_valid_creation_date
    Get_token
    ${Valid_Date}                             Get_date
    Post_with_creationDate                    ${Valid_Date}                     ${Valid_Date}

Create_Product_with_valid_future_creation_date
    Get_token
    Post_with_creationDate                    ${Valid_Future_Date}              ${Valid_Future_Date}

Create_Product_with_valid_past_creation_date
    Get_token
    Post_with_creationDate                    ${Valid_Past_Date}                ${Valid_Future_Date}

Create_Product_with_empty_creation_date
    Get_token
    Post_with_empty_creationDate              ${EMPTY}

Create_Product_with_nullable_creation_date
    Get_token
    ${Valid_Date}                             Get_date
    Post_with_creationDate                    ${NULL}                           ${Valid_Date}


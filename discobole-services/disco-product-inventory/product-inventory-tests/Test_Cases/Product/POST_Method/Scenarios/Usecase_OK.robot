*** Settings ***
Library    SeleniumLibrary
Resource        ../Keywords/post_usecases.robot
*** Variables ***
${PRODUCT_ID}  67123f234ff22065eac91689

*** Test Cases ***

Create_Product
    [Tags]      IPCEISCPIB-2249
    Get_token
    ${id2}=      Post_valid     Post_body_request_for_physical_product


Create_Product_with_relatedParty
    Get_token
    ${id}=      Post_valid      Post_body_with_relatedParty_request


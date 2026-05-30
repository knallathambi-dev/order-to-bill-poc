*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_with_empty_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_bad_operationalStatus          ${EMPTY}        ${code_28}       ${invalid_paramter_reason}       operationalStatus must not be empty

Get_Product_with_nonExistent_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_bad_operationalStatus          worng_operationalStatus    ${code_28}       ${invalid_paramter_reason}        operationalStatus value is not a valid type

Get_Product_with_Created_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_partial_with_operationalStatus      Created

Get_Product_with_Confirmed_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_partial_with_operationalStatus      Confirmed

Get_Product_with_Cancelled_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              Cancelled

Get_Product_with_PendingActive_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              PendingActive

Get_Product_with_PendingCancel_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              PendingCancel

Get_Product_with_Locked_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              Locked

Get_Product_with_Aborted_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              Aborted

Get_Product_with_Active_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_partial_with_operationalStatus      Active

Get_Product_with_PendingModification_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              PendingModification

Get_Product_with_PendingTerminate_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              PendingTerminate

Get_Product_with_Terminated_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              Terminated

Get_Product_with_Sold_operationalStatus
    [Tags]  Get_Method
    Get_token
    get_with_operationalStatus              Sold

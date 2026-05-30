*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot
Resource        ../../../../Resources/Common_Keywords.robot
Library    SeleniumLibrary
Library    RequestsLibrary
Library    OperatingSystem
Library    String
Library    Collections


*** Variables ***
@{EXCLUDE_CLASSES}    tag    tag-sm    status-value
${ADMIN_PRODUCT_ID}             682b3001f7065e5a450eab15
*** Test Cases ***


## Valid Test Cases

Valid_Patch_physicalProduct_with_Locked_operationalStatus_From_Confirmed
    [Tags]          IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Confirmed.
    #Action: Change Operational Status to "Locked".
    #Expected Result: Main Status remains "Created", Operational Status changes to "Locked".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Locked
    Log Result To Text File     IPCEISCPIB-512      ${result}       Valid_Patch_physicalProduct_with_Locked_operationalStatus_From_Confirmed    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_PendingDelivery_operationalStatus_From_Confirmed
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Confirmed.
    #Action: Change Operational Status to "PendingDelivery".
    #Expected Result: Main Status remains "Created", Operational Status changes to "PendingDelivery".

    Get_token
    ${Contract_id}      ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          PendingDelivery
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      PendingDelivery
    Log Result To Text File     IPCEISCPIB-512      ${result}       Valid_Patch_physicalProduct_with_PendingDelivery_operationalStatus_From_Confirmed       ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_Locked
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Locked.
    #Action: Change Operational Status to "Confirmed".
    #Expected Result: Main Status remains "Created", Operational Status changes to "Confirmed".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Created          Confirmed
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Confirmed
    Log Result To Text File     IPCEISCPIB-512      ${result}      Valid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_Locked       ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Locked
    [Tags]      IPCEISCPIB-512_Tangible         IPCEISCPIB-912_UC3
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Locked.
    #Action: Change Operational Status to "Aborted".
    #Expected Result: Main Status changes to "Aborted", Operational Status changes to "Aborted".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Aborted          Aborted
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Aborted     ${oper_status}      Aborted
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Locked    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_Locked
    [Tags]      IPCEISCPIB-512_Tangible         IPCEISCPIB-912_UC4
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Locked.
    #Action: Change Operational Status to "Sold".
    #Expected Result: Main Status changes to "Sold", Operational Status changes to "Sold".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Sold          Sold
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Sold     ${oper_status}      Sold
    Log Result To Text File     IPCEISCPIB-512      ${result}       Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_Locked        ${physicalProduct_id}

Valid_Patch_physicalProduct_with_PendingDelivery_operationalStatus_From_Locked
    [Tags]      IPCEISCPIB-512_Tangible         IPCEISCPIB-912_UC2
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Locked.
    #Action: Change Operational Status to "PendingDelivery".
    #Expected Result: Main Status remains "Created", Operational Status changes to "PendingDelivery".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Created          PendingDelivery
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      PendingDelivery
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_PendingDelivery_operationalStatus_From_Locked    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_PendingDelivery
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = PendingDelivery.
    #Action: Change Operational Status to "Confirmed".
    #Expected Result: Main Status remains "Created", Operational Status changes to "Confirmed".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Created          PendingDelivery
    patch_status        ${physicalProduct_id}         Created          Confirmed
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Confirmed
    Log Result To Text File    IPCEISCPIB-512       ${result}    Valid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_PendingDelivery    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_PendingDelivery
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = PendingDelivery.
    #Action: Change Operational Status to "Sold".
    #Expected Result: Main Status changes to "Sold", Operational Status changes to "Sold".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Created          PendingDelivery
    patch_status        ${physicalProduct_id}         Sold          Sold
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Sold     ${oper_status}      Sold
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_PendingDelivery    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Locked_operationalStatus_From_PendingDelivery
    [Tags]      IPCEISCPIB-512_Tangible         IPCEISCPIB-912_UC1
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = PendingDelivery.
    #Action: Change Operational Status to "Sold".
    #Expected Result: Main Status changes to "Sold", Operational Status changes to "Sold".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Created          Locked
    patch_status        ${physicalProduct_id}         Created          PendingDelivery
    patch_status        ${physicalProduct_id}         Created          Locked
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Locked
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Locked_operationalStatus_From_PendingDelivery    ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Cancelled_operationalStatus_From_PendingCancel
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = PendingCancel.
    #Action: Change Operational Status to "PendingCancel".
    #Expected Result: Main Status changes to "Created", Operational Status changes to "Cancelled".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          PendingCancel
    patch_status        ${physicalProduct_id}         Cancelled        Cancelled
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Cancelled     ${oper_status}      Cancelled
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Cancelled_operationalStatus_From_PendingCancel    ${physicalProduct_id}


Valid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Created
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Aborted.
    #Action: Change Operational Status to "Aborted".
    #Expected Result: Main Status changes to "Aborted", Operational Status changes to "Aborted".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created           Created
    patch_status        ${physicalProduct_id}         Aborted           Aborted
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Aborted     ${oper_status}      Aborted
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Created     ${physicalProduct_id}

Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_Created
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    #Precondition: Main Status = Created, Operational Status = Aborted.
    #Action: Change Operational Status to "Aborted".
    #Expected Result: Main Status changes to "Aborted", Operational Status changes to "Aborted".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created           Created
    patch_status        ${physicalProduct_id}         Sold              Sold
    ${main_status}=     Check_Main_Status       ${physicalProduct_id}
    ${oper_status}=     Check_Operational_Status   ${physicalProduct_id}
    ${result}=          Evaluate Test Step      ${main_status}      Sold     ${oper_status}      Sold
    Log Result To Text File    IPCEISCPIB-512       ${result}        Valid_Patch_physicalProduct_with_Sold_operationalStatus_From_Created     ${physicalProduct_id}

# Invalid Test Cases

Invalid_Patch_physicalProduct_with_Locked_operationalStatus_From_Created
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = Created.
    # Action: Attempt to change Operational Status to "Locked".
    # Expected Result: The operation should fail or result in an error, as "Locked" is not valid from "Created".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Created
    ${error_message}=       invalid_patch_operationalStatus_for_tangible_product        ${physicalProduct_id}         Created          Locked
    ${result}=           Evaluate_Error_Message    ${error_message}    ${EXPECTED_ERROR_1}
    Log Result To Text File    IPCEISCPIB-512       ${result}       Invalid_Patch_physicalProduct_with_Locked_operationalStatus_From_Created    ${physicalProduct_id}

Invalid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_Sold
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = Sold.
    # Action: Attempt to change Operational Status to "Confirmed".
    # Expected Result: The operation should fail or result in an error, as "Confirmed" cannot be reverted from "Sold".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created           Confirmed
    patch_status        ${physicalProduct_id}         Sold              Sold
    ${error_message}=       invalid_patch_operationalStatus_for_tangible_product        ${physicalProduct_id}         Sold          Confirmed
    ${result}=           Evaluate_Error_Message    ${error_message}    ${EXPECTED_ERROR_2}
    Log Result To Text File    IPCEISCPIB-512       ${result}       Invalid_Patch_physicalProduct_with_Confirmed_operationalStatus_From_Sold    ${physicalProduct_id}

Invalid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Confirmed
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = Confirmed.
    # Action: Attempt to change Operational Status to "Aborted".
    # Expected Result: The operation should fail, as "Aborted" cannot be changed from "Confirmed".

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    ${error_message}=       invalid_patch_operationalStatus_for_tangible_product        ${physicalProduct_id}         Confirmed       Aborted
    ${result}=           Evaluate_Error_Message    ${error_message}    ${EXPECTED_ERROR_3}
    Log Result To Text File    IPCEISCPIB-512       ${result}       Invalid_Patch_physicalProduct_with_Aborted_operationalStatus_From_Confirmed     ${physicalProduct_id}

Invalid_Patch_physicalProduct_with_Unknown_Status_change
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = Created.
    # Action: Attempt to change Operational Status to an invalid status "UnknownStatus".
    # Expected Result: The operation should fail, as "UnknownStatus" is not a valid operational status.

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Created
    ${error_message}=       invalid_patch_operationalStatus_for_tangible_product        ${physicalProduct_id}         Created          UnknownStatus
    ${result}=           Evaluate_Error_Message    ${error_message}    ${EXPECTED_ERROR_3}
    Log Result To Text File    IPCEISCPIB-512       ${result}       Invalid_Patch_physicalProduct_with_Unknown_Status_change    ${physicalProduct_id}

Invalid_Patch_physicalProduct_with_Already_Sold_status_change
    [Tags]      IPCEISCPIB-512_Tangible
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = Sold.
    # Action: Attempt to change Operational Status after it is already "Sold".
    # Expected Result: The operation should fail, as "Sold" cannot be changed to another operational status.

    Get_token
    ${Contract_id}       ${BundleProductOffering_id}       ${AtomicProductOffering_id}      ${physicalProduct_id}       ${parentContract_id}=        Post_valid_physical                 Post_body_request_for_physical_product
    patch_status        ${physicalProduct_id}         Created          Confirmed
    patch_status        ${physicalProduct_id}         Sold          Sold
    ${error_message}=       invalid_patch_operationalStatus_for_tangible_product        ${physicalProduct_id}         Sold          PendingDelivery
    ${result}=           Evaluate_Error_Message    ${error_message}    ${EXPECTED_ERROR_4}
    Log_Result_To_Text_File    IPCEISCPIB-512       ${result}       Invalid_Patch_physicalProduct_with_Already_Sold_status_change        ${physicalProduct_id}

Valid_Patch_with_Locked_operationalStatus_From_Created
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Created, Operational Status = Confirmed.
     # Action: Change Operational Status to "Locked".
     # Expected Result: Main Status remains "Created", Operational Status changes to "Locked".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Locked
     Log Result To Text File     IPCEISCPIB-524         ${result}       Valid_Patch_with_Locked_operationalStatus_From_Created    ${Atomic_ProductOffering_id}

Valid_Patch_with_LockedActive_operationalStatus_From_Created
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Created, Operational Status = Confirmed.
     # Action: Change Operational Status to "LockedActive".
     # Expected Result: Main Status remains "Active", Operational Status changes to "LockedActive".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Locked
      Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_LockedActive_operationalStatus_From_Created    ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingActive_operationalStatus_From_Locked
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Created, Operational Status = Locked.
     # Action: Change Operational Status to "PendingActive".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingActive".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      PendingActive
      Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingActive_operationalStatus_From_Locked    ${Atomic_ProductOffering_id}

Valid_Patch_with_Locked_operationalStatus_From_Confirmed
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
    # Precondition: Main Status = Created, Operational Status = Confirmed.
    # Action: Change Operational Status to "Locked".
    # Expected Result: Main Status remains "Created", Operational Status changes to "Locked".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      Locked
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Locked_operationalStatus_From_Confirmed        ${Atomic_ProductOffering_id}

Valid_Patch_with_Aborted_operationalStatus_From_Locked
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering          IPCEISCPIB-912_UC3
     [Documentation]

     # Precondition: Main Status = Created, Operational Status = Locked.
     # Action: Change Operational Status to "Aborted".
     # Expected Result: Main Status changes to "Aborted", Operational Status changes to "Aborted".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Aborted                        Aborted
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     patch_status                                ${Atomic_ProductOffering_id}        Aborted                        Aborted
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Aborted     ${oper_status}      Aborted
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Aborted_operationalStatus_From_Locked    ${Atomic_ProductOffering_id}

Valid_Patch_with_Active_operationalStatus_From_Confirmed
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]

     # Precondition: Main Status = Created, Operational Status = Confirmed.
     # Action: Change Operational Status to "Active".
     # Expected Result: Main Status changes to "Active", Operational Status changes to "Active".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Active                        Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                        Active
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      Active
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Active_operationalStatus_From_Confirmed        ${Atomic_ProductOffering_id}

Valid_Patch_with_Aborted_operationalStatus_From_Confirmed
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]

     # Precondition: Main Status = Created, Operational Status = Confirmed.
     # Action: Change Operational Status to "Aborted".
     # Expected Result: Main Status changes to "Aborted", Operational Status changes to "Aborted".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Aborted                        Aborted
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Aborted                        Aborted
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Aborted     ${oper_status}      Aborted
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Aborted_operationalStatus_From_Confirmed      ${Atomic_ProductOffering_id}

Valid_Patch_with_LockedActive_operationalStatus_From_Active
    [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = Active.
    # Action: Change Operational Status to "LockedActive".
    # Expected Result: Main Status remains "Active", Operational Status changes to "LockedActive".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      LockedActive
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_LockedActive_operationalStatus_From_Active        ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingModification_operationalStatus_From_LockedActive
    [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = LockedActive.
    # Action: Change Operational Status to "PendingModification".
    # Expected Result: Main Status remains "Active", Operational Status changes to "PendingModification".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Spec_ProductOffering_id}          Active                         PendingModification
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingModification
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingModification_operationalStatus_From_LockedActive       ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingTerminate_operationalStatus_From_LockedActive
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = LockedActive.
    # Action: Change Operational Status to "PendingTerminate".
    # Expected Result: Main Status remains "Active", Operational Status changes to "PendingTerminate".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Spec_ProductOffering_id}          Active                         PendingTerminate
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingTerminate
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingTerminate_operationalStatus_From_LockedActive      ${Atomic_ProductOffering_id}

Valid_Patch_with_LockedActive_operationalStatus_From_PendingModification
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = PendingModification.
    # Action: Change Operational Status to "LockedActive".
    # Expected Result: Main Status remains "Active", Operational Status changes to "LockedActive".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Spec_ProductOffering_id}          Active                         PendingModification
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      LockedActive
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_LockedActive_operationalStatus_From_PendingModification       ${Atomic_ProductOffering_id}

Valid_Patch_with_LockedActive_operationalStatus_From_PendingTerminate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = PendingTerminate.
    # Action: Change Operational Status to "LockedActive".
    # Expected Result: Main Status remains "Active", Operational Status changes to "LockedActive".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Spec_ProductOffering_id}          Active                         PendingTerminate
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      LockedActive
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_LockedActive_operationalStatus_From_PendingTerminate      ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingCancel_operationalStatus_From_PendingActive
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = PendingActive.
    # Action: Change Operational Status to "PendingCancel".
    # Expected Result: Main Status remains "Created", Operational Status changes to "PendingCancel".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingCancel

     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}

     ${result}=          Evaluate Test Step      ${main_status}      Created     ${oper_status}      PendingCancel
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingCancel_operationalStatus_From_PendingActive      ${Atomic_ProductOffering_id}

Valid_Patch_with_Cancelled_operationalStatus_From_Confirmed
    [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]

    # Precondition: Main Status = Created, Operational Status = Confirmed.
    # Action: Change Operational Status to "Cancelled".
    # Expected Result: Main Status remains "Cancelled", Operational Status changes to "Cancelled".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Cancelled                      Cancelled
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Cancelled                      Cancelled
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Cancelled     ${oper_status}      Cancelled
     Log Result To Text File     IPCEISCPIB-524     ${result}        Valid_Patch_with_Cancelled_operationalStatus_From_Confirmed      ${Atomic_ProductOffering_id}

Valid_Patch_with_Cancelled_operationalStatus_From_PendingCancel
    [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]

    # Precondition: Main Status = Created, Operational Status = PendingCancel.
    # Action: Change Operational Status to "Cancelled".
    # Expected Result: Main Status remains "Cancelled", Operational Status changes to "Cancelled".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Aborted                        Aborted
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingCancel
     patch_status                                ${Atomic_ProductOffering_id}        Cancelled                      Cancelled
     log                                                ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}

     ${result}=          Evaluate Test Step      ${main_status}      Cancelled     ${oper_status}      Cancelled
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Cancelled_operationalStatus_From_PendingCancel      ${Atomic_ProductOffering_id}


Valid_Patch_with_Active_operationalStatus_From_PendingActive
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingActive.
     # Action: Change Operational Status to "Active".
     # Expected Result: Main Status remains "Active", Operational Status changes to "Active".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      Active
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Active_operationalStatus_From_PendingActive       ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingTerminate_operationalStatus_From_Active
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = Active.
     # Action: Change Operational Status to "PendingTerminate".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingTerminate".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingTerminate
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingTerminate_operationalStatus_From_Active       ${Atomic_ProductOffering_id}

Valid_Patch_with_PendingMigrate_operationalStatus_From_Active
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = Active.
     # Action: Change Operational Status to "PendingMigrate".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingMigrate".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingMigrate
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingMigrate
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingMigrate_operationalStatus_From_Active       ${Atomic_ProductOffering_id}


Valid_Patch_with_Active_operationalStatus_From_PendingMigrate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingMigrate.
     # Action: Change Operational Status to "Active".
     # Expected Result: Main Status remains "Active", Operational Status changes to "Active".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingMigrate
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      Active
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Active_operationalStatus_From_PendingMigrate       ${Atomic_ProductOffering_id}


Valid_Patch_with_PendingModification_operationalStatus_From_Active
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = Active.
     # Action: Change Operational Status to "PendingModification".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingModification".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingModification
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingModification_operationalStatus_From_Active       ${Atomic_ProductOffering_id}


Valid_Patch_with_Active_operationalStatus_From_PendingModification
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingModification.
     # Action: Change Operational Status to "Active".
     # Expected Result: Main Status remains "Active", Operational Status changes to "Active".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      Active
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Active_operationalStatus_From_PendingModification       ${Atomic_ProductOffering_id}

Valid_Patch_with_Terminated_operationalStatus_From_PendingMigrate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering_to_be_fixed_IPCEISCPIB-585

     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingMigrate.
     # Action: Change Operational Status to "Terminated".
     # Expected Result: Main Status remains "Terminated", Operational Status changes to "Terminated".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     #patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingMigrate
     patch_status                                ${Spec_ProductOffering_id}          Terminated                     Terminated
     patch_status                                ${Atomic_ProductOffering_id}        Terminated                     Terminated
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Terminated     ${oper_status}      Terminated
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Terminated_operationalStatus_From_PendingMigrate       ${Atomic_ProductOffering_id}

Valid_Patch_with_Terminated_operationalStatus_From_PendingTerminate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingTerminate.
     # Action: Change Operational Status to "Terminated".
     # Expected Result: Main Status remains "Terminated", Operational Status changes to "Terminated".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Created                        Confirmed
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active

     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     #patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     patch_status                                ${Spec_ProductOffering_id}          Terminated                     Terminated
     patch_status                                ${Atomic_ProductOffering_id}        Terminated                     Terminated
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Terminated     ${oper_status}      Terminated
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_Terminated_operationalStatus_From_PendingTerminate       ${Atomic_ProductOffering_id}



Valid_Patch_with_PendingTerminate_operationalStatus_From_PendingModification
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingModification.
     # Action: Change Operational Status to "PendingTerminate".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingTerminate".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     log                                         ${Product_id}[0]
     ${main_status}=     Check_Main_Status              ${Atomic_ProductOffering_id}
     ${oper_status}=     Check_Operational_Status       ${Atomic_ProductOffering_id}
     ${result}=          Evaluate Test Step      ${main_status}      Active     ${oper_status}      PendingTerminate
     Log Result To Text File     IPCEISCPIB-524     ${result}       Valid_Patch_with_PendingTerminate_operationalStatus_From_PendingModification       ${Atomic_ProductOffering_id}


Invalid_Patch_with_Confirmed_operationalStatus_From_Locked
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
    # Precondition: Main Status = Created, Operational Status = Locked.
    # Action: Change Operational Status to "Confirmed".
    # Expected Result: Main Status remains "Created", Operational Status changes to "Locked".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Locked
     log                                         ${Product_id}[0]
     ${error_message}=       invalid_patch_operationalStatus_for_Atomic_product        ${Atomic_ProductOffering_id}         Created       Confirmed
     ${result}=           Evaluate_Error_Message    ${error_message}        The operational status Locked cannot be modified into Confirmed
     Log Result To Text File    IPCEISCPIB-524       ${result}       Invalid_Patch_with_Confirmed_operationalStatus_From_Locked        ${Atomic_ProductOffering_id}

Invalid_Patch_with_PendingActive_operationalStatus_From_PendingCancel
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Created, Operational Status = PendingCancel.
    # Action: Change Operational Status to "PendingActive".
    # Expected Result: Main Status remains "Created", Operational Status changes to "PendingActive".

     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingCancel
     log                                                ${Product_id}[0]
     ${error_message}=       invalid_patch_operationalStatus_for_Atomic_product        ${Atomic_ProductOffering_id}         Created                        PendingActive
     ${result}=           Evaluate_Error_Message    ${error_message}        The operational status PendingCancel cannot be modified into PendingActive
     Log Result To Text File    IPCEISCPIB-524       ${result}       Valid_Patch_with_PendingActive_operationalStatus_From_PendingCancel        ${Atomic_ProductOffering_id}

Valid_Patch_with_Active_operationalStatus_From_LockedActive
    [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
    [Documentation]
    # Precondition: Main Status = Active, Operational Status = Active.
    # Action: Change Operational Status to "LockedActive".
    # Expected Result: Main Status remains "Active", Operational Status changes to "LockedActive".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Spec_ProductOffering_id}          Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         LockedActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     log                                         ${Product_id}[0]

Invalid_Patch_with_Active_operationalStatus_From_PendingTerminate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingTerminate.
     # Action: Change Operational Status to "Active".
     # Expected Result: Main Status remains "Active", Operational Status changes to "Active".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     log                                         ${Product_id}[0]
     ${error_message}=       invalid_patch_operationalStatus_for_Atomic_product        ${Atomic_ProductOffering_id}         Active                         Active
     ${result}=           Evaluate_Error_Message    ${error_message}        The operational status PendingTerminate cannot be modified into Active
     Log Result To Text File    IPCEISCPIB-524       ${result}       Invalid_Patch_with_Active_operationalStatus_From_PendingTerminate        ${Atomic_ProductOffering_id}

Invalid_Patch_with_PendingMigrate_operationalStatus_From_PendingTerminate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingTerminate.
     # Action: Change Operational Status to "PendingMigrate".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingMigrate".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
#     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
#     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
#     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
#     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
#     patch_status                                ${Product_id}[3]                    Sold                           Sold
#     patch_status                                ${Product_id}[2]                    Sold                           Sold
#     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
#     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
#     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
#     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
#     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
#
#     log                                         ${Product_id}[0]
#     ${error_message}=       invalid_patch_operationalStatus_for_Atomic_product        ${Atomic_ProductOffering_id}         Active                         PendingMigrate
#     ${result}=           Evaluate_Error_Message    ${error_message}        The operational status PendingTerminate cannot be modified into PendingMigrate
#     Log Result To Text File    IPCEISCPIB-524       ${result}       Invalid_Patch_with_PendingMigrate_operationalStatus_From_PendingTerminate        ${Atomic_ProductOffering_id}


Invalid_Patch_with_PendingModification_operationalStatus_From_PendingTerminate
     [Tags]  IPCEISCPIB-524_Atomic_ProductOffering
     [Documentation]
     # Precondition: Main Status = Active, Operational Status = PendingTerminate.
     # Action: Change Operational Status to "PendingModification".
     # Expected Result: Main Status remains "Active", Operational Status changes to "PendingModification".
     Get_token
     ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product
     ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
     patch_with_reliesOn_reliesFrom              ${Spec_ProductOffering_id}          ${Spec_ProductOffering_id2}
     patch_with_reliesOn_reliesFrom              ${Product_id}[3]                    ${Spec_ProductOffering_id}
     patch_status                                ${Product_id}[3]                    Sold                           Sold
     patch_status                                ${Product_id}[2]                    Sold                           Sold
     patch_status                                ${Spec_ProductOffering_id}          Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Created                        Confirmed
     patch_status                                ${Atomic_ProductOffering_id}        Created                        PendingActive
     patch_status                                ${Atomic_ProductOffering_id}        Active                         Active
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingModification
     patch_status                                ${Atomic_ProductOffering_id}        Active                         PendingTerminate
     log                                         ${Product_id}[0]
     ${error_message}=       invalid_patch_operationalStatus_for_Atomic_product        ${Atomic_ProductOffering_id}         Active                         PendingModification
     ${result}=           Evaluate_Error_Message    ${error_message}        The operational status PendingTerminate cannot be modified into PendingModification
     Log Result To Text File    IPCEISCPIB-524       ${result}       Invalid_Patch_with_PendingModification_operationalStatus_From_PendingTerminate        ${Atomic_ProductOffering_id}











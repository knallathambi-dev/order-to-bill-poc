*** Settings ***
Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../Keywords/patch_usecases.robot

*** Test Cases ***

Invalid_Patch_with_nonExistent_status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         status              nonExistent             One or multiple issues are exist in the path or the value fields

Invalid_Patch_with_Empty_status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         status              ${EMPTY}                One or multiple issues are exist in the path or the value fields

# Created Status

Invalid_Patch_Created/Terminated_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_body      ${valid_id}         status              Terminated             The status Created cannot be modified into Terminated

Invalid_Patch_Sold_for_Product_with_Crated_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_invalid_status    ${valid_id}         sold               Sold                     One or multiple issues are exist in the path or the value fields

# Cancelled Status

Invalid_Patch_Cancelled/Created_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled           Cancelled
    patch_invalid_body      ${valid_id}         status              Created                The status Cancelled cannot be modified into Created

Invalid_Patch_Cancelled/Aborted_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled           Cancelled
    patch_invalid_body      ${valid_id}         status              Aborted                 The status Cancelled cannot be modified into Aborted

Invalid_Patch_Cancelled/Active_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled           Cancelled
    patch_invalid_body      ${valid_id}         status              Active                  The status Cancelled cannot be modified into Active

Invalid_Patch_Cancelled/Terminated_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled           Cancelled
    patch_invalid_body      ${valid_id}         status              Terminated             The status Cancelled cannot be modified into Terminated

Invalid_Patch_Sold_for_Product_with_Cancelled_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Cancelled           Cancelled
    patch_invalid_status    ${valid_id}         sold                Sold                     One or multiple issues are exist in the path or the value fields

# Aborted Status

Invalid_Patch_Aborted/Created_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Created             PendingActive
    patch_status            ${valid_id}         Aborted             Aborted
    patch_invalid_body      ${valid_id}         status              Created                 The status Aborted cannot be modified into Created

Invalid_Patch_Aborted/Cancelled_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Created             PendingActive
    patch_status            ${valid_id}         Aborted             Aborted
    patch_invalid_body      ${valid_id}         status              Cancelled                 The status Aborted cannot be modified into Cancelled

Invalid_Patch_Aborted/Active_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Created             PendingActive
    patch_status            ${valid_id}         Aborted             Aborted
    patch_invalid_body      ${valid_id}         status              Active                 The status Aborted cannot be modified into Active

Invalid_Patch_Aborted/Terminated_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Created             PendingActive
    patch_status            ${valid_id}         Aborted             Aborted
    patch_invalid_body      ${valid_id}         status              Terminated                 The status Aborted cannot be modified into Terminated

Invalid_Patch_Sold_for_Product_with_Aborted_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Created             PendingActive
    patch_status            ${valid_id}         Aborted             Aborted
    patch_invalid_status    ${valid_id}         sold                Sold                     One or multiple issues are exist in the path or the value fields

# Active Status

Invalid_Patch_Active/Created_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_invalid_body      ${valid_id}         status              Created                     The status Active cannot be modified into Created

Invalid_Patch_Active/Cancelled_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_invalid_body      ${valid_id}         status              Cancelled                     The status Active cannot be modified into Cancelled

Invalid_Patch_Active/Aborted_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_invalid_body      ${valid_id}         status              Aborted                     The status Active cannot be modified into Aborted

Invalid_Patch_Active/Terminated_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_invalid_body      ${valid_id}         status              Terminated                  The mapping between the status Terminated and the operational status Active is invalid

Invalid_Patch_Sold_for_Product_with_Active_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_invalid_status    ${valid_id}         sold                Sold                     One or multiple issues are exist in the path or the value fields

# Terminated Status

Invalid_Patch_Terminated/Created_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_status            ${valid_id}         Terminated          Terminated
    patch_invalid_body      ${valid_id}         status              Created                         The status Terminated cannot be modified into Created

Invalid_Patch_Terminated/Cancelled_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_status            ${valid_id}         Terminated          Terminated
    patch_invalid_body      ${valid_id}         status              Cancelled                         The status Terminated cannot be modified into Cancelled

Invalid_Patch_Terminated/Aborted_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_status            ${valid_id}         Terminated          Terminated
    patch_invalid_body      ${valid_id}         status              Aborted                         The status Terminated cannot be modified into Aborted

Invalid_Patch_Terminated/Active_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_status            ${valid_id}         Terminated          Terminated
    patch_invalid_body      ${valid_id}         status              Active                         The status Terminated cannot be modified into Active

Invalid_Patch_Sold_for_Product_with_Active_Status
    Get_token
    ${valid_id}             Post_valid          Post_body_request_for_Patch
    patch_status            ${valid_id}         Created             Confirmed
    patch_status            ${valid_id}         Active              Active
    patch_status            ${valid_id}         Terminated          Terminated
    patch_invalid_status    ${valid_id}         sold                Sold                     One or multiple issues are exist in the path or the value fields

# Sold Status

Invalid_Patch_Sold/Created_Status
    Get_token
    ${valid_id}             Post_valid_physical                     Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                Sold
    patch_invalid_body      ${valid_id}[3]      status              Created                        The status Sold cannot be modified into Created

Invalid_Patch_Sold/Cancelled_Status
    Get_token
    ${valid_id}             Post_valid_physical                     Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                Sold
    patch_invalid_body      ${valid_id}[3]      status              Cancelled                      The status Sold cannot be modified into Cancelled

Invalid_Patch_Sold/Aborted_Status
    Get_token
    ${valid_id}             Post_valid_physical                     Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                Sold
    patch_invalid_body      ${valid_id}[3]      status              Aborted                        The status Sold cannot be modified into Aborted

Invalid_Patch_Sold/Active_Status
    Get_token
    ${valid_id}             Post_valid_physical                     Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                Sold
    patch_invalid_body      ${valid_id}[3]      status              Active                         The status Sold cannot be modified into Active

Invalid_Patch_Sold/Terminated_Status
    Get_token
    ${valid_id}             Post_valid_physical                     Post_body_request_for_physical_product
    patch_status            ${valid_id}[3]      Sold                Sold
    patch_invalid_body      ${valid_id}[3]      status              Terminated                     The status Sold cannot be modified into Terminated

*** Settings ***
Library    RequestsLibrary

Resource        ../../POST_Method/Keywords/post_usecases.robot
Resource        ../../GET_Method/Keywords/get_usecases.robot
Resource        ../Keywords/patch_usecases.robot


*** Keywords ***
Remove Relationship At Index
    [Arguments]    ${product_id}    ${index}
    # Retrieve the current relationships before removing any
    ${existing_relationships}=    Verify Relationship Before Removal    ${product_id}

    # Prepare the operation to remove the relationship at the specified index
    ${operation}=    Create Dictionary    op=remove    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/${index}    value=
    @{body}=    Create List    ${operation}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    Should Be Equal As Integers    ${response.status_code}    200

    # Verify the relationship has been successfully removed
    Verify Relationship Removed    ${product_id}    ${index}    ${existing_relationships}

Verify Relationship Before Removal
    [Arguments]    ${product_id}
     #${CPIB_TOKEN}=     Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI1ODg0MzksImlhdCI6MTc3MjU1NDEzMiwiYXV0aF90aW1lIjoxNzcyNTUyNDM5LCJqdGkiOiIyYWYwNDk5My03N2Y2LTRlYzUtYmI3ZC0xZGM5NmYwNTg4ZmUiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6ImI1OGRhNTRlLWMwZWQtNDNhZC05ZGIzLTA3YmQ1MzQ2MTQ2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.CtN1i82azspwyUP_jjRvKDCdHLNXN6Mt8vaSt98wfHLIzrTJDYk2e2h2e-yh5YdO9ZLi8esnOHC1xIHmmLtd1D6NGkisouo0yXiGUG3HDuNHIcRbCxMNY30az1rBBpGjjjVAtglT-d3w0qF1d3kkCrJjjQFBv8n4H6Zg5Ri0dWDa0FudjvmzuKbYTV_yTmHFyQR2e2bgsZP2spcN883QxvB9OOBDTNP-2Ax7Ce1RphRWV4YEuUBKlqALQxiltaIH_IMTOj-6U8ArSJIGlvNlob7-qmXjpUNS2kFZ-jFlyhSKrPQnkMR2cb80M_E4Cjp767O6ex8PGmt8lVIP1YRWRw
     &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
     Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session    /productInventoryManagement/v1/product/${product_id}    headers=&{headers}
    Should Be Equal As Integers    ${response.status_code}    200
    ${json}=    To JSON    ${response.content}
    ${relationships}=    Get From Dictionary    ${json}    productRelationship
    RETURN    ${relationships}

Verify Relationship Removed
    [Arguments]    ${product_id}    ${index}    ${existing_relationships}
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI1ODg0MzksImlhdCI6MTc3MjU1NDEzMiwiYXV0aF90aW1lIjoxNzcyNTUyNDM5LCJqdGkiOiIyYWYwNDk5My03N2Y2LTRlYzUtYmI3ZC0xZGM5NmYwNTg4ZmUiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6ImI1OGRhNTRlLWMwZWQtNDNhZC05ZGIzLTA3YmQ1MzQ2MTQ2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.CtN1i82azspwyUP_jjRvKDCdHLNXN6Mt8vaSt98wfHLIzrTJDYk2e2h2e-yh5YdO9ZLi8esnOHC1xIHmmLtd1D6NGkisouo0yXiGUG3HDuNHIcRbCxMNY30az1rBBpGjjjVAtglT-d3w0qF1d3kkCrJjjQFBv8n4H6Zg5Ri0dWDa0FudjvmzuKbYTV_yTmHFyQR2e2bgsZP2spcN883QxvB9OOBDTNP-2Ax7Ce1RphRWV4YEuUBKlqALQxiltaIH_IMTOj-6U8ArSJIGlvNlob7-qmXjpUNS2kFZ-jFlyhSKrPQnkMR2cb80M_E4Cjp767O6ex8PGmt8lVIP1YRWRw
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session    /productInventoryManagement/v1/product/${product_id}    headers=&{headers}
    Should Be Equal As Integers    ${response.status_code}    200
    ${json}=    To JSON    ${response.content}
    ${relationships}=    Get From Dictionary    ${json}    productRelationship

    # Ensure that the relationship ID at the given index is no longer present
    ${id_to_remove}=    Get From List    ${existing_relationships}    ${index}
    Should Not Contain    ${relationships}    ${id_to_remove}

Remove Relationship Expecting Failure
    [Arguments]    ${product_id}    ${index}
    ${operation}=    Create Dictionary    op=remove    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/${index}    value=
    @{body}=    Create List    ${operation}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    RETURN    ${response.status_code}

Remove Multiple Relationships
    [Arguments]    ${product_id}    ${index1}    ${index2}
    # Prepare the operations to remove two relationships at the specified indices
    ${operation1}=    Create Dictionary    op=remove    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/${index1}    value=
    ${operation2}=    Create Dictionary    op=remove    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/${index2}    value=
    @{body}=    Create List    ${operation1}    ${operation2}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    Should Be Equal As Integers    ${response.status_code}    200

Remove With Empty Path
    # This operation attempts to remove a relationship with an empty path, which should fail
    ${operation}=    Create Dictionary    op=remove    path=    value=
    @{body}=    Create List    ${operation}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    RETURN    ${response.status_code}

Create Request Headers
    # Create the request headers, including the authorization token and content type
    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Content-Type=application/json-patch+json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive
    RETURN    &{headers}

Create Full Product Tree For Test
    ${token}=    Get_token

    # Create a physical product using the API
    Get_token
    ${product_id}                               Post_valid_physical                 Post_body_request_for_physical_product

    # Attach atomic product offerings to the physical product
    ${Atomic_ProductOffering_id}                ${Spec_ProductOffering_id}          Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering
    ${Atomic_ProductOffering_id2}               ${Spec_ProductOffering_id2}         Post_for_attach_AtomicProductOffering             ${Product_id}[1]        Post_body_request_for_AtomicProductOffering

    # Patch the relationships (reliesOn and reliesFrom)
    patch_with_reliesOn_reliesFrom    ${Atomic_ProductOffering_id}     ${Product_id}[2]

    # Set product and offering statuses to Active or Sold
    patch_status    ${Product_id}[3]                    Sold        Sold
    patch_status    ${Product_id}[2]                    Sold        Sold
    patch_status    ${Spec_ProductOffering_id}          Active      Active
    patch_status    ${Atomic_ProductOffering_id}        Active      Active
    patch_status    ${Spec_ProductOffering_id2}         Active      Active
    patch_status    ${Atomic_ProductOffering_id2}       Active      Active
    patch_status    ${Product_id}[1]                    Active      Active
    patch_status    ${Product_id}[0]                    Active      Active

    # Create a dictionary to store product and offering IDs for easy reference
    ${ids}=    Create Dictionary
    ...    Product_id=${Product_id}
    ...    Atomic_ProductOffering_id=${Atomic_ProductOffering_id}
    ...    Atomic_ProductOffering_id2=${Atomic_ProductOffering_id2}
    ...    Spec_ProductOffering_id=${Spec_ProductOffering_id}
    ...    Spec_ProductOffering_id2=${Spec_ProductOffering_id2}

    RETURN    ${ids}

*** Test Cases ***

TC01 Remove Valid Relationship At Index 0 From Product_id_0
    Get_token
    # Create product and relationships for testing
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][0]}

    # Capture existing relationships
    ${existing_relationships}=    Verify Relationship Before Removal    ${product_id}

    # Remove the relationship at index 0
    Remove Relationship At Index    ${product_id}    0

    # Verify the relationship was removed successfully
    Verify Relationship Removed    ${product_id}    0    ${existing_relationships}
    Log    Successfully removed the relationship at index 0 from product ID ${product_id}.

TC02 Remove Invalid Relationship Index 999
    # Test removing a relationship at an invalid index (999)
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][0]}

    # Attempt to remove a relationship at an invalid index
    ${status}=    Remove Relationship Expecting Failure    ${product_id}    999

    # Verify that the status code indicates an error (400 or 404)
    Should Be True    ${status} in [400, 404]
    Log    Attempted to remove relationship at invalid index 999. Received status ${status}.

TC03 Remove Relationship From Nonexistent Product ID
    # Attempt to remove a relationship from a nonexistent product
    ${status}=    Remove Relationship Expecting Failure    nonexistent-id    0

    # Verify that the status code indicates an error (400 or 404)
    Should Be True    ${status} in [400, 404]
    Log    Attempted to remove relationship from nonexistent product ID. Received status ${status}.

TC04 Remove Multiple Relationships
    # Test removing multiple relationships at once
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][1]}

    # Capture existing relationships before removal
    ${existing_relationships}=    Verify Relationship Before Removal    ${product_id}

    # Remove the relationships at indices 2 and 3
    Remove Multiple Relationships    ${product_id}    2    3

    # Verify both relationships were removed
    Verify Relationship Removed      ${product_id}    2    ${existing_relationships}
    Verify Relationship Removed      ${product_id}    3    ${existing_relationships}
    Log    Successfully removed multiple relationships from product ID ${product_id} at indices 2 and 3.

TC05 Remove Relationship With Empty Path (Invalid)
    # Test removing a relationship with an empty path (invalid request)
    ${status}=    Remove With Empty Path

    # Verify that the status code indicates an error (500)
    Should Be True    ${status}     500
    Log    Attempted to remove relationship with empty path. Received status ${status}.

TC06 Remove Same Index Twice (Should Fail Second Time)
    # Test removing the same relationship twice (should fail the second time)
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][0]}

    # Capture existing relationships before removal
    ${existing_relationships}=    Verify Relationship Before Removal    ${product_id}

    # Remove the relationship at index 0
    Remove Relationship At Index     ${product_id}    0
    Verify Relationship Removed      ${product_id}    0    ${existing_relationships}

    # Attempt to remove the same relationship again and expect failure
    ${status}=    Remove Relationship Expecting Failure    ${product_id}    0
    Should Be True    ${status} in [400, 404]
    Log    Successfully removed the relationship at index 0, then failed to remove it again as expected. Status: ${status}.

TC07 Remove Invalid Relationship Index 999
    # Test removing a relationship at an invalid index (999)
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][0]}

    # Attempt to remove a relationship at an invalid index
    ${status}=    Remove Relationship Expecting Failure    ${product_id}    999

    # Verify that the status code indicates an error (400 or 404)
    Should Be True    ${status} in [400, 404]
    Log    Attempted to remove relationship at invalid index 999. Received status ${status}.

TC08 Remove Multiple Relationships Non-Sequential
    # Test removing multiple relationships that are not in sequential order
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][1]}

    # Capture existing relationships before removal
    ${existing_relationships}=    Verify Relationship Before Removal    ${product_id}

    # Remove the relationships at indices 4 and 1
    Remove Multiple Relationships    ${product_id}    3    1

    # Verify the relationship at index 4 was removed
    Verify Relationship Removed    ${product_id}    3    ${existing_relationships}

    # Verify the relationship at index 1 was removed
    Verify Relationship Removed    ${product_id}    1    ${existing_relationships}
    Log    Successfully removed the relationships at indices 3 and 1 from product ID ${product_id}.

TC09 Replace And Remove On Same Array Fails
    [Tags]  IPCEISCPIB-792
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][0]}

    ${replace_op}=    Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/1/relationshipType
    ...    value=test

    ${remove_op}=    Create Dictionary
    ...    op=remove
    ...    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/0

    @{body}=    Create List    ${replace_op}    ${remove_op}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    Should Be True    ${response.status_code} in [400, 500]
    Log    PATCH failed as expected due to replace/remove on same array.

TC10 Remove Without Value Should Pass
    [Tags]  IPCEISCPIB-793
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][1]}

    ${op}=    Create Dictionary
    ...    op=remove
    ...    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/0

    @{body}=    Create List    ${op}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    Should Be Equal As Integers    ${response.status_code}    200
    Log    Successfully removed item without value field in remove operation.

TC11 Remove Then Replace On Same Index
    [Tags]  IPCEISCPIB-792
    ${IDS}=    Create Full Product Tree For Test
    ${product_id}=    Set Variable    ${IDS['Product_id'][2]}
    log     ${product_id}

    ${remove_op}=    Create Dictionary
    ...    op=remove
    ...    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/1

    ${replace_op}=    Create Dictionary
    ...    op=replace
    ...    path=/productInventoryManagement/v1/product/${product_id}/productRelationship/1/relationshipType
    ...    value=reliesOn

    @{body}=    Create List    ${remove_op}    ${replace_op}
    &{headers}=    Create Request Headers
    Create Session    session    ${end-point-cpib}
    ${response}=    PATCH Request    session    /productInventoryManagement/v1/product    headers=&{headers}    json=${body}
    Should Be True    ${response.status_code}    200
    Log    Replace after remove on same index should fail or raise conflict.

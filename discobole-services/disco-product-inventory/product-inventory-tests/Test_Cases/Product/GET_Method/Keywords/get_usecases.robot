*** Settings ***
Resource            ../../../../Global_Configuration/Manage_Token.robot
Resource            ../../../../Global_Configuration/${Environnement_used}_Variables.robot

*** Variables ***
${space}            ' '  # Define the space if not already defined
${EMPTY_id}            ''   # Define the empty string if not already defined
*** Keywords ***

valid_get
    [Arguments]          ${id_value}       ${@type_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token super admin
    #${CPIB_TOKEN}=  Set Variable    eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id_value}          headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings         ${Return_response['id']}     ${id_value}
    Should Be Equal As Strings         ${Return_response['@type']}     ${@type_value}

valid_get_UC
    [Arguments]    ${id_value}    ${@type_value}

    # Generate a random string for log-tag
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate    valid-get-    ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}

    # Create headers for the request
    #${CPIB_TOKEN}=      Set Variable        eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzI0MTc3MjgsImlhdCI6MTc3MjM4MjExMSwiYXV0aF90aW1lIjoxNzcyMzgxNzI4LCJqdGkiOiIzNzlhYmRlOC1lOTU2LTRlODMtOGQ3YS0zOWI4ZjZjZmI1YWYiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiZ2F0ZXdheSJdLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi11aSIsInNpZCI6ImJmOWMwOWZiLTIxZWItNDY4OS05NTkwLTAyZTA5NTZmMWI2MiIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsInJlYWxtLWFkbWluIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJnYXRld2F5Ijp7InJvbGVzIjpbIkFkbWluUG9ydGFsUm9sZSIsIlByb2R1Y3QgQ2F0YWxvZyBBZG1pbiIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHJvZHVjdFNwZWNBZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluUmVhZCIsIlN1cGVyIEFjY2VzcyBSb2xlIiwiUHJvZHVjdE9mZmVyaW5nQWRtaW4iLCJPcmRlckNhcHR1cmVBZG1pbiIsIlJlYWRQcm9kdWN0T3JkZXIiLCJkaXNjby1hZG1pbiIsIkZhbGxvdXRJbmNpZGVudEFkbWluV3JpdGUiLCJQcm9kdWN0T2ZmZXJpbmdSb2xlIiwiUG9saWN5UnVsZVJvbGUiLCJSZWFkUHJvZHVjdCIsIlByb2R1Y3RPZmZlcmluZ1ByaWNlQWRtaW4iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJTdXBlciBBZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVpQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiU3VwZXIgQWRtaW4iLCJsb2NhbGUiOiJlbiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJhZG1pbi11aUBvcmFuZ2UuY29tIn0.B0uTUnr6YLxwSJqofGsHvHEKbHZ60JXQgi1znOUaFQ8ASSZikbdPKFYfvRHxZXi8AooZ-J07qDKqizqAqRnMfbX4HdXlkfEyP_HewxttwKB25aCarOJzzwf6Gtx50Zb4IZWbPHPX_9QLodHhei5LafJp0-f_SzCFI-Jev9W2WzhycqGu5Ux0Y6oQuIvBQIWbTsupWaDJIzXJuwgO2U63Wef12WqQUUAHwm-hRRUK48lu-S7gGU--GW95PCoyy8BnJq4qYIFbPLYaSR_X8uZY28MX1FRc_8fdhExpfldBjyYPjptX4sfmcdnQS2Is-VNUJmb1m_4eFc0un3oKe2oKUQ
    &{headers}=    Create Dictionary
    ...    Authorization=Bearer ${CPIB_TOKEN}
    ...    Content-Type=application/json
    ...    Accept=*/*
    ...    Accept-Encoding=gzip, deflate, br
    ...    Connection=keep-alive

    # Create a session and perform the GET request
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session    /productInventoryManagement/v1/product/${id_value}    headers=&{headers}

    # Ensure that the response status code is 200 or 206
    Should Be True    '${response.status_code}' == '200' or '${response.status_code}' == '206'

    # Parse the response text into JSON format
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json

    # Verify that 'id' and '@type' values match expected values
    Should Be Equal As Strings    ${Return_response['id']}    ${id_value}
    Should Be Equal As Strings    ${Return_response['@type']}    ${@type_value}

#    # Iterate over the product relationships
#    ${relationships}=    Get From Dictionary    ${Return_response}    productRelationship
#    Log    ${relationships}
#
#    # Initialize the index variable (set to 0 or any other initial value)
#    ${index}=    Set Variable    0
#
#    # Itérer sur chaque relation
#    FOR    ${relationship}    IN    ${relationships}
#
#        # Vérifier si 'relationship' est un dictionnaire et si 'relationshipType' existe
#        Run Keyword If    '${relationship}' is Dictionary    ${type}=    Get From Dictionary    ${relationship}    relationshipType
#        Run Keyword If    '${type}' == 'bundlesMigrate'
#            # Incrémenter l'index pour chaque correspondance
#            ${index}=    Set Variable    ${index + 1}
#            Log    ${index}
#
#        # Vérifier si 'relationship' est une liste
#        IF    '${relationship}' is List
#            FOR    ${item}    IN    ${relationship}
#                # Vérifier si l'élément est un dictionnaire et si 'relationshipType' existe
#                Run Keyword If    '${item}' is Dictionary    ${type}=    Get From Dictionary    ${item}    relationshipType
#                Run Keyword If    '${type}' == 'bundlesMigrate'
#                    # Incrémenter l'index pour chaque correspondance
#                    ${index}=    Set Variable    ${index + 1}
#                    Log    ${index}
#            END
#        END
#    END

    # Return the final index value
    RETURN    ${Return_response}


Get BundlesMigrate Index
    [Arguments]    ${json_object}
    ${relationships}=    Get From Dictionary    ${json_object}    productRelationship
    log         ${relationships}
    FOR    ${index}    ${relationship}    IN    ${relationships}
        ${type}=    Get From Dictionary    ${relationship}    relationshipType
        Run Keyword If    '${type}' == 'bundlesMigrate'        log      ${index}               #${index}=  Set Variable      ${index}
    END
    RETURN    ${index}

#Get Relationship Index
#    [Arguments]    ${relationships}    ${relationship_type}
#    ${i}=    Set Variable    0  # Default value
#
#    FOR    ${item}    IN    ${relationships}
#        # Get the productRelationship, which is a list of dictionaries
#        ${type}=    Get From Dictionary    ${item}    productRelationship
#        Log    ${type}  # Log the value of type to see its structure
#
#        # Check if ${type} is a list (since it should be)
#        FOR    ${sub_item}    IN    ${type}
#            Log    ${sub_item}  # Log the current sub_item list to check its contents
#
#            # Check if ${sub_item} is a list and its length
#            ${len_sub_item}=    Get Length    ${sub_item}
#            Log    ${len_sub_item}  # Log the length to ensure it's a list of the correct size
#
#            # If ${sub_item} is a list of dictionaries, extract the first dictionary
#            #Run Keyword If    '${len_sub_item}' == '1'    ${sub_item}=    Get From List    ${sub_item}    0
#            ${sub_item}=    Get From List    ${sub_item}    0
#            Log    ${sub_item}
#            # Now, make sure ${sub_item} is a dictionary before accessing the relationshipType
#            #IF    '${sub_item}' is a dictionary
#            ${type2}=    Get From Dictionary    ${sub_item}    relationshipType
#            Log    ${type2}
#
#            # Extract the value based on ${relationship_type} key
##            ${type3}=    Get From Dictionary    ${sub_item}    ${relationship_type}
##            Log    ${type3}
#
#            # Check if the extracted relationship type matches
#            Run Keyword If    '${type2}' == '${relationship_type}'      Set Variable    ${i}    ${i}+1
#            #END
#        END
#    END
#
#    [Return]    ${i}

Get Relationship Index
    [Arguments]    ${relationships}    ${relationship_type}
    ${i}=    Set Variable    0  # Initialisation du compteur à 0
    ${found}=    Set Variable    False  # Flag pour savoir si nous avons trouvé 'bundlesMigrate'

    # Parcours des éléments dans ${relationships}
    FOR    ${item}    IN    ${relationships}
        # Récupérer le champ 'productRelationship' de chaque item
        ${type}=    Get From Dictionary    ${item}    productRelationship
        Log    ${type}  # Afficher le contenu de ${type} pour déboguer

        # Vérifier que ${type} est bien une liste
        FOR    ${sub_item}    IN    ${type}
            Log    ${sub_item}  # Afficher le contenu de chaque ${sub_item} pour vérifier la structure

            # Vérifier la longueur de chaque sous-élément
            ${len_sub_item}=    Get Length    ${sub_item}
            Log    ${len_sub_item}  # Afficher la longueur pour déboguer

            # Si le sous-élément est une liste, boucle pour chaque élément dans ${sub_item}
            IF    '${len_sub_item}' != '1' AND '${found}' == 'False'
                # Si ${sub_item} est une liste avec plus d'un élément, effectuer une nouvelle boucle sur cette liste
                FOR    ${inner_sub_item}    IN    ${sub_item}
                    Log    ${inner_sub_item}  # Afficher l'élément interne pour vérification
                    # Extraire le type de relation de chaque sous-élément interne
                    ${type2}=    Get From Dictionary    ${inner_sub_item}    relationshipType
                    Log    ${type2}

                    # Vérifier si le type de relation est 'bundlesMigrate'
                    Run Keyword If    '${type2}' == 'bundlesMigrate'
                        Set Variable    ${i}    ${i}+1  # Incrémenter le compteur
                        Set Variable    ${found}    True  # Marquer que nous avons trouvé 'bundlesMigrate'
                END
            END
            Run Keyword If    '${found}' == 'True'    Set Variable    ${i}    ${i}  # Assurer que nous avons trouvé
            # Si ce n'est pas une liste, traiter directement le champ
            Run Keyword If    '${len_sub_item}' == '1' AND '${found}' == 'False'
                ${type2}=    Get From Dictionary    ${sub_item}    relationshipType
                Log    ${type2}  # Afficher le type de relation extrait

                # Vérifier si le type de relation est 'bundlesMigrate'
                Run Keyword If    '${type2}' == 'bundlesMigrate'
                    Set Variable    ${i}    ${i}+1  # Incrémenter le compteur
                    Set Variable    ${found}    True  # Marquer que nous avons trouvé 'bundlesMigrate'
        END

        Run Keyword If    '${found}' == 'True'    Set Variable    ${i}    ${i}  # Assurer que nous avons trouvé
    END

    # Retourner l'index si trouvé, sinon 0
    Run Keyword If    '${found}' == 'True'    Set Variable    ${i}    ${i}
    RETURN    ${i}



valid_filtering
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   valid-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}           headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'


invalid_get
    [Arguments]          ${id_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-get-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /product/${id_value}           headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        NOT_FOUND
    Should Be Equal As Strings      ${Return_response['code']}          ${code_60}
    Should Be Equal As Strings      ${Return_response['reason']}        ${notfound_reason}
    Should Be Equal As Strings      ${Return_response['message']}       The requested URI or the requested resource does not exist.
    #The product with id ${id_value} does not exist

invalid_filtering
    [Arguments]         ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   invalid-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}         headers=&{headers}           #tag_test=${log-tag}
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    IF      '${field_value}' == '${EMPTY}'
        Should Be True  '${response.status_code}'=='400'
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       ${field} must not be empty
    ELSE
        Should Be True  '${response.status_code}'=='200'
        Should Be Equal As Strings      ${Return_response}          []
    END

Unsupported_filtering
    [Arguments]         ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   Unsupported-filtering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}         headers=&{headers}           #tag_test=${log-tag}
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be True  '${response.status_code}'=='400'
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       Unsupported filter parameter: ${field}

empty_get
    [Arguments]          ${id}
    #Token Super Admin
    #${CPIB_TOKEN}=      Set Variable        eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
    Should Contain         ${response.text}        NOT_FOUND

get_valid_relatedParty
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-relatedParty-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?relatedParty.${field}=${field_value}&limit=10            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}[0:3]
#        Should Be Equal As Strings          ${object["relatedParty"][0]["${field}"]}        ${field_value}
#    END
get_invalid_relatedParty
    [Arguments]          ${field}       ${field_value}         ${code_verif}       ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-@type-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?relatedParty.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}


get_valid_fields
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-relatedParty-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}&limit=10            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}[0:3]
#        Should Be Equal As Strings          ${object["relatedParty"][0]["${field}"]}        ${field_value}
#    END

get_valid_productOffering
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-productOffering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOffering.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productOffering"]["${field}"]}        ${field_value}
#    END

get_valid_productSpecification
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-productOffering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productSpecification.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productSpecification"]["${field}"]}        ${field_value}
#    END

get_valid_with_@Type
    [Arguments]                ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-@type-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?@type=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
    FOR    ${object}    IN    @{Return_response}
        Should Be Equal As Strings          ${object["@type"]}        ${field_value}
    END

get_valid_with_isRoot_equal_true
   [Arguments]                ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-isRoot-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?isRoot=${field_value}          headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
    IF    '${field_value}' == 'true'
        Should Not Contain     ${response.text}            "@type": "AtomicProductOffering"
#    ELSE
#        Should Contain          ${response.text}            "@type":"Contract"
    END

get_valid_with_isRoot_equal_false
   [Arguments]                ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-isRoot-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?isRoot=${field_value}          headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
    IF    '${field_value}' == 'false'
        Should Not Contain      ${response.text}            "@type":"Contract"
    END

get_with_bad_isRoot
    [Arguments]                ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-isRoot-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?isRoot=${field_value}          headers=&{headers}         #
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF    '${field_value}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       isRoot must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       isRoot value is not a valid type
    END

get_valid_productOrderItem
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-productOrderItem-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productOrderItem"][0]["${field}"]}        ${field_value}
#    END

get_valid_name_product
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-product-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["${field}"]}        ${field_value}
#    END

get_bad_name_product
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-product-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response}          []

get_empty_name_product
    [Arguments]          ${field}       ${field_value}          ${code_verif}      ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-name-product_  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_valid_relationshipType
    [Arguments]          ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-relationshipType-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productRelationship.relationshipType=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}

get_relationshipType_productId
    [Arguments]          ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-relationshipType-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productRelationship.product.id=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    IF  '${field_value}' == 'worng_id'
#       Should Be Equal As Strings      ${Return_response}          []
#    ELSE
#        Should Not be empty     ${Return_response}
#    END

get_relationshipType_bad_productId
    [Arguments]          ${field_value}             ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-valid-relationshipType-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productRelationship.product.id=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_bad_relatedParty
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-relatedParty-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?relatedParty.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response}          []

get_empty_relatedParty
    [Arguments]          ${field}       ${field_value}      ${code_verif}       ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-relatedParty-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?relatedParty.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_bad_@Type
    [Arguments]          ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-@type-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?@type=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response}          []

get_empty_@Type
    [Arguments]          ${field_value}         ${code_verif}       ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-@type-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}   Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?@type=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_bad_productOffering
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-productOffering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOffering.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response}          []

get_bad_productSpecification
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-productSpecification-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productSpecification.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response}          []


get_empty_productOffering
    [Arguments]          ${field}       ${field_value}           ${code_verif}      ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-productOffering-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOffering.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_empty_productSpecification
    [Arguments]          ${field}       ${field_value}          ${code_verif}      ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-productSpecification-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productSpecification.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_bad_productOrderItem
    [Arguments]          ${field}       ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-productOrderItem-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.${field}=${field_value}            headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Be Equal As Strings      ${Return_response}          []

get_empty_productOrderItem
    [Arguments]          ${field}       ${field_value}          ${code_verif}           ${reason-verif}             ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-empty-productOrderItem-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productOrderItem.${field}=${field_value}            headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Not Be Empty     ${Return_response}
    Should Be True  '${response.status_code}'=='400'
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_bad_relationshipType_Allowed
    [Arguments]          ${field_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-relationshipType-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productRelationship.relationshipType=${field_value}            headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be True    ${response.status_code} in [200, 206]    # This checks if the status code is either 200 or 206


get_bad_relationshipType
    [Arguments]          ${field_value}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-relationshipType-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}     Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productRelationship.relationshipType=${field_value}            headers=&{headers}
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be True  '${response.status_code}'=='400'
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_field
    [Arguments]          ${field}    ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_field
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "href"

get_with_bad_status
    [Arguments]          ${status}          ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?status=${status}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_operationalStatus
    [Arguments]          ${operationalStatus}          ${code_verif}   ${reason-verif}     ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-bad-operationalStatus-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?operationalStatus=${operationalStatus}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.content}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_status
    [Arguments]          ${status}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?status=${status}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    IF      '${response.text}' == '[]'
        Should Contain    ${response.text}     []
    ELSE
        ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
        ${modified_json_objects}    Create List
        FOR    ${object}    IN    @{json_data}
            ${modified_object}    Create Dictionary        id=${object['id']}    Status=${object['status']}
            Append To List    ${modified_json_objects}    ${modified_object}
        END
        Should Be Equal As Strings      ${modified_json_objects[0]["Status"]}                ${status}
    END

get_with_exist_status
    [Arguments]          ${status}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-exist-status-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?status=${status}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
#    ${modified_json_objects}    Create List
#    FOR    ${object}    IN    @{json_data}
#        ${modified_object}    Create Dictionary        id=${object['id']}    Status=${object['status']}
#        Append To List    ${modified_json_objects}    ${modified_object}
#    END
#    Should Be Equal As Strings      ${modified_json_objects[0]["Status"]}                ${status}

get_with_operationalStatus
    [Arguments]          ${operationalStatus}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-operationalStatus-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?operationalStatus=${operationalStatus}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    IF      '${response.text}' == '[]'
        Should Contain    ${response.text}     []
    ELSE
        ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
        ${modified_json_objects}    Create List
        FOR    ${object}    IN    @{json_data}
            ${modified_object}    Create Dictionary        id=${object['id']}    operationalStatus=${object['operationalStatus']}
            Append To List    ${modified_json_objects}    ${modified_object}
        END
        Should Be Equal As Strings      ${modified_json_objects[0]["operationalStatus"]}                ${operationalStatus}
    END

get_partial_with_operationalStatus
    [Arguments]          ${operationalStatus}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-partial-with-operationalStatus-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?operationalStatus=${operationalStatus}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    IF      '${response.text}' == '[]'
        Should Contain    ${response.text}     []
    ELSE
        ${json_data}                Evaluate                    json.loads('''${response.text}''')    json
        ${modified_json_objects}    Create List
        FOR    ${object}    IN    @{json_data}
            ${modified_object}    Create Dictionary        id=${object['id']}    operationalStatus=${object['operationalStatus']}
            Append To List    ${modified_json_objects}    ${modified_object}
        END
        Should Be Equal As Strings      ${modified_json_objects[0]["operationalStatus"]}                ${operationalStatus}
    END

get_one_with_bad_field
    [Arguments]          ${id}          ${field}        ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-bad-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}?fields=${field}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}        400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_all_products
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-all-products-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product     headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id":"
    Should Contain         ${response.text}        "href"

    # count data
    ${data}=    To JSON    ${response.text}
    ${count}=   Get Length    ${data}
    Log    ${count} Products

get_all_tangible_products
    [Arguments]          ${@type_value}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-all-products-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?@type=${@type_value}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    #Should Contain         ${response.text}        "id":"
    #Should Contain         ${response.text}        "href"
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR    ${object}    IN    @{Return_response}
        Should Be Equal As Strings          ${object["@type"]}       ${@type_value}
    END

get_one_with_field
    [Arguments]          ${id}          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}?fields=${field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id":"${id}"
    Should Contain         ${response.text}        "href"

get_with_nested_field
    [Arguments]          ${field}     ${nested_field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-nested-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}.${nested_field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    Should Contain         ${response.text}        "id":
#    Should Contain         ${response.text}        "href"
#    IF     '${field}' == 'productCharacteristic'
#        Should Contain         ${response.text}        "${field}"
#        Should Contain         ${response.text}        "${nested_field}"
#    ELSE
#        Should Contain         ${response.text}        "${field}":{"${nested_field}":
#    END


get_with_null_field
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-null-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "href"

get_with_specific_field
    [Arguments]          ${field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Contain         ${response.text}        "id"
    Should Contain         ${response.text}        "href"
    Should Contain         ${response.text}        "${field}"

get_with_bad_nested_field
    [Arguments]          ${field}       ${nested_field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-nested-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?fields=${field}.${nested_field}     headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${field}.${nested_field} not included in product fields

get_without_fields
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-without-fields-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product    headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    Should Not Be Equal As Strings        ${response.text}        []

get_with_offset
    [Arguments]          ${offset}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?offset=${offset}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

get_with_bad_parameter
    [Arguments]          ${parameter}          ${code_verif}           ${reason-verif}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-parameter-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${parameter}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_verif}
    Should Be Equal As Strings      ${Return_response['reason']}        ${reason-verif}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_bad_offset_or_limit
    [Arguments]          ${offset}     ${limit}         ${message_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-bad-parameter-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?offset=${offset}&limit=${limit}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_with_limit
    [Arguments]          ${limit}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-limit-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?limit=${limit}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

get_with_offset_&_limit
    [Arguments]          ${offset}          ${limit}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-with-offset-and-limit-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}      Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?offset=${offset}&limit=${limit}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${object_count}    Get Length    ${Return_response}
    Log    Number of objects: ${object_count}
    Should Be Equal As Strings    ${object_count}         ${limit}

get_one_product_with_field
    [Arguments]          ${id}          ${fields}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}?fields=${fields}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['id']}        ${id}
    Should Be Equal As Strings      ${Return_response['href']}      ${end-point-cpib}/productInventoryManagement/v1/product/${id}

get_one_product_with_specific_field
    [Arguments]          ${id}          ${specific_field}           ${value_verif}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}?fields=${specific_field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['id']}                        ${id}
    Should Be Equal As Strings      ${Return_response['href']}                      ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    Should Be Equal As Strings      ${Return_response['${specific_field}']}         ${value_verif}

get_one_product_with_specific_field_2
    [Arguments]          ${id}          ${specific_field}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   get-one-with-specific-field-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    # Token Super Admin
    #${CPIB_TOKEN}=      Set Variable     eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMTM4MTksImlhdCI6MTc3MjE3NzgxOSwianRpIjoiYjM5OGJkOGMtYjZmNy00YjkyLTk2ZDAtZTMwMDAyYWEwNzA4IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJzdWIiOiJkZDYzNmQ3Yi02ZjQ0LTRiNGMtOWZjMy01MTIyNzYwZTIxYTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJnYXRld2F5Iiwic2lkIjoiMjdmMzEyOGYtNjQ0ZS00NjU2LWJkNmEtNDliNGZkZGJlNTA1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2Rpc2NvLWdhdGV3YXktc3RhZ2luZy1kaXNjby5hcHBzLmZyMDEucGFhcy50ZWNoLm9yYW5nZSJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiZ2F0ZXdheSI6eyJyb2xlcyI6WyJBZG1pblBvcnRhbFJvbGUiLCJQcm9kdWN0IENhdGFsb2cgQWRtaW4iLCJPcmNoZXN0cmF0aW9uUGxhbnNBZG1pbiIsIlByb2R1Y3RTcGVjQWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pblJlYWQiLCJTdXBlciBBY2Nlc3MgUm9sZSIsIlByb2R1Y3RPZmZlcmluZ0FkbWluIiwiT3JkZXJDYXB0dXJlQWRtaW4iLCJSZWFkUHJvZHVjdE9yZGVyIiwiZGlzY28tYWRtaW4iLCJGYWxsb3V0SW5jaWRlbnRBZG1pbldyaXRlIiwiUHJvZHVjdE9mZmVyaW5nUm9sZSIsIlBvbGljeVJ1bGVSb2xlIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0T2ZmZXJpbmdQcmljZUFkbWluIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbi11aUBvcmFuZ2UuY29tIiwiZ2l2ZW5fbmFtZSI6IlN1cGVyIEFkbWluIiwibG9jYWxlIjoiZW4iLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiYWRtaW4tdWlAb3JhbmdlLmNvbSJ9.y4jYj65FQE4GXbfvQNlo28EvMdK47CWIL9lbq3XFYEALqX4Aa5OKVmEqkyiJ_s1XLwr9JGnoPF2z8hTfo_2Po6JC7JnVJO337knDkuOTyenkbA8gxVMeivRFQAeuxERKxEvNHOfrkcf6-t7D9cYxdf6zIS1LMmJ7mBzgtx8XEok5rIJQ8LdPGhw6-LLIsxIs1kk1tO8bcbOE8i7bwzODH0xqHYGXolltxe_4BLvTYZ965JALNq-HUGaL2s1epMo2FkpohAy0V-TDwiIByPAm3MKZxXFlLA22wj-EcD1kT_wQvo3Rd3rAVpmy0mSbB9WEE3sw6eNzJTJzWMwp23kT2A
    &{headers}=    Create Dictionary      Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product/${id}?fields=${specific_field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['id']}                        ${id}
    Should Be Equal As Strings      ${Return_response['href']}                      ${end-point-cpib}/productInventoryManagement/v1/product/${id}
    Should Not Be Empty            ${Return_response['${specific_field}']}


get_with_date
    [Arguments]    ${date_filed}      ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}=${date_input}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${product}  IN  @{Return_response}
        ${actual_value}=  Set Variable  ${product}[${date_filed}]
        Should Be Equal As Strings  ${actual_value}  ${date_input}
    END

Invalid_get_with_date
    [Arguments]    ${date_filed}      ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}=${date_input}      headers=&{headers}
    Should Be True  '${response.status_code}'=='400'

get_anded_with_2_dates
    [Arguments]      ${date_filed}     ${min_date_input}      ${max_date_input}          ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${min_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${min_date_input}''', '${format}').timestamp()
    ${max_timestamp_input}=    Evaluate    datetime.datetime.strptime('''${max_date_input}''', '${format}').timestamp()
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    FOR  ${product}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${product}[${date_filed}]
        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
        # for MIN
        ${difference_with_min}           Evaluate    ${timestamp_actual} - ${min_timestamp_input}
        ${difference_with_min}           Convert To Integer    ${difference_with_min}
        ${is_greater_min}                BuiltIn.Evaluate     ${difference_with_min} >= 0
        Should be True      ${is_greater_min}
        # for MAX
        ${difference_with_max}           Evaluate    ${timestamp_actual} - ${max_timestamp_input}
        ${difference_with_max}           Convert To Integer    ${difference_with_max}
        ${is_greater_max}                BuiltIn.Evaluate     ${difference_with_max} <= 0
        Should be True      ${is_greater_max}
    END

Invalid_get_anded_with_2_dates
    [Arguments]      ${date_filed}     ${min_date_input}      ${max_date_input}          ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}      headers=&{headers}
    Should Be True  '${response.status_code}'=='400'

bad_get_anded_with_2_dates
    [Arguments]     ${date_filed}      ${min_date_input}      ${max_date_input}          ${format}        ${op}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   bad-get-anded-with-tow-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}.gte=${min_date_input}&${date_filed}.lte=${max_date_input}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${min_date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE IF     '${min_date_input}' == '${NULL}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    ELSE IF     '${max_date_input}' == '${NULL}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    ELSE IF     '${max_date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    END

get_with_bad_date
    [Arguments]      ${date_field}    ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-bad-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_field}=${date_input}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_field} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_field} value is not a valid type
    END

get_with_op_bad_date
    [Arguments]    ${date_filed}         ${op}       ${op_diff}       ${date_input}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-op-bad-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}.${op}=${date_input}      headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF  '${date_input}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       ${date_filed}.${op} value is not a valid type
    END

get_with_op_date
    [Arguments]    ${date_filed}     ${op}       ${op_diff}       ${date_input}      ${format}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-op-and-date-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?${date_filed}.${op}=${date_input}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    ${timestamp_input}=    Evaluate    datetime.datetime.strptime('''${date_input}''', '${format}').timestamp()
    FOR  ${product}  IN  @{Return_response}[0:3]
        ${actual_value}         Set Variable  ${product}[${date_filed}]
        ${timestamp_actual}     Evaluate    datetime.datetime.strptime('''${actual_value}''', '%Y-%m-%dT%H:%M:%SZ').timestamp()
        ${difference}           Evaluate    ${timestamp_actual} - ${timestamp_input}
        ${difference}           Convert To Integer    ${difference}
        ${is_greater}           BuiltIn.Evaluate     ${difference} ${op_diff} 0
        Should be True      ${is_greater}
    END

get_with_sorting
    [Arguments]    ${Sort-Direction}         ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?sort=${Sort-Direction}${Sort-Field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Not be empty     ${Return_response}

get_invalid_sorting
    [Arguments]    ${Sort-Field}        ${message_verif}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-invalid-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?sort=${Sort-Field}&=${log-tag}    headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    400
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    Should Be Equal As Strings      ${Return_response['message']}       ${message_verif}

get_duplicated_sorting
    [Arguments]    ${Sort-Field}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-duplicated-sorting-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?sort=-${Sort-Field},${Sort-Field}      headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'

#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
#    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
#    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
#    Should Be Equal As Strings      ${Return_response['message']}       sort value is not a valid type

Check_terminationDate
    [Arguments]          ${id_value}        ${diff_value}      ${unit_value}        ${unittype}
    ${random_number}=    Generate Random String    2    123456789
    ${log-tag}=    Catenate   Check-terminationDate-  ${random_number}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /product/${id_value}           headers=&{headers}
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
    ${startDate_value}                      Evaluate    json.loads('''${response.content}''')['startDate']
    ${terminationDate_value}               Evaluate    json.loads('''${response.content}''')['terminationDate']
    ${date1_obj}    Convert Date    ${terminationDate_value}    result_format=%Y-%m-%dT%H:%M:%S
    ${date2_obj}    Convert Date    ${startDate_value}    result_format=%Y-%m-%dT%H:%M:%S
    ${timestamp_termination}         Evaluate        datetime.datetime.strptime('''${date1_obj}''', '%Y-%m-%dT%H:%M:%S').timestamp()
    ${timestamp_start}         Evaluate        datetime.datetime.strptime('''${date2_obj}''', '%Y-%m-%dT%H:%M:%S').timestamp()
    ${diff}    Evaluate         int(${timestamp_termination} - ${timestamp_start})
    Should Be Equal As Strings    ${diff}       ${diff_value}
    RETURN             ${timestamp_termination}

get_productCharacteristic
    [Arguments]    ${productCharacteristic_field}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.${productCharacteristic_field}=${productCharacteristic_value}    headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        ${length}=    Get Length    ${object["productCharacteristic"]}
#        IF    ${length} != 1
#            Should Be Equal As Strings          ${object["productCharacteristic"][${length}-1]["${productCharacteristic_field}"]}        ${productCharacteristic_value}
#        ELSE
#            Should Be Equal As Strings          ${object["productCharacteristic"][0]["${productCharacteristic_field}"]}        ${productCharacteristic_value}
#        END
#    END

get_bad_productCharacteristic
    [Arguments]    ${productCharacteristic_name}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-bad-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.${productCharacteristic_name}=${productCharacteristic_value}    headers=&{headers}          #
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    IF    '${productCharacteristic_value}' == 'worng_value'
        Should Be True  '${response.status_code}'=='200'
        Should be empty     ${Return_response}
        Should Be Equal As Strings         ${Return_response}       []
    ELSE
        Should Be True  '${response.status_code}'=='400'
        Should not be empty     ${Return_response}
        ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
        Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
        Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
        Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
        Should Be Equal As Strings      ${Return_response['message']}       productCharacteristic.${productCharacteristic_name} must not be empty
    END

get_valid_productCharacteristic
    [Arguments]    ${productCharacteristic_name}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-valid-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.name=${productCharacteristic_name}&productCharacteristic.value=${productCharacteristic_value}   headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productCharacteristic"][0]["name"]}        ${productCharacteristic_name}
#        Should Be Equal As Strings          ${object["productCharacteristic"][0]["value"]}        ${productCharacteristic_value}
#    END

get_incomplete_productCharacteristic
    [Arguments]    ${productCharacteristic_name}         ${productCharacteristic_value}          ${productCharacteristic_complete_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-valid-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.name=${productCharacteristic_name}&productCharacteristic.value=${productCharacteristic_value}   headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productCharacteristic"][0]["name"]}        ${productCharacteristic_name}
#        Should Be Equal As Strings          ${object["productCharacteristic"][0]["value"]}       ${productCharacteristic_complete_value}
#    END

get_invalid_productCharacteristic
    [Arguments]    ${productCharacteristic_name}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-valid-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.name=${productCharacteristic_name}&productCharacteristic.value=${productCharacteristic_value}    headers=&{headers}         #
    Should Be True  '${response.status_code}'=='200'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should be empty     ${Return_response}
    Should Be Equal As Strings         ${Return_response}       []

get_empty_productCharacteristic
    [Arguments]    ${productCharacteristic_name}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-valid-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.name=${productCharacteristic_name}&productCharacteristic.value=${productCharacteristic_value}   headers=&{headers}             #
    Should Be True  '${response.status_code}'=='400'
    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
    IF    '${productCharacteristic_name}' == '${EMPTY}'
        Should Be Equal As Strings      ${Return_response['message']}       productCharacteristic.name must not be empty
    ELSE
        Should Be Equal As Strings      ${Return_response['message']}       productCharacteristic.value must not be empty
    END

get_missing_productCharacteristic
    [Arguments]    ${productCharacteristic_field}         ${productCharacteristic_value}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   get-with-valid-productCharacteristic-  ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Content-Type=application/json        Accept=*/*     Authorization=Bearer ${CPIB_TOKEN}    Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /productInventoryManagement/v1/product?productCharacteristic.${productCharacteristic_field}=${productCharacteristic_value}    headers=&{headers}             #
    Should Be True  '${response.status_code}'=='200' or '${response.status_code}'=='206'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Not be empty     ${Return_response}
#    FOR    ${object}    IN    @{Return_response}
#        Should Be Equal As Strings          ${object["productCharacteristic"][0]["${productCharacteristic_field}"]}        ${productCharacteristic_value}
#    END
#    Should Be True  '${response.status_code}'=='400'
#    ${Return_response}=    Evaluate    json.loads('''${response.text}''')    json
#    Should Be Equal As Strings      ${Return_response['status']}        BAD_REQUEST
#    Should Be Equal As Strings      ${Return_response['code']}          ${code_28}
#    Should Be Equal As Strings      ${Return_response['reason']}        ${invalid_paramter_reason}
#    Should Be Equal As Strings      ${Return_response['message']}       Both productCharacteristicValue and productCharacteristicName must either be included or excluded.

check_lastUpdateDate
    [Arguments]        ${id}
    ${uuid}=       Evaluate    uuid.uuid4()    modules=uuid
    ${log-tag}=    Catenate   patch-status-     ${uuid}
    ${log-tag}=    Replace String    ${log-tag}    ${space}    ${empty}
    &{headers}=    Create Dictionary       Authorization=Bearer ${CPIB_TOKEN}       Content-Type=application/json        Accept=*/*      Accept-Encoding=gzip, deflate, br       Connection=keep-alive
    Create Session    session    ${end-point-cpib}
    ${response}=    GET Request    session  /product/${id}             headers=&{headers}
    Run Keyword And Continue On Failure    Should Be Equal As Strings    ${response.status_code}    404
#    ${current_date}=    Get Current Date
#    ${one_hour_earlier}=    Subtract Time From Date    ${current_date}    1 hour
#    ${lastUpdateDate_value}    Convert Date    ${one_hour_earlier}    result_format=%Y-%m-%dT%H:%M:%S
#    Should Contain         ${response.text}          "lastUpdateDate":"${lastUpdateDate_value}Z"





*** Settings ***
Resource        ../Keywords/get_usecases.robot

*** Test Cases ***

Get_Product_By_valid_relatedParty_id
    [Tags]  Get_Method
    #Get_token
    get_valid_relatedParty              partyOrPartyRole.id          ${Valid_relatedParty_id}

Get_Product_By_valid_relatedParty_partyOrPartyRole.@type
    [Tags]  Get_Method
    Get_token
    get_invalid_relatedParty              @type          RelatedPartyRefOrPartyRoleRef          ${code_28}          ${invalid_paramter_reason}          Unsupported filter parameter: relatedParty.@type

Get_Product_By_valid_relatedParty_partyOrPartyRole.id
    [Tags]  Get_Method
    Get_token
    get_valid_relatedParty              partyOrPartyRole.id          xkwpqr8s

Get_Product_By_valid_relatedParty_partyOrPartyRole.name
    [Tags]  Get_Method
    Get_token
    get_valid_relatedParty              partyOrPartyRole.name          Joe Doe

Get_Product_By_valid_relatedParty_partyOrPartyRole.@referredType
    [Tags]  Get_Method
    Get_token
    get_valid_relatedParty              partyOrPartyRole.@referredType         Individual

Get_Product_By_valid_relatedParty_partyOrPartyRole.partyId
    [Tags]  Get_Method
    Get_token
    get_valid_relatedParty              partyOrPartyRole.partyId          111

Get_Product_By_valid_relatedParty_partyOrPartyRole.partyName
    [Tags]  Get_Method
    Get_token
    get_valid_relatedParty              partyOrPartyRole.partyName          Jean

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.@type
    [Tags]  Get_Method
    Get_token
    get_valid_fields             fields          relatedParty.partyOrPartyRole.@type

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.id
    [Tags]  Get_Method
    Get_token
    get_valid_fields               fields     relatedParty.partyOrPartyRole.id

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.name
    [Tags]  Get_Method
    Get_token
    get_valid_fields               fields         relatedParty.partyOrPartyRole.name

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.@referredType
    [Tags]  Get_Method
    Get_token
    get_valid_fields               fields     relatedParty.partyOrPartyRole.@referredType

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.partyId
    [Tags]  Get_Method
    Get_token
    get_valid_fields               fields         relatedParty.partyOrPartyRole.partyId

Get_Product_By_valid_relatedParty_fields_partyOrPartyRole.partyName
    [Tags]  Get_Method
    Get_token
    get_valid_fields               fields     relatedParty.partyOrPartyRole.partyName

Get_Product_By_nonExistent_relatedParty_id
    [Tags]  Get_Method    bug_IPCEISCPIB-4167 
    #Get_token
    get_bad_relatedParty                partyOrPartyRole.id          worng_id

Get_Product_By_empty_relatedParty_id
    Get_token
    get_empty_relatedParty              partyOrPartyRole.id          ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        relatedParty.partyOrPartyRole.id must not be empty

#Get_Product_By_valid_relatedParty_name
#    Get_token
#    get_valid_relatedParty              partyOrPartyRole.name        lisa

Get_Product_By_nonExistent_relatedParty_name
    Get_token
    get_bad_relatedParty                partyOrPartyRole.name        worng_name


Get_Product_By_empty_relatedParty_name
    Get_token
    get_empty_relatedParty              partyOrPartyRole.name        ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        relatedParty.partyOrPartyRole.name must not be empty

#Get_Product_By_valid_relatedParty_role
#    Get_token
#    get_valid_relatedParty              partyOrPartyRole.role        customer

Get_Product_By_nonExistent_relatedParty_role
    Get_token
    get_bad_relatedParty                partyOrPartyRole.role        worng_role

Get_Product_By_empty_relatedParty_role
    Get_token
    get_empty_relatedParty              partyOrPartyRole.role        ${EMPTY}        ${code_28}       ${invalid_paramter_reason}        relatedParty.partyOrPartyRole.role must not be empty

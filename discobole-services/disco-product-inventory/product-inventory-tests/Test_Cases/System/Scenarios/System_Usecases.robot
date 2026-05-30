*** Settings ***
Resource        ../Keywords/system_usecases.robot

*** Test Cases ***

Check_Status
    [Tags]  IPCEISCPIB-2231
    Get_token
    get_with_status      status          ${CPIB_serviceName}         ${CPIB_version}             Service is up and running
    
Check_Version
    [Tags]  IPCEISCPIB-2232
    get_with_version    version          ${CPIB_version}

Check_Doc
    [Tags]  IPCEISCPIB-2233
    get_with_doc         doc

Check_Configurations
    [Tags]  IPCEISCPIB-2234
    Get_token
    get_with_conf        configuration

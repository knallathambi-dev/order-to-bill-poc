*** Settings ***
Resource            Global_Variables.robot
Resource            Global_keywords.robot

*** Variables ***

${KEYCLOAK_URL}                         %{KEYCLOAK_URL=${KC_URL_Value}}
${USERNAME}                             %{CPIB_USER=${KC_Username_Value}}
${PASSWORD}                             %{CPIB_PASSWORD=${KC_Password_Value}}
${CLIENT_ID}                            %{AUTH_CLIENT_ID=gateway}
${client_secret}                        %{AUTH_CLIENT_SECRET=${KC_ClientSecret_Value}}

*** Keywords ***

Get_token
    log         ${USERNAME}
    
    # Set proxy environment variables for Integration
    #Run Keyword If    '${Environnement_used}' == 'Integration'
    #...    Set Environment Variable    HTTP_PROXY    http://proxyparfil.si.fr.intraorange:8080
    #Run Keyword If    '${Environnement_used}' == 'Integration'
    #...    Set Environment Variable    HTTPS_PROXY    http://proxyparfil.si.fr.intraorange:8080
    #Run Keyword If    '${Environnement_used}' == 'Integration'
    #...    Set Environment Variable    NO_PROXY    localhost,127.0.0.1
    
    # Create session without explicit proxies parameter (will use environment variables)
    Create Session    keycloak    ${KEYCLOAK_URL}    verify=False    disable_warnings=1
    
    ${headers}=    Create Dictionary
    ...     Content-Type=application/x-www-form-urlencoded
    ${data}=    Create Dictionary
    ...     grant_type=password
    ...     username=${USERNAME}
    ...     password=${PASSWORD}
    ...     scope=openid
    ...     client_id=${CLIENT_ID}
    ...     client_secret=${client_secret}
    ${response}=    Post Request    keycloak    /realms/SpringBootKeycloak/protocol/openid-connect/token
    ...     headers=${headers}    data=${data}
    Should Be Equal As Strings    ${response.status_code}    200
    ${CPIB_TOKEN}=    Set Variable     ${response.json()["access_token"]}
    Set Global Variable           ${CPIB_TOKEN}


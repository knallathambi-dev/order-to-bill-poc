
*** Settings ***
Resource            ../Global_Configuration/Manage_Token.robot
Library    RequestsLibrary
Library    Collections
Library    JSONLibrary
Library             String

*** Variables ***
${RingBackTonePCIdModif}
${GLOBAL_CONFIG_IDS_Modif}
${SamsungID}
${SamsungPCID}
${PixelID}
${PixelPCID}
${SamsungGalaxyID}
${SamsungGalaxyPCID}
${NetflixPCID}
${DeviceInsurancePCID}
${DataPassPOID}
${DataPassPCID}
${DataPassPSID}
${DataPassGlobalID}
${DataBundlePCID}
${DataBundleID}
${DiscountedSamsungID}
${DiscountedSamsungPCID}
${HBOID}
${HBOPCID}
${ExtraBundleNatID}
${ExtraBundleNatPCID}
${TvChannelID}
${TvBoxID}
${CommitementPCID}
${MSlicencePCID}
${FiberPCID}
${WifiExtPCID}
${MSlicenceID}
${FiberID}
${WifiExtID}
${4GBackupID}
${IPStaticID}
${InternetBoostID}

*** Keywords ***
the customer create a product Configuration for ${Product_Offering_Id}
     [Return]             ${response.content}
     &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
     ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/POST Product Configurator.json
     ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
         Log    ${body}
     Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['productOffering']}      id=${Product_Offering_Id}
     ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
     Log    ${jsonMisAJour}
     Create Session    session    ${EndPoint_Product_Configurator}
     Set Log Level    NONE
                                   ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                   Set Log Level    INFO
                                       # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                   Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                   Log    PATCH Response: Status=${response.content}

extract id from Configurator "${response}"
          ${Return_response}=    Evaluate    json.loads('''${response}''')    json
          Log    ${Return_response}
          ${Product_Conf}=    Get From Dictionary    ${Return_response}    id
          Set Global Variable    ${GLOBAL_CONFIG_IDS}    ${GLOBAL_CONFIG_IDS} ${Return_response['id']}
          Log    ${GLOBAL_CONFIG_IDS}
                       FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                           ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                           ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                           Run Keyword If    '${product_offering_name}' in ['Shipment for tangible products', 'Shipment PO OMG']    Set Global Variable    ${productConfiguration}    ${productConfiguration} ${item['productConfiguration']['id']}
                           Run Keyword If    '${product_offering_name}' in ['Shipment for tangible products', 'Shipment PO OMG']    Set Global Variable    ${computedProductConfigurationID}    ${computedProductConfigurationID} ${item['id']}
                       END
                       FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                           ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                           ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                           Run Keyword If    '${product_offering_name}' == 'Ring Back Tone'    Set Global Variable    ${RingBackTonePCId}    ${RingBackTonePCId} ${item['id']}
                       END
                       FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                           ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                           ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                           Run Keyword If    '${product_offering_name}' == 'SMS Option'    Set Global Variable    ${SMSPCId}    ${SMSPCId} ${item['id']}
                       END
                       FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                           ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                           ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                           Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${SamsungID}    ${SamsungID} ${item['id']}
                           Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${HandsetPOID}    ${HandsetPOID} ${item['productConfiguration']['productOffering']['id']}
                           Run Keyword If    '${product_offering_name}' == 'Samsung galaxy S10 reconditioned'    Set Global Variable    ${HandsetPCID}    ${HandsetPCID} ${item['productConfiguration']['productSpecification']['id']}
                       END
                      FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                          ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                          ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                          Run Keyword If    '${product_offering_name}' == 'Samsung Galaxy A55'    Set Global Variable    ${SamsungID}    ${SamsungGalaxyID} ${item['id']}
                          Run Keyword If    '${product_offering_name}' == 'Samsung Galaxy A55'    Set Global Variable    ${SamsungPCID}    ${SamsungGalaxyPCID} ${item['productConfiguration']['id']}

                      END
                      FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                     ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                     ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                     Run Keyword If    '${product_offering_name}' == 'Device Insurance'    Set Global Variable    ${DeviceInsurancePCID}    ${DeviceInsurancePCID} ${item['id']}

                      END
                      FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                Run Keyword If    '${product_offering_name}' == 'Add-on Netflix'    Set Global Variable    ${NetflixPCID}    ${NetflixPCID} ${item['id']}

                      END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                         ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                         ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                         Run Keyword If    '${product_offering_name}' == 'Data Pass'    Set Global Variable    ${DataPassPOID}    ${item['productConfiguration']['productOffering']['id']}
                         Run Keyword If    '${product_offering_name}' == 'Data Pass'    Set Global Variable    ${DataPassPSID}    ${item['productConfiguration']['productSpecification']['id']}
                         Run Keyword If    '${product_offering_name}' == 'Data Pass'    Set Global Variable    ${DataPassPCID}    ${item['productConfiguration']['id']}
                         Run Keyword If    '${product_offering_name}' == 'Data Pass'    Set Global Variable    ${DataPassGlobalID}    ${item['id']}
                         Run Keyword If    '${product_offering_name}' == 'Data Pass'    Exit For Loop
                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                 ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                 ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                 Run Keyword If    '${product_offering_name}' == 'Samsung S24'    Set Global Variable    ${SamsungID}    ${SamsungID} ${item['id']}
                                 Run Keyword If    '${product_offering_name}' == 'Samsung S24'    Set Global Variable    ${SamsungPCID}    ${SamsungPCID} ${item['productConfiguration']['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                            ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                            ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                            Run Keyword If    '${product_offering_name}' == 'Pixel 8'    Set Global Variable    ${PixelID}    ${PixelID} ${item['id']}
                                            Run Keyword If    '${product_offering_name}' == 'Pixel 8'    Set Global Variable    ${PixelPCID}    ${PixelPCID} ${item['productConfiguration']['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                            ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                            ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                            Run Keyword If    '${product_offering_name}' == 'Data Bundle'    Set Global Variable    ${DataBundleID}    ${DataBundleID} ${item['id']}
                                            Run Keyword If    '${product_offering_name}' == 'Data Bundle'    Set Global Variable    ${DataBundlePCID}    ${DataBundlePCID} ${item['productConfiguration']['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                            ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                            ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                            Run Keyword If    '${product_offering_name}' == 'Data Extra Bundle National'    Set Global Variable    ${ExtraBundleNatID}    ${ExtraBundleNatID} ${item['id']}
                                            Run Keyword If    '${product_offering_name}' == 'Data Extra Bundle National'    Set Global Variable    ${ExtraBundleNatPCID}    ${ExtraBundleNatPCID} ${item['productConfiguration']['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                            ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                            ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                            Run Keyword If    '${product_offering_name}' == 'Discounted Samsung A55'    Set Global Variable    ${DiscountedSamsungID}    ${DiscountedSamsungID} ${item['id']}
                                            Run Keyword If    '${product_offering_name}' == 'Discounted Samsung A55'    Set Global Variable    ${DiscountedSamsungPCID}    ${DiscountedSamsungPCID} ${item['productConfiguration']['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                            ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                            ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                            Run Keyword If    '${product_offering_name}' == 'Add-on HBO'    Set Global Variable    ${HBOID}    ${HBOID} ${item['id']}
                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                                 ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                                 ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                                 Run Keyword If    '${product_offering_name}' == 'TV Channels'    Set Global Variable    ${TvChannelID}    ${TvChannelID} ${item['id']}
                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                                                      ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                                                      ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                                                      Run Keyword If    '${product_offering_name}' == 'TV Box'    Set Global Variable    ${TvChannelID}    ${TvBoxID} ${item['id']}
                     END
                    FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                         ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                         ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                          Run Keyword If    '${product_offering_name}' == 'Mobile Extra Contract'    Set Global Variable    ${CommitementPCID}    ${CommitementPCID} ${item['id']}

                                          END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                        ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                        ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                        Run Keyword If    '${product_offering_name}' == 'Fiber Access'    Set Global Variable    ${FiberID}    ${FiberID} ${item['id']}
                                        Run Keyword If    '${product_offering_name}' == 'Fiber Access'    Set Global Variable    ${FiberPCID}    ${FiberPCID} ${item['productConfiguration']['id']}


                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                        ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                        ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                        Run Keyword If    '${product_offering_name}' == 'Microsoft 365 Licence'    Set Global Variable    ${MSlicenceID}    ${MSlicenceID} ${item['id']}
                                        Run Keyword If    '${product_offering_name}' == 'Microsoft 365 Licence'    Set Global Variable    ${MSlicencePCID}    ${MSlicencePCID} ${item['productConfiguration']['id']}
                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                        ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                        ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                        Run Keyword If    '${product_offering_name}' == 'Wifi Extender'    Set Global Variable    ${WifiExtID}    ${WifiExtID} ${item['id']}

                     END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                             ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                             ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                             Run Keyword If    '${product_offering_name}' == 'Internet Boost'    Set Global Variable    ${InternetBoostID}    ${InternetBoostID} ${item['id']}

                    END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                             ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                             ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                             Run Keyword If    '${product_offering_name}' == 'Static IPV4 Address'    Set Global Variable    ${IPStaticID}    ${IPStaticID} ${item['id']}

                    END
                     FOR    ${item}    IN    @{Return_response['computedProductConfigurationItem']}
                                                             ${product_offering}=    Get From Dictionary    ${item['productConfiguration']}    productOffering
                                                             ${product_offering_name}=    Get From Dictionary    ${product_offering}    name
                                                             Run Keyword If    '${product_offering_name}' == '4G Backup'    Set Global Variable    ${4GBackupID}    ${4GBackupID} ${item['id']}

                    END
           Log    ${DataPassGlobalID}
           Log    ${DataPassPCID}
           Log    ${CommitementPCID}

############################################################### shipping ################################################################################################################

Update Product Configuration "${shpping}"
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${computedProductConfigID}=      Strip String    ${computedProductConfigurationID}
    ${productConfig}=  Strip String    ${productConfiguration}
      &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
      ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Shipement.json
      ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

      ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
      ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}
      ${NEW_PRODUCT_ID}                   Set Variable        ${productConfig}


      # Update main ID
      Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

      # Update IDs in requestProductConfigurationItem
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
      Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_PRODUCT_ID}

      # Update Memory Value
      # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
      # Accéder au dictionnaire de la caractéristique
      Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${shpping}
      ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
      Log    ${jsonMisAJour}
      Create Session    session    ${EndPoint_Product_Configurator}
                                    ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                        # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                    Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                    Log    PATCH Response: Status=${response.content}


############################################################### Ring back tone ################################################################################################################
Update "${type}" Characteristique Ring "${ring type}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${RingBackTonePCId}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Ring.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${ring type}
           Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}


############################################################### SMS ################################################################################################################

Update Characteristique SMS
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${SMSPCId}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Selected.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
            Set Log Level    NONE
                                          ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                          Set Log Level    INFO
                                              # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                          Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                          Log    PATCH Response: Status=${response.content}



################################################################################ Data Pass ###############################################################################################

Update "${type}" Characteristique Data pass "${volume}"
    ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
    ${DataPassGlobalID}=  Strip String    ${DataPassGlobalID}

    &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json

    ${MY_FILE}=     OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Data Pass.json
    ${body}=        Evaluate    json.loads('''${MY_FILE}''')    json

    # Update main ID
    Set To Dictionary    ${body}    id    ${glob_cong_ids}

    # Update requestProductConfigurationItem ID
    Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${DataPassGlobalID}

    # Update characteristic value
    Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}    value=${volume}

    # Update configuration action
    Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}    action=${type}

    ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
    Log    ${jsonMisAJour}

    Create Session    session    ${EndPoint_Product_Configurator}
    ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}
    Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
    Log    PATCH Response Content: ${response.content}

################################################################################ Data Bundle ###############################################################################################

Update "${type}" Characteristique Data Bundle "${volume}"
           ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
           ${DataBundlePCID}=      Strip String    ${DataBundlePCID}
           ${DataBundleID}=  Strip String    ${DataBundleID}
             &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
             ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Data Bundle.json
             ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

             ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
             ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${DataBundleID}
             ${NEW_PRODUCT_ID}                   Set Variable        ${DataBundlePCID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
           Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
           Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_PRODUCT_ID}

            Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${volume}
              # Update configuration action
            Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}    action=${type}
          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
          Set Log Level    NONE
                                        ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                        Set Log Level    INFO
                                            # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                        Log    PATCH Response: Status=${response.content}

Update "${type}" Characteristique Extra Bundle "${volume}"
           ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
           ${DataBundlePCID}=      Strip String    ${ExtraBundleNatPCID}
           ${DataBundleID}=  Strip String    ${ExtraBundleNatID}
             &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
             ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Data Bundle.json
             ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

             ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
             ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${DataBundleID}
             ${NEW_PRODUCT_ID}                   Set Variable        ${DataBundlePCID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
           Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
           Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_PRODUCT_ID}

            Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${volume}
                      Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}    action=${type}
          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
          Set Log Level    NONE
                                        ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                        Set Log Level    INFO
                                            # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                        Log    PATCH Response: Status=${response.content}


################################################################################ Handset ###############################################################################################

Update Product Configurator samsung memory "${memory}" and Color "${color}"
       ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductID}=    Strip String    ${SamsungID}
        ${computedProductConfID}=    Strip String    ${SamsungPCID}
        &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
        ${MY_FILE}=    OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Samsung.json
        ${body}=    evaluate    json.loads('''${MY_FILE}''')    json

        ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
        ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
        ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${computedProductConfID}
        # Update main ID
        Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

        # Update IDs in requestProductConfigurationItem
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_QUERY_PRODUCT_CONF_ID}
        # Update Colour Value (assuming it's the first characteristic)
        FOR    ${characteristic}    IN    @{body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic']}
                ${char_name}=    Get From Dictionary    ${characteristic}    name
                Run Keyword If    '${char_name}' == 'Colour'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${color}
                Run Keyword If    '${char_name}' == 'Memory'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${memory}
        END
        # Convert updated body to JSON
        ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
        Log    ${jsonMisAJour}

        Create Session    session    ${EndPoint_Product_Configurator}
        ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}

        # Log response status and content
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response Content: ${response.content}

############################################################ discount samsung################################################"""""
Update Product Configurator discouted samsung memory "${memory}" and Color "${color}"
       ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductID}=    Strip String    ${DiscountedSamsungID}
        ${computedProductConfID}=    Strip String    ${DiscountedSamsungPCID}
        &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
        ${MY_FILE}=    OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Samsung.json
        ${body}=    evaluate    json.loads('''${MY_FILE}''')    json

        ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
        ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
        ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${computedProductConfID}
        # Update main ID
        Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

        # Update IDs in requestProductConfigurationItem
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_QUERY_PRODUCT_CONF_ID}
        # Update Colour Value (assuming it's the first characteristic)
        FOR    ${characteristic}    IN    @{body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic']}
                ${char_name}=    Get From Dictionary    ${characteristic}    name
                Run Keyword If    '${char_name}' == 'Colour'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${color}
                Run Keyword If    '${char_name}' == 'Memory'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${memory}
        END
        # Convert updated body to JSON
        ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
        Log    ${jsonMisAJour}

        Create Session    session    ${EndPoint_Product_Configurator}
        ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}

        # Log response status and content
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response Content: ${response.content}

Update Product Configurator Pixel memory "${memory}" and Color "${color}"
       ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${pixelId}=    Strip String    ${PixelID}
        ${pixelPcId}=    Strip String    ${PixelPCID}
        &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
        ${MY_FILE}=    OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Samsung.json
        ${body}=    evaluate    json.loads('''${MY_FILE}''')    json

        Log    ${pixelId}
        Log    ${pixelPcId}
        ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
        ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${pixelId}
        ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${pixelPcId}
        # Update main ID
        Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

        # Update IDs in requestProductConfigurationItem
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_QUERY_PRODUCT_CONF_ID}
        # Update Colour Value (assuming it's the first characteristic)
        FOR    ${characteristic}    IN    @{body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic']}
                ${char_name}=    Get From Dictionary    ${characteristic}    name
                Run Keyword If    '${char_name}' == 'Colour'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${color}
                Run Keyword If    '${char_name}' == 'Memory'    Set To Dictionary    ${characteristic['configurationCharacteristicValues'][0]['characteristic']}    value    ${memory}
        END
        # Convert updated body to JSON
        ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
        Log    ${jsonMisAJour}

        Create Session    session    ${EndPoint_Product_Configurator}
        ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}

        # Log response status and content
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response Content: ${response.content}

Update Product Configurator handset memory "${memory}"
       ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductID}=    Strip String    ${SamsungID}
        &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
        ${MY_FILE}=    OperatingSystem.Get File    Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Memory.json
        ${body}=    evaluate    json.loads('''${MY_FILE}''')    json

        ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
        ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
        # Update main ID
        Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

        # Update IDs in requestProductConfigurationItem
        Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
        # Update Colour Value (assuming it's the first characteristic)
       Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${memory}
        # Convert updated body to JSON
        ${jsonMisAJour}=    Evaluate    json.dumps(${body})    json
        Log    ${jsonMisAJour}

        Create Session    session    ${EndPoint_Product_Configurator}
        ${response}=    POST Request    session    ${Api_POST_Product-Configurator}    headers=&{headers}    data=${jsonMisAJour}

        # Log response status and content
        Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
        Log    PATCH Response Content: ${response.content}

################################################################################ device Insurance ###############################################################################################
Update "${type}" Characteristique device Insurance "${level}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${DeviceInsurancePCID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Device Insurance.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

################################################################################ Netflix ###############################################################################################

Update "${type}" Characteristique Netflix "${level}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${NetflixPCID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Netflix.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

#####################################################HBO################################################################""

Update "${type}" Characteristique HBO "${level}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${HBOID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator HBO.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

#####################################################Tv channels################################################################""

Update "${type}" Characteristique TV Channels "${level}"
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${TvChannelID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Tv Channels.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}
Update Characteristique TV Box
        ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
        ${computedProductConfigID}=      Strip String    ${TvBoxID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Tv Channels.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

          ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Update Commitement "${value}"
            ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
            ${computedProductConfigID}=      Strip String    ${CommitementPCID}
              &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
              ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Commitement change.json
              ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

              ${NEW_MAIN_ID}                      Set Variable        ${glob_cong_ids}
              ${NEW_QUERY_PRODUCT_ID}             Set Variable        ${computedProductConfigID}

              # Update main ID
              Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

              # Update IDs in requestProductConfigurationItem
              Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

              # Update Memory Value
              # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
              # Accéder au dictionnaire de la caractéristique
              Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationTerm'][0]['duration']}           amount=${value}
              ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
              Log    ${jsonMisAJour}
              Create Session    session    ${EndPoint_Product_Configurator}
               Set Log Level    NONE
                                             ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                             Set Log Level    INFO
                                                 # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                             Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                             Log    PATCH Response: Status=${response.content}



Update "${type}" Characteristique ms Office"${level}"
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${MSlicenceID}
           ${computedProductConfID}=    Strip String    ${MSlicencePCID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator MSLicence.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json

           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${computedProductConfID}

          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Update "${type}" Characteristique Fiber acces "${level}"
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${FiberID}
           ${computedProductConfID}=    Strip String    ${FiberPCID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Fiber Access.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${computedProductConfID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_QUERY_PRODUCT_CONF_ID}
          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Update "${type}" Characteristique MSLicence "${level}"
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${MSlicenceID}
           ${computedProductConfID}=    Strip String    ${MSlicencePCID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator MSLicence.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          ${NEW_QUERY_PRODUCT_CONF_ID}=    Set Variable    ${computedProductConfID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']}    id=${NEW_QUERY_PRODUCT_CONF_ID}
          # Update Memory Value
          # Set To Dictionary    ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']['value']}       value=${NEW_MEMORY_VALUE}
          # Accéder au dictionnaire de la caractéristique
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationCharacteristic'][0]['configurationCharacteristicValues'][0]['characteristic']}           value=${level}
          Set To Dictionary      ${body['requestProductConfigurationItem'][0]['productConfiguration']['configurationAction'][0]}           action=${type}
          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}
Update Internet Boost
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${InternetBoostID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Wifi extander.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Update Static IPV4 Address
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${IPStaticID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Wifi extander.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Update 4G Backup
         ${glob_cong_ids}=    Strip String    ${GLOBAL_CONFIG_IDS}
          ${computedProductID}=    Strip String    ${4GBackupID}
          &{headers}=    Create Dictionary    Authorization=Bearer ${OM_TOKEN}    Content-Type=application/json    Accept=*/*    Accept-Encoding=gzip, deflate, br    Connection=keep-alive
          ${MY_FILE}=     OperatingSystem.Get File       Global_Configuration/DATA/DATA_Product_Configurator/PATCH Product Configurator Wifi extander.json
          ${body}=        evaluate    json.loads('''${MY_FILE}''')      json
           ${NEW_MAIN_ID}=    Set Variable    ${glob_cong_ids}
          ${NEW_QUERY_PRODUCT_ID}=    Set Variable    ${computedProductID}
          # Update main ID
          Set To Dictionary    ${body}    id    ${NEW_MAIN_ID}

          # Update IDs in requestProductConfigurationItem
          Set To Dictionary    ${body['requestProductConfigurationItem'][0]}    id=${NEW_QUERY_PRODUCT_ID}

          ${jsonMisAJour}    Evaluate    json.dumps(${body})    json
          Log    ${jsonMisAJour}
          Create Session    session    ${EndPoint_Product_Configurator}
           Set Log Level    NONE
                                         ${response}=    POST Request    session  ${Api_POST_Product-Configurator}           headers=&{headers}         data=${jsonMisAJour}
                                         Set Log Level    INFO
                                             # If you want to log specific parts of the response (e.g., status, reason), you can do so without logging the full content:
                                         Log    PATCH Response: Status=${response.status_code}, Reason=${response.reason}
                                         Log    PATCH Response: Status=${response.content}

Teardown Test Case
    # Reset global variables to empty string (as a null alternative)
    Set Global Variable    ${computedProductConfigurationID}    ${EMPTY}
    Set Global Variable    ${productConfiguration}              ${EMPTY}
    Set Global Variable    ${GLOBAL_CONFIG_IDS}                 ${EMPTY}
    Set Global Variable    ${RingBackTonePCId}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_CONTRACT_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_BUNDLE_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_MOBILELINE_IDS}                  ${EMPTY}
    Set Global Variable    ${GLOBAL_RINGBACKTONE_IDS}                  ${EMPTY}
    Set Global Variable    ${DataPassPOID}              ${EMPTY}
    Set Global Variable    ${DataPassPSID}              ${EMPTY}
    Set Global Variable    ${DataPassGlobalID}                  ${EMPTY}
    Set Global Variable    ${DataBundlePCID}              ${EMPTY}
    Set Global Variable    ${DataBundleID}              ${EMPTY}
    Set Global Variable    ${SamsungID}              ${EMPTY}
    Set Global Variable    ${SamsungPCID}              ${EMPTY}
    Set Global Variable    ${DataPassPCID}              ${EMPTY}
    Set Global Variable    ${SMSPCId}                       ${EMPTY}
    Set Global Variable    ${PixelPCID}              ${EMPTY}
    Set Global Variable    ${PixelID}              ${EMPTY}
    Set Global Variable    ${SamsungGalaxyID}              ${EMPTY}
    Set Global Variable    ${SamsungGalaxyPCID}              ${EMPTY}
    Set Global Variable    ${NetflixPCID}              ${EMPTY}
    Set Global Variable    ${DeviceInsurancePCID}              ${EMPTY}
    Set Global Variable    ${DataPassGlobalID}    ${EMPTY}
    Set Global Variable    ${DiscountedSamsungID}    ${EMPTY}
    Set Global Variable    ${DiscountedSamsungPCID}    ${EMPTY}
    Set Global Variable    ${HBOID}    ${EMPTY}
    Set Global Variable    ${HBOPCID}    ${EMPTY}
    Set Global Variable    ${ExtraBundleNatID}    ${EMPTY}
    Set Global Variable    ${ExtraBundleNatPCID}    ${EMPTY}
    Set Global Variable    ${TvChannelID}    ${EMPTY}
    Set Global Variable    ${TvBoxID}    ${EMPTY}
    Set Global Variable    ${CommitementPCID}    ${EMPTY}
    Set Global Variable    ${MSlicencePCID}    ${EMPTY}
    Set Global Variable    ${FiberPCID}    ${EMPTY}
    Set Global Variable    ${MSlicenceID}    ${EMPTY}
    Set Global Variable    ${FiberID}    ${EMPTY}
    Set Global Variable    ${WifiExtID}    ${EMPTY}
    Set Global Variable    ${4GBackupID}    ${EMPTY}
    Set Global Variable    ${IPStaticID}    ${EMPTY}
    Set Global Variable    ${InternetBoostID}    ${EMPTY}
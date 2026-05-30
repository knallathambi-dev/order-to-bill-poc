*** Settings ***
Library           RequestsLibrary
Library           OperatingSystem
Library           String
Library           DateTime
Library           XML
Library           SSHLibrary
Library           Collections
Library           Process
Library           json
Library           Collections
Library           JSONLibrary
Library           Selenium2Library
Library           SeleniumLibrary
Library           JSONLibrary
Library           RPA.JSON
Library           RPA.Excel.Files
Library           RPA.SAP
Library           ExcelLibrary


*** Variables ***

${contractId}         ${EMPTY}
${bundledId}          ${EMPTY}
${mobileLinePOId}     ${EMPTY}
${mobileLinePSId}     ${EMPTY}
${ConnectivityPOId}   ${EMPTY}
${ConnectivityPSId}   ${EMPTY}
${TimeBundlePOId}     ${EMPTY}
${TimeBundlePSId}     ${EMPTY}
${SMSOptionPOId}      ${EMPTY}
${SMSOptionPSId}      ${EMPTY}
${RingBackTonePOId}   ${EMPTY}
${RingBackTonePSId}   ${EMPTY}

@{ACKMaxPlusPatterns}    # Cette liste sera remplie avec les patterns ACKMaxPlus
${Address}                          1234-Street
${DeliveryDate}                     2023-01-01
${contractId}                       contractId
${bundledId}                        bundledId
${mobileLinePOId}                   mobileLinePOId
${mobileLinePSId}                   mobileLinePSId
${ConnectivityPOId}                 ConnectivityPOId
${ConnectivityPSId}                 ConnectivityPSId
${TimeBundlePOId}                   TimeBundlePOId
${TimeBundlePSId}                   TimeBundlePSId
${SMSOptionPOId}                    SMSOptionPOId
${SMSOptionPSId}                    SMSOptionPSId
${RingBackTonePOId}                 RingBackTonePOId
${RingBackTonePSId}                 RingBackTonePSId
@{handsetCapacities}                128  256
@{modes}                            In store  Other
@{options}                          ${EMPTY}  SMS  Ring  SMSRing
${outputFile}                       output_patterns4.txt

${testSuiteNumber}                  0
${row}                              1

${INPUT_PI_JSON}                    FILE/PI_response.json
${INPUT_POI_JSON}                   FILE/POI_response.json
${OUTPUT_POI_JSON}                  FILE/OUTPUT_PI_response.json
${INPUT_MaxPlus_xlsx}      	        FILE/UC_INPUT_MaxPlus.xlsx

${OUTPUT_MaxPlus_xlsx}              FILE/UC_MaxPlus.xlsx
${OUTPUT_Max_xlsx}                  FILE/UC_Max.xlsx
${OUTPUT_Basic_xlsx}                FILE/UC_Basic.xlsx
${OUTPUT_PI_xlsx}                   FILE/PI_output.xlsx

${OUTPUT_MaxPlus_txt}               FILE/UC_MaxPlus.txt
${OUTPUT_Max_txt}                   FILE/UC_Max.txt
${OUTPUT_Basic_txt}                 FILE/UC_Basic.txt
${OUTPUT_PI_txt}                    FILE/PI_output.txt



### KEY ###                         ### VALUE ###
${Environnement_used}               %{ENV=Integration}

${code_20}                          20
${invalidURL_reason}                Invalid URL parameter value

${code_21}                          21
${missingbody_reason}               missing request body

${code_22}                          22
${invalidbody_reason}               invalid request body

${code_23}                          23
${missing_reason}                   Missing body field

${code_24}                          24
${invalid_reason}                   Invalid body field

${code_28}                          28
${invalid_paramter_reason}          Invalid query-string parameter value

${code_60}                          60
${notfound_reason}                  Resource not found


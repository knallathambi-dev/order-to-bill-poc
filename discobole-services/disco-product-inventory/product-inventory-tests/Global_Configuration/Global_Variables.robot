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
#Library           JSONLibrary

*** Variables ***
### KEY ###                          ### VALUE ###
${Environnement_used}                Integration

${KC_Username_Value}               admin-ui@orange.com
${KC_Password_Value}               admin-ui@orange.com

#${KC_Username_Value}                test.user@orange.com
#${KC_Password_Value}                12345678


#${KC_Username_Value}                om-user@test.com
#${KC_Password_Value}                om

#${KC_Username_Value}                cpib-user@test.com
#${KC_Password_Value}                cpib

#${KC_Username_Value}                om-user@test.com
#${KC_Password_Value}                om

#${KC_Username_Value}                h.mrad@sofrecom.com
#${KC_Password_Value}                Henda$2025

#${KC_Username_Value}                disco.rp@orange.com
#${KC_Password_Value}                12345678

#${KC_Username_Value}                disco.admin@orange.com
#${KC_Password_Value}                12345678

#${KC_Username_Value}                admin-ui@orange.com
#${KC_Password_Value}                admin-ui@orange.com

#${KC_Username_Value}                lisa@orange.com
#${KC_Password_Value}                12345678

#${KC_Username_Value}                hassen@gmail.com
#${KC_Password_Value}                hassen

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
${invalid_Api_Url_reason}           Invalid Api URL

${code_61}                          61
${not_allowed_reason}               Method not allowed

${code_501}                         501
${not_implemented_reason}           Not Implemented


# ERROR
${EXPECTED_ERROR_1}    The operational status Created cannot be modified into Locked
${EXPECTED_ERROR_2}    The operational status Sold cannot be modified into Confirmed
${EXPECTED_ERROR_3}    One or multiple issues are exist in the path or the value fields
${EXPECTED_ERROR_4}    The operational status Sold cannot be modified into PendingDelivery


${CPIB_serviceName}                                     cpib
${CPIB_version}                                         1.16.0-SNAPSHOT

${OUTPUT_FILE}      test_results.txt

# Contract IDs
${Contract_Mobile_Package_ProductOffering_Id}           dbbc92f9-faf2-4886-8749-0f07abfc1971
# BundleProductOffering IDs
${BundlePO_Mobile_Package_ProductOffering_Id}          7d66ed18-b9d2-44a5-81f5-6d89336544bc
# AtomicProductOffering IDs
${AtomicPO_Mobile_Line_ProductOffering_Id}              39d17453-6b6c-4902-9743-a50ec9ad5e67
${AtomicPO_Mobile_Handset_ProductOffering_Id}           7d66ed18-b9d2-44a5-81f5-6d89336544bc
${AtomicPO_Sim_Card_ProductOffering_Id}                 021ee0a9-73e6-4a3d-bc3f-d5b55631b6b2
${AtomicPO_Connectivity_ProductOffering_Id}             13ee923a-8449-4f45-b81e-bb7ccb7b893e
${AtomicPO_Time_Bundle_ProductOffering_Id}              95023bc2-151d-4bf2-bb87-1b7d6cd2e847
${AtomicPO_Ring_Back_Tone_ProductOffering_Id}           a055e826-633e-4b36-bd72-9774df80b769
${AtomicPO_SMS_Option_ProductOffering_Id}               889058f0-1ce8-4973-bc35-849766bdf6a4
${AtomicPO_Samsung_Galaxy_S10_ProductOffering_Id}       24b34407-3c7d-4228-ba45-feff73ec4f05
${AtomicPO_Samsung_Galaxy_ProductOffering_Id}           78bc2297-cb07-4161-babb-35fc6b55979f
# ProductSpecification IDs
${Connectivity_ProductSpecification_Id}                 a310de0e-276a-4992-8ac2-36459e68ea3f
${Mobile_line_ProductSpecification_Id}                  9a2fb547-92ae-4fd0-afa7-6418e27d51cb
${Sim_Card_ProductSpecification_Id}                     191424cb-6002-46aa-a5a9-0602cfba83c7
${Time_Bundle_ProductSpecification_Id}                  63d24f7e-c529-404c-911b-7c7926fd12ff
${Shipement_ProductSpecification_Id}                    3e828676-7345-4ac9-92f3-7606857fa588

${Valid_relatedParty_id_HM}                             HM-1234
# old related party .id -----------------------------------------------------------------------
#${Valid_relatedParty_id}                                456-dd-df45
# new related party .id -----------------------------------------------------------------------
${Valid_relatedParty_id}                                106
${Valid_productOffering_id}                             5fe032f0-fa01-42ec-afeb-920f74c2eae3
# Staging environment
${Mobile_Line_productSpecification_id}                  f18a3fe0-1e61-419a-b7d8-dd68215b3705
#${Mobile_Line_productSpecification_id}                  c458d968-3718-4754-9408-7bd81526e7b2 // integration
${Valid_productOrderId}                                 660e84d8a300175175babba3
${Valid_orderItemId}                                    01
${Handset_productOffering_id}                           c8846e4c-ecca-40b2-b7de-9c130f4f3466
${Handset_productOffering_name}                         Mobile Handset
${Mobile_Package__productOffering_name}                 Mobile Package 1
${Mobile_Line_productSpecification_name}                Mobile line
${Mobile_Package_Max_Plus_product_name}                 Mobile Package Max Plus
${Smart_Contract_name}                                  SmartView Wallet Galaxy A55 case

${Valid_Job_ExportJobSpecification_id}                          66fa9d1a8f5ecc56d354a86b
${Valid_Job_PurgeJobSpecification_JobSpecifications_id}         66faa78d8f5ecc56d354a8ca
${Valid_Job_PurgeJobSpecification_Tasks_id}                     66fbfadbfe0c08275cfb2a73
${Valid_Job_PurgeJobSpecification_Products_id}                  X

${Valid_MSISDN}                                         4152797439
${Valid_IMSI}                                           310478356879098

${KafkaHost}                                            aaa
${KafkaPort}                                            bbb
${topic}                                                disco.product-inventory.productAttributeValueChangeEvent-event

${Valid_Date_with_Time}                                 2023-11-03T09:02:47Z

*** Settings ***
Library     RequestsLibrary
*** Variables ***

### KEY ###                             ### VALUE ###
${Api_POST_PF}                                  /processManagement/v1/processFlow
${Api_POST_Product-Configurator}                /v1/queryProductConfiguration


${valid_productOffering_id}                     440cde08-111e-4893-ab8f-a7dd9e569de2
${valid_productOffering_id2}                    5c1b0a6c-5ae4-4c1b-ac40-a3209aa63eee
${productOffering_referredType}                 productOffering
${product_referredType}                         product
${valid_productOffering_name}                   OPC_CMP1

${Qualified_productOffering_id}                 1234
${Not_Contract_productOffering_id}              cb07fd83-8f41-4cd2-97f5-5989f3ab2bd8




${valid_configuration_id}                       ACKMax_In store_Les berges du lac_2024-01-01
${valid_configuration_id_SMS}                   ACKMaxSMS_In store_Les berges du lac_2024-01-01
${valid_configuration_id_Ring}                  ACKMaxRing_In store_Les berges du lac_2024-01-01
${valid_configuration_id_SMSRing}               ACKMaxSMSRing_In store_Les berges du lac_2024-01-01


${configuration_id_MODAddSMS}                   MODAddRING_
${configuration_id_MODAddRING}                  MODAddRING_65c3a52a924ac93e4cdac8d8_65c3a52a924ac93e4cdac8d0
${configuration_id_MODModifyRING}               MODModifyRING_65c3a8ac924ac93e4cdac8db_65c3a8ac924ac93e4cdac8da
${configuration_id_MODDeleteRING}               MODDeleteRING_65c3a8ac924ac93e4cdac8db_65c3a8ac924ac93e4cdac8da
${product_offering_id_ACKMaxPlus}               ACKMaxPlus_Inshop_20Rue_2024-02-13
${configuration_id_ack_ACKMaxPlusHandset}       ACKMaxPlusHandset_Inshop_20Rue_2024-02-13
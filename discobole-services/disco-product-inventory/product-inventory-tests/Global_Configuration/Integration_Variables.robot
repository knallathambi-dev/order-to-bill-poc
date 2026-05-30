*** Variables ***

### KEY ###                                             ### VALUE ###
${end-point-cpib}                                       https://product-inventory-integration-disco.apps.prd1.c1.paas.tech.orange
${KC_URL_Value}                                         https://keycloak-integration-disco.apps.prd1.c1.paas.tech.orange
${KC_ClientSecret_Value}                                xbr84SmdpFoB4YURQA921Jac4IHnntxm
#${KC_URL_Value}                                        https://keycloak-integration-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak

${Valid_TerminationJobSpecification_JobSpecification_id}                                    67079394ec0f3b2de7e9dbca
${Valid_Event_JobSpecification_id}                                                          xxxxxxxxxxxxxxxxxxxxxxxx
${Valid_ExportJobSpecification_JobSpecification_id}                                         673cb16f13768b66fb0a5b15
${Valid_PurgeJobSpecification_JobSpecification_id}                                          670793ceec0f3b2de7e9dbcc



#--------------------------------------------------------------------------------------------
# Integration environment
#--------------------------------------------------------------------------------------------

#curl --request POST \
#  --url https://keycloak-integration-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=disco.admin@orange.com \
#  --data password=12345678 \
#  --data grant_type=password \
#  --data 'client_id=gateway' \
#  --data 'client_secret=xbr84SmdpFoB4YURQA921Jac4IHnntxm' \
#  --data scope=openid

*** Variables ***

### KEY ###                     ### VALUE ###
${KC_URL_Value}                 https://keycloak-review-disco.apps.prd1.c1.paas.tech.orange/
${end-point-cpib}               https://product-inventory-review-disco.apps.prd1.c1.paas.tech.orange/




#--------------------------------------------------------------------------------------------
# GCP - Review environment
#--------------------------------------------------------------------------------------------
#curl --request POST \
#  --url https://keycloak-review-disco.apps.prd1.c1.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzR2trQTJSODJxNmg5TTd5c1JpZHVBaHcxVS1BV3JpSVFESENNNzFQekQ0In0.eyJleHAiOjE3NzAzNDY2NzEsImlhdCI6MTc3MDMxMDY3MSwianRpIjoiMGNjOTBhYzItMjZjYy00MjA3LTgzNzAtZTNiODQ3MzE3OGQ5IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1yZXZpZXctZGlzY28uYXBwcy5wcmQxLmMxLnBhYXMudGVjaC5vcmFuZ2UvcmVhbG1zL1NwcmluZ0Jvb3RLZXljbG9hayIsInN1YiI6ImUxOWJjODFjLWZhMTktNGVjNS05OTg4LThhMTgyNTBjMzEwMiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdhdGV3YXkiLCJzaWQiOiI2ZGM3YmUyYS1jNDA0LTRkMmItOGIwNi0zYzlmOWViNGY3NjAiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vZGlzY28tZ2F0ZXdheS1yZXZpZXctZGlzY28uYXBwcy5wcmQxLmMxLnBhYXMudGVjaC5vcmFuZ2UiXSwicmVzb3VyY2VfYWNjZXNzIjp7ImdhdGV3YXkiOnsicm9sZXMiOlsiVXBkYXRlUHJvZHVjdCIsImRpc2NvLWFkbWluIiwiUmVhZFByb2R1Y3QiLCJQcm9kdWN0Q3JlYXRpb24iXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwicmVsYXRlZFBhcnR5Um9sZSI6InByb3NwZWN0IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJyZWxhdGVkUGFydHlJZCI6IjIyNy1tZjMwIiwibmFtZSI6Ikxpc2EiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaXNhQG9yYW5nZS5jb20iLCJsb2NhbGUiOiJlbiIsImdpdmVuX25hbWUiOiJMaXNhIiwiZW1haWwiOiJsaXNhQG9yYW5nZS5jb20ifQ.XZ_zpWJv6JeoGMydUKHlwN5N7cr1FDDZYgT3Mpjvst0k_uBE7W6RGMiC8QM-5FbsAAjqAFiyK6jhs4ZltpOPskzlZ8OCaBrIdMBs9_JX2TtVEONXquCISz3OE-CjeC4IbFQR16iCDIBhp0W8aVNhpxJ1bA7rOekOJU7JDoKjWAbXRmRNhrTsYcQuk0JnNpY23A9iif5hxnNkCIyKfDj2Ut2Ptf0JRpdOJitNbxgVMUEXPkIus8r11TjU5wqzb5FiAvkxdu4IqLj08sva6eJCRKK-QvC0CecRGQVxzR3jxKitsvSupbgF4G6tKl-UGR-Ovyr4Fano0PcBWEqRMrPd5g' \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=lisa@orange.com \
#  --data password=12345678 \
#  --data grant_type=password \
#  --data client_id=gateway \
#  --data client_secret=epO9DcZUpM68SgwYS897gX8Luhb7Jgvk \
#  --data scope=openid


#--------------------------------------------------------------------------------------------
# Review environment
#--------------------------------------------------------------------------------------------
#https://product-inventory-review-disco.apps.fr01.paas.tech.orange
#https://product-inventory-review-disco.apps.prd1.c1.paas.tech.orange/
#https://keycloak-review-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak
#--------------------------------------------------------------------------------------------

${Valid_relatedParty_id}                        45hj-8888
${Valid_productOffering_id}                     5fe032f0-fa01-42ec-afeb-920f74c2eae3
${Mobile_Line_productSpecification_id}          c458d968-3718-4754-9408-7bd81526e7b2
${Valid_productOrderId}                         1106
${Valid_orderItemId}                            01
${Handset_productOffering_id}                   c8846e4c-ecca-40b2-b7de-9c130f4f3466
${Handset_productOffering_name}                 Mobile Handset

${KC_Username_Value}                            lisa@orange.com
${KC_Password_Value}                            12345678

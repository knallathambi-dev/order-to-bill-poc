*** Variables ***

### KEY ###                                             ### VALUE ###
${end-point-cpib}                                       https://product-inventory-staging-disco.apps.fr01.paas.tech.orange
${KC_URL_Value}                                         https://keycloak-staging-disco.apps.fr01.paas.tech.orange
${KC_ClientSecret_Value}                                9ga8VqPWPlWKv5EngBTt8yag4CYrBPGu

${Valid_Batch_task_id}                                  671f718081b85d04a1e530d2
${Valid_Event_task_id}                                  xxxxxxxxxxxxxxxxxxxxxxxx
${Valid_ExportJob_task_id}                              671f6d4381b85d04a1e52d03
${Valid_PurgeJob_task_id}                               671f590481b85d04a1e52ba3


#--------------------------------------------------------------------------------------------
# Staging environment
#--------------------------------------------------------------------------------------------

#curl --request POST \
#  --url https://keycloak-staging-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=disco.admin@orange.com \
#  --data password=12345678 \
#  --data grant_type=password \
#  --data 'client_id=gateway' \
#  --data 'client_secret=9ga8VqPWPlWKv5EngBTt8yag4CYrBPGu' \
#  --data scope=openid

#--------------------------------------------------------------------------------------------

#curl --request POST \
#  --url https://keycloak-staging-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=cpib-user@test.com \
#  --data password=cpib-user@test.com \
#  --data grant_type=password \
#  --data client_id=cpib \
#  --data client_secret=Dt0YeawQnD0uuiuq1cJNQ1WxWbCBmaKO \
#  --data scope=openid

#--------------------------------------------------------------------------------------------

#curl --request POST \
#  --url https://keycloak-staging-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'authorization: Bearer {{token}}' \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=admin-ui@orange.com \
#  --data password=admin-ui@orange.com \
#  --data grant_type=password \
#  --data client_id=gateway \
#  --data client_secret=9ga8VqPWPlWKv5EngBTt8yag4CYrBPGu \
#  --data 'scope=openid profile email'

#---------------------------------------------------------------------------------------------

#curl --request POST \
#  --url https://keycloak-staging-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'authorization: Bearer {{token}}' \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=lisa@orange.com \
#  --data password=12345678 \
#  --data grant_type=password \
#  --data client_id=gateway \
#  --data client_secret=9ga8VqPWPlWKv5EngBTt8yag4CYrBPGu \
#  --data 'scope=openid profile email'

# "scope": "openid profile email",
#  "relatedPartyRole": "customer",
#  "email_verified": true,
#  "relatedPartyId": "227-mf30",
#  "name": "Lisa",
#  "preferred_username": "lisa@orange.com",
#  "given_name": "Lisa",
#  "locale": "en",
#  "email": "lisa@orange.com"

#---------------------------------------------------------------------------------------------
#curl --request POST \
#  --url https://keycloak-staging-disco.apps.fr01.paas.tech.orange/realms/SpringBootKeycloak/protocol/openid-connect/token \
#  --header 'authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJabk5QYnItR0hQeUFkeWcwamhUUFJLdUhYM2RhcnkzVGJzbjVrQkVGRUFjIn0.eyJleHAiOjE3NzIyMzU2ODIsImlhdCI6MTc3MjE5OTY4MiwianRpIjoiYTFiOWQ2YTktNjFmMC00NWRiLWEzNWYtYWRhM2RkNzA4Mjg3IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay1zdGFnaW5nLWRpc2NvLmFwcHMuZnIwMS5wYWFzLnRlY2gub3JhbmdlL3JlYWxtcy9TcHJpbmdCb290S2V5Y2xvYWsiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiYWM0NTc1NzItMWYwNy00NzhjLTliZGYtZGRhMWQ4NDA3MzI3IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZ2F0ZXdheSIsInNpZCI6IjEwYzUxNDljLTJiODAtNDk3Yy04NjhkLWMxY2M4Zjc3YTcyYyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9kaXNjby1nYXRld2F5LXN0YWdpbmctZGlzY28uYXBwcy5mcjAxLnBhYXMudGVjaC5vcmFuZ2UiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1zcHJpbmdib290a2V5Y2xvYWsiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfSwiZ2F0ZXdheSI6eyJyb2xlcyI6WyJPcmRlckNhcHR1cmVBZG1pbiIsIlVwZGF0ZVByb2R1Y3QiLCJSZWFkUHJvZHVjdE9yZGVyIiwiQ3JlYXRlUHJvZHVjdCIsIlB1cmdlUHJvZHVjdCIsIk9yY2hlc3RyYXRpb25QbGFuc0FkbWluIiwiUHVyZ2VUYXNrIiwiUmVhZFByb2R1Y3QiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwicmVsYXRlZFBhcnR5Um9sZSI6ImN1c3RvbWVyIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInJlbGF0ZWRQYXJ0eUlkIjoieGt3cHFyOHMiLCJuYW1lIjoiVGVzdCBVc2VyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdC51c2VyQG9yYW5nZS5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsImxvY2FsZSI6ImVuIiwiZmFtaWx5X25hbWUiOiJVc2VyIiwiZW1haWwiOiJ0ZXN0LnVzZXJAb3JhbmdlLmNvbSJ9.tNiFgqyoXwK29xWiHxp4-Vzi4QI9ZgA9tuwgiv1PsxmHVQhmL0aZfE4d_JqqIIT4RugUXzOWwzFIBieSCvOjBg6KZbHLDy7NidXrfCealRMasNRNZk3E71AvZbA2vW9lM1IKQkdUrb3ECrLw23uHxftY4nXdZ1aAfqwqhyo8hvL0bGt7CYImg00GnVW1bt5Trj0tBt4bYmKdrgLUK4CCXVvm7x4EbvVL-SiTrshipKG5iUBMCas46c6owK7EAXD_OjWz02pxrjSolKEWtwGRx5splPjnnHPSTLjomWRVV050wVSrYWe9GTzq8xL3pEl1l5GKMXnO-tmch5n952kQXw' \
#  --header 'content-type: application/x-www-form-urlencoded' \
#  --data username=test.user@orange.com \
#  --data password=12345678 \
#  --data grant_type=password \
#  --data client_id=gateway \
#  --data client_secret=9ga8VqPWPlWKv5EngBTt8yag4CYrBPGu \
#  --data scope=openid
#Party Id: xkwpqr8s
#Party Rôle: customer

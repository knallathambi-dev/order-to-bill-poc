#!/bin/bash
set -e

KEYCLOAK_URL="http://localhost:8080"
REALM="SpringBootKeycloak"
CLIENT_ID="cood"
USERNAME="admin-ui@orange.com"
PASSWORD="admin-ui@orange.com"
KEYCLOAK_ADMIN="admin"
KEYCLOAK_ADMIN_PASSWORD="admin"

# Wait for Keycloak to be ready
echo "⏳ Waiting for Keycloak..."
until curl -s -o /dev/null -w "%{http_code}" "${KEYCLOAK_URL}/realms/master" | grep -q "200"; do
  echo "Waiting for Keycloak to be available at ${KEYCLOAK_URL}..."
  sleep 2
done
echo "✅ Keycloak is ready."

# Get admin token
TOKEN=$(curl -s -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${KEYCLOAK_ADMIN}" \
  -d "password=${KEYCLOAK_ADMIN_PASSWORD}" \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' | jq -r .access_token)

# Get client UUID for CLIENT_ID
CLIENT_UUID=$(curl -s -H "Authorization: Bearer ${TOKEN}" \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=${CLIENT_ID}" \
  | jq -r '.[0].id')

if [ "$CLIENT_UUID" == "null" ] || [ -z "$CLIENT_UUID" ]; then
  echo "❌ Client '${CLIENT_ID}' not found in realm '${REALM}'."
  exit 1
fi

# Ensure client is confidential
echo "🔧 Ensuring client is confidential..."
curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${CLIENT_UUID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"publicClient": false}' > /dev/null

# Regenerate client secret
echo "🔐 Regenerating client secret..."
CLIENT_SECRET=$(curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${CLIENT_UUID}/client-secret" \
  -H "Authorization: Bearer ${TOKEN}" \
  | jq -r .value)

echo "✅ New client secret: $CLIENT_SECRET"

# Check if user exists
USER_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=${USERNAME}" \
  -H "Authorization: Bearer ${TOKEN}" | jq -r '.[0].id')

if [ -z "$USER_ID" ] || [ "$USER_ID" == "null" ]; then
  echo "User not found, creating user..."

  # Create user
  curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "'${USERNAME}'",
      "email": "'${USERNAME}'",
      "enabled": true,
      "emailVerified": true,
      "firstName": "Admin",
      "lastName": "UI"
    }' > /dev/null

  sleep 3 # wait for creation

  # Fetch the user ID again
  USER_ID=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM}/users?username=${USERNAME}" \
    -H "Authorization: Bearer ${TOKEN}" | jq -r '.[0].id')

  if [ -z "$USER_ID" ] || [ "$USER_ID" == "null" ]; then
    echo "❌ Failed to create or find user."
    exit 1
  fi
else
  echo "User found: $USER_ID"
fi

# Set password
curl -s -X PUT "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/reset-password" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "password",
    "value": "'${PASSWORD}'",
    "temporary": false
  }' > /dev/null

echo "✅ Password set."

# Get all realm roles (simplified to only needed fields)
realm_roles=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM}/roles" \
  -H "Authorization: Bearer ${TOKEN}" | jq '[.[] | {id: .id, name: .name, composite: .composite, clientRole: false}]')

# Assign all realm roles to user
if [ "$(echo "$realm_roles" | jq 'length')" -gt 0 ]; then
  echo "Assigning realm roles..."
  curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/role-mappings/realm" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -d "$realm_roles" > /dev/null
  echo "✅ Realm roles assigned."
else
  echo "No realm roles found."
fi

# Get all clients
clients=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM}/clients" \
  -H "Authorization: Bearer ${TOKEN}")

# Loop through clients and assign all client roles for each client
for client_id in $(echo "$clients" | jq -r '.[].id'); do
  client_roles=$(curl -s "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${client_id}/roles" \
    -H "Authorization: Bearer ${TOKEN}" | jq '[.[] | {id: .id, name: .name, composite: .composite, clientRole: true}]')

  if [ "$(echo "$client_roles" | jq 'length')" -gt 0 ]; then
    curl -s -X POST "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${USER_ID}/role-mappings/clients/${client_id}" \
      -H "Authorization: Bearer ${TOKEN}" \
      -H "Content-Type: application/json" \
      -d "$client_roles" > /dev/null
  fi
done

echo "🎉 All roles assigned to '${USERNAME}'!"

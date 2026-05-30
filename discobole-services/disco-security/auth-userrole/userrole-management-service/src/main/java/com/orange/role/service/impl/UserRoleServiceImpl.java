// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.role.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.discobole.permission.ComponentConfiguration;
import com.orange.discobole.permission.Entitlement;
import com.orange.discobole.permission.FunctionConfiguration;
import com.orange.discobole.permission.UserRole;
import com.orange.role.handler.DiscoClientException;
import com.orange.role.service.UserRoleService;
import com.orange.role.util.QueryParamUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger LOGGER = LogManager.getLogger(UserRoleServiceImpl.class);
    public static final String INVOLVEMENT_ROLE = "involvementRole";
    public static final String ADMIN_REALMS = "/admin/realms/";
    public static final String AUTHORIZATION = "Authorization";

    @Value("${spring.keycloakUrl}")
    private String keycloakUrl;

    @Value("${spring.keycloakclientId}")
    private String clientId;

    @Value("${spring.realm}")
    private String realm;

    private MongoTemplate mongoTemplate;
    public UserRoleServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserRole createUserRole(UserRole userRole, String token) {

        LOGGER.info("Creating User Role: {}", userRole.getInvolvementRole());

        try {
            validateEntitlementActions(userRole);
            createRoleCall(userRole, token);
            userRole.setId(UUID.randomUUID().toString());
            UserRole user = mongoTemplate.save(userRole);
            return user;
        } catch (Exception e) {
            LOGGER.error("Failed to create role: {}" , e.getMessage());
            throw new RuntimeException("Failed to create role in Keycloak: " + e.getMessage(), e);
        }
    }

    private void validateEntitlementActions(UserRole userRole) {
        List<String> allowedActions = Arrays.asList("read", "readAndWrite");
        String component = userRole.getComponent();
        if (userRole.getInvolvementRole() == null) {
            throw new InvalidActionException("Role Name cannot be null or empty.");
        }

        if (userRole.getEntitlement() == null || userRole.getEntitlement().isEmpty()) {
            throw new InvalidActionException("Entitlement list cannot be null or empty.");
        }

        for (Entitlement entitlement : userRole.getEntitlement()) {
            String action = entitlement.getAction();
            String function= entitlement.getFunction();


            if (action == null || action.trim().isEmpty()) {
                throw new InvalidActionException("Action cannot be null or empty.");
            }

            if(function!=null){
                Query query = new Query();
                query.addCriteria(Criteria.where("functionName").is(function));
                FunctionConfiguration functionCheck = mongoTemplate.findOne(query,FunctionConfiguration.class);
                if(functionCheck==null){
                    throw new InvalidActionException("Invalid function");
                }
            }
            // Trim and compare action in a case-insensitive way
            if (!allowedActions.contains(action)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only \"read\"/\"readAndWrite\" values are allowed for \"action\" function.");
            }
        }

        if(component!=null){
            Query query = new Query();
            query.addCriteria(Criteria.where("componentName").is(component));
            ComponentConfiguration functionCheck = mongoTemplate.findOne(query,ComponentConfiguration.class);
            if(functionCheck==null){
                throw new InvalidActionException("Invalid componentName");
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class InvalidActionException extends RuntimeException {
        public InvalidActionException(String message) {
            super(message);
        }
    }

    private void createRoleCall(UserRole userRole, String accessToken) {
        LOGGER.info("Creating User Role in keycloak");
        HttpResponse<String> response;
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> role = new HashMap<>();
            role.put("name", userRole.getInvolvementRole());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(keycloakUrl + ADMIN_REALMS + realm + "/clients/" + clientId + "/roles"))
                    .header(AUTHORIZATION, accessToken).header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(role))).build();

            response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 201) {
                throw new RuntimeException("Failed to create role: " + response.body());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create role: {} " , e.getMessage());
            throw new RuntimeException("Failed to create role in Keycloak {} " + e.getMessage());
        }
    }

    @Override
    public void updateUserRole(UserRole userRole, String roleId) {
        LOGGER.info("Updating User Role in");
        try {
            checkRoleNameChange(userRole, roleId);
            Update update = new Update();
            update.set("entitlement", userRole.getEntitlement());
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(roleId));
            mongoTemplate.updateFirst(query, update, UserRole.class);
        } catch (Exception e) {
            LOGGER.error("Failed to update role: " + e.getMessage());
            throw new RuntimeException("Failed to update role:{} " + e.getMessage(), e);
        }
    }

    private void checkRoleNameChange(UserRole userRole, String roleId) {
        UserRole oldUserRole = fetchUserRoleById(roleId);
        if (!oldUserRole.getInvolvementRole().equalsIgnoreCase(userRole.getInvolvementRole())) {
            throw new RuntimeException("Role Name Cannot be changed");
        }
    }

    @Override
    public void deleteRole(String roleId, String token) {
        LOGGER.info("Deleting User Role: {}", roleId);
        try {
            deleteRoleInKeycloak(roleId, token);
            Query query = Query.query(Criteria.where("id").is(roleId));
            mongoTemplate.remove(query, UserRole.class);
        } catch (Exception e) {
            LOGGER.error("Failed to delete role: " , e.getMessage());
            throw new RuntimeException("Failed to delete role:  in Keycloak: " + e.getMessage(), e);
        }
    }

    private void deleteRoleInKeycloak(String roleId, String accessToken) throws Exception {
        LOGGER.info("Deleting User Role in keycloak: {}", roleId);
        try {
            UserRole userRole = fetchUserRoleById(roleId);
            if (userRole == null) {
                throw new RuntimeException("Unable to find role with role Id " + roleId);
            }
            String keycloakRoleId = getRoleIdByRoleName(userRole.getInvolvementRole(), accessToken);

            HttpClient client = HttpClient.newHttpClient();
            String deleteRoleUrl = keycloakUrl + ADMIN_REALMS + realm + "/roles-by-id/" + keycloakRoleId;

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(deleteRoleUrl))
                    .header(AUTHORIZATION, accessToken).DELETE().build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                LOGGER.info("Role deleted successfully {} " ,response.body());
            } else {
                throw new RuntimeException("Failed to delete role: " + response.body());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete role: " , e.getMessage());
            throw new RuntimeException("Failed to delete role in Keycloak: " + e.getMessage(), e);
        }
    }

    public String getRoleIdByRoleName(String roleName, String accessToken) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String getRolesUrl = keycloakUrl + ADMIN_REALMS + realm + "/clients/" + clientId + "/roles";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(getRolesUrl))
                .header(AUTHORIZATION, accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> roles = mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {
        });

        Optional<Map<String, Object>> role = roles.stream()
                .filter(r -> roleName.equals(r.get("name")))
                .findFirst();

        if (role.isPresent()) {
            return role.get().get("id").toString();
        } else {
            throw new RuntimeException("Role not found: " + roleName);
        }
    }

    @Override
    public UserRole fetchUserRoleById(String id) {
        return mongoTemplate.findById(id, UserRole.class);
    }

    @Override
    public UserRole fetchUserRoleById(final String id, List<String> fieldList) {
        Query query = QueryParamUtil.prepareFieldsFilter(fieldList, id);
        return mongoTemplate.findOne(query, UserRole.class);
    }

    @Override
    public long countUserRoles(Map<String, Object> requestParams) {
        Query query = new Query();
        if (requestParams.get("_id") != null) {
            query.addCriteria(Criteria.where("_id").is(requestParams.get("_id")));
        }
        if (requestParams.get("involvementRole") != null) {
            Set<String> valuesSet = new HashSet<>();
            String[] values = requestParams.get("involvementRole").toString().split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = URLDecoder.decode(values[i], StandardCharsets.UTF_8);
            }
            valuesSet.addAll(Arrays.asList(values));
            query.addCriteria(Criteria.where("involvementRole").in(valuesSet));
        }
        if (requestParams.get("type") != null) {
            query.addCriteria(Criteria.where("type").is(requestParams.get("type")));
        }
        return mongoTemplate.count(query, UserRole.class);
    }

    @Override
    public List<Entitlement> getEntitlements() {
        try {
            return mongoTemplate.findAll(Entitlement.class);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch entitlements {} " , e.getMessage());
            throw new DiscoClientException("Failed to fetch entitlements: " + e.getMessage());
        }
    }

    @Override
    public List<Entitlement> createEntitlements(List<Entitlement> entitlements) {
        try {
            return (List<Entitlement>) mongoTemplate.insertAll(entitlements);
        } catch (Exception e) {
            LOGGER.error("Failed to save list of entitlement {} " ,  e.getMessage());
            throw new DiscoClientException("Failed to save list of entitlements: " + e.getMessage());
        }
    }

    /**
     * @param requestParams
     * @param offset
     * @param limit
     * @param fields
     * @return
     */
    @Override
    public Map<String, Object> fetchCategoryWithCount(Map<String, Object> requestParams, Long offset, Long limit, String fields) {
        return QueryParamUtil.fetchEntityMap(requestParams, offset, limit, fields, "userRole", UserRole.class,mongoTemplate);
    }

}

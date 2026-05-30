// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.orange.discobole.productinventory.annotation.IntegrationTest;
import com.orange.discobole.productinventory.config.security.SecurityConfigProperties;
import com.orange.discobole.productinventory.dto.Error;
import com.orange.discobole.productinventory.dto.v1.*;
import com.orange.discobole.productinventory.migration.CreateTerminationJobSpecification;
import com.orange.discobole.productinventory.model.ProductEntity;
import com.orange.discobole.productinventory.model.job.JobEntity;
import com.orange.discobole.productinventory.model.job.JobSpecificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.orange.discobole.productinventory.constant.TestConstant.PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
@Slf4j
public abstract class AbstractTest {

    @RegisterExtension
    protected static final WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().asynchronousResponseThreads(10).asynchronousResponseEnabled(true).bindAddress("localhost").port(9273).globalTemplating(true))
            .configureStaticDsl(true).build();
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    protected ProductAssertionUtil productAssertionUtil;
    @Autowired
    protected CacheManager cacheManager;
    @Autowired
    protected SecurityConfigProperties securityConfigProperties;

    public static OffsetDateTime mergeTimeAndDate(OffsetDateTime nextDate, LocalTime executionTime) {
        return nextDate
                .withHour(executionTime.getHour())
                .withMinute(executionTime.getMinute())
                .withSecond(executionTime.getSecond())
                .withNano(0)
                .withOffsetSameLocal(ZoneOffset.UTC);
    }

    public static RequestPostProcessor allOf(RequestPostProcessor... requestPostProcessors) {
        return request -> {
            for (RequestPostProcessor rpp : requestPostProcessors) {
                request = rpp.postProcessRequest(request);
            }
            return request;
        };
    }

    public static RequestPostProcessor contentBody(Object bodyContent) {
        return contentBodyJson(toJsonString(bodyContent));
    }

    public static RequestPostProcessor contentBodyJson(String jsonBodyContent) {
        return request -> {
            request.setContent(jsonBodyContent.getBytes(StandardCharsets.UTF_8));
            return request;
        };
    }

    public static RequestPostProcessor param(Map<String, ?> params) {
        return request -> {
            request.setParameters(params);
            return request;
        };
    }

    public static String toJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static RequestPostProcessor header(String name, Object value) {
        return request -> {
            request.addHeader(name, value.toString());
            return request;
        };
    }

    public static void assertBadRequestErrorExists(Error error, int invalidBodyFieldCode, String message, String invalidInput) {
        Assertions.assertThat(error).hasToString(new Error(invalidBodyFieldCode, invalidInput, message, HttpStatus.BAD_REQUEST).toString());
    }

    public static void assertConflictErrorExists(Error error, int invalidBodyFieldCode, String message, String invalidInput) {
        Assertions.assertThat(error).hasToString(new Error(invalidBodyFieldCode, invalidInput, message, HttpStatus.CONFLICT).toString());
    }

    public static void assertNotImplementedErrorExists(Error error, int invalidBodyFieldCode, String message, String invalidInput) {
        Assertions.assertThat(error).hasToString(new Error(invalidBodyFieldCode, invalidInput, message, HttpStatus.NOT_IMPLEMENTED).toString());
    }

    public static void assertMethodNotAllowedErrorExists(Error error, int invalidBodyFieldCode, String message, String invalidInput) {
        Assertions.assertThat(error).hasToString(new Error(invalidBodyFieldCode, invalidInput, message, HttpStatus.METHOD_NOT_ALLOWED).toString());
    }

    public static void assertNotFoundErrorExists(Error error, int invalidBodyFieldCode, String message, String invalidInput) {
        Assertions.assertThat(error).hasToString(new Error(invalidBodyFieldCode, invalidInput, message, HttpStatus.NOT_FOUND).toString());
    }

    public static void mockCatalogUrl(String urlPattern, String id, String bodyFilePath, int status) throws IOException {
        if (Objects.nonNull(bodyFilePath)) {
            String body = Files.readString(Paths.get(bodyFilePath));
            String[] ids = id.split(",");
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPattern));

            for (String individualId : ids) {
                mappingBuilder.withQueryParam("id", containing(individualId.trim()));
            }

            wireMock.stubFor(mappingBuilder.willReturn(
                    aResponse()
                            .withStatus(status)
                            .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                            .withBody(body)
            ));
        } else {
            wireMock.stubFor(get(urlPathEqualTo(urlPattern)).willReturn(aResponse().withStatus(status)));
        }
    }


    public static void mockCatalogUrl(String urlPattern, String bodyFilePath, int status) throws IOException {
        if (Objects.nonNull(bodyFilePath)) {
            String body = Files.readString(Paths.get(bodyFilePath));
            wireMock.stubFor(get(urlPathEqualTo(urlPattern)).withQueryParam("id", matching(".*")).willReturn(aResponse().withStatus(status).withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType()).withBody(body)));
        } else {
            wireMock.stubFor(get(urlPathEqualTo(urlPattern)).willReturn(aResponse().withStatus(status)));
        }
    }

    public static void mockInventoryUrl(String urlPattern, String id, String bodyFilePath, int status) throws IOException {
        if (Objects.nonNull(bodyFilePath)) {
            String body = Files.readString(Paths.get(bodyFilePath));
            wireMock.stubFor(get(urlPathEqualTo(urlPattern + "/" + id)).willReturn(aResponse().withStatus(status).withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType()).withBody(body)));
        } else {
            wireMock.stubFor(get(urlPathEqualTo(urlPattern + "/" + id)).willReturn(aResponse().withStatus(status)));
        }
    }

    public static void assertAccessDeniedErrorExists(Error error, int errorCode, String message, String errorReason) {
        Assertions.assertThat(error).hasToString(new Error(errorCode, errorReason, message, HttpStatus.FORBIDDEN).toString());
    }

    @AfterEach
    void cleanDatabase() {
        mongoTemplate.remove(new Query(), "products");
        mongoTemplate.remove(new Query(), "jobSpecification");
        mongoTemplate.remove(new Query(), "jobReport");
        mongoTemplate.remove(new Query(), "shedLock");
        mongoTemplate.remove(new Query(), "job");
        Objects.requireNonNull(cacheManager.getCache("catalogCache")).clear();
        Objects.requireNonNull(cacheManager.getCache("resourceInventoryCache")).clear();
        createTerminationJobSpecs();
    }

    private void createTerminationJobSpecs() {
        JobSpecificationEntity jobSpecificationEntity = mongoTemplate.save(CreateTerminationJobSpecification.getTerminationJob());
        mongoTemplate.save(CreateTerminationJobSpecification.getJob(jobSpecificationEntity));
    }

    private String mockAuthority(HttpMethod httpMethod, String auth) {
        String authority = auth != null ? auth : switch (httpMethod.name()) {
            case "POST" -> "x500";
            case "PUT", "PATCH" -> "x502";
            case "GET" -> "x501";
            case "DELETE" -> "x503";
            default -> throw new UnsupportedOperationException(httpMethod + " is not supported!");
        };
        return authority;
    }

    private MockHttpServletRequestBuilder buildRequest(HttpMethod httpMethod, String url, String auth, String relatedPartyId, boolean includeAdminRole, RequestPostProcessor... requestPostProcessors) {
        String authority = mockAuthority(httpMethod, auth);
        RequestPostProcessor authProcessor = includeAdminRole
            ? loginWithAuthority(authority, relatedPartyId)
            : loginWithAuthorityWithoutAdminRole(authority, relatedPartyId);

        return switch (httpMethod.name()) {
            case "POST" ->
                    MockMvcRequestBuilders.post(url).with(allOf(requestPostProcessors)).with(authProcessor);
            case "PUT" ->
                    MockMvcRequestBuilders.put(url).with(allOf(requestPostProcessors)).with(authProcessor);
            case "PATCH" ->
                    MockMvcRequestBuilders.patch(url).with(allOf(requestPostProcessors)).with(authProcessor);
            case "GET" ->
                    MockMvcRequestBuilders.get(url).with(allOf(requestPostProcessors)).with(authProcessor);
            case "DELETE" ->
                    MockMvcRequestBuilders.delete(url).with(allOf(requestPostProcessors)).with(authProcessor);
            default -> throw new UnsupportedOperationException(httpMethod + " is not supported!");
        };
    }

    public ResultActions callRestfulEndpoint(MockMvc mockMvc, HttpMethod httpMethod, String url, RequestPostProcessor... requestPostProcessors) {
        MockHttpServletRequestBuilder requestBuilder = buildRequest(httpMethod, url, null, null, true, requestPostProcessors);
        return execute(mockMvc, requestBuilder);
    }

    public ResultActions callRestfulEndpoint(MockMvc mockMvc, HttpMethod httpMethod, String url, String auth, RequestPostProcessor... requestPostProcessors) {
        MockHttpServletRequestBuilder requestBuilder = buildRequest(httpMethod, url, auth, null, true, requestPostProcessors);
        return execute(mockMvc, requestBuilder);
    }

    public ResultActions callRestfulEndpointWithRelatedPartyIdAuth(MockMvc mockMvc, HttpMethod httpMethod, String url, String auth, String relatedPartyId, RequestPostProcessor... requestPostProcessors) {
        MockHttpServletRequestBuilder requestBuilder = buildRequest(httpMethod, url, auth, relatedPartyId, true, requestPostProcessors);
        return execute(mockMvc, requestBuilder);
    }

    /**
     * Calls REST endpoint with authentication as a NON-ADMIN user (for IDOR protection tests)
     * This method does NOT add the admin role to the user's authorities
     */
    public ResultActions callRestfulEndpointAsNonAdminUser(MockMvc mockMvc, HttpMethod httpMethod, String url, String auth, String relatedPartyId, RequestPostProcessor... requestPostProcessors) {
        MockHttpServletRequestBuilder requestBuilder = buildRequest(httpMethod, url, auth, relatedPartyId, false, requestPostProcessors);
        return execute(mockMvc, requestBuilder);
    }

    public ResultActions callRestfulEndpointWithMultipart(MockMvc mockMvc, HttpMethod httpMethod, String url, String auth, MockPart file, String jobId, RequestPostProcessor... requestPostProcessors) throws IOException {
        String authority = mockAuthority(httpMethod, auth);
        if (httpMethod == HttpMethod.POST) {
            MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);
            requestBuilder.param("jobId", jobId);
            requestBuilder.with(allOf(requestPostProcessors)).with(loginWithAuthority(authority));
            if (Objects.nonNull(file)) {
                requestBuilder.part(file);
            }
            return execute(mockMvc, requestBuilder);
        }
        return null;
    }

    protected RequestPostProcessor loginWithAuthority(String authority) {
        return loginWithAuthority(authority, null, true);
    }

    protected RequestPostProcessor loginWithAuthority(String authority, String relatedPartyId) {
        return loginWithAuthority(authority, relatedPartyId, true);
    }

    /**
     * Creates authentication with or without admin role
     * @param authority The authority to grant
     * @param relatedPartyId The relatedPartyId claim (optional)
     * @param includeAdminRole Whether to include the admin role
     */
    private RequestPostProcessor loginWithAuthority(String authority, String relatedPartyId, boolean includeAdminRole) {
        String adminRole = (securityConfigProperties != null && securityConfigProperties.getDiscoAdminRole() != null)
            ? securityConfigProperties.getDiscoAdminRole()
            : "disco-admin"; // Fallback to default admin role

        // Always use JWT (not OIDC) for consistency with SecurityConfig which uses oauth2ResourceServer().jwt()
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtProcessor = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    // Create Keycloak-style resource_access claim
                    Map<String, List<String>> gatewayRoles = new HashMap<>();

                    if (includeAdminRole) {
                        // Include both the specified authority AND admin role
                        gatewayRoles.put("roles", List.of(authority, adminRole));
                    } else {
                        // Only the specified authority, NO admin role
                        gatewayRoles.put("roles", List.of(authority));
                    }

                    Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();
                    resourceAccess.put("gateway", gatewayRoles);

                    jwt.claim("resource_access", resourceAccess);

                    // Add relatedPartyId claim if provided
                    if (relatedPartyId != null) {
                        jwt.claim("relatedPartyId", relatedPartyId);
                    }

                    // Add standard JWT claims
                    jwt.claim("preferred_username", "test.user@orange.com");
                    jwt.claim("email", "test.user@orange.com");
                    jwt.claim("name", "Test User");
                });

        // Add authorities
        if (includeAdminRole) {
            jwtProcessor.authorities(new SimpleGrantedAuthority(authority), new SimpleGrantedAuthority(adminRole));
        } else {
            jwtProcessor.authorities(new SimpleGrantedAuthority(authority));
        }

        return jwtProcessor;
    }

    /**
     * Creates authentication WITHOUT admin role - for IDOR protection tests
     * This ensures the user is treated as a regular user, not an admin
     * Uses JWT (not OIDC) to properly set the relatedPartyId claim
     */
    protected RequestPostProcessor loginWithAuthorityWithoutAdminRole(String authority, String relatedPartyId) {
        return loginWithAuthority(authority, relatedPartyId, false);
    }

    public ResultActions execute(MockMvc mockMvc, RequestBuilder requestBuilder) {
        try {
            return mockMvc.perform(requestBuilder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readJsonFromAPIResponse(ResultActions resultActions, TypeReference<T> typeReference) {
        T result;

        try {
            String json = resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            result = objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public int readStatusFromAPIResponse(ResultActions resultActions) {
        int status;
        try {
            status = resultActions.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getStatus();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public <T> T getHeaderValueFromAPIResponse(ResultActions resultActions, String header, TypeReference<T> typeReference) {
        T result;
        try {
            result = (T) resultActions.andReturn().getResponse().getHeaderValue(header);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void assertListProductDtoEqualsToListProductEntity(List<Product> products, List<ProductEntity> productEntities, boolean compareRelationRecursively) {
        productAssertionUtil.assertListProductDtoEqualsToListProductEntity(products, productEntities, compareRelationRecursively);
    }

    public void assertProductDtoEqualsToProductEntity(Product product, ProductEntity productEntity, boolean compareRelationRecursively, String... additionalSkippedFields) {
        productAssertionUtil.assertProductDtoEqualsToProductEntity(product, productEntity, compareRelationRecursively, additionalSkippedFields);
    }

    protected List<JobEntity> assertAndGetTerminationJobCreatedFromSpec(String terminationJobSpecification) {
        Query query = new Query();
        query.addCriteria(Criteria.where(JobEntity.Fields.jobSpecification + "." + JobSpecificationEntity.Fields.id).is(terminationJobSpecification));
        List<JobEntity> scheduledJobEntities = mongoTemplate.find(query, JobEntity.class);
        org.junit.jupiter.api.Assertions.assertEquals(1, scheduledJobEntities.size());
        return scheduledJobEntities;
    }

    protected TerminationJobSpecification createTerminationJobSpecificationWithImmediateJobScheduler() throws Exception {
        String jobSpecification = toJsonString(TerminationJobSpecification.builder().atType(JobSpecificationType.TERMINATIONJOBSPECIFICATION.getValue()).schedule(
                ImmediateJobScheduler
                        .builder()
                        .atType(JobSchedulerType.IMMEDIATEJOBSCHEDULER.getValue())
                        .build()).build());
        ResultActions resultActions = callRestfulEndpoint(mockMvc, POST, PRODUCT_INVENTORY_MANAGEMENT_V_1_JOB_SPECIFICATION, contentBodyJson(jobSpecification), header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
        return readJsonFromAPIResponse(resultActions, new TypeReference<>() {
        });
    }
}



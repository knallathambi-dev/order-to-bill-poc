// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.Resource;
import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.ResourceCharacteristic;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceInventoryServiceImplTest {
    private static final String VALID_RESOURCE_ID = RandomStringUtils.randomAlphabetic(6);
    private static final String VALID_RESOURCE_ID_2 = RandomStringUtils.randomAlphabetic(6);
    private static final String RESOURCE_SPEC_ID_1 = "ICCID";
    private static final String RESOURCE_SPEC_ID_2 = "MSISDN";
    private static final String BASE_URL = "http://mock-url/resource";
    private static final String BATCH_URL = "http://mock-url/resource/batch";

    @Mock
    private DiscoServiceUrl discoServiceUrl;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ResourceInventoryServiceImpl inventoryResourceService;

    @Test
    @DisplayName("Given null input, when getAvailableResources is called, then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullInputInGetAvailableResources() {
        assertThrows(InvalidParameterException.class,
                () -> inventoryResourceService.getAvailableResources(null));
    }

    @Test
    @DisplayName("Given empty map, when getAvailableResources is called, then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForEmptyMapInGetAvailableResources() {
        Map<String, List<String>> emptyMap = Collections.emptyMap();
        assertThrows(InvalidParameterException.class,
                () -> inventoryResourceService.getAvailableResources(emptyMap));
    }


    @Test
    @DisplayName("Given valid input, when getAvailableResources is called, then return map of available resources")
    void shouldReturnMapOfAvailableResourcesInGetAvailableResources() {
        final Map<String, List<String>> inputMap = Map.of(
                "key1", List.of(RESOURCE_SPEC_ID_1),
                "key2", List.of(RESOURCE_SPEC_ID_2)
        );

        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        resource1.setName(RESOURCE_SPEC_ID_1);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.AVAILABLE);
        resource2.setName(RESOURCE_SPEC_ID_2);

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, List.of(resource1, resource2));

        Map<String, List<Resource>> result = inventoryResourceService.getAvailableResources(inputMap);

        assertEquals(2, result.size());
        assertTrue(result.get("key1").contains(resource1));
        assertTrue(result.get("key2").contains(resource2));
    }

    @Test
    @DisplayName("Given valid resource spec IDs, when getAvailableResourcesCall is called, then return grouped resources")
    void shouldReturnGroupedResourcesInGetAvailableResourcesCall() {
        final List<String> resourceSpecIds = List.of(RESOURCE_SPEC_ID_1, RESOURCE_SPEC_ID_2);

        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        resource1.setName(RESOURCE_SPEC_ID_1);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.AVAILABLE);
        resource2.setName(RESOURCE_SPEC_ID_2);

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, List.of(resource1, resource2));

        Map<String, List<Resource>> result = inventoryResourceService.getAvailableResourcesCall(resourceSpecIds);

        assertEquals(2, result.size());
        assertEquals(1, result.get(RESOURCE_SPEC_ID_1).size());
        assertEquals(1, result.get(RESOURCE_SPEC_ID_2).size());
        assertEquals(resource1, result.get(RESOURCE_SPEC_ID_1).get(0));
        assertEquals(resource2, result.get(RESOURCE_SPEC_ID_2).get(0));
    }


    @Test
    @DisplayName("Given empty response, when getAvailableResourcesCall is called, then return empty map")
    void shouldReturnEmptyMapForEmptyResponseInGetAvailableResourcesCall() {
        List<String> resourceSpecIds = List.of(RESOURCE_SPEC_ID_1);

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, Collections.emptyList());

        Map<String, List<Resource>> result = inventoryResourceService.getAvailableResourcesCall(resourceSpecIds);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given null input, when getReservedResourceList is called, then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForNullInputInGetReservedResourceList() {
        assertThrows(InvalidParameterException.class,
                () -> inventoryResourceService.getReservedResourceList(null));
    }

    @Test
    @DisplayName("Given empty map, when getReservedResourceList is called, then throw InvalidParameterException")
    void shouldThrowInvalidParameterExceptionForEmptyMapInGetReservedResourceList() {
        Map<String, List<Resource>> emptyMap = Collections.emptyMap();
        assertThrows(InvalidParameterException.class,
                () -> inventoryResourceService.getReservedResourceList(emptyMap));
    }

    @Test
    @DisplayName("Given empty resource IDs, when getReservedResourceList is called, then return empty map")
    void shouldReturnEmptyMapForEmptyResourceIdsInGetReservedResourceList() {
        Resource resource = createResource(null, ServiceConstants.AVAILABLE);
        Map<String, List<Resource>> inputMap = Map.of("key1", List.of(resource));

        Map<String, List<Resource>> result = inventoryResourceService.getReservedResourceList(inputMap);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given valid input, when getReservedResourceList is called, then return map of reserved resources")
    void shouldReturnMapOfReservedResourcesInGetReservedResourceList() {
        Resource resourceAvailable = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        Map<String, List<Resource>> inputMap = Map.of("orderItem1", List.of(resourceAvailable));

        Resource resourceReserved = createResource(VALID_RESOURCE_ID, ServiceConstants.RESERVED);
        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponse(HttpStatus.OK, List.of(resourceReserved));

        Map<String, List<Resource>> result = inventoryResourceService.getReservedResourceList(inputMap);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("orderItem1"));
        assertEquals(VALID_RESOURCE_ID, result.get("orderItem1").get(0).getId());
    }

    @Test
    @DisplayName("Given error during reservation, when getReservedResourceList is called, then rollback and throw DiscoException")
    void shouldRollbackAndThrowDiscoExceptionWhenErrorOccursInGetReservedResourceList() {
        Resource resourceAvailable = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        final Map<String, List<Resource>> inputMap = Map.of("orderItem1", List.of(resourceAvailable));

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        ResourceInventoryServiceImpl spy = spy(inventoryResourceService);
        doNothing().when(spy).rollBackResourceAsync(anyList());

        assertThrows(DiscoException.class, () -> spy.getReservedResourceList(inputMap));
        verify(spy, times(1)).rollBackResourceAsync(anyList());
    }

    @Test
    @DisplayName("Given valid resource IDs, when reserveResources is called, then return map of reserved resources")
    void shouldReturnMapOfReservedResourcesInReserveResources() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID, VALID_RESOURCE_ID_2);

        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.RESERVED);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.RESERVED);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponse(HttpStatus.OK, List.of(resource1, resource2));

        Map<String, Resource> result = inventoryResourceService.reserveResources(resourceIds);

        assertEquals(2, result.size());
        assertEquals(resource1, result.get(VALID_RESOURCE_ID));
        assertEquals(resource2, result.get(VALID_RESOURCE_ID_2));
    }


    @Test
    @DisplayName("Given server error, when reserveResources is called, then throw DiscoException")
    void shouldThrowDiscoExceptionForServerErrorInReserveResources() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);

        DiscoException ex = assertThrows(DiscoException.class,
                () -> inventoryResourceService.reserveResources(resourceIds));
        assertEquals(DescriptionConstants.RESOURCE_INVENTORY_SERVICE_UNREACHABLE, ex.getReason());
    }

    @Test
    @DisplayName("Given fully available resources, when checkAndReserveLogicalResources is called, then return map of reserved resources")
    void shouldReturnMapOfReservedResourcesInCheckAndReserveLogicalResources() {
        final Map<String, List<String>> inputMap = Map.of(
                "item1", List.of(RESOURCE_SPEC_ID_1),
                "item2", List.of(RESOURCE_SPEC_ID_2)
        );

        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        resource1.setName(RESOURCE_SPEC_ID_1);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.AVAILABLE);
        resource2.setName(RESOURCE_SPEC_ID_2);

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, List.of(resource1, resource2));

        Resource reserved1 = createResource(VALID_RESOURCE_ID, ServiceConstants.RESERVED);
        Resource reserved2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.RESERVED);
        mockWebClientPatchBatchResponse(HttpStatus.OK, List.of(reserved1, reserved2));

        Map<String, List<Resource>> result = inventoryResourceService.checkAndReserveLogicalResources(inputMap);

        assertEquals(2, result.size());
        assertEquals(VALID_RESOURCE_ID, result.get("item1").get(0).getId());
        assertEquals(VALID_RESOURCE_ID_2, result.get("item2").get(0).getId());
    }

    @Test
    @DisplayName("Given no available resources, when checkAndReserveLogicalResources is called, then return empty map")
    void shouldReturnEmptyMapForNoAvailableResourcesInCheckAndReserveLogicalResources() {
        Map<String, List<String>> inputMap = Map.of("item1", List.of(RESOURCE_SPEC_ID_1));

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, Collections.emptyList());

        Map<String, List<Resource>> result = inventoryResourceService.checkAndReserveLogicalResources(inputMap);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given partial reservation, when checkAndReserveLogicalResources is called, then return empty map")
    void shouldReturnEmptyMapForPartialReservationInCheckAndReserveLogicalResources() {
        final Map<String, List<String>> inputMap = Map.of(
                "item1", List.of(RESOURCE_SPEC_ID_1),
                "item2", List.of(RESOURCE_SPEC_ID_2)
        );

        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.AVAILABLE);
        resource1.setName(RESOURCE_SPEC_ID_1);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.AVAILABLE);
        resource2.setName(RESOURCE_SPEC_ID_2);

        when(discoServiceUrl.getAvailableResourceByIdsUrl(anyString())).thenReturn(BASE_URL);
        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientGetBatchResponse(HttpStatus.OK, List.of(resource1, resource2));

        Resource reserved1 = createResource(VALID_RESOURCE_ID, ServiceConstants.RESERVED);
        mockWebClientPatchBatchResponse(HttpStatus.OK, List.of(reserved1));

        Map<String, List<Resource>> result = inventoryResourceService.checkAndReserveLogicalResources(inputMap);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given null resource IDs, when rollBackReservedResource is called, then return without error")
    void shouldReturnWithoutErrorForNullResourceIdsInRollBackReservedResource() {
        assertDoesNotThrow(() -> inventoryResourceService.rollBackReservedResource(null));
    }

    @Test
    @DisplayName("Given empty resource IDs, when rollBackReservedResource is called, then return without error")
    void shouldReturnWithoutErrorForEmptyResourceIdsInRollBackReservedResource() {
        assertDoesNotThrow(() -> inventoryResourceService.rollBackReservedResource(Collections.emptyList()));
    }

    @Test
    @DisplayName("Given valid resource IDs, when rollBackReservedResource is called, then rollback successfully")
    void shouldRollbackSuccessfullyInRollBackReservedResource() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID, VALID_RESOURCE_ID_2);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponseForRollback(HttpStatus.OK);

        assertDoesNotThrow(() -> inventoryResourceService.rollBackReservedResource(resourceIds));
    }

    @Test
    @DisplayName("Given server error, when rollBackReservedResource is called, then throw DiscoException")
    void shouldThrowDiscoExceptionForServerErrorInRollBackReservedResource() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponseForRollback(HttpStatus.INTERNAL_SERVER_ERROR);

        DiscoException ex = assertThrows(DiscoException.class,
                () -> inventoryResourceService.rollBackReservedResource(resourceIds));
        assertEquals(ExceptionMessage.ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE, ex.getReason());
    }

    @Test
    @DisplayName("Given valid resource IDs, when rollBackResources is called, then rollback successfully")
    void shouldRollbackSuccessfullyInRollBackResources() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID, VALID_RESOURCE_ID_2);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponseForRollback(HttpStatus.OK);

        assertDoesNotThrow(() -> inventoryResourceService.rollBackResources(resourceIds));
    }

    @Test
    @DisplayName("Given server error, when rollBackResources is called, then throw DiscoException")
    void shouldThrowDiscoExceptionForServerErrorInRollBackResources() {
        List<String> resourceIds = List.of(VALID_RESOURCE_ID);

        when(discoServiceUrl.getReserveResourcesUrl()).thenReturn(BATCH_URL);
        mockWebClientPatchBatchResponseForRollback(HttpStatus.INTERNAL_SERVER_ERROR);

        DiscoException ex = assertThrows(DiscoException.class,
                () -> inventoryResourceService.rollBackResources(resourceIds));
        assertEquals(ExceptionMessage.ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE, ex.getReason());
    }

    @Test
    @DisplayName("Given resource list, when rollBackResourceAsync is called, then start async rollback")
    void shouldStartAsyncRollbackInRollBackResourceAsync() {
        Resource resource1 = createResource(VALID_RESOURCE_ID, ServiceConstants.RESERVED);
        Resource resource2 = createResource(VALID_RESOURCE_ID_2, ServiceConstants.RESERVED);
        List<Resource> resourceList = List.of(resource1, resource2);

        ResourceInventoryServiceImpl spy = spy(inventoryResourceService);
        doNothing().when(spy).rollBackReservedResource(anyList());

        spy.rollBackResourceAsync(resourceList);

        verify(spy, timeout(500)).rollBackReservedResource(List.of(VALID_RESOURCE_ID, VALID_RESOURCE_ID_2));
    }

    private Resource createResource(String resourceId, String status) {
        return Resource.builder()
                .id(resourceId)
                .resourceCharacteristic(List.of(ResourceCharacteristic.builder()
                        .name("ICCID")
                        .value("1234")
                        .valueType("String")
                        .type("StringCharacteristic")
                        .build()))
                .resourceStatus(status)
                .build();
    }

    private void mockWebClientGetBatchResponse(HttpStatus status, List<Resource> responseBody) {
        WebClient.RequestHeadersUriSpec<?> uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(uriSpecMock).when(webClient).get();
        doReturn(headersSpecMock).when(uriSpecMock).uri(anyString());
        doReturn(responseSpecMock).when(headersSpecMock).retrieve();

        if (status.is2xxSuccessful() && responseBody != null) {
            ResponseEntity<List<Resource>> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<List<Resource>>> responseMono = Mono.just(responseEntity);
            doReturn(responseMono).when(responseSpecMock).toEntityList(Resource.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(Resource.class);
        }
    }

    private void mockWebClientPatchBatchResponse(HttpStatus status, List<Resource> responseBody) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(requestBodyUriSpecMock).when(webClient).patch();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).header(anyString(), anyString());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();

        if (status.is2xxSuccessful() && responseBody != null) {
            ResponseEntity<List<Resource>> responseEntity = new ResponseEntity<>(responseBody, status);
            Mono<ResponseEntity<List<Resource>>> responseEntityMono = Mono.just(responseEntity);
            doReturn(responseEntityMono).when(responseSpecMock).toEntityList(Resource.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(), status.getReasonPhrase(), null, null, null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(Resource.class);
        }
    }

    private void mockWebClientPatchBatchResponseForRollback(HttpStatus status) {
        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        doReturn(requestBodyUriSpecMock).when(webClient).patch();
        doReturn(requestBodySpecMock).when(requestBodyUriSpecMock).uri(anyString());
        doReturn(requestBodySpecMock).when(requestBodySpecMock).header(anyString(), anyString());
        doReturn(requestHeadersSpecMock).when(requestBodySpecMock).bodyValue(any());
        doReturn(responseSpecMock).when(requestHeadersSpecMock).retrieve();

        if (status.is2xxSuccessful()) {
            ResponseEntity<List<Resource>> responseEntity = new ResponseEntity<>(Collections.emptyList(), status);
            Mono<ResponseEntity<List<Resource>>> responseEntityMono = Mono.just(responseEntity);
            doReturn(responseEntityMono).when(responseSpecMock).toEntityList(Resource.class);
        } else {
            WebClientResponseException exception = WebClientResponseException.create(
                    status.value(),
                    status.getReasonPhrase(),
                    null,
                    null,
                    null
            );
            doReturn(Mono.error(exception)).when(responseSpecMock).toEntityList(Resource.class);
        }
    }
}
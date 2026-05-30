// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.service.impl;

import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTO;
import com.orange.discobole.ordermanagement.commons.dto.operations.PatchDTOList;
import com.orange.discobole.ordermanagement.commons.dto.resource.inventory.Resource;
import com.orange.discobole.ordermanagement.commons.enumeration.PatchOperationType;
import com.orange.discobole.ordermanagement.ordercapture.constant.DescriptionConstants;
import com.orange.discobole.ordermanagement.ordercapture.constant.ExceptionMessage;
import com.orange.discobole.ordermanagement.ordercapture.service.ResourceInventoryService;
import com.orange.discobole.ordermanagement.ordercapture.service.util.DiscoServiceUrl;
import com.orange.discobole.ordermanagement.ordercapture.util.MiscUtil;
import com.orange.discobole.processflow.exception.DiscoException;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orange.discobole.ordermanagement.ordercapture.constant.ServiceConstants.*;

@Component
@Slf4j
public class ResourceInventoryServiceImpl implements ResourceInventoryService {
    private final DiscoServiceUrl discoServiceUrl;
    private final WebClient webClient;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ResourceInventoryServiceImpl(DiscoServiceUrl discoServiceUrl, WebClient webClient) {
        this.discoServiceUrl = discoServiceUrl;
        this.webClient = webClient;
    }

    @Override
    public Map<String, List<Resource>> getAvailableResources(Map<String, List<String>> productOrderItemLogicalResources) {
        if (MiscUtil.isMapEmptyOrContainsNull(productOrderItemLogicalResources)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_RESOURCE_PARAMETERS);
        }

        Set<String> allResourceSpecIds = productOrderItemLogicalResources.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        if (allResourceSpecIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<Resource>> resourcesByCategory = getAvailableResourcesCall(new ArrayList<>(allResourceSpecIds));
        Map<String, List<Resource>> availableResourceList = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : productOrderItemLogicalResources.entrySet()) {
            List<Resource> availableResourcesList = entry.getValue().stream()
                    .map(resourcesByCategory::get)
                    .filter(Objects::nonNull)
                    .filter(resources -> !resources.isEmpty())
                    .flatMap(resources -> resources.stream().findAny().stream())
                    .toList();

            if (!availableResourcesList.isEmpty()) {
                availableResourceList.put(entry.getKey(), availableResourcesList);
            }
        }

        return availableResourceList;
    }


    public Map<String, List<Resource>> getAvailableResourcesCall(List<String> resourceSpecIds) {
        log.info("Getting {} available resources in get available resources call: {}", resourceSpecIds.size(), resourceSpecIds);
        String resourceSpecIdsParam = String.join(",", resourceSpecIds);
        List<Resource> allResources = fetchAvailableResources(resourceSpecIdsParam);
        return groupResourcesBySpecId(allResources, resourceSpecIds);
    }

    public List<Resource> fetchAvailableResources(String resourceSpecIdsParam) {
        String productManagementUri = discoServiceUrl.getAvailableResourceByIdsUrl(resourceSpecIdsParam);
        return webClient.get()
                .uri(productManagementUri)
                .retrieve()
                .toEntityList(Resource.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    log.error("Error getting available resources in get available resources call: {}", error.getMessage(), error);
                    return Mono.error(new DiscoException(DescriptionConstants.RESOURCE_INVENTORY_SERVICE_UNREACHABLE));
                })
                .block();
    }

    private Map<String, List<Resource>> groupResourcesBySpecId(List<Resource> allResources, List<String> resourceSpecIds) {
        if (allResources == null || allResources.isEmpty()) {
            log.warn("Get available resources call returned empty or null resources. Expected resources for: {}", resourceSpecIds);
            return new HashMap<>();
        }

        log.info("Get available resources call successful. Received {} resources", allResources.size());

        Map<String, List<Resource>> groupedResources = resourceSpecIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        resourceSpecId -> allResources.stream()
                                .filter(resource -> resourceSpecId.equals(resource.getName()))
                                .toList()
                ));

        log.debug("Grouped resources by resourceSpecId: {}", groupedResources.keySet());
        return groupedResources;
    }

    @Override
    public Map<String, List<Resource>> getReservedResourceList(Map<String, List<Resource>> productOrderItemLogicalResources) {
        if (MiscUtil.isMapEmptyOrContainsNull(productOrderItemLogicalResources)) {
            throw new InvalidParameterException(ExceptionMessage.INVALID_RESOURCE_PARAMETERS);
        }

        List<Resource> reserveResourceListToBeRolledBack = new ArrayList<>();

        try {
            Map<String, List<Resource>> prodOrderItemReservedResListMap = new HashMap<>();

            for (Map.Entry<String, List<Resource>> entry : productOrderItemLogicalResources.entrySet()) {
                List<String> resourceIds = entry.getValue().stream()
                        .map(Resource::getId)
                        .filter(Objects::nonNull)
                        .toList();

                if (resourceIds.isEmpty()) {
                    continue;
                }

                Map<String, Resource> reservedResourcesMap = reserveResources(resourceIds);

                List<Resource> reserveResourceList = entry.getValue().stream()
                        .map(resource -> reservedResourcesMap.get(resource.getId()))
                        .filter(Objects::nonNull)
                        .toList();

                prodOrderItemReservedResListMap.put(entry.getKey(), reserveResourceList);
                reserveResourceListToBeRolledBack.addAll(reserveResourceList);
            }

            return prodOrderItemReservedResListMap;
        } catch (DiscoException e) {
            rollBackResourceAsync(reserveResourceListToBeRolledBack);
            throw new DiscoException("Error reserving resource");
        }
    }

    public Map<String, Resource> reserveResources(List<String> resourceIds) {
        log.info("Reserving {} resources in reserve resources call", resourceIds.size());
        try {
            PatchDTOList resourcePatch = createResourceReservationPatchRequest(resourceIds, RESERVED);
            List<Resource> reservedResources = updateResources(resourcePatch.toJsonString());

            if (reservedResources == null || reservedResources.isEmpty()) {
                throw new DiscoException("No resources were reserved in reserve resources call");
            }

            return reservedResources.stream()
                    .filter(resource -> resource.getId() != null)
                    .collect(Collectors.toMap(Resource::getId, Function.identity(), (existing, replacement) -> existing));
        } catch (Exception e) {
            log.error("Error reserving resources in reserve resources call: {}", e.getMessage(), e);
            throw new DiscoException(DescriptionConstants.RESOURCE_INVENTORY_SERVICE_UNREACHABLE);
        }
    }

    public List<Resource> updateResources(String jsonPatch) {
        String resourceManagementUri = discoServiceUrl.getReserveResourcesUrl();
        return webClient.patch()
                .uri(resourceManagementUri)
                .header("Content-Type", APPLICATION_JSON_PATCH_JSON)
                .bodyValue(jsonPatch)
                .retrieve()
                .toEntityList(Resource.class)
                .flatMap(responseEntity -> {
                    log.debug(RESPONSE_STATUS_CODE, responseEntity.getStatusCode());
                    log.debug(RESPONSE_BODY, responseEntity.getBody());
                    return Mono.just(Objects.requireNonNull(responseEntity.getBody()));
                })
                .onErrorResume(error -> {
                    log.error("Error updating resources: {}", error.getMessage(), error);
                    return Mono.error(new DiscoException(DescriptionConstants.RESOURCE_INVENTORY_SERVICE_UNREACHABLE));
                })
                .block();
    }

    private PatchDTOList createResourceReservationPatchRequest(List<String> resourceIds, String status) {
        List<PatchDTO> patchDTOList = resourceIds.stream()
                .map(resourceId -> buildResourcePatchRequest(resourceId, status))
                .flatMap(List::stream)
                .toList();

        return PatchDTOList.builder()
                .list(patchDTOList)
                .build();
    }

    private List<PatchDTO> buildResourcePatchRequest(String resourceId, String status) {
        String resourceUri = RESOURCE_INVENTORY_URI + resourceId;
        List<PatchDTO> patches = new ArrayList<>();

        PatchDTO patchResourceStatusDTO = createPatchRequest(resourceUri + RESOURCE_STATUS_URI, status);
        patches.add(patchResourceStatusDTO);
        return patches;
    }

    private PatchDTO createPatchRequest(String path, String value) {
        return PatchDTO.builder()
                .op(PatchOperationType.REPLACE)
                .path(path)
                .value(value)
                .build();
    }

    @Override
    public Map<String, List<Resource>> checkAndReserveLogicalResources(Map<String, List<String>> productOrderItemLogicalResources) {
        Map<String, List<Resource>> productOrderItemAvailableLogicalResources = getAvailableResources(productOrderItemLogicalResources);
        if (CollectionUtils.isEmpty(productOrderItemAvailableLogicalResources)) {
            return Collections.emptyMap();
        }

        Map<String, List<Resource>> reservedResources = getReservedResourceList(productOrderItemAvailableLogicalResources);
        if (reservedResources.size() != productOrderItemAvailableLogicalResources.size() ||
                reservedResources.values().stream().anyMatch(List::isEmpty)) {
            return Collections.emptyMap();
        }

        return reservedResources;
    }

    @Override
    public void rollBackReservedResource(List<String> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }
        try {
            rollBackResources(resourceIds);
        } catch (Exception e) {
            throw new DiscoException(ExceptionMessage.ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE, e);
        }
    }

    public void rollBackResources(List<String> resourceIds) {
        log.info("Rolling back {} resources in roll back resources call", resourceIds.size());
        try {
            PatchDTOList resourcePatch = createResourceReservationPatchRequest(resourceIds, AVAILABLE);
            updateResources(resourcePatch.toJsonString());
        } catch (Exception e) {
            log.error("Error rolling back resources in roll back resources call: {}", e.getMessage(), e);
            throw new DiscoException(ExceptionMessage.ERROR_WHILE_ROLLING_BACK_RESERVED_RESOURCE);
        }
    }

    public void rollBackResourceAsync(List<Resource> idList) {
        List<String> resourceIdList = idList.stream().map(Resource::getId).toList();
        new Thread(() -> this.rollBackReservedResource(resourceIdList)).start();
    }
}
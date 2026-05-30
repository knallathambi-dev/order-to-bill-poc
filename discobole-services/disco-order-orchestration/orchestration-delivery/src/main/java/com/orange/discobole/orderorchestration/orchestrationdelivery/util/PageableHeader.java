// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.OrchestrationManagementApiImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.dto.v1.OrchestrationPlan;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageableHeader {

    public static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";

    public static final String X_RESULT_COUNT_HEADER = "X-Result-Count";

    private static final String LINK_HEADER = "Link";

    private static final String REL_SELF = "self";

    private static final String REL_FIRST = "first";

    private static final String REL_NEXT = "next";

    private static final String REL_PREV = "prev";

    private static final String REL_LAST = "last";

    private PageableHeader() {
    }

    public static HttpHeaders buildPaginationHeaders(OrchestrationPlanFilter orchestrationPlanFilter, long totalCount, Integer resultCount, List<String> sorts) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_TOTAL_COUNT_HEADER, String.valueOf(totalCount));
        headers.add(X_RESULT_COUNT_HEADER, String.valueOf(resultCount));
        String links = getLinks(orchestrationPlanFilter, totalCount, sorts);
        headers.add(LINK_HEADER, links);
        headers.setAccessControlExposeHeaders(List.of(X_TOTAL_COUNT_HEADER, X_RESULT_COUNT_HEADER, LINK_HEADER));
        return headers;
    }

    private static String getLinks(OrchestrationPlanFilter orchestrationPlanFilter, long totalCount, List<String> sorts) {
        List<String> linksList = new ArrayList<>();

        // Add "self" link if not on the first page
        linksList.add(getLink(REL_SELF, orchestrationPlanFilter, sorts, totalCount));

        // Add "first" link if not on the first page
        linksList.add(getLink(REL_FIRST, orchestrationPlanFilter, sorts, totalCount));

        // Add "prev" link if not on the first page
        linksList.add(getLink(REL_PREV, orchestrationPlanFilter, sorts, totalCount));

        // Add "next" link if not on the last page
        linksList.add(getLink(REL_NEXT, orchestrationPlanFilter, sorts, totalCount));

        // Add "last" link if not on the last page
        linksList.add(getLink(REL_LAST, orchestrationPlanFilter, sorts, totalCount));

        linksList.removeIf(String::isBlank);
        return StringUtils.collectionToDelimitedString(linksList, ",");
    }

    private static String getLink(String rel, OrchestrationPlanFilter filter, List<String> sorts, long totalCount) {
        int offsetForRel = getOffsetForRel(rel, filter, totalCount);

        // Skip if the link should be excluded
        if (offsetForRel < 0) {
            return "";
        }

        UriComponents uriComponents = getOrchestrationPlansApiUriComponentsBuilder(filter, sorts, offsetForRel);

        String uriString = uriComponents.toUriString();

        return String.format("<%s>;rel=\"%s\"", uriString, rel);
    }

    /**
     * <i>getOrchestrationPlansApiUriComponentsBuilder uses Hateoas WebMvcLinkBuilder to generate uri component for incoming</i>
     * <i>request with its filter, we used if condition to set the enum manually as the default was using the enum name and not its value</i>
     * <i>so, we setting them with null in getOrchestrationPlansMethod so that, Hateoas doesn't add it automatically</i>
     *
     * @param filter       orchestration plan filter coming in the current request
     * @param sorts        sort direction in the incoming request
     * @param offsetForRel rel offset for each link
     * @return uriComponent with the link of the API method
     */
    private static @NotNull UriComponents getOrchestrationPlansApiUriComponentsBuilder(OrchestrationPlanFilter filter, List<String> sorts, int offsetForRel) {
        UriComponentsBuilder uriComponentsBuilder = WebMvcLinkBuilder.linkTo(getOrchestrationPlansMethod(filter, offsetForRel, sorts))
                .toUriComponentsBuilder();
        if (Objects.nonNull(filter.getState())) {
            uriComponentsBuilder.queryParam("state", filter.getState());
        }
        if (Objects.nonNull(filter.getOrchestrationPlanNodesState())) {
            uriComponentsBuilder.queryParam("orchestrationPlanNodes.state", filter.getOrchestrationPlanNodesState());
        }
        return uriComponentsBuilder.build();
    }

    private static int getOffsetForRel(String rel, OrchestrationPlanFilter orchestrationPlanFilter, long totalCount) {
        return switch (rel) {
            case REL_SELF -> orchestrationPlanFilter.getOffset();
            case REL_NEXT -> {
                int nextOffset = orchestrationPlanFilter.getOffset() + orchestrationPlanFilter.getLimit();
                yield nextOffset >= totalCount ? -1 : nextOffset;
            }
            case REL_PREV -> {
                int prevOffset = orchestrationPlanFilter.getOffset() - orchestrationPlanFilter.getLimit();
                yield prevOffset < 0 ? -1 : prevOffset;
            }
            case REL_FIRST -> 0;
            case REL_LAST -> {
                int lastOffset = (int) (totalCount - totalCount % orchestrationPlanFilter.getLimit());
                yield lastOffset == totalCount ? lastOffset - orchestrationPlanFilter.getLimit() : lastOffset;
            }
            default -> 0;
        };
    }

    private static ResponseEntity<List<OrchestrationPlan>> getOrchestrationPlansMethod(OrchestrationPlanFilter orchestrationPlanFilter, int offset, List<String> sorts) {
        return WebMvcLinkBuilder
                .methodOn(OrchestrationManagementApiImpl.class)
                .getOrchestrationPlans(orchestrationPlanFilter.getId(),
                        null,
                        checkAndReturnNull(orchestrationPlanFilter.getReceivedDate()),
                        orchestrationPlanFilter.getRelatedPartyId(),
                        orchestrationPlanFilter.getRelatedPartyRole(),
                        orchestrationPlanFilter.getRelatedPartyHref(),
                        orchestrationPlanFilter.getRelatedPartyName(),
                        orchestrationPlanFilter.getRelatedProductOrderId(),
                        null,
                        orchestrationPlanFilter.getOrchestrationPlanNodesRelatedServiceOrderId(),
                        orchestrationPlanFilter.getOrchestrationPlanNodesRelatedProductOrderItemId(),
                        orchestrationPlanFilter.getOrchestrationPlanNodesRelatedProductId(),
                        orchestrationPlanFilter.getFields(),
                        offset,
                        orchestrationPlanFilter.getLimit(),
                        orchestrationPlanFilter.getRequestedDeliveryDate(),
                        sorts,
                        orchestrationPlanFilter.getArchived(),
                        orchestrationPlanFilter.getReceivedDateGte(),
                        orchestrationPlanFilter.getReceivedDateLts(),
                        orchestrationPlanFilter.getRequestedDeliveryDateGte(),
                        orchestrationPlanFilter.getRequestedDeliveryDateLts()
                );
    }

    private static <T> T checkAndReturnNull(T object) {
        return Objects.isNull(object) ? null : object;
    }

}

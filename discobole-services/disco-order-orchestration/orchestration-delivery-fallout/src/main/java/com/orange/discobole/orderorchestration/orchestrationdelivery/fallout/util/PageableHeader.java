// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.util;

import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.FalloutManagementApiImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.controller.filters.FalloutFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.fallout.dto.v1.FalloutIncident;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class PageableHeader {

    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";

    private static final String X_RESULT_COUNT_HEADER = "X-Result-Count";

    private static final String LINK_HEADER = "Link";

    private static final String REL_SELF = "self";

    private static final String REL_FIRST = "first";

    private static final String REL_NEXT = "next";

    private static final String REL_PREV = "prev";

    private static final String REL_LAST = "last";

    private PageableHeader() {
    }

    public static HttpHeaders buildPaginationHeaders(FalloutFilter falloutFilter, long totalCount, Integer resultCount, List<String> sorts) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_TOTAL_COUNT_HEADER, String.valueOf(totalCount));
        headers.add(X_RESULT_COUNT_HEADER, String.valueOf(resultCount));
        String links = getLinks(falloutFilter, totalCount, sorts);
        headers.add(LINK_HEADER, links);
        headers.setAccessControlExposeHeaders(List.of(X_TOTAL_COUNT_HEADER, X_RESULT_COUNT_HEADER, LINK_HEADER));
        return headers;
    }

    private static String getLinks(FalloutFilter falloutFilter, long totalCount, List<String> sorts) {
        List<String> linksList = new ArrayList<>();

        // Add "self" link if not on the first page
        linksList.add(getLink(REL_SELF, falloutFilter, sorts, totalCount));

        // Add "first" link if not on the first page
        linksList.add(getLink(REL_FIRST, falloutFilter, sorts, totalCount));

        // Add "prev" link if not on the first page
        linksList.add(getLink(REL_PREV, falloutFilter, sorts, totalCount));

        // Add "next" link if not on the last page
        linksList.add(getLink(REL_NEXT, falloutFilter, sorts, totalCount));

        // Add "last" link if not on the last page
        linksList.add(getLink(REL_LAST, falloutFilter, sorts, totalCount));

        linksList.removeIf(String::isBlank);
        return StringUtils.collectionToDelimitedString(linksList, ",");
    }

    private static String getLink(String rel, FalloutFilter filter, List<String> sorts, long totalCount) {
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
    private static UriComponents getOrchestrationPlansApiUriComponentsBuilder(FalloutFilter filter, List<String> sorts, int offsetForRel) {
        UriComponentsBuilder uriComponentsBuilder = WebMvcLinkBuilder.linkTo(getFalloutListMethod(filter, offsetForRel, sorts))
                .toUriComponentsBuilder();
        return uriComponentsBuilder.build();
    }

    private static int getOffsetForRel(String rel, FalloutFilter falloutFilter, long totalCount) {
        return switch (rel) {
            case REL_SELF -> falloutFilter.getOffset();
            case REL_NEXT -> {
                int nextOffset = falloutFilter.getOffset() + falloutFilter.getLimit();
                yield nextOffset >= totalCount ? -1 : nextOffset;
            }
            case REL_PREV -> {
                int prevOffset = falloutFilter.getOffset() - falloutFilter.getLimit();
                yield prevOffset < 0 ? -1 : prevOffset;
            }
            case REL_FIRST -> 0;
            case REL_LAST -> {
                int lastOffset = (int) (totalCount - totalCount % falloutFilter.getLimit());
                yield lastOffset == totalCount ? lastOffset - falloutFilter.getLimit() : lastOffset;
            }
            default -> 0;
        };
    }

    private static ResponseEntity<List<FalloutIncident>> getFalloutListMethod(FalloutFilter falloutFilter, int offset,
                                                                              List<String> sorts) {
        return WebMvcLinkBuilder
                .methodOn(FalloutManagementApiImpl.class)
                .getFallouts(falloutFilter.getId(),
                        falloutFilter.getState(),
                        falloutFilter.getRelatedPartyId(),
                        falloutFilter.getRelatedPartyRole(),
                        falloutFilter.getRelatedPartyName(),
                        falloutFilter.getRelatedEntityState(),
                        falloutFilter.getRelatedEntityId(),
                        falloutFilter.getRelatedEntityRole(),
                        falloutFilter.getFields(),
                        offset,
                        falloutFilter.getLimit(),
                        sorts,
                        falloutFilter.getCreationDateGte(),
                        falloutFilter.getCreationDateLte(),
                        falloutFilter.getLastModifiedDateLte(),
                        falloutFilter.getLastModifiedDateGte()
                );
    }

}

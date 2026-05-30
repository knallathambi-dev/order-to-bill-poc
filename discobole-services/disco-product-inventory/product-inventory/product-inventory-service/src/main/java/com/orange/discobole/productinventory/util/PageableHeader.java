// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.util;

import com.orange.discobole.productinventory.dto.PageableTMF;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageableHeader {
    public static final String X_TOTAL_COUNT = "X-Total-Count";

    public static final String X_RESULT_COUNT = "X-Result-Count";

    private static final String LINK_HEADER = "Link";

    private static final String REL_SELF = "self";

    private static final String REL_FIRST = "first";

    private static final String REL_NEXT = "next";

    private static final String REL_PREV = "prev";

    private static final String REL_LAST = "last";

    private static final String OFFSET = "offset";

    private PageableHeader() {
    }

    public static HttpHeaders buildPaginationHeaders(PageableTMF pageable, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_TOTAL_COUNT, String.valueOf(pageable.getTotalCount()));
        headers.add(X_RESULT_COUNT, String.valueOf(pageable.getResultCount()));
        if (Objects.nonNull(pageable.getOffset())) {
            String links = getLinks(pageable, request);
            headers.add(LINK_HEADER, links);
        }
        headers.setAccessControlExposeHeaders(List.of(X_TOTAL_COUNT, X_RESULT_COUNT, LINK_HEADER));
        return headers;
    }

    private static String getLinks(PageableTMF pageable, HttpServletRequest request) {
        List<String> linksList = new ArrayList<>();

        linksList.add(getLink(REL_SELF, pageable, request));

        linksList.add(getLink(REL_FIRST, pageable, request));

        linksList.add(getLink(REL_PREV, pageable, request));

        linksList.add(getLink(REL_NEXT, pageable, request));

        linksList.add(getLink(REL_LAST, pageable, request));

        linksList.removeIf(String::isBlank);
        return StringUtils.collectionToDelimitedString(linksList, ",");
    }

    private static String getLink(String rel, PageableTMF pageable, HttpServletRequest request) {
        // TODO : check security vulnerabilities
        int offsetForRel = getOffsetForRel(rel, pageable.getOffset(), pageable.getLimit(), pageable.getTotalCount());
        if (offsetForRel < 0) {
            return "";
        }
        var paramMap = request.getParameterMap();
        var uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        paramMap.forEach((key, values) -> {
            if (key.equals(OFFSET)) {
                uriBuilder.queryParam(key, offsetForRel);
            } else {
                uriBuilder.queryParam(key, values);
            }
        });
        var uriString = uriBuilder.encode().toUriString();
        return String.format("<%s>;rel=\"%s\"", uriString, rel);
    }

    private static int getOffsetForRel(String rel, Integer offset, Integer limit, long totalCount) {
        return switch (rel) {
            case REL_SELF -> offset;
            case REL_NEXT -> (offset + limit) < totalCount ? offset + limit : -1;
            case REL_PREV -> offset - limit < 0 ? -1 : offset - limit;
            case REL_FIRST -> offset > -1 ? 0 : -1;
            case REL_LAST -> (int) ((totalCount - 1) / limit) * limit;
            default -> 0;
        };
    }
}

// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.util.impl;

import com.orange.discobole.orderorchestration.orchestrationdelivery.controller.filters.OrchestrationPlanFilter;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.EnumMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapper;
import com.orange.discobole.orderorchestration.orchestrationdelivery.mapper.OrchestrationPlanMapperImpl;
import com.orange.discobole.orderorchestration.orchestrationdelivery.util.PageableHeader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(value = SpringExtension.class)
@ContextConfiguration(classes = {
        OrchestrationPlanMapperImpl.class,
        EnumMapperImpl.class
})
class PageableHeaderTest {
    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";

    private static final String X_RESULT_COUNT_HEADER = "X-Result-Count";

    private static final String LINK_HEADER = "Link";

    @Autowired
    @SuppressWarnings("PMD.UnusedPrivateField")
    private OrchestrationPlanMapper orchestrationPlanMapper;

    @Test
    void givenOrchestrationPlanFilterAndTotalCountAndSort_whenSetPagination_thenReturnHttpHeaders() {

        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .offset(0)
                .limit(100)
                .build();
        long expectedTotalCount = 1L;
        Integer orchestrationPlanListSize = 1;
        List<String> sorts = List.of();

        HttpHeaders actualHeaders = PageableHeader.buildPaginationHeaders(orchestrationPlanFilter, expectedTotalCount, orchestrationPlanListSize, sorts);

        Assertions.assertThat(actualHeaders.getFirst(X_TOTAL_COUNT_HEADER))
                .isEqualTo(String.valueOf(expectedTotalCount));


        Assertions.assertThat(actualHeaders.getFirst(X_RESULT_COUNT_HEADER))
                .isEqualTo(String.valueOf(orchestrationPlanListSize));

        Assertions.assertThat(actualHeaders.getFirst(LINK_HEADER))
                .isEqualTo("</orchestrationPlan?offset=0&limit=100>;rel=\"self\"," +
                        "</orchestrationPlan?offset=0&limit=100>;rel=\"first\"," +
                        "</orchestrationPlan?offset=0&limit=100>;rel=\"last\"");
    }


    @Test
    void givenLimitSmallerThanTotalCount_whenSetPagination_thenReturnHttpHeaders() {

        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .offset(0)
                .limit(10)
                .build();
        long expectedTotalCount = 20L;
        Integer orchestrationPlanListSize = 10;
        List<String> sorts = List.of();

        HttpHeaders actualHeaders = PageableHeader.buildPaginationHeaders(orchestrationPlanFilter, expectedTotalCount, orchestrationPlanListSize, sorts);

        Assertions.assertThat(actualHeaders.getFirst(X_TOTAL_COUNT_HEADER))
                .isEqualTo(String.valueOf(expectedTotalCount));


        Assertions.assertThat(actualHeaders.getFirst(X_RESULT_COUNT_HEADER))
                .isEqualTo(String.valueOf(orchestrationPlanListSize));

        Assertions.assertThat(actualHeaders.getFirst(LINK_HEADER))
                .isEqualTo("</orchestrationPlan?offset=0&limit=10>;rel=\"self\"," +
                        "</orchestrationPlan?offset=0&limit=10>;rel=\"first\"," +
                        "</orchestrationPlan?offset=10&limit=10>;rel=\"next\"," +
                        "</orchestrationPlan?offset=10&limit=10>;rel=\"last\"");
    }


    @Test
    void givenSecondPage_whenSetPagination_thenReturnHttpHeadersWithAllLinks() {

        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .offset(10)
                .limit(10)
                .build();
        long expectedTotalCount = 30L;
        Integer orchestrationPlanListSize = 10;
        List<String> sorts = List.of();

        HttpHeaders actualHeaders = PageableHeader.buildPaginationHeaders(orchestrationPlanFilter, expectedTotalCount, orchestrationPlanListSize, sorts);

        Assertions.assertThat(actualHeaders.getFirst(X_TOTAL_COUNT_HEADER))
                .isEqualTo(String.valueOf(expectedTotalCount));


        Assertions.assertThat(actualHeaders.getFirst(X_RESULT_COUNT_HEADER))
                .isEqualTo(String.valueOf(orchestrationPlanListSize));

        Assertions.assertThat(actualHeaders.getFirst(LINK_HEADER))
                .isEqualTo("</orchestrationPlan?offset=10&limit=10>;rel=\"self\"," +
                        "</orchestrationPlan?offset=0&limit=10>;rel=\"first\"," +
                        "</orchestrationPlan?offset=0&limit=10>;rel=\"prev\"," +
                        "</orchestrationPlan?offset=20&limit=10>;rel=\"next\"," +
                        "</orchestrationPlan?offset=20&limit=10>;rel=\"last\"");
    }

    @Test
    void givenLastPage_whenSetPagination_thenReturnHttpHeadersWithoutNextLink() {

        OrchestrationPlanFilter orchestrationPlanFilter = OrchestrationPlanFilter.builder()
                .offset(20)
                .limit(10)
                .build();
        long expectedTotalCount = 30L;
        Integer orchestrationPlanListSize = 10;
        List<String> sorts = List.of();

        HttpHeaders actualHeaders = PageableHeader.buildPaginationHeaders(orchestrationPlanFilter, expectedTotalCount, orchestrationPlanListSize, sorts);

        Assertions.assertThat(actualHeaders.getFirst(X_TOTAL_COUNT_HEADER))
                .isEqualTo(String.valueOf(expectedTotalCount));


        Assertions.assertThat(actualHeaders.getFirst(X_RESULT_COUNT_HEADER))
                .isEqualTo(String.valueOf(orchestrationPlanListSize));

        Assertions.assertThat(actualHeaders.getFirst(LINK_HEADER))
                .isEqualTo("</orchestrationPlan?offset=20&limit=10>;rel=\"self\"," +
                        "</orchestrationPlan?offset=0&limit=10>;rel=\"first\"," +
                        "</orchestrationPlan?offset=10&limit=10>;rel=\"prev\"," +
                        "</orchestrationPlan?offset=20&limit=10>;rel=\"last\"");
    }
}

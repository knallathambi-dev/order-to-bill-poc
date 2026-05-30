// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.orderinventory.util;

import com.orange.discobole.ordermanagement.orderinventory.dto.v1.ProductOrder;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.util.DoubleComparator;
import org.assertj.core.util.FloatComparator;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static com.orange.discobole.ordermanagement.orderinventory.constant.Constant.HREF_FIELD;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductOrderAssertionUtil {

    private static final RecursiveComparisonConfiguration recursiveComparisonConfiguration;

    static {
        recursiveComparisonConfiguration = RecursiveComparisonConfiguration.builder()
                .withComparatorForType(new FloatComparator(0.001f), Float.class)
                .withComparatorForType(new DoubleComparator(0.001d), Double.class)
                .withComparatorForType(new InstantComparator(), Instant.class)
                .withComparatorForType(new ListComparator(), List.class)
                .build();
    }

    public static void assertProductEqualsToProduct(ProductOrder productOrder, ProductOrder productOrder1, String... additionalSkippedFields) {
        String[] skippedFields = {HREF_FIELD};

        assertThat(productOrder).usingRecursiveComparison(recursiveComparisonConfiguration)
                .ignoringFields(mergeArrays(skippedFields, additionalSkippedFields))
                .isEqualTo(productOrder1);
    }

    public static void assertListProductEqualsToListProduct(List<ProductOrder> productOrders, List<ProductOrder> productOrders1) {
        assertThat(productOrders).hasSameSizeAs(productOrders1);
        if (!CollectionUtils.isEmpty(productOrders)) {
            for (int i = 0; i < productOrders.size(); i++) {
                assertProductEqualsToProduct(productOrders.get(i), productOrders1.get(i));
            }
        }
    }

    public static String[] mergeArrays(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        String[] mergedArray = new String[length1 + length2];

        // Copy elements from array1 to mergedArray
        System.arraycopy(array1, 0, mergedArray, 0, length1);

        // Copy elements from array2 to mergedArray
        System.arraycopy(array2, 0, mergedArray, length1, length2);

        return mergedArray;
    }

    static class InstantComparator implements Comparator<Instant> {
        @Override
        public int compare(Instant dt1, Instant dt2) {
            return dt1.truncatedTo(java.time.temporal.ChronoUnit.MINUTES).compareTo(dt2.truncatedTo(java.time.temporal.ChronoUnit.MINUTES));
        }
    }

    static class ListComparator implements Comparator<List> {
        @Override
        public int compare(List dt1, List dt2) {
            if (CollectionUtils.isEmpty(dt1) && CollectionUtils.isEmpty(dt2)) {
                return 0;
            }
            if (CollectionUtils.isEmpty(dt1)) {
                return -1;
            }
            if (CollectionUtils.isEmpty(dt2)) {
                return 1;
            }
            if (dt1.size() != dt2.size()) {
                return Integer.compare(dt1.size(), dt2.size());
            }
            for (int i = 0; i < dt1.size(); i++) {
                Object item1 = dt1.get(i);
                Object item2 = dt2.get(i);
                try {
                    assertThat(item1)
                            .usingRecursiveComparison(recursiveComparisonConfiguration)
                            .ignoringFields(HREF_FIELD)
                            .isEqualTo(item2);
                } catch (AssertionError assertionError) {
                    int comparisonResult = compareObjects(item1, item2);
                    if (comparisonResult != 0) {
                        return comparisonResult;
                    }
                }
            }
            return 0;
        }

        private int compareObjects(Object o1, Object o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (o1 instanceof Comparable && o2 instanceof Comparable) {
                return ((Comparable) o1).compareTo(o2);
            }
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    }
}
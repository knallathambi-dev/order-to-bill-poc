// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.ordermanagement.ordercapture.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;


class MiscUtilTest {

    @Test
    void testIsMapEmptyOrContainsNull_WithEmptyMap_ReturnsTrue() {
        Map<String, List<String>> map = new HashMap<>();
        assertTrue(MiscUtil.isMapEmptyOrContainsNull(map));
    }

    @Test
    void testIsMapEmptyOrContainsNull_WithNullMap_ReturnsTrue() {
        Map<String, List<String>> map = null;
        assertTrue(MiscUtil.isMapEmptyOrContainsNull(map));
    }

    @Test
    void testIsMapEmptyOrContainsNull_WithNullResourceList_ReturnsTrue() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("key", null);
        assertTrue(MiscUtil.isMapEmptyOrContainsNull(map));
    }

    @Test
    void testIsMapEmptyOrContainsNull_WithEmptyResourceList_ReturnsTrue() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("key", new ArrayList<>());
        assertTrue(MiscUtil.isMapEmptyOrContainsNull(map));
    }

    @Test
    void testIsMapEmptyOrContainsNull_WithNullResource_ReturnsTrue() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> resourceList = new ArrayList<>();
        resourceList.add(null);
        map.put("key", resourceList);
        assertTrue(MiscUtil.isMapEmptyOrContainsNull(map));
    }

    @Test
    void testIsMapEmptyOrContainsNull_WithValidMap_ReturnsFalse() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> resourceList = new ArrayList<>();
        resourceList.add("value");
        map.put("key", resourceList);
        Assertions.assertFalse(MiscUtil.isMapEmptyOrContainsNull(map));
    }
}
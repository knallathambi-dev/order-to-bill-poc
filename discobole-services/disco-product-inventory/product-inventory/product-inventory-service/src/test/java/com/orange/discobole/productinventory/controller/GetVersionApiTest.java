// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.controller;

import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static com.orange.discobole.productinventory.constant.TestConstant.VERSION_V_1_SERVICE_VERSION_URI;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetVersionApiTest extends AbstractTest {


    @Test
    void givenServiceIsRunning_whenGetServiceVersion_thenSuccess() throws Exception {
        ResultActions resultActions = callRestfulEndpoint(mockMvc, GET, VERSION_V_1_SERVICE_VERSION_URI);
        resultActions.andExpect(status().isOk());
    }


}
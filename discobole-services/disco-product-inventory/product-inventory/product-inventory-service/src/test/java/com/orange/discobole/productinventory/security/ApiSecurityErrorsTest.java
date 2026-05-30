// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.security;

import com.orange.discobole.productinventory.util.AbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiSecurityErrorsTest extends AbstractTest {

    public static final String GET_PRODUCT_URI = "/productInventoryManagement/v1/product";

    @Test
    void givenNoTokenInRequest_whenGetProduct_then401() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_PRODUCT_URI));
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void givenTokenWithNoRoles_whenGetProduct_then403() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_PRODUCT_URI).with(SecurityMockMvcRequestPostProcessors.oidcLogin()));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void givenTokenWithDifferentRole_whenGetProduct_then403() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_PRODUCT_URI).with(loginWithAuthority("CreateProduct")));
        resultActions.andExpect(status().isForbidden());
    }
}

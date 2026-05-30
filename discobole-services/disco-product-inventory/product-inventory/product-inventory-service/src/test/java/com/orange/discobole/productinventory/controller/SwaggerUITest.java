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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class SwaggerUITest extends AbstractTest {

    @Test
    void whenOpeningHomePage_thenRedirectToSwaggerUI() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();
        Assertions.assertEquals("/swagger-ui.html", mvcResult.getResponse().getRedirectedUrl());
    }

    @Test
    void whenAccessingSwaggerWithoutLogin_thenSucceed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui.html"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();
        Assertions.assertEquals("/swagger-ui/index.html", mvcResult.getResponse().getRedirectedUrl());
        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/index.html"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }
}

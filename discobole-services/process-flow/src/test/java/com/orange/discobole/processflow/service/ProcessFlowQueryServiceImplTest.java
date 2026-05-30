// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

//package com.orange.disco.processflow.service;
//
//import com.orange.disco.processflow.dto.generated.Characteristic;
//import com.orange.disco.processflow.dto.generated.ProcessFlow;
//import com.orange.disco.processflow.dto.generated.StringCharacteristic;
//import com.orange.disco.processflow.repository.ProcessFlowRepo;
//import com.orange.disco.processflow.service.impl.ProcessFlowQueryServiceImpl;
//import org.junit.Ignore;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//public class ProcessFlowQueryServiceImplTest {
//
//    private final ProcessFlowQueryServiceImpl processFlowQueryService = Mockito.spy(new ProcessFlowQueryServiceImpl());
//    private final ProcessFlowRepo processFlowRepo = Mockito.mock(ProcessFlowRepo.class);
//    private final String PROCESS_FLOW_ID = "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
//
//    @BeforeEach
//    public void setup() {
//        ReflectionTestUtils.setField(processFlowQueryService, "processFlowRepo", processFlowRepo);
//    }
//
//    @Ignore
//    public void findProcessFlowsTest() {
//        List<ProcessFlow> processFLowList = new ArrayList<>();
//        List<Characteristic> characteristics = new ArrayList<>();
//        characteristics.add(
//                new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));
//        ProcessFlow processFlow = new ProcessFlow().id(PROCESS_FLOW_ID).processFlowSpecification("DummyProcess")
//                .characteristic(characteristics);
//        processFLowList.add(processFlow);
//
//        List<ProcessFlow> processFlows = processFlowQueryService.findProcessFlows(Collections.emptyMap());
//        assertNotNull(processFlows);
//        assertEquals(PROCESS_FLOW_ID, processFlows.get(0).getId());
//
//    }
//
//    @Test
//    public void findProcessFlowByIdTest() {
//        List<Characteristic> characteristics = new ArrayList<>();
//        characteristics.add(
//                new StringCharacteristic().value("value_1").name("test_1").valueType(String.class.getSimpleName()));
//        ProcessFlow processFlow = new ProcessFlow().id(PROCESS_FLOW_ID).processFlowSpecification("DummyProcess")
//                .characteristic(characteristics);
//        when(processFlowRepo.findById(anyString())).thenReturn(java.util.Optional.ofNullable(processFlow));
//        processFlowQueryService.findProcessFlowById(PROCESS_FLOW_ID);
//        assertNotNull(processFlow);
//        assertEquals(PROCESS_FLOW_ID, processFlow.getId());
//    }
//}



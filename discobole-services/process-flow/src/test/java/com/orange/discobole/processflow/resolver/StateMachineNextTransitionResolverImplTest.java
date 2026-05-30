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

//package com.orange.disco.processflow.resolver;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.config.StateMachineBuilder;
//import org.springframework.statemachine.transition.Transition;
//
//import com.orange.disco.processflow.resolver.StateMachineNextTransitionResolver;
//import com.orange.disco.processflow.resolver.impl.StateMachineNextTransitionResolverImpl;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class StateMachineNextTransitionResolverImplTest {
//
//    private StateMachineNextTransitionResolver stateMachineNextTransitionResolver = new StateMachineNextTransitionResolverImpl();
//
//    public StateMachine<String, String> buildMachine() throws Exception {
//        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
//        builder.configureStates()
//                .withStates()
//                .initial("S1")
//                .end("SF")
//                .states(new HashSet<String>(Arrays.asList("S1", "S2", "S3", "S4")));
//        builder.configureTransitions().withExternal().source("S1").target("S2").event("FIRST_EVENT")
//                .and().withExternal().source("S2").target("S3").guard(context -> true).event("SECOND_EVENT")
//                .and().withExternal().source("S3").target("S4").event("THIRD_EVENT")
//                .and().withExternal().source("S4").target("SF").event("FINAL_EVENT");
//        return builder.build();
//    }
//
//    @Test
//    public void availableTaskTest() throws Exception {
//        StateMachine<String, String> stateMachine = buildMachine();
//
//        stateMachine.start();
//        stateMachine.sendEvent("FIRST_EVENT");
//        List<Transition<String, String>> transitions = stateMachineNextTransitionResolver.getAvailableTask(stateMachine);
//
//        assertNotNull(transitions);
//        assertTrue(transitions.size() == 1);
//        assertEquals("S2", transitions.get(0).getSource().getId());
//        assertEquals("S3", transitions.get(0).getTarget().getId());
//    }
//
//    @Test
//    public void regionTest() throws Exception {
//        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
//
//        builder.configureStates()
//                .withStates()
//                .initial("S1")
//                .state("S2")
//                .and()
//                .withStates()
//                .parent("S2")
//                .region("R1")
//                .initial("S2I")
//                .state("S21")
//                .state("S22")
//                .end("S2F")
//                .and()
//                .withStates()
//                .parent("S2")
//                .region("R2")
//                .initial("S3I")
//                .state("S31")
//                .end("S3F");
//
//        builder.configureTransitions().withExternal()
//                .source("S1").target("S2").event("enteringRegion")
//                .and().withExternal().source("S2I").target("S22").event("FirstStep");
//
//                StateMachine < String, String > stateMachine = builder.build();
//
//        stateMachine.start();
//        stateMachine.sendEvent("enteringRegion");
//        stateMachine.sendEvent("FirstStep");
//
//        List<Transition<String, String>> transitions = stateMachineNextTransitionResolver.getAvailableTask(stateMachine);
//        assertNotNull(transitions);
//
//    }
//}



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

package com.orange.discobole.processflow.service;
/*
 * package com.orange.bos.envelope.service;
 * 
 * import com.orange.bos.envelope.aggregate.StateMachineAggregate; import
 * com.orange.bos.envelope.command.InitiateProcessFlowCommand; import
 * com.orange.bos.envelope.dto.generated.ProcessFlow; import
 * com.orange.bos.envelope.dto.generated.ProcessFlowCreate; import
 * com.orange.bos.envelope.infra.Publisher; import
 * com.orange.bos.envelope.resolver.StateMachineNextTransitionResolver; import
 * com.orange.bos.envelope.service.impl.ProcessFlowCommandServiceImpl; import
 * com.orange.bos.envelope.ssm.builder.StateMachineModelBuilder; import
 * org.junit.jupiter.api.BeforeEach; import org.junit.jupiter.api.Test; import
 * org.mockito.Mockito; import org.springframework.context.ApplicationContext;
 * import org.springframework.statemachine.ExtendedState; import
 * org.springframework.statemachine.StateMachine; import
 * org.springframework.statemachine.config.ObjectStateMachineFactory; import
 * org.springframework.statemachine.config.model.StateMachineModel; import
 * org.springframework.test.util.ReflectionTestUtils;
 * 
 * import java.util.HashMap; import java.util.List; import java.util.Map; import
 * java.util.UUID;
 * 
 * import static org.junit.Assert.assertEquals; import static
 * org.junit.Assert.assertNotNull; import static
 * org.mockito.ArgumentMatchers.any; import static
 * org.mockito.ArgumentMatchers.eq; import static org.mockito.Mockito.when;
 * 
 * public class ProcessFlowCommandServiceImplTest {
 * 
 * private ProcessFlowCommandServiceImpl processFlowCommandService =
 * Mockito.spy(new ProcessFlowCommandServiceImpl());
 * 
 * private ApplicationContext context = Mockito.spy(ApplicationContext.class);
 * 
 * private StateMachineNextTransitionResolver<String, String> transitionResolver
 * = Mockito.spy(StateMachineNextTransitionResolver.class);
 * 
 * private StateMachineAggregate stateMachineAggregate = Mockito.spy(new
 * StateMachineAggregate(List.of(),context,transitionResolver));
 * 
 * private StateMachineModelBuilder stateMachineModelBuilder =
 * Mockito.mock(StateMachineModelBuilder.class);
 * 
 * private StateMachineModel<String, String> stateMachineModel =
 * Mockito.mock(StateMachineModel.class);
 * 
 * private ObjectStateMachineFactory<String, String> factory =
 * Mockito.mock(ObjectStateMachineFactory.class);
 * 
 * private StateMachine<String,String> stateMachine =
 * Mockito.mock(StateMachine.class);
 * 
 * private ExtendedState extendedState = Mockito.mock(ExtendedState.class);
 * 
 * private Publisher publisher = Mockito.mock(Publisher.class);
 * 
 * private final String PROCESS_FLOW_ID =
 * "2e05d202-18a0-4e7b-bb3f-88c5dd68954d";
 * 
 * @BeforeEach public void setup() {
 * when(processFlowCommandService.aggregate(List.of(),context,transitionResolver
 * )).thenReturn(stateMachineAggregate);
 * ReflectionTestUtils.setField(processFlowCommandService, "appCtx", context);
 * ReflectionTestUtils.setField(processFlowCommandService,
 * "stateMachineNextTransitionResolver", transitionResolver);
 * ReflectionTestUtils.setField(processFlowCommandService,"publisher",publisher)
 * ; when(context.getBean(eq(StateMachineModelBuilder.class))).thenReturn(
 * stateMachineModelBuilder);
 * when(stateMachineModelBuilder.build(any(String.class))).thenReturn(
 * stateMachineModel);
 * when(stateMachineAggregate.getFactory(stateMachineModel)).thenReturn(factory)
 * ; when(factory.getStateMachine(any(UUID.class))).thenReturn(stateMachine);
 * when(stateMachine.getExtendedState()).thenReturn(extendedState);
 * Map<Object,Object> extendedVariables = new HashMap<>();
 * when(stateMachine.getExtendedState().getVariables()).thenReturn(
 * extendedVariables);
 * when(stateMachine.getUuid()).thenReturn(UUID.fromString(PROCESS_FLOW_ID));
 * 
 * }
 * 
 * 
 * @Test public void triggerInitiateProcessFlowCommand() { ProcessFlowCreate
 * processFlowCreate = new ProcessFlowCreate();
 * processFlowCreate.setProcessFlowSpecification("DummyProcess");
 * 
 * InitiateProcessFlowCommand initiateProcessFlowCommand = new
 * InitiateProcessFlowCommand(processFlowCreate); ProcessFlow processFLow =
 * processFlowCommandService.createProcessFlow(processFlowCreate);
 * assertNotNull(processFLow);
 * assertEquals(PROCESS_FLOW_ID,processFLow.getId()); } }
 */
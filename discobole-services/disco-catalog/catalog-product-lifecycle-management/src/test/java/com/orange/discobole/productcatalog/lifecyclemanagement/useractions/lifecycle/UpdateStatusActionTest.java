// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.useractions.lifecycle;
  
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.orange.discobole.processflow.exception.DiscoManagedClientException;
import  org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orange.discobole.processflow.dto.generated.Characteristic;
import com.orange.discobole.processflow.dto.generated.CharacteristicSpecification;
import com.orange.discobole.processflow.dto.generated.ObjectCharacteristic;
import com.orange.discobole.processflow.dto.generated.TaskFlowUpdate;
import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.dto.StateMachineTransition;
import com.orange.discobole.productcatalog.lifecyclemanagement.ManageLifeCycleApplicationTests;
import com.orange.discobole.productcatalog.lifecyclemanagement.constant.LifeCycleConstants;
import com.orange.discobole.productcatalog.lifecyclemanagement.dto.generated.common.LifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.lifecyclemanagement.lifecyclemanagement.LifeCycleManager;
import com.orange.discobole.productcatalog.lifecyclemanagement.pojo.DefineLifecycleState;
import com.orange.discobole.productcatalog.lifecyclemanagement.service.LifeCycleService;

  class UpdateStatusActionTest extends ManageLifeCycleApplicationTests {
  
  @InjectMocks private UpdateStatusAction updateStatusAction;
  
  @Mock private LifeCycleService lifeCycleService;
  
  @Mock private LifeCycleManager lifeCycleManager;
  
  @Mock private StateMachineTransition stateMachineTransition;

  @Mock private AccessTokenInterceptor accessTokenInterceptor;
  
  private final Map<String, Object> variablesFromUserActions = new HashMap<>();

  private final ObjectMapper objectMapper = new ObjectMapper()
  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
  false).registerModule(new JavaTimeModule());
  
  @BeforeEach void setUp() { 
  Mockito.when(stateMachineTransition.getVariablesFromUserActions()).thenReturn
  (variablesFromUserActions); ReflectionTestUtils.setField(updateStatusAction,
  "objectMapper", objectMapper); 
  updateStatusAction.requiredCharacteristics(stateMachineTransition, null);
      ReflectionTestUtils.setField(updateStatusAction,"accessTokenInterceptor",accessTokenInterceptor);
      Mockito.when(accessTokenInterceptor.getToken()).thenReturn("mockToken");
  }
  
@Test 
void performTest() { 
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate(); 
	List<Characteristic> characteristicList = new ArrayList<>(); 
	DefineLifecycleState defineStateLifecycleData = new DefineLifecycleState();
   defineStateLifecycleData.setState(LifecycleState.ACTIVE);
  
  Characteristic characteristic = new ObjectCharacteristic().value(defineStateLifecycleData).valueType("Object")
  .name(LifeCycleConstants.PRODUCT_UPDATE_STATE);
  characteristicList.add(characteristic);
  taskFlowUpdate.setCharacteristic(characteristicList);
  updateStatusAction.perform(stateMachineTransition, taskFlowUpdate);
  Mockito.verify(lifeCycleService,
  Mockito.times(1)).updateStatus(null,
  defineStateLifecycleData.getState(),null);
  }  
@Test 
void performTestWithNullLifeCycle() { 
	TaskFlowUpdate taskFlowUpdate = new TaskFlowUpdate(); 
	List<Characteristic> characteristicList = new ArrayList<>(); 
	DefineLifecycleState defineStateLifecycleData = new DefineLifecycleState();
   defineStateLifecycleData.setState(null);
  
  Characteristic characteristic = new ObjectCharacteristic().value(defineStateLifecycleData).valueType("Object")
  .name(LifeCycleConstants.PRODUCT_UPDATE_STATE);
  characteristicList.add(characteristic);
  taskFlowUpdate.setCharacteristic(characteristicList);
  try {
  updateStatusAction.perform(stateMachineTransition, taskFlowUpdate);
  }
  catch (DiscoManagedClientException ex) {
	  ex.getLocalizedMessage();
	assertNotNull(ex.getReason());
}
  } 
 
@Test
void requiredCharacteristicsTest() {
	List<CharacteristicSpecification> list= updateStatusAction.requiredCharacteristics(stateMachineTransition, null); 
	 assertNotNull(list);
}

  }
 
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.lifecyclemanagement.service.impl;

import com.orange.discobole.processflow.infra.Publisher;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleEntitySelectEvent;
import com.orange.discobole.productcatalog.lifecyclemanagement.event.lifecycle.LifeCycleStateSelectedEvent;

import jakarta.annotation.Resource;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageLifeCycleEventHandlerService {
@Resource
Publisher publisher;
@EventHandler
public void handle(LifeCycleEntitySelectEvent event) {
	publisher.project(List.of(event));
}
@EventHandler
public void handle(LifeCycleStateSelectedEvent event) {
	publisher.project(List.of(event));
}
}

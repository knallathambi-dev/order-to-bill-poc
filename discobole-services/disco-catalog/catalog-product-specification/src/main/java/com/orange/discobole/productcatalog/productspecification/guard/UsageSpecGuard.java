// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productspecification.guard;

import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;
import com.orange.discobole.productcatalog.productspecification.dto.generated.servicespec.ServiceSpecification;
import com.orange.discobole.productcatalog.productspecification.interceptor.AccessTokenInterceptor;
import com.orange.discobole.productcatalog.productspecification.service.QueryService;

import jakarta.annotation.Resource;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * UsageSpecGuard defines Condition to be executed when state reaches selectUsageSpec state.
 *
 * @author Sunny Srivastava
 * @since 1.0
 */
@Component("isSkippable")
public class UsageSpecGuard implements StateMachineGuard<String, String> {

    @Resource
    private QueryService queryService;

    @Resource
    private AccessTokenInterceptor accessTokenInterceptor;

    @Override
    public Mono<Boolean> apply(StateContext<String, String> context) {
        ServiceSpecification serviceSpecification = queryService.getServiceSpecById(String.valueOf(context.
                getExtendedState().getVariables().get("serviceSpecId")), accessTokenInterceptor.getToken());
        return Mono.just(!serviceSpecification.getUsageSpecification().isEmpty());
    }
}

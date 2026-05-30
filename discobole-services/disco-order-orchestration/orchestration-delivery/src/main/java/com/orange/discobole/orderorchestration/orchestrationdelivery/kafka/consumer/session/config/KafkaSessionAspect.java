// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config;

import com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.service.KafkaConsumerSession;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * KafkaSessionAspect class.
 *
 * This aspect is used to manage Kafka consumer session scope.
 * It ensures that a new Kafka consumer session is started before processing a message
 * and ended after processing the message.
 */
@Aspect
@Component
public class KafkaSessionAspect {

    /**
     * Starts a new Kafka consumer session scope.
     * This method is executed before any method annotated with @KafkaSessionScope.
     * RequestContextHolder used to initiate new context for any kafka session as it make use of the request context to have copy one for kafka context
     * kafkaConsumerSession attribute proved that it has new reference id for any kafka consumer execution
     */
    @Before("@annotation(com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope)")
    public void startKafkaSessionScope() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            requestAttributes = new KafkaRequestAttributes();
            RequestContextHolder.setRequestAttributes(requestAttributes);
        }

        if (requestAttributes.getAttribute("kafkaConsumerSession", RequestAttributes.SCOPE_REQUEST) == null) {
            requestAttributes.setAttribute("kafkaConsumerSession", new KafkaConsumerSession(), RequestAttributes.SCOPE_REQUEST);
        }
    }

    /**
     * Ends the current Kafka consumer session scope.
     * This method is executed after any method annotated with @KafkaSessionScope.
     */
    @After("@annotation(com.orange.discobole.orderorchestration.orchestrationdelivery.kafka.consumer.session.config.KafkaSessionScope)")
    public void endKafkaSessionScope() {
        RequestContextHolder.resetRequestAttributes();
    }
}
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.consumers;

import com.mongodb.MongoException;
import com.orange.discobole.orderorchestration.exception.model.CoodException;
import com.orange.discobole.orderorchestration.exception.model.CoodRecoverableAndNonRetryableException;
import com.orange.discobole.orderorchestration.exception.model.constants.CoodError;
import com.orange.discobole.orderorchestration.exception.model.constants.ExceptionCode;
import com.orange.discobole.orderorchestration.exception.utils.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.util.backoff.FixedBackOff;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConsumerConfig extends RetryTopicConfigurationSupport {

    @Override
    protected void manageNonBlockingFatalExceptions(List<Class<? extends Throwable>> nonBlockingFatalExceptions) {
        nonBlockingFatalExceptions.addAll(Arrays.asList(
                CoodRecoverableAndNonRetryableException.class
        ));
        log.debug("Configured non-blocking fatal exceptions: {}", nonBlockingFatalExceptions);
    }

    // This is mainly for Kafka errors to be retried in the same topic
    @Override
    protected void configureBlockingRetries(BlockingRetriesConfigurer blockingRetries) {
        blockingRetries
                .retryOn(MongoException.class, KafkaException.class)
                .backOff(new FixedBackOff(5000, 5)); // 5 seconds delay, 5 retry attempts
        log.debug("Configured blocking retries for MongoException and KafkaException with a fixed backoff of 5 seconds and 5 retry attempts.");
    }

    @Override
    protected java.util.function.Consumer<DeadLetterPublishingRecovererFactory> configureDeadLetterPublishingContainerFactory() {
        return (factory) -> {
            factory.setDeadLetterPublisherCreator((templateResolver, destinationResolver) -> {
                DeadLetterPublishingRecoverer customDLPR = new SkipUnrecoverableRecordsDLPR(templateResolver, destinationResolver);
                log.debug("Created custom DeadLetterPublishingRecoverer.");
                customDLPR.setExceptionHeadersCreator((kafkaHeaders, exception, isKey, headerNames) -> {
                    // Workaround because default headers are not added when using setExceptionHeadersCreator
                    // and addHeadersFunction is not executed or working at all (not adding any custom header)
                    callAddExceptionInfoHeaders(customDLPR, kafkaHeaders, exception, isKey, headerNames);
                    log.debug("Called addExceptionInfoHeaders for custom DLPR. Headers before adding custom headers: {}", kafkaHeaders);
                    addCustomHeadersForRecoverableCOODExceptions(kafkaHeaders, exception);
                    log.debug("Added custom headers for recoverable COOD exceptions. Headers after adding custom headers: {}", kafkaHeaders);
                });
                return customDLPR;
            });
        };
    }

    private static void addCustomHeadersForRecoverableCOODExceptions(Headers kafkaHeaders, Exception exception) {
        if (exception instanceof ListenerExecutionFailedException ex) {
            Throwable rootCause = ExceptionUtil.getRootCause(exception);

            if (Objects.nonNull(rootCause) && rootCause instanceof CoodException foundCoodException) {
                // Add headers for CoodException
                kafkaHeaders.add(new RecordHeader("exception-reason",
                        foundCoodException.getCoodError().reason().getBytes(StandardCharsets.UTF_8)));
                kafkaHeaders.add(new RecordHeader("exception-message",
                        foundCoodException.getCoodError().message().getBytes(StandardCharsets.UTF_8)));
                kafkaHeaders.add(new RecordHeader("exception-code",
                        foundCoodException.getCoodError().code().getBytes(StandardCharsets.UTF_8)));
                log.debug("Added COOD headers: reason={}, message={}, code={}",
                        foundCoodException.getCoodError().reason(),
                        foundCoodException.getCoodError().message(),
                        foundCoodException.getCoodError().code());

                return;
            }

            // Fallback: non-CoodException → add COOD_TECHNICAL_EXCEPTION
            String message = Optional.ofNullable(rootCause)
                    .map(Throwable::getMessage)
                    .orElse("Unknown technical error");

            CoodError fallback = new CoodError(
                    ExceptionCode.COOD_TECHNICAL_EXCEPTION.getCode(),
                    ExceptionCode.COOD_TECHNICAL_EXCEPTION.getMessagePattern().formatted(message),
                    ExceptionCode.COOD_TECHNICAL_EXCEPTION.getReason(),
                    Instant.now()
            );
            kafkaHeaders.add(new RecordHeader("exception-reason",
                    fallback.reason().getBytes(StandardCharsets.UTF_8)));
            kafkaHeaders.add(new RecordHeader("exception-message",
                    fallback.message().getBytes(StandardCharsets.UTF_8)));
            kafkaHeaders.add(new RecordHeader("exception-code",
                    fallback.code().getBytes(StandardCharsets.UTF_8)));
            log.debug("Added fallback COOD_TECHNICAL_EXCEPTION headers for message={}", message);
        }
    }

    private void callAddExceptionInfoHeaders(DeadLetterPublishingRecoverer instance, Headers kafkaHeaders, Exception exception, boolean isKey, DeadLetterPublishingRecoverer.HeaderNames headerNames) {
        try {
            // Get the method object for the private method
            Method method = DeadLetterPublishingRecoverer.class.getDeclaredMethod("addExceptionInfoHeaders", Headers.class, Exception.class, boolean.class, DeadLetterPublishingRecoverer.HeaderNames.class);
            method.setAccessible(true); // Make the method accessible
            // Invoke the method on the provided instance
            method.invoke(instance, kafkaHeaders, exception, isKey, headerNames);
            log.debug("Successfully called addExceptionInfoHeaders on DeadLetterPublishingRecoverer instance.");
        } catch (Exception e) {
            log.error("Failed to call addExceptionInfoHeaders on DeadLetterPublishingRecoverer instance.", e);
        }
    }
}

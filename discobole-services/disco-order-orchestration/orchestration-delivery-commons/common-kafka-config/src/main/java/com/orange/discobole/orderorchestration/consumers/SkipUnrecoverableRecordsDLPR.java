// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.orderorchestration.consumers;

import com.fasterxml.jackson.core.JsonParseException;
import com.orange.discobole.orderorchestration.exception.model.CoodNonRecoverableAndNonRetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;

import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class SkipUnrecoverableRecordsDLPR extends DeadLetterPublishingRecoverer {


    public SkipUnrecoverableRecordsDLPR(Function<ProducerRecord<?, ?>, KafkaOperations<?, ?>> templateResolver, BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> destinationResolver) {
        super(templateResolver, destinationResolver);
    }

    public SkipUnrecoverableRecordsDLPR(KafkaOperations<? extends Object, ? extends Object> template) {
        super(template);
    }

    //this to allow the skip from dlt option as it is not supported if there is DLT
    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> consumer, Exception exception) {
        log.debug("SkipUnrecoverableRecordsDLPR invoked with record: {}", consumerRecord);
        if (exception instanceof ListenerExecutionFailedException ex) {
            Throwable rootCause = ex.getRootCause();
            log.error("ListenerExecutionFailedException root cause", ex);
            if (ExceptionUtils.indexOfThrowable(ex, SerializationException.class) != -1 ||
                    ExceptionUtils.indexOfThrowable(ex, JsonParseException.class) != -1 ||
                    ExceptionUtils.indexOfThrowable(ex, CoodNonRecoverableAndNonRetryableException.class) != -1 ||
                    ExceptionUtils.indexOfThrowable(ex, DeserializationException.class) != -1) {
                log.error("Root cause is an unrecoverable exception, handling differently to avoid sending to DLT", rootCause);
                skipMessageFromGoingToNextTopic(consumerRecord, consumer, exception);
            } else {
                log.info("Root cause is a recoverable exception, invoking super.accept");
                super.accept(consumerRecord, consumer, exception);
            }
        } else {
            log.info("Exception is not ListenerExecutionFailedException, handling differently to avoid infinite retry");
            skipMessageFromGoingToNextTopic(consumerRecord, consumer, exception);
        }
    }

    private void skipMessageFromGoingToNextTopic(ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> consumer, Exception exception) {
        log.warn("Handling unrecoverable exception: {}, record: {}", exception, consumerRecord);
        consumer.seek(new TopicPartition(consumerRecord.topic(), consumerRecord.partition()), consumerRecord.offset() + 1);
    }
}
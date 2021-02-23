package com.happn.poc.kafka.infrastructure;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.util.backoff.BackOff;

import java.util.List;
import java.util.Optional;

import static org.springframework.kafka.support.KafkaHeaders.OFFSET;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;

@Slf4j
public class KafkaRepeatableWithBackOffLoggingErrorHandler extends SeekToCurrentErrorHandler {

    public KafkaRepeatableWithBackOffLoggingErrorHandler(BackOff backOff) {
        super(backOff);
    }

    @Override
    public void handle(
            Exception thrownException,
            List<ConsumerRecord<?, ?>> records,
            Consumer<?, ?> consumer,
            MessageListenerContainer container
    ) {
        Throwable rootCauseException = Throwables.getRootCause(thrownException);
        if (rootCauseException instanceof MethodArgumentNotValidException) {
            handleMethodArgumentNotValid((MethodArgumentNotValidException) rootCauseException);
            return;
        }
        super.handle(thrownException, records, consumer, container);
    }

    private void handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Optional.ofNullable(exception.getBindingResult())
                .ifPresent(bindingResult -> log.warn("Invalid message at {} {}", kafkaMessageIdentification(exception.getFailedMessage()), bindingResult.getAllErrors()));
    }

    private String kafkaMessageIdentification(Message<?> message) {
        return Optional.ofNullable(message)
                .map(Message::getHeaders)
                .map(headers -> "[" + headers.get(RECEIVED_TOPIC) + ":" + headers.get(RECEIVED_PARTITION_ID) + ":" + headers.get(OFFSET) + "]")
                .orElse("");
    }
}

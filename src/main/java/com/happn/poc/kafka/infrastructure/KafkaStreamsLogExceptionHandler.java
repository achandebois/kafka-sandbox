package com.happn.poc.kafka.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;

@Slf4j
public class KafkaStreamsLogExceptionHandler extends DefaultProductionExceptionHandler {

    @Override
    public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> record, Exception exception) {
        log.error("Unhandled exceptions has been thrown in KafkaStreams. Streams will be closed", exception);
        return super.handle(record, exception);
    }
}

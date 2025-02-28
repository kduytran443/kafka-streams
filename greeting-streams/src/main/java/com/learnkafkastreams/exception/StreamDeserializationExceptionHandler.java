package com.learnkafkastreams.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

@Slf4j
public class StreamDeserializationExceptionHandler implements DeserializationExceptionHandler {

    private int counter = 0;

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext, ConsumerRecord<byte[], byte[]> consumerRecord, Exception e) {
        log.error("Exception is: {}, and Kafka record is: {}", e.getMessage(), consumerRecord, e);
        log.info("Error counter: {}", counter);
        if (counter < 2) {
            counter++;
            return DeserializationHandlerResponse.CONTINUE;
        }
        return DeserializationHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> map) {}
}

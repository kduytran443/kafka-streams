package com.learnkafkastreams.serdes.greeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafkastreams.domain.Greeting;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

public class GreetingDeserializer implements Deserializer<Greeting> {

    private final ObjectMapper objectMapper;

    public GreetingDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Greeting deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Greeting.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

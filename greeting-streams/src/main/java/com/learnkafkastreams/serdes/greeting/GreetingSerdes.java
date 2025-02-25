package com.learnkafkastreams.serdes.greeting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafkastreams.domain.Greeting;
import com.learnkafkastreams.serdes.SerdesUtil;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class GreetingSerdes implements Serde<Greeting> {

    private final ObjectMapper objectMapper = SerdesUtil.objectMapper();

    @Override
    public Serializer<Greeting> serializer() {
        return new GreetingSerializer(objectMapper);
    }

    @Override
    public Deserializer<Greeting> deserializer() {
        return new GreetingDeserializer(objectMapper);
    }
}

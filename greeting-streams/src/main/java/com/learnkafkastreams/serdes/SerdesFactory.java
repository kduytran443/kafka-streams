package com.learnkafkastreams.serdes;

import com.learnkafkastreams.domain.Greeting;
import com.learnkafkastreams.serdes.greeting.GreetingSerdes;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory {

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }

    public static Serde<Greeting> greetingSerdesUsingGeneric() {
        CustomJsonSerializer<Greeting> serializer = new CustomJsonSerializer<>(SerdesUtil.objectMapper());
        CustomJsonDeserializer<Greeting> deserializer = new CustomJsonDeserializer<>(SerdesUtil.objectMapper(), Greeting.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}

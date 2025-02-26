package com.learnkafkastreams.serdes;

import com.learnkafkastreams.domain.Order;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory {

    public static Serde<Order> orderSerdes() {
        CustomJsonSerializer<Order> serializer = new CustomJsonSerializer<>(SerdesUtil.objectMapper());
        CustomJsonDeserializer<Order> deserializer = new CustomJsonDeserializer<>(SerdesUtil.objectMapper(), Order.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}

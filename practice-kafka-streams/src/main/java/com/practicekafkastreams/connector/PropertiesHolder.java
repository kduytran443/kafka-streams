package com.practicekafkastreams.connector;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class PropertiesHolder {

    public static Properties getProperties() {
        Properties boostrapProperties = new Properties();
        boostrapProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-stream-app");
        boostrapProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        boostrapProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return boostrapProperties;
    }
}

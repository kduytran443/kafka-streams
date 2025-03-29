package com.practicekafkastreams.connector;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaStreamsConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsConnector.class);

    public static void connect(Properties properties, Topology topology) {
        try {
            var kafkaStreams = new KafkaStreams(topology, properties);

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

            // Start kafka streams
            kafkaStreams.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}

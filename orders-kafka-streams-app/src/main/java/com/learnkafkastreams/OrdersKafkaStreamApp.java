package com.learnkafkastreams;


import com.learnkafkastreams.topology.OrdersTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class OrdersKafkaStreamApp {

    private static Properties getProperties() {
        Properties boostrapProperties = new Properties();
        boostrapProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-stream-app");
        boostrapProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        boostrapProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return boostrapProperties;
    }

    public static void main(String[] args) {
        log.info("CPU Core: {}", Runtime.getRuntime().availableProcessors());

        Properties boostrapProperties = getProperties();
        createTopics(boostrapProperties, List.of(OrdersTopology.TOPIC_ORDERS, OrdersTopology.TOPIC_STORES));

        Topology topology = OrdersTopology.buildTopology();

        // Create an instance of KafkaStreams
        var kafkaStreams = new KafkaStreams(topology, boostrapProperties);

        // This closes the streams anytime the JVM shuts down normally or abruptly.
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        try {
            kafkaStreams.start();
        } catch (Exception e ) {
            log.error("Exception in starting the Streams : {}", e.getMessage(), e);
        }
    }

    private static void createTopics(Properties config, List<String> topics) {
        AdminClient admin = AdminClient.create(config);
        var partitions = 1;
        short replication  = 1;

        var newTopics = topics
                .stream()
                .map(topic -> new NewTopic(topic, partitions, replication))
                .collect(Collectors.toList());

        var createTopicResult = admin.createTopics(newTopics);
        try {
            createTopicResult.all().get();
            log.info("topics are created successfully");
        } catch (Exception e) {
            log.error("Exception creating topics : {} ",e.getMessage(), e);
        }
    }
}

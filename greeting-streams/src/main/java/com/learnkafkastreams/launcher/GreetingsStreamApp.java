package com.learnkafkastreams.launcher;

import com.learnkafkastreams.topology.GreetingsTopology;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.internals.metrics.ClientMetrics;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class GreetingsStreamApp {

    static {
        try (InputStream resourceStream = ClientMetrics.class.getResourceAsStream("/kafka/kafka-streams-version.properties")) {
            new Properties().load(resourceStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        Properties boostrapProperties = getProperties();

        // Create topics
        createTopics(boostrapProperties, List.of(GreetingsTopology.GREETING, GreetingsTopology.GREETING_UPPERCASE));

        var greetingsTopology = GreetingsTopology.buildTopology();
        try {
            var kafkaStreams = new KafkaStreams(greetingsTopology, boostrapProperties);
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

            // Start kafka streams
            kafkaStreams.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Properties getProperties() {
        Properties boostrapProperties = new Properties();
        boostrapProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-stream-app");
        boostrapProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        boostrapProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return boostrapProperties;
    }

    private static void createTopics(Properties config, List<String> topics) {
        AdminClient admin = AdminClient.create(config);
        var partitions = 1;
        short replication  = 1;

        var newTopics = topics
                .stream()
                .map(topic ->{
                    return new NewTopic(topic, partitions, replication);
                })
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

package com.practicekafkastreams.connector;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class TopicCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicCreator.class);

    public static void createNewTopics(Properties properties, List<NewTopic> newTopics) {
        AdminClient adminClient = AdminClient.create(properties);
        var createdTopics = adminClient.createTopics(newTopics);
        try {
            createdTopics.all().get();
            LOGGER.info("Topics created successfully");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}

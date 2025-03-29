package com.practicekafkastreams.greetingstreams;

import com.practicekafkastreams.connector.KafkaStreamsConnector;
import com.practicekafkastreams.connector.PropertiesHolder;
import com.practicekafkastreams.connector.TopicCreator;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Properties;

public class GreetingStreamsMain {

    public static void main(String[] args) {
        Properties properties = PropertiesHolder.getProperties();

        // Create all needed topics
        TopicCreator.createNewTopics(properties, List.of(
                new NewTopic(GreetingTopology.GREETING_TOPIC, 1, (short) 1),
                new NewTopic(GreetingTopology.HANDLED_GREETING_TOPIC, 1, (short) 1)
        ));

        KafkaStreamsConnector.connect(properties, GreetingTopology.build());
    }
}

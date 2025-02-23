package com.learnkafkastreams.topology;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

@Slf4j
public class GreetingsTopology {

    public static final String GREETING = "practice_topic_greetings";
    public static final String GREETING_UPPERCASE = "practice_topic_greetings_uppercase";

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Create greetingStream by defining Topic, Serdes
        var greetingsStream = builder.stream(GREETING, Consumed.with(Serdes.String(), Serdes.String()));

        greetingsStream.peek((key, value) -> log.info("[GREETING] {}: {}", key, value));

        // Processing logic
        var modifiedStream = greetingsStream.mapValues((readOnlyKey, value) -> value.toUpperCase());

        // Sink processor
        modifiedStream.to(GREETING_UPPERCASE, Produced.with(Serdes.String(), Serdes.String()));
        modifiedStream.peek((key, value) -> log.info("[GREETING_UPPERCASE] {}: {}", key, value));

        return builder.build();
    }
}

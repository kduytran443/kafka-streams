package com.learnkafkastreams.topology;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;

@Slf4j
public class ExploreKTableTopology {

    public static final String TOPIC_WORDS = "topic_words";

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        var wordTable = builder.table(TOPIC_WORDS, Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as("words_store"));

        wordTable.filter((key, value) -> value.length() > 3)
                .mapValues(value -> value.toUpperCase())
                .toStream()
                .peek((key, value) -> log.info("[KTable] {}: {}", key, value));

        return builder.build();
    }
}

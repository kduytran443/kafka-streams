package com.practicekafkastreams.greetingstreams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class GreetingTopology {

    public static final String GREETING_TOPIC = "pt-greeting-topic";
    public static final String HANDLED_GREETING_TOPIC = "pt-handled_greeting-topic";

    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        var streams = builder.stream(GREETING_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));
        streams.print(Printed.<String, String>toSysOut().withLabel("greeting-source"));

        streams.mapValues(value -> value.toUpperCase())
                .to(HANDLED_GREETING_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }
}

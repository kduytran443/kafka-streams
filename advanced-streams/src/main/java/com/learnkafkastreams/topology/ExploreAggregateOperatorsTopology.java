package com.learnkafkastreams.topology;

import com.learnkafkastreams.domain.AlphabetWordAggregate;
import com.learnkafkastreams.serdes.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class ExploreAggregateOperatorsTopology {


    public static String AGGREGATE = "aggregate";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        var inputStream = streamsBuilder.stream(AGGREGATE, Consumed.with(Serdes.String(), Serdes.String()));

        var groupedStream = inputStream.groupByKey(Grouped.with(Serdes.String(), Serdes.String()));
        exploreCount(groupedStream);

        return streamsBuilder.build();
    }

    private static void exploreCount(KGroupedStream<String, String> groupedStream) {
        KTable<String, Long> countStream = groupedStream.count(Named.as("count-per-alphabet"));
        countStream.toStream().print(Printed.<String, Long>toSysOut().withLabel("count-per-alphabet"));
    }
}

package com.learnkafkastreams.topology;

import com.learnkafkastreams.domain.Alphabet;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

@Slf4j
public class ExploreJoinsOperatorsTopology {


    public static String ALPHABETS = "alphabets"; // A => First letter in the english alphabet
    public static String ALPHABETS_ABBREVIATIONS = "alphabets_abbreviations"; // A=> Apple

    public static Topology build(){
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        joinKStreamWithKTable(streamsBuilder);
        joinKStreamWithGlobalKTable(streamsBuilder);

        return streamsBuilder.build();
    }

    private static void joinKStreamWithGlobalKTable(StreamsBuilder builder) {
        var alphabetStream = builder.stream(ALPHABETS, Consumed.with(Serdes.String(), Serdes.String()));
        alphabetStream.print(Printed.<String, String>toSysOut().withLabel("alphabets"));

        var alphabetAbbreviationGlobalTable = builder.globalTable(ALPHABETS_ABBREVIATIONS, Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as("alphabets_abbreviations-global-store"));

        ValueJoiner<String, String, Alphabet> joiner = Alphabet::new;

        KeyValueMapper<String, String, String> keyValueMapper = (leftKey, rightKey) -> leftKey;

        var joinedStream = alphabetStream.join(alphabetAbbreviationGlobalTable, keyValueMapper, joiner);
        joinedStream.print(Printed.<String, Alphabet>toSysOut().withLabel("joined-stream"));
    }

    private static void joinKStreamWithKTable(StreamsBuilder builder) {
        var alphabetStream = builder.stream(ALPHABETS, Consumed.with(Serdes.String(), Serdes.String()));
        alphabetStream.print(Printed.<String, String>toSysOut().withLabel("alphabets_abbreviations"));

        var alphabetAbbreviationTable = builder.table(ALPHABETS_ABBREVIATIONS, Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as("alphabets_abbreviations-store"));

        ValueJoiner<String, String, Alphabet> joiner = Alphabet::new;

        var joinedStream = alphabetStream.join(alphabetAbbreviationTable, joiner);
        joinedStream.print(Printed.<String, Alphabet>toSysOut().withLabel("joined-stream"));
    }
}

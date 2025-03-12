package com.learnkafkastreams.topology;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ExploreWindowTopology {

    public static final String WINDOW_WORDS = "windows-words";

    public static Topology build(){
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> kStream = streamsBuilder.stream(WINDOW_WORDS);
        exploreTumblingWindow(kStream);

        return streamsBuilder.build();
    }

    private static void exploreTumblingWindow(KStream<String, String> kStream) {
        kStream.print(Printed.<String, String>toSysOut().withLabel("windows-words-beginning"));

        Duration duration = Duration.ofSeconds(5);
        TimeWindows tumblingWindow = TimeWindows.ofSizeWithNoGrace(duration);

        var windowStream = kStream.groupByKey()
                .windowedBy(tumblingWindow)
                .count();

        windowStream.toStream().peek(ExploreWindowTopology::printLocalDateTimes);
    }

    private static void printLocalDateTimes(Windowed<String> key, Long value) {
        var startTime = key.window().startTime();
        var endTime = key.window().endTime();

        LocalDateTime startLDT = LocalDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        LocalDateTime endLDT = LocalDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        log.info("startLDT : {} , endLDT : {}, Count : {}", startLDT, endLDT, value);
    }

}

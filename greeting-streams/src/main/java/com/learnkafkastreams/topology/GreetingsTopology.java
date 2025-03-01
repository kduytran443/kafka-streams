package com.learnkafkastreams.topology;

import com.learnkafkastreams.domain.Greeting;
import com.learnkafkastreams.serdes.SerdesFactory;
import com.learnkafkastreams.service.DeadLetterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GreetingsTopology {

    public static final String TOPIC_GREETINGS = "practice_topic_greetings";
    public static final String TOPIC_SPANISH_GREETINGS = "practice_topic_spanish_greetings";
    public static final String TOPIC_GREETINGS_UPPERCASE = "practice_topic_greetings_uppercase";

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Create greetingStream by defining Topic, Serdes
        var greetingsStream = builder.stream(TOPIC_GREETINGS, Consumed.with(Serdes.String(),
                SerdesFactory.greetingSerdesUsingGeneric()));
        greetingsStream.peek((key, value) -> log.info("[GREETING] {}: {}", key, value));

        var spanishGreetingsStream = builder.stream(TOPIC_SPANISH_GREETINGS,
                Consumed.with(Serdes.String(), SerdesFactory.greetingSerdesUsingGeneric()));
        spanishGreetingsStream.peek((key, value) -> log.info("[SPANISH] {}: {}", key, value));

        var mergedStream = greetingsStream.merge(spanishGreetingsStream);

        List<StreamExceptionHandler<String, Greeting, ? extends Exception>> handlers = List.of(
                StreamExceptionHandler.of(IllegalArgumentException.class, (key, value) -> {
                    log.error("ERROR in StreamExceptionHandler {}", value);
                }, true),
                StreamExceptionHandler.of(NullPointerException.class, (key, value) -> {
                    log.error("Bad null pointer {}", value);
                }, true)
        );

        // Processing logic
        var modifiedStream = mergedStream
                .peek((key, value) -> log.info("Before: {}", value))
//                .mapValues(value -> {
//                    try {
//                        handleUppercaseWithException(value);
//                    } catch (Exception e) {
//                        log.error(e.getMessage(), e);
//                        return null;
//                    }
//                    return value;
//                })
                .map(SafeStreamHandler.safeHandleForMap((key, value) -> {
                    handleUppercaseWithException(value);
                    return KeyValue.pair(key, value);
                }, new DeadLetterService(), handlers))
                .filter((key, value) -> value != null);

        // Sink processor
        modifiedStream.to(TOPIC_GREETINGS_UPPERCASE, Produced.with(Serdes.String(), SerdesFactory.greetingSerdesUsingGeneric()));
        modifiedStream.peek((key, value) -> log.info("[GREETING_UPPERCASE] {}: {}", key, value));

        return builder.build();
    }

    private static void handleUppercaseWithException(Greeting greeting) {
        if ("ERROR".equals(greeting.getMessage())) {
            throw new IllegalArgumentException();
        }
        greeting.setMessage(greeting.getMessage().toUpperCase());
    }

    public static Topology buildOldTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Create greetingStream by defining Topic, Serdes
        var greetingsStream = builder.stream(TOPIC_GREETINGS, Consumed.with(Serdes.String(), Serdes.String()));
        greetingsStream.peek((key, value) -> log.info("[GREETING] {}: {}", key, value));

        var spanishGreetingsStream = builder.stream(TOPIC_SPANISH_GREETINGS, Consumed.with(Serdes.String(), Serdes.String()));
        spanishGreetingsStream.peek((key, value) -> log.info("[SPANISH] {}: {}", key, value));

        var mergedStream = greetingsStream.merge(spanishGreetingsStream);

        // Processing logic
        var modifiedStream = mergedStream
                .peek((key, value) -> log.info("Skip = {}", value.toUpperCase().equals(value)))
                .filterNot((key, value) -> value.toUpperCase().equals(value)) // Skip already Upper text
                .map((readOnlyKey, value) -> KeyValue.pair(readOnlyKey.toUpperCase(), value.toUpperCase()))
                .flatMap((key, value) -> {
                    var newValues = Arrays.asList(value.split(""));
                    return newValues.stream().map(val -> KeyValue.pair(key, val)).toList();
                });

        // Sink processor
        modifiedStream.to(TOPIC_GREETINGS_UPPERCASE, Produced.with(Serdes.String(), Serdes.String()));
        modifiedStream.peek((key, value) -> log.info("[GREETING_UPPERCASE] {}: {}", key, value));

        return builder.build();
    }
}

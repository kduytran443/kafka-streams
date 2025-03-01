package com.learnkafkastreams.topology;

import com.learnkafkastreams.service.DeadLetterService;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.List;
import java.util.function.BiConsumer;

public class SafeStreamHandler {

    public static <K, V> KeyValueMapper<K, V, KeyValue<K, V>> safeHandleForMap(
            KeyValueMapper<K, V, KeyValue<K, V>> processor,
            DeadLetterService dltService,
            List<StreamExceptionHandler<K, V, ? extends Exception>> handlers) {
        return (key, value) -> {
            try {
                return processor.apply(key, value);
            } catch (Exception e) {
                for (var handler : handlers) {
                    handler.handle(key, value, e);
                    if (!handler.isForwardToDLT()) return KeyValue.pair(key, null);
                }
                dltService.sendToDLT("key", value, e);
                return KeyValue.pair(key, null);
            }
        };
    }
}

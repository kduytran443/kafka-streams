package com.learnkafkastreams.topology;

import java.util.function.BiConsumer;

public class StreamExceptionHandler<K, V, E extends Exception> {
    private final Class<E> exceptionClass;
    private final BiConsumer<K, V> handler;
    private final boolean forwardToDLT;

    private StreamExceptionHandler(Class<E> exceptionClass, BiConsumer<K, V> handler, boolean forwardToDLT) {
        this.exceptionClass = exceptionClass;
        this.handler = handler;
        this.forwardToDLT = forwardToDLT;
    }

    public static <K, V, E extends Exception> StreamExceptionHandler<K, V, E> of(
            Class<E> exceptionClass, BiConsumer<K, V> handler, boolean forwardToDLT) {
        return new StreamExceptionHandler<>(exceptionClass, handler, forwardToDLT);
    }

    public boolean handle(K key, V value, Exception e) {
        if (exceptionClass.isInstance(e)) {
            handler.accept(key, value);
            return forwardToDLT;
        }
        return false;
    }

    public boolean isForwardToDLT() {
        return forwardToDLT;
    }
}

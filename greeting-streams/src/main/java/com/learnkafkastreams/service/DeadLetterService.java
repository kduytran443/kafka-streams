package com.learnkafkastreams.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeadLetterService {

    public <V> void sendToDLT(String key, V value, Exception e) {
        log.info("Sent to DLT: {} - {}", key, value, e);
    }
}

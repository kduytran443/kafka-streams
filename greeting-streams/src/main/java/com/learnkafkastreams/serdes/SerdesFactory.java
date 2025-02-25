package com.learnkafkastreams.serdes;

import com.learnkafkastreams.domain.Greeting;
import com.learnkafkastreams.serdes.greeting.GreetingSerdes;
import org.apache.kafka.common.serialization.Serde;

public class SerdesFactory {

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }
}

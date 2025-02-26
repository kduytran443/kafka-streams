package com.learnkafkastreams.topology;

import com.learnkafkastreams.domain.Order;
import com.learnkafkastreams.domain.OrderType;
import com.learnkafkastreams.serdes.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

@Slf4j
public class OrdersTopology {

    public static final String TOPIC_ORDERS = "topic_orders";
    public static final String TOPIC_RESTAURANT_ORDERS = "topic_restaurant_orders";
    public static final String TOPIC_GENERAL_ORDERS = "topic_general_orders";
    public static final String TOPIC_STORES = "topic_stores";

    private static Predicate<String, Order> isRestaurantOrder = (key, order) -> OrderType.RESTAURANT == order.orderType();
    private static Predicate<String, Order> isGeneralOrder = (key, order) -> OrderType.GENERAL == order.orderType();

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Order> orderStreams = builder
                .stream(TOPIC_ORDERS, Consumed.with(Serdes.String(), SerdesFactory.orderSerdes()));

        orderStreams.peek((s, order) -> log.info("[Order {}]: {}", s, order));

        orderStreams.split()
                .branch(isRestaurantOrder, Branched.withConsumer(restaurantOrderStream -> {
                    restaurantOrderStream.peek((s, order) -> log.info("[{}]: {}", order.orderType(), order));
                    restaurantOrderStream.to(TOPIC_RESTAURANT_ORDERS, Produced.with(Serdes.String(), SerdesFactory.orderSerdes()));
                }))
                .branch(isGeneralOrder, Branched.withConsumer(generalOrderStream -> {
                    generalOrderStream.peek((s, order) -> log.info("[{}]: {}", order.orderType(), order));
                    generalOrderStream.to(TOPIC_GENERAL_ORDERS, Produced.with(Serdes.String(), SerdesFactory.orderSerdes()));
                }));

        return builder.build();
    }
}

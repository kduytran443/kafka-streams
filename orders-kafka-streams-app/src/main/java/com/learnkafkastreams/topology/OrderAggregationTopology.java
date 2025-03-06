package com.learnkafkastreams.topology;

import com.learnkafkastreams.domain.Order;
import com.learnkafkastreams.domain.OrderType;
import com.learnkafkastreams.domain.TotalRevenue;
import com.learnkafkastreams.serdes.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class OrderAggregationTopology {

    public static final String STORE_COUNT_ORDERS_BY_LOCATION = "count_orders_by_location_store";
    public static final String STORE_TOTAL_REVENUE_BY_LOCATION = "total_revenue_by_location_store";
    public static final String TOPIC_ORDERS = "topic_orders";
    public static final String TOPIC_RESTAURANT_ORDERS = "topic_restaurant_orders";
    public static final String TOPIC_GENERAL_ORDERS = "topic_general_orders";
    public static final String TOPIC_STORES = "topic_stores";

    private static final Predicate<String, Order> isRestaurantOrder = (key, value) -> value != null && value.orderType() == OrderType.RESTAURANT;
    private static final Predicate<String, Order> isGeneralOrder = (key, value) -> value != null && value.orderType() == OrderType.GENERAL;

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        var orderStream = builder.stream(TOPIC_ORDERS, Consumed.with(Serdes.String(), SerdesFactory.orderSerdes()))
                .peek((key, value) -> log.info("[Receiving Order] {}: {}", key, value));

        orderStream.split()
                .branch(isRestaurantOrder, Branched.withConsumer(stream -> {
                    countOrdersByLocation(stream);
                    calculateOrderRevenue(stream);
                }))
                .branch(isGeneralOrder, Branched.withConsumer(OrderAggregationTopology::countOrdersByLocation));

        return builder.build();
    }

    private static void countOrdersByLocation(KStream<String, Order> orderStream) {
        KTable<String, Long> countStream = orderStream.groupBy((key, value) -> value.locationId())
                .count(Materialized.as(STORE_COUNT_ORDERS_BY_LOCATION));
        countStream.toStream().peek((key, value) -> log.info("[Count Orders By Location] {}: {}", key, value));
    }

    private static void calculateOrderRevenue(KStream<String, Order> orderStream) {
        // Initializer
        Initializer<TotalRevenue> initializer = TotalRevenue::new;

        // Aggregator
        Aggregator<String, Order, TotalRevenue> aggregator = (key, value, aggregate) -> aggregate.update(key, value);

        KTable<String, TotalRevenue> revenueKTable = orderStream.groupBy((key, value) -> value.locationId())
                .aggregate(initializer, aggregator,
                        Materialized.<String, TotalRevenue, KeyValueStore<Bytes, byte[]>>as(STORE_TOTAL_REVENUE_BY_LOCATION));

        revenueKTable.toStream().peek((key, value) -> log.info("[Revenue By Location] {}: {}", key, value));
    }
}

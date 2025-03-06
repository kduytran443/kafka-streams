package com.learnkafkastreams.domain;

import java.math.BigDecimal;

public record TotalRevenue(String locationId,
                           Integer runningOrderCount,
                           BigDecimal runningRevenue) {

    public TotalRevenue() {
        this(null, 0, BigDecimal.ZERO);
    }

    public TotalRevenue update(String key, Order order) {
        var newCount = runningOrderCount + 1;
        var newRevenue = this.runningRevenue.add(order.finalAmount());
        return new TotalRevenue(key, newCount, newRevenue);
    }
}

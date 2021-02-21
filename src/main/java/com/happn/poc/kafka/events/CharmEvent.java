package com.happn.poc.kafka.events;

public interface CharmEvent extends Event {

    String getUserId();

    CharmAggregator aggregator();

    static Long initializer() {
        return 0L;
    }

    @FunctionalInterface
    interface CharmAggregator {
        Long aggregate(Long initValue);

        static CharmAggregator charmReceived() {
            return initValue -> initValue + 1;
        }

        static CharmAggregator charmCleared() {
            return initValue -> CharmEvent.initializer();
        }
    }
}

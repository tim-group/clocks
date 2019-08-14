package com.timgroup.clocks.testing;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public interface MutableClock {
    default void bump(long amountToAdd, TemporalUnit unit) {
        bump(Duration.of(amountToAdd, unit));
    }

    default void bumpSeconds(int secs) {
        bump(Duration.ofSeconds(secs));
    }

    default void bumpMillis(long millis) {
        bump(Duration.ofMillis(millis));
    }

    void bump(TemporalAmount amountToAdd);

    void advanceTo(Instant futureInstant);
}

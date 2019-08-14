package com.timgroup.clocks.joda.testing;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface MutableJodaClock {
    default void bumpSeconds(long secs) {
        bump(Duration.standardSeconds(secs));
    }

    default void bumpMillis(long millis) {
        bump(Duration.millis(millis));
    }

    void bump(Duration duration);

    void advanceTo(Instant futureInstant);
}

package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Clock that only updates in positive increments when called directly.
 */
public class ManualClock extends Clock {
    private Instant instant;
    private final ZoneId zone;

    public ManualClock(Instant initialInstant, ZoneId zone) {
        this.instant = initialInstant;
        this.zone = zone;
    }

    public void bumpSeconds(int secs) {
        bump(Duration.ofSeconds(secs));
    }

    public void bumpMillis(long millis) {
        bump(Duration.ofMillis(millis));
    }

    public void bump(Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        instant = instant.plus(duration);
    }

    @Override
    public Instant instant() {
        return instant;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId overrideZone) {
        return new Clock() {
            @Override
            public Instant instant() {
                return instant;
            }

            @Override
            public ZoneId getZone() {
                return overrideZone;
            }

            @Override
            public Clock withZone(ZoneId z) {
                return ManualClock.this.withZone(z);
            }

            @Override
            public String toString() {
                return ManualClock.this.toString() + "{zone:" + overrideZone + "}";
            }
        };
    }

    @Override
    public String toString() {
        return "ManualClock:" + instant;
    }
}

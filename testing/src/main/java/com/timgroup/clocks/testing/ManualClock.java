package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

/**
 * Clock that only updates in positive increments when called directly.
 */
public final class ManualClock extends Clock {
    private Instant instant;
    private final ZoneId zone;

    public static ManualClock initiallyAt(Clock clock) {
        return new ManualClock(clock.instant(), clock.getZone());
    }

    public static ManualClock createDefault() {
        return initiallyAt(Clock.systemUTC());
    }

    public ManualClock(Instant initialInstant, ZoneId zone) {
        this.instant = requireNonNull(initialInstant);
        this.zone = requireNonNull(zone);
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

    public void advanceTo(Instant futureInstant) {
        requireNonNull(futureInstant);
        if (futureInstant.isBefore(instant)) {
            throw new IllegalArgumentException("Instant must not be before the current time");
        }
        instant = futureInstant;
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
        requireNonNull(overrideZone);
        if (overrideZone.equals(zone)) {
            return this;
        }
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

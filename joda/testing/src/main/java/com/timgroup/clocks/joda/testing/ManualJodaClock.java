package com.timgroup.clocks.joda.testing;

import com.timgroup.clocks.joda.JodaClock;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Clock that only updates in positive increments when called directly.
 */
public final class ManualJodaClock extends JodaClock {
    private Instant instant;
    private final DateTimeZone zone;

    public static ManualJodaClock initiallyAt(JodaClock clock) {
        return new ManualJodaClock(clock.now(), clock.getDateTimeZone());
    }

    public static ManualJodaClock createDefault() {
        return initiallyAt(JodaClock.getDefault());
    }

    public ManualJodaClock(Instant initialInstant, DateTimeZone zone) {
        this.instant = requireNonNull(initialInstant);
        this.zone = requireNonNull(zone);
    }

    public void bumpSeconds(long secs) {
        bumpMillis(secs * DateTimeConstants.MILLIS_PER_SECOND);
    }

    public void bumpMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }
        instant = instant.plus(millis);
    }

    public void bump(Duration duration) {
        if (duration.compareTo(Duration.ZERO) < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }
        instant = instant.plus(duration);
    }

    public void advanceTo(Instant futureInstant) {
        if (futureInstant.isBefore(instant)) {
            throw new IllegalArgumentException("Instant must not be before the current time");
        }
        instant = futureInstant;
    }

    @Override
    public Instant now() {
        return instant;
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return zone;
    }

    @Override
    public JodaClock withZone(DateTimeZone overrideZone) {
        if (overrideZone.equals(zone)) {
            return this;
        }
        return new JodaClock() {
            @Override
            public Instant now() {
                return instant;
            }

            @Override
            public DateTimeZone getDateTimeZone() {
                return overrideZone;
            }

            @Override
            public JodaClock withZone(DateTimeZone jodaTimeZone) {
                return ManualJodaClock.this.withZone(jodaTimeZone);
            }

            @Override
            public String toString() {
                return ManualJodaClock.this.toString() + "{zone:" + overrideZone + "}";
            }
        };
    }

    @Override
    public String toString() {
        return "ManualJodaClock:" + instant;
    }
}

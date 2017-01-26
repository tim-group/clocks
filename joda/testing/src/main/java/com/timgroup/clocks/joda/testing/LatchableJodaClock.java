package com.timgroup.clocks.joda.testing;

import com.timgroup.clocks.joda.JodaClock;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Clock that can be either free-running or latched to some fixed instant.
 *
 * @see ManualJodaClock
 */
public final class LatchableJodaClock extends JodaClock {
    private final JodaClock delegate;
    private Instant fixedInstant;

    public LatchableJodaClock(JodaClock delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public Instant now() {
        if (fixedInstant != null) {
            return fixedInstant;
        }
        return delegate.now();
    }

    @Override
    public long millis() {
        if (fixedInstant != null) {
            return fixedInstant.getMillis();
        }
        return delegate.millis();
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return delegate.getDateTimeZone();
    }

    public void latch() {
        fixedInstant = now();
    }

    public void latchTo(Instant instant) {
        fixedInstant = instant;
    }

    public void unlatch() {
        fixedInstant = null;
    }

    @Override
    public JodaClock withZone(DateTimeZone zone) {
        requireNonNull(zone);
        return new JodaClock() {
            @Override
            public Instant now() {
                return LatchableJodaClock.this.now();
            }

            @Override
            public long millis() {
                return LatchableJodaClock.this.millis();
            }

            @Override
            public JodaClock withZone(DateTimeZone zone) {
                return LatchableJodaClock.this.withZone(zone);
            }

            @Override
            public DateTimeZone getDateTimeZone() {
                return zone;
            }

            @Override
            public String toString() {
                return LatchableJodaClock.this.toString() + "{zone:" + zone + "}";
            }
        };
    }

    public void bump(Duration duration) {
        if (duration.compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (fixedInstant == null) {
            throw new IllegalStateException("Clock must be latched");
        }
        fixedInstant = fixedInstant.plus(duration);
    }

    @Override
    public String toString() {
        if (fixedInstant == null) {
            return "LatchableClock:" + delegate;
        }
        else {
            return "LatchableClock:@" + fixedInstant;
        }
    }
}

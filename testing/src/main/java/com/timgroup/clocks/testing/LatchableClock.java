package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Clock that can be either free-running or latched to some fixed instant.
 *
 * @see ManualClock
 */
public class LatchableClock extends Clock {
    private final Clock delegate;
    private Instant fixedInstant;

    public LatchableClock(Clock delegate) {
        this.delegate = delegate;
    }

    @Override
    public Instant instant() {
        if (fixedInstant != null) {
            return fixedInstant;
        }
        return delegate.instant();
    }

    @Override
    public long millis() {
        if (fixedInstant != null) {
            return fixedInstant.toEpochMilli();
        }
        return delegate.millis();
    }

    @Override
    public ZoneId getZone() {
        return delegate.getZone();
    }

    public void latch() {
        fixedInstant = instant();
    }

    public void latchTo(Instant instant) {
        fixedInstant = instant;
    }

    public void unlatch() {
        fixedInstant = null;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new Clock() {
            @Override
            public Instant instant() {
                return LatchableClock.this.instant();
            }

            @Override
            public long millis() {
                return LatchableClock.this.millis();
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return LatchableClock.this.withZone(zone);
            }

            @Override
            public ZoneId getZone() {
                return zone;
            }

            @Override
            public String toString() {
                return LatchableClock.this.toString() + "{zone:" + zone + "}";
            }
        };
    }

    public void bump(Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (fixedInstant != null) {
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

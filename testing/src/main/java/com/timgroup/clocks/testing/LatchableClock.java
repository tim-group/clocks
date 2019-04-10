package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Clock that can be either free-running or latched to some fixed instant.
 *
 * @see ManualClock
 */
public final class LatchableClock extends Clock {
    private final Clock delegate;
    private AtomicReference<Instant> fixedInstantRef = new AtomicReference<>(null);

    public LatchableClock(Clock delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public Instant instant() {
        Instant fixedInstant = fixedInstantRef.get();
        if (fixedInstant != null) {
            return fixedInstant;
        }
        return delegate.instant();
    }

    @Override
    public long millis() {
        Instant fixedInstant = fixedInstantRef.get();
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
        fixedInstantRef.set(instant());
    }

    public void latchTo(Instant instant) {
        fixedInstantRef.set(instant);
    }

    public void unlatch() {
        fixedInstantRef.set(null);
    }

    @Override
    public Clock withZone(ZoneId zone) {
        requireNonNull(zone);
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
        Instant fixedInstant = fixedInstantRef.get();
        if (fixedInstant == null) {
            throw new IllegalStateException("Clock must be latched");
        }
        fixedInstantRef.set(fixedInstant.plus(duration));
    }

    @Override
    public String toString() {
        Instant fixedInstant = fixedInstantRef.get();
        if (fixedInstant == null) {
            return "LatchableClock:" + delegate;
        }
        else {
            return "LatchableClock:@" + fixedInstant;
        }
    }
}

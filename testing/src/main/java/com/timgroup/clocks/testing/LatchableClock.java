package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.requireNonNull;

/**
 * Clock that can be latched to some fixed instant, or offset from a running clock.
 *
 * @see ManualClock
 */
public final class LatchableClock extends Clock {
    private final Clock delegate;
    private Instant fixedInstant;
    private Duration delegateClockOffset;

    public LatchableClock(Clock delegate) {
        this(delegate, delegate.instant(), true);
    }

    public LatchableClock(Clock delegate, Instant initialInstant, boolean running) {
        this.delegate = requireNonNull(delegate);
        if (running) {
            this.fixedInstant = null;
            this.delegateClockOffset = Duration.between(initialInstant, delegate.instant());
        }
        else {
            this.fixedInstant = initialInstant;
            this.delegateClockOffset = null;
        }
    }

    @Override
    public synchronized Instant instant() {
        if (fixedInstant != null) {
            return fixedInstant;
        }
        return delegate.instant().minus(delegateClockOffset);
    }

    @Override
    public synchronized long millis() {
        if (fixedInstant != null) {
            return fixedInstant.toEpochMilli();
        }
        return delegate.millis() - delegateClockOffset.toMillis();
    }

    @Override
    public ZoneId getZone() {
        return delegate.getZone();
    }

    public synchronized void latch() {
        latchTo(instant());
    }

    public synchronized void latchTo(Instant instant) {
        this.fixedInstant = instant;
        this.delegateClockOffset = null;
    }

    public synchronized void unlatch() {
        if (fixedInstant != null) {
            delegateClockOffset = Duration.between(fixedInstant, delegate.instant());
            fixedInstant = null;
        }
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

    public synchronized void bump(Duration duration) {
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (fixedInstant == null) {
            throw new IllegalStateException("Clock must be latched");
        }
        fixedInstant = fixedInstant.plus(duration);
    }

    @Override
    public synchronized String toString() {
        if (fixedInstant == null) {
            return "LatchableClock:" + delegate + "-" + delegateClockOffset;
        }
        else {
            return "LatchableClock:@" + fixedInstant;
        }
    }
}

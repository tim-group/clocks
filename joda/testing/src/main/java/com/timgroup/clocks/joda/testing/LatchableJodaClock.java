package com.timgroup.clocks.joda.testing;

import com.timgroup.clocks.joda.JodaClock;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import static java.util.Objects.requireNonNull;

/**
 * Clock that can be latched to some fixed instant, or offset from a running clock.
 *
 * @see ManualJodaClock
 */
public final class LatchableJodaClock extends JodaClock implements MutableJodaClock {
    private final JodaClock delegate;
    private Instant fixedInstant;
    private Duration delegateClockOffset;

    public LatchableJodaClock(JodaClock delegate) {
        this(delegate, delegate.now(), true);
    }

    public LatchableJodaClock(JodaClock delegate, Instant initialInstant, boolean running) {
        this.delegate = requireNonNull(delegate);
        requireNonNull(initialInstant);
        if (running) {
            this.fixedInstant = null;
            this.delegateClockOffset = new Duration(initialInstant, delegate.now());
        }
        else {
            this.fixedInstant = initialInstant;
            this.delegateClockOffset = null;
        }
    }

    @Override
    public synchronized Instant now() {
        if (fixedInstant != null) {
            return fixedInstant;
        }
        return delegate.now().minus(delegateClockOffset);
    }

    @Override
    public synchronized long millis() {
        if (fixedInstant != null) {
            return fixedInstant.getMillis();
        }
        return delegate.millis() - delegateClockOffset.getMillis();
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return delegate.getDateTimeZone();
    }

    public void latch() {
        latchTo(now());
    }

    public synchronized void latchTo(Instant instant) {
        this.fixedInstant = instant;
        this.delegateClockOffset = null;
    }

    public synchronized void unlatch() {
        if (fixedInstant != null) {
            delegateClockOffset = new Duration(fixedInstant, delegate.now());
            fixedInstant = null;
        }
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

    public synchronized void bump(Duration duration) {
        if (duration.compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (fixedInstant == null) {
            throw new IllegalStateException("Clock must be latched");
        }
        fixedInstant = fixedInstant.plus(duration);
    }

    @Override
    public synchronized void advanceTo(Instant futureInstant) {
        if (futureInstant.isBefore(now())) {
            throw new IllegalArgumentException("Instant must not be before the current time");
        }
        latchTo(futureInstant);
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

package com.timgroup.clocks.joda.testing;

import java.util.TimeZone;
import java.util.function.Supplier;

import com.timgroup.clocks.joda.JodaClock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static java.util.Objects.requireNonNull;

/**
 * JUnit rule for resetting time via Joda-Time.
 */
public final class ResetTime extends JodaClock implements TestRule {
    private final DateTimeZone timeZone;
    private Instant instant;

    private ResetTime(Instant timeToResetTo, DateTimeZone timeZone) {
        this.instant = requireNonNull(timeToResetTo);
        this.timeZone = requireNonNull(timeZone);
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate() throws Throwable {
                try (Resource r = open()) {
                    base.evaluate();
                }
            }
        };
    }

    /**
     * Obtain value from supplier with time frozen.
     *
     * @param <T> type to return from supplier
     * @param supplier code to produce value
     * @return value produced with time frozen
     */
    public <T> T supply(Supplier<T> supplier) {
        try (Resource ignored = open()) {
            return supplier.get();
        }
    }

    /**
     * Run block of code with time frozen.
     *
     * @param runnable code to run with time frozen
     */
    public void run(Runnable runnable) {
        try (Resource r = open()) {
            runnable.run();
        }
    }

    @Override
    public Instant now() {
        return instant;
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return timeZone;
    }

    @Override
    public JodaClock withZone(DateTimeZone jodaTimeZone) {
        return new JodaClock() {
            @Override
            public DateTimeZone getDateTimeZone() {
                return jodaTimeZone;
            }

            @Override
            public Instant now() {
                return instant;
            }

            @Override
            public JodaClock withZone(DateTimeZone jodaTimeZone) {
                return ResetTime.this.withZone(jodaTimeZone);
            }

            @Override
            public String toString() {
                return ResetTime.this.toString() + "{ zone = " + jodaTimeZone + " }";
            }
        };
    }

    /**
     * Advance the fixed time.
     *
     * @param duration Amount to add to current fixed time
     * @see ManualJodaClock#bump
     */
    public void bump(Duration duration) {
        if (duration.compareTo(Duration.ZERO) <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        instant = instant.plus(duration);
    }

    /**
     * Advance the fixed time by the given number of milliseconds.
     *
     * @param millis Milliseconds to add to current fixed time
     * @see ManualJodaClock#bumpMillis
     */
    public void bumpMillis(long millis) {
        bump(Duration.millis(millis));
    }

    /**
     * Advance the fixed time by the given number of seconds.
     *
     * @param seconds Seconds to add to current fixed time
     * @see ManualJodaClock#bumpSeconds
     */
    public void bumpSeconds(int seconds) {
        bump(Duration.standardSeconds(seconds));
    }

    /**
     * Advance the fixed time to the given instant
     *
     * @param futureInstant Instant to use as current fixed time
     * @see ManualJodaClock#advanceTo
     */
    public void advanceTo(Instant futureInstant) {
        if (futureInstant.isBefore(now())) {
            throw new IllegalArgumentException("Attempted to move back in time from " + now() + " to " + futureInstant);
        }
        instant = futureInstant;
    }

    /**
     * Obtain an auto-closeable resource that keeps time frozen as long as it is open.
     *
     * <p>Intended to be used in try-with-resources blocks, e.g.:</p>
     *     <pre>
     *         try (ResetTime.Resource r = resetTime.open()) {
     *             // time frozen here
     *         }
     *         // time unfrozen here
     *     </pre>
     *
     * @return an {@link AutoCloseable} resource
     */
    public Resource open() {
        DateTimeUtils.setCurrentMillisProvider(this::millis);
        DateTimeZone.setDefault(timeZone);
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone.getID()));
        return new Resource();
    }

    public static ResetTime to(Instant instant) {
        return new ResetTime(instant, DateTimeZone.getDefault());
    }

    public static ResetTime to(Instant instant, DateTimeZone timeZone) {
        return new ResetTime(instant, timeZone);
    }

    public static ResetTime to(DateTime dateTime) {
        return new ResetTime(dateTime.toInstant(), dateTime.getZone());
    }

    public static ResetTime to(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, String timezone) {
        return to(new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0, DateTimeZone.forID(timezone)));
    }

    public final class Resource implements AutoCloseable {
        @Override
        public void close() {
            DateTimeUtils.setCurrentMillisSystem();
            TimeZone.setDefault(null);
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        }
    }

    @Override
    public String toString() {
        return "ResetTime.to(" + instant + ")";
    }
}

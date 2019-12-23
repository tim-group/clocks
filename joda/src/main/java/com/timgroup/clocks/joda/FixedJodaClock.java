package com.timgroup.clocks.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import static java.util.Objects.requireNonNull;

final class FixedJodaClock extends JodaClock {
    private final Instant instant;
    private final DateTimeZone timeZone;

    FixedJodaClock(Instant instant, DateTimeZone timeZone) {
        this.instant = requireNonNull(instant);
        this.timeZone = requireNonNull(timeZone);
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return timeZone;
    }

    @Override
    public Instant now() {
        return instant;
    }

    @Override
    public JodaClock withZone(DateTimeZone jodaTimeZone) {
        if (timeZone.equals(jodaTimeZone))
            return this;
        return new FixedJodaClock(instant, jodaTimeZone);
    }

    @Override
    public String toString() {
        return "FixedJodaClock[" + instant + "," + timeZone + "]";
    }
}

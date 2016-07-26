package com.timgroup.clocks.joda;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.joda.time.DateTimeZone;

public final class JodaClock extends Clock {
    private final Clock clock;

    public static JodaClock getDefault() {
        return new JodaClock(JodaCompatibleClock.getInstance());
    }

    public JodaClock(Clock clock) {
        this.clock = clock;
    }

    public DateTimeZone getDateTimeZone() {
        ZoneId zoneId = clock.getZone();
        if (zoneId == ZoneOffset.UTC) {
            return DateTimeZone.UTC;
        }
        return DateTimeZone.forID(zoneId.getId());
    }

    public org.joda.time.Instant now() {
        return new org.joda.time.Instant(clock.millis());
    }

    public org.joda.time.DateTime nowDateTime() {
        return new org.joda.time.DateTime(clock.millis(), getDateTimeZone());
    }

    public org.joda.time.LocalDateTime nowLocal() {
        return new org.joda.time.LocalDateTime(clock.millis(), getDateTimeZone());
    }

    public org.joda.time.LocalDate today() {
        return new org.joda.time.LocalDate(clock.millis(), getDateTimeZone());
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public java.time.Instant instant() {
        return clock.instant();
    }

    @Override
    public JodaClock withZone(ZoneId zone) {
        Clock newClock = clock.withZone(zone);
        if (newClock == clock) {
            return this;
        }
        return new JodaClock(newClock);
    }

    public JodaClock withZone(DateTimeZone jodaTimeZone) {
        ZoneId zone;
        if (jodaTimeZone == DateTimeZone.UTC) {
            zone = ZoneOffset.UTC;
        }
        else {
            zone = ZoneId.of(jodaTimeZone.getID());
        }
        return withZone(zone);
    }

    @Override
    public String toString() {
        return "JodaClock:" + clock;
    }
}

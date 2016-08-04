package com.timgroup.clocks.joda;

import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

/**
 * An extended clock that serves as a source of Joda-Time instants and other
 * types.
 * <p>
 * Delegates to a JSR310 clock to get the current millis and zone-id. Assumes
 * that JSR310 zone IDs are equivalent to Joda-Time's --- which should generally
 * be true, although they may be on different versions of the tzinfo data.
 * <p>
 * This is intended as a replacement for Clock/TimeService classes in
 * applications that use Joda-Time. {@link #getDefault} will return an instance
 * that is compatible with Joda-Time types' default constructors.
 */
public final class JodaClock extends Clock {
    private final Clock clock;

    public static JodaClock getDefault() {
        return new JodaClock(JodaCompatibleClock.getInstance());
    }

    public static JodaClock using(Clock clock) {
        if (clock instanceof JodaClock) {
            return (JodaClock) clock;
        }
        return new JodaClock(clock);
    }

    JodaClock(Clock clock) {
        this.clock = clock;
    }

    public DateTimeZone getDateTimeZone() {
        return toDateTimeZone(clock.getZone());
    }

    public static DateTimeZone toDateTimeZone(ZoneId zoneId) {
        if (zoneId == ZoneOffset.UTC) {
            return DateTimeZone.UTC;
        }
        if (zoneId instanceof ZoneOffset) {
            ZoneOffset zoneOffset = (ZoneOffset) zoneId;
            return DateTimeZone.forOffsetMillis(zoneOffset.getTotalSeconds() * MILLIS_PER_SECOND);
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
        return withZone(toZoneId(jodaTimeZone));
    }

    public static ZoneId toZoneId(DateTimeZone jodaTimeZone) {
        if (jodaTimeZone == DateTimeZone.UTC) {
            return ZoneOffset.UTC;
        }
        else if (jodaTimeZone.isFixed()) {
            return ZoneOffset.ofTotalSeconds(jodaTimeZone.getOffset(0) / DateTimeConstants.MILLIS_PER_SECOND);
        }
        else {
            return ZoneId.of(jodaTimeZone.getID());
        }
    }

    @Override
    public String toString() {
        return "JodaClock:" + clock;
    }
}

package com.timgroup.clocks.joda;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import static java.util.Objects.requireNonNull;
import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;

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
public abstract class JodaClock extends Clock {
    public static JodaClock getDefault() {
        return JodaCompatibleClock.DEFAULT_ZONE;
    }

    public static JodaClock using(Clock clock) {
        if (clock instanceof JodaClock) {
            return (JodaClock) clock;
        }
        return new Delegating(clock);
    }

    public abstract DateTimeZone getDateTimeZone();

    @Override
    public final ZoneId getZone() {
        return toZoneId(getDateTimeZone());
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

    public abstract org.joda.time.Instant now();

    @Override
    public long millis() {
        return now().getMillis();
    }

    @Override
    public final Instant instant() {
        return Instant.ofEpochMilli(millis());
    }

    public final org.joda.time.DateTime nowDateTime() {
        return new org.joda.time.DateTime(millis(), getDateTimeZone());
    }

    public final org.joda.time.LocalDateTime nowLocal() {
        return new org.joda.time.LocalDateTime(millis(), getDateTimeZone());
    }

    public final org.joda.time.LocalDate today() {
        return new org.joda.time.LocalDate(millis(), getDateTimeZone());
    }

    public abstract JodaClock withZone(DateTimeZone jodaTimeZone);

    public final JodaClock withUTC() {
        return withZone(DateTimeZone.UTC);
    }

    public final JodaClock withZone(ZoneId zone) {
        return withZone(toDateTimeZone(zone));
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

    private static final class Delegating extends JodaClock {
        private final Clock clock;

        Delegating(Clock clock) {
            this.clock = requireNonNull(clock);
        }

        @Override
        public org.joda.time.Instant now() {
            return new org.joda.time.Instant(clock.millis());
        }

        @Override
        public long millis() {
            return clock.millis();
        }

        @Override
        public DateTimeZone getDateTimeZone() {
            return toDateTimeZone(clock.getZone());
        }

        @Override
        public JodaClock withZone(DateTimeZone jodaTimeZone) {
            ZoneId zoneId = toZoneId(jodaTimeZone);
            Clock newClock = clock.withZone(zoneId);
            if (newClock == clock) {
                return this;
            }
            return new Delegating(newClock);
        }

        @Override
        public String toString() {
            return "JodaClock:" + clock;
        }
    }
}

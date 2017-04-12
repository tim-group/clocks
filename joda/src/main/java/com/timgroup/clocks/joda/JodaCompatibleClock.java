package com.timgroup.clocks.joda;

import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

import static java.util.Objects.requireNonNull;

/**
 * Clock that uses Joda-Time's static accessors for the current time and zone.
 *
 * @see DateTimeUtils
 */
public abstract class JodaCompatibleClock extends JodaClock {
    private JodaCompatibleClock() {
    }

    @Override
    public org.joda.time.Instant now() {
        return new org.joda.time.Instant();
    }

    @Override
    public JodaClock withZone(DateTimeZone jodaTimeZone) {
        return new ZoneOverridden(jodaTimeZone);
    }

    @Override
    public long millis() {
        return DateTimeUtils.currentTimeMillis();
    }

    public static final DefaultZone DEFAULT_ZONE = new DefaultZone();

    public static final class DefaultZone extends JodaCompatibleClock {
        private DefaultZone() {
        }

        @Override
        public DateTimeZone getDateTimeZone() {
            return DateTimeZone.getDefault();
        }

        @Override
        public String toString() {
            return "JodaCompatibleClock[" + DateTimeZone.getDefault().getID() + " (default)]";
        }
    }

    public static final class ZoneOverridden extends JodaCompatibleClock {
        private final DateTimeZone zone;

        private ZoneOverridden(DateTimeZone zone) {
            this.zone = requireNonNull(zone);
        }

        @Override
        public DateTimeZone getDateTimeZone() {
            return zone;
        }

        @Override
        public String toString() {
            return "JodaCompatibleClock[" + zone + "]";
        }
    }
}

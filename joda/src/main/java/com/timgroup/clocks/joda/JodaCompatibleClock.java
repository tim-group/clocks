package com.timgroup.clocks.joda;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

public abstract class JodaCompatibleClock extends Clock {
    private static final JodaCompatibleClock INSTANCE = new JodaCompatibleClock.DefaultZone();

    public static Clock getInstance() {
        return INSTANCE;
    }

    private JodaCompatibleClock() {
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new ZoneOverridden(zone);
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(DateTimeUtils.currentTimeMillis());
    }

    @Override
    public long millis() {
        return DateTimeUtils.currentTimeMillis();
    }

    public static final class DefaultZone extends JodaCompatibleClock {
        @Override
        public ZoneId getZone() {
            return ZoneId.of(DateTimeZone.getDefault().getID());
        }

        @Override
        public String toString() {
            return "JodaCompatibleClock[" + DateTimeZone.getDefault().getID() + " (default)]";
        }
    }

    public static final class ZoneOverridden extends JodaCompatibleClock {
        private final ZoneId zone;

        private ZoneOverridden(ZoneId zone) {
            this.zone = zone;
        }

        @Override
        public Clock withZone(ZoneId newZone) {
            if (newZone.equals(zone)) {
                return this;
            }

            return super.withZone(newZone);
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public String toString() {
            return "JodaCompatibleClock[" + zone + "]";
        }
    }
}

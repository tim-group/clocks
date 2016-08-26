package com.timgroup.clocks.joda.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Duration.millis;
import static org.joda.time.Duration.standardSeconds;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import com.timgroup.clocks.joda.JodaClock;

public class LatchableJodaClockTest {
    @Test
    public void delegates_to_underlying_clock_by_default() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getDateTimeZone(), equalTo(UTC));
        underlying.bumpSeconds(1);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:01Z")));
        assertThat(clock.getDateTimeZone(), equalTo(UTC));
    }

    @Test
    public void latching_overrides_underlying_clock() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T19:00:00Z")));
    }

    @Test
    public void latching_overrides_underlying_clock_taking_its_current_time() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.latch();
        underlying.bumpSeconds(1);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test
    public void latched_clock_can_be_advanced() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        clock.bump(standardSeconds(1));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T19:00:01Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void latched_clock_cannot_be_advanced_by_zero() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        clock.bump(Duration.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void latched_clock_cannot_be_advanced_by_negative_duration() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        clock.bump(millis(-1));
    }

    @Test(expected = IllegalStateException.class)
    public void unlatched_clock_cannot_be_advanced() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        clock.bump(standardSeconds(1));
    }

    @Test
    public void latched_clock_can_be_have_zone_overridden_in_derived_clock() throws Exception {
        ManualJodaClock underlying = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableJodaClock clock = new LatchableJodaClock(underlying);
        JodaClock derivedClock = clock.withZone(DateTimeZone.forID("America/Los_Angeles"));
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        assertThat(derivedClock.now(), equalTo(Instant.parse("2016-08-26T19:00:00Z")));
        assertThat(derivedClock.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }
}

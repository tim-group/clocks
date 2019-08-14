package com.timgroup.clocks.testing;

import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LatchableClockTest {
    @Test
    public void delegates_to_underlying_clock_by_default() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getZone(), equalTo(UTC));
        underlying.bumpSeconds(1);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:01Z")));
        assertThat(clock.getZone(), equalTo(UTC));
    }

    @Test
    public void can_be_latched_on_construction() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying, underlying.instant(), false);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getZone(), equalTo(UTC));
        underlying.bumpSeconds(1);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getZone(), equalTo(UTC));
    }

    @Test
    public void latching_overrides_underlying_clock() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T19:00:00Z")));
    }

    @Test
    public void latching_overrides_underlying_clock_taking_its_current_time() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        clock.latch();
        underlying.bumpSeconds(1);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test
    public void latched_clock_can_be_advanced() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        clock.bump(Duration.ofSeconds(1));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T19:00:01Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void latched_clock_cannot_be_advanced_by_negative_duration() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        clock.bump(Duration.ofMillis(-1));
    }

    @Test(expected = IllegalStateException.class)
    public void unlatched_clock_cannot_be_advanced() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        clock.bump(Duration.ofSeconds(1));
    }

    @Test
    public void unlatching_clock_recalculates_offset_using_fixed_instant() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying, Instant.parse("2016-08-26T17:30:00Z"), false);
        clock.unlatch();
        underlying.bumpSeconds(1);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T17:30:01Z")));
    }

    @Test
    public void latched_clock_can_be_have_zone_overridden_in_derived_clock() throws Exception {
        ManualClock underlying = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        LatchableClock clock = new LatchableClock(underlying);
        Clock derivedClock = clock.withZone(ZoneId.of("America/Los_Angeles"));
        clock.latchTo(Instant.parse("2016-08-26T19:00:00Z"));
        assertThat(derivedClock.instant(), equalTo(Instant.parse("2016-08-26T19:00:00Z")));
        assertThat(derivedClock.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
    }
}

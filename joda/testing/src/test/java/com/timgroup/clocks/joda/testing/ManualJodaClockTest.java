package com.timgroup.clocks.joda.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.joda.time.DateTimeZone.UTC;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import com.timgroup.clocks.joda.JodaClock;

public class ManualJodaClockTest {
    @Test
    public void returns_initial_time_and_zone() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getDateTimeZone(), equalTo(UTC));
    }

    @Test
    public void advances_by_arbitrary_duration() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.parse("PT321.123S"));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:35:21.123Z")));
    }

    @Test
    public void advances_by_millis() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(123L);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00.123Z")));
    }

    @Test
    public void advances_by_seconds() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(123);
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:32:03Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_zero_millis() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_zero_seconds() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_zero_duration() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.ZERO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_millis() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(-123L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_seconds() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(-123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_duration() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.parse("PT-1S"));
    }

    @Test
    public void allows_timezone_to_be_overridden_in_derived_clock() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        JodaClock overridden = clock.withZone(DateTimeZone.forID("America/Los_Angeles"));
        clock.bumpSeconds(1);
        assertThat(overridden.now(), equalTo(Instant.parse("2016-08-26T18:30:01Z")));
        assertThat(overridden.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void overriding_with_same_zone_returns_original_clock() throws Exception {
        ManualJodaClock clock = new ManualJodaClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        assertThat(clock.withZone(UTC), sameInstance(clock));
    }
}

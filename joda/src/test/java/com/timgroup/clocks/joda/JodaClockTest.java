package com.timgroup.clocks.joda;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.joda.time.DateTimeZone;
import org.junit.Test;

public class JodaClockTest {
    @Test
    public void provides_joda_utc_instance_for_system_utc_clock() throws Exception {
        assertThat(new JodaClock(Clock.systemUTC()).getDateTimeZone(), sameInstance(DateTimeZone.UTC));
    }

    @Test
    public void provides_joda_instant() throws Exception {
        assertThat(new JodaClock(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC)).now(),
                equalTo(org.joda.time.Instant.parse("2016-06-10T10:11:12Z")));
    }

    @Test
    public void provides_same_joda_instant_regardless_of_timezone() throws Exception {
        assertThat(new JodaClock(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("America/Los_Angeles"))).now(),
                equalTo(org.joda.time.Instant.parse("2016-06-10T10:11:12Z")));
    }

    @Test
    public void provides_equivalent_joda_time_zone() throws Exception {
        assertThat(new JodaClock(Clock.systemUTC().withZone(ZoneId.of("America/Los_Angeles"))).getDateTimeZone(),
                equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void provides_localdatetime_in_clock_timezone() throws Exception {
        assertThat(
                new JodaClock(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .nowLocal(),
                equalTo(org.joda.time.LocalDateTime.parse("2016-06-09T23:11:12")));
    }

    @Test
    public void provides_localdate_in_clock_timezone() throws Exception {
        assertThat(
                new JodaClock(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .today(),
                equalTo(org.joda.time.LocalDate.parse("2016-06-09")));
    }

    @Test
    public void provides_datetime_in_clock_timezone() throws Exception {
        assertThat(
                new JodaClock(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .nowDateTime(),
                equalTo(org.joda.time.LocalDateTime.parse("2016-06-09T23:11:12")
                        .toDateTime(DateTimeZone.forID("Pacific/Midway"))));
    }

    @Test
    public void zone_id_can_be_applied_to_joda_clock() throws Exception {
        JodaClock jodaClockWithZone = new JodaClock(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(ZoneId.of("America/Los_Angeles"));
        assertThat(jodaClockWithZone.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
        assertThat(jodaClockWithZone.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void joda_time_zone_can_be_applied_to_joda_clock() throws Exception {
        JodaClock jodaClockWithZone = new JodaClock(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(DateTimeZone.forID("America/Los_Angeles"));
        assertThat(jodaClockWithZone.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
        assertThat(jodaClockWithZone.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void reapplying_utc_returns_same_instance() throws Exception {
        JodaClock jodaClock = new JodaClock(Clock.systemUTC());
        assertThat(jodaClock.withZone(ZoneOffset.UTC), sameInstance(jodaClock));
    }

    @Test
    public void reapplying_same_offset_returns_same_instance() throws Exception {
        JodaClock jodaClock = new JodaClock(Clock.system(ZoneOffset.ofHours(2)));
        assertThat(jodaClock.withZone(ZoneOffset.ofHours(2)), sameInstance(jodaClock));
    }

    @Test
    public void reapplying_joda_utc_returns_same_instance() throws Exception {
        JodaClock jodaClock = new JodaClock(Clock.systemUTC());
        assertThat(jodaClock.withZone(DateTimeZone.UTC), sameInstance(jodaClock));
    }
}

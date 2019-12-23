package com.timgroup.clocks.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.joda.time.DateTimeConstants.MILLIS_PER_HOUR;
import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;
import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;

public class JodaClockTest {
    @Test
    public void provides_joda_utc_instance_for_system_utc_clock() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC()).getDateTimeZone(), sameInstance(DateTimeZone.UTC));
    }

    @Test
    public void provides_joda_instant() throws Exception {
        assertThat(JodaClock.using(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC)).now(),
                equalTo(org.joda.time.Instant.parse("2016-06-10T10:11:12Z")));
    }

    @Test
    public void provides_same_joda_instant_regardless_of_timezone() throws Exception {
        assertThat(JodaClock.using(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("America/Los_Angeles"))).now(),
                equalTo(org.joda.time.Instant.parse("2016-06-10T10:11:12Z")));
    }

    @Test
    public void provides_equivalent_joda_time_zone_for_named_zone() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC().withZone(ZoneId.of("America/Los_Angeles"))).getDateTimeZone(),
                equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void provides_equivalent_joda_time_zone_for_zone_offset() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC().withZone(ZoneOffset.ofHours(6))).getDateTimeZone(),
                equalTo(DateTimeZone.forOffsetHours(6)));
    }

    @Test
    public void provides_equivalent_joda_time_zone_for_negative_zone_offset() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC().withZone(ZoneOffset.ofHours(-6))).getDateTimeZone(),
                equalTo(DateTimeZone.forOffsetHours(-6)));
    }

    @Test
    public void provides_equivalent_joda_time_zone_for_zone_offset_with_minutes_offset() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC().withZone(ZoneOffset.ofHoursMinutesSeconds(-11, -20, 0))).getDateTimeZone(),
                equalTo(DateTimeZone.forOffsetHoursMinutes(-11, -20)));
    }

    @Test
    public void provides_equivalent_joda_time_zone_for_zone_offset_with_bizarre_offset() throws Exception {
        assertThat(JodaClock.using(Clock.systemUTC().withZone(ZoneOffset.ofHoursMinutesSeconds(-11, -20, -25))).getDateTimeZone(),
                equalTo(DateTimeZone.forOffsetMillis(-11 * MILLIS_PER_HOUR + -20 * MILLIS_PER_MINUTE + -25 * MILLIS_PER_SECOND)));
    }

    @Test
    public void provides_localdatetime_in_clock_timezone() throws Exception {
        assertThat(
                JodaClock.using(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .nowLocal(),
                equalTo(org.joda.time.LocalDateTime.parse("2016-06-09T23:11:12")));
    }

    @Test
    public void provides_localdate_in_clock_timezone() throws Exception {
        assertThat(
                JodaClock.using(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .today(),
                equalTo(org.joda.time.LocalDate.parse("2016-06-09")));
    }

    @Test
    public void provides_datetime_in_clock_timezone() throws Exception {
        assertThat(
                JodaClock.using(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneId.of("Pacific/Midway")))
                        .nowDateTime(),
                equalTo(org.joda.time.LocalDateTime.parse("2016-06-09T23:11:12")
                        .toDateTime(DateTimeZone.forID("Pacific/Midway"))));
    }

    @Test
    public void zone_id_can_be_applied_to_joda_clock() throws Exception {
        JodaClock jodaClockWithZone = JodaClock.using(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(ZoneId.of("America/Los_Angeles"));
        assertThat(jodaClockWithZone.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
        assertThat(jodaClockWithZone.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void joda_time_zone_with_id_can_be_applied_to_joda_clock() throws Exception {
        JodaClock jodaClockWithZone = JodaClock.using(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(DateTimeZone.forID("America/Los_Angeles"));
        assertThat(jodaClockWithZone.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
        assertThat(jodaClockWithZone.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }

    @Test
    public void joda_time_zone_with_fixed_offset_can_be_applied_to_joda_clock() throws Exception {
        JodaClock jodaClockWithZone = JodaClock.using(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(DateTimeZone.forOffsetHours(6));
        assertThat(jodaClockWithZone.getZone(), equalTo(ZoneOffset.ofHours(6)));
        assertThat(jodaClockWithZone.getDateTimeZone(), equalTo(DateTimeZone.forOffsetHours(6)));
    }

    @Test
    public void result_of_applying_a_fixed_offset_is_a_zone_offset() throws Exception {
        JodaClock jodaClockWithZone = JodaClock.using(
                Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC))
                        .withZone(DateTimeZone.forOffsetHours(6));
        assertThat(jodaClockWithZone.getZone(), instanceOf(ZoneOffset.class));
    }

    @Test
    public void reapplying_utc_returns_same_instance() throws Exception {
        JodaClock jodaClock = JodaClock.using(Clock.systemUTC());
        assertThat(jodaClock.withZone(ZoneOffset.UTC), sameInstance(jodaClock));
    }

    @Test
    public void reapplying_same_offset_returns_same_instance() throws Exception {
        JodaClock jodaClock = JodaClock.using(Clock.system(ZoneOffset.ofHours(2)));
        assertThat(jodaClock.withZone(ZoneOffset.ofHours(2)), sameInstance(jodaClock));
    }

    @Test
    public void reapplying_joda_utc_returns_same_instance() throws Exception {
        JodaClock jodaClock = JodaClock.using(Clock.systemUTC());
        assertThat(jodaClock.withZone(DateTimeZone.UTC), sameInstance(jodaClock));
    }

    @Test
    public void static_factory_wraps_given_clock() throws Exception {
        JodaClock jodaClock = JodaClock.using(Clock.fixed(java.time.Instant.parse("2016-06-10T10:11:12Z"), ZoneOffset.UTC));
        assertThat(jodaClock.now(), equalTo(org.joda.time.Instant.parse("2016-06-10T10:11:12Z")));
    }

    @Test
    public void static_factory_returns_existing_joda_clock() throws Exception {
        JodaClock jodaClock = JodaClock.using(Clock.systemUTC());
        assertThat(JodaClock.using(jodaClock), sameInstance(jodaClock));
    }

    @Test
    public void static_factory_returns_fixed_clock() throws Exception {
        JodaClock jodaClock = JodaClock.fixed(Instant.parse("2019-12-23T11:00:00Z"), DateTimeZone.UTC);
        assertThat(jodaClock, instanceOf(FixedJodaClock.class));
        assertThat(jodaClock.now(), equalTo(Instant.parse("2019-12-23T11:00:00Z")));
        assertThat(jodaClock.getDateTimeZone(), equalTo(DateTimeZone.UTC));
        assertThat(jodaClock.withZone(DateTimeZone.UTC), sameInstance(jodaClock));
        assertThat(jodaClock.withZone(DateTimeZone.forID("Europe/London")).getDateTimeZone(), equalTo(DateTimeZone.forID("Europe/London")));
    }
}

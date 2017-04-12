package com.timgroup.clocks.joda;

import java.time.ZoneId;

import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JodaCompatibleClockTest {
    @Test
    public void setting_joda_time_global_affects_clock_output() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(org.joda.time.Instant.parse("2015-04-21T11:18:33.123Z").getMillis());
        assertThat(JodaCompatibleClock.DEFAULT_ZONE.instant(), equalTo(java.time.Instant.parse("2015-04-21T11:18:33.123Z")));
        DateTimeUtils.setCurrentMillisFixed(org.joda.time.Instant.parse("2015-04-21T11:21:59+01:00").getMillis());
        assertThat(JodaCompatibleClock.DEFAULT_ZONE.instant(), equalTo(java.time.Instant.parse("2015-04-21T10:21:59Z")));
    }

    @Test
    public void setting_joda_time_timezone_affects_clock_output() throws Exception {
        DateTimeZone.setDefault(DateTimeZone.forID("America/New_York"));
        assertThat(JodaCompatibleClock.DEFAULT_ZONE.getZone(), equalTo(ZoneId.of("America/New_York")));
        DateTimeZone.setDefault(DateTimeZone.forID("Asia/Novosibirsk"));
        assertThat(JodaCompatibleClock.DEFAULT_ZONE.getZone(), equalTo(ZoneId.of("Asia/Novosibirsk")));
    }

    @Test
    public void overriding_clock_timezone_overrides_joda_setting() throws Exception {
        DateTimeZone.setDefault(DateTimeZone.forID("America/New_York"));
        assertThat(JodaCompatibleClock.DEFAULT_ZONE.withZone(ZoneId.of("Europe/Berlin")).getZone(), equalTo(ZoneId.of("Europe/Berlin")));
    }

    @After
    public void clean_up_global_state() throws Exception {
        DateTimeUtils.setCurrentMillisSystem();
        java.util.TimeZone.setDefault(null);
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(java.util.TimeZone.getDefault()));
    }
}

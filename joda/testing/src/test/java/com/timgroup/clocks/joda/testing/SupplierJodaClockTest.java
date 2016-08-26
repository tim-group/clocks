package com.timgroup.clocks.joda.testing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.DateTimeZone.UTC;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

import com.timgroup.clocks.joda.JodaClock;

public class SupplierJodaClockTest {
    @Test
    public void passes_instant_from_input_supplier() throws Exception {
        Instant[] source = new Instant[1];
        SupplierJodaClock clock = new SupplierJodaClock(() -> source[0], UTC);
        source[0] = Instant.parse("2016-08-26T18:30:00Z");
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getDateTimeZone(), equalTo(UTC));
        source[0] = Instant.parse("2016-08-26T18:30:02Z");
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(clock.now(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(clock.getDateTimeZone(), equalTo(UTC));
    }

    @Test
    public void allows_overriding_timezone() throws Exception {
        Instant[] source = new Instant[1];
        SupplierJodaClock clock = new SupplierJodaClock(() -> source[0], UTC);
        JodaClock derivedClock = clock.withZone(DateTimeZone.forID("America/Los_Angeles"));
        source[0] = Instant.parse("2016-08-26T18:30:00Z");
        assertThat(derivedClock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(derivedClock.now(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(derivedClock.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
        source[0] = Instant.parse("2016-08-26T18:30:02Z");
        assertThat(derivedClock.now(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(derivedClock.now(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(derivedClock.getDateTimeZone(), equalTo(DateTimeZone.forID("America/Los_Angeles")));
    }
}

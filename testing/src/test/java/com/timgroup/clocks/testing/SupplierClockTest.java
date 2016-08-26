package com.timgroup.clocks.testing;

import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

public class SupplierClockTest {
    @Test
    public void passes_instant_from_input_supplier() throws Exception {
        Instant[] source = new Instant[1];
        SupplierClock clock = new SupplierClock(() -> source[0], UTC);
        source[0] = Instant.parse("2016-08-26T18:30:00Z");
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getZone(), equalTo(UTC));
        source[0] = Instant.parse("2016-08-26T18:30:02Z");
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(clock.getZone(), equalTo(UTC));
    }

    @Test
    public void allows_overriding_timezone() throws Exception {
        Instant[] source = new Instant[1];
        SupplierClock clock = new SupplierClock(() -> source[0], UTC);
        Clock derivedClock = clock.withZone(ZoneId.of("America/Los_Angeles"));
        source[0] = Instant.parse("2016-08-26T18:30:00Z");
        assertThat(derivedClock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(derivedClock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(derivedClock.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
        source[0] = Instant.parse("2016-08-26T18:30:02Z");
        assertThat(derivedClock.instant(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(derivedClock.instant(), equalTo(Instant.parse("2016-08-26T18:30:02Z")));
        assertThat(derivedClock.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
    }
}

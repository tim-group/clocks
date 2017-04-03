package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;

public class ManualClockTest {
    // somewhat like Duration.ofMinutes(2) but not a Duration
    private static final TemporalAmount NONSTANDARD_TWO_MINUTES = new TemporalAmount() {
        @Override
        public long get(TemporalUnit unit) {
            if (unit.equals(ChronoUnit.MINUTES)) {
                return 2;
            }
            throw new UnsupportedTemporalTypeException("Not supported: " + unit);
        }

        @Override
        public List<TemporalUnit> getUnits() {
            return Collections.singletonList(ChronoUnit.MINUTES);
        }

        @Override
        public Temporal addTo(Temporal temporal) {
            return ((Instant) temporal).plus(2, ChronoUnit.MINUTES);
        }

        @Override
        public Temporal subtractFrom(Temporal temporal) {
            return ((Instant) temporal).minus(2, ChronoUnit.MINUTES);
        }
    };

    @Test
    public void returns_initial_time_and_zone() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
        assertThat(clock.getZone(), equalTo(UTC));
    }

    @Test
    public void advances_by_arbitrary_duration() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.parse("PT321.123S"));
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:35:21.123Z")));
    }

    @Test
    public void advances_by_amount_with_temporal_unit() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(2, ChronoUnit.MINUTES);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:32:00Z")));
    }

    @Test
    public void advances_by_arbitrary_temporal_amount() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(NONSTANDARD_TWO_MINUTES);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:32:00Z")));
    }

    @Test
    public void advances_by_millis() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(123L);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00.123Z")));
    }

    @Test
    public void advances_by_seconds() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(123);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:32:03Z")));
    }

    @Test
    public void advance_by_zero_millis_is_a_noop() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(0L);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test
    public void advance_by_zero_seconds_is_a_noop() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(0);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test
    public void advance_by_zero_duration_is_a_noop() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.ZERO);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test
    public void advance_by_zero_of_units_is_a_noop() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(0, ChronoUnit.MINUTES);
        assertThat(clock.instant(), equalTo(Instant.parse("2016-08-26T18:30:00Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_millis() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpMillis(-123L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_seconds() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bumpSeconds(-123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_duration() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(Duration.parse("PT-1S"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_by_negative_of_units() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        clock.bump(-1, ChronoUnit.MINUTES);
    }

    @Test
    public void allows_timezone_to_be_overridden_in_derived_clock() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        Clock overridden = clock.withZone(ZoneId.of("America/Los_Angeles"));
        clock.bumpSeconds(1);
        assertThat(overridden.instant(), equalTo(Instant.parse("2016-08-26T18:30:01Z")));
        assertThat(overridden.getZone(), equalTo(ZoneId.of("America/Los_Angeles")));
    }

    @Test
    public void overriding_with_same_zone_returns_original_clock() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2016-08-26T18:30:00Z"), UTC);
        assertThat(clock.withZone(UTC), sameInstance(clock));
    }

    @Test
    public void advances_to_an_instant() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("1982-06-22T16:00:00Z"), UTC);
        clock.advanceTo(Instant.parse("2017-02-03T12:05:03Z"));
        assertThat(clock.instant(), equalTo(Instant.parse("2017-02-03T12:05:03Z")));
    }

    @Test
    public void advancing_to_current_instant_is_noop() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("1982-06-22T16:00:00Z"), UTC);
        clock.advanceTo(Instant.parse("1982-06-22T16:00:00Z"));
        assertThat(clock.instant(), equalTo(Instant.parse("1982-06-22T16:00:00Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_to_a_past_instant() throws Exception {
        ManualClock clock = new ManualClock(Instant.parse("2020-12-25T01:02:03Z"), UTC);
        clock.advanceTo(Instant.parse("2017-02-03T12:05:03Z"));
    }
}

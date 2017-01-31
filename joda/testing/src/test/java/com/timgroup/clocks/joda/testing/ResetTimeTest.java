package com.timgroup.clocks.joda.testing;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

public class ResetTimeTest {
    private final Instant fixedInstant = Instant.parse("1992-01-31T14:23:00Z");
    private final DateTimeZone fixedTimeZone = DateTimeZone.forID("Antarctica/Troll");

    @Test
    public void overrides_current_instant_provision() throws Exception {
        Instant timeBeforeBlock = Instant.now();
        Instant timeInsideBlock;
        Instant timeAfterBlock;
        try (ResetTime.Resource ignored = ResetTime.to(fixedInstant).open()) {
            timeInsideBlock = Instant.now();
        }
        timeAfterBlock = Instant.now();

        assertThat(timeInsideBlock, equalTo(fixedInstant));
        assertThat(timeAfterBlock, not(equalTo(fixedInstant)));
        assertThat(timeAfterBlock, greaterThanOrEqualTo(timeBeforeBlock));
    }

    @Test
    public void overrides_current_time_zone_provision() throws Exception {
        DateTime dateTimeBeforeBlock = DateTime.now();
        DateTime dateTimeInsideBlock;
        DateTime dateTimeAfterBlock;
        try (ResetTime.Resource ignored = ResetTime.to(fixedInstant, fixedTimeZone).open()) {
            dateTimeInsideBlock = DateTime.now();
        }
        dateTimeAfterBlock = DateTime.now();

        assertThat(dateTimeInsideBlock, equalTo(new DateTime(fixedInstant, fixedTimeZone)));
        assertThat(dateTimeAfterBlock.getZone(), equalTo(dateTimeBeforeBlock.getZone()));
        assertThat(dateTimeAfterBlock.toInstant(), greaterThanOrEqualTo(dateTimeBeforeBlock.toInstant()));
    }

    @Test
    public void overrides_current_time_zone_provision_from_datetime() throws Exception {
        DateTime dateTimeInsideBlock;
        try (ResetTime.Resource ignored = ResetTime.to(new DateTime(fixedInstant, fixedTimeZone)).open()) {
            dateTimeInsideBlock = DateTime.now();
        }

        assertThat(dateTimeInsideBlock, equalTo(new DateTime(fixedInstant, fixedTimeZone)));
    }
}

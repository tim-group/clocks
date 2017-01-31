package com.timgroup.clocks.joda.testing;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

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

    @Test
    public void enacts_changes_for_runnable() throws Exception {
        List<Instant> container = new ArrayList<>();
        ResetTime.to(fixedInstant).run(() -> container.add(Instant.now()));
        Instant instantInsideBlock = container.iterator().next();
        assertThat(instantInsideBlock, equalTo(fixedInstant));
    }

    @Test
    public void enacts_changes_for_supplier() throws Exception {
        Instant instantInsideBlock = ResetTime.to(fixedInstant).supply(Instant::now);
        assertThat(instantInsideBlock, equalTo(fixedInstant));
    }

    @Test
    public void enacts_changes_for_junit_statement() throws Throwable {
        class CaptureStatement extends Statement {
            private Instant result;

            @Override
            public void evaluate() throws Throwable {
                result = Instant.now();
            }
        }
        CaptureStatement statement = new CaptureStatement();
        Description description = Description.createSuiteDescription(getClass());
        ResetTime.to(fixedInstant).apply(statement, description).evaluate();
        assertThat(statement.result, equalTo(fixedInstant));
    }

    @Test
    public void bumps_by_positive_millis() throws Exception {
        ResetTime resetTime = ResetTime.to(fixedInstant);
        resetTime.bumpMillis(100L);
        assertThat(resetTime.now(), equalTo(fixedInstant.plus(100L)));
    }

    @Test
    public void bumps_by_positive_seconds() throws Exception {
        ResetTime resetTime = ResetTime.to(fixedInstant);
        resetTime.bumpSeconds(5);
        assertThat(resetTime.now(), equalTo(fixedInstant.plus(Duration.standardSeconds(5))));
    }

    @Test
    public void bumps_by_duration() throws Exception {
        ResetTime resetTime = ResetTime.to(fixedInstant);
        resetTime.bump(Duration.standardMinutes(1));
        assertThat(resetTime.now(), equalTo(fixedInstant.plus(Duration.standardMinutes(1))));
    }

    @Test
    public void advances_to_specified_time() throws Exception {
        ResetTime resetTime = ResetTime.to(fixedInstant);
        resetTime.advanceTo(fixedInstant.plus(Duration.standardHours(2)));
        assertThat(resetTime.now(), equalTo(fixedInstant.plus(Duration.standardHours(2))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refuses_to_advance_to_past_time() throws Exception {
        ResetTime resetTime = ResetTime.to(fixedInstant);
        resetTime.advanceTo(fixedInstant.minus(Duration.standardHours(2)));
    }
}

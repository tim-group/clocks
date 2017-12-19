Clocks
======

[![Build Status](https://travis-ci.org/tim-group/clocks.svg?branch=master)](https://travis-ci.org/tim-group/clocks)

Implementations of JSR310's java.time.Clock to help with testing,
and for integration with Joda-Time.

Modules
-------

 - testing - test clocks for use with codebases that only use java.time
 - joda - JodaClock and various friends, a bridge between Joda-Time and java.time
 - joda-testing - equivalent test clocks that extend JodaClock
 
Testing
-------

The basic test clock is ManualClock, which is constructed with an initial time and
zone, and can be advanced by fixed intervals or to other instants. It enforces time
only going forwards.

SupplierClock is mainly a convenience to wrap a supplier of instants as a clock (with
some initial time zone). LatchableClock is a hybrid clock that can either be
"free-running" (just like the system clock) or "frozen" (just like a manual clock).

Joda-Time integration
---------------------

JodaClock is an abstract extension of java.time.Clock that provides factory methods
for Joda-Time instants, DateTimes and LocalDates. It also allows accessing and
overriding the time zone based on DateTimeZone as well as ZoneId. The default
JodaCompatibleClock implementation uses Joda-Time's static DateTimeUtils accessor
as the time source- so getting the instant from a clock instant works just like
Joda's "now" factory method.

This can be used for adopting the pattern of Clock objects for Joda-Time in general,
and is also a useful aid when combining codebases that use java.time and Joda-Time,
since JodaClock objects can be shared between both. JodaClock also supports being
wrapped around any Clock object.

In addition, the joda-testing module provides ManualJodaClock etc classes, equivalent
to the JSR310 clocks in the testing module.

joda-testing also provides a ResetTime JUnit rule, that will reset Joda's static
time to a fixed point before a test, and release it afters. ResetTime also itself
implements JodaClock directly, and has similar bump/advance methods to ManualJodaClock.

package com.timgroup.clocks.testing;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.Supplier;

/**
 * Clock that delegates to a supplier of instants.
 * <p>
 * Typically, the supplier is a lambda, for example:
 * <pre>
 *   private Instant now = Instant.now();
 *   private final Clock testClock = SupplierClock.utc(() -> now);
 * </pre>
 *
 * @see ManualClock
 */
public final class SupplierClock extends Clock {
    private final Supplier<Instant> supplier;
    private final ZoneId zone;

    public static SupplierClock utc(Supplier<Instant> supplier) {
        return new SupplierClock(supplier, ZoneOffset.UTC);
    }

    public static SupplierClock systemDefault(Supplier<Instant> supplier) {
        return new SupplierClock(supplier, ZoneId.systemDefault());
    }

    public SupplierClock(Supplier<Instant> supplier, ZoneId zone) {
        this.supplier = supplier;
        this.zone = zone;
    }

    @Override
    public Instant instant() {
        return supplier.get();
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        if (newZone.equals(zone)) {
            return this;
        }
        return new SupplierClock(supplier, newZone);
    }

    @Override
    public String toString() {
        return "SupplierClock[" + supplier + " @ " + zone + "]";
    }
}

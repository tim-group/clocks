package com.timgroup.clocks.joda.testing;

import java.util.function.Supplier;

import com.timgroup.clocks.joda.JodaClock;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

/**
 * Clock that delegates to a supplier of instants.
 * <p>
 * Typically, the supplier is a lambda, for example:
 * <pre>
 *   private Instant now = Instant.now();
 *   private final JodaClock testClock = SupplierJodaClock.utc(() -> now);
 * </pre>
 *
 * @see ManualJodaClock
 */
public final class SupplierJodaClock extends JodaClock {
    private final Supplier<Instant> supplier;
    private final DateTimeZone zone;

    public static SupplierJodaClock utc(Supplier<Instant> supplier) {
        return new SupplierJodaClock(supplier, DateTimeZone.UTC);
    }

    public static SupplierJodaClock systemDefault(Supplier<Instant> supplier) {
        return new SupplierJodaClock(supplier, DateTimeZone.getDefault());
    }

    public SupplierJodaClock(Supplier<Instant> supplier, DateTimeZone zone) {
        this.supplier = supplier;
        this.zone = zone;
    }

    @Override
    public Instant now() {
        return supplier.get();
    }

    @Override
    public DateTimeZone getDateTimeZone() {
        return zone;
    }

    @Override
    public JodaClock withZone(DateTimeZone newZone) {
        if (newZone.equals(zone)) {
            return this;
        }
        return new SupplierJodaClock(supplier, newZone);
    }

    @Override
    public String toString() {
        return "SupplierJodaClock[" + supplier + " @ " + zone + "]";
    }

}

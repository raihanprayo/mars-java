package dev.scaraz.mars.core;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;

public class TestUtilities {

    private void scoring(double radius, long everySec, long maxSec) {
        for (int i = 0; ; i++) {
            long secToCompare = everySec * (i + 1);
            long msToCompare = secToCompare * 1000;
            double result = BigDecimal.valueOf((1.0 - (radius * i)))
                    .round(new MathContext(2, RoundingMode.HALF_DOWN))
                    .doubleValue();

            System.out.printf("- (%s) %s%n", secToCompare, result);
            if (secToCompare >= maxSec) break;
        }
    }

    private double scoreByInterval(Duration interval, double radius, long everySec, long maxSec) {
        if (interval == null) return 0;

        double result;
        long ms = interval.toMillis(),
                secToCompare, msToCompare;

        for (int i = 0; ; i++) {
            secToCompare = everySec * (i + 1);
            msToCompare = secToCompare * 1000;
            result = BigDecimal.valueOf((1.0 - (radius * i)))
                    .round(new MathContext(2, RoundingMode.HALF_DOWN))
                    .doubleValue();

            if (ms <= msToCompare) return result;

            if (secToCompare >= maxSec) break;
        }

        return radius;
    }

    @Test
    public void testScoring() {
        Duration interval = Duration.ofMinutes(36);

        System.out.printf("Score Response: %s%n",
                scoreByInterval(interval, 0.1, 300, 300 * 6)
        );

        System.out.printf("Score Action: %s%n",
                scoreByInterval(interval, 0.2, 900, 900 * 4)
        );
    }

}

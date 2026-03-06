package com.comonier.shopguiaddon;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class PriceCalculator {
    public static double calculate(double current, double adjustment, boolean isSubtract) {
        BigDecimal currentBD = BigDecimal.valueOf(current);
        BigDecimal adjustmentBD = BigDecimal.valueOf(adjustment);
        BigDecimal result;
        if (isSubtract) {
            result = currentBD.subtract(adjustmentBD);
        } else {
            result = currentBD.add(adjustmentBD);
        }
        double finalValue = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (finalValue >= 0.0) {
            return finalValue;
        }
        return 0.0;
    }
    public static int calculateQuantity(int current, int adjustment, boolean isSubtract) {
        int result;
        if (isSubtract) {
            result = current - adjustment;
        } else {
            result = current + adjustment;
        }
        if (result <= 1) {
            return 1;
        }
        if (result >= 1000) {
            return 1000;
        }
        return result;
    }
}

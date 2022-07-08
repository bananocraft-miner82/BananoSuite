package miner82.bananosuite.classes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Math {

    public static double Round(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(value).setScale(decimalPlaces, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static int RandomBetween(int minimum, int maximum) {

        Random random = new Random();

        try {
            return random.nextInt(minimum, maximum);
        }
        catch (Exception e) {
            return minimum;
        }
    }

}

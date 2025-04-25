package com.hsr.pity.logic;

public class PityCalculator {
    private static final double BASE_RATE = 0.006;
    private static final int SOFT_PITY_START = 74;
    private static final int HARD_PITY = 90;

    public static double calculateCurrentRate(int pityCounter) {
        if (pityCounter >= HARD_PITY) return 1.0;
        if (pityCounter >= SOFT_PITY_START)
            return BASE_RATE + 0.06 * (pityCounter - SOFT_PITY_START + 1);
        return BASE_RATE;
    }

    public static int calculateRemainingPity(int pityCounter) {
        return HARD_PITY - pityCounter;
    }
}
package com.hsr.pity.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PityCalculatorTest {
    @Test
    void testBaseRate() {
        assertEquals(0.006, PityCalculator.calculateCurrentRate(0), 0.001);
    }

    @Test
    void testSoftPity() {
        assertEquals(0.066, PityCalculator.calculateCurrentRate(74), 0.001);
        assertEquals(0.666, PityCalculator.calculateCurrentRate(84), 0.001);
    }

    @Test
    void testHardPity() {
        assertEquals(1.0, PityCalculator.calculateCurrentRate(90), 0.001);
        assertEquals(1.0, PityCalculator.calculateCurrentRate(100), 0.001);
    }
}
package com.hsr.pity.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PityCalculatorTest {
    @Test
    void testBaseRate() {
        assertEquals(0.006, PityCalculator.calculateCurrentRate(0));
    }

    @Test
    void testSoftPity() {
        assertEquals(0.066, PityCalculator.calculateCurrentRate(74));
        assertEquals(0.666, PityCalculator.calculateCurrentRate(84));
    }

    @Test
    void testHardPity() {
        assertEquals(1.0, PityCalculator.calculateCurrentRate(90));
        assertEquals(1.0, PityCalculator.calculateCurrentRate(100));
    }
}
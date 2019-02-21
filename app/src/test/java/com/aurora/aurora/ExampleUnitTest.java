package com.aurora.aurora;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * Example Unit test class with Robolectric
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    /**
     * This test should check if the result of an addition is correct
     */
    @Test
    public void addition_should_be_correct() {
        assertEquals(4, 2 + 2);
    }
}
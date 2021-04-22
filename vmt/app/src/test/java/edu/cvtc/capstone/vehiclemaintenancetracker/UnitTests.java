package edu.cvtc.capstone.vehiclemaintenancetracker;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {
    @Test
    public void VINSimpleCheckDigitTestPasses(){
        assertTrue(Vehicle.isVINValid("11111111111111111", "1999"));
    }

    @Test
    public void VINTestsFailIfLessThan17Chars(){
        assertFalse(Vehicle.isVINValid("B", "1999"));
    }

    @Test
    public void VINTestsCatchBadCharacters(){
        //An I, a Q, and an O
        assertFalse(Vehicle.isVINValid("2C3ID46R4WH170262", "1999"));
        assertFalse(Vehicle.isVINValid("2C3OD46R4WH170262", "1999"));
        assertFalse(Vehicle.isVINValid("2C3QD46R4WH170262", "1999"));
    }

    @Test
    public void VINTestsWorkWithActualVIN(){
        //A 1998 Chrysler Concorde, apparently
        assertTrue(Vehicle.isVINValid("2C3HD46R4WH170262", "1998"));
    }
}
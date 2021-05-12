package edu.cvtc.capstone.vehiclemaintenancetracker;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {

    //Vehicle Data Verification
    @Test
    public void VINSimpleCheckDigitWithYearPasses(){
        assertTrue(VerifyUtil.isVINValid("11111111111111111", "2001"));
    }

    @Test
    public void VINYearCheckIsFutureProof(){
        assertTrue(VerifyUtil.isVINValid("11111111111111111", "2061"));
    }

    @Test
    public void VINTestsFailIfLessThan17Chars(){
        assertFalse(VerifyUtil.isVINValid("B", "1999"));
    }

    @Test
    public void VINTestsCatchBadCharacters(){
        //An I, a Q, and an O
        assertFalse(VerifyUtil.isVINValid("2C3ID46R4WH170262", "1999"));
        assertFalse(VerifyUtil.isVINValid("2C3OD46R4WH170262", "1999"));
        assertFalse(VerifyUtil.isVINValid("2C3QD46R4WH170262", "1999"));
    }

    @Test
    public void VINTestsWorkWithActualVIN(){
        //A 1998 Chrysler Concorde, apparently
        assertTrue(VerifyUtil.isVINValid("2C3HD46R4WH170262", "1998"));
    }

    @Test
    public void VehicleYearValidationPasses(){
        Vehicle v = new Vehicle("Old Brick");
        v.setYear("a");
        assertNull(v.getYear());
        v.setYear("1960");
        assertEquals("1960", v.getYear());
        v.setYear("1200");
        assertEquals("1960", v.getYear());
    }

    @Test
    public void DateParserWorksWithValidDates(){
        assertNotNull(VerifyUtil.parseStringToDate("04/04/2004"));
    }

    @Test
    public void DateParserWorksWithSemiValidDates(){
        assertNotNull(VerifyUtil.parseStringToDate("4/4/04"));
    }

    @Test
    public void DateParserReturnsNullOnInvalidDates(){
        assertNull(VerifyUtil.parseStringToDate("444444"));
    }

    @Test
    public void DateParserReturnsNullWithInvalidChars(){
        assertNull(VerifyUtil.parseStringToDate("Jan 5th, 2007"));
    }

    @Test
    public void DateParserParsesAsAProperParserWould(){
        Calendar c = Calendar.getInstance();
        c.clear();
        //month starts at 0, so jan = 0, feb = 1, mar = 2, apr = 3, etc.
        c.set(2004, 3, 4, 0, 0, 0);
        assertEquals(new Date(c.getTimeInMillis()), VerifyUtil.parseStringToDate("4/4/04"));
    }
}
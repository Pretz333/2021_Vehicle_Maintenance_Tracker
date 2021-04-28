package edu.cvtc.capstone.vehiclemaintenancetracker;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {

    //Vehicle Data Verification
    @Test
    public void VINSimpleCheckDigitTestPasses(){
        assertTrue(VerifyUtil.isVINValid("11111111111111111", "1999"));
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
        Vehicle v = new Vehicle(0, "Old Brick");
        v.setYear("a");
        assertNull(v.getYear());
        v.setYear("1960");
        assertEquals("1960", v.getYear());
        v.setYear("1200");
        assertEquals("1960", v.getYear());
    }

    //Database methods: ensure they don't throw errors to calling functions
    @Test
    public void CheckNullIdReturnsNegOne(){
        DBHelper dbHelper = new DBHelper(null);

        //Try to get a non-existent vehicle.
        //The program should change the id to -1 when it realizes it does not exist
        int id = dbHelper.getVehicleIdByNickname("AReallyLongAndPointlessNameThatIsNotInTheDatabase");
        assertEquals(-1, id);
    }

    @Test
    public void CheckNonexistentVehicleIsNull(){ //and doesn't error
        DBHelper dbHelper = new DBHelper(null);

        //Try to get a non-existent vehicle.
        assertNull(dbHelper.getVehicleByNickname("AReallyLongAndPointlessNameThatIsNotInTheDatabase"));
    }
}
package edu.cvtc.capstone.vehiclemaintenancetracker;

import org.junit.Test;

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
        Vehicle v = new Vehicle("Old Brick");
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
        assertNull(dbHelper.getVehicleById(999999));
    }

    @Test
    public void CheckVehicleIdDBChecks(){ //and doesn't error
        DBHelper dbHelper = new DBHelper(null);

        //Try to get a non-existent vehicle.
        assertFalse(dbHelper.checkIfVehicleIdExists(999999));

        //Try to get a real vehicle. TODO: need to insert a vehicle first
        //assertTrue(dbHelper.checkIfVehicleIdExists(0));
    }

    @Test
    public void CheckSystemIdDBChecks(){ //and doesn't error
        DBHelper dbHelper = new DBHelper(null);

        //Try to get a non-existent vehicle.
        assertFalse(dbHelper.checkIfSystemIdExists(999999));

        //Try to get a real system. TODO: need to insert a system first
        //assertTrue(dbHelper.checkIfSystemIdExists(0));
    }

    @Test
    public void CheckIssueStatusIdDBChecks(){ //and doesn't error
        DBHelper dbHelper = new DBHelper(null);

        //Try to get a non-existent vehicle.
        assertFalse(dbHelper.checkIfIssueStatusIdExists(999999));

        //Try to get a real issue status. TODO: need to insert a issue status first
        //assertTrue(dbHelper.checkIfIssueStatusIdExists(0));
    }

    @Test
    public void EnsureVehicleIdIsSetWhenNoneProvided(){
        DBHelper dbHelper = new DBHelper(null);
        Vehicle v = new Vehicle("Bruh", "Ford", "T", "1980", "B", 100000, "111111111111111111", "ABC-123", new Date(0), 0);
        dbHelper.insertVehicle(v);
        assertTrue(dbHelper.checkIfVehicleIdExists(v.getId()));
    }

    @Test
    public void EnsureVehicleIdEqualsVehicleRowId(){
        DBHelper dbHelper = new DBHelper(null);

        int id = 28;
        Vehicle v = new Vehicle(id, "Bruh", "Ford", "T", "1980", "B", 100000, "111111111111111111", "ABC-123", new Date(0), 0);
        dbHelper.insertVehicle(v);
        assertEquals(id, v.getId());
    }

    @Test
    public void EnsureVehicleSavesWithNullValues(){
        DBHelper dbHelper = new DBHelper(null);
        Vehicle v = new Vehicle("VehicleName");
        dbHelper.insertVehicle(v);
        assertTrue(dbHelper.checkIfVehicleIdExists(v.getId()));
    }
}
package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "edu.cvtc.capstone.vehiclemaintenancetracker.DBHelper.SEARCH";
    public static final String DATABASE_NAME = "VehicleMaintenanceTracker.db";
    public static final int DATABASE_VERSION = 1;

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
    private final List<Issue> issues = new ArrayList<>();

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(VehicleSQL.SQL_CREATE_TABLE_VEHICLE);
        db.execSQL(MaintenanceLogSQL.SQL_CREATE_TABLE_MAINTENANCE_LOG);
        db.execSQL(IssueSQL.SQL_CREATE_TABLE_ISSUE);
        db.execSQL(IssueStatusSQL.SQL_CREATE_TABLE_ISSUE_STATUS);
        db.execSQL(SystemSQL.SQL_CREATE_TABLE_SYSTEM);
        db.execSQL(IssueLogSQL.SQL_CREATE_TABLE_ISSUE_LOG);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO
    }

    public void insertVehicle(Vehicle vehicle) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(VehicleSQL._ID, vehicle.getId());
        values.put(VehicleSQL.COLUMN_VEHICLE_MAKE, vehicle.getMake());
        values.put(VehicleSQL.COLUMN_VEHICLE_MODEL, vehicle.getModel());
        values.put(VehicleSQL.COLUMN_VEHICLE_YEAR, vehicle.getYear());
        values.put(VehicleSQL.COLUMN_VEHICLE_NICKNAME, vehicle.getName());
        values.put(VehicleSQL.COLUMN_VEHICLE_COLOR, vehicle.getColor());
        values.put(VehicleSQL.COLUMN_VEHICLE_MILEAGE, vehicle.getMileage());
        values.put(VehicleSQL.COLUMN_VEHICLE_VIN, vehicle.getVIN());
        values.put(VehicleSQL.COLUMN_VEHICLE_LICENSE_PLATE, vehicle.getLicensePlate());
        values.put(VehicleSQL.COLUMN_VEHICLE_DATE_PURCHASED, vehicle.getPurchaseDate().getTime());
        values.put(VehicleSQL.COLUMN_VEHICLE_VALUE, vehicle.getValue());

        long newRowId = db.insert(VehicleSQL.TABLE_NAME_VEHICLE, null, values);

    }

    public void insertMaintenanceLog(MaintenanceLog maintenanceLog) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MaintenanceLogSQL._ID, maintenanceLog.getId());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_TITLE, maintenanceLog.getTitle());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_DESCRIPTION, maintenanceLog.getDescription());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_DATE, maintenanceLog.getDate().getTime());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_COST, maintenanceLog.getCost());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_TOTAL_TIME, maintenanceLog.getTime().getTime());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_MILEAGE, maintenanceLog.getMileage());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_VEHICLE_ID, maintenanceLog.getVehicleId());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_SYSTEM_ID, maintenanceLog.getSystemId());

        long newRowId = db.insert(MaintenanceLogSQL.TABLE_NAME_MAINTENANCE_LOG, null, values);

    }

    public void insertSystem(System system) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SystemSQL._ID, system.getId());
        values.put(SystemSQL.COLUMN_SYSTEM_DESCRIPTION, system.getDescription());

        long newRowId = db.insert(SystemSQL.TABLE_NAME_SYSTEM, null, values);

    }

    public void insertIssue(Issue issue) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(IssueSQL._ID, issue.getId());
        values.put(IssueSQL.COLUMN_ISSUE_TITLE, issue.getTitle());
        values.put(IssueSQL.COLUMN_ISSUE_DESCRIPTION, issue.getDescription());
        values.put(IssueSQL.COLUMN_ISSUE_PRIORITY, issue.getPriority());
        values.put(IssueSQL.COLUMN_ISSUE_VEHICLE_ID, issue.getVehicleId());
        values.put(IssueSQL.COLUMN_ISSUE_STATUS_ID, issue.getStatusId());

        long newRowId = db.insert(IssueSQL.TABLE_NAME_ISSUE, null, values);

    }

    public void insertIssueStatus(IssueStatus issueStatus) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(IssueStatusSQL._ID, issueStatus.getId());
        values.put(IssueStatusSQL.COLUMN_ISSUE_STATUS_DESCRIPTION, issueStatus.getDescription());

        long newRowId = db.insert(IssueStatusSQL.TABLE_NAME_ISSUE_STATUS, null, values);

    }

    public List<Vehicle> getAllVehicles() {
        SQLiteDatabase db = getReadableDatabase();

        String[] vehicleColumns = {
                VehicleSQL._ID,
                VehicleSQL.COLUMN_VEHICLE_MAKE,
                VehicleSQL.COLUMN_VEHICLE_MODEL,
                VehicleSQL.COLUMN_VEHICLE_YEAR,
                VehicleSQL.COLUMN_VEHICLE_NICKNAME,
                VehicleSQL.COLUMN_VEHICLE_COLOR,
                VehicleSQL.COLUMN_VEHICLE_MILEAGE,
                VehicleSQL.COLUMN_VEHICLE_VIN,
                VehicleSQL.COLUMN_VEHICLE_LICENSE_PLATE,
                VehicleSQL.COLUMN_VEHICLE_DATE_PURCHASED,
                VehicleSQL.COLUMN_VEHICLE_VALUE};

        String vehicleOrderBy = VehicleSQL._ID;
        Cursor cursor = db.query(VehicleSQL.TABLE_NAME_VEHICLE, vehicleColumns, null,
                null, null, null, vehicleOrderBy);

        int vehicleIdPosition = cursor.getColumnIndex(VehicleSQL._ID);
        int vehicleMakePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MAKE);
        int vehicleModelPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MODEL);
        int vehicleYearPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_YEAR);
        int vehicleNicknamePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_NICKNAME);
        int vehicleColorPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_COLOR);
        int vehicleMileagePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MILEAGE);
        int vehicleVINPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_VIN);
        int vehicleLicensePlatePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_LICENSE_PLATE);
        int vehicleDatePurchasedPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_DATE_PURCHASED);
        int vehicleValuePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_VALUE);

        while (cursor.moveToNext()) {
            vehicles.add(new Vehicle(cursor.getInt(vehicleIdPosition), cursor.getString(vehicleNicknamePosition),
                    cursor.getString(vehicleMakePosition), cursor.getString(vehicleModelPosition),
                    cursor.getString(vehicleYearPosition), cursor.getString(vehicleColorPosition),
                    cursor.getInt(vehicleMileagePosition), cursor.getString(vehicleVINPosition),
                    cursor.getString(vehicleLicensePlatePosition), new Date(vehicleDatePurchasedPosition),
                    cursor.getInt(vehicleValuePosition)));
        }
        cursor.close();

        return vehicles;
    }

    public Vehicle getVehicleByNickname(String name) {
        SQLiteDatabase db = getReadableDatabase();

        //Get all of the fields
        String[] categoryColumns = {
            VehicleSQL._ID,
            VehicleSQL.COLUMN_VEHICLE_MAKE,
            VehicleSQL.COLUMN_VEHICLE_MODEL,
            VehicleSQL.COLUMN_VEHICLE_YEAR,
            VehicleSQL.COLUMN_VEHICLE_NICKNAME,
            VehicleSQL.COLUMN_VEHICLE_COLOR,
            VehicleSQL.COLUMN_VEHICLE_MILEAGE,
            VehicleSQL.COLUMN_VEHICLE_VIN,
            VehicleSQL.COLUMN_VEHICLE_LICENSE_PLATE,
            VehicleSQL.COLUMN_VEHICLE_DATE_PURCHASED,
            VehicleSQL.COLUMN_VEHICLE_VALUE
        };

        //Write the query in a SQL injection-proof way
        String filter = VehicleSQL.COLUMN_VEHICLE_NICKNAME + " = ?";
        String[] filterArgs = {name};
        Cursor cursor = db.query(VehicleSQL.TABLE_NAME_VEHICLE, categoryColumns, filter,
                filterArgs, null, null, null);

        //Get the places where all of the information is stored
        int idPosition = cursor.getColumnIndex(VehicleSQL._ID);
        int makePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MAKE);
        int modelPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MODEL);
        int yearPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_YEAR);
        int namePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_NICKNAME);
        int colorPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_COLOR);
        int mileagePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_MILEAGE);
        int VINPosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_VIN);
        int licensePlatePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_LICENSE_PLATE);
        int datePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_DATE_PURCHASED);
        int valuePosition = cursor.getColumnIndex(VehicleSQL.COLUMN_VEHICLE_VALUE);

        //Get the information of the first matching vehicle (so be as specific as possible!)
        cursor.moveToNext();
        Vehicle v = new Vehicle(
                cursor.getInt(idPosition),
                cursor.getString(namePosition),
                cursor.getString(makePosition),
                cursor.getString(modelPosition),
                cursor.getString(yearPosition),
                cursor.getString(colorPosition),
                cursor.getInt(mileagePosition),
                cursor.getString(VINPosition),
                cursor.getString(licensePlatePosition),
                new Date(cursor.getInt(datePosition)),
                cursor.getInt(valuePosition)
        );
        cursor.close();

        return v;
    }

    public int getVehicleIdByNickname(String name) {
        SQLiteDatabase db = getReadableDatabase();
        int id;

        String[] categoryColumns = {
                VehicleSQL._ID
        };

        String filter = VehicleSQL.COLUMN_VEHICLE_NICKNAME + " = ?";
        String[] filterArgs = {name};

        //No cursor.close() in the catch (or a finally) as it's wrapped in a block
        try {
            Cursor cursor = db.query(VehicleSQL.TABLE_NAME_VEHICLE, categoryColumns, filter,
                    filterArgs, null, null, null);

            int idPosition = cursor.getColumnIndex(VehicleSQL._ID);

            cursor.moveToNext();
            id = cursor.getInt(idPosition);
            cursor.close();
        } catch (NullPointerException ex){
            id = -1;
        }

        return id;
    }

    private static final class VehicleSQL implements BaseColumns {
        // Constants for vehicle table and fields
        private static final String TABLE_NAME_VEHICLE = "vehicle";
        private static final String COLUMN_VEHICLE_MAKE = "vehicle_make";
        private static final String COLUMN_VEHICLE_MODEL = "vehicle_model";
        private static final String COLUMN_VEHICLE_YEAR = "vehicle_year";
        private static final String COLUMN_VEHICLE_NICKNAME = "vehicle_nickname";
        private static final String COLUMN_VEHICLE_COLOR = "vehicle_color";
        private static final String COLUMN_VEHICLE_MILEAGE = "vehicle_mileage";
        private static final String COLUMN_VEHICLE_VIN = "vehicle_vin";
        private static final String COLUMN_VEHICLE_LICENSE_PLATE = "vehicle_license_plate";
        private static final String COLUMN_VEHICLE_DATE_PURCHASED = "vehicle_date_purchased";
        private static final String COLUMN_VEHICLE_VALUE = "vehicle_value";

        // Constant to create the vehicle table
        private static final String SQL_CREATE_TABLE_VEHICLE =
                "CREATE TABLE " + TABLE_NAME_VEHICLE + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_VEHICLE_MAKE + " TEXT NOT NULL, " +
                    COLUMN_VEHICLE_MODEL + " TEXT NOT NULL, " +
                    COLUMN_VEHICLE_YEAR + " INTEGER NOT NULL, " +
                    COLUMN_VEHICLE_NICKNAME + " TEXT, " +
                    COLUMN_VEHICLE_COLOR + " TEXT, " +
                    COLUMN_VEHICLE_MILEAGE + " INTEGER NOT NULL, " +
                    COLUMN_VEHICLE_VIN + " INTEGER NOT NULL, " +
                    COLUMN_VEHICLE_LICENSE_PLATE + " TEXT NOT NULL, " +
                    COLUMN_VEHICLE_DATE_PURCHASED + " INTEGER, " +
                    COLUMN_VEHICLE_VALUE + " INTEGER)";

        // Constant to drop the vehicle table
        private static final String SQL_DROP_TABLE_VEHICLE = "DROP TABLE IF EXISTS " + TABLE_NAME_VEHICLE;
    }

    private static final class MaintenanceLogSQL implements BaseColumns {
        // Constants for maintenance log table and fields
        private static final String TABLE_NAME_MAINTENANCE_LOG = "maintenance_log";
        private static final String COLUMN_MAINTENANCE_LOG_TITLE = "maintenance_log_title";
        private static final String COLUMN_MAINTENANCE_LOG_DESCRIPTION = "maintenance_log_description";
        private static final String COLUMN_MAINTENANCE_LOG_DATE = "maintenance_log_date";
        private static final String COLUMN_MAINTENANCE_LOG_COST = "maintenance_log_cost";
        private static final String COLUMN_MAINTENANCE_LOG_TOTAL_TIME = "maintenance_log_total_time";
        private static final String COLUMN_MAINTENANCE_LOG_MILEAGE = "maintenance_log_mileage";
        private static final String COLUMN_MAINTENANCE_LOG_VEHICLE_ID = "maintenance_log_vehicle_id";
        private static final String COLUMN_MAINTENANCE_LOG_SYSTEM_ID = "maintenance_log_system_id";

        // Constant to create the maintenance log table
        private static final String SQL_CREATE_TABLE_MAINTENANCE_LOG =
                "CREATE TABLE " + TABLE_NAME_MAINTENANCE_LOG + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MAINTENANCE_LOG_TITLE + " TEXT NOT NULL, " +
                        COLUMN_MAINTENANCE_LOG_DESCRIPTION + " TEXT, " +
                        COLUMN_MAINTENANCE_LOG_DATE + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_COST + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_TOTAL_TIME + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_MILEAGE + " INTEGER NOT NULL, " +
                        COLUMN_MAINTENANCE_LOG_VEHICLE_ID + " INTEGER NOT NULL, " +
                        COLUMN_MAINTENANCE_LOG_SYSTEM_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_MAINTENANCE_LOG_VEHICLE_ID + ") REFERENCES " +
                        VehicleSQL.TABLE_NAME_VEHICLE + " (" + VehicleSQL._ID + "), " +
                        "FOREIGN KEY (" + COLUMN_MAINTENANCE_LOG_SYSTEM_ID + ") REFERENCES " +
                        SystemSQL.TABLE_NAME_SYSTEM + " (" + SystemSQL._ID + "))";

        // Constant to drop the maintenance log table
        private static final String SQL_DROP_TABLE_MAINTENANCE_LOG = "DROP TABLE IF EXISTS " + TABLE_NAME_MAINTENANCE_LOG;
    }

    private static final class IssueSQL implements BaseColumns {
        // Constants for issue table and fields
        private static final String TABLE_NAME_ISSUE = "issue";
        private static final String COLUMN_ISSUE_TITLE = "issue_title";
        private static final String COLUMN_ISSUE_DESCRIPTION = "issue_description";
        private static final String COLUMN_ISSUE_PRIORITY = "issue_priority";
        private static final String COLUMN_ISSUE_VEHICLE_ID = "issue_vehicle_id";
        private static final String COLUMN_ISSUE_STATUS_ID = "issue_status_id";

        // Constant to create the issue table
        private static final String SQL_CREATE_TABLE_ISSUE =
                "CREATE TABLE " + TABLE_NAME_ISSUE + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ISSUE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_ISSUE_DESCRIPTION + " TEXT, " +
                        COLUMN_ISSUE_PRIORITY + " INTEGER, " +
                        COLUMN_ISSUE_VEHICLE_ID + " INTEGER NOT NULL, " +
                        COLUMN_ISSUE_STATUS_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_ISSUE_VEHICLE_ID + ") REFERENCES " +
                        VehicleSQL.TABLE_NAME_VEHICLE + " (" + VehicleSQL._ID + "), " +
                        "FOREIGN KEY (" + COLUMN_ISSUE_STATUS_ID + ") REFERENCES " +
                        IssueStatusSQL.TABLE_NAME_ISSUE_STATUS + " (" + IssueStatusSQL._ID + "))";

        // Constant to drop the issue table
        private static final String SQL_DROP_TABLE_ISSUE = "DROP TABLE IF EXISTS " + TABLE_NAME_ISSUE;
    }

    private static final class SystemSQL implements BaseColumns {
        // Constants for system table and fields
        private static final String TABLE_NAME_SYSTEM = "system";
        private static final String COLUMN_SYSTEM_DESCRIPTION = "system_description";

        // Constant to create the system table
        private static final String SQL_CREATE_TABLE_SYSTEM =
                "CREATE TABLE " + TABLE_NAME_SYSTEM + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SYSTEM_DESCRIPTION + " TEXT NOT NULL)";

        // Constant to drop the system table
        private static final String SQL_DROP_TABLE_SYSTEM = "DROP TABLE IF EXISTS " + TABLE_NAME_SYSTEM;
    }

    private static final class IssueStatusSQL implements BaseColumns {
        // Constants for issue status table and fields
        private static final String TABLE_NAME_ISSUE_STATUS = "issue_status";
        private static final String COLUMN_ISSUE_STATUS_DESCRIPTION = "issue_status_description";

        // Constant to create the issue status table
        private static final String SQL_CREATE_TABLE_ISSUE_STATUS =
                "CREATE TABLE " + TABLE_NAME_ISSUE_STATUS + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ISSUE_STATUS_DESCRIPTION + " TEXT NOT NULL)";

        // Constant to drop the status table
        private static final String SQL_DROP_TABLE_ISSUE_STATUS = "DROP TABLE IF EXISTS " + TABLE_NAME_ISSUE_STATUS;
    }

    private static final class IssueLogSQL implements BaseColumns {
        // Constants for the issue log table and fields
        private static final String TABLE_NAME_ISSUE_LOG = "issue_log";
        private static final String COLUMN_ISSUE_LOG_LOG_ID = "issue_log_log_id";
        private static final String COLUMN_ISSUE_LOG_ISSUE_ID = "issue_log_issue_id";

        // Constant to create the issue log table
        private static final String SQL_CREATE_TABLE_ISSUE_LOG =
                "CREATE TABLE " + TABLE_NAME_ISSUE_LOG + " (" +
                        COLUMN_ISSUE_LOG_LOG_ID + " INTEGER, " +
                        COLUMN_ISSUE_LOG_ISSUE_ID + " INTEGER, " +
                        "PRIMARY KEY (" + COLUMN_ISSUE_LOG_LOG_ID + ", " + COLUMN_ISSUE_LOG_ISSUE_ID + "), " +
                        "FOREIGN KEY (" + COLUMN_ISSUE_LOG_LOG_ID + ") REFERENCES " +
                        MaintenanceLogSQL.TABLE_NAME_MAINTENANCE_LOG + " (" + MaintenanceLogSQL._ID + "), " +
                        "FOREIGN KEY (" + COLUMN_ISSUE_LOG_ISSUE_ID + ") REFERENCES " +
                        IssueSQL.TABLE_NAME_ISSUE + " (" + IssueSQL._ID + "))";

        // Constant to drop the issue log table
        private static final String SQL_DROP_TABLE_ISSUE_LOG = "DROP TABLE IF EXISTS " + TABLE_NAME_ISSUE_LOG;
    }
}

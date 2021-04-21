package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "edu.cvtc.capstone.vehiclemaintenancetracker.DBHelper.SEARCH";
    public static final String DATABASE_NAME = "VehicleMaintenanceTracker.db";
    public static final int DATABASE_VERSION = 1;

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_TITLE, maintenanceLog.getId());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_DESCRIPTION, maintenanceLog.getDescription());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_DATE, maintenanceLog.getDate().getTime());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_COST, maintenanceLog.getCost());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_TOTAL_TIME, maintenanceLog.getTime().getTime());
        values.put(MaintenanceLogSQL.COLUMN_MAINTENANCE_LOG_MILEAGE, maintenanceLog.getMileage());

        long newRowId = db.insert(MaintenanceLogSQL.TABLE_NAME_MAINTENANCE_LOG, null, values);

    }

    public void insertIssue(Issue issue) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(IssueSQL._ID, issue.getId());
        values.put(IssueSQL.COLUMN_ISSUE_TITLE, issue.getTitle());
        values.put(IssueSQL.COLUMN_ISSUE_DESCRIPTION, issue.getDescription());
        values.put(IssueSQL.COLUMN_ISSUE_PRIORITY, issue.getPriority());

        long newRowId = db.insert(IssueSQL.TABLE_NAME_ISSUE, null, values);

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

        // Constant to create the maintenance log table
        private static final String SQL_CREATE_TABLE_MAINTENANCE_LOG =
                "CREATE TABLE " + TABLE_NAME_MAINTENANCE_LOG + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_MAINTENANCE_LOG_TITLE + " TEXT NOT NULL, " +
                        COLUMN_MAINTENANCE_LOG_DESCRIPTION + " TEXT, " +
                        COLUMN_MAINTENANCE_LOG_DATE + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_COST + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_TOTAL_TIME + " INTEGER, " +
                        COLUMN_MAINTENANCE_LOG_MILEAGE + " INTEGER NOT NULL)";

        // Constant to drop the maintenance log table
        private static final String SQL_DROP_TABLE_MAINTENANCE_LOG = "DROP TABLE IF EXISTS " + TABLE_NAME_MAINTENANCE_LOG;
    }

    private static final class IssueSQL implements BaseColumns {
        // Constants for issue table and fields
        private static final String TABLE_NAME_ISSUE = "issue";
        private static final String COLUMN_ISSUE_TITLE = "issue_title";
        private static final String COLUMN_ISSUE_DESCRIPTION = "issue_description";
        private static final String COLUMN_ISSUE_PRIORITY = "issue_priority";

        // Constant to create the issue table
        private static final String SQL_CREATE_TABLE_ISSUE =
                "CREATE TABLE " + TABLE_NAME_ISSUE + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_ISSUE_TITLE + " TEXT NOT NULL, " +
                        COLUMN_ISSUE_DESCRIPTION + " TEXT, " +
                        COLUMN_ISSUE_PRIORITY + " INTEGER)";

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
}

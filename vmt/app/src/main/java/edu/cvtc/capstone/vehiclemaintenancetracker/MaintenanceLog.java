package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

import java.sql.Time;
import java.util.Date;

public class MaintenanceLog {
    public static final String TAG = "LOG_CLASS";

    private int id;
    private String title;
    private String description;
    private Date date;
    private double cost;
    private Time time;
    private int mileage;
    private int vehicleId;
    private int systemId;

    //Constructors
    //Minimum
    public MaintenanceLog(String title, int vehicleId, int systemId) {
        this.id = -1;
        setTitle(title);
        setVehicleId(vehicleId);
        setSystemId(systemId);
    }

    //Everything but the id
    public MaintenanceLog(String title, String description, Date date, double cost, Time time, int mileage, int vehicleId, int systemId) {
        this.id = -1;
        setTitle(title);
        setDescription(description);
        setDate(date);
        setCost(cost);
        setTime(time);
        setMileage(mileage);
        setVehicleId(vehicleId);
        setSystemId(systemId);
    }

    //Everything, for use when reading from the database. Since it's in the database, it's already passed verification
    public MaintenanceLog(int id, String title, String description, Date date, double cost, Time time, int mileage, int vehicleId, int systemId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.cost = cost;
        this.time = time;
        this.mileage = mileage;
        this.vehicleId = vehicleId;
        this.systemId = systemId;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(VerifyUtil.isStringSafe(title)) {
            this.title = title;
        } else {
            Log.w(TAG, "Log title unsafe");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(VerifyUtil.isTextSafe(description)) {
            this.description = description;
        } else {
            Log.w(TAG, "Log description unsafe");
        }
    }

    public Date getDate() {
        return date;
    }

    //Type casting from the TextView is all we need for verification
    public void setDate(Date date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    //This is a calculated field, but even if the user types it in,
    //type casting from the TextView is all we need for verification
    public void setCost(double cost) {
        this.cost = cost;
    }

    public Time getTime() {
        return time;
    }

    //Type casting from the TextView is all we need for verification
    public void setTime(Time time) {
        this.time = time;
    }

    public int getMileage() {
        return mileage;
    }

    //Type casting from the TextView is all we need for verification
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        //DBHelper dbHelper = new DBHelper(null);
        //if(dbHelper.checkIfVehicleIdExists(vehicleId)) {
        this.vehicleId = vehicleId;
        //} else {
        //Log.w(TAG, "VehicleID did not exist");
        //}
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        //DBHelper dbHelper = new DBHelper(null);
        //if(dbHelper.checkIfSystemIdExists(systemId)) {
        this.systemId = systemId;
        //} else {
        //Log.w(TAG, "SystemId did not exist");
        //}
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
        return builder.toString();
    }
}
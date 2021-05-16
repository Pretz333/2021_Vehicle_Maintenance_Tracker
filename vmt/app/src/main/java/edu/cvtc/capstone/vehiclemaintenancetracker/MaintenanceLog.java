package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

import java.util.Date;

public class MaintenanceLog {
    public static final String TAG = "LOG_CLASS";

    private int id;
    private String title;
    private String description;
    private Date date;
    private double cost;
    private int time;
    private int mileage;
    private int vehicleId;
    private int systemId;

    // Constructors, using sets to keep DRY
    // Minimum
    public MaintenanceLog(String title, int vehicleId) {
        this.id = -1;
        setTitle(title);
        setVehicleId(vehicleId);
    }

    // Everything but the id
    public MaintenanceLog(String title, String description, Date date, double cost, int time, int mileage, int vehicleId, int systemId) {
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

    // Everything, for use when reading from the database. Since it's in the database, it's already passed verification
    public MaintenanceLog(int id, String title, String description, Date date, double cost, int time, int mileage, int vehicleId, int systemId) {
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

    // Getters and Setters
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
        if (VerifyUtil.isStringSafe(title)) {
            this.title = title;
        } else {
            Log.w(TAG, "Log title unsafe");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (VerifyUtil.isTextSafe(description)) {
            this.description = description;
        } else {
            Log.w(TAG, "Log description unsafe");
        }
    }

    public Date getDate() {
        return date;
    }

    // Type casting from the TextView is all we need for verification
    public void setDate(Date date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    // This is a calculated field, but even if the user types it in,
    // type casting from the TextView is all we need for verification
    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getTime() {
        return time;
    }

    // Type casting from the TextView is all we need for verification
    public void setTime(int time) {
        this.time = time;
    }

    public int getMileage() {
        return mileage;
    }

    // Type casting from the TextView is all we need for verification
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return "Log ID: " + id + ", Title: " + title + ", Description: " + description;
    }
}
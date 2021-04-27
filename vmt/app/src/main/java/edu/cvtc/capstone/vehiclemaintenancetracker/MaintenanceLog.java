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
    //Default, can probably delete later
    public MaintenanceLog() {
    }

    //Minimum. May want to add Title?
    public MaintenanceLog(int id, String title, int vehicleId, int systemId) {
        setId(id);
        setTitle(title);
        setVehicleId(vehicleId);
        setSystemId(systemId);
    }

    //Everything
    public MaintenanceLog(int id, String title, String description, Date date, double cost, Time time, int mileage, int vehicleId, int systemId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setDate(date);
        setCost(cost);
        setTime(time);
        setMileage(mileage);
        setVehicleId(vehicleId);
        setSystemId(systemId);
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    //May want to delete? Only have the database use this with the constructors above?
    public void setId(int id) {
        //TODO: Check if record is in database or the call came from the database
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
        if(VerifyUtil.isStringSafe(description)) {
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
        //TODO: Check if record is in database or the call came from the database
        this.vehicleId = vehicleId;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        //TODO: Check if record is in database or the call came from the database
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
                //Append vehicle.name based where vehicle.id = vehicleId
                //Append status.name based where status.id = statusId
        return builder.toString();
    }
}

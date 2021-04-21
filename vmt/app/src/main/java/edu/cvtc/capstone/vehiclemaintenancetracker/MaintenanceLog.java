package edu.cvtc.capstone.vehiclemaintenancetracker;

import java.sql.Time;
import java.util.Date;

public class MaintenanceLog {
    private int id;
    private String title;
    private String description;
    private Date date;
    private double cost;
    private Time time;
    private int mileage;
    private int vehicleId;
    private int statusId;

    //Constructors
    //Default, can probably delete later
    public MaintenanceLog() {
    }

    //Minimum. May want to add Title?
    public MaintenanceLog(int id, int vehicleId, int statusId) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.statusId = statusId;
    }

    //Everything
    public MaintenanceLog(int id, String title, String description, Date date, double cost, Time time, int mileage, int vehicleId, int statusId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.cost = cost;
        this.time = time;
        this.mileage = mileage;
        this.vehicleId = vehicleId;
        this.statusId = statusId;
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    //May want to delete? Only have the database use this with the constructors above?
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        //TODO: Make verification that the vehicleId exists
        this.vehicleId = vehicleId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        //TODO: Make verification that the statusId exists
        this.statusId = statusId;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Log ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
                //Append vehicle.name based where vehicle.id = vehicleId
                //Append status.name based where status.id = statusId
        return builder.toString();
    }
}

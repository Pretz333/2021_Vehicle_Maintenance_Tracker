package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

public class Issue {
    public static final String TAG = "ISSUE_CLASS";
    
    private int id;
    private String title;
    private String description;
    private int priority; //TODO: Make this a FK?
    private int vehicleId;
    private int statusId;

    //Constructors
    //Default, can probably delete later
    public Issue() {
    }

    //Minimum. May want to add Title?
    public Issue(int id, int vehicleId, int statusId) {
        setId(id);
        setVehicleId(vehicleId);
        setStatusId(statusId);
    }

    //Everything
    public Issue(int id, String title, String description, int priority, int vehicleId, int statusId) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setPriority(priority);
        setVehicleId(vehicleId);
        setStatusId(statusId);
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
        if(VerifyUtil.isStringSafe(title)){
            this.title = title;
        } else {
            Log.w(TAG, "Issue title unsafe");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(VerifyUtil.isStringSafe(description)){
            this.description = description;
        } else {
            Log.w(TAG, "Issue description unsafe");
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        //TODO: Check if record is in database or the call came from the database
        this.vehicleId = vehicleId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        //TODO: Check if record is in database or the call came from the database
        this.statusId = statusId;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Issue ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
                //Append vehicle.name based where vehicle.id = vehicleId
                //Append status.name based where status.id = statusId
        return builder.toString();
    }
}

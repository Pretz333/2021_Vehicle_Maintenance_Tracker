package edu.cvtc.capstone.vehiclemaintenancetracker;

public class Issue {
    private int id;
    private String title;
    private String description;
    private int priority; //Make this a FK?
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
        builder.append("Issue ID:").append(id)
                .append("\nTitle:").append(title)
                .append("\nDescription:").append(description);
                //Append vehicle.name based where vehicle.id = vehicleId
                //Append status.name based where status.id = statusId
        return builder.toString();
    }
}

package edu.cvtc.capstone.vehiclemaintenancetracker;

public class System {
    private int id;
    private String description;

    //Pre-database insert
    public System(String description) {
        this.id = -1;
        this.description = description;
    }

    //For use when reading from the database
    public System(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

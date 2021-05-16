package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.util.Log;

public class System {
    public static final String TAG = "SYSTEM_CLASS";

    private int id;
    private String description;

    // Constructors, using a set to keep DRY
    // Pre-database insert so there's no ID
    public System(String description) {
        this.id = -1;
        setDescription(description);
    }

    // For use when reading from the database. Since it's in the database, it's already passed verification
    public System(int id, String description) {
        this.id = id;
        this.description = description;
    }

    // Getters and Setters
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
        if (VerifyUtil.isStringLettersOnly(description)) {
            this.description = description;
        } else {
            Log.w(TAG, "Description unsafe");
        }
    }

    @Override
    public String toString() {
        return "System ID: " + id + ", Description: " + description;
    }
}

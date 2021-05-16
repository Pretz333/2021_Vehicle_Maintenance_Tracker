package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.Context;
import android.content.SharedPreferences;

/*
This is a custom class that uses SharedPreferences to get and set
the amount of issues displayed on the MainActivity's Recycler View.

This gives us a little wedge preventing us from querying the database
a second time to grab the amount of issues a specified vehicle has.

 */
public class PreferenceUtil {

    // Member variables
    private final SharedPreferences sharedPreferences;
    private final String KEY_ISSUE = "issue";

    // Constructor
    public PreferenceUtil(Context context) {
        // Member variables
        String PREFERENCE_FILE = "edu.cvtc.capstone.vehiclemaintenancetracker.PREFERENCE_FILE_KEY";
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    // Get a value of a shared preference
    // Returns the value of the key if successful, -1 if it fails.
    // Note: The VehicleID field has no relation to the database.
    // This is just a way to identify unique key-value pairs within the shared preferences file.
    public int getIssueCountByVehicleId(int vehicleID) {
        int returnValue;

        // Create the unique key used to grab a value.
        // This can be anything. Just so happens this looks clean. :)
        String key = KEY_ISSUE + "_" + vehicleID;

        // Get the preference tied to an id
        returnValue = sharedPreferences.getInt(key, -1);

        return returnValue;
    }

    // Similar to the getter above, this just sets a value to a corresponding key.
    public void setIssueCountByVehicleId(int vehicleID, int newValue) {
        // Create an editor for the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key, just like the getter above
        String key = KEY_ISSUE + "_" + vehicleID;

        // Update the key specified along with its new value
        editor.putInt(key, newValue);

        // Commit the changes asynchronously
        editor.apply();
    }

    public void deleteIssueCountByVehicleId(int VehicleID) {
        // Create an editor for the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create a unique key
        String key = KEY_ISSUE + "_" + VehicleID;

        // Remove the key and its corresponding value
        editor.remove(key);

        // Commit the changes asynchronously
        editor.apply();
    }

}


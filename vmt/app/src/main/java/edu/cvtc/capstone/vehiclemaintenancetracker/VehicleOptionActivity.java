package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class VehicleOptionActivity extends AppCompatActivity {

    // Intent extras used by the MainActivity's RecyclerView
    public static final String EXTRA_VEHICLE_ID = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_VEHICLE_ID";
    //public static final String EXTRA_VEHICLE_NICKNAME = "edu.cvtc.capstone.vehiclemaintenancetracker.EXTRA_VEHICLE_NICKNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_option);

        // Grab the intent data
        Intent receivedIntent = getIntent();

        // Grab the ID with a provided default value
        int vehicleIdFromRecycler = receivedIntent.getIntExtra(EXTRA_VEHICLE_ID, -1);

        // Get the user hint TextView and set it
        // Short-hand because I'm that lazy at the moment... - Alexander
        ((TextView) findViewById(R.id.textView_optionActivity_vehicleHint))
                .setText(getResources()
                        .getString(R.string.optionActivity_userHint)
                        .concat(" Vehicle/RecyclerView Item ID Of: " + vehicleIdFromRecycler));
    }
}
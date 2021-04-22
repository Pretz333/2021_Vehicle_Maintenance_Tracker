package edu.cvtc.capstone.vehiclemaintenancetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class IssueActivity extends AppCompatActivity {

    private int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        // Grab the intent data
        Intent receivedIntent = getIntent();

        // Grab the ID with a provided default value
        vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        //Toast it to make sure it was passed along, TODO: delete this
        Toast.makeText(getApplicationContext(), "VehicleID: " + Integer.toString(vehicleId), Toast.LENGTH_LONG).show();
    }
}
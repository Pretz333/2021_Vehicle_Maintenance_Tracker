package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VehicleSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    // The ID of the vehicle received via intent
    private int vehicleId;

    // The mode of the activity.
    // The only difference between edit & create
    // mode is how the save button functions.
    //
    private boolean isInCreative;

    // View references to all the editText fields
    EditText mNickname,
            mMake,
            mModel,
            mYear,
            mPlate,
            mDatePurchased,
            mColor,
            mMileage,
            mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for
        // reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VehicleSettingsActivity.super.finish();
            }
        });

        // Get references to all the editText fields
        mNickname = findViewById(R.id.vehicleSettings_editTextNickname);
        mMake = findViewById(R.id.vehicleSettings_editTextMake);
        mModel = findViewById(R.id.vehicleSettings_editTextModel);
        mYear = findViewById(R.id.vehicleSettings_editTextYear);
        mPlate = findViewById(R.id.vehicleSettings_editTextPlate);
        mDatePurchased = findViewById(R.id.vehicleSettings_editTextDatePurchased);
        mColor = findViewById(R.id.vehicleSettings_editTextColor);
        mMileage = findViewById(R.id.vehicleSettings_editTextMileage);
        mValue = findViewById(R.id.vehicleSettings_editTextValue);
        Button buttonSave = findViewById(R.id.vehicleSettings_buttonSave);

        // Initialize listener for this activity
        buttonSave.setOnClickListener(this);


        // Get the vehicleID from the intent
        Intent receivedIntent = getIntent();
        vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Check if this activity should be in create mode or
        // edit mode.
        //
        // If a VehicleID was passed to this activity, we will initiate edit mode.
        // If no ID was passed to this activity, we will initiate create mode.
        //
        if (vehicleId == -1) {

            // Friendly snackbar for us developers
            Snackbar.make(toolbar, "This activity is in create mode", Snackbar.LENGTH_LONG).show();
            // Prepare this activity in create mode
            isInCreative = true;

        } else {

            // Friendly snackbar for us developers
            Snackbar.make(toolbar, "This activity is in edit mode", Snackbar.LENGTH_LONG).show();

            // Prepare this activity in edit mode
            isInCreative = false;

            // Since we are in edit mode (a vehicle id was
            // passed to this activity), we now need to
            // populate the fields with the data from
            // that vehicle (a test object in this case)
            //
            // TODO: Replace with an actual vehicle retrieved from the database
            Vehicle vehicle = new Vehicle(
                    12,
                    "Tunbruh",
                    "Toyota",
                    "Tundra",
                    "2017",
                    "white",
                    180000,
                    "A8DM6Fl8XK01LA8F",
                    "LDA-9815",
                    new Date(1619130376711L),
                    14000);

            // Populate fields
            populateFieldsByObject(vehicle);

        }

    }


    // Populate all the fields by a Vehicle object
    private void populateFieldsByObject(Vehicle vehicle) {
        // Set the fields data from the given vehicle
        // object
        mNickname.setText(vehicle.getName());
        mMake.setText(vehicle.getMake());
        mModel.setText(vehicle.getModel());
        mYear.setText(vehicle.getYear());
        mPlate.setText(vehicle.getLicensePlate());
        mColor.setText(vehicle.getColor());
        mMileage.setText(String.valueOf(vehicle.getMileage()));
        mValue.setText(String.valueOf(vehicle.getValue()));

        // Set the date formatting for easier reading
        // because no one wants to read a LONG date.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/y", Locale.ENGLISH);
        mDatePurchased.setText(simpleDateFormat.format(vehicle.getPurchaseDate()));
    }

    // Listener for view events.
    // Though, it might just be for the save button
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicleSettings_buttonSave:
                if (isInCreative) {
                    // TODO: Validate that the editText fields contain
                    //  real data. If all is good, CREATE a new vehicle
                    //  in the database and return to the MainActivity
                    //
                    // Just a heads up, we might want to put the validation and
                    // database insertion code into their own methods. Thoughts?
                    // - Alexander
                    Snackbar.make(v, "INSERT vehicle", Snackbar.LENGTH_SHORT).show();
                } else {
                    // TODO: Validate that the editText fields contain
                    //  real data. If all is good, UPDATE the vehicle
                    //  in the database and return to the MainActivity
                    Snackbar.make(v, "UPDATE vehicle", Snackbar.LENGTH_SHORT).show();
                }
                break;

            default:
                // ...
        }
    }
}
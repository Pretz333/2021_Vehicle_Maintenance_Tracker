package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VehicleSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "VEHICLESETTINGS_CLASS";

    //Class variables
    DBHelper dbHelper = new DBHelper(VehicleSettingsActivity.this);
    Vehicle vehicle = null;

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

        // Back button, better than the Manifest way for reasons... - Alexander
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
        Button buttonDelete = findViewById(R.id.vehicleSettings_buttonDelete);

        // Initialize listener for this activity
        buttonSave.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        // Get the vehicleID from the intent
        Intent receivedIntent = getIntent();
        int vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // Check if this activity should be in create mode or edit mode.
        // If a VehicleID was passed to this activity, we will initiate edit mode.
        // If no ID was passed to this activity, we will initiate create mode.
        if (vehicleId != -1) {

            // Friendly snackbar for us developers
            Snackbar.make(toolbar, "This activity is in edit mode", Snackbar.LENGTH_LONG).show();

            // Since we are in edit mode (a vehicle id was passed to this activity),
            // we now need to populate the fields with the data from that vehicle
            vehicle = dbHelper.getVehicleById(vehicleId);
            populateFieldsByObject(vehicle);

            // This vehicle is already in the database, so the delete button should be visible.
            buttonDelete.setVisibility(View.VISIBLE);

        }

    }

    // Populate all the fields by a Vehicle object
    private void populateFieldsByObject(Vehicle vehicle) {
        // Set the fields data from the given vehicle object
        mNickname.setText(vehicle.getName());
        mMake.setText(vehicle.getMake());
        mModel.setText(vehicle.getModel());
        mYear.setText(vehicle.getYear());
        mPlate.setText(vehicle.getLicensePlate());
        mColor.setText(vehicle.getColor());
        mMileage.setText(String.valueOf(vehicle.getMileage()));
        mValue.setText(String.valueOf(vehicle.getValue()));

        // Set the date formatting for easier reading because no one wants to read a LONG date.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/y", Locale.ENGLISH);
        mDatePurchased.setText(simpleDateFormat.format(vehicle.getPurchaseDate()));
    }

    private void updateVehicleWithValues() {
        //Ensure they have the params needed to have the minimum constructor
        if(!mNickname.getText().toString().equals("")) {
            //Make the vehicle if this is a new vehicle
            if (vehicle == null) {
                vehicle = new Vehicle(mNickname.getText().toString());
            }

            //For the remaining properties, send them through the validation and set them
            //as long as the user typed something in
            if (!mMake.getText().toString().equals("")) {
                vehicle.setMake(mMake.getText().toString());
            }

            if (!mModel.getText().toString().equals("")) {
                vehicle.setModel(mModel.getText().toString());
            }

            if (!mYear.getText().toString().equals("")) {
                vehicle.setYear(mYear.getText().toString());
            }

            if (!mColor.getText().toString().equals("")) {
                vehicle.setColor(mColor.getText().toString());
            }

            if (!mMileage.getText().toString().equals("")) {
                vehicle.setMileage(Integer.parseInt(mMileage.getText().toString()));
            }

            if (!mValue.getText().toString().equals("")) {
                vehicle.setValue(Double.parseDouble(mValue.getText().toString()));
            }

            if (!mPlate.getText().toString().equals("")) {
                vehicle.setLicensePlate(mPlate.getText().toString());
            }

            //VIN
            //Purchase Date
        }
    }

    // Listener for view events.
    // Though, it might just be for the save button
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vehicleSettings_buttonSave){
            //If we found a vehicle from the id passed, update it. If not, make one
            if (vehicle == null || vehicle.getId() == -1) {
                updateVehicleWithValues();
                dbHelper.insertVehicle(vehicle);
                Toast.makeText(this, "Successfully added " + vehicle.getName() + "!", Toast.LENGTH_SHORT).show();
            } else {
                updateVehicleWithValues();
                //TODO: Create an update statement in the DBHelper
                Snackbar.make(v, "UPDATE vehicle", Snackbar.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.vehicleSettings_buttonDelete) {
            // Display the alert dialog to confirm the vehicle delete.
            AlertDialog.Builder builder = new AlertDialog.Builder(VehicleSettingsActivity.this);

            // Create the LayoutInflater to use the custom layout.
            LayoutInflater inflater = getLayoutInflater();
            View view = VehicleSettingsActivity.this.getLayoutInflater().inflate(R.layout.delete_alert_dialog, null);

            // Get a reference to the buttons in the delete alert dialog
            Button yesButton = view.findViewById(R.id.deleteAlertDialog_buttonYes);
            Button noButton = view.findViewById(R.id.deleteAlertDialog_buttonNo);

            builder.setView(view);
            AlertDialog alert = builder.create();

            // The yes button was clicked.
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete vehicle from the database
                    dbHelper.deleteVehicle(vehicle);

                    // Close the alert dialog box
                    alert.cancel();

                    // Display a toast that the vehicle was deleted
                    Toast.makeText(getApplicationContext(), "The vehicle was deleted.", Toast.LENGTH_SHORT).show();
                }
            });

            // The no button was clicked.
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close the alert dialog
                    alert.cancel();
                }
            });

            // Display the alert dialog
            alert.show();
        }
    }

}
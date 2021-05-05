package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
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
            mValue,
            mVIN;

    TextInputLayout eNickname,
                    eMake,
                    eModel,
                    eYear,
                    ePlate,
                    eColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button, better than the Manifest way for reasons... - Alexander
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> VehicleSettingsActivity.super.finish());

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
        mVIN = findViewById(R.id.vehicleSettings_editTextVin);
        Button buttonSave = findViewById(R.id.vehicleSettings_buttonSave);
        Button buttonDelete = findViewById(R.id.vehicleSettings_buttonDelete);

        // These view references are for the container that holds the
        // edit text. The purposes of these are only to provide the
        // material design error message with better highlighting.
        eNickname = findViewById(R.id.vehicleSettings_textInputNickname);
        eMake = findViewById(R.id.vehicleSettings_textInputMake);
        eModel = findViewById(R.id.vehicleSettings_textInputModel);
        eYear = findViewById(R.id.vehicleSettings_textInputYear);
        ePlate = findViewById(R.id.vehicleSettings_textInputPlate);
        eColor = findViewById(R.id.vehicleSettings_textInputColor);

        // Initialize listener for this activity
        buttonSave.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        // Get the vehicleID from the intent
        Intent receivedIntent = getIntent();
        int vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid VehicleID was passed to this activity, we want to pre-populate the fields
        if (vehicleId != -1) {
            //Since a vehicleID was passed, we also want to grab the vehicle from the db for later modification
            vehicle = dbHelper.getVehicleById(vehicleId);
            populateFieldsByObject(vehicle);
          
            // This vehicle is already in the database, so the delete button should be visible.
            buttonDelete.setVisibility(View.VISIBLE);
        }

        if (mNickname.getParent() instanceof TextInputLayout) {
            TextInputLayout tip = (TextInputLayout) mNickname.getParent();
            tip.setError("Some error message here");
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
        if(vehicle.getMileage() != 0) {
            mMileage.setText(String.valueOf(vehicle.getMileage()));
        }
        if(vehicle.getValue() != 0) {
            mValue.setText(String.valueOf(vehicle.getValue()));
        }
        mVIN.setText(vehicle.getVIN());

        if(vehicle.getPurchaseDate() != null) {
            // Set the date formatting for easier reading because no one wants to read a LONG date.
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/y", Locale.ENGLISH);
            mDatePurchased.setText(simpleDateFormat.format(vehicle.getPurchaseDate()));
        }
    }

    // Validate that required fields contain data.
    // If some fields don't contain data, alert the user and highlight the field red.
    // Returns TRUE if all fields are good to go, FALSE otherwise.
    private boolean hasMinimums() {
        // If any field was empty, or contains invalid data, set this to true.
        // This will trigger an alert dialog alerting the user of the fields that aren't correct.
        // This also serves as a return value.
        boolean flagField = false;

        // Store each editText into an array
        // This makes it easier to loop through all the fields and check them without having
        // to write lots of boilerplate code for new or removed editText box's.
        EditText[] fieldsToCheck = {mNickname, mMake, mModel, mYear, mColor, mPlate, /*mDatePurchased, mMileage/*, mValue, mVIN*/};

        // These are the fields that should be highlighted with the better
        // material design error message indicators
        TextInputLayout[] fieldsToHighlight = {eNickname, eMake, eModel, eYear, eColor, ePlate};

        // Loop through the fields and check if they contain
        // data. If not, highlight the respectful field(s)
        for (int i=0; i<fieldsToCheck.length; i++) {
            String tempStringFromField = fieldsToCheck[i].getText().toString();

            // If the field contains no string, just a space,
            // or is literally empty, highlight the respectful
            // editText box with a warning message and set the
            // flagFieldEmpty variable to true
            //
            // Check if the string is empty in any way.
            // If so set the editText to that specific
            // warning message
            if (tempStringFromField.equals("")
                    || tempStringFromField.equals(" ")
                    || tempStringFromField.isEmpty()) {

                // Set the error message for the dialog
                //editText.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
                fieldsToHighlight[i].setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));

                // One or more fields were empty, set to true
                flagField = true;

            } else if (!VerifyUtil.isStringSafe(tempStringFromField)) {
                fieldsToHighlight[i].setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
                // One or more fields contained invalid data, set to true
                flagField = true;

            } else {
                // Set the error to null
                // if everything passes checking!
                fieldsToHighlight[i].setError(null);
            }

        }

        // One or more fields were empty. Let's tell the user about it!
        if (flagField) {
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
            alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
            alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
            alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
            alert.show();
        }

        return !flagField;
    }

    private void updateVehicleWithValues() {
        //Ensure they have the params needed to have the minimum constructor
        if(!mNickname.getText().toString().equals("")) {
            //Make the vehicle if this is a new vehicle
            if (vehicle == null) {
                vehicle = new Vehicle(mNickname.getText().toString());
            } else {
                vehicle.setName(mNickname.getText().toString());
            }
        }

        // Now that we've given a chance to create the vehicle, only
        // set the remaining properties if we have a vehicle
        if(vehicle != null && vehicle.getName() != null) {
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

            if (!mVIN.getText().toString().equals("")) {
                vehicle.setVIN(mVIN.getText().toString());
            }

            if (!mDatePurchased.getText().toString().equals("")) {
                vehicle.setPurchaseDate(VerifyUtil.parseStringToDate(mDatePurchased.getText().toString()));
            }
        }
    }

    // Listener for view events.
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vehicleSettings_buttonSave){
            // Check if all fields are valid, no need for an ELSE block as
            // the function hasMinimums() will alert the user on its own.
            if(hasMinimums()) {
                //Update all values
                updateVehicleWithValues();

                //If we found a vehicle from the id passed, update it. If not, make one
                if (vehicle == null) { //Should never be called, as hasMinimums() should catch this
                    //It couldn't be made as it didn't have the minimum properties needed to be functional
                    Snackbar.make(v, "Vehicle must have a nickname", Snackbar.LENGTH_SHORT).show();
                } else if(vehicle.getId() == -1) {
                    dbHelper.insertVehicle(vehicle);
                    Snackbar.make(v, "Successfully added " + vehicle.getName() + "!", Snackbar.LENGTH_SHORT).show();
                    // Close this activity and return to the main activity
                    VehicleSettingsActivity.super.finish();
                } else {
                    dbHelper.updateVehicle(vehicle);
                    Snackbar.make(v, "Successfully updated " + vehicle.getName() + "!", Snackbar.LENGTH_SHORT).show();
                    VehicleSettingsActivity.super.finish();
                }
            }
        } else if (v.getId() == R.id.vehicleSettings_buttonDelete) {
            // Display the alert dialog to confirm the vehicle delete.
            AlertDialog.Builder builder = new AlertDialog.Builder(VehicleSettingsActivity.this);

            // Create the LayoutInflater to use the custom layout.
            LayoutInflater inflater = getLayoutInflater();
            View view = VehicleSettingsActivity.this.getLayoutInflater().inflate(R.layout.alert_dialog, null);

            // Get a reference to the buttons in the delete alert dialog
            Button yesButton = view.findViewById(R.id.alertDialog_buttonYes);
            Button noButton = view.findViewById(R.id.alertDialog_buttonNo);

            builder.setView(view);
            AlertDialog alert = builder.create();

            TextView alertDialogText = view.findViewById(R.id.alertDialog_message);
            alertDialogText.setText(R.string.alertDialog_messageDeleteVehicle);

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
                    VehicleSettingsActivity.super.finish();
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
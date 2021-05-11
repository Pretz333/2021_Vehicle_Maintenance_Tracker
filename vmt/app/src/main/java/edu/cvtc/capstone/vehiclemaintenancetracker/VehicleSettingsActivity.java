package edu.cvtc.capstone.vehiclemaintenancetracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    // A custom preference util used to set/get
    // a key-value pair. In this case, the amount
    // of issues a given vehicle has.
    private PreferenceUtil preferenceUtil;

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
                    eColor,
                    eVIN;

    int vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_settings);

        // Initialize the preference util
        preferenceUtil = new PreferenceUtil(this);

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
        eVIN = findViewById(R.id.vehicleSettings_textInputVin);

        // Initialize listener for this activity
        buttonSave.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        // Get the vehicleID from the intent
        Intent receivedIntent = getIntent();
        vehicleId = receivedIntent.getIntExtra(VehicleOptionActivity.EXTRA_VEHICLE_ID, -1);

        // If a valid VehicleID was passed to this activity, we want to pre-populate the fields
        if (vehicleId != -1) {
            //Since a vehicleID was passed, we also want to grab the vehicle from the db for later modification
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


    // This checks every editText field if its empty, or contains invalid data.
    //
    // If it catches any issues, it will highlight the editText container field and return false.
    private boolean hasMinimumRequirements() {
        // Whether the values from the fields can be inserted into the database without conflict.
        boolean retVal = true;

        // Convert the EditText fields to strings
        String checkNickname = mNickname.getText().toString();
        String checkMake = mMake.getText().toString();
        String checkModel = mModel.getText().toString();
        String checkYear = mYear.getText().toString();
        String checkPlate = mPlate.getText().toString();
        String checkColor = mColor.getText().toString();
        String checkVIN = mVIN.getText().toString().toUpperCase();

        //Make the vehicle if this is a new vehicle
        if (vehicle == null) {
            vehicle = new Vehicle(checkNickname);
        } else {
            vehicle.setName(checkNickname);
        }

        // Check if each field is not blank, null, or contains invalid data.
        // If it does, retVal will be false.

        // Check if the nickname is empty or contains invalid data.
        //
        // If not, set the values to the object
        // TODO: These are only separated because each one might have a different
        // VerifyUtil check
        if (isStringEmpty(checkNickname)) {
            retVal = false;
            eNickname.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkNickname)) {
            retVal = false;
            eNickname.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eNickname.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setName(checkNickname);
        }

        if (isStringEmpty(checkMake)) {
            retVal = false;
            eMake.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkMake)) {
            retVal = false;
            eMake.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eMake.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setMake(checkMake);
        }

        if (isStringEmpty(checkModel)) {
            retVal = false;
            eModel.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkModel)) {
            retVal = false;
            eModel.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eModel.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setModel(checkModel);
        }

        if (isStringEmpty(checkYear)) {
            retVal = false;
            eYear.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isYearValid(checkYear)) {
            retVal = false;
            eYear.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidYear));
        } else {
            eYear.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setYear(checkYear);
        }

        if (isStringEmpty(checkPlate)) {
            retVal = false;
            ePlate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringSafe(checkPlate)) {
            retVal = false;
            ePlate.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            ePlate.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setLicensePlate(checkPlate);
        }

        if (isStringEmpty(checkColor)) {
            retVal = false;
            eColor.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isStringLettersOnly(checkColor)) {
            retVal = false;
            eColor.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidCharacters));
        } else {
            eColor.setError(null);
            // Set this attribute to the vehicle object
            vehicle.setColor(checkColor);
        }

        //TODO: Add warnings if these fail, such as VIN being "14" or DatePurchased being "/////"

        if (!mMileage.getText().toString().equals("")) {
            vehicle.setMileage(Integer.parseInt(mMileage.getText().toString()));
        }

        if (!mValue.getText().toString().equals("")) {
            vehicle.setValue(Double.parseDouble(mValue.getText().toString()));
        }

        if (isStringEmpty(checkVIN)) {
            retVal = false;
            eVIN.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessage));
        } else if (!VerifyUtil.isVINValid(checkVIN, checkYear)) {
            retVal = false;
            eVIN.setError(getResources().getString(R.string.vehicleSettingsActivity_errorValidationEditTextMessageInvalidVIN));
        } else {
            eVIN.setError(null);
            vehicle.setVIN(checkVIN);
        }

        if (!mDatePurchased.getText().toString().equals("")) {
            vehicle.setPurchaseDate(VerifyUtil.parseStringToDate(mDatePurchased.getText().toString()));
        }

        return retVal;
    }

    // Simplified function to check if a string is empty
    private boolean isStringEmpty(String string) {
        return (string.isEmpty() || string.equals("") || string.equals(" "));
    }


    // Listener for view events.
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vehicleSettings_buttonSave){
            // Check if all fields are valid, no need for an ELSE block as
            // the function hasMinimumsDEP() will alert the user on its own.
            if(hasMinimumRequirements()) {

                //If we found a vehicle from the id passed, update it. If not, make one
                if (vehicle == null) { //Should never be called, as hasMinimumsDEP() should catch this
                    //It couldn't be made as it didn't have the minimum properties needed to be functional
                    Snackbar.make(v, "Vehicle must have a nickname", Snackbar.LENGTH_SHORT).show();
                } else if(vehicle.getId() == -1) {
                    dbHelper.insertVehicle(vehicle);
                    // Close this activity and return to the main activity
                    VehicleSettingsActivity.super.finish();
                } else {
                    dbHelper.updateVehicle(vehicle);
                    VehicleSettingsActivity.super.finish();
                }
            } else {
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(this);
                alert.setTitle(getResources().getString(R.string.vehicleSettingsActivity_errorValidationTitle));
                alert.setMessage(getResources().getString(R.string.vehicleSettingsActivity_errorValidationMessage));
                alert.setPositiveButton(getResources().getString(R.string.vehicleSettingsActivity_errorValidationButtonNeutral), null);
                alert.show();
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

                    // Delete the issueCounter key-value pair
                    // associated with this vehicle
                    preferenceUtil.deleteIssueCountByVehicleId(vehicleId);

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